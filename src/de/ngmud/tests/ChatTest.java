package de.ngmud.tests;

import javax.swing.JFrame;

import de.ngmud.ngMUDException;

public class ChatTest extends TestBase {
	private javax.swing.JTextPane Output;
	private javax.swing.JTextField Input;
	public void Main(String[] args) throws ngMUDException {
		ChatTestWindow Test2=new ChatTestWindow();
		Test2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Test2.setVisible(true);
		Output=Test2.getOutput();
		Input=Test2.getInput();
		java.awt.event.ActionListener InputListener=new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Output.setText(Output.getText()+"actionPerformed()\n");
			}
		};
		Input.addActionListener(InputListener);
		Test2.getSendButton().addActionListener(InputListener);
	}
}
