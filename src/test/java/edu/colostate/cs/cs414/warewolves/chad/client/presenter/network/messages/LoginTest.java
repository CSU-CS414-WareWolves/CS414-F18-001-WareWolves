package edu.colostate.cs.cs414.warewolves.chad.client.presenter.network.messages;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoginTest {

  private static Login testLogin, testLogin2;
  private final String testUserString = "1:testUser@example.com:TestPassword";

	@BeforeAll
	public static void setup() {
		testLogin = new Login("testUser@example.com", "TestPassword");
		testLogin2 = new Login("1:testUser@example.com:TestPassword");
	}

	@Test
	public void getDataString() {
		assertEquals(testUserString, testLogin.getDataString());
	}

	@Test
	public void getDataString2() {
		assertEquals(testUserString, testLogin2.getDataString());
	}
  
  
}