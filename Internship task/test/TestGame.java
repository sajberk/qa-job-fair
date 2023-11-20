package test;

import cards.AttackCard;
import cards.BoostAttackCard;
import cards.Card;
import game.Game;
import jdk.jshell.execution.Util;
import org.junit.Test;
import player.Player;
import utility.Utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestGame {
    @Test
    public void testCanPlayWithNoCardsInDeck() {
        Player player = new Player(10, Utility.generateCards());
        while (!player.getDeck().isEmpty()) {
            player.drawCard();
        }
        assertEquals(0, player.getNumberOfCardsInDeck());
        assertNotEquals(0, player.getNumberOfCardsInHand());

        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);
        assertTrue(game.testCommandCanPlayATurn(player, opponent, new int[]{}, null, ""));
    }


    @Test
    public void testEmptyingHandEndsTurn() {
        // one player finishing his turn without losing or the game raising an error leads to
        // the next player's turn in the while(true) loop of game.startGame()
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        // player has an empty hand, so we make him draw and then play the card 2
        // after trying to play it multiple times, our boost counter should remain at 3, since we could only play it once before the turn ended
        assertTrue(game.testCommandCanPlayATurn(player, opponent, new int[]{2, 2, 2, 2, 2}, new BoostAttackCard(), ""));
        assertEquals(3, player.getDamage());
    }

    @Test
    public void testAttackingEndsTurn() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());

        // we want the player to have plenty of options here
        player.testCommandSetHand(player.getDeck());
        Game game = new Game(player, opponent);

        // player has many attack cards in hand, but can only use one of them before the turn forcibly ends
        // he is still able to use a boost first, resulting in expected 3+3=6 total damage
        assertTrue(game.testCommandCanPlayATurn(player, opponent, new int[]{2, 3, 4, 5, 6, 7}, null, ""));
        assertEquals(6, player.getDamage());
    }

    @Test
    public void testDefendingDoesNotDamageOpponent() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);
        opponent.testCommandSetHand(newHand);

        game.testCommandCanPlayATurn(opponent, player, new int[]{5}, null, "");
        game.testCommandCanPlayATurn(player, opponent, new int[]{}, null, "5");

        assertEquals(10, opponent.getHealth());
    }

    @Test
    public void testDefendingDoesNotSetAttackingState() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);
        opponent.testCommandSetHand(newHand);

        game.testCommandCanPlayATurn(opponent, player, new int[]{5}, null, "");
        game.testCommandCanPlayATurn(player, opponent, new int[]{}, null, "5");

        assertFalse(player.getAttackingStatus());
    }

    @Test
    public void testGameEndsWhenOnePlayerDies() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(4, new ArrayList<>());
        Game game = new Game(player, opponent);

        // a 5 attack card since the opponent has 4 health
        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        // giving the opponent a card so we can assure that
        // it's not him running out of cards that ends the game
        List<Card> opponentHand = new ArrayList<>();
        opponentHand.add(new BoostAttackCard());
        player.testCommandSetHand(opponentHand);

        game.testCommandCanPlayATurn(player, opponent, new int[]{5}, null, "");
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "");

        assertTrue(game.getGameEnded());
    }

    @Test
    public void testRunningOutOfCardsEndsGame() {
        Player player = new Player(10, new ArrayList<Card>());
        Player opponent = new Player(10, new ArrayList<Card>());
        Game game = new Game(player, opponent);

        game.testCommandCanPlayATurn(player, opponent, new int[]{}, null, "");

        assertTrue(game.getGameEnded());
    }

    @Test
    public void testStartingATurnRevertsDamageCounterToZero() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        game.testCommandCanPlayATurn(player, opponent, new int[]{5}, null, "");
        // player's damage counter should be 5 here (there is a separate test for that)
        game.testCommandCanPlayATurn(player, opponent, new int[]{}, null, "");
        // and now it should be 0 again

        assertEquals(0, player.getDamage());
    }

    @Test
    public void testStartingATurnRevertsStatusToNotAttacking() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        game.testCommandCanPlayATurn(player, opponent, new int[]{5}, null, "");
        // attackingstatus should be true here (there is a separate test for that)
        game.testCommandCanPlayATurn(player, opponent, new int[]{}, null, "");
        // and now it should be false again

        assertFalse(player.getAttackingStatus());
    }

    @Test
    public void testPlayerTakesDamageWhenCantProtect() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        // giving the opponent only a card he can't use to protect
        List<Card> opponentHand = new ArrayList<>();
        opponentHand.add(new BoostAttackCard());
        player.testCommandSetHand(opponentHand);

        game.testCommandCanPlayATurn(player, opponent, new int[]{5}, null, "");

        assertFalse(opponent.checkForProtectionPossibilitiesInHand(player.getDamage()));
    }
}
