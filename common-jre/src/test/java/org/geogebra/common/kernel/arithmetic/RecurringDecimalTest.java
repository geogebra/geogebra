package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class RecurringDecimalTest extends BaseUnitTest {
	@Test
	public void testToFraction() {
		shouldBeAsFraction(3.25444444444, "3.254\u0305", "2929/900");
		shouldBeAsFraction(0.33333333333, "0.3\u0305", "3/9");
		shouldBeAsFraction(1.3333333333333333, "1.3\u0305", "12/9");
		shouldBeAsFraction(0.51251251251, "0.5\u03051\u03052\u0305", "512/999");
	}
	public void shouldBeAsFraction(double val, String representation, String fraction) {
		RecurringDecimal recurringDecimal = new RecurringDecimal(getKernel(),
				val, representation);
		assertThat(recurringDecimal.toFractionSting(), is(fraction));
	}

	@Test
	public void testNominator() {
		assertThat(RecurringDecimal.nominator(3, 25, 4), is(2929));
		assertThat(RecurringDecimal.nominator(0, 0, 512), is(512));
		assertThat(RecurringDecimal.nominator(1, 0, 91), is(1081));
		assertThat(RecurringDecimal.nominator(1, null, 3), is(12));
		assertThat(RecurringDecimal.nominator(0, 3, 789), is(3786));
	}
		@Test
	public void testDenominator() {
		assertThat(RecurringDecimal.denominator(1, 0), is(9));
		assertThat(RecurringDecimal.denominator(2, 0), is(99));
		assertThat(RecurringDecimal.denominator(5, 0), is(99999));
		assertThat(RecurringDecimal.denominator(1, 1), is(90));
		assertThat(RecurringDecimal.denominator(1, 2), is(900));
		assertThat(RecurringDecimal.denominator(1, 5), is(900000));
		assertThat(RecurringDecimal.denominator(2, 5), is(9900000));
		assertThat(RecurringDecimal.denominator(0, 3), is(1000));
	}
}
