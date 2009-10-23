package org.ngmud.xml;

import org.ngmud.CLog;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


public class CXmlParser extends DefaultHandler {
	private CXmlNode Root;
	private CXmlNode Current;

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
		Current.SetContent(new String(buf,offset,len));
	}
}
