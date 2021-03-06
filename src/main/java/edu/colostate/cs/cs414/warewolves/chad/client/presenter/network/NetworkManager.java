package edu.colostate.cs.cs414.warewolves.chad.client.presenter.network;

import edu.colostate.cs.cs414.warewolves.chad.client.gui.ChadGameDriver;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages.NetworkMessage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkManager {
	private Socket sock;
	private Sender send;
	private RecieveThread recv;
	private ChadGameDriver presenter;
	
	/**
	 * @constructor
	 * @param addr IP address of server
	 * @param port Port that server is listening on
	 * @throws IOException 
	 */
	public NetworkManager(InetAddress addr, int port, ChadGameDriver presenter) throws IOException {
		sock = new Socket(addr, port);
		send = new Sender(sock);
		recv = new RecieveThread(sock, this);
		this.presenter = presenter;
	}
	
	/**
	 * Starts RecieveThread
	 */
	public void startThread() {
		recv.start();
	}
	
	/**
	 * 
	 * @param msg The Message object to send
	 * @return True if send successful, false if send failed
	 */
	public boolean sendMessage(NetworkMessage msg) {
		return send.sendToServer(msg);
	}

  /**
   *
   * @param msg The Message object to send
   */
	protected void sendToPresenter(NetworkMessage msg) {
		presenter.handleNetMessage(msg);
	}

	
}
