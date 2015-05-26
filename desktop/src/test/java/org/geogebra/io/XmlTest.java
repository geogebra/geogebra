package org.geogebra.io;

import java.io.StringReader;
import java.net.URL;
import java.util.Locale;

import javax.swing.JFrame;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.main.AppD;
import org.junit.Assert;
import org.junit.Test;

public class XmlTest {

	@Test
	public void test() {
		AppD app = new AppD(new CommandLineArguments(
				new String[]{"--silent"}), new JFrame(), false);
		app.setLanguage(Locale.US);
		testCurrentXML(app);
	}

	public static void testCurrentXML(AppD app) {
		String xml = "";
		try{

			URL schemaFile = new URL("http://www.geogebra.org/ggb.xsd");
			xml = app.getXML();
			Source xmlFile = new StreamSource(new StringReader(xml));
			
			SchemaFactory schemaFactory = SchemaFactory
			    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			
			validator.validate(xmlFile);
			
		} catch (Exception e) {
			System.out.println(xml);
			Assert.assertNull(e.getLocalizedMessage(), e);
		}
		
	}

}
