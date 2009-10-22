
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import org.lwjgl.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.ngmud.CLog;
import org.ngmud.ngMUDException;
import org.ngmud.network.*;
import org.ngmud.network.packets.*;
import org.ngmud.util.CConfig;
import org.ngmud.util.CPair;

import chattest.ChatBot;

import chattest.Server;

public class Test {
	public static void main(String[] args) throws ngMUDException { // Test-main-Func
		CLog.Init("",true,CLog.LOG_LEVEL.CUSTOM,(short)5);
		CLog.Info("Log initialized successfull :D");
		CLog.Custom("Custom-Test Level 1", 1);
		CLog.Custom("Custom-Text Level 6", 6);
		CLog.Custom("Custom-Test Level 5", 5);
		CLog.Debug("Debug-Test");
		CLog.Warning("Warnung-Test");
		CLog.Error("Error-Test");
		CLog.Force("Force-Test");
		CLog.CustomForce("Custom-Force Testüüü");
		
		//GfxTest();
		
		//CPacket.InitPackets("Test.ini");
		CConfig Conf=new CConfig();
		Conf.Init("Packets.ini",false);
		CPair<LinkedList<String>,LinkedList<String>> Pair=Conf.GetKeysAndValuesSeperate();
		CPacket.InitPackets(Pair.GetFirst(),Pair.GetSecond());
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY+1);
		
		ChatBot Bot=new ChatBot();
		
		try
		{
			Bot.setEncoding("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
	         System.out.println("Encoding not supported: " + e);
        }
        Bot.setVerbose(true);
        try {
        	Bot.connect("fhdev.ath.cx",3724);
        }
        catch(Exception e)
        {
        	CLog.Error(e.getMessage());
        }
        Bot.joinChannel("#FH");
        
		
		/*Server Serv=new Server();
		Serv.Run();
		
		CSocket Socket=new CSocket();
		CLog.Warning(Socket.Init(new InetSocketAddress("127.0.0.1",3724), 3724, null, 0, 10, false) ? "Con" : "NoCon");
		CPacket Pack=new CPacket(Socket);
		while(true)
		{
			if(Pack.GetSock().DataAvailable()>0)
			{
				CLog.Warning("There is data!");
			}
			if(Pack.Recv()==CPacket.RECIEVED.ALL)
			{
				CLog.Error(((Pack_Chat)Pack.GetData()).From+": "+CPacketHelper.BytesToString(((Pack_Chat)Pack.GetData()).Content));
			}
			try {
				Thread.sleep(2000);
			} catch(Exception e) {}
			Pack_Chat SendPackData=new Pack_Chat();
			SendPackData.From=1;
			SendPackData.Content=CPacketHelper.StringToBytes("FHHFFH");
			CPacket SendPack=new CPacket(Pack.GetSock());
			SendPack.SetType(Pack_Chat.PACK_NUM);
			SendPack.SetSubType((short)Pack_Chat.SUB_TYPE.SAY.ordinal());
			SendPack.SetData(SendPackData);
			CLog.Warning(SendPack.Send() ? "Sending Data" : "Failure Sending Data");
		}*/
		
		//CLog.UnInit();
	}

	public static void GfxTest()
	{
		// in this case we only care about the dimensions of the screen, we're aiming
		// for an 800x600 display.
		int targetWidth = 800;
		int targetHeight = 600;
		org.lwjgl.LWJGLUtil.log("string");
		 
		DisplayMode chosenMode = null;
		 
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
		     
		 
			for (int i=0;i<modes.length;i++) {
				if ((modes[i].getWidth() == targetWidth) && (modes[i].getHeight() == targetHeight)) {
					chosenMode = modes[i];
					break;
				}
			}
		} catch (LWJGLException e) {     
			Sys.alert("Error", "Unable to determine display modes.");
			System.exit(0);
		}
		 
		// at this point if we have no mode there was no appropriate, let the user know 
		// and give up
		if (chosenMode == null) {
			Sys.alert("Error", "Unable to find appropriate display mode.");
			System.exit(0);
		}
		
		try {
			Display.setDisplayMode(chosenMode);
			Display.setTitle("Example");
			Display.setVSyncEnabled(true);
			Display.create();
			GL11.glClearColor(0,0,0,0);
		} catch (LWJGLException e) {
			Sys.alert("","Unable to create display.");
			System.exit(0);
		}
		boolean gameRunning=true;
		long FPSTime=GetTime()+1000;
		int FPS=0;
		float pos=1.0f;
		 
		while (gameRunning) {
			// perform game logic updates here
			pos -= 0.01f;    
		 
			// render using OpenGL 
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex3f(pos,pos,pos);
			GL11.glVertex3f(1,pos,pos);
			GL11.glVertex3f(1,1,pos);
			GL11.glVertex3f(pos,1,pos);
			GL11.glEnd();
			GL11.glFlush();
			GL11.glFinish();
		    
			if(pos<=-1.0f)
			{	pos=1.0f;	}
		 
			// now tell the screen to update
			Display.update();
			Display.sync(60);
		 
			// finally check if the user has requested that the display be 
			// shutdown
			if (Display.isCloseRequested()) {
				gameRunning = false;
				Display.destroy();
				System.exit(0);
			}
			FPS++;
			if(GetTime()>FPSTime)
			{
				CLog.CustomForce("FPS "+FPS);
				FPS=0;
				FPSTime=GetTime()+1000;
			}
		}
	}

	public static long GetTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

}
