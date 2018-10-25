package client.gui.cl;

import client.gui.LoginView;

public class CLLogin implements LoginView {
  //
  public void showSplash() {
    StringBuilder res = new StringBuilder();
    res.append("{  W  E  L  C  O  M  E       T  O  }\n");
    res.append("####################################\n");
    res.append("##|  ____|| |##| | / __ \\ |  __ \\ ##\n");
    res.append("##| |#####| |##| || |##| || |##\\ |##\n");
    res.append("##| |#####|  __  ||  __  || |###||##\n");
    res.append("##| |#####| |##| || |##| || |##/ /##\n");
    res.append("##|______||_|##|_||_|##|_||_____/ ##\n");
    res.append("####################################\n");
    res.append("|  ____|| |##| ||  __|| ____||  ___|\n");
    res.append("| |#####| |##| || |###| |####| |####\n");
    res.append("| |#####|  __  ||  __||___  ||___  |\n");
    res.append("| |#####| |##| || |#######| |####| |\n");
    res.append("|______||_|##|_||____||_____||_____|\n");
    res.append("####################################\n");

    res.append("\n\n{A Chess variant by Christian Freeling (1979)}\n");
    res.append("{     Implementation by WareWolves (2018)    }\n");
    System.out.println(res);
  }
  /**
   * Shows the Login screen options
   */
  public void showLogin() {
    StringBuilder res = new StringBuilder();
    res.append("1.[     Login    ]\n");
    res.append("2.[    Register  ]\n");
    System.out.println(res);
  }
}
