package de.ngmud.xml;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class CXmlNode {

	private String Name;
	private String Content;
	private Map<String,String> Attribs;
	private Map<String,LinkedList<CXmlNode> > SubNodes;
	private CXmlNode Parent;
	
	public void ReConstruct()
	{
		Name=new String("");
		Content=new String("");
		Attribs=new HashMap<String,String>();
		SubNodes=new HashMap<String,LinkedList<CXmlNode> >();
	}
	
	public CXmlNode()
	{
		ReConstruct();
	}
	
	public CXmlNode(CXmlNode Parent,String Name)
	{
		ReConstruct();
		this.Parent=Parent;
		this.Name=Name;
	}
	
	public CXmlNode(CXmlNode Parent)
	{
		this.Parent=Parent;
	}
	
	public final String GetName()
	{	return Name;	}
	
	public final String GetContent()
	{	return Content;	}
	
	public final Map<String,String> GetAttributes()
	{	return Attribs;	}
	
	public final Map<String,LinkedList<CXmlNode> > GetSubNodes()
	{	return SubNodes;	}
	
	public final LinkedList<CXmlNode> GetSubNodes(String Name)
	{
		return SubNodes.get(Name);
	}
	
	public CXmlNode GetParent()
	{	return Parent;	}
	
	public void SetName(String Name)
	{
		this.Name=new String(Name);
	}
	
	public void SetContent(String Content)
	{
		this.Content=Content;
	}
	
	public void NewAttribute(String Name,String Value)
	{
		Attribs.put(Name,Value);
	}
	
	public void NewSubNode(String Name,CXmlNode Node)
	{
		if(!SubNodes.containsKey(Name))
		{
			SubNodes.put(Name, new LinkedList<CXmlNode>());
		}
		SubNodes.get(Name).add(Node);
	}
	
	public void NewSubNode(CXmlNode Node)
	{
		NewSubNode(Node.GetName(),Node);
	}
	
	public void ReCalc()
	{	ReCalc(0);	}
	
	public void ReCalc(int Size)
	{
		Map<String,String> NewAttribs=new HashMap<String,String>(Size==0 ? Attribs.size()+1 : Size,1);
		Map<String,LinkedList<CXmlNode> > NewSubNodes=new HashMap<String,LinkedList<CXmlNode> >(Size==0 ? SubNodes.size()+1 : Size,1);
		NewAttribs.putAll(Attribs);
		NewSubNodes.putAll(SubNodes);
		Attribs.clear();
		SubNodes.clear();
		Attribs=null;
		SubNodes=null;
		Attribs=NewAttribs;
		SubNodes=NewSubNodes;
		Iterator<Map.Entry<String,LinkedList<CXmlNode>>> It=SubNodes.entrySet().iterator();
		while(It.hasNext())
		{
			Iterator<CXmlNode> It2=It.next().getValue().listIterator();
			if(It2.hasNext())
			{
				It2.next().ReCalc(Size);
			}
		}
	}
	
	public String toString()
	{
		String Ret=new String("<"+GetName());
		if(Attribs.size()>0)
		{
			Iterator<Map.Entry<String,String>> It=Attribs.entrySet().iterator();
			while(It.hasNext())
			{
				Map.Entry<String, String> Entry=It.next();
				Ret+=" "+Entry.getKey()+"=\""+Entry.getValue()+"\"";
			}
		}
		Ret+=">";
		
		if(SubNodes.size()>0)
		{
			Ret+=System.getProperty("line.separator");
			Iterator<Map.Entry<String,LinkedList<CXmlNode>>> It=SubNodes.entrySet().iterator();
			while(It.hasNext())
			{
				Iterator<CXmlNode> It2=It.next().getValue().listIterator();
				while(It2.hasNext())
				{
					Ret+=It2.next().toString();
				}
			}
		}
		Ret+=Content;
		Ret+="</"+Name+">"+System.getProperty("line.separator");
		return Ret;
	}
	
	public void Clear()
	{
		Iterator<Map.Entry<String,LinkedList<CXmlNode>>> It=SubNodes.entrySet().iterator();
		while(It.hasNext())
		{
			Iterator<CXmlNode> It2=It.next().getValue().listIterator();
			while(It2.hasNext())
			{
				It2.next().Clear();
			}
		}
		
		Name=null;
		Content=null;
		Attribs.clear();
		Attribs=null;
		SubNodes.clear();
		SubNodes=null;
	}
	
	protected void finalize()
	{
		Clear();
	}
}
