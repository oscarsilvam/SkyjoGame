package ca.uqam.info.student.skyjo.model;

import ca.uqam.info.max.skyjo.controller.Controller;
import ca.uqam.info.max.skyjo.controller.ModelPreset;
import ca.uqam.info.max.skyjo.integration.DefaultSizeAbstractIT;
import ca.uqam.info.max.skyjo.view.CommandSelector;
import ca.uqam.info.student.skyjo.controller.ControllerImpl;
import java.util.Random;

public class RobotTest37IT extends DefaultSizeAbstractIT {


  @Override
  public boolean isFullTraceRequested() {
    return true;
  }

  @Override
  public Controller getController(ModelPreset modelPreset, CommandSelector[] commandSelectors, Random random) {
    Controller controller = new ControllerImpl();

    int numPlayers = commandSelectors.length;
    String[] playerNames = new String[numPlayers];
    for (int i = 0; i < numPlayers; i++) {
      playerNames[i] = "Player" + i;
    }

    controller.initializeModel(modelPreset, playerNames, random);
    return controller;
  }
}
