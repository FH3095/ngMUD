import ngmud.CLog;
import ngmud.ngMUDException;
import ngmud.network.packets.*;
import ngmud.network.*;
import ngmud.util.CConfig;
import ngmud.util.CPair;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

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
		
		GfxTest();
		
		//CPacket.InitPackets("Test.ini");
		CConfig Conf=new CConfig();
		Conf.Init("Packets.ini",false);
		CPair<LinkedList<String>,LinkedList<String>> Pair=Conf.GetKeysAndValuesSeperate();
		CPacket.InitPackets(Pair.GetFirst(),Pair.GetSecond());
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY+1);
		
		Server Serv=new Server();
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
		}
		
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
		    //Display.setDisplayMode(chosenMode);
		    Display.setTitle("An example title");
		    Display.setVSyncEnabled(true);
		    Display.create();
		    GL11.glClearColor(00,0,00,0);
		} catch (LWJGLException e) {
		    Sys.alert("","Unable to create display.");
		    System.exit(0);
		}
		boolean gameRunning=true;
		long FPSTime=GetTime()+1000;
		int FPS=0;
		float angle=0;
		 
		while (gameRunning) {
		     // perform game logic updates here
			Display.update();
			angle += 2.0f % 360;
		    GL11.glPushMatrix();
		    GL11.glTranslatef(Display.getDisplayMode().getWidth() / 2, Display.getDisplayMode().getHeight() / 2, 0.0f);
		 
		      // rotate square according to angle
		      GL11.glRotatef(angle, 0, 0, 1.0f);
		 
		      // render the square
		      GL11.glBegin(GL11.GL_QUADS);
		        GL11.glVertex2i(-50, -50);
		        GL11.glVertex2i(50, -50);
		        GL11.glVertex2i(50, 50);
		        GL11.glVertex2i(-50, 50);
		      GL11.glEnd();
		 
		    GL11.glPopMatrix();
		    Display.sync(60);
		     FPS++;
		     if(GetTime()>FPSTime)
		     {
		    	 CLog.CustomForce("FPS "+FPS);
		    	 FPS=0;
		    	 FPSTime=GetTime()+1000;
		     }
		 
		     // finally check if the user has requested that the display be 
		     // shutdown
		     if (Display.isCloseRequested()) {
		           gameRunning = false;
		           Display.destroy();
		           System.exit(0);
		     }
		}
	}
	
	public static long GetTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

}
