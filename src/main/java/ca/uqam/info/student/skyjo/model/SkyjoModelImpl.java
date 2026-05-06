package ca.uqam.info.student.skyjo.model;

import ca.uqam.info.max.skyjo.controller.ModelPreset;
import ca.uqam.info.max.skyjo.model.Card;
import ca.uqam.info.max.skyjo.model.ModelAccessException;
import ca.uqam.info.max.skyjo.model.SkyjoModel;
import ca.uqam.info.max.skyjo.model.Stack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the SkyjoModel interface.
 */
public class SkyjoModelImpl implements SkyjoModel {
  private final int amountPlayers;
  private final int initialDimensionX;
  private final int initialDimensionY;
  private int[] score;
  private int currentPlayerIndex;
  // Fixed list of player names used in the deterministic model.
  private String[] names;
  private int round;
  private Stack<Card> pileDeck;
  private Stack<Card> pileDiscard;
  private Card buffer;
  // Three-dimensional array storing each player's grid of cards.
  // format: [player][column][row].
  private Card[][][] cardsPlayer;
  private boolean[][][] revealed;
  private int gameEnder = -1;

  /**
   * Constructs a dynamic Skyjo model.
   *
   * @param players player's names.
   * @param random  Number used to shuffle and choose the order of cards.
   * @param preset  matrix model.
   */
  public SkyjoModelImpl(String[] players, Random random, ModelPreset preset) {
    this.names = players;
    this.amountPlayers = players.length;
    this.score = new int[amountPlayers];
    // Dimensions dynamiques
    this.initialDimensionX = preset.getSizeX();
    this.initialDimensionY = preset.getSizeY();
    cardsPlayer = new Card[amountPlayers][initialDimensionX][initialDimensionY];
    revealed = new boolean[amountPlayers][initialDimensionX][initialDimensionY];
    pileDeck = createDeck(random);
    pileDiscard = new Stack<>();
    buffer = null;
    round = 1;
    shuffleCards(random);
    distributionCartes(); // Distribution
    revealTwoCards(random); // Reveler les cartes en fonction du random
    recalculateScores();
    findMaxScore();
    pileDiscard.push(pileDeck.pop());
  }

  @Override
  public int getAmountDeckCards() {
    return pileDeck.getSize();
  }

  @Override
  public boolean isBufferCardPresent() {
    return buffer != null;
  }

  @Override
  public Card getBufferCard() {
    if (buffer == null) {
      throw new ModelAccessException("Buffer is empty!");
    }
    return buffer;
  }

  @Override
  public void setBufferCard(Card card) {
    // If buffer contains a card.
    if (buffer != null) {
      throw new ModelAccessException("The buffer space is already taken");
    }
    buffer = card;
  }

  @Override
  public int getAmountPlayers() {
    return amountPlayers;
  }

  @Override
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  @Override
  public int getInitialDimensionsX() {
    return initialDimensionX;
  }

  @Override
  public int getInitialDimensionsY() {
    return initialDimensionY;
  }

  @Override
  public int getCurrentDimensionsX(int playerIndex) {
    return cardsPlayer[playerIndex].length;
  }

  @Override
  public int getCurrentDimensionsY(int currentPlayerIndex) {
    return cardsPlayer[currentPlayerIndex][0].length;
  }

  @Override
  public int getRound() {
    return round;
  }

  /**
   * This methode iterates through all players and verifies
   * if at least one player has revealed all of their cards.
   *
   * @return true if such a player exists, false otherwise.
   */
  public boolean isGameOverInitialized() {
    // Iterate through each player
    for (int player = 0; player < amountPlayers; player++) {
      // If a player has all their cards revealed, returns true.
      if (isAllPlayerCardsRevealed(player)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Card peekDiscardPile() {
    return pileDiscard.peek();
  }

  @Override
  public int getAmountDiscardPileCards() {
    return pileDiscard.getSize();
  }

  @Override
  public boolean isGameOver() {
    return getGameEnder() != -1;
  }

  @Override
  public int getGameEnder() {
    return gameEnder;
  }

  @Override
  public String getPlayerName(int playerIndex) {
    return names[playerIndex];
  }

  @Override
  public boolean isPlayerCardAtPositionRevealed(int playerIndex, int cardPositionX,
                                                int cardPositionY) {
    return revealed[playerIndex][cardPositionX][cardPositionY];
  }

  @Override
  public boolean isAllPlayerCardsRevealed(int playerIndex) {
    for (int y = 0; y < getCurrentDimensionsY(playerIndex); y++) {
      for (int x = 0; x < getCurrentDimensionsX(playerIndex); x++) {
        if (!revealed[playerIndex][x][y]) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public Card getCardForPlayerByPosition(int playerIndex, int cardPositionX, int cardPositionY) {
    if (!revealed[playerIndex][cardPositionX][cardPositionY]) {
      throw new ModelAccessException("This card isn't revealed");
    }
    return cardsPlayer[playerIndex][cardPositionX][cardPositionY];
  }
  // ====================== METHODES ECRITURE ===============================

  @Override
  public int[] getPlayerScores() {
    return score;
  }

  @Override
  public void revealPlayerCard(int playerIndex, int x, int y) {
    // Reveal card at position (x y)
    if (isPlayerCardAtPositionRevealed(playerIndex, x, y)) {
      throw new RuntimeException("Error! This card is already revealed!");
    }
    revealed[playerIndex][x][y] = true;
    score[playerIndex] += cardsPlayer[playerIndex][x][y].getValue();
  }

  @Override
  public Card[] eliminateRow(int playerIndex, int rowIndex) {
    int rowsPlayer = cardsPlayer[playerIndex][0].length;
    int columnsPlayer = cardsPlayer[playerIndex].length;
    Card[] cardsRow = new Card[columnsPlayer];
    if (rowIndex < 0 || rowIndex >= rowsPlayer) {
      throw new RuntimeException("Wrong row!");
    }
    // Value of the first card in the row
    int cardRef = cardsPlayer[playerIndex][0][rowIndex].getValue();
    for (int x = 0; x < columnsPlayer; x++) {
      if (cardsPlayer[playerIndex][x][rowIndex].getValue() != cardRef
          || !revealed[playerIndex][x][rowIndex]) {
        throw new RuntimeException("Error! This row cannot be eliminated.");
      }
      cardsRow[x] = cardsPlayer[playerIndex][x][rowIndex];
    }
    cardsPlayer[playerIndex] = reduceRows(cardsPlayer[playerIndex], rowIndex);
    revealed[playerIndex] = reduceRowsRevealed(revealed[playerIndex], rowIndex);
    recalculateScores();
    return cardsRow;
  }

  @Override
  public Card[] eliminateColumn(int playerIndex, int colIndex) {
    int columnsPlayer = cardsPlayer[playerIndex].length;
    int rowsPlayer = cardsPlayer[playerIndex][0].length;
    if (colIndex < 0 || colIndex >= columnsPlayer) {
      throw new RuntimeException("Wrong column!");
    }
    // Value of the first card in the column.
    Card[] cardsColumn = new Card[rowsPlayer];
    int cardRef = cardsPlayer[playerIndex][colIndex][0].getValue();
    for (int y = 0; y < rowsPlayer; y++) {
      if (cardsPlayer[playerIndex][colIndex][y].getValue() != cardRef
          || !revealed[playerIndex][colIndex][y]) {
        throw new RuntimeException("Error! This column cannot be eliminated");
      }
      cardsColumn[y] = cardsPlayer[playerIndex][colIndex][y];
    }
    // Copy of the new Matrix
    cardsPlayer[playerIndex] = reduceColumns(cardsPlayer[playerIndex], colIndex);
    revealed[playerIndex] = reduceColumnsRevealed(revealed[playerIndex], colIndex);
    recalculateScores();
    return cardsColumn;
  }

  @Override
  public void restoreColumn(int playerIndex, int colIndex, Card[] colCards) {
    int newCols = cardsPlayer[playerIndex].length + 1;
    int rows = cardsPlayer[playerIndex][0].length;
    Card[][] newCards = new Card[newCols][rows];
    boolean[][] newRevealed = new boolean[newCols][rows];
    int shift = 0;
    for (int col = 0; col < newCols; col++) {
      if (col == colIndex) {
        for (int row = 0; row < rows; row++) {
          newCards[col][row] = colCards[row];
          newRevealed[col][row] = true;
        }
        shift = 1;
      } else {
        newCards[col] = cardsPlayer[playerIndex][col - shift];
        newRevealed[col] = revealed[playerIndex][col - shift];
      }
    }
    cardsPlayer[playerIndex] = newCards;
    revealed[playerIndex] = newRevealed;
  }

  @Override
  public void restoreRow(int playerIndex, int rowIndex, Card[] rowCards) {
    int cols = cardsPlayer[playerIndex].length;
    int newRows = cardsPlayer[playerIndex][0].length + 1;
    Card[][] newCards = new Card[cols][newRows];
    boolean[][] newRevealed = new boolean[cols][newRows];
    for (int x = 0; x < cols; x++) {
      int shift = 0;
      for (int y = 0; y < newRows; y++) {
        if (y == rowIndex) {
          newCards[x][y] = rowCards[x];
          newRevealed[x][y] = true;
          shift = 1;
        } else {
          newCards[x][y] = cardsPlayer[playerIndex][x][y - shift];
          newRevealed[x][y] = revealed[playerIndex][x][y - shift];
        }
      }
    }
    cardsPlayer[playerIndex] = newCards;
    revealed[playerIndex] = newRevealed;
  }

  @Override
  public Card replacePlayerCard(int playerIndex, int x, int y, Card card) {
    Card oldCard = cardsPlayer[playerIndex][x][y];
    boolean wasRevealed = revealed[playerIndex][x][y];
    // enlever ancienne valeur si révélée
    if (wasRevealed) {
      score[playerIndex] -= oldCard.getValue();
    }
    cardsPlayer[playerIndex][x][y] = card;
    revealed[playerIndex][x][y] = true;
    score[playerIndex] += card.getValue(); // score adjustment
    return oldCard;
  }

  @Override
  public Card popDeck() {
    return pileDeck.pop();
  }

  @Override
  public Card popDiscardPile() {
    return pileDiscard.pop();
  }

  @Override
  public void pushDiscardPile(Card card) {
    pileDiscard.push(card);
  }

  @Override
  public Card popBufferCard() {
    Card card;
    // If buffer space is empty
    if (buffer == null) {
      throw new ModelAccessException("Buffer is empty!");
    }
    card = buffer;
    buffer = null; // Clean the buffer card.
    return card;
  }

  @Override
  public void advancePlayer() {
    // Iteration among players
    currentPlayerIndex = (currentPlayerIndex + 1) % amountPlayers;
  }

  @Override
  public void setGameEnder() {
    if (isGameOver()) {
      throw new RuntimeException();
    }
    if (!isAllPlayerCardsRevealed(getCurrentPlayerIndex())) {
      throw new RuntimeException("Error! This player's cards are not revealed");
    }
    gameEnder = currentPlayerIndex;
  }

  @Override
  public int[] endGame() {
    if (!isGameOverInitialized()) {
      throw new RuntimeException();
    }
    return getPlayerScores();
  }
  // ==========================PRIVATE METHOD============================

  /**
   * Creates the deterministic deck used in the default model.
   * If random is null, a deterministic deck is creatend cointaining
   * 10 copies of each card value form -2 to 12
   *
   * @return a new Stack containing all cards of the generated deck.
   */
  private Stack<Card> createDeck(Random random) {
    Stack<Card> pile = new Stack<>();
    // Iterate through all possible card values (-2 to 12) if random null.
    if (random == null) {
      for (int value = -2; value <= 12; value++) {
        // Create 10 copies of each value
        for (int j = 0; j < 10; j++) {
          pile.push(new CardImpl(value));
        }
      }
    } else {
      addCards(pile, -2, 5);
      addCards(pile, -1, 10);
      addCards(pile, 0, 15);
      for (int i = 1; i <= 12; i++) {
        addCards(pile, i, 10);
      }
    }
    return pile;
  }

  /**
   * Adds a specified number of cards with the same value.
   *
   * @param pile   the stack to wich the cards will be added
   * @param val    the value of the cards to add
   * @param amount the number of cards to add.
   */
  private void addCards(Stack<Card> pile, int val, int amount) {
    for (int i = 0; i < amount; i++) {
      pile.push(new CardImpl(val));
    }
  }

  private Card[][] reduceColumns(Card[][] matrix, int columnEliminate) {
    int cols = matrix.length;
    int rows = matrix[0].length;
    Card[][] newMatrix = new Card[cols - 1][rows];
    int newCol = 0;
    for (int c = 0; c < cols; c++) {
      if (c != columnEliminate) {
        for (int r = 0; r < rows; r++) {
          newMatrix[newCol][r] = matrix[c][r];
        }
        newCol++;
      }
    }
    return newMatrix;
  }

  private boolean[][] reduceColumnsRevealed(boolean[][] matrix, int columnEliminate) {
    int cols = matrix.length;
    int rows = matrix[0].length;
    boolean[][] newMatrix = new boolean[cols - 1][rows];
    int newCol = 0;
    for (int c = 0; c < cols; c++) {
      if (c != columnEliminate) {
        for (int r = 0; r < rows; r++) {
          newMatrix[newCol][r] = matrix[c][r];
        }
        newCol++;
      }
    }
    return newMatrix;
  }

  private Card[][] reduceRows(Card[][] matrix, int rowEliminate) {
    int cols = matrix.length;
    int rows = matrix[0].length;
    Card[][] newMatrix = new Card[cols][rows - 1];
    for (int c = 0; c < cols; c++) {
      int newRow = 0;
      for (int r = 0; r < rows; r++) {
        if (r != rowEliminate) {
          newMatrix[c][newRow] = matrix[c][r];
          newRow++;
        }
      }
    }
    return newMatrix;
  }

  private boolean[][] reduceRowsRevealed(boolean[][] matrix, int rowEliminate) {
    int cols = matrix.length;
    int rows = matrix[0].length;
    boolean[][] newMatrix = new boolean[cols][rows - 1];
    for (int c = 0; c < cols; c++) {
      int newRow = 0;
      for (int r = 0; r < rows; r++) {
        if (r != rowEliminate) {
          newMatrix[c][newRow] = matrix[c][r];
          newRow++;
        }
      }
    }
    return newMatrix;
  }

  /**
   * Recalculates the scores of all players on their revealed cards.
   * This method iterates through each player's grid and sums the values of all revealed cards.
   * Only cards revealed contribute to the score.
   */
  private void recalculateScores() {
    for (int p = 0; p < amountPlayers; p++) {
      score[p] = 0;
      for (int y = 0; y < getCurrentDimensionsY(p); y++) {
        for (int x = 0; x < getCurrentDimensionsX(p); x++) {
          if (revealed[p][x][y]) {
            score[p] += cardsPlayer[p][x][y].getValue();
          }
        }
      }
    }
  }

  /**
   * Determines the player with the highest score and sets them as the current player.
   * This method iterates through all players' scores to find the maximum score.
   * The index of the player with the highest score is then assigned to
   * currentPlayerIndex.
   */
  private void findMaxScore() {
    int startPlayer = 0;
    int maxScore = 0;
    for (int i = 0; i < amountPlayers; i++) {
      if (score[i] > maxScore) {
        maxScore = score[i];
        startPlayer = i;
      }
    }
    currentPlayerIndex = startPlayer;
  }

  /**
   * Reveals the initial two cards for each player.
   * If the provided random is null the model behaves deterministically:
   * for each player, the top-left and bottom-right cards of their grid are revealed.
   * If random is provided, two disctint cards are revealed at random positions for each player.
   *
   * @param random used to determinate which cards to reveal randomly.
   */
  private void revealTwoCards(Random random) {
    if (random == null) {
      for (int p = 0; p < amountPlayers; p++) {
        revealPlayerCard(p, 0, 0);
        revealPlayerCard(p, initialDimensionX - 1, initialDimensionY - 1);
      }
    } else {
      // selon random
      for (int p = 0; p < amountPlayers; p++) {
        revealRandomCard(p, random);
        revealRandomCard(p, random);
      }
    }
  }

  /**
   * Distribution de cartes, joueur par joueur.
   */
  private void distributionCartes() {
    for (int p = 0; p < amountPlayers; p++) {
      for (int y = 0; y < initialDimensionY; y++) {
        for (int x = 0; x < initialDimensionX; x++) {
          cardsPlayer[p][x][y] = pileDeck.pop();
          revealed[p][x][y] = false;
        }
      }
    }
  }

  /**
   * Reveals a random ono-revealed card for the specified player.
   *
   * @param p            the index of the player whose card will be revealed.
   * @param randomNumber the random number generator used to select positions within the grid.
   */
  private void revealRandomCard(int p, Random randomNumber) {
    while (true) {
      int x = randomNumber.nextInt(0, getInitialDimensionsX());
      int y = randomNumber.nextInt(0, getInitialDimensionsY());
      if (!revealed[p][x][y]) {
        revealPlayerCard(p, x, y);
        return;
      }
    }
  }

  /**
   * Shuffles the deck of cards using the provided random generator.
   * If random is null, the deck remains unchanged.
   *
   * @param random the random number generator used to shuffle the deck.
   */
  private void shuffleCards(Random random) {
    // Shuffle  si random n'est pas null
    if (random != null) {
      List<Card> temp = new ArrayList<>();
      while (pileDeck.getSize() > 0) {
        temp.add(0, pileDeck.pop());
      }
      Collections.shuffle(temp, random);
      for (Card c : temp) {
        pileDeck.push(c);
      }
    }
  }
}
