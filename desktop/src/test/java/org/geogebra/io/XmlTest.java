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

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXParseException;

public class XmlTest {

	static AppDNoGui app;
	private static AlgebraProcessor ap;

	@BeforeClass
	public static void setup() {
		app = new AppDNoGui(new LocalizationD(3), false);
		ap = app.getKernel().getAlgebraProcessor();
		app.setLanguage(Locale.US);
	}

	@Test
	public void test() {
		testCurrentXML(app);
	}

	/**
	 * Validate app's state against XML schema.
	 * 
	 * @param app
	 *            app
	 */
	public static void testCurrentXML(AppDNoGui app) {
		String xml = "";
		try {

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
			String[] rows = xml.split("\\n");
			for (int i = l - 2; i < l + 3 && i > 0 && i < rows.length; i++) {
				System.out.println(rows[i]);

			}
			Assert.assertNull(se.getLocalizedMessage(), se);
		} catch (Exception e) {
			Assert.assertNull(e.getLocalizedMessage(), e);
		}

	}

	@Test
	public void pointReloadTest() {
		GeoElementND p = ap.processAlgebraCommand("P=(1,1)", true)[0];
		((GeoPoint) p).setAnimationStep(0.01);
		app.setXML(app.getXML(), true);
		Assert.assertEquals(0.01,
				app.getKernel().lookupLabel("P").getAnimationStep(), 1E-8);
	}

	@Test
	public void specialPointsLoadTest() {
		app.setXML(UtilD.loadFileIntoString(
				"src/test/resources/specialpoints.xml"), true);
		Assert.assertEquals(app.getGgbApi().getAllObjectNames().length, 20);
	}

}
