package org.geogebra.common.io;

import static org.junit.Assert.fail;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.geogebra.common.jre.headless.AppCommon;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlTestUtil {

	/**
	 * Validate app's state against XML schema.
	 * 
	 * @param application
	 *            app
	 */
	public static void testCurrentXML(AppCommon application) {
		testCurrentXMLWithURL(application, "https://www.geogebra.org/apps/xsd/ggb.xsd");
	}

	/**
	 * Validate app's state against XML schema at a specified url.
	 *
	 * @param application
	 *            app
	 * @param xsdUrl
	 * 			url to the XML schema
	 */
	public static void testCurrentXMLWithURL(AppCommon application, String xsdUrl) {
		String xml = application.getXML();
		try {

			xml = application.getXML();
			Source xmlFile = new StreamSource(new StringReader(xml));
			String url = System.getProperty("xsdUrl", xsdUrl);
			getValidator(url).validate(xmlFile);
		} catch (SAXParseException se) {
			int l = se.getLineNumber();
			String[] rows = xml.split("\\n");
			for (int i = l - 2; i < l + 3 && i > 0 && i < rows.length; i++) {
				System.out.println(rows[i]);
			}
			fail(se.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	private static Validator getValidator(String url) throws MalformedURLException, SAXException {
		URL schemaFile = new URL(url);
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);
		return schema.newValidator();
	}

}
