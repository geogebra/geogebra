package org.geogebra.common.io;

import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.io.MyXMLioCommon;
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.LocalizationCommonUTF;
import org.geogebra.test.annotation.Issue;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Test;
import org.mockito.Mockito;

public class MyXMLioTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return new AppCommon3D(new LocalizationCommonUTF(3), new AwtFactoryCommon(),
				new AppConfigDefault()) {
			@Override
			public void showErrorDialog(String message) {
				fail(message);
			}
		};
	}

	@Test
	public void testXmlContainsAppCode() {
		MyXMLio myXMLio = Mockito.mock(MyXMLio.class, Mockito
				.withSettings()
				.defaultAnswer(Mockito.CALLS_REAL_METHODS)
				.useConstructor(getKernel(), getConstruction()));
		getApp().setConfig(new AppConfigGraphing());
		String fullXml = myXMLio.getFullXML();
		assertThat(fullXml, containsString("app=\"graphing\""));
	}

	@Test
	public void testXmlContainsParentName() {
		MyXMLio myXMLio = Mockito.mock(MyXMLio.class, Mockito
				.withSettings()
				.defaultAnswer(Mockito.CALLS_REAL_METHODS)
				.useConstructor(getKernel(), getConstruction()));
		getApp().setConfig(new AppConfigGeometry(SUITE_APPCODE));
		String fullXml = myXMLio.getFullXML();
		assertThat(fullXml, containsString("app=\"suite\" subApp=\"geometry\""));
	}

	@Test
	public void loadCompleteZip() throws IOException, XMLParseException {
		MyXMLioJre xmlIO = new MyXMLioCommon(getKernel(), getConstruction());
		xmlIO.readZipFromInputStream(Files.newInputStream(
				Paths.get("src/test/resources/org/geogebra/common/io/ziptest.ggb")), false);
		assertThat(lookup("A"), notNullValue());
		assertThat(getKernel().getLibraryJavaScript(), containsString("console.log"));
		// verify 2d defaults
		GeoPoint point = add("(1,1)");
		assertThat(point.getObjectColor(), equalTo(GColor.ORANGE));
		// verify 3d defaults
		GeoElement cube = add("Cube(A,B)");
		assertThat(cube.getObjectColor(), equalTo(GColor.newColor(216, 27, 96)));
	}

	@Test
	@Issue("APPS-6753")
	public void testErrorHandler() throws IOException, XMLParseException {
		MyXMLioJre xmlIO = new MyXMLioCommon(getKernel(), getConstruction());
		ErrorAccumulator errorHandler = new ErrorAccumulator();
		xmlIO.setErrorHandler(errorHandler);
		xmlIO.doParseXML(new MyXMLioJre.XMLStreamStringJre(
				"<geogebra format=\"5.2\"><construction>"
						+ "<expression exp=\"*\"/></construction></geogebra>"),
				true, false, false , false, false);
		assertEquals("Opening file failed \n" + Unicode.CENTER_DOT
				+ " error in <expression>: label=null, exp= *", errorHandler.getErrors().trim());
	}
}
