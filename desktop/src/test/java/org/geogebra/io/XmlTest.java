package org.geogebra.io;

import java.io.StringReader;
import java.net.URL;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXParseException;

public class XmlTest {

	@Test
	public void test() {
		AppDNoGui app = new AppDNoGui(new LocalizationD(3), true);
		app.setLanguage(Locale.US);
		testCurrentXML(app);
	}

	public static void testCurrentXML(AppDNoGui app) {
		String xml = "";
		try{

			URL schemaFile = new URL("http://static.geogebra.org/ggb.xsd");
			xml = app.getXML();
			Source xmlFile = new StreamSource(new StringReader(xml));
			
			SchemaFactory schemaFactory = SchemaFactory
			    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			
			validator.validate(xmlFile);
		} catch (SAXParseException se) {
			int l = se.getLineNumber();
			for (int i = l - 2; i < l + 3; i++) {
				System.out.println(xml.split("\\n")[i]);

			}
			Assert.assertNull(se.getLocalizedMessage(), se);
		} catch (Exception e) {
			Assert.assertNull(e.getLocalizedMessage(), e);
		}
		
	}

}
