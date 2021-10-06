package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TrigonometricCompositeTest {

	@Test
	public void CosCotXShouldBeOneAtKTimesPiHalf() {
		// cos(cot(k * pi + pi/2))
		assertEquals(one(), withPiPeriod(Math.PI / 2,-2).cot().cos());
		assertEquals(one(), withPiPeriod(Math.PI / 2,-1).cot().cos());
		assertEquals(one(), withPiPeriod(Math.PI / 2,0).cot().cos());
		assertEquals(one(), withPiPeriod(Math.PI / 2,1).cot().cos());
		assertEquals(one(), withPiPeriod(Math.PI / 2,2).cot().cos());
		assertEquals(one(), withPiPeriod(Math.PI / 2,12010).cot().cos());

	}

	@Test
	public void CosCotXShouldBeOneAroundKTimesPiHalf() {
		// cos(cot(pi/2))
		assertEquals(one(), withPiPeriodAround(Math.PI / 2,0).cot().cos());
		assertEquals(one(), withPiPeriodAround(Math.PI / 2,1).cot().cos());
		assertEquals(one(), withPiPeriodAround(Math.PI / 2,2).cot().cos());
		assertEquals(one(), withPiPeriodAround(Math.PI / 2,12010).cot().cos());

	}

	private Interval withPiPeriod(double value, int k) {
		double period = Math.PI * k;
		return interval(period + value);
	}

	private Interval withPiPeriodAround(double value, int k) {
		double period = Math.PI * k;
		double x = period + value;
		return interval(x - 1E-6, x + 1E-6);
	}
}
