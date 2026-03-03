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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTestSetup;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AngleConversionFilterTest extends BaseExamTestSetup {
	AngleConversionFilter filter;

	@BeforeEach
	public void setupWtrExam() {
		setupApp(SuiteSubApp.SCIENTIFIC);
		examController.startExam(ExamType.WTR, null);
		filter = new AngleConversionFilter();
	}

	@Test
	@Issue("APPS-6299")
	public void example2() {
		getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
		assertFalse(filter.isAllowed(evaluate("a=1 deg")[0]));
		// check that value of a does not leak through computations
		assertFalse(filter.isAllowed(evaluate("a+a")[0]));
		assertFalse(filter.isAllowed(evaluate("sin(3deg)+a")[0]));
	}

	@Test
	@Issue("APPS-6299")
	public void example3() {
		getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
		assertFalse(filter.isAllowed(evaluate("1 deg")[0]));
	}

	@Test
	@Issue("APPS-6299")
	public void example5() {
		evaluate("a=1 deg");
		for (int unit: List.of(Kernel.ANGLE_DEGREES_MINUTES_SECONDS,
				Kernel.ANGLE_RADIANT, Kernel.ANGLE_DEGREE)) {
			getKernel().setAngleUnit(unit);
			assertFalse(filter.isAllowed(evaluate("pi/deg")[0]));
			assertFalse(filter.isAllowed(evaluate("pi/a")[0]));
		}
	}

	@Test
	public void hideAngleComputationsDegrees() {
		getKernel().setAngleUnit(Kernel.ANGLE_DEGREE);
		evaluate("a=1 deg");
		evaluate("b=2");
		evaluate("c=3");
		assertTrue(filter.isAllowed(evaluate("sin(a)")[0]));
		assertTrue(filter.isAllowed(evaluate("a+a")[0]));
		assertFalse(filter.isAllowed(evaluate("sin(3deg)+a")[0]));
		assertTrue(filter.isAllowed(evaluate("b*deg")[0]));
		// 3deg is allowed for GeoAngle, but not GeoNumeric (which can't print value in degrees)
		assertFalse(filter.isAllowed(evaluate("c=3deg")[0]));
	}

	@Test
	public void hideAngleComputationsRadians() {
		getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
		evaluate("a=1deg");
		evaluate("b=2");
		assertFalse(filter.isAllowed(evaluate("b*deg")[0]));
		assertTrue(filter.isAllowed(evaluate("sin(a)")[0]));
	}
}
