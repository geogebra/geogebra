package org.geogebra.web.html5.io;

import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

/**
 * Web implementation of XML parser
 *
 */
public class MyXMLioW extends org.geogebra.common.io.MyXMLio {

	private XmlParser xmlParser;

	/**
	 * @param kernel
	 *            kernel
	 * @param cons
	 *            construction
	 */
	public MyXMLioW(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	@Override
	protected void createXMLParser() {
		xmlParser = new GwtXmlParser();
	}


	@Override
	protected void resetXMLParser() {
		// nothing to do in web
	}

	@Override
	protected void parseXML(MyXMLHandler xmlHandler, XMLStream stream)
			throws Exception {
		xmlParser.parse(xmlHandler, ((XMLStreamStringW) stream).getString());
	}

	protected class XMLStreamStringW implements XMLStream {

		private String str;

		public XMLStreamStringW(String str) {
			this.str = str;
		}

		public String getString() {
			return str;
		}

	}

	@Override
	protected XMLStream createXMLStreamString(String str) {
		return new XMLStreamStringW(str);
	}

}
