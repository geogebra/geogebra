package org.geogebra.web.html5.io;

import java.io.IOException;
import java.io.StringReader;

import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.QDParser;
import org.geogebra.common.io.XMLParseException;
import org.geogebra.common.io.file.Base64ZipFile;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArchiveLoader;

/**
 * Web implementation of XML parser
 *
 */
public class MyXMLioW extends MyXMLio {

	private QDParser xmlParser;

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
		xmlParser = new QDParser();
	}

	@Override
	protected void resetXMLParser() {
		// nothing to do in web
	}

	@Override
	protected void parseXML(MyXMLHandler xmlHandler, XMLStream stream)
			throws IOException, XMLParseException {
		xmlParser.parse(xmlHandler,
				new StringReader(((XMLStreamStringW) stream).getString()));
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
	public final void readZipFromString(ZipFile zipFile) {
		Base64ZipFile zip = (Base64ZipFile) zipFile;

		String base64 = zip.getBase64();

		((AppW) app).resetPerspectiveParam();
		ArchiveLoader view = ((AppW) app).getArchiveLoader();
		view.processBase64String(base64);

	}

}
