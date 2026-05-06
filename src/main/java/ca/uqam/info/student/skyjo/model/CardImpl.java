package ca.uqam.info.student.skyjo.model;

import ca.uqam.info.max.skyjo.model.Card;
import ca.uqam.info.max.skyjo.model.CardType;

/**
 * Implementation of the Card's interface.
 * Represents a numeric Skyjo card identified by its integer value.
 */
public class CardImpl implements Card {
  private int numberCard;
  // Constructor

  /**
   * Creates a Card with the given int.
   *
   * @param numberCard number of the card.
   */
  public CardImpl(int numberCard) {
    this.numberCard = numberCard;
  }

  /**
   * Returns the type of this card.
   *
   * @return the type correspondig to this card's value.
   */
  @Override
  public CardType getType() {
    return valNumber(getValue());
  }

  /**
   * Returns the numeric value of this card.
   *
   * @return the integer value of the card.
   */
  @Override
  public int getValue() {
    return numberCard;
  }

  /**
   * Returns a formatted string representations of the card value.
   * Single-digit values are padded with a leading space to
   * ensure alignment in the game display.
   *
   * @return formatted string representation of the card value.
   */
  @Override
  public String toString() {
    String value = "" + getValue();
    if (value.length() == 1) {
      return " " + value;
    }
    return value;
  }
  // PRIVATE METHOD

  /**
   * Determines the type of this card based on the numeric value.
   *
   * @param number the card's numeric value
   * @return the corresponding CardType.
   */
  private CardType valNumber(int number) {
    if (number >= -2 && number < 0) {
      return CardType.NUMERIC_NEGATIVE;
    }
    if (number == 0) {
      return CardType.NUMERIC_NEUTRAL;
    }
    if (number <= 4) {
      return CardType.NUMERIC_POSITIVE_LIGHT;
    }
    if (number <= 8) {
      return CardType.NUMERIC_POSITIVE_MODERATE;
    }
    if (number <= 12) {
      return CardType.NUMERIC_POSITIVE_HEAVY;
    }
    return CardType.SPECIAL;
  }
}
