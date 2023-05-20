package goboom.files;

import java.util.*;

public class CardGame {
    private List<String> deck;             // List to store the deck of cards
    private List<String> center;           // List to store the cards played in the center
    private List<List<String>> playerHands;// List of lists to store the hands of each player
    private int currentPlayerIndex;        // Index of the current player
    private int currentTrick;              // Current trick number
    private int[] scores;                  // Array to store the scores of each player
    private int countplays;                // Counter to keep track of played cards in a trick

    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "X", "J", "Q", "K", "A"};
    private static final String[] SUITS = {"c", "d", "h", "s"};

    // Constuctor
    public CardGame() {
        deck = new ArrayList<>();             // Initialize deck as an ArrayList
        center = new ArrayList<>();           // Initialize center as an ArrayList
        playerHands = new ArrayList<>();      // Initialize playerHands as an ArrayList of ArrayLists
        scores = new int[4];                   // Initialize scores as an array of size 4
        currentPlayerIndex = 0;                // Initialize currentPlayerIndex to 0
        currentTrick = 1;                      // Initialize currentTrick to 1
        countplays = 0;                        // Initialize countplays to 0
    }

    public void startNewGame() {
        createDeck();                          // Create the deck of cards
        shuffleDeck();                         // Shuffle the deck
        dealCards();                           // Deal the cards to players
        determineFirstPlayer();                // Determine the first player
        playGame();                            // Start the game
    }

    // Creates a standard deck of cards by combining ranks and suits
    private void createDeck() {
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(suit + rank);
            }
        }
    }

    // Shuffles the deck using the Collections.shuffle() method
    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    // Deals the cards to the players
    private void dealCards() {
        for (int i = 0; i < 4; i++) {
            List<String> hand = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                hand.add(deck.remove(0));
            }
            playerHands.add(hand);
        }
    }

    // Determines the first player based on the lead card
    private void determineFirstPlayer() {
        String leadCard = deck.remove(0);
        center.add(leadCard);
        char suit = leadCard.charAt(1);

        switch (suit) {
            case 'A', '5', '9', 'K' -> currentPlayerIndex = 0;
            case '2', '6', 'X' -> currentPlayerIndex = 1;
            case '3', '7', 'J' -> currentPlayerIndex = 2;
            case '4', '8', 'Q' -> currentPlayerIndex = 3;
        }

        System.out.println("Lead card " + leadCard);
        System.out.println("Player" + (currentPlayerIndex + 1) + " is the first player because of " + leadCard + ".");
        System.out.println();
    }

    // Starts the game and handles the game flow
    private void playGame() {
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;

        while (!gameOver) {
            System.out.println("Trick #" + currentTrick);
            displayPlayerHands();
            displayDeck();
            displayCenter();

            System.out.println("Score: Player1 = " + scores[0] + " | Player2 = " + scores[1] + " | Player3 = " + scores[2] + " | Player4 = " + scores[3]);
            System.out.println("Turn: Player" + (currentPlayerIndex + 1));

            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("x")) {
                System.out.println("Game ended by the player.");
                break;
            } else if (input.equalsIgnoreCase("d")) {
                System.out.println("Drawing a card...");
                drawCard();

            } else if (input.matches("[cdhs][2-9XJQKA]")) {
                playCard(input);
            } else {
                System.out.println("Invalid command. Please try again.");
            }

            for (int i = 0; i < 4; i++) {
                if (playerHands.get(i).isEmpty()) {
                    System.out.println("*** Player " + (i + 1) + " is the Winner § !!! ***");
                    gameOver = true;
                }
            }

        }
        scanner.close();
    }

    // Displays the current state of the deck
    private void displayDeck() {
        System.out.print("Deck: ");
        System.out.println(deck);
    }

    // Displays the hands of each player
    private void displayPlayerHands() {
        for (int i = 0; i < playerHands.size(); i++) {
            System.out.print("Player" + (i + 1) + ": ");
            System.out.println(playerHands.get(i));
        }
    }

    // Displays the cards played in the center
    private void displayCenter() {
        System.out.print("Center: ");
        System.out.println(center);
    }

    // Draws a card from the deck and adds it to the current player's hand
    private void drawCard() {
        if (!deck.isEmpty()) {
            String card = deck.remove(0);
            playerHands.get(currentPlayerIndex).add(card);
            System.out.println("Player" + (currentPlayerIndex + 1) + " drew a card: " + card);
        } else {
            System.out.println("The deck is empty. Skipping to the next player.");
            nextPlayerTurn();
        }
    }

    // Checks if a played card is a legal move
    private boolean isLegalMove(String playedCard) {
        if (center.isEmpty()) {
            return true; // The center is empty, any card can be played
        }
        String firstCard = center.get(0);

        // Extract the suit and rank of the first card
        String firstSuit = firstCard.substring(0, 1);
        String firstRank = firstCard.substring(1);

        // Extract the suit and rank of the played card
        String playedSuit = playedCard.substring(0, 1);
        String playedRank = playedCard.substring(1);

        // Check if either the suit or rank of the played card matches the first card
        if (playedSuit.equals(firstSuit) || playedRank.equals(firstRank)) {
            return true;  // Move is legal
        }

        return false;  // Move is illegal
    }

    // Plays a card from the current player's hand
    private void playCard(String card) {
        List<String> currentPlayerHand = playerHands.get(currentPlayerIndex);

        if (isLegalMove(card)) {
            if (currentPlayerHand.contains(card)) {
                countplays += 1;
                currentPlayerHand.remove(card);
                center.add(card);
                System.out.println("Player" + (currentPlayerIndex + 1) + " plays " + card);

                if (countplays == 4) {
                    int winningPlayerIndex = determineWinningPlayerIndex();
                    scores[winningPlayerIndex]++;
                    System.out.println("*** Player" + (winningPlayerIndex + 1) + " wins Trick #" + currentTrick + " ***");
                    System.out.println();
                    center.clear();
                    currentTrick++;
                    countplays = 0;
                }

                nextPlayerTurn();
            } else {
                System.out.println("Invalid card. Please choose a card from your hand.");
            }
        } else {
            System.out.println("Invalid card. Draw a card from the deck");
        }
    }

    // Determines the winning player in the current trick
    private int determineWinningPlayerIndex() {
        int a = 1;
        String leadSuit = center.get(0).substring(0, 1);
        int highestRankIndex = -1;
        int highestRankValue = -1;
        if (center.size() == 5) {
            a = 0;
        }

        for (int i = 0; i < center.size(); i++) {
            String card = center.get(i);
            String suit = card.substring(0, 1);
            String rank = card.substring(1);

            if (suit.equals(leadSuit)) {
                int rankValue = getRankValue(rank);
                if (rankValue > highestRankValue) {
                    highestRankIndex = i;
                    highestRankValue = rankValue;
                }
            }

        }
        int winningPlayerIndex = (currentPlayerIndex + highestRankIndex + a) % 4;
        currentPlayerIndex = winningPlayerIndex - 1;
        return winningPlayerIndex;
    }

    // Returns the numerical value of a rank
    private int getRankValue(String rank) {
        for (int i = 0; i < RANKS.length; i++) {
            if (RANKS[i].equals(rank)) {
                return i;
            }
        }
        return -1;
    }

    // Moves to the next player's turn
    private void nextPlayerTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }
}
