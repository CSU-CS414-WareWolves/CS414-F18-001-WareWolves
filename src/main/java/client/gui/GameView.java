package client.gui;

import client.game.GameBoard;
import java.util.ArrayList;

public interface GameView {
  // Prints a list of current games
  void showCurrentGames(String[] games);

  // Prints the board from the given state
  void showGameBoard(GameBoard gb);

  // Prints the valid moves for the current player
  void showValidMoves(ArrayList<String> list);

  // Prints the "in-game" menu
  void showInGameMenu();
}
