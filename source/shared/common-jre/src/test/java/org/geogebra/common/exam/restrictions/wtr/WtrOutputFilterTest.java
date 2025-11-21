package org.geogebra.common.exam.restrictions.wtr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTestSetup;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.Kernel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WtrOutputFilterTest extends BaseExamTestSetup {

	@BeforeEach
	public void setupCvteExam() {
		setupApp(SuiteSubApp.SCIENTIFIC);
		examController.startExam(ExamType.WTR, null);
	}

	@Test
	public void hideAngleComputationsDegrees() {
		evaluate("a=1 deg");
		evaluate("b=2");
		evaluate("c=3");
		WtrAlgebraOutputFilter filter = new WtrAlgebraOutputFilter(null);
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
	public void hideAngleComputationsRadians() {
		getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
		evaluate("a=1 deg");
		evaluate("b=2");
		WtrAlgebraOutputFilter filter = new WtrAlgebraOutputFilter(null);
		assertFalse(filter.isAllowed(evaluate("pi/deg")[0]));
		assertFalse(filter.isAllowed(evaluate("pi/a")[0]));
		assertFalse(filter.isAllowed(evaluate("a+a")[0]));
		assertFalse(filter.isAllowed(evaluate("sin(3deg)+a")[0]));
		assertFalse(filter.isAllowed(evaluate("b*deg")[0]));
		// only allowed in trig
		assertTrue(filter.isAllowed(evaluate("sin(a)")[0]));
	}
}
