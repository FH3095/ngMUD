package de.ngmud.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.ngmud.CLog;

public class CXml {
	CXmlNode RootNode;
	String FileName;
	
	public final CXmlNode GetRootNode()
	{
		return RootNode;
	}

	public boolean LoadFile(String File,boolean TrimContent)
	{
		FileName=new String("");
		RootNode=null;
		CXmlParser Handler = new CXmlParser();
		Handler.SetTrimContent(TrimContent);
		try {
			SAXParser Parser = SAXParserFactory.newInstance().newSAXParser();
			Parser.parse(new java.io.File(File), Handler);
		}
		catch(Throwable t)
		{
			CLog.Error("Can\'t parse XML-File \""+File+"\". Reason: "+t.getMessage());
			return false;
		}
		RootNode=Handler.GetRootNode();
		if(RootNode==null)
		{
			CLog.Error("Can\'t load XML-File \""+File+"\" for unknown reason (Root-Node=null)");
			return false;
		}
		return true;
	}
	
	public void Clear()
	{
		RootNode.Clear();
		RootNode=null;
	}
	
}