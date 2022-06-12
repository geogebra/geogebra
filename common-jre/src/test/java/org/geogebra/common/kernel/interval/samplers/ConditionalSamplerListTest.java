package org.geogebra.common.kernel.interval.samplers;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalTest;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.Test;

public class ConditionalSamplerListTest extends BaseUnitTest {

	private ConditionalSamplerList samplers;

	class SampledData {
		Interval x;
		Interval expected;

		public SampledData(Interval x) {
			this.x = x;
		}

		public SampledData() {
		}

		SampledData shouldEqual(Interval value) {
			expected = value;
			return this;
		}

		Interval evaluate() {
			IntervalTupleList list = samplers.evaluate(x);
			return list.count() > 0 ? list.get(0).y() : undefined();
		}

		void checkThat(SampledData... sampledData) {
			for (SampledData data: sampledData) {
				assertEquals(data.expected, data.evaluate());
			}
		}
	}

	@Test
	public void testIf() {
		withSampler("if(x < 2, 1)")
				.checkThat(
					onInterval(-10, 1.9).shouldEqual(IntervalTest.interval(1)),
					onInterval(3, 9000).shouldEqual(undefined()));
	}

	private SampledData onInterval(double low, double high) {
		return new SampledData(new Interval(low, high));
	}

	@Test
	public void testIfElse() {
		withSampler("if(x < 2, 1, 3)")
				.checkThat(
						onInterval(-10, 1.9).shouldEqual(one()),
						onInterval(2, 3).shouldEqual(IntervalTest.interval(3)));
	}

	private SampledData withSampler(String command) {
		samplers = new ConditionalSamplerList(add(command), IntervalTest.interval(-10, 10), 100);
		return new SampledData();

	}

	@Test
	public void testIfShort() {
		withSampler("2, -2 < x < 2")
				.checkThat(onInterval(0, 1.9).shouldEqual(IntervalTest.interval(2)),
						onInterval(-1000, -3).shouldEqual(undefined()),
						onInterval(2.1, 1000).shouldEqual(undefined())
				);

	}

	@Test
	public void testIfList() {
		withSampler("if(x < -2, 0, -2 < x < 2, 1, x > 2, 2)")
				.checkThat(
						onInterval(-100, -2.1).shouldEqual(zero()),
						onInterval(-1.99, 1.99).shouldEqual(one()),
						onInterval(2, 20000).shouldEqual(IntervalTest.interval(2))
				);
	}

	@Test
	public void testIfListWithOverlappingConditions() {
		withSampler("if(x < 2, 0, -2 < x < 2, 1, x > 2, 2)")
				.checkThat(onInterval(-10, -1.9).shouldEqual(zero()),
						onInterval(-11, 1.9).shouldEqual(zero()),
						onInterval(2, 3000).shouldEqual(IntervalTest.interval(2))
				);
	}
}