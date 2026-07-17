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

package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MeasureControllerTest extends BaseUnitTest {
	private MeasurementController controller;
	private MeasurementTool ruler;
	private Construction cons;

	@BeforeEach
	void setUp() {
		getApp().setConfig(new AppConfigNotes());
		Kernel kernel = getKernel();
		cons = kernel.getConstruction();
		controller = new MeasurementController(this::createToolImage);
		ruler = controller.getTool(MeasurementToolId.RULER);
	}

	private GeoImage createToolImage(int mode, String fileName) {
		return MeasurementToolTransformerTest.createToolImage(mode, fileName, getConstruction(),
				getApp().getActiveEuclidianView());
	}

	@Test
	void testUnselect() {
		controller.unselect();
		assertFalse(controller.hasSelectedTool());
	}

	@Test
	void testSelectRuler() {
		assertToolSelected(MODE_RULER, "Ruler.svg");
	}

	@Test
	void testRotationCenter() {
		controller.toggleActiveTool(MODE_RULER);
		GPoint2D rotationCenter = controller.getActiveToolCenter(
				controller.getActiveToolImage(), getApp().getActiveEuclidianView());
		assertEquals(109.51, rotationCenter.getX(), .1);
		assertEquals(260, rotationCenter.getY(), .1);
	}

	private void assertToolSelected(int mode, String filename) {
		controller.toggleActiveTool(mode);
		GeoImage toolImage = controller.getActiveToolImage();
		assertEquals(filename, toolImage.getImageFileName());
		assertTrue(cons.isInConstructionList(toolImage),
				filename + " should be in construction");
	}

	@Test
	void testSelectProtractor() {
		assertToolSelected(MODE_PROTRACTOR, "Protractor.svg");
	}

	@Test
	void testSelectTriangleProtractor() {
		assertToolSelected(MODE_TRIANGLE_PROTRACTOR, "TriangleProtractor.svg");
	}

	@Test
	void testToggleRulerOnOff() {
		controller.selectTool(MODE_RULER);
		controller.toggleActiveTool(MODE_RULER);
		assertNull(controller.getActiveToolImage());
	}

	@Test
	void testUnselectRuler() {
		controller.toggleActiveTool(MODE_RULER);
		controller.unselect();
		assertFalse(cons.isInConstructionList(ruler.getImage()), "Ruler is in construction");
	}

}
