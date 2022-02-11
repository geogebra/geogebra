package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class GraphingCommandArgumentFilterTest extends BaseUnitTest {

	@Before
	public void setUp() {
		getApp().setGraphingConfig();
	}

	@Test
	public void testParallelLineWithPointAndLineIsFiltered() {
		addAvInput("A = (1,1)");
		addAvInput("B = (2,2)");
		addAvInput("C = (3,2)");
		addAvInput("f:Line(B,C)");
		assertThat(addAvInput("g:Line(A,f)"), is(nullValue()));
	}

	@Test
	public void testParallelLineWithPointAndFunctionIsFiltered() {
		addAvInput("A = (1,2)");
		addAvInput("f(x) = x");
		assertThat(addAvInput("g:Line(A,f)"), is(nullValue()));
	}

	@Test
	public void testLineWithTwoPointsAllowed() {
		addAvInput("A = (1,2)");
		addAvInput("B = (3,4)");
		assertThat(addAvInput("g: Line(A, B)"), is(notNullValue()));
	}

	@Test
	public void testLengthOfListAllowed() {
		addAvInput("L = {(0,0), (1,1), (2,2)}");
		assertThat(addAvInput("Length(L)"), is(notNullValue()));
	}

	@Test
	public void testLengthOfTextAllowed() {
		addAvInput("text = Text(\"1234\")");
		assertThat(addAvInput("Length(text)"), is(notNullValue()));
	}

	@Test
	public void testLengthOfVectorIsFiltered() {
		addAvInput("vector = (1,2)");
		assertThat(addAvInput("Length(vector)"), is(nullValue()));
	}

	@Test
	public void testLengthFunctionStartXValueEndXValueIsFiltered() {
		assertThat(addAvInput("a = Length(2 x, 0, 1)"), is(nullValue()));
	}

	@Test
	public void testLengthFunctionStartPointEndPointIsFiltered() {
		assertThat(addAvInput("a = Length(2 x, (0,0), (1,1))"), is(nullValue()));
	}

	@Test
	public void testLengthCurveStartTValueEndTValueIsFiltered() {
		addAvInput("curve = Curve(2 cos(t), 2 sin(t), t, 0, 2π)");
		assertThat(addAvInput("Length(curve, 1, 7)"), is(nullValue()));
	}

	@Test
	public void testLengthCurveStartPointEndPointIsFiltered() {
		addAvInput("curve = Curve(2 cos(t), 2 sin(t), t, 0, 2π)");
		assertThat(addAvInput("Length(curve, (2,0), (0,-2))"), is(nullValue()));
	}

	@Test
	public void testPolylineWithPointsFiltered() {
		assertThat(addAvInput("Polyline((1, 3), (4, 3))"), is(nullValue()));
	}

	@Test
	public void testPolylineWithPointsAndBooleanAllowed() {
		assertThat(addAvInput("Polyline((1, 3), (4, 3), true)"), is(notNullValue()));
	}

	@Test
	public void testPolylineWithPointListAndBooleanAllowed() {
		assertThat(addAvInput("Polyline({(1, 3), (4, 3)}, true)"), is(notNullValue()));
	}
}
