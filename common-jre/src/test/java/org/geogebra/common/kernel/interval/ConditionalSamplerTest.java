package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class ConditionalSamplerTest extends BaseUnitTest {

	@Test
	public void testIf() {
		GeoFunction function = add("a=If(x < 0, 1, -1)");
		MyNumberPair pair = (MyNumberPair) Objects.requireNonNull(
				function.getFunctionExpression()).getLeft();
		DiscreteSpace discreteSpace = new DiscreteSpaceImp(interval(-10, 10), 100);
		ConditionalSampler sampler = new ConditionalSampler(pair.getX().wrap(),
				pair.getY().wrap(), discreteSpace);
		sampler.evaluate();
		IntervalTupleList tuples = sampler.result();
		assertEquals(tuples.count(),
				tuples.stream().filter(tuple -> tuple.y().almostEqual(one())).count());

	}

	@Test
	public void testIfOnInterval() {
		GeoFunction function = add("a=If(x < 0, 1, -1)");
		MyNumberPair pair = (MyNumberPair) Objects.requireNonNull(
				function.getFunctionExpression()).getLeft();
		DiscreteSpace discreteSpace = new DiscreteSpaceImp(interval(-10, 10), 100);
		ConditionalSampler sampler = new ConditionalSampler(pair.getX().wrap(),
				pair.getY().wrap(), discreteSpace);
		IntervalTupleList tuples = sampler.evaluateOn(interval(-2, -1));

		assertEquals(5,
				tuples.stream().filter(tuple -> tuple.y().almostEqual(one())).count());

	}

	@Test
	public void testIfNegated() {
		GeoFunction function = add("a=If(x < 0, 1, -1)");
		MyNumberPair pair = (MyNumberPair) Objects.requireNonNull(
				function.getFunctionExpression()).getLeft();
		DiscreteSpace discreteSpace = new DiscreteSpaceImp(interval(-10, 10), 100);
		ConditionalSampler sampler = ConditionalSampler.createNegated(function, pair.getX().wrap(),
				pair.getY().wrap(), discreteSpace);
		sampler.evaluate();
		IntervalTupleList tuples = sampler.result();
		assertEquals(tuples.count(),
				tuples.stream().filter(tuple -> tuple.y().almostEqual(one())).count());
	}

	@Test
	public void testSignumElse() {
		GeoFunction function = add("a=If(x < 0, 1, -1)");
		MyNumberPair pair = (MyNumberPair) Objects.requireNonNull(
				function.getFunctionExpression()).getLeft();
		DiscreteSpace discreteSpace = new DiscreteSpaceImp(interval(-10, 10), 100);
		ConditionalSampler sampler = ConditionalSampler.createNegated(function, pair.getX().wrap(),
				pair.getY().wrap(), discreteSpace);

		sampler.evaluate();

		IntervalTupleList tuples = sampler.result();
		assertEquals(tuples.count(),
				tuples.stream().filter(tuple -> tuple.y().almostEqual(interval(-1))).count());

	}

	@Test
	public void testCompoundCondition() {
		GeoFunction function = add("a=If(0 < x < 5, 1)");
		ExpressionNode node = function.getFunctionExpression();
		ConditionalSampler sampler =
				new ConditionalSampler(node.getLeftTree(), node.getRightTree(), null);
		assertTrue(sampler.isConditionTrue(interval(1, 3)));
	}
	@Test
	public void testLessThanOrEqual() {
		GeoFunction function = add("a=If(x <= 0, 1)");
		ExpressionNode node = function.getFunctionExpression();
		DiscreteSpace space = new DiscreteSpaceImp(interval(-10, 10), 100);
		ConditionalSampler sampler =
				new ConditionalSampler(node.getLeftTree(), node.getRightTree(), space);
		IntervalTupleList tuples = sampler.evaluateOn(interval(-2, 0));
		assertEquals(tuples.count(),
				tuples.stream().filter(tuple -> tuple.y().almostEqual(interval(1))).count());
		tuples = sampler.evaluateOn(interval(1, 2));
		assertEquals(0,
				tuples.stream().filter(tuple -> tuple.y().almostEqual(interval(1))).count());
	}

	@Test
	public void testGreaterThanOrEqual() {
		GeoFunction function = add("a=If(x >= 0, 1)");
		ExpressionNode node = function.getFunctionExpression();
		DiscreteSpace space = new DiscreteSpaceImp(interval(-10, 10), 100);
		ConditionalSampler sampler =
				new ConditionalSampler(node.getLeftTree(), node.getRightTree(), space);
		IntervalTupleList tuples = sampler.evaluateOn(interval(-10, 0.002));
		assertEquals(0,
				tuples.stream().filter(tuple -> tuple.y().almostEqual(interval(1))).count());
		tuples = sampler.evaluateOn(interval(0, 10));
		assertEquals(49,
				tuples.stream().filter(tuple -> tuple.y().almostEqual(interval(1))).count());
	}
	@Test
	public void testEqual() {
		GeoFunction function = add("a=If(x == 0, 1)");
		ExpressionNode node = function.getFunctionExpression();
		DiscreteSpace space = new DiscreteSpaceImp(interval(-10, 10), 100);
		ConditionalSampler sampler =
				new ConditionalSampler(node.getLeftTree(), node.getRightTree(), space);
		IntervalTupleList tuples = sampler.evaluateOn(interval(-10, -0 - 1E-15));
		assertEquals(0,
				tuples.stream().filter(tuple -> tuple.y().almostEqual(interval(1))).count());

		double step = space.getStep();
		tuples = sampler.evaluateOn(interval(0 - step/2, 0 + step/2));
		assertEquals(1,
				tuples.stream().filter(tuple -> tuple.y().almostEqual(interval(1))).count());
	}
}