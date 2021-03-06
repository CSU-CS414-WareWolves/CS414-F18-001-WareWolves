package edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages;

import static edu.colostate.cs.cs414.warewolves.chad.client.presenter.SharedTestAttributes.TEST_GAME_BOARD;
import static org.junit.jupiter.api.Assertions.*;

import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.ViewMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovePieceResponseTest {

  private static final String message = "Draw";
  private static MovePieceResponse testMessage;

  @BeforeEach
  public void setup() {
    testMessage = new MovePieceResponse(message, TEST_GAME_BOARD);
  }

  @Test
  public void testType() {
    assertEquals(ViewMessageType.MOVE_PIECE_RESPONSE, testMessage.messageType);
  }

  @Test
  public void testSuccess() {
    assertTrue(message.equals(testMessage.message));
  }

}