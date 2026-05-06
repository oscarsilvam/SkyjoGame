package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.Command;
import ca.uqam.info.max.skyjo.model.SkyjoModel;

/**
 * Command representing the end of a player's turn.
 * When executed, this command advances the game to the next player.
 * If the current player has revealed all their cards and no game ender
 * has been set yet, this player is marked as the game ender.
 */
public class EndTurnCommand implements Command {
  private final SkyjoModel model;

  /**
   * Constructs an EndTurnCommand associated with the given model.
   *
   * @param model the game model used to update the current player and game state
   */
  public EndTurnCommand(SkyjoModel model) {
    this.model = model;

  }

  @Override
  public void execute() {
    int player = model.getCurrentPlayerIndex();
    if (model.isAllPlayerCardsRevealed(player) && model.getGameEnder() == -1) {
      model.setGameEnder();
    }
    model.advancePlayer();
  }

  @Override
  public boolean isSpaceholder() {
    return false;
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
    return new Command[0];
  }

  @Override
  public String toString() {
    return "End turn.";
  }
}
