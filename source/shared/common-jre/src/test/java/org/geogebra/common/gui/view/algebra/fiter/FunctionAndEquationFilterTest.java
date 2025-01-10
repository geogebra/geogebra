package org.geogebra.common.gui.view.algebra.fiter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class FunctionAndEquationFilterTest extends BaseUnitTest {

    private final FunctionAndEquationFilter filter = new FunctionAndEquationFilter();

    @Test
    public void testIsAllowed() {
        GeoElement f = addAvInput("f(x) = x");
        assertThat(filter.isAllowed(f), is(true));
    }

    @Test
    public void testRemoveUndefinedIsAllowedCommandWithinFunction() {
        GeoElement poly = addAvInput("FitPoly(RemoveUndefined({(1,2),(3,4),(2,1)}),2)");
        assertThat(filter.isAllowed(poly), is(true));
    }
}