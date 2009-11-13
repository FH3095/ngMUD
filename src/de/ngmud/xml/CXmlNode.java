package de.ngmud.xml;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.ListIterator;

public class CXmlNode {

	private String Name;
	private String Content;
	private Hashtable<String,String> Attribs;
	private Hashtable<String,LinkedList<CXmlNode> > SubNodes;
	private CXmlNode Parent;
	
	public void ReConstruct()
	{
		Name=new String("");
		Content=new String("");
		Attribs=new Hashtable<String,String>();
		SubNodes=new Hashtable<String,LinkedList<CXmlNode> >();
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
	
	public final Hashtable<String,String> GetAttributes()
	{	return Attribs;	}
	
	public final Hashtable<String,LinkedList<CXmlNode> > GetSubNodes()
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
		Hashtable<String,String> NewAttribs=new Hashtable<String,String>(Size==0 ? Attribs.size()+1 : Size);
		Hashtable<String,LinkedList<CXmlNode> > NewSubNodes=new Hashtable<String,LinkedList<CXmlNode> >(Size==0 ? SubNodes.size()+1 : Size);
		NewAttribs.putAll(Attribs);
		NewSubNodes.putAll(SubNodes);
		Attribs.clear();
		SubNodes.clear();
		Attribs=null;
		SubNodes=null;
		Attribs=NewAttribs;
		SubNodes=NewSubNodes;
		Enumeration<LinkedList<CXmlNode> > E=SubNodes.elements();
		while(E.hasMoreElements())
		{
			ListIterator<CXmlNode> It=E.nextElement().listIterator();
			if(It.hasNext())
			{
				It.next().ReCalc(Size);
			}
		}
	}
	
	public String toString()
	{
		String Ret=new String("<"+GetName());
		if(Attribs.size()>0)
		{
			Set<Map.Entry<String, String> > AttribsList=Attribs.entrySet();
			Iterator<Map.Entry<String,String>> It=AttribsList.iterator();
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
			Enumeration<LinkedList<CXmlNode> > Nodes=SubNodes.elements();
			while(Nodes.hasMoreElements())
			{
				ListIterator<CXmlNode> It=Nodes.nextElement().listIterator();
				while(It.hasNext())
				{
					Ret+=It.next().toString();
				}
			}
		}
		Ret+=Content;
		Ret+="</"+Name+">"+System.getProperty("line.separator");
		return Ret;
	}
	
	public void Clear()
	{
		Enumeration<LinkedList<CXmlNode> > Nodes=SubNodes.elements();
		while(Nodes.hasMoreElements())
		{
			ListIterator<CXmlNode> It=Nodes.nextElement().listIterator();
			while(It.hasNext())
			{
				It.next().Clear();
			}
		}
		
		Name=null;
		Content=null;
		Attribs.clear();
		Attribs=null;
		SubNodes.clear();
		SubNodes=null;
	}
}
