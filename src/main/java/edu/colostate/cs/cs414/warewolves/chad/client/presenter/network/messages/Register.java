package edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages;

public class Register extends NetworkMessage{
	/**
	 * Email of attempted registration
	 */
	public final String email;
	/**
	 * Email of attempted registration
	 */
	public final String nickname;
	/**
	 * Hash of password for attempted registration
	 */
	public final String password;
	
	/**
	 * Constructor for the presenter
	 * @param email email keyed in by user
	 * @param nickname chosen nickname
	 * @param hashedPass Hashed password entered in by user
	 */
	public Register(String email, String nickname, String hashedPass) {
		super(NET_MESSAGE_TYPE.REGISTER);
		this.email = email;
		this.nickname = nickname;
		password = hashedPass;
		length = this.getDataString().getBytes().length;
	}
	
	/**
	 * Constructor for server
	 * Expected: "4:email:nickname:password"
	 * @param data String representation of message from RecieveThread
	 */
	public Register(String data) {
		super(NET_MESSAGE_TYPE.REGISTER);
		String[] splt = data.split(":");
		email = splt[1];
		nickname = splt[2];
		password = splt[3];
		length = this.getDataString().getBytes().length;
	}
	
	@Override
	public String getDataString() {
		return type.typeCode+":"+email+":"+nickname+":"+password;
	}

}
