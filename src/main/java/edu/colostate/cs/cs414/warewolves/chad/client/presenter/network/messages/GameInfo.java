package edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages;

public class GameInfo extends NetworkMessage {
	/**
	 * ID of the game who's information is stored in the message
	 */
	public final int gameID;
	/**
	 * String representation of the gameBoard
	 */
	public final String gameBoard;
	/**
	 * Turn of the game.
	 */
	public final boolean turn;
	
	/**
	 * Constructor for server, params from DB fetch
	 * @param gameID
	 * @param gameBoard
	 * @param turn
	 */
	public GameInfo(int gameID, String gameBoard, boolean turn) {
		super(NET_MESSAGE_TYPE.GAME_INFO);
		this.gameID = gameID;
		this.gameBoard = gameBoard;
		this.turn = turn;
		length = this.getDataString().getBytes().length;
	}
	
	/**
	 * Constructor for NetManager
	 * Expected: "7:gameID:gameBoard:turn"
	 * @param data
	 */
	public GameInfo(String data) {
		super(NET_MESSAGE_TYPE.GAME_INFO);
		String[] splt = data.split(":");
		this.gameID = Integer.parseInt(splt[1]);
		this.gameBoard = splt[2];
		this.turn = Boolean.parseBoolean(splt[3]);
		length = this.getDataString().getBytes().length;
	}

	@Override
	public String getDataString() {
		return type.typeCode+":"+gameID+":"+gameBoard+":"+turn;
	}

}
