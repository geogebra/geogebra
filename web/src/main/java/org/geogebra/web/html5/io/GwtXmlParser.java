package org.geogebra.web.html5.io;

import java.io.StringReader;

import org.geogebra.common.io.DocHandler;
import org.geogebra.common.io.QDParser;

public class GwtXmlParser implements XmlParser {

	public void parse(DocHandler docHandler, String xml) throws Exception {
		// FIXME trying to use QDParser always to fix problem with invalid XML
		// files
		// if(!GWT.isProdMode()){
		parseDirty(docHandler, xml);
		/*
		 * return; } Document doc = null; try{ doc = XMLParser.parse(xml);
		 * docHandler.startDocument(); recursiveElementWalk(docHandler,
		 * doc.getDocumentElement()); docHandler.endDocument(); }catch(Exception
		 * e){ //In Win8 app the parser may fail
		 * App.debug("Native parser failed"+e.getCause());
		 * parseDirty(docHandler, xml); }
		 */

	}

	private static void parseDirty(DocHandler docHandler, String xml)
	        throws ConstructionException {
		try {
			new QDParser().parse(docHandler, new StringReader(xml));
		} catch (Exception e2) {
			throw new ConstructionException(e2);
		}
	}

	/*
	 * private void recursiveElementWalk(DocHandler docHandler, Element element)
	 * throws Exception { docHandler.startElement(element.getTagName(),
	 * getAttributesFor(element));
	 * 
	 * NodeList children = element.getChildNodes(); if (element.hasChildNodes())
	 * for(int i = 0; i < children.getLength(); i++) { Node child =
	 * children.item(i); if (child.getNodeType() == Node.ELEMENT_NODE) {
	 * recursiveElementWalk(docHandler, (Element) child); } }
	 * 
	 * docHandler.endElement(element.getTagName()); }
	 */

	/*
	 * private LinkedHashMap<String, String> getAttributesFor(Element element) {
	 * LinkedHashMap<String, String> copiedAttributes = new
	 * LinkedHashMap<String, String>(); NamedNodeMap attributes =
	 * element.getAttributes(); if (element.hasAttributes()) for (int i = 0; i <
	 * attributes.getLength(); i++) { Attr attr = (Attr) attributes.item(i);
	 * copiedAttributes.put(attr.getName(), attr.getValue()); }
	 * 
	 * return copiedAttributes; }
	 */

}
