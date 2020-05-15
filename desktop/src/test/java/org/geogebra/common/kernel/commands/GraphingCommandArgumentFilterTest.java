package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.junit.Assert;
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
       assertGeoIsNull("g:Line(A,f)");
    }

    @Test
    public void testParallelLineWithPointAndFunctionIsFiltered() {
        addAvInput("A = (1,2)");
        addAvInput("f(x) = x");
        assertGeoIsNull("g:Line(A,f)");
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

    @Test
    public void testLengthOfVectorIsFiltered() {
        addAvInput("vector = (1,2)");
        assertGeoIsNull("Length(vector)");
    }

    @Test
    public void testLengthFunctionStartXValueEndXValueIsFiltered() {
        assertGeoIsNull("a = Length(2 x, 0, 1)");
    }

    @Test
    public void testLengthFunctionStartPointEndPointIsFiltered() {
        assertGeoIsNull("a = Length(2 x, (0,0), (1,1))");
    }

    @Test
    public void testLengthCurveStartTValueEndTValueIsFiltered() {
        addAvInput("curve = Curve(2 cos(t), 2 sin(t), t, 0, 2π)");
        assertGeoIsNull("Length(curve, 1, 7)");
    }

    @Test
    public void testLengthCurveStartPointEndPointIsFiltered() {
        addAvInput("curve = Curve(2 cos(t), 2 sin(t), t, 0, 2π)");
        assertGeoIsNull("Length(curve, (2,0), (0,-2))");
    }

    private void assertGeoIsNull(String command) {
        GeoElement length = addAvInput(command);
        Assert.assertNull(length);
    }
}
