package test;

import cards.AttackCard;
import cards.Card;
import org.junit.Test;
import player.*;
import utility.Utility;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestPlayer {
    @Test
    public void testTakeDamageToZero() {
        Player player = new Player(10, Utility.generateCards());
        player.takeDamage(20);
        assertEquals(0, player.getHealth());
    }

    @Test
    public void testTakeDamage() {
        //new Utility();
        Player player = new Player(10, Utility.generateCards());
        player.takeDamage(2);
        assertEquals(8, player.getHealth());
    }

    @Test
    public void testDrawingDecreasesCardsInDeck() {
        Player player = new Player(10, Utility.generateCards());
        int sizeBefore = player.getNumberOfCardsInDeck();
        player.drawCard();
        int sizeAfter = player.getNumberOfCardsInDeck();
        assertEquals(sizeBefore - 1, sizeAfter);
    }

    @Test
    public void testDrawingIncreasesCardsInHand() {
        Player player = new Player(10, Utility.generateCards());
        for (int i = 0; i < 3; i++) {
            player.drawCard();
        }
        int sizeBefore = player.getNumberOfCardsInHand();
        player.drawCard();
        int sizeAfter = player.getNumberOfCardsInHand();
        assertEquals(sizeBefore + 1, sizeAfter);
    }

    @Test
    public void testInitialDraw() {
        Player player = new Player(10, Utility.generateCards());
        player.drawInitialCards();
        assertEquals(player.getInitialNumberOfCards(), player.getNumberOfCardsInHand());
    }

    @Test
    public void testPlayACardInHand() {
        Player player = new Player(10, Utility.generateCards());
        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);
        assertTrue(player.testCommandPlayCardSuccessful(5));
    }

    @Test
    public void testPlayACardNotInHand() {
        Player player = new Player(10, Utility.generateCards());
        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);
        assertFalse(player.testCommandPlayCardSuccessful(6));
    }

    @Test
    public void testPlayedCardsGetRemovedFromHand() {
        Player player = new Player(10, Utility.generateCards());

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        newHand.add(new AttackCard(6));
        player.testCommandSetHand(newHand);

        player.playCard(6);

        List<Card> expectedHand = new ArrayList<>();
        expectedHand.add(new AttackCard(5));

        // i think this is plenty:
        assertEquals(1, player.getNumberOfCardsInHand());
        assertEquals(5, player.getHand().get(0).getNumber());
    }

    @Test
    public void testDeckIsGeneratedWith25Cards() {
        Player player = new Player(10, Utility.generateCards());
        assertEquals(25, player.getNumberOfCardsInDeck());
    }


}
