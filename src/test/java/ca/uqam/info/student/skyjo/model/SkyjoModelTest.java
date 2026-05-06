package ca.uqam.info.student.skyjo.model;

import ca.uqam.info.max.skyjo.controller.ModelPreset;
import ca.uqam.info.max.skyjo.model.SkyjoModel;
import ca.uqam.info.max.skyjo.model.SkyjoModelReadOnlyTestAbstract;
import ca.uqam.info.max.skyjo.model.SkyjoModelTestAbstract;
import java.util.Random;

/**
 * This class extends and provides the default model instance used for all
 * inherited tests.
 */
public class SkyjoModelTest extends SkyjoModelTestAbstract {

  private String[] players = {"Max", "Ryan", "Maram", "Quentin"};;
  private Random random;

  /**
   * Creates and returns the default implementation of the SkyjoModel used in the
   * test suite.
   *
   * @return instance of SkyjoModelImpl's class.
   */
  @Override
  public SkyjoModel getDefaultModel() {
    ModelPreset model = ModelPreset.DEFAULT;

    return new SkyjoModelImpl(players,  this.random, model);
  }
}
