package edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.messages;

import static edu.colostate.cs.cs414.warewolves.chad.client.presenter.SharedTestAttributes.*;
import static org.junit.jupiter.api.Assertions.*;

import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.MenuMessageTypes;
import edu.colostate.cs.cs414.warewolves.chad.client.presenter.controller.ViewMessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class MenuMessageTest {


  private static MenuMessage testMessage;

  @BeforeEach
  public void setup() {
    testMessage = new MenuMessage(MenuMessageTypes.PLAYER_STATS, TEST_MENU_OPTIONS_INT_STRING);
  }

  @DisplayName("testMenuTypes")
  @ParameterizedTest(name = "({0}) should be {0}")
  @EnumSource(
      value = MenuMessageTypes.class,
      names = {"LOGOUT", "PLAYER_STATS", "ACTIVE_GAMES", "INVITES", "SELECT_GAME", "SEND_INVITE"})

  public void testMenuTypes(MenuMessageTypes menuMessageTypes) {
    assertEquals(menuMessageTypes, new MenuMessage(menuMessageTypes, new String[0]).menuType);
  }

  @Test
  public void testType() {
    assertEquals(ViewMessageType.MENU,
        new MenuMessage(MenuMessageTypes.LOGOUT, new String[0]).messageType);
  }

  @Test
  public void testNotEqualNull(){
    assertNotEquals( testMessage, null);
  }

  @Test
  public void testHashCode() {
    MenuMessage testHash = new MenuMessage(MenuMessageTypes.PLAYER_STATS, TEST_MENU_OPTIONS_INT_STRING);
    assertEquals(testMessage.hashCode(), testHash.hashCode());
  }
}