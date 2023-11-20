package test;

import cards.AttackCard;
import cards.BoostAttackCard;
import cards.Card;
import cards.ProtectCard;
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

public class TestCards {
    @Test
    public void testUsingAProtectCardDoesNotChangeHealth() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        List<Card> opponentHand = new ArrayList<>();
        opponentHand.add(new ProtectCard());
        opponent.testCommandSetHand(opponentHand);

        game.testCommandCanPlayATurn(opponent, player, new int[]{5}, null, "");
        game.testCommandCanPlayATurn(player, opponent, new int[]{}, null, "1");

        assertEquals(10, opponent.getHealth());
    }

    @Test
    public void testPlayingAProtectCardIncreasesProtectCounter() {
        Player player = new Player(10, Utility.generateCards());
        List<Card> newHand = new ArrayList<>();
        newHand.add(new ProtectCard());
        newHand.add(new ProtectCard());
        player.testCommandSetHand(newHand);

        player.playCard(1);
        player.playCard(1);

        assertEquals(2, player.getProtectCounter());
    }

    @Test
    public void testDefendingWithAProtectCardDoesNotRaiseProtectCounter() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        List<Card> opponentHand = new ArrayList<>();
        opponentHand.add(new ProtectCard());
        opponent.testCommandSetHand(opponentHand);

        game.testCommandCanPlayATurn(opponent, player, new int[]{5}, null, "");
        game.testCommandCanPlayATurn(player, opponent, new int[]{}, null, "1");

        assertEquals(0, player.getProtectCounter());
    }

    @Test
    public void testProtectCounterWorks() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        opponent.testCommandSetProtectCounter(5);

        game.testCommandCanPlayATurn(player, opponent, new int[]{5}, null, "");
        // we set the opponent's decision to "take", but it does not matter since the protect counter will kick in before that
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "take");

        // double assert like a boss (this should be split into two tests but i hope it's not a big deal,
        // since these two effects are closely related)
        assertEquals(10, opponent.getHealth());
        assertEquals(4, opponent.getProtectCounter());
    }

    @Test
    public void testBoostCardIncreasesAttackBy3() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(20, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        // 5 for the first attack
        newHand.add(new AttackCard(5));
        // 2 and 5 for the second attack
        newHand.add(new BoostAttackCard());
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        game.testCommandCanPlayATurn(player, opponent, new int[]{5}, null, "");
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "take");
        // 5 damage expected
        int firstDamage = 20 - opponent.getHealth();

        game.testCommandCanPlayATurn(player, opponent, new int[]{2, 5}, null, "");
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "take");
        // 8 damage expected
        int secondDamage = (20 - firstDamage) - opponent.getHealth();

        assertEquals(secondDamage, firstDamage + 3);
    }

    @Test
    public void testBoostCardsStack() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(20, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new BoostAttackCard());
        newHand.add(new BoostAttackCard());
        newHand.add(new BoostAttackCard());
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        // 3 + 3 + 3 + 5 = 14 expected damage
        game.testCommandCanPlayATurn(player, opponent, new int[]{2, 2, 2, 5}, null, "");
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "take");

        assertEquals(6, opponent.getHealth());
    }

    @Test
    public void testBoostCardsMakeNoDamage() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(20, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new BoostAttackCard());
        newHand.add(new BoostAttackCard());
        newHand.add(new BoostAttackCard());
        player.testCommandSetHand(newHand);

        // 3 + 3 + 3 + 5 = 14 expected damage
        game.testCommandCanPlayATurn(player, opponent, new int[]{2, 2, 2}, null, "");
        // the opponent won't be asked if he wants to take damage, but let's leave it like this just in case
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "take");

        assertEquals(20, opponent.getHealth());
    }

    @Test
    public void testBoostCardsDontSetAttackingStatus() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(20, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new BoostAttackCard());
        player.testCommandSetHand(newHand);

        player.playCard(2);
        assertFalse(player.getAttackingStatus());
    }

    @Test
    public void testBoostCardsOnlyLastsOneTurn() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(20, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new BoostAttackCard());
        newHand.add(new BoostAttackCard());
        newHand.add(new BoostAttackCard());
        newHand.add(new AttackCard(5));
        player.testCommandSetHand(newHand);

        // 3 + 3 + 3 = 9 boost, no attack
        game.testCommandCanPlayATurn(player, opponent, new int[]{2, 2, 2}, null, "");
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "take");
        // 5 attack, expecting 5 damaga total
        game.testCommandCanPlayATurn(player, opponent, new int[]{5}, null, "");
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "take");

        assertEquals(15, opponent.getHealth());
    }

    @Test
    public void testAttackCardsSetDamageProperly() {
        Player player = new Player(10, Utility.generateCards());

        List<Card> newHand = new ArrayList<>();
        newHand.add(new AttackCard(3));
        newHand.add(new AttackCard(4));
        newHand.add(new AttackCard(5));
        newHand.add(new AttackCard(6));
        newHand.add(new AttackCard(7));
        player.testCommandSetHand(newHand);

        // we will be doing multiple asserts to check each of the five options
        // this may not be a good practice and may be a bit overkill but
        // i think it's good to thoroughly check this
        player.playCard(3);
        assertEquals(3, player.getDamage());
        player.resetDamage();

        player.playCard(4);
        assertEquals(4, player.getDamage());
        player.resetDamage();

        player.playCard(5);
        assertEquals(5, player.getDamage());
        player.resetDamage();

        player.playCard(6);
        assertEquals(6, player.getDamage());
        player.resetDamage();

        player.playCard(7);
        assertEquals(7, player.getDamage());

    }

    @Test
    public void testDefendingWithAttackCards() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new BoostAttackCard());
        newHand.add(new AttackCard(4));
        player.testCommandSetHand(newHand);

        List<Card> opponentHand = new ArrayList<>();
        opponentHand.add(new AttackCard(7));
        player.testCommandSetHand(opponentHand);

        // player1 = boost + 4 = 7 dmg; so player2 should be able to defend with the attack card 7
        game.testCommandCanPlayATurn(player, opponent, new int[]{2, 4}, null, "");
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, null, "7");

        assertEquals(10, opponent.getHealth());
    }

    @Test
    public void testDefendingWithAttackCards2() {
        Player player = new Player(10, Utility.generateCards());
        Player opponent = new Player(10, Utility.generateCards());
        Game game = new Game(player, opponent);

        List<Card> newHand = new ArrayList<>();
        newHand.add(new BoostAttackCard());
        newHand.add(new AttackCard(6));
        player.testCommandSetHand(newHand);

        List<Card> opponentHand = new ArrayList<>();
        opponentHand.add(new AttackCard(6));
        opponent.testCommandSetHand(opponentHand);

        // player1 = boost + 6 = 9 dmg; so player2 should not be able to defend with the attack card 6
        game.testCommandCanPlayATurn(player, opponent, new int[]{2, 6}, null, "");
        game.testCommandCanPlayATurn(opponent, player, new int[]{}, new BoostAttackCard(), "6");

        assertEquals(1, opponent.getHealth());
    }

}
