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

package org.geogebra.common.gui.view.algebra.scicalc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.junit.Before;
import org.junit.Test;

public class LabelHiderCallbackTest extends BaseUnitTest {

	private LabelHiderCallback callback;
	private LabelController labelController;

	@Before
	public void setUp() {
		callback = new LabelHiderCallback();
		labelController = new LabelController();
	}

	@Test
	public void testCallbackHidesLabels() {
		String[] inputs = {"1", "x^2", "Cross((1,2), (3,4))", "y=1", "x^2 + y^2 = 5"};
		for (String input: inputs) {
			GeoElement element = (GeoElement) getElementFactory().create(input);
			assertTrue(labelController.hasLabel(element));
			callback.callback(new GeoElement[] { element });
			assertFalse(labelController.hasLabel(element));
		}
	}

	@Test
	public void testCallbackDoesNotHideSliderLabels() {
		GeoElement element = (GeoElement) getElementFactory().create("Slider(2, 20)");
		callback.callback(new GeoElement[] { element });
		assertTrue(labelController.hasLabel(element));
	}
}
