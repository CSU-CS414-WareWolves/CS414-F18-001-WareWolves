package edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages;

import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.ViewMessageType;
import java.util.Arrays;

public class RegisterResponseMessage extends ViewMessage {

  /**
   * A response of success for the register request
   */
  public final boolean success;
  /**
   * An array of messages that respond to the register request
   */
  public final String[] messages;

  /**
   * Sets if the request was successful and the responding messages
   * @param success success of the register request
   * @param messages response messages to the register request
   */
  public RegisterResponseMessage(boolean success, String[] messages) {
    super(ViewMessageType.REGISTER_RESPONSE);

    this.success = success;
    this.messages = messages;
  }

  @Override
  public boolean equals(Object o){
    if (o == null || !(o instanceof RegisterResponseMessage)) {
      return false;
    }
    RegisterResponseMessage other = (RegisterResponseMessage) o;
    return success == other.success && Arrays.equals(messages, other.messages);
  }
}
