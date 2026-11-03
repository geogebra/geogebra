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

package org.geogebra.common.euclidian;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EuclidianViewForNotesTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupNotesApp();
	}

	@Test
	void showAllObjectsPortrait() {
		evaluate("Segment((0,0),(10,10))");
		getApp().getEuclidianView1().setViewShowAllObjects(false, true);
		assertEquals(
				"(-2.64967, -0.31767)",
				evaluateGeoElement("Corner(1)").toValueString(StringTemplate.editTemplate));
		assertEquals(
				"(11.519, 10.31767)",
				evaluateGeoElement("Corner(3)").toValueString(StringTemplate.editTemplate));
	}

	@Test
	void showAllObjectsLandscape() {
		evaluate("Segment((0,0),(20,10))");
		getApp().getEuclidianView1().setViewShowAllObjects(false, true);
		assertEquals(
				"(-2.47228, -3.67011)",
				evaluateGeoElement("Corner(1)").toValueString(StringTemplate.editTemplate));
		assertEquals(
				"(20.6288, 13.67011)",
				evaluateGeoElement("Corner(3)").toValueString(StringTemplate.editTemplate));
	}

	@ParameterizedTest
	@CsvSource({
		"50, 0, true",
		"100, 0, true",
		"400, 0, true",
		"50, 10, true",
		"100, 10, true",
		"400, 10, true",
		"50, 0, false",
		"50, 10, false"
	})
	@Issue("MOW-1946")
	void showAllObjectsCapsZoomAndCentersTinyStroke(
			double initialScale, int steps, boolean keepRatio) {
		EuclidianView view = getApp().getEuclidianView1();
		view.setCoordSystem(100, 200, initialScale, initialScale);
		// This stroke is 2 pixels long at the standard scale of 50 pixels per unit.
		evaluate("PenStroke((3,4),(3.024,4.032))");
		GRectangle bounds = view.getBounds();
		double strokeCenterX = view.toRealWorldCoordX((bounds.getMinX() + bounds.getMaxX()) / 2);
		double strokeCenterY = view.toRealWorldCoordY((bounds.getMinY() + bounds.getMaxY()) / 2);

		view.setViewShowAllObjects(false, keepRatio, steps);

		// When preserving the ratio, Notes reserves 64 pixels on the left for its toolbar.
		double leftMargin = keepRatio ? 64 : 0;
		double centerX = (view.getSettings().getVisibleFromX() + leftMargin + view.getWidth()) / 2.0;
		double centerY = view.getVisibleHeight() / 2.0;
		assertAll(
				() -> assertEquals(400, view.getXscale(), 1e-6, "800% horizontal zoom"),
				() -> assertEquals(400, view.getYscale(), 1e-6, "800% vertical zoom"),
				() -> assertEquals(
						centerX, view.toScreenCoordXd(strokeCenterX), 1e-6, "Stroke centered horizontally"),
				() -> assertEquals(
						centerY, view.toScreenCoordYd(strokeCenterY), 1e-6, "Stroke centered vertically"));
	}

	@Test
	void lineThicknessScaledShouldBeSavedAndReloaded() {
		getApp().getSettings().getEuclidian(1).setLineThicknessScaled(true);
		String xml = getApp().getXML();
		assertTrue(xml.contains("lineThicknessScaled=\"true\""));

		// reset then reload
		getApp().getSettings().getEuclidian(1).setLineThicknessScaled(false);
		getApp().setXML(xml, true);
		assertTrue(getApp().getSettings().getEuclidian(1).getLineThicknessScaled());
	}

	@Test
	void lineThicknessScaledShouldDefaultToFalseAndNotBeSaved() {
		getApp().getSettings().getEuclidian(1).setLineThicknessScaled(false);
		String xml = getApp().getXML();
		assertFalse(xml.contains("lineThicknessScaled"));

		getApp().setXML(xml, true);
		assertFalse(getApp().getSettings().getEuclidian(1).getLineThicknessScaled());
	}

	@Test
	void loadingFileWithoutLineThicknessScaledShouldResetIt() {
		getApp().getSettings().getEuclidian(1).setLineThicknessScaled(true);
		String xml = getApp().getXML().replace(" lineThicknessScaled=\"true\"", "");
		getApp().setXML(xml, true);
		assertFalse(getApp().getSettings().getEuclidian(1).getLineThicknessScaled());
	}
}
