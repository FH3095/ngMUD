/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngmud_ChatServer;

import java.io.*;
import java.lang.String;
import java.net.*;
import java.util.*;
/**
 *
 * @author shiva
 */
public class ngMud_ChatClient extends Thread {
  Socket s;
  OutputStream out;
  InputStream in;

  public ngMud_ChatClient(Socket s) throws IOException {
    this.s = s;
    out = s.getOutputStream();
    in = s.getInputStream();
  }

  public void run() {
    byte[] buffer = new byte[1024];
    int num;
    try {
      while((num = in.read(buffer)) != -1) {
        out.write(buffer, 0, num);
       
for (byte item: buffer) {
   System.out.print((char) item);
}
        System.out.println("");
      }
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }
}
