package geogebra.web.io;


import java.util.HashMap;
import java.util.Map;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

public class GwtXmlParser implements XmlParser {
	
	@Override
	public void parse(DocHandler docHandler, String xml) throws ConstructionException {
		Document doc = parseXml(xml);
		docHandler.startDocument();
		recursiveElementWalk(docHandler, doc.getDocumentElement());
		docHandler.endDocument();
	}
	
	private Document parseXml(String xml) throws ConstructionException {
		try {
			return XMLParser.parse(xml);
		} catch (DOMParseException ex) {
			throw new ConstructionException(ex);
		}
	}

	private void recursiveElementWalk(DocHandler docHandler, Element element) {
		docHandler.startElement(element.getTagName(), getAttributesFor(element));
		
		NodeList children = element.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				recursiveElementWalk(docHandler, (Element) child);
			}
		}
		
		docHandler.endElement(element.getTagName());
	}

	private Map<String, String> getAttributesFor(Element element) {
		HashMap<String, String> copiedAttributes = new HashMap<String, String>();
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);
			copiedAttributes.put(attr.getName(), attr.getValue());
		}
		
		return copiedAttributes;
	}
	
}
