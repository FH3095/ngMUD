/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngMUD_ChatClient;


import java.applet.Applet;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class EchoDemo extends Applet implements Runnable, ActionListener  {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  TextArea    incoming;  // Zum Anzeigen von Text
  TextField   outgoing;  // Zur Texteingabe
  Thread      t;         // Thread, zum Textempfangen
  DataInputStream in;    // Zum Einlesen vom Server
  DataOutputStream out;  // Ausgabeverbindung zum Server
  Socket      server;    // Verbindung zum Server
  boolean     ok = true; // Status der Verbindung

  public void init() {
    // Erzeugen und Initialisieren der Komponenten
    incoming = new TextArea();
    outgoing = new TextField();
    incoming.setEditable(false);
    // Hinzufügen der Komponenten
    setLayout(new BorderLayout());
    add("Center", incoming);
    add("South", outgoing);
    try {
      // Aufbau der Verbindung
      server = new Socket("79.199.22.57", 3724);
      // Anlegen von Ein- und Ausgabestream
      in = new DataInputStream(server.getInputStream());
      out = new DataOutputStream(server.getOutputStream());
      outgoing.addActionListener(this);
    }
    catch(Exception e) {
      ok = false;
      incoming.append(e.getMessage()+"\n");
      outgoing.setEditable(false);
    }
  }

  public void start() {
    if (t == null) {
      t = new Thread(this);
      t.start();
    }
  }

  public void stop() {
    t = null;
  }

  public void destroy() {
    close();
  }

  public void run() {
    // Überprüfen, ob die Initialisierung der
    // Verbindung geglückt ist
    if (ok) {
      try {
        // Einlesen von Zeichen, bis zum EOF-Character
        // und anschließende Ausgabe
        String text;
        while((text = in.readUTF()) != null) {
          incoming.append(text+"\n");
        }
      }
      // Im Fehlerfall Ausgabe einer Meldung und
      // Schließen der Verbindung
      catch (IOException e) {
        incoming.append(e.getMessage()+"\n");
      }
      close();
    }
  }

  public void close() {
    ok = false;
    // Anhalten des Einlese-Threads
    stop();
    try {
      // Verbindung schließen
      server.close();
    }
    catch(IOException e) {
      incoming.append(e.getMessage()+"\n");
    }
  }

  public void actionPerformed(ActionEvent e) {
    // Wurde Text in das Textfeld eingegeben?
    if (e.getSource() instanceof TextField) {
      // Schicke Meldung an Echo-Server
      try {
	      out.writeUTF(outgoing.getText());
	      outgoing.setText("");
      }
      catch(IOException ex) {
	      incoming.append(ex.getMessage()+"\n");
      }
    }
  }
}










