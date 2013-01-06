package geogebra.io;

import geogebra.CommandLineArguments;
import geogebra.main.AppD;

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

import junit.framework.Assert;

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
		try{

			URL schemaFile = new URL("http://www.geogebra.org/ggb.xsd");
			Source xmlFile = new StreamSource(new StringReader(app.getXML()));
			
			SchemaFactory schemaFactory = SchemaFactory
			    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			
			  validator.validate(xmlFile);
			
			}catch(Exception e){
				Assert.assertNull(e.getLocalizedMessage(),e);
			}
		
	}

}
