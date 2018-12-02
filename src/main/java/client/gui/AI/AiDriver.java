package client.gui.AI;

import client.game.Game;
import client.gui.ChadGameDriver;
import client.presenter.controller.messages.ViewMessage;
import client.presenter.controller.util.HashPasswords;
import client.presenter.network.NetworkManager;
import client.presenter.network.messages.InboxRequest;
import client.presenter.network.messages.InboxResponse;
import client.presenter.network.messages.InviteResponse;
import client.presenter.network.messages.Login;
import client.presenter.network.messages.Move;
import client.presenter.network.messages.NetworkMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public class AiDriver implements ChadGameDriver {

  private NetworkManager network;

  public AiDriver(InetAddress addr, int port) throws IOException, NoSuchAlgorithmException {
    this.network = new NetworkManager(addr, port, this);
    network.sendMessage(new Login(
        "ai@ai.ai", HashPasswords.SHA1FromString("easymode")));
    InboxPing inboxChecker = new InboxPing();
    inboxChecker.run();
  }


  @Override
  public void handleViewMessage(ViewMessage message) {

  }

  @Override
  public void handleNetMessage(NetworkMessage message) {
    switch (message.type) {
      case LOGIN_RESPONSE:
        break;
      case MOVE:
        Move move = (Move) message;
        boolean turn = (Character.isUpperCase(move.move.charAt(0)));
        Game chadGame = new Game(move.board, turn);
        String args = move.board + " " + (turn ? "True" : "False");
        try {
          ProcessBuilder p = new ProcessBuilder(
              "python3", "-c", "\'import ChadML.py as gm; gm.bestMove(" + args + ");\'");
          Process pr = p.start();

          BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
          String aiMove = in.readLine();

          String boardString = chadGame.getBoard();
          int pIndex = boardString.indexOf(aiMove.substring(0, 2));
          String moveFrom = boardString.substring(pIndex - 1, pIndex + 1);
          chadGame.move(aiMove.substring(0, 2), aiMove.substring(2, 4));

          network.sendMessage(new Move(
              move.gameID,
              moveFrom + aiMove.substring(2, 4),
              chadGame.getBoard(),
              chadGame.gameover(), false));

        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      case INBOX_RESPONSE:
        InboxResponse response = (InboxResponse) message;
        for (int i = 0; i < response.inviteIDs.length; ++i){
          if (response.senders[i].equals("AI")){
            network.sendMessage(new InviteResponse(response.inviteIDs[i], true));
          }
        }
        break;
      default:
        break;
    }
  }
  public class InboxPing extends Thread {
    public void run() {
      while (true) {
        network.sendMessage(new InboxRequest("AI"));
        try {
          sleep(10000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  public static void main(String [] args) throws IOException, NoSuchAlgorithmException {
    AiDriver driver = new AiDriver(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
  }

}
