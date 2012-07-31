package geogebra.web.io;


import geogebra.common.io.DocHandler;

import java.util.LinkedHashMap;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

public class GwtXmlParser implements XmlParser {
	
	public void parse(DocHandler docHandler, String xml) throws Exception {
		Document doc = parseXml(xml);
		docHandler.startDocument();
		recursiveElementWalk(docHandler, doc.getDocumentElement());
		docHandler.endDocument();
	}
	
	private Document parseXml(String xml) throws Exception {
		try {
			return XMLParser.parse(xml);
		} catch (DOMParseException ex) {
			throw new ConstructionException(ex);
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
