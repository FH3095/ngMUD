package de.ngmud.tests;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Rectangle;
import javax.swing.JTextPane;

public class ChatTestWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton SendButton = null;
	private JTextField Input = null;
	private JTextPane Output = null;
	/**
	 * This is the default constructor
	 */
	public ChatTestWindow() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(672, 358);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getSendButton(), null);
			jContentPane.add(getInput(), null);
			jContentPane.add(getOutput(), null);
		}
		return jContentPane;
	}

	/**
     * This method initializes SendButton	
     * 	
     * @return javax.swing.JButton	
     */
    public JButton getSendButton() {
    	if (SendButton == null) {
    		SendButton = new JButton();
    		SendButton.setName("SendButton");
    		SendButton.setText("Send");
    		SendButton.setBounds(new Rectangle(569, 306, 93, 23));
    	}
    	return SendButton;
    }

	/**
     * This method initializes Input	
     * 	
     * @return javax.swing.JTextField	
     */
    public JTextField getInput() {
    	if (Input == null) {
    		Input = new JTextField();
    		Input.setBounds(new Rectangle(3, 308, 565, 20));
    	}
    	return Input;
    }

	/**
     * This method initializes Output	
     * 	
     * @return javax.swing.JTextPane	
     */
    public JTextPane getOutput() {
    	if (Output == null) {
    		Output = new JTextPane();
    		Output.setBounds(new Rectangle(1, 1, 660, 304));
    		Output.setEditable(false);
    		Output.setEnabled(true);
    		Output.setContentType("text/plain");
    	}
    	return Output;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
