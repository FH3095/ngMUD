package de.ngmud.network.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jibble.pircbot.*;

import de.ngmud.CLog;

public class ClientChat extends PircBot {
	private String Nick="Guest";
	private String Password="";
	private String Account="Guest";
	
	private String Server="fhdev.ath.cx";
	private int Port=0;
	
	private boolean IsGuest=false;
	
	public ClientChat()
	{
		super();
	}
	
	public boolean isGuest()
	{
		return IsGuest;
	}
	
	public void setGuest()
	{
		setLoginData("Guest","Guest","");
		this.setAutoNickChange(true);
		IsGuest=true;
	}
	
	public void setLoginData(String Nick,String Account,String Password)
	{
		this.IsGuest=false;
		this.setAutoNickChange(false);
		this.Account=Account;
		this.Password=Password;
		this.Nick=Nick;
		this.setName(Nick);
		this.setLogin(Account);
		this.setFinger("FH_Finger");
		this.setVersion("ngMUD-Client 0.1");
		try {
            this.setEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			CLog.Warning("Can't set encoding for chat-connection to UTF-8. Error: "+e.getMessage());
		}
	}
	
	public void connectToServer(String Server,int Port)
	{
		this.Server=Server;
		this.Port=Port;
		try {
	        this.connect(Server, Port, Password);
        } catch (NickAlreadyInUseException e) {
	        e.printStackTrace();
        } catch (IOException e) {
	        e.printStackTrace();
        } catch (IrcException e) {
	        e.printStackTrace();
        }
	}
	
	protected void onIncomingChatRequest(DccChat Chat)
	{
	}
	
	protected void onIcomingFileTransfer(DccFileTransfer File)
	{
	}
	
	protected void onFinger(String SourceNick,String SourceLogin,String SourceHostname,String Target)
	{
	}
	
	protected void onTime(String SourceNick,String SourceLogin,String SourceHostname,String Target)
	{
	}
	
	protected void onPing(String SourceNick,String SourceLogin,String SourceHostname,String Target,String PingValue)
	{
	}
	
	public void Delete()
	{
		disconnect();
		dispose();
	}
}
