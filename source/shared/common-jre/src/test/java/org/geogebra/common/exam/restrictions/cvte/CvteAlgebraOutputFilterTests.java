package org.geogebra.common.exam.restrictions.cvte;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTests;
import org.geogebra.common.gui.view.algebra.fiter.AlgebraOutputFilter;
import org.junit.Before;
import org.junit.Test;

public class CvteAlgebraOutputFilterTests extends BaseExamTests {

    @Before
    public void setup() {
        setInitialApp(SuiteSubApp.GRAPHING);
    }

    @Test
    public void testAlgebraOutputRestrictions() {
        AlgebraOutputFilter outputFilter = new CvteAlgebraOutputFilter(null);

        // For Lines, Rays, Conics, Implicit Equations and Functions created with command or tool,
        // we do not show the calculated equation.
        assertFalse(outputFilter.isAllowed(evaluateGeoElement("Line((0, 0), (1, 2))")));
        assertFalse(outputFilter.isAllowed(evaluateGeoElement("Ray((0, 0), (1, 2))")));
        assertFalse(outputFilter.isAllowed(evaluateGeoElement("Circle((0, 0), 1)")));
        // implicit curve
        assertFalse(outputFilter.isAllowed(evaluateGeoElement(
                "FitImplicit((1...10,(1/(1...10))),3)")));
        // functions: any of the FitPoly / FitLog / ... commands
        assertFalse(outputFilter.isAllowed(evaluateGeoElement(
                "f(x)=FitPoly({(-2,1),(-1,0),(0,1),(1,0)},3)")));

        //  Lines, Rays, Conics, Implicit Equations and Functions created from manual input
        // line
        assertTrue(outputFilter.isAllowed(evaluateGeoElement("x = y")));
        // conic
        assertTrue(outputFilter.isAllowed(evaluateGeoElement("x^2 + y^2 = 4")));
        // implicit equation
        assertTrue(outputFilter.isAllowed(evaluateGeoElement("x^3 + y = 0")));
        // function
        assertTrue(outputFilter.isAllowed(evaluateGeoElement("x")));
    }
}
