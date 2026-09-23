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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_DELETE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_DELETE);
	}

	@Test
	void dragAcrossObjectsShouldDeleteOnlyIntersectedObjects() {
		add("A = (2, -2)");
		add("B = (4, -2)");
		add("C = (5, -5)");

		dragAcrossObjects();

		checkContent("C = (5, -5)");
	}

	@Test
	void clickOverlappingObjectsShouldDeleteOneAtATime() {
		add("a:x=1");
		add("b:y=-1");
		add("C = (5, 5)");
		clickRW(1, -1);
		checkContent("a: x = 1", "C = (5, 5)");
		resetMouseLocation();
		clickRW(1, -1);
		checkContent("C = (5, 5)");
	}

	@Test
	void dragDeletionShouldBeUndoableAsSingleAction() {
		getApp().setUndoActive(true);
		add("A = (2, -2)");
		add("B = (4, -2)");
		getApp().storeUndoInfo();
		dragAcrossObjects();
		checkContentLabels();

		getKernel().undo();

		checkContentLabels("A", "B");
	}

	@Test
	void clickPenStrokeShouldDeleteWholeStroke() {
		add("stroke = PenStroke((2, -2), (3, -2), (4, -2))");

		clickRW(3, -2);

		checkContentLabels();
	}

	@Test
	void dragPenStrokeShouldDeleteOnlyIntersectedPart() {
		add("stroke = PenStroke((2, -2), (3, -2), (4, -2))");

		dragStart(150, 50);
		for (int y = 60; y < 160; y += 10) {
			drag(150, y);
		}
		dragEnd(150, 160);

		GeoElement remainingStroke = lookup("stroke");
		assertNotNull(remainingStroke);
		Drawable drawable = getDrawable(remainingStroke);
		assertAll(
				() -> assertTrue(drawable.hit(110, 100, 5)),
				() -> assertFalse(drawable.hit(150, 100, 5)),
				() -> assertTrue(drawable.hit(190, 100, 5)));
	}

	@Test
	void clickImageShouldNotDeleteImage() {
		GeoImage image = createImageAt(100, 150);
		assertTrue(getDrawable(image).hit(125, 125, 5));

		click(125, 125);

		assertNotNull(lookup("image"));
	}

	@Test
	void dragAcrossImageShouldNotDeleteImage() {
		GeoImage image = createImageAt(100, 150);
		assertTrue(getDrawable(image).hit(125, 125, 5));

		dragStart(50, 125);
		drag(125, 125);
		dragEnd(200, 125);

		assertNotNull(lookup("image"));
	}

	private void dragAcrossObjects() {
		dragStart(50, 100);
		for (int x = 60; x < 250; x += 10) {
			drag(x, 100);
		}
		dragEnd(250, 100);
	}

	private GeoImage createImageAt(int x, int y) {
		GeoImage image = createImage();
		image.setLabel("image");
		image.setAbsoluteScreenLocActive(true);
		image.setAbsoluteScreenLoc(x, y);
		image.setEuclidianVisible(true);
		image.updateRepaint();
		return image;
	}
}
