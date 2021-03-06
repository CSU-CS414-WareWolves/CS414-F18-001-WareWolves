package edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller;

/**
 * These are the types of message that the View can send and receive
 */
public enum ViewMessageType {
  REGISTER,
  LOGIN,
  UNREGISTER,
  SHOW_VALID_MOVES,
  MENU,
  MOVE_PIECE,
  REGISTER_RESPONSE,
  LOGIN_RESPONSE,
  UNREGISTER_RESPONSE,
  SHOW_VALID_MOVES_RESPONSE,
  MENU_RESPONSE,
  MOVE_PIECE_RESPONSE,
  PROFILE,
  ACTIVE_GAMES,
  INBOX,
  GAME_REQUEST,
  NEW_INVITE,
  INVITE_RESPONSE,
  LOGOUT,
  RESIGN
}
