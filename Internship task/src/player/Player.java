package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cards.*;

public class Player {

    private int health;
    private List<Card> hand;
    private List<Card> deck;
    private Card lastPlayedCard;
    private static int initialNumberOfCards = 6;
    private boolean attackingStatus;
    private int damage;
    private int protectCounter = 0;

    public Player(int health, List<Card> deck) {
        this.health = health;
        this.deck = deck;
        this.hand = new ArrayList<>();
        lastPlayedCard = null;
        attackingStatus = false;
        damage = 0;
        protectCounter = 0;
        shuffleDeck();
    }

    public void takeDamage(int amountOfDamage) {
        health -= amountOfDamage;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean getAttackingStatus() {
        return attackingStatus;
    }

    public void resetAttackingStatus() {
        attackingStatus = false;
    }

    public int getDamage() {
        return damage;
    }

    public void resetDamage() {
        damage = 0;
    }

    public int getHealth() {
        return health;
    }

    public int getProtectCounter() {
        return protectCounter;
    }

    public void testCommandSetProtectCounter(int protectCounter) {
        this.protectCounter = protectCounter;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void testCommandSetHand(List<Card> newHand) {
        this.hand = newHand;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public Card getLastPlayedCard() {
        return lastPlayedCard;
    }

    public int getInitialNumberOfCards() {
        return initialNumberOfCards;
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public void populateDeck(List<Card> cardList) {
        deck.addAll(cardList);
    }

    public int getNumberOfCardsInDeck() {
        return deck.size();
    }

    public int getNumberOfCardsInHand() {
        return hand.size();
    }

    public void drawCard() {
        if (!deck.isEmpty()) {
            Card drawnCard = deck.remove(deck.size() - 1);
            hand.add(drawnCard);
        }
    }

    public void drawInitialCards() {
        for (int i = 0; i < initialNumberOfCards; i++) {
            drawCard();
        }
    }


    public boolean testCommandPlayCardSuccessful(int cardNumber) {
        Card cardToPlay = null;
        for (Card card : hand) {
            if (card.getNumber() == cardNumber) {
                cardToPlay = card;
                break;
            }
        }
        if (cardToPlay == null) {
            return false;
        }
        hand.remove(cardToPlay);
        return true;

    }

    public void playCard(int cardNumber) {
        Card cardToPlay = null;
        for (Card card : hand) {
            if (card.getNumber() == cardNumber) {
                cardToPlay = card;
                break;
            }
        }

        if (cardToPlay == null) {
            System.out.println("You don't have this card in your hand...\n");
            return;
        }
        hand.remove(cardToPlay);
        cardToPlay.effect();

        lastPlayedCard = cardToPlay;

        if (cardToPlay instanceof AttackCard) {
            attackingStatus = true;
            damage += cardToPlay.getNumber();
        }
        if (cardToPlay instanceof BoostAttackCard) {
            damage += ((BoostAttackCard) cardToPlay).getBoost();
        }
        if (cardToPlay instanceof ProtectCard) {
            protectCounter += 1;
        }


    }

    public void playCardInDefense(int cardNumber) {
        Card cardToPlay = null;
        for (Card card : hand) {
            if (card.getNumber() == cardNumber) {
                cardToPlay = card;
                break;
            }
        }

        if (cardToPlay != null) {
            lastPlayedCard = cardToPlay;
            hand.remove(cardToPlay);
            System.out.println(String.format("You've defended yourself! You've been attacked for %d damage and you've used special ability of Attacking card %d to deflect the attack\r\n", cardNumber, cardNumber));
        } else {
            System.out.println("You don't have this card in your hand...\r\n");
        }
    }

    public boolean checkForProtectionPossibilitiesInHand(int incomingDamage) {
        for (Card card : hand) {
            if (card instanceof ProtectCard || card.getNumber() == incomingDamage) {
                return true;
            }
        }
        return false;
    }

    public boolean findNumberInHand(int number) {
        for (Card card : hand) {
            if (card.getNumber() == number) {
                return true;
            }
        }
        System.out.println(String.format("There is no card in hand with number %d\r\n", number));
        return false;
    }

    public void printHand() {
        System.out.println("Player's Hand:");
        for (Card card : hand) {
            System.out.println(card.getNumber() + "(" + card.description() + ")\r");
            // You can add additional details about the card if needed
        }
        System.out.println();
    }

    public void decreaseProtect() {
        protectCounter -= 1;
    }
}

