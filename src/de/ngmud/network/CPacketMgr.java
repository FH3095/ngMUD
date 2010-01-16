package de.ngmud.network;

import java.util.*;

import de.ngmud.CLog;
import de.ngmud.network.packets.CSubPacket;
import de.ngmud.util.CClassFinder;
import de.ngmud.xml.CXmlNode;

public class CPacketMgr {
	protected static TreeMap<Short,Class<?> > PacketTypes=null;
	
	public static boolean InitPackets(CXmlNode Classes)
	{
		String BasePacket;
		BasePacket=Classes.GetAttributes().get("package");
		if(BasePacket==null || BasePacket=="")
		{
			CLog.Error("No package given for the network-packets in config.");
			return false;
		}
		BasePacket+=".";
		
		PacketTypes=new TreeMap<Short,Class<?> >();
		Iterator<LinkedList<CXmlNode>> ClassIt=Classes.GetSubNodes().values().iterator();
		while(ClassIt.hasNext())
		{
			CXmlNode Class=ClassIt.next().peekLast();
			String ClassName=Class.GetName();
			int ClassValue;
			try {
				ClassValue=Integer.parseInt(Class.GetContent());
			}
			catch(NumberFormatException e)
			{
				CLog.Error("Can't convert \""+Class.GetContent()+"\" to number for class \""+
						Class.GetName()+"\"");
				continue;
			}
			if(ClassValue==1)
			{
				Class<?> C=CClassFinder.FindClass(BasePacket,ClassName,true);
				if(C!=null)
				{
					CSubPacket Pack=null;
					try {
						Pack=(CSubPacket)C.newInstance();
					}
					catch(Exception e)
					{
						CLog.Error("Can't create instance from "+C.getName()+
						           " for unknown reason. Exception reports \""+e.getMessage()+"\"");
						continue;
					}
					if(Pack==null)
					{
						CLog.Error("Instance from "+C.getName()+
						           " wasn't created for unknown reason.");
						continue;
					}
					short PackNum=0;
					try {
						PackNum=C.getField("PACK_NUM").getShort(Pack);
					}
					catch(IllegalAccessException e)
					{
						CLog.Error("Can't access PACK_NUM in "+C.getName());
						continue;
					}
					catch(NoSuchFieldException e)
					{
						CLog.Error("Class "+C.getName()+" hasn't a field called PACK_NUM.");
						continue;
					}
					if(PackNum!=0)
					{
						PacketTypes.put(PackNum, C);
						CLog.Debug("New class found: "+C.getName()+" ; PACK_NUM: "+PackNum);
					}
				}
				else
				{
					CLog.Error("No instancible Class "+ClassName+" found in "+BasePacket);
				}
			}
		}
		
		
		return true;
	}
	
	public static CSubPacket GetNewPacket(int Type)
	{
		if(PacketTypes==null)
		{	return null;	}
		
		Class<?> C=PacketTypes.get(Type);
		if(C!=null)
		{
			try {
				return (CSubPacket)(C.newInstance());
			}
			catch(Exception e)
			{
				CLog.Error("Can't create instance from class "+C.getName()+" ("+e.getMessage()+").");
				return null;
			}
		}
		return null;
	}
}
