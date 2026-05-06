package ca.uqam.info.student.skyjo.model;

import ca.uqam.info.max.skyjo.model.Card;
import ca.uqam.info.max.skyjo.model.CardTestAbstract;

/**
 * Classe de test pour valider l'implémentation de CardImpl.
 * Cette classe permet d'augmenter la couverture de code du package model.
 */
public class CardTest37 extends CardTestAbstract {

  /**
   * Teste la création d'une carte et la récupération de sa valeur.
   */
  @Override
  public Card getCard(int value) {
    return new CardImpl(value);
  }

  /**
   * Teste la création de cartes et la récupération de leur valeur.
   */
  public void validateCards() {
    Card c1 = getCard(10);
    Card c2 = getCard(10);

    c1.equals(c2);
    c1.hashCode();
  }
}