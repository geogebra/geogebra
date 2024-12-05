package org.geogebra.common.exam;

import static org.geogebra.common.exam.restrictions.CvteExamRestrictions.isVisibilityEnabled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Before;
import org.junit.Test;

public final class CvteExamTests extends BaseExamTests {
    @Before
    public void setupCvteExam() {
        setInitialApp(SuiteSubApp.GRAPHING);
        examController.startExam(ExamType.CVTE, null);
    }

    @Test
    public void testMatrixOutputRestrictions() {
        evaluate("l1={1,2}");
        evaluate("l2={1,2}");

        assertNull(evaluate("{l1, l2}"));
        assertNull(evaluate("{If(true, l1)}"));
        assertNull(evaluate("{IterationList(x^2,3,2)}"));
        assertNull(evaluate("{Sequence(k,k,1,3)}"));
    }

    @Test
    public void testSyntaxRestrictions() {
        evaluate("A=(1,1)");
        evaluate("B=(2,2)");

        errorAccumulator.resetError();
        assertNull(evaluate("Circle(A, B)"));
        assertThat(errorAccumulator.getErrorsSinceReset(),
                containsString("Illegal argument: Point B"));

        errorAccumulator.resetError();
        assertNotNull(evaluate("Circle(A, 1)"));
        assertEquals("", errorAccumulator.getErrorsSinceReset());
    }

    @Test
    public void testToolRestrictions() {
        assertTrue(app.getAvailableTools().contains(EuclidianConstants.MODE_MOVE));
        assertFalse(app.getAvailableTools().contains(EuclidianConstants.MODE_POINT));
        assertTrue(commandDispatcher.isAllowedByCommandFilters(Commands.Curve));
        assertTrue(commandDispatcher.isAllowedByCommandFilters(Commands.CurveCartesian));
    }

    @Test
    public void testUnrestrictedVisibility() {
        // Enabled conics
        assertTrue(isVisibilityEnabled(evaluateGeoElement("Circle((0, 0), 2)")));

        // Enabled equations
        assertTrue(isVisibilityEnabled(evaluateGeoElement("x = 0")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("y = 5")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("x + y = 0")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("x = y")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("2x - 3y = 4")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("2x = y")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("y = 2x")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("y = x^2")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("y = x^3")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("y = x^2 - 5x + 2")));

        // Other enabled inputs
        assertTrue(isVisibilityEnabled(evaluateGeoElement("x")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("f(x) = x^2")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("x^2")));
        assertTrue(isVisibilityEnabled(evaluateGeoElement("A = (1, 2)")));
    }

    @Test
    public void testRestrictedVisibility() {
        // Restricted conics
        assertFalse(isVisibilityEnabled(evaluateGeoElement("x^2 + y^2 = 4")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("x^2 / 9 + y^2 / 4 = 1")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("x^2 - y^2 = 4")));

        // Restricted equations
        assertFalse(isVisibilityEnabled(evaluateGeoElement("x^2 = 0")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("x^2 = 1")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("2^x = 0")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("sin(x) = 0")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("ln(x) = 0")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("|x - 3| = 0")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("y - x^2 = 0")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("x^2 = y")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("x^3 = y")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("y^2 = x")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("x^3 + y^2 = 2")));
        assertFalse(isVisibilityEnabled(evaluateGeoElement("y^3 = x")));
    }
    
    @Test
    public void testRestrictedVisibilityInEuclidianView() {
        GeoElement allowedGeoElement = evaluateGeoElement("x = 0");
        assertTrue(isVisibilityEnabled(allowedGeoElement));

        GeoElement restrictedGeoElement = evaluateGeoElement("x^2 = 0");
        assertFalse(isVisibilityEnabled(restrictedGeoElement));

        assertTrue(allowedGeoElement.isEuclidianVisible());
        assertTrue(allowedGeoElement.isEuclidianToggleable());
        assertNotNull(geoElementPropertiesFactory.createShowObjectProperty(
                app.getLocalization(), List.of(allowedGeoElement)));

        assertFalse(restrictedGeoElement.isEuclidianVisible());
        assertFalse(restrictedGeoElement.isEuclidianToggleable());
        assertNull(geoElementPropertiesFactory.createShowObjectProperty(
                app.getLocalization(), List.of(restrictedGeoElement)));
    }
}
