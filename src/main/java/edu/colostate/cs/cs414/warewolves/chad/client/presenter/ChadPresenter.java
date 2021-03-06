package edu.colostate.cs.cs414.warewolves.chad.client.presenter;

import edu.colostate.cs.cs414.warewolves.chad.client.Point;
import edu.colostate.cs.cs414.warewolves.chad.client.game.Game;
import edu.colostate.cs.cs414.warewolves.chad.client.gui.ChadGameDriver;
import edu.colostate.cs.cs414.warewolves.chad.client.gui.cl.CLDriver;
import edu.colostate.cs.cs414.warewolves.chad.client.gui.swing.SwingController;
import edu.colostate.cs.cs414.warewolves.chad.client.gui.swing.info.ActiveGameInfo;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.GameRequestMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.InviteMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.InviteMessageResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.LoginMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.LoginResponseMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.MovePieceMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.MovePieceResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.ProfileMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.RegisterMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.RegisterResponseMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.ResignMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.UnregisterMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.UnregisterResponseMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.ViewMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.ViewValidMoves;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages.ViewValidMovesResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.NetworkManager;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.ActiveGameRequest;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.ActiveGameResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.InboxRequest;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.InboxResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.InviteRequest;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.InviteResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.Login;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.LoginResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.Logout;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.Move;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.NetworkMessage;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.Players;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.ProfileRequest;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.ProfileResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.Register;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.RegisterResponse;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.Resign;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.SeeResults;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.Unregister;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.UnregisterResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChadPresenter implements ChadGameDriver{

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

  private ActiveGameInfo currentGame;



  /**
   * Processes a message from the Swing GUI
   * @param message the message to process
   */
  public void handleViewMessage(ViewMessage message) {

    //System.out.println(message.messageType);
    switch (message.messageType){
      case REGISTER:
        handleViewRegister((RegisterMessage) message);
        break;
      case LOGIN:
        LoginMessage loginMessage = (LoginMessage) message;
        networkManager.sendMessage(new Login(loginMessage.email, loginMessage.password));
        break;
      case UNREGISTER:
        UnregisterMessage unregisterMessage = (UnregisterMessage) message;
        networkManager.sendMessage(new Unregister(unregisterMessage.email, unregisterMessage.nickname, unregisterMessage.password));
        break;
      case SHOW_VALID_MOVES:
        handleViewValidMoves((ViewValidMoves) message);
        break;
      case MENU:
        break;
      case MOVE_PIECE:
        handleMovePieceViewMessage((MovePieceMessage) message);
        break;
      case PROFILE:
        // Send a profile request to the net manager
        ProfileMessage profile = (ProfileMessage) message;
        ProfileRequest profileRequest = new ProfileRequest(profile.nickname);
        networkManager.sendMessage(profileRequest);
        break;
      case ACTIVE_GAMES:
        // Send a active games request to the net manager
        ActiveGameRequest activeGameRequest = new ActiveGameRequest(playerNickname);
        networkManager.sendMessage(activeGameRequest);
        break;
      case INBOX:
        // Send an inbox request to the net manager
        InboxRequest inboxRequest = new InboxRequest(playerNickname);
        networkManager.sendMessage(inboxRequest);
        break;
      case GAME_REQUEST:
        // Send a game request to the net manager
        handleGameRequestViewMessage((GameRequestMessage) message);
        break;
      case NEW_INVITE:
        // Send an invite request to the net manager
        InviteMessage inviteMessage = (InviteMessage) message;
        InviteRequest inviteRequest = new InviteRequest(inviteMessage.sender, inviteMessage.recipient);
        networkManager.sendMessage(inviteRequest);
        networkManager.sendMessage(new InboxRequest(playerNickname));
        break;
      case INVITE_RESPONSE:
        InviteMessageResponse inviteMessageResponse = (InviteMessageResponse) message;
        networkManager.sendMessage(new InviteResponse(inviteMessageResponse.inviteID, inviteMessageResponse.response));
        networkManager.sendMessage(new InboxRequest(playerNickname));
        break;
      case LOGOUT:
        networkManager.sendMessage(new Logout(playerNickname));
        playerNickname = null;
        break;
      case RESIGN:
        handleResignViewMessage((ResignMessage) message);
        break;
    }
  }

  /**
   * Handles the views request to start a game
   * @param message the info about the game
   */
  private void handleGameRequestViewMessage(GameRequestMessage message) {
    // Load the game
    currentGame = new ActiveGameInfo(message.gameInfo);
    chadGame = new Game(currentGame.getGameBoard(), currentGame.getTurn());
    viewDriver.handleViewMessage(new MovePieceResponse(getMoveMessage(), chadGame.getBoard()));

    // If the game was finished tell the server we saw the results
    if(currentGame.getEnded()){
      networkManager.sendMessage(new SeeResults(currentGame.getGameID(), currentGame.getColor()));
    }
  }

  /**
   * Handles the view resigning a game
   * @param message the info about the game being resigned
   */
  private void handleResignViewMessage(ResignMessage message) {
    Resign resign = new Resign(message.gameID, playerNickname);
    networkManager.sendMessage(resign);
    // Request new active games
    networkManager.sendMessage(new ActiveGameRequest(playerNickname));
  }

  /**
   * Handle a move message from the view
   * @param message the message with the move
   */
  private void handleMovePieceViewMessage(MovePieceMessage message) {
    // It is not the players turn just redraw the board
    if(chadGame.getTurn() != currentGame.getColor()) {
      viewDriver.handleViewMessage(new MovePieceResponse(getMoveMessage(), chadGame.getBoard()));
    }

    // Check to see if the move was valid
    if(chadGame.move(message.fromLocation.toString(), message.toLocation.toString())){

      boolean endingMove = chadGame.gameover();
      boolean gameIsDraw = chadGame.isDraw();
      String moveMessage = createMoveMessage(endingMove, gameIsDraw);

      MovePieceResponse movePieceResponse = new MovePieceResponse(moveMessage, chadGame.getBoard());
      viewDriver.handleViewMessage(movePieceResponse);
      sentMoveToServer(message, gameIsDraw, endingMove);

    } else {
      // Send a move piece response message with an error
      String error = "Invalid Move.";
      MovePieceResponse movePieceResponse = new MovePieceResponse(error, chadGame.getBoard());
      viewDriver.handleViewMessage(movePieceResponse);
    }
  }

  /**
   * Finds the string reputation of the results of the move
   * @param ending did the move end the game
   * @param draw did the move result in a draw
   * @return the status of the game
   */
  private String createMoveMessage(boolean ending, boolean draw) {
    String message;
    if(draw){
      message = "The game has ended in a draw.";
    } else if(ending){
      message = playerNickname + " has won the game.";
    } else {
      message = getMoveMessage();
    }
    return message;

  }

  /**
   * Sends a move to the server
   * @param message the information on the move
   * @param draw did the move result in a draw
   * @param ending did the move end the game
   */
  private void sentMoveToServer(MovePieceMessage message, boolean draw, boolean ending) {
    // Get piece being moved
    int index = chadGame.getBoard().indexOf(message.toLocation.toString());
    String board = chadGame.getBoard();
    char piece = board.charAt(index - 1);
    String moveString = piece + message.fromLocation.toString() + message.toLocation.toString();
    Move move = new Move(currentGame.getGameID(), moveString, chadGame.getBoard(), ending, draw);
    networkManager.sendMessage(move);
  }

  /**
   * Handles the views request for the valid moves of a piece
   * @param message the info on the piece to get the valid moves for
   */
  private void handleViewValidMoves(ViewValidMoves message) {
    // if the game is over there are no more valid moves
    if(chadGame.gameover()){return;}
    // If it is not your turn there are no valid moves
    if(chadGame.getTurn() != currentGame.getColor()) {return;}
    // Find the valid moves
    String validMoves = chadGame.validMoves(message.location.toString());
    // Tell GUI to what moves to show
    viewDriver.handleViewMessage(new ViewValidMovesResponse(new String [] {validMoves}));
  }

  /**
   * Checks if a word contains a # or :
   * @param word the word to check
   * @return if the word contains # or :
   */
  private boolean containsInvalidCharacters(String word){
    return word.contains("#") || word.contains(":");
  }

  /**
   * Handles the logic for an new user registering
   * @param message the registration info
   */
  private void handleViewRegister(RegisterMessage message) {
    // Check if messages have invalid characters

    if(!checkForValidUserInfo(message)){
      return;
    }
    // Email and nickname do not contain any invalid characters. Send to network manager.
    networkManager.sendMessage(new Register(message.email, message.nickname, message.password));
  }

  /**
   * Checks to see if the user entered valid nickname and email
   * @param message the message with the user info
   * @return true if user name is valid, false otherwise
   */
  private boolean checkForValidUserInfo(RegisterMessage message) {
    boolean validInfo = true;
    String[] messages =  new String[1];

    if(containsInvalidCharacters(message.nickname)){
      messages[0] = "Invalid Nickname - Nickname can not have # or : in it";
      validInfo = false;
    } else if (containsInvalidCharacters(message.email)){
      messages[0] = "Invalid Email - Emails can not have # or : in them";
      validInfo = false;
    }
    // Send failed Register Response Message with reason
    if(!validInfo){
      viewDriver.handleViewMessage(new RegisterResponseMessage(false, messages));
    }
    return validInfo;
  }


  /**
   * Handles all the messages from NetManager(Not Implemented)
   * @param message the message to process
   */
  public void handleNetMessage(NetworkMessage message){
    //System.out.println("Presenter::handleNetMessage:: " + message.type);
    switch (message.type){
      case LOGIN_RESPONSE:
        handleLoginResponseNetMessage((LoginResponse) message);
        break;
      case MOVE:
        handleMoveNetMessage((Move) message);
        break;
      case REGISTER_RESPONSE:
        handleRegisterResponseNetMessage((RegisterResponse) message);
        break;
      case UNREGISTER_RESPONSE:
        handleUnregisterNetMessage((UnregisterResponse) message);
        break;
      // Pass through all responses the are handled by the view
      case INBOX_RESPONSE:
      case PROFILE_RESPONSE:
      case PLAYERS:
      case ACTIVE_GAMES_RESPONSE:
        viewDriver.handleNetMessage(message);
        break;
      default:
        System.err.println("Presenter::handleNetMessage:: received invalid message " + message.type);
    }

  }

  private boolean currentlyPlayingGame(int gameID){
    return currentGame != null && currentGame.getGameID() == gameID;
  }

  /**
   * Handles a move message from the server
   * @param message info about a move
   */
  private void handleMoveNetMessage(Move message) {
    // Ignore if not playing the game
    if (!currentlyPlayingGame(message.gameID)) {
      return;
    }
    // Check if the move message is for the current game
    if (message.ending) {
      handleWinningMoveNetMessage(message);
      networkManager.sendMessage(new SeeResults(message.gameID, currentGame.getColor()));

    }

    // Game is not over

    // Find move info
    Point moveFrom = new Point(message.move.substring(1, 3));
    Point moveTo = new Point(message.move.substring(3));

    chadGame.move(moveFrom.toString(), moveTo.toString());
    viewDriver.handleViewMessage( new MovePieceResponse(getMoveMessage(), chadGame.getBoard()));

  }


  /**
   * Displays the results of a move the opponent used to end the game
   * @param message the move info
   */
  private void handleWinningMoveNetMessage(Move message) {
    String endingMessage;
    if (message.draw) {
      endingMessage = "The game has ended in a draw.";
    } else {
      endingMessage = getCurrentPlayer(chadGame.getTurn()) + " has won.";
    }
    viewDriver.handleViewMessage(new MovePieceResponse(endingMessage, message.board));
  }



  /**
   * Handles a login response from the server
   * @param message the results of the login attempt
   */
  private void handleLoginResponseNetMessage(LoginResponse message) {
    // If the login was successful
    if(message.success) {
      this.playerNickname = message.nickname;
    }
    viewDriver.handleViewMessage(new LoginResponseMessage(message.success, message.nickname));
  }

  /**
   * Handles the response from for a registration attempt
   * @param message the results from a registration attempt
   */
  private void handleRegisterResponseNetMessage(RegisterResponse message) {
    String[] resultsMessage = new String[1];
    if(message.success) {
      // Successful Register
      resultsMessage[0] = "Successfully Registered.";
    } else {
      if(message.reason) {
        // Nickname already taken
        resultsMessage[0] = "Could not register. Nickname already in use.";
      } else {
        // Email already taken
        resultsMessage[0] = "Could not register. Email already in use.";
      }
    }

    RegisterResponseMessage registerResponseMessage = new RegisterResponseMessage(message.success, resultsMessage);
    viewDriver.handleViewMessage(registerResponseMessage);
  }

  /**
   * Sends the results of an unregister attempt
   * @param message the result of the unregister attempt
   */
  private void handleUnregisterNetMessage(UnregisterResponse message) {
    String[] resultsMessage = new String[1];
    if(message.success) {
      // Successfully unregistered
      resultsMessage[0] = "Successfully Unregistered.";
      // Remove data on current login
      playerNickname = "";
      currentGame = null;
    }
    else {
      // Not successful
      resultsMessage[0] = "Unable to unregister. User Information did not match, try again.";
    }
    viewDriver.handleViewMessage( new UnregisterResponseMessage(message.success, resultsMessage));
  }

  /**
   * Gets the player of the current turn's nickname and their color
   * @return the info about the current turn
   */
  private String getMoveMessage() {
    return getCurrentPlayer(chadGame.getTurn()) + "'s turn. Playing: " + getPlayerColor(chadGame.getTurn()) + ".";
  }


  public void createAndShowGUI() { }

  /**
   * Default constructor will need IP and Port for server
   */
  public ChadPresenter(String host, String port, String userInterface){
    try {
      InetAddress addr = InetAddress.getByName(host);
      networkManager = new NetworkManager(addr, Integer.parseInt(port), this);
      networkManager.startThread();
    } catch (UnknownHostException e) {
      System.err.println("Unknown Host");
    } catch (IOException e) { }
    if(userInterface.equals("cli")){
      // Instantiate CLI Controller
      viewDriver = new CLDriver(this);
    } else if(userInterface.equals("gui")){
      // Instantiate GUI Controller
      viewDriver = new SwingController(this);
    }
  }

  /**
   * Default Constructor no arguments
   */
  public ChadPresenter() {

  }

  /**
   * Starts a thread for the Swing GUI or CLI
   * @param gui String with GUI option
   */
  public void start(String gui){
    if(gui.equals("gui")) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          viewDriver.createAndShowGUI();
        }
      });
    }
    else {
      CLIThread cl = new CLIThread();
      cl.run();
    }
  }

  public class CLIThread extends Thread {
    public void run() {
      viewDriver.createAndShowGUI();
    }
  }

  /**
   * Gets the Name of the current players turn
   * @param turn the turn
   * @return Black or White String
   */
  private String getCurrentPlayer(boolean turn) {
    return turn == currentGame.getColor() ? playerNickname : currentGame.getOpponent();
  }

  private String getPlayerColor(Boolean turn) {
    if(turn == currentGame.getColor())
      return currentGame.getColor() ? "Black" : "White";
    return !currentGame.getColor() ? "Black" : "White";
  }

  public static void main(String[] args) {
    // args[0] = "cli" or "gui", args[1] server host, args[2] server port
    ChadPresenter app = new ChadPresenter(args[1], args[2], args[0]);
    app.start(args[1]);
  }
}
