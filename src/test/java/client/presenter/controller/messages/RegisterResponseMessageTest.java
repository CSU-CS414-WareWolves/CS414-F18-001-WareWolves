package client.presenter.controller.messages;

import static org.junit.jupiter.api.Assertions.*;

import client.presenter.controller.ViewMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterResponseMessageTest {

  private static final boolean success = true;
  private static final String nickname = "Frodo";
  private static LoginResponseMessage testMessage;

  @BeforeEach
  public void setup() {
    testMessage = new LoginResponseMessage(success, nickname);
  }

  @Test
  public void testType() {
    assertEquals(ViewMessageType.REGISTER_RESPONSE, testMessage.messageType);
  }

  @Test
  public void testSuccess() {
    assertEquals(success, testMessage.success);
  }

  @Test
  public void testMessages() {
    assertEquals(nickname, testMessage.nickname);
  }

}