import java.util.List;

import cards.*;
import game.Game;
import player.Player;
import utility.Utility;


public class Main {


    public static void main(String[] args) {
        new Utility();
        // Create cards for player 1's deck
        List<Card> player1Deck = Utility.generateCards();

        // Create cards for player 2's deck
        List<Card> player2Deck = Utility.generateCards();

        Player player1 = new Player(20, player1Deck);
        Player player2 = new Player(20, player2Deck);
        // Create a game instance


        Game game = new Game(player1, player2);
        game.startGame();

    }
}