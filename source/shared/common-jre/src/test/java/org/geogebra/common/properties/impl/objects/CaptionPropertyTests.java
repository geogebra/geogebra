package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class CaptionPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSuggestions() {
		setupApp(SuiteSubApp.GRAPHING);
		evaluateGeoElement("text1 = \"abc\"");
		evaluateGeoElement("text2 = \"def\"");
		GeoElement geoElement = evaluateGeoElement("x");
		CaptionProperty captionProperty = new CaptionProperty(getLocalization(), geoElement);
		assertEquals(List.of("text1", "text2"), captionProperty.getSuggestions());
	}

	@Test
	public void testDefaultUnsetCaption() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("x");
		CaptionProperty captionProperty = new CaptionProperty(getLocalization(), geoElement);
		assertNull(captionProperty.getValue());
		assertNull(geoElement.getCaptionSimple());
		assertNull(geoElement.getDynamicCaption());
	}

	@Test
	public void testSettingAndUpdatingSuggestedCaption() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("text1 = \"abc\"");
		GeoElement geoElement = evaluateGeoElement("x");
		CaptionProperty captionProperty = new CaptionProperty(getLocalization(), geoElement);

		captionProperty.setValue("text1");
		assertEquals("text1", captionProperty.getValue());
		assertNull(geoElement.getCaptionSimple());
		assertEquals("abc", geoElement.getDynamicCaption().getTextString());

		editGeoElement(geoText, "text1 = \"def\"");
		assertEquals("text1", captionProperty.getValue());
		assertNull(geoElement.getCaptionSimple());
		assertEquals("def", geoElement.getDynamicCaption().getTextString());
	}

	@Test
	public void testSwitchingBetweenStaticAndDynamicCaption() {
		setupApp(SuiteSubApp.GRAPHING);
		evaluateGeoElement("text1 = \"abc\"");
		GeoElement geoElement = evaluateGeoElement("x");
		CaptionProperty captionProperty = new CaptionProperty(getLocalization(), geoElement);

		captionProperty.setValue("text1");
		assertEquals("text1", captionProperty.getValue());
		assertNull(geoElement.getCaptionSimple());
		assertEquals("abc", geoElement.getDynamicCaption().getTextString());

		captionProperty.setValue("def");
		assertEquals("def", captionProperty.getValue());
		assertEquals("def", geoElement.getCaptionSimple());
		assertNull(geoElement.getDynamicCaption());
	}
}
