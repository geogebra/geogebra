package org.geogebra.common.io;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.Assert;
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
		Assert.assertTrue(fullXml.contains("app=\"graphing\""));
	}

	@Test
	public void testXmlContainsParentName() {
		MyXMLio myXMLio = Mockito.mock(MyXMLio.class, Mockito
				.withSettings()
				.defaultAnswer(Mockito.CALLS_REAL_METHODS)
				.useConstructor(getKernel(), getConstruction()));
		getApp().setConfig(new AppConfigGeometry("suite"));
		String fullXml = myXMLio.getFullXML();
		Assert.assertTrue(fullXml.contains("app=\"suite\" subApp=\"geometry\""));
	}
}
