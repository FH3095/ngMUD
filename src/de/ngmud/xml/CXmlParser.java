package de.ngmud.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import de.ngmud.CLog;


public class CXmlParser extends DefaultHandler {
	private CXmlNode Root;
	private CXmlNode Current;
	private boolean TrimContent;
	
	public CXmlParser()
	{
		TrimContent=false;
	}
	
	public void SetTrimContent(boolean TrimContent)
	{
		this.TrimContent=TrimContent;
	}

	public CXmlNode GetRootNode()
	{	return Root;	}

	public void startDocument()
	throws SAXException
	{
		Current=Root=new CXmlNode(null,"<root>");
	}

	public void endDocument()
	throws SAXException
	{
		Root.ReCalc();
	}

	public void startElement( String namespaceURI,
							  String LocalName,   // local name
							  String qName,       // qualified name
							  Attributes Attrs )
	throws SAXException
	{
		String Name = ( "".equals( LocalName ) ) ? qName : LocalName;
		CXmlNode Node=new CXmlNode(Current,Name);
		if(Current==null)
		{	CLog.Error("XML-Node doesn't have a parrent-node, which should be impossible.");	}

		if( Attrs != null )
		{
			for( int i=0; i<Attrs.getLength(); i++ )
			{
				Name = Attrs.getLocalName(i);
				if("".equals(Name))
					{	Name = Attrs.getQName(i);	}
				Node.NewAttribute(Name, Attrs.getValue(i));
			}
		}
		Current.NewSubNode(Node);
		Current=Node;
	}

	public void endElement( String namespaceURI,
							String LocalName,     // local name
							String qName )        // qualified name
	throws SAXException
	{
		Current=Current.GetParent();
	}

	public void characters( char[] buf, int offset, int len )
	throws SAXException
	{
		String Content=new String(buf,offset,len);
		if(TrimContent)
		{	Content=Content.trim();	}
		Current.SetContent(Content);
	}
}