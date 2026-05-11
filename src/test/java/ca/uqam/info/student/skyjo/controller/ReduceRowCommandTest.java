package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.ModelPreset;
import ca.uqam.info.max.skyjo.model.SkyjoModel;
import ca.uqam.info.student.skyjo.model.SkyjoModelImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the ReduceRowCommand class.
 */
public class ReduceRowCommandTest {
  @Test
  void testReduceRow() {
    SkyjoModel model = new SkyjoModelImpl(new String[] {"player1"},
        null, ModelPreset.DEFAULT);
    int player = 0;
    int row = 0;
    //reveal all row
    for (int x = 0; x < model.getCurrentDimensionsX(player); x++) {
      if (!model.isPlayerCardAtPositionRevealed(player, x, row)) {
        model.revealPlayerCard(player, x, row);
      }
    }
    int beforeRows = model.getCurrentDimensionsY(player);
    ReduceRowCommand command = new ReduceRowCommand(model, player, row);
    command.execute();
    // The new grid
    int afterRows = model.getCurrentDimensionsY(player);
    Assertions.assertNotEquals(beforeRows, afterRows);
    Assertions.assertEquals(beforeRows - 1, afterRows);
  }
}
