package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.Command;
import ca.uqam.info.max.skyjo.controller.Controller;
import ca.uqam.info.max.skyjo.controller.ModelFactory;
import ca.uqam.info.max.skyjo.controller.ModelObserver;
import ca.uqam.info.max.skyjo.controller.ModelPreset;
import ca.uqam.info.max.skyjo.model.SkyjoModel;
import ca.uqam.info.max.skyjo.model.SkyjoModelReadOnly;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Interface for controller. Use this interface to implement your MVC controller.
 *
 * @author Maximilian Schiedermeier
 */
public class ControllerImpl implements Controller {
  private final java.util.List<ModelObserver> observers = new java.util.ArrayList<>();
  private final ModelFactory factory = new ModelFactoryImpl();
  private SkyjoModel model;
  private Command[] currentCommands;
  // CONSTRUCTOR

  /**
   * Default constructor.
   */
  public ControllerImpl() {
  }

  /**
   * Created a new model. Must internally use the model factory to encapsulate complexity.
   *
   * @param preset  as the layout configuration for the Skyjo session.
   * @param players as the player names in String array format.
   * @param seed    as object used for pseudo-random decision-making. Shuffling etc is
   *                skipped if null.
   */
  @Override
  public void initializeModel(ModelPreset preset, String[] players, Random seed) {
    this.model = factory.createModel(preset, players, seed);
    this.currentCommands = initialCommands();
    notifyObservers();
  }

  @Override
  public SkyjoModelReadOnly getModel() {
    return model;
  }

  @Override
  public void addModelObserver(ModelObserver observer) {
    if (observer != null) {
      this.observers.add(observer);
      observer.refresh();
    }
  }

  /**
   * Notifies all registered observers that the model state has changed.
   */
  private void notifyObservers() {
    for (ModelObserver observer : observers) {
      observer.refresh();
    }
  }

  @Override
  public Command[] getCurrentPlayerCommands() {
    return currentCommands;
  }

  @Override
  public void doCommand(int i) {
    if (i >= 0 && i < currentCommands.length) {
      Command cmd = currentCommands[i];
      cmd.execute();
      // Check end game
      if (nextCommand(cmd)) {
        return; // END GAME
      }
      notifyObservers();
    }
  }

  /**
   * Determinates and updates the next set of available commands after executing a command.
   *
   * @param cmd the command that has just been executed
   * @return True if the game is over and no further commands are availabe, false otherwise.
   */
  private boolean nextCommand(Command cmd) {
    int nextPlayer = model.getCurrentPlayerIndex();
    if (model.getGameEnder() != -1 && nextPlayer == model.getGameEnder()) {
      currentCommands = new Command[0];
      notifyObservers();
      return true;
    }
    if (cmd instanceof EndTurnCommand) {
      currentCommands = initialCommands();
    } else {
      Command[] next = cmd.getFollowUpCommands();
      if (next.length == 0) {
        // FIN DU TOUR
        currentCommands = new Command[] {new EndTurnCommand(model)};
      } else {
        // CONTINUATION DU TOUR
        currentCommands = next;
      }
    }
    return false;
  }

  @Override
  public boolean isUndoAvailable() {
    return false;
  }

  @Override
  public void undoLastCommand() {
  }

  /**
   * Cette methode permet de creer un tableau avec les commandes
   * organisees.
   *
   * @return tableau de commandes.
   */
  private Command[] initialCommands() {
    int player = model.getCurrentPlayerIndex();
    List<Command> commands = new ArrayList<>();
    // buffer empty -> juste piocher
    isBufferPresent(commands, player);
    // Reduce COLUMN
    for (int col = 0; col < model.getCurrentDimensionsX(player); col++) {
      if (isColumnEquals(player, col)) {
        commands.add(new ReduceColumnCommand(model, player, col));
      }
    }
    // Reduce ROW
    for (int row = 0; row < model.getCurrentDimensionsY(player); row++) {
      if (isRowEquals(player, row)) {
        commands.add(new ReduceRowCommand(model, player, row));
      }
    }
    // Then replace
    replace(player, commands);
    return commands.toArray(new Command[0]);
  }

  /**
   * Adds new replace command to the command list.
   *
   * @param player   the index of the current player.
   * @param commands the list to which the generated ReplaceCommand instance are added.
   */
  private void replace(int player, List<Command> commands) {
    for (int y = 0; y < model.getCurrentDimensionsY(player); y++) {
      for (int x = 0; x < model.getCurrentDimensionsX(player); x++) {
        commands.add(new ReplaceCommand(model, player, x, y));
      }
    }
  }

  /**
   * Adds new Reveal Commands to the command list only if the buffer card is not null.
   *
   * @param commands the list to which the generated RevealCommand instance are added
   * @param player   the index of the current player.
   */
  private void isBufferPresent(List<Command> commands, int player) {
    if (!model.isBufferCardPresent()) {
      commands.add(new RevealCommand(model, player, -1, -1));
    } else {
      // Rej&Rev d'abord
      for (int y = 0; y < model.getCurrentDimensionsY(player); y++) {
        for (int x = 0; x < model.getCurrentDimensionsX(player); x++) {
          if (!model.isPlayerCardAtPositionRevealed(player, x, y)) {
            commands.add(new RevealCommand(model, player, x, y));
          }
        }
      }
    }
  }

  /**
   * Checks wheter all cards in a given column for a specific plater are revealed
   * and have the same value.
   *
   * @param player the index of the player.
   * @param x      the column index to verify
   * @return true if the column is fully and revealed and all cards have the same value.
   */
  private boolean isColumnEquals(int player, int x) {
    int rows = model.getCurrentDimensionsY(player);
    // Verification of the first card
    if (!model.isPlayerCardAtPositionRevealed(player, x, 0)) {
      return false;
    }
    int rowRef = model.getCardForPlayerByPosition(player, x, 0).getValue();
    // Retrieve the value from the column
    for (int y = 0; y < rows; y++) {
      if (!model.isPlayerCardAtPositionRevealed(player, x, y)) {
        return false;
      }
      int value = model.getCardForPlayerByPosition(player, x, y).getValue();
      if (rowRef != value) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks wheter all cards in a given crow for a specific plater are revealed
   * and have the same value.
   *
   * @param player the index of the player.
   * @param y      the row index to verify.
   * @return true if the row is fully and revealed and all cards have the same value.
   */
  private boolean isRowEquals(int player, int y) {
    int columns = model.getCurrentDimensionsX(player);
    if (!model.isPlayerCardAtPositionRevealed(player, 0, y)) {
      return false;
    }
    int colRef = model.getCardForPlayerByPosition(player, 0, y).getValue();
    for (int x = 0; x < columns; x++) {
      if (!model.isPlayerCardAtPositionRevealed(player, x, y)) {
        return false;
      }
      int value = model.getCardForPlayerByPosition(player, x, y).getValue();
      if (colRef != value) {
        return false;
      }
    }
    return true;
  }
}