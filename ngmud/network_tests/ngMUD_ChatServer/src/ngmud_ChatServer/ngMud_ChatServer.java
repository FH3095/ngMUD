/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngmud_ChatServer;

import java.net.*;


/**
 *
 * @author shiva
 */
public class ngMud_ChatServer implements Runnable  {
  Thread listener;
  ServerSocket server;

  public static void main(String args[]) {
    new ngMud_ChatServer();
  }
  public ngMud_ChatServer () {
    try {
      server = new ServerSocket(3724);
      listener = new Thread(this);
      listener.start();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      while(true) {
        Socket client = server.accept();
        new ngMud_ChatClient(client).start();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}
