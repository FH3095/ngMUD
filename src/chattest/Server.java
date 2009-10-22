package chattest;

import java.net.InetSocketAddress;
import org.ngmud.*;
import java.util.*;

import org.ngmud.CLog;
import org.ngmud.ngMUDException;
import org.ngmud.network.*;
import org.ngmud.network.packets.*;

public class Server {
	static final int MAX_CONS=5;
	public void Run() throws ngMUDException
	{
		CServer Serv=new CServer();
		Serv.Init(MAX_CONS, CServer.WHEN_LISTEN_FULL.CLOSE, null, 3724, new InetSocketAddress(3724));
		
		CPacket Packs[]=new CPacket[MAX_CONS];
		for(int i=0;i<MAX_CONS;i++)
		{
			Packs[i]=new CPacket();
		}
		while(true)
		{
			Vector<CSocket> NewSocks=Serv.GetNewSocks();
			for(int i=0;i<NewSocks.size();i++)
			{
				for(int j=0;j<MAX_CONS;j++)
				{
					if(Packs[j].GetSock()==null || !Packs[j].GetSock().IsConnected())
					{
						CLog.Warning("New Connection");
						Packs[j].SetSock(NewSocks.get(i));
						break;
					}
				}
			}
			NewSocks.clear();

			for(int i=0;i<MAX_CONS;i++)
			{
				if(Packs[i].GetSock()!=null)
				{
					if(Packs[i].GetSock().IsConnected() && Packs[i].GetSock().DataAvailable()>0)
					{
						CLog.Warning("New Data!");
						if(Packs[i].Recv()==CPacket.RECIEVED.ALL)
						{
							((Pack_Chat)Packs[i].GetData()).From=i;
							for(int j=0;j<MAX_CONS;j++)
							{
								Packs[i].Send(Packs[j].GetSock());
							}
						}
					}
				}
			}
		}
	}
}
