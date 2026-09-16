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

package org.geogebra.common.exam.restrictions.wtr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTestSetup;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WtrOutputFilterTest extends BaseExamTestSetup {

	@BeforeEach
	void setupWtrExam() {
		setupApp(SuiteSubApp.SCIENTIFIC);
		examController.startExam(ExamType.WTR, null);
	}

	@Test
	void hideAngleComputationsDegrees() {
		evaluate("a=1 deg");
		evaluate("b=2");
		evaluate("c=3");
		WtrAlgebraOutputFilter filter = new WtrAlgebraOutputFilter();
		assertFalse(filter.isAllowed(evaluate("pi/deg")[0]));
		assertFalse(filter.isAllowed(evaluate("pi/a")[0]));
		assertTrue(filter.isAllowed(evaluate("sin(a)")[0]));
		assertTrue(filter.isAllowed(evaluate("a+a")[0]));
		assertFalse(filter.isAllowed(evaluate("sin(3deg)+a")[0]));
		assertTrue(filter.isAllowed(evaluate("b*deg")[0]));
		// 3deg is allowed for GeoAngle, but not GeoNumeric (which can't print value in degrees)
		assertFalse(filter.isAllowed(evaluate("c=3deg")[0]));
	}

	@Test
	void hideAngleComputationsRadians() {
		getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
		evaluate("a=1 deg");
		evaluate("b=2");
		WtrAlgebraOutputFilter filter = new WtrAlgebraOutputFilter();
		assertFalse(filter.isAllowed(evaluate("pi/deg")[0]));
		assertFalse(filter.isAllowed(evaluate("pi/a")[0]));
		assertFalse(filter.isAllowed(evaluate("a+a")[0]));
		assertFalse(filter.isAllowed(evaluate("sin(3deg)+a")[0]));
		assertFalse(filter.isAllowed(evaluate("b*deg")[0]));
		// only allowed in trig
		assertTrue(filter.isAllowed(evaluate("sin(a)")[0]));
	}

	@Test
	@Issue("APPS-7922")
	void hideAngleComputationsDegreesMinuteSeconds() {
		getApp().setRounding("3d");
		getKernel().setAngleUnit(Kernel.ANGLE_DEGREES_MINUTES_SECONDS);
		getApp().setAlgebraOutputFilter(new WtrAlgebraOutputFilter());
		GeoElement angle = evaluateGeoElement("a=60°30'50″");
		assertFalse(
				AlgebraItem.shouldShowBothRows(angle, getAlgebraSettings()),
				"Definition and value identical, only show one");
		assertEquals("60°30'50″", angle.toValueString(StringTemplate.defaultTemplate));
		GeoElement trig = evaluateGeoElement("a=asind(1/3)");
		assertEquals("19.471°", trig.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7922")
	void hideAngleComputationsAfterSwitch() {
		getApp().setRounding("3d");
		getKernel().setAngleUnit(Kernel.ANGLE_DEGREE);
		getApp().setAlgebraOutputFilter(new WtrAlgebraOutputFilter());
		GeoElement angle = evaluateGeoElement("a=60°30'50″");
		getKernel().setAngleUnit(Kernel.ANGLE_DEGREES_MINUTES_SECONDS);
		assertEquals(
				"a\\, = \\,60°30'50″",
				angle.getLaTeXAlgebraDescription(true, StringTemplate.latexTemplate));
	}
}
