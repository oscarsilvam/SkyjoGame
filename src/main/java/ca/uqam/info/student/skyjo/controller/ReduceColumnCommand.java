package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.Command;
import ca.uqam.info.max.skyjo.model.Card;
import ca.uqam.info.max.skyjo.model.SkyjoModel;

/**
 * This class is used to create a command for eliminating a determinate column
 * to the player's grid.
 */
public class ReduceColumnCommand implements Command {
  private final SkyjoModel model;
  private final int playerIndex;
  private final int positionX;

  /**
   * Creates a command to eliminate a column.
   *
   * @param model the game model on which the command operates.
   * @param playerIndex the index of the current player executing the command
   * @param positionX the column that will be deleted.
   */
  public ReduceColumnCommand(SkyjoModel model, int playerIndex, int positionX) {
    this.model = model;
    this.playerIndex = playerIndex;
    this.positionX = positionX;
  }

  @Override
  public boolean isSpaceholder() {
    return false;
  }

  @Override
  public void execute() {
    Card [] cardsRemoved = model.eliminateColumn(playerIndex, positionX);
    for (Card c : cardsRemoved) {
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
    return String.format("Reduce (col %d)", positionX);
  }
}
