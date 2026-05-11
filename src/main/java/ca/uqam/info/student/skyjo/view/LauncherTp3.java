package ca.uqam.info.student.skyjo.view;

import ca.uqam.info.max.skyjo.controller.Command;
import ca.uqam.info.max.skyjo.controller.Controller;
import ca.uqam.info.max.skyjo.controller.ModelPreset;
import ca.uqam.info.max.skyjo.view.CommandSelector;
import ca.uqam.info.max.skyjo.view.TextualCommandSelector;
import ca.uqam.info.max.skyjo.view.TextualVisualizer;
import ca.uqam.info.student.skyjo.controller.ControllerImpl;
import java.util.Random;

/**
 * Launcher for a textual / TTY session with all physical players sharing one keyboard / screen.
 *
 * @author Maximilian Schiedermeier
 */
public class LauncherTp3 {
  /**
   * Replace command selector by robot players to obtain an automated game (also used for
   * integration testing).
   */
  private static CommandSelector commandSelector;

  /**
   * Default constructor, as imposed by javadoc.
   */
  public LauncherTp3() {
  }

  /**
   * Starts game by creating a new controller (which in turn creates a new model). Then keeps
   * prompting players for choices until game end is reached.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    String[] playerNames;
    // Register UI to automatically refresh on model updates
    boolean useTtyColours = true;
    if (args.length == 0) {
      playerNames = new String[] {"Max", "Ryan", "Maram", "Quentin"};
    } else if ((args.length == 1) || args.length > 4) {
      System.out.println("Error : Skyjo requires between 2 and 4 players\nExample:\n"
          + "java -jar Skyjo-jar-with-dependencies.jar Sophie Ryan Camille");
      return;
    } else {
      // Player names come directly form command line arguments
      playerNames = args;
    }
    // TODO
    Controller controller = new ControllerImpl();
    // Register UI to automatically refresh on model updates
    controller.initializeModel(ModelPreset.DEFAULT, playerNames, new Random());
    controller.addModelObserver(new TextualVisualizer(controller.getModel(), useTtyColours));
    // Register UI to automatically refresh on model updates
    controller.addModelObserver(new TextualVisualizer(controller.getModel(), useTtyColours));
    // Initialize commandSelector for interactive / TTY mode
    commandSelector = new TextualCommandSelector(useTtyColours, false);
    // Play the game :)
    playUntilGameEnd(controller);
  }

  /**
   * Note: This method is not concerned with updating model state representations, for the model
   * adheres to the observer pattern for this purpose.
   * This loop is only about retrieving user inputs until game end. The model is automatically
   * notified and re-rendered after each executed command.
   *
   * @param controller as the MVC controller allowing to progress the game command by command.
   *                   Note that the view has no direct access to the model, and can only
   *                   manipulate model state by executing commands.
   */
  private static void playUntilGameEnd(Controller controller) {
    // Initialize options for game start
    Command[] options = controller.getCurrentPlayerCommands();
    // Keep playing until controller offers no more options (game end)
    while (options.length > 0) {
      // Request a choice from human player - "undo"s have no relevance for INF2050, leave at
      // "false".
      int selectedCommand = commandSelector.selectCommand(options, false);
      // Execute choice (this implicitly re-renders the model)
      controller.doCommand(selectedCommand);
      // Update options
      options = controller.getCurrentPlayerCommands();
    }
  }
}