/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
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

	@Issue("APPS-7157")
	@Test
	public void testSettingCaptionEnsuresVisibility() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement("(1,2)");
		element.setLabelVisible(false);
		element.setLabelMode(GeoElementND.LABEL_HIDDEN);
		CaptionProperty captionProperty = new CaptionProperty(getLocalization(), element);

		captionProperty.setValue("caption");
		assertTrue(element.isLabelVisible());
		assertEquals(GeoElementND.LABEL_CAPTION, element.getLabelMode());

		element.setLabelMode(GeoElementND.LABEL_NAME_VALUE);
		captionProperty.setValue("caption");
		assertEquals(GeoElementND.LABEL_CAPTION_VALUE, element.getLabelMode());
	}
}
