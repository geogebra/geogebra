package org.geogebra.common.io;

import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.Test;
import org.mockito.Mockito;

public class MyXMLioTest extends BaseUnitTest {

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
}
