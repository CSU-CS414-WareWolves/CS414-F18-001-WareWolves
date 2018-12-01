package client.gui.swing;

import client.game.Game;
import client.gui.ChadGameDriver;
import client.presenter.controller.messages.LoginMessage;
import client.presenter.controller.messages.LoginResponseMessage;
import client.presenter.controller.messages.MenuMessage;
import client.presenter.controller.messages.MovePieceMessage;
import client.presenter.controller.messages.RegisterMessage;
import client.presenter.controller.messages.RegisterResponseMessage;
import client.presenter.controller.messages.UnregisterMessage;
import client.presenter.controller.messages.ViewMessage;
import client.presenter.controller.messages.ViewValidMoves;
import client.presenter.network.NetworkManager;
import client.presenter.network.messages.ActiveGameResponse;
import client.presenter.network.messages.GameInfo;
import client.presenter.network.messages.InboxResponse;
import client.presenter.network.messages.Login;
import client.presenter.network.messages.LoginResponse;
import client.presenter.network.messages.Move;
import client.presenter.network.messages.NetworkMessage;
import client.presenter.network.messages.Players;
import client.presenter.network.messages.ProfileResponse;
import client.presenter.network.messages.Register;
import client.presenter.network.messages.RegisterResponse;
import client.presenter.controller.util.HashPasswords;
import client.presenter.network.messages.Unregister;
import client.presenter.network.messages.UnregisterResponse;
import java.security.NoSuchAlgorithmException;
import javax.swing.JFrame;

public class SwingChadDriver implements ChadGameDriver{

  /**
   * The Swing GUI / View
   */
  private GameJPanel gamePanel;
  /**
   * The view controller
   */
  private ChadGameDriver viewDriver;
  /**
   * The game / Model
   */
  private Game chadGame;

  /**
   * The currently login play
   */
  private int gameID;
  /**
   * The players name
   */
  private String playerNickname;
  /**
   * The network manager that handles messages to the server
   */
  private NetworkManager networkManager; // Initialize (Not Implemented)


  /**
   * Processes a message from the Swing GUI
   * @param message the message to process
   */
  public void handleViewMessage(ViewMessage message) throws NoSuchAlgorithmException {

    switch (message.messageType){
      case REGISTER:
        RegisterMessage registerMessage = (RegisterMessage) message;
        // Check if messages have invalid characters
        int nicknamePound = registerMessage.nickname.indexOf('#');
        int nicknameColon = registerMessage.nickname.indexOf(':');
        int emailPound = registerMessage.email.indexOf('#');
        int emailColon = registerMessage.email.indexOf(':');
        if(nicknameColon != -1 || nicknamePound != -1) {
          // Nickname contains invalid characters
          String[] messages = {"Invalid Nickname"};
          RegisterResponseMessage registerResponseMessage = new RegisterResponseMessage(false, messages);
          // Send message to gui/cli handle view message
          viewDriver.handleViewMessage(registerResponseMessage);
        }
        else if (emailColon != -1 || emailPound != -1) {
          // Email contains invalid characters
          String[] messages = {"Invalid Email"};
          RegisterResponseMessage registerResponseMessage = new RegisterResponseMessage(false, messages);
          // Send message to gui/cli handle view message
          viewDriver.handleViewMessage(registerResponseMessage);
        }
        else {
          // Email and nickname do not contain any invalid characters. Send to network manager.
          networkManager.sendMessage(new Register(registerMessage.email, registerMessage.nickname,HashPasswords.SHA1FromString(registerMessage.password)));
        }
        break;
      case LOGIN:
        LoginMessage loginMessage = (LoginMessage) message;
        networkManager.sendMessage(new Login(loginMessage.email, HashPasswords.SHA1FromString(loginMessage.password)));
        break;
      case UNREGISTER:
        UnregisterMessage unregisterMessage = (UnregisterMessage) message;
        networkManager.sendMessage(new Unregister(unregisterMessage.email, unregisterMessage.nickname, HashPasswords.SHA1FromString(unregisterMessage.password)));
        break;
      case SHOW_VALID_MOVES:
        // if the game is over no valid moves
        if(chadGame.gameover()){return;}
        // Find the valid moves
        ViewValidMoves validMovesMessage = (ViewValidMoves) message;
        String validMoves = chadGame.validMoves(validMovesMessage.location.toString());
        // Tell GUI to what moves to show
        gamePanel.setValidMoves(validMoves);
        break;
      case MENU:
        handleMenuMessage((MenuMessage) message);
        break;
      case MOVE_PIECE:
        MovePieceMessage moves = (MovePieceMessage) message;
        chadGame.move(moves.fromLocation.toString(), moves.toLocation.toString());
        setupGame(gameID, chadGame.getBoard(), chadGame.getTurn());

        // Show the winner if the game is over
        if(chadGame.gameover()){
          gamePanel.displayMessage(
              getCurrentPlayer(!chadGame.getTurn()) + " player won the game!");
        }
        // Send Move to Server
        break;
    }
  }

  /**
   * Handles all the menu messages from the gui
   * @param message the message to process
   */
  private void handleMenuMessage(MenuMessage message) {
    switch (message.menuType){

      case LOGOUT:
        // Send Logout to server
        System.exit(0);
        break;
      case PLAYER_STATS:
        break;
      case ACTIVE_GAMES:
        break;
      case INVITES:
        break;
      case SELECT_GAME:
        break;
      case SEND_INVITE:
        break;
    }
  }

  /**
   * Handles all the messages from NetManager(Not Implemented)
   * @param message the message to process
   */
  public void handleNetMessage(NetworkMessage message){
    switch (message.type){
      case LOGIN_RESPONSE:
        LoginResponse loginResponse = (LoginResponse) message;
        // If the login was successful
        if(loginResponse.success) {
          this.playerNickname = loginResponse.nickname;
          LoginResponseMessage loginResponseMessage = new LoginResponseMessage(loginResponse.success, loginResponse.nickname);
          // Send message to gui/cli handle view message
        }
        else {
          LoginResponseMessage loginResponseMessage = new LoginResponseMessage(loginResponse.success, loginResponse.nickname);
          // Send message to gui/cli handle view message
        }
        break;
      case GAME_INFO:
        GameInfo gameInfo = (GameInfo) message;
        chadGame = new Game(gameInfo.gameBoard, gameInfo.turn);
        setupGame(gameInfo.gameID, chadGame.getBoard(), chadGame.getTurn());
        break;
      case MOVE:
        Move move = (Move) message;
        // The game has ended
        if(move.ending) {
          // The game ends in a draw
          if(move.draw) {
            // Show draw (Not Implemented)
          }
          else {
            // Show game ending (Not Implemented)
          }
        }
        else {
          // Game has not ended
          // Handle showing move (Not Implemented)
        }
        break;
      case ACTIVE_GAMES_RESPONSE:
        ActiveGameResponse activeGameResponse = (ActiveGameResponse) message;
        // Display Active Games in view with ID, board, opponents, start dates, current turn, color and if it has ended
        // (Not Implemented)
        break;
      case REGISTER_RESPONSE:
        RegisterResponse registerResponse = (RegisterResponse) message;
        if(registerResponse.success) {
          // Successful Register
          // Display successful register (Not Implemented)
        }
        else {
          if(registerResponse.reason) {
            // Nickname already taken
            // Display unsuccessful register nickname taken (Not Implemented)
          }
          else {
            // Email already taken
            // Display unsuccessful register email taken (Not Implemented)
          }
        }
        break;
      case INBOX_RESPONSE:
        InboxResponse inboxResponse = (InboxResponse) message;
        // Display inbox of messages with ids, senders, recipients, and send dates (Not Implemented)
        break;
      case PROFILE_RESPONSE:
        ProfileResponse profileResponse = (ProfileResponse) message;
        // Display profile with games player played white and black, start and end dates of games, and results of games
        // (Not Implemented)
        break;
      case PLAYERS:
        Players players = (Players) message;
        // Store player array (Not Implemented)
        break;
      case UNREGISTER_RESPONSE:
        UnregisterResponse unregisterResponse = (UnregisterResponse) message;
        if(unregisterResponse.success) {
          // Successfully unregistered
          // Display unregistered success (Not Implemented)
        }
        else {
          // Not successful
          // Display unregistered unsuccessful (Not Implemented)
        }
        break;
    }
  }

  /**
   * Default constructor will need IP and Port for server
   */
  public SwingChadDriver(){
  }

  /**
   * Starts a thread for the Swing GUI
   */
  public void start(){

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
          createAndShowGUI();
      }
    });
  }

  /**
   * Sets up the Java Swing Frame for the game
   */
  public void createAndShowGUI() {
    //Create and set up the window.
    JFrame frame = new JFrame("Team Warewolves Chad");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    gamePanel = new GameJPanel(this);
    chadGame = new Game();

    setupGame(-1, chadGame.getBoard(), chadGame.getTurn()); // Setup default game for demo

    frame.setContentPane(gamePanel);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Sets up a game from the database and saves the gameId to use for  move messages to the server
   * @param gameId the ID for the game
   * @param boardSetup the current setup of the board
   * @param turn the current players turn
   */
  private void setupGame(int gameId, String boardSetup, boolean turn){
    this.gameID = gameId;
    gamePanel.setBoardPieces(boardSetup);
    String playerTurnMessage = getCurrentPlayer(turn) + " player's turn";
    gamePanel.setSetGameStatus(playerTurnMessage);
  }

  /**
   * Gets the Name of the current players turn
   * @param turn the turn
   * @return Black or White String
   */
  private String getCurrentPlayer(boolean turn) {
    return turn ? "The Black" : "The White";
  }


  public static void main(String[] args) {
    if(args[1].equals("cli")){
      // Instantiate CLI Controller
    } else if(args[1].equals("gui")){
      // Instantiate GUI Controller
    }
    SwingChadDriver app = new SwingChadDriver();
    app.start();
  }
}
