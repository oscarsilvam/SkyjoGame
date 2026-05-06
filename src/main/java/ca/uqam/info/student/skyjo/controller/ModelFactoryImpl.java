package ca.uqam.info.student.skyjo.controller;

import ca.uqam.info.max.skyjo.controller.ModelFactory;
import ca.uqam.info.max.skyjo.controller.ModelPreset;
import ca.uqam.info.max.skyjo.model.SkyjoModel;
import ca.uqam.info.student.skyjo.model.SkyjoModelImpl;
import java.util.Random;

/**
 * Implementation of the ModelFactory interface.
 * This class provides instances of the application's model.
 * It encapsulates the instantiation to ensure a cnetralized way
 * of creating model objects.
 */
public class ModelFactoryImpl implements ModelFactory {
  /**
   * Default constructor.
   */
  public ModelFactoryImpl() {
  }

  /**
   * Creates and returns a new instance of the Skyjo model.
   *
   * @param modelPreset the preset configuration for the model (e.g. layout or rules)
   * @param strings     the names of the players participating in the game
   * @param random      a random generator used for shuffling or randomness
   * @return a new instance of SkyjoModelImpl.
   */
  @Override
  public SkyjoModel createModel(ModelPreset modelPreset, String[] strings, Random random) {
    return new SkyjoModelImpl(strings, random, modelPreset);
  }
}
