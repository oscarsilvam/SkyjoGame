package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.Command;
import ca.uqam.info.max.skyjo.model.Card;
import ca.uqam.info.max.skyjo.model.SkyjoModel;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to create a command for revealing card's value.
 */
public class RevealCommand implements Command {
  private final SkyjoModel model;
  private final int playerIndex;
  private final int positionX;
  private final int positionY;

  /**
   * Creates a command to either draw a card into the buffer or reject the
   * buffer card and reveal a specific card on the player's grid.
   *
   * @param model       the game model on which the command operates.
   * @param playerIndex the index of the current player executing the command
   * @param x           the column index of the card to reveal.
   * @param y           the row index of the card to reveal
   */
  public RevealCommand(SkyjoModel model, int playerIndex, int x, int y) {
    this.model = model;
    this.playerIndex = playerIndex;
    this.positionX = x;
    this.positionY = y;
  }

  @Override
  public boolean isSpaceholder() {
    return false;
  }

  @Override
  public void execute() {
    // buffer vide : piocher
    if (!model.isBufferCardPresent()) {
      Card card = model.popDeck();
      model.setBufferCard(card);
    } else {
      // rejeter + révéler
      model.pushDiscardPile(model.popBufferCard());
      model.revealPlayerCard(playerIndex, positionX, positionY);
    }
  }

  @Override
  public void undo() {
  }

  @Override
  public boolean isUndoable() {
    return false;
  }

  @Override
  public Command[] getFollowUpCommands() {
    // Si on vient de piocher alors proposer actions
    List<Command> commands = new ArrayList<>();
    int player = model.getCurrentPlayerIndex();
    if (model.isBufferCardPresent()) {
      // Rej&Rev
      rejectAndReveal(player, commands);
      // Then Replace
      replace(player, commands);
      return commands.toArray(new Command[0]);
    }
    // Sinon fin du tour
    verificationElim(player, commands);
    if (commands.isEmpty()) {
      return new Command[0];
    }
    commands.add(new EndTurnCommand(model));
    return commands.toArray(new Command[0]);
  }

  /**
   * Adds all possible replace commands for the current player to the
   * provided list.
   *
   * @param player   the index of the current player
   * @param commands the list to which the generated repalce command instances are added
   */
  private void replace(int player, List<Command> commands) {
    for (int y = 0; y < model.getCurrentDimensionsY(player); y++) {
      for (int x = 0; x < model.getCurrentDimensionsX(player); x++) {
        commands.add(new ReplaceCommand(model, player, x, y));
      }
    }
  }

  /**
   * Adds all possible reveal commands for the current player.
   *
   * @param player   the index of the current player.
   * @param commands the list to which the generated repalce command instances are added.
   */
  private void rejectAndReveal(int player, List<Command> commands) {
    // Rej&Rev d'abord
    for (int y = 0; y < model.getCurrentDimensionsY(player); y++) {
      for (int x = 0; x < model.getCurrentDimensionsX(player); x++) {
        if (!model.isPlayerCardAtPositionRevealed(player, x, y)) {
          commands.add(new RevealCommand(model, player, x, y));
        }
      }
    }
  }

  /**
   * Checks if the current player has any rows or columns eligible for eliminateion
   * and adds the reduce commands to the provided command list.
   *
   * @param player   the index of the player
   * @param commands the list of commands to which valid reduction commands will be added.
   */
  private void verificationElim(int player, List<Command> commands) {
    for (int col = 0; col < model.getCurrentDimensionsX(player); col++) {
      if (isColumnEquals(player, col)) {
        commands.add(new ReduceColumnCommand(model, player, col));
      }
    }
    for (int row = 0; row < model.getCurrentDimensionsY(player); row++) {
      if (isRowEquals(player, row)) {
        commands.add(new ReduceRowCommand(model, player, row));
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

  @Override
  public String toString() {
    if (positionX == -1 && positionY == -1) {
      return "Reveal deck card";
    }
    return String.format("Rej&Rev (%d/%d)", positionX, positionY);
  }
}



