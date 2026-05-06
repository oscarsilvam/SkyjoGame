package ca.uqam.info.student.skyjo.model;

import ca.uqam.info.max.skyjo.model.Card;
import ca.uqam.info.max.skyjo.model.CardTestAbstract;

/**
 * This class extends CardTestAbstract and provides the default model instance used
 * for all inherited tests.
 */
public class CardTest extends CardTestAbstract {
  /**
   * Creates and returns the default implementation of the Card class
   * used in the test suite.
   *
   * @param value the card's number
   * @return a new instance of CardImpl
   */
  @Override
  public Card getCard(int value) {
    return new CardImpl(value);
  }
}
