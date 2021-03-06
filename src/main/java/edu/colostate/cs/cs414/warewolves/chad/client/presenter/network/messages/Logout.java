package edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages;

public class Logout extends NetworkMessage {
	/**
	 * Nickname of player logging out
	 */
	public final String nickname;
	
	
	/**
	 * Constructor for presenter
	 * @param nickname the nickname of the currently logged in user
	 */
	public Logout(String nickname) {
		super(NET_MESSAGE_TYPE.LOGOUT);
		this.nickname = nickname;
		length = this.getDataString().getBytes().length;
	}
	
	/**
	 * Constructor for server
	 * Expected: "3:nickname"
	 * @param data data string read from RecieveThread 
	 * @param off dummy variable to differentiate constructors
	 */
	public Logout(String data, int off) {
		super(NET_MESSAGE_TYPE.LOGOUT);
		nickname = data.split(":")[1];
		length = getDataString().getBytes().length;
	}
	
	
	@Override
	public String getDataString() {
		return type.typeCode+":"+nickname;
	}

}
