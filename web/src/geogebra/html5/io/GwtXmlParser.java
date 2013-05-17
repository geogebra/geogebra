package geogebra.html5.io;


import geogebra.common.io.DocHandler;
import geogebra.common.io.QDParser;
import geogebra.common.main.App;

import java.io.StringReader;
import java.util.LinkedHashMap;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class GwtXmlParser implements XmlParser {
	
	public void parse(DocHandler docHandler, String xml) throws Exception {
		Document doc = null;
		try{
			doc = XMLParser.parse(xml);
			docHandler.startDocument();
			recursiveElementWalk(docHandler, doc.getDocumentElement());
			docHandler.endDocument();
		}catch(Exception e){
			//In Win8 app the parser may fail
			App.debug("Native parser failed"+e.getCause());
			try{
				new QDParser().parse(docHandler, new StringReader(xml));
			}catch(Exception e2){
				throw new ConstructionException(e);	
			}
		}
		
	}

	private void recursiveElementWalk(DocHandler docHandler, Element element) throws Exception {
		docHandler.startElement(element.getTagName(), getAttributesFor(element));

		NodeList children = element.getChildNodes();
		if (element.hasChildNodes())
			for(int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					recursiveElementWalk(docHandler, (Element) child);
				}
			}

		docHandler.endElement(element.getTagName());
	}

	private LinkedHashMap<String, String> getAttributesFor(Element element) {
		LinkedHashMap<String, String> copiedAttributes = new LinkedHashMap<String, String>();
		NamedNodeMap attributes = element.getAttributes();
		if (element.hasAttributes())
			for (int i = 0; i < attributes.getLength(); i++) {
				Attr attr = (Attr) attributes.item(i);
				copiedAttributes.put(attr.getName(), attr.getValue());
			}

		return copiedAttributes;
	}
	
}
