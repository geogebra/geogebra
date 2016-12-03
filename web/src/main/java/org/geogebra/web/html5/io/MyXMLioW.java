package org.geogebra.web.html5.io;

import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

/**
 * Web implementation of XML parser
 *
 */
public class MyXMLioW extends MyXMLio {

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

	protected static class XMLStreamStringW implements XMLStream {

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

	@Override
	public final void readZipFromString(byte[] zipFile) throws Exception {
		// not implemented in web
	}

}
