package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoLine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GraphingCommandArgumentFilterTest extends BaseUnitTest {

    @Before
    public void setUp() {
        getApp().setGraphingConfig();
    }

    @Test(expected = NullPointerException.class)
    public void testParallelLineWithPointAndLineIsFiltered() {
       addAvInput("A = (1,1)");
       addAvInput("B = (2,2)");
       addAvInput("C = (3,2)");
       addAvInput("f:Line(B,C)");
       addAvInput("g:Line(A,f)");
    }

    @Test(expected = NullPointerException.class)
    public void testParallelLineWithPointAndFunctionIsFiltered() {
        addAvInput("A = (1,2)");
        addAvInput("f(x) = x");
        addAvInput("g:Line(A,f)");
    }

    @Test
    public void testLineWithTwoPointsAllowed() {
        addAvInput("A = (1,2)");
        addAvInput("B = (3,4)");
        GeoLine element = addAvInput("g: Line(A, B)");
        Assert.assertNotNull(element);
    }

    @Test
    public void testLengthOfListAllowed() {
        addAvInput("L = {(0,0), (1,1), (2,2)}");
        addAvInput("Length(L)");
    }

    @Test
    public void testLengthOfTextAllowed() {
        addAvInput("text = Text(\"1234\")");
        addAvInput("Length(text)");
    }

    @Test(expected = NullPointerException.class)
    public void testLengthOfLocusAllowed() {
        addAvInput("f(x) = x^2 - 2x - 1");
        addAvInput("A = (0,-1)");
        addAvInput("Locus = (x(A), f'(x(A)))");
        addAvInput("Length(Locus)");
    }

    @Test(expected = NullPointerException.class)
    public void testLengthOfVectorIsFiltered() {
        addAvInput("vector = (1,2)");
        addAvInput("Length(vector)");
    }

    @Test(expected = NullPointerException.class)
    public void testLengthFunctionStartXValueEndXValueIsFiltered() {
        addAvInput("a = Length(2 x, 0, 1)");
    }

    @Test(expected = NullPointerException.class)
    public void testLengthFunctionStartPointEndPointIsFiltered() {
        addAvInput("a = Length(2 x, (0,0), (1,1))");
    }

    @Test(expected = NullPointerException.class)
    public void testLengthCurveStartTValueEndTValueIsFiltered() {
        addAvInput("curve = Curve(2 cos(t), 2 sin(t), t, 0, 2π)");
        addAvInput("Length(curve, 1, 7)");
    }

    @Test(expected = NullPointerException.class)
    public void testLengthCurveStartPointEndPointIsFiltered() {
        addAvInput("curve = Curve(2 cos(t), 2 sin(t), t, 0, 2π)");
        addAvInput("Length(curve, (2,0), (0,-2))");
    }
}
