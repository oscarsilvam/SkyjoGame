package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.Controller;
import ca.uqam.info.max.skyjo.controller.ControllerDefaultInitialCommandsTestAbstract;

public class ControllerTpTest extends ControllerDefaultInitialCommandsTestAbstract {
  @Override
  public Controller getController() {
    return new ControllerImpl();
  }
}

