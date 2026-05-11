package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.Controller;
import ca.uqam.info.max.skyjo.controller.ControllerDefaultInitialCommandsTestAbstract;

/**
 * This class tests the list of initial commmands as well as the replace and reveal.
 */
public class ControllerTpTest extends ControllerDefaultInitialCommandsTestAbstract {
  @Override
  public Controller getController() {
    return new ControllerImpl();
  }
}

