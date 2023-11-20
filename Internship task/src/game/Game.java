package game;

import java.util.List;
import java.util.Scanner;

import cards.AttackCard;
import cards.Card;
import player.*;


public class Game {
    private Player player1;
    private Player player2;
    private Scanner scanner;
    private boolean gameEnded;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.scanner = new Scanner(System.in);
        gameEnded = false;
    }

    public boolean getGameEnded() {
        return gameEnded;
    }

    private boolean isPlayerWithoutOptionsToPlay(Player player) {
        return isHandEmpty(player) && player.getDeck().isEmpty();
    }

    private boolean hasHealth(Player player) {
        return player.getHealth() != 0;
    }

    private boolean isHandEmpty(Player player) {
        return player.getHand().isEmpty();
    }

    // A dummy function that calls a few private functions to ensure that a player in a given state is able to play a turn
    // Essentially a stripped-down version of startGame()
    // cardToDraw = null just draws the next card from the deck, decision is for potentional defending at the start of the turn
    public boolean testCommandCanPlayATurn(Player player, Player opponent, int[] plannedCards, Card cardToDraw, String decision) {
        if (!getGameEnded()) {
            if (isPlayerWithoutOptionsToPlay(player)) {
                gameEnded = true;
                return false;
            }

            // player's turn
            if (cardToDraw == null) {
                player.drawCard();
            } else {
                List<Card> newHand = player.getHand();
                newHand.add(cardToDraw);
                player.testCommandSetHand(newHand);
            }

            player.resetAttackingStatus();
            player.resetDamage();


            if (opponent.getAttackingStatus()) {
                testCommandCurrentPlayerUnderAttack(player, opponent, decision);
                if (isHandEmpty(player) || !hasHealth(player)) {
                    gameEnded = true;
                    return false;
                }
            }


            for (Integer cardNumber : plannedCards) {
                player.playCard(cardNumber);
                if (isHandEmpty(player) || player.getLastPlayedCard() instanceof AttackCard) {
                    break;
                }
            }

            if (!hasHealth(player)) {
                gameEnded = true;
                return false;
            }

            return true;
        }
        return false;
    }

    public void startGame() {
        // Draw initial cards for both players
        player1.drawInitialCards();
        player2.drawInitialCards();

        // Game loop
        while (!getGameEnded()) {
            // Player 1's turn
            System.out.println("Player 1's Turn");
            if (isPlayerWithoutOptionsToPlay(player1)) {
                System.out.println("You lost all your cards... \r\n Player 2 wins!");
                gameEnded = true;
                break;
            }

            playTurn(player1, player2);

            if (!hasHealth(player1)) {
                System.out.println("Player 2 wins!");
                gameEnded = true;
                break;
            }

            // Player 2's turn
            System.out.println("Player 2's Turn");
            if (isPlayerWithoutOptionsToPlay(player2)) {
                System.out.println("You lost all your cards... \r\n Player 1 wins!");
                gameEnded = true;
                break;
            }

            playTurn(player2, player1);

            if (!hasHealth(player2)) {
                System.out.println("Player 1 wins!");
                gameEnded = true;
                break;
            }
        }
    }

    private void playTurn(Player currentPlayer, Player opponentPlayer) {
        currentPlayer.drawCard();
        System.out.println("Health: " + currentPlayer.getHealth() + "\r\n");
        currentPlayer.resetAttackingStatus();
        currentPlayer.resetDamage();

        if (opponentPlayer.getAttackingStatus()) {
            currentPlayer.printHand();
            currentPlayerUnderAttack(currentPlayer, opponentPlayer);
            if (isHandEmpty(currentPlayer) || !hasHealth(currentPlayer)) {
                return;
            }
        }

        while (true) {
            currentPlayer.printHand();
            System.out.println("Enter the number of the card you want to play (or enter 'end' to end your turn):");

            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("end")) {
                break;
            }
            try {
                int cardNumber = Integer.parseInt(input);
                currentPlayer.playCard(cardNumber);
                //check if player can play anything else
                if (isHandEmpty(currentPlayer) || currentPlayer.getLastPlayedCard() instanceof AttackCard) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid card number or 'end'.");
            }
        }
    }

    // combines playerUnderAttack and tryToDefend, making them automatizeable
    public void testCommandCurrentPlayerUnderAttack(Player player, Player opponent, String decision) {
        if (player.getProtectCounter() > 0) {
            player.decreaseProtect();
            return;
        }
        if (player.checkForProtectionPossibilitiesInHand(opponent.getDamage())) {
            if (decision.equalsIgnoreCase("take")) {
                player.takeDamage(opponent.getDamage());
            }
            if (decision.equalsIgnoreCase("1")) {
                player.playCard(Integer.parseInt(decision));
            }
            if (decision.equalsIgnoreCase(Integer.toString(opponent.getDamage()))) {
                player.playCardInDefense(Integer.parseInt(decision));
            }
        } else {
            player.takeDamage(opponent.getDamage());
        }
    }

    public void currentPlayerUnderAttack(Player currentPlayer, Player opponentPlayer) {

        System.out.println("WOW your opponent's attacking your health points with " + opponentPlayer.getDamage() + " damage!!! \r\n");
        if (currentPlayer.getProtectCounter() > 0) {
            currentPlayer.decreaseProtect();
            System.out.println("You had a Protect Card ready! You did not take any damage.");
            return;
        }
        if (currentPlayer.checkForProtectionPossibilitiesInHand(opponentPlayer.getDamage())) {
            tryToDefend(currentPlayer, opponentPlayer);
        } else {
            //player must take damage from attack
            currentPlayer.takeDamage(opponentPlayer.getDamage());
            System.out.println("Ohhh you've taken damage... Health: " + currentPlayer.getHealth() + "\r\n");
        }
    }

    public void tryToDefend(Player currentPlayer, Player opponentPlayer) {

        //player has a way to deflect the attack
        System.out.println(String.format("Avoid the attack or take the damage... ('take'/1/%d)\r\n", opponentPlayer.getDamage()));
        String decision = getPlayersDistinctInput(opponentPlayer.getDamage(), currentPlayer);

        if (decision.equalsIgnoreCase("take")) {
            //player wants to take damage
            currentPlayer.takeDamage(opponentPlayer.getDamage());
            System.out.println("Ohhh you've taken damage... Health: " + currentPlayer.getHealth() + "\r\n");
        }
        if (decision.equalsIgnoreCase("1")) {
            //player doesn't take damage this turn - uses protect card
            currentPlayer.playCard(Integer.parseInt(decision));
        }
        if (decision.equalsIgnoreCase(Integer.toString(opponentPlayer.getDamage()))) {
            //player doesn't take damage this turn - uses special ability of attacking card
            currentPlayer.playCardInDefense(Integer.parseInt(decision));
        }
    }

    public String getPlayersDistinctInput(int opponentDamage, Player currentPlayer) {

        String input;

        do {
            input = scanner.nextLine();
        } while (!input.equalsIgnoreCase("take") && !input.equals("1") && !(input.equals(Integer.toString(opponentDamage)) && currentPlayer.findNumberInHand(Integer.parseInt(input))));

        return input;
    }

}
