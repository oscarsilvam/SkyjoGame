package ca.uqam.info.student.skyjo.model;

import ca.uqam.info.max.skyjo.controller.ModelFactory;
import ca.uqam.info.max.skyjo.controller.ModelFactoryTestAbstract;
import ca.uqam.info.student.skyjo.controller.ModelFactoryImpl;

public class RandomTest extends ModelFactoryTestAbstract {
  @Override
  public ModelFactory getModelFactory() {
    return new ModelFactoryImpl();
  }
}
