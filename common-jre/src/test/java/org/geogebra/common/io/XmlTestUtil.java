package org.geogebra.common.io;

import java.io.StringReader;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Assert;
import org.xml.sax.SAXParseException;

public class XmlTestUtil {
	/**
	 * Validate app's state against XML schema.
	 * 
	 * @param application
	 *            app
	 */
	public static void testCurrentXML(AppCommon application) {
		String xml = "";
		try {

			URL schemaFile = new URL("https://www.geogebra.org/apps/xsd/ggb.xsd");
			xml = application.getXML();
			Source xmlFile = new StreamSource(new StringReader(xml));

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			validator.validate(xmlFile);
		} catch (SAXParseException se) {
			int l = se.getLineNumber();
			String[] rows = xml.split("\\n");
			for (int i = l - 2; i < l + 3 && i > 0 && i < rows.length; i++) {
				System.out.println(rows[i]);

			}
			Assert.assertNull(se.getLocalizedMessage(), se);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertNull(e.getLocalizedMessage(), e);
		}

	}

}
