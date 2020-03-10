package org.geogebra.common.kernel.commands;

import com.sun.tools.javac.util.Assert;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoLine;
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
        Assert.checkNonNull(element);
    }
}
