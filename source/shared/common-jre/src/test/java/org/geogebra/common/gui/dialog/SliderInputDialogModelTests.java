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

package org.geogebra.common.gui.dialog;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.dialog.SliderInputDialogModel.Field;
import org.geogebra.common.gui.dialog.SliderInputDialogModel.SliderType;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SliderInputDialogModelTests extends BaseAppTestSetup {
	private SliderInputDialogModel model;

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
		model = new SliderInputDialogModel(getApp(), getApp().getActiveEuclidianView(),
				getLocalization(), getKernel(), 100, 100);
	}

	@Test
	public void testDefaultNumberFields() {
		assertAll(
				() -> assertEquals("a", model.getLastValidField(SliderType.NUMBER, Field.NAME)),
				() -> assertEquals("-5", model.getLastValidField(SliderType.NUMBER, Field.MIN)),
				() -> assertEquals("5", model.getLastValidField(SliderType.NUMBER, Field.MAX)),
				() -> assertEquals("0.1", model.getLastValidField(SliderType.NUMBER, Field.STEP)));
	}

	@Test
	public void testDefaultAngleFields() {
		assertAll(
				() -> assertEquals("α", model.getLastValidField(SliderType.ANGLE, Field.NAME)),
				() -> assertEquals("0°", model.getLastValidField(SliderType.ANGLE, Field.MIN)),
				() -> assertEquals("360°", model.getLastValidField(SliderType.ANGLE, Field.MAX)),
				() -> assertEquals("1°", model.getLastValidField(SliderType.ANGLE, Field.STEP)));
	}

	@Test
	public void submitCreatesLabeledNumberGeo() {
		assertTrue(model.submit(SliderType.NUMBER, "s", "-5", "5", "0.1"));
		assertInstanceOf(GeoNumeric.class, lookup("s"));
	}

	@Test
	public void submitSetsNumberBounds() {
		assertTrue(model.submit(SliderType.NUMBER, "s", "-3", "7", "0.5"));
		GeoNumeric geo = (GeoNumeric) lookup("s");
		assertAll(
				() -> assertEquals(-3.0, geo.getIntervalMin(), 1e-10),
				() -> assertEquals(7.0, geo.getIntervalMax(), 1e-10),
				() -> assertEquals(0.5, geo.getAnimationStep(), 1e-10));
	}

	@Test
	public void submitCreatesLabeledAngleGeo() {
		assertTrue(model.submit(SliderType.ANGLE, "alpha", "0°", "360°", "1°"));
		assertInstanceOf(GeoAngle.class, lookup("alpha"));
	}

	@Test
	public void submitSetsAngleBoundsInRadians() {
		assertTrue(model.submit(SliderType.ANGLE, "alpha", "0°", "180°", "1°"));
		GeoNumeric geo = (GeoNumeric) lookup("alpha");
		assertAll(
				() -> assertInstanceOf(GeoAngle.class, lookup("alpha")),
				() -> assertEquals(0.0, geo.getIntervalMin(), 1e-10),
				() -> assertEquals(Math.PI, geo.getIntervalMax(), 1e-10));
	}

	@Test
	public void validateFieldReturnsErrorForInvalidName() {
		String previousName = model.getLastValidField(SliderType.NUMBER, Field.NAME);
		String error = model.validateField(SliderType.NUMBER, Field.NAME, "");
		assertAll(
				() -> assertNotNull(error),
				() -> assertEquals(previousName,
						model.getLastValidField(SliderType.NUMBER, Field.NAME)));
	}

	@Test
	public void validateFieldReturnsErrorForInvalidNumber() {
		assertNull(model.validateField(SliderType.NUMBER, Field.MIN, "-3"));
		String error = model.validateField(SliderType.NUMBER, Field.MIN, "abc");
		assertAll(
				() -> assertNotNull(error),
				() -> assertEquals("-3", model.getLastValidField(SliderType.NUMBER, Field.MIN)));
	}

	@Test
	public void validateFieldUpdatesLastValidValueForValidInput() {
		String error = model.validateField(SliderType.NUMBER, Field.STEP, "0.25");
		assertAll(
				() -> assertNull(error),
				() -> assertEquals("0.25", model.getLastValidField(SliderType.NUMBER, Field.STEP)));
	}

	@Test
	public void submitReturnsFalseForInvalidInput() {
		assertFalse(model.submit(SliderType.NUMBER, "s", "abc", "5", "0.1"));
		assertNull(lookup("s"));
	}

	@Test
	public void submitReturnsFalseForInvalidName() {
		assertFalse(model.submit(SliderType.NUMBER, "1a", "-5", "5", "0.1"));
		assertNull(lookup("1a"));
	}
}
