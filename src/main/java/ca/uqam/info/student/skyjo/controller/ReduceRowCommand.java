package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.Command;
import ca.uqam.info.max.skyjo.model.Card;
import ca.uqam.info.max.skyjo.model.SkyjoModel;

/**
 * This class is used to create a command for eliminating a determinate row
 * to the player's grid.
 */
public class ReduceRowCommand implements Command {
  private final SkyjoModel model;
  private final int playerIndex;
  private final int positionY;

  /**
   * Creates a command to eliminate a row.
   *
   * @param model       the game model on which the command operates.
   * @param playerIndex the index of the current player executing the command
   * @param positionY   the row that will be deleted.
   */
  public ReduceRowCommand(SkyjoModel model, int playerIndex, int positionY) {
    this.model = model;
    this.playerIndex = playerIndex;
    this.positionY = positionY;
  }

  @Override
  public boolean isSpaceholder() {
    return false;
  }

  @Override
  public void execute() {
    Card[] cardsRemove = model.eliminateRow(playerIndex, positionY);
    for (Card c : cardsRemove) {
      model.pushDiscardPile(c);
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
    return new Command[0];
  }

  @Override
  public String toString() {
    return String.format("Reduce (row %d)", positionY);
  }
}
