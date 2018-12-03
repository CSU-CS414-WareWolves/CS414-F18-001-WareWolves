package client.gui.cl;

import client.Point;
import client.game.Game;
import client.gui.ChadGameDriver;
import client.presenter.ChadPresenter;
import client.presenter.controller.MenuMessageTypes;
import client.presenter.controller.messages.*;
import client.presenter.network.messages.ActiveGameResponse;
import client.presenter.network.messages.GameInfo;
import client.presenter.network.messages.InboxResponse;
import client.presenter.network.messages.InviteResponse;
import client.presenter.network.messages.NetworkMessage;
import client.presenter.network.messages.Players;
import client.presenter.network.messages.ProfileResponse;
import client.presenter.network.messages.RegisterResponse;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class CLDriver implements ChadGameDriver {

  private ChadPresenter controller;

  private CLLogin login;
  private CLMenu menu;
  private CLGameView game;
  private Scanner keys;

  // Active player nickname
  private String nickname;

  private Game chadGame;
  private int gameid;
  private String[] activePlayers;

  public CLDriver(ChadPresenter _controller){
    controller = _controller;
    login = new CLLogin();
    menu = new CLMenu();
    game = new CLGameView();

    activePlayers = new String[]{};
    keys = new Scanner(System.in);
  }

  /**
   * Gets CLDriver's CLLogin instance
   * @return class instance of CLLogin
   */
  public CLLogin getLogin() {
    return login;
  }

  /**
   * Gets CLDriver's CLMenu instance
   * @return class instance of CLMenu
   */
  public CLMenu getMenu() {
    return menu;
  }

  /**
   * Gets CLDriver's CLGameView instance
   * @return class instance of CLGameView
   */
  public CLGameView getGame() {
    return game;
  }

  /**
   * Creates space for readability of the command-line
   * (returns nothing, but prints a long line and some space for readability)
   */
  public void clearScreen() {
    System.out.println("\n-----------------------------------------------------------\n");
  }

  /**
   * Prints the splash and title screen menu
   */
  public void createAndShowGUI(){
    login.showSplash();
    login.showLogin();
    chadGame = new Game();

    handleTitleScreen();
  }

  /**
   *
   */
  public void handleTitleScreen() {
    clearScreen();
    int option = 0;
    try {
      while (true) {
        option = keys.nextInt();
        switch (option) {
          case 1:
            controller.handleViewMessage(handleLogin());
            break;
          case 2:
            controller.handleViewMessage(handleRegister());
            break;
          case 3:
            //handleLogout();
            break;
          default:
            warningValidOption();
            login.showSplash();
            login.showLogin();
            clearScreen();
        }
      }
    } catch(NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles a given NetworkMessage, acts according to the its type
   * @param message a NetworkMessage with a type and data dependent on its type
   */
  public void handleNetMessage(NetworkMessage message){
    switch(message.type){
      case ACTIVE_GAMES_RESPONSE:
        ActiveGameResponse agr = (ActiveGameResponse) message;
        handleActiveGames(agr.gameIDs, agr.opponents);
        controller.handleViewMessage(handleSelectGame());
        break;
      case GAME_INFO:
        GameInfo gi = (GameInfo) message;
        chadGame = new Game(gi.gameBoard, gi.turn);
        showGame();
        break;
      case INBOX_RESPONSE:
        InboxResponse ir = (InboxResponse) message;
        controller.handleViewMessage(handleInbox(ir.inviteIDs, ir.sendDates, ir.senders));
        break;
      case LOGOUT:
        login.showLogout();
        break;
      case MOVE:
        break;
      case PLAYERS:
        Players p = (Players) message;
        activePlayers = p.players;
//        ProfileMessage pm = handleProfile();
//        controller.handleViewMessage(pm);
        break;
      case PROFILE_RESPONSE:
        //TODO
        ProfileResponse pr = (ProfileResponse) message;
//        menu.showStats(pr.whitePlayers, pr.blackPlayers, pr.results, pr.startDates, pr.endDates);
        break;
      case REGISTER_RESPONSE:
        RegisterResponse rr = (RegisterResponse) message;
        handleViewMessage(new RegisterResponseMessage(rr.success, new String[]{}));
        break;
    }
  }

  /**
   * Handles a given ViewMessage, acts according to the its type
   * @param message a ViewMessage with a type and data dependent on its type
   */
  public void handleViewMessage(ViewMessage message){
    switch (message.messageType){
      case GAME_REQUEST:
        GameRequestMessage gr = handleSelectGame();
        controller.handleViewMessage(gr);
        break;
      case LOGIN:
        try {
          LoginMessage lm = handleLogin();
          controller.handleViewMessage(lm);
        } catch (NoSuchAlgorithmException e) {
          //handle error
          e.printStackTrace();
        }
        break;
      case LOGIN_RESPONSE:
        LoginResponseMessage lrm = (LoginResponseMessage) message;
        if(lrm.success){
          //if login successful, print main menu
          //-send presenter a message for option chose
          this.nickname = lrm.nickname;
          menu.showMenu(nickname);
          ViewMessage vm = handleMenu();
          controller.handleViewMessage(vm);
        }
        else {
          login.failedCreds(0);
          handleTitleScreen();
        }
        break;
      case MOVE_PIECE:
        ViewMessage mpm = handleMovePiece();
        controller.handleViewMessage(mpm);
        break;
      case MOVE_PIECE_RESPONSE:
        MovePieceResponse mpr = (MovePieceResponse) message;
        game.showGameBoard(mpr.gameBoard);
        System.out.println(mpr.message);
        ViewMessage vm = handleMenu();
        controller.handleViewMessage(vm);
        break;
      case REGISTER:
        try {
          RegisterMessage rm = handleRegister();
          controller.handleViewMessage(rm);
        } catch (NoSuchAlgorithmException e) {
          //handle error
          e.printStackTrace();
        }
        break;
      case REGISTER_RESPONSE:
        //TODO
        //ask about this message?
        RegisterResponseMessage rrm = (RegisterResponseMessage) message;
        if(rrm.success){
          System.out.println(rrm.messages);
          menu.showMenu(nickname);
          ViewMessage vmm = handleMenu();
          controller.handleViewMessage(vmm);
        }
        else{
          login.failedCreds(1);
        }
        break;
      case SHOW_VALID_MOVES:
        //Give presenter valid moves
        //TODO
        chadGame.validMoves(((ViewValidMoves)message).location.toString());
        break;
      case SHOW_VALID_MOVES_RESPONSE:
        ViewValidMovesResponse vvmr = (ViewValidMovesResponse) message;
        String[] validMovesArray1 = vvmr.locations;
        game.showValidMoves(validMovesArray1);
        break;
      case UNREGISTER:
        menu.unregisterUser();
        UnregisterMessage urm = handleUnregister();
        controller.handleViewMessage(urm);
        break;
      case UNREGISTER_RESPONSE:
        handleUnregister();
        //TODO
        break;
    }
  }

  /**
   * Handles login for an existing user
   * @return a LoginMessage with the user's input
   */
  public LoginMessage handleLogin() throws NoSuchAlgorithmException {
    clearScreen();
    String email = "";
    String pass = "";

    System.out.println("Enter your e-mail:");
    while(email.equals("")) {
      email = keys.nextLine();
    }
    System.out.println("Enter your password:");
    pass = keys.nextLine();

    return new LoginMessage(email, pass);
  }

  /**
   * Handles registration for a new user
   * @return a RegisterMessage with the new user's input
   * @throws NoSuchAlgorithmException
   */
  public RegisterMessage handleRegister() throws NoSuchAlgorithmException {
    clearScreen();
    String email = "";
    String pass = "";
    String nick = "";

    System.out.println("Please enter a valid e-mail:");
    while(email.equals("")) {
      email = keys.nextLine();
    }
    System.out.println("Enter a unique nickname:");
    while(nick.equals("")) {
      nick = keys.nextLine();
    }
    System.out.println("Enter a strong password:");
    pass = keys.nextLine();

    return new RegisterMessage(email, pass, nick);
  }

  /**
   * Handles main menu interactions
   * @return a ViewMessage corresponding to the option chosen
   */
  public ViewMessage handleMenu(){
    int option;
    while(true) {
      option = keys.nextInt();
      menu.showMenu(nickname);
      switch (option) {
        case 1:
          //View Active Games
          return new ActiveGameMessage();
        case 2:
          //View Inbox
          return new InboxMessage();
        case 3:
          //Send Outbox
          return handleOutbox();
        case 4:
          //View Stats
          return handleProfile();
        case 5:
          //Unregister
          return handleUnregister();
        case 6:
          //Logout
          System.out.println("[!] Hope to see you again soon!");
          //TODO
          //merge new messages
//          return new LogoutMessage();
          break;
        default:
          clearScreen();
          warningValidOption();
          menu.showMenu(nickname);
          break;
      }
    }
  }

  /**
   * Handles unregister confirmation for current user
   * @return an Unregister message
   */
  public UnregisterMessage handleUnregister() {
    clearScreen();
    String email;
    String pass;

    menu.unregisterUser();
    System.out.println("E-mail:");
    email = keys.nextLine();
    System.out.println("Password:");
    pass = keys.nextLine();
    
    try {
      return new UnregisterMessage(email, pass, nickname);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Shows the user's active games
   * @param ids list of ids of invitations
   * @param opponents list of nicknames the player has an invite from
   * @return MenuMessage object with a String array = {gameId, opponent's nickname}
   */
  public void handleActiveGames(int[] ids, String[] opponents){
    game.showCurrentGames(ids, opponents);
  }

  /**
   * Handle the game selection screen
   * @return a GameRequestMessage with chosen gameID
   */
  public GameRequestMessage handleSelectGame() {
    int option = keys.nextInt();
    return new GameRequestMessage(new String[]{Integer.toString(option)});
  }

  /**
   * Handle in-game interactions with the board
   * @return a ViewMessage corresponding to the user's actions
   */
  public ViewMessage handleMovePiece() {
    String from;
    String to;
    while (true) {
      System.out.println("~ Select a piece (e.g. \"1a\"): ");
      from = keys.nextLine();
      //check for exit or resignation
      if(from.toUpperCase().equals("EXIT")) {
        return handleMenu();
      }
      else if(from.toUpperCase().equals("RESIGN")) {
        //TODO
        //merge in new messages
//        return new ResignMessage();
        return handleMenu();
      }

      //Display valid moves for selected piece
      //TODO: helper to convert validMoves String into String[]
      String[] moves = {chadGame.validMoves(from)};
      game.showValidMoves(moves);

      System.out.println("[!] Type \"c\" to cancel piece selection");
      System.out.println("~ Select space to move to (e.g. \"1a\"): ");
      to = keys.nextLine();
      if (!to.equals("c")) {
        break;
      }
    }
    return new MovePieceMessage(new Point(from), new Point(to));
  }

  /**
   * Helper method to show in-game view.
   * (returns nothing but prints a nice view)
   */
  public void showGame(){
    clearScreen();
    game.showGameBoard(chadGame.getBoard());
    game.showInGameMenu();
  }

  /**
   * Handle inbox interactions
   * @param ids array with ids for games
   * @param dates array with invite received dates
   * @param senders array with challenger nicknames
   * @return an AcceptInvite message with chosen id/nickname
   */
  public MenuMessage handleInbox(int[] ids, String[] dates, String[] senders){
    clearScreen();
    menu.viewInvites(ids, dates, senders);

    String[] info = new String[2];
    int option = keys.nextInt();

    info[0] = Integer.toString(ids[option]);
    info[1] = senders[option];
    //TODO: NEED ACCEPT_INVITE
    return new MenuMessage(MenuMessageTypes.SELECT_GAME, info);
  }

  /**
   * Takes input from the user to send a number of invites according to their selection
   * @return a MenuMessage of type SEND_INVITE with a String array of nicknames||emails(?)
   */
  public InviteMessage handleOutbox() {
    clearScreen();
    String info = "";
    menu.requestUsername();
    info = keys.nextLine();
    System.out.println("Invite will be sent to: " + info);
    return new InviteMessage(nickname, info);
  }

  public ProfileMessage handleProfile() {
    clearScreen();
    menu.showPlayers(activePlayers);
    menu.requestUsername();
    String nick = keys.nextLine();
    return new ProfileMessage(nick);
  }

  /**
   * Prints a warning if incorrect input is entered
   * >>May not be needed in this class<<
   */
  private void warningValidOption() {
    System.err.println("[!] Please input a valid option\n");
  }

  public static void main(String[] args) {
  }
}
