package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.Command;
import ca.uqam.info.max.skyjo.controller.Controller;
import ca.uqam.info.max.skyjo.controller.ModelObserver;
import ca.uqam.info.max.skyjo.controller.ModelPreset;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ControllerTest {

  class Observer implements ModelObserver {
    int number = 0;

    @Override
    public void refresh() {
      number ++;
    }
  }
  private Controller controller;
  private Observer observer;

  @BeforeEach
  public void initController() {
    controller = new ControllerImpl();
    observer = new Observer();
    controller.addModelObserver(observer);
    String[] players = new String[] {"player1", "player2", "player3", "player4"};
    controller.initializeModel(ModelPreset.DEFAULT, players, null);
  }

  @Test
  public void testInitialCommands() {
    Command[] listeCommands = Arrays.stream(controller.getCurrentPlayerCommands()).filter(cmd ->
        !cmd.isSpaceholder()).toArray(Command[]::new);
    Assertions.assertEquals("Reveal deck card", listeCommands[0].toString());
    Assertions.assertEquals(13, listeCommands.length);
  }

  @Test
  public void testAvancePlayer() {
    // Reveal a card
    controller.doCommand(0);
    Assertions.assertTrue(controller.getCurrentPlayerCommands()[0].toString().startsWith("Rej&Rev"));
    Assertions.assertEquals(22, controller.getCurrentPlayerCommands().length);
    // End turn
    controller.doCommand(0);
    Assertions.assertEquals("End turn.",
        controller.getCurrentPlayerCommands()[0].toString());
    Assertions.assertFalse(controller.getModel().isBufferCardPresent());
    controller.doCommand(0);
    Assertions.assertEquals(1, controller.getModel().getCurrentPlayerIndex());
    Assertions.assertFalse(controller.isUndoAvailable());
  }

  @Test
  public void testReplaceCommandFollowUp() {

    controller.doCommand(0);

    // prendre un Replace
    Command[] cmds = controller.getCurrentPlayerCommands();

    int indexReplace = -1;
    for (int i = 0; i < cmds.length; i++) {
      if (cmds[i].toString().startsWith("Replace")) {
        indexReplace = i;
        break;
      }
    }
    Assertions.assertTrue(indexReplace != -1);

    Command replace = cmds[indexReplace];
    Assertions.assertEquals(0, replace.getFollowUpCommands().length);
    Assertions.assertTrue(observer.number > 0);
    Assertions.assertNotNull(observer);
  }

  @Test
  public void testNumberCommands() {
    controller.doCommand(0);
    Command[] cmdBefore = controller.getCurrentPlayerCommands();
    // Command out of bounds
    controller.doCommand(controller.getCurrentPlayerCommands().length+1);
    Command[] cmdAfter = controller.getCurrentPlayerCommands();
    Assertions.assertArrayEquals(cmdBefore, cmdAfter);
    controller.doCommand(-1);
    Assertions.assertArrayEquals(cmdBefore, cmdAfter);

  }
}
