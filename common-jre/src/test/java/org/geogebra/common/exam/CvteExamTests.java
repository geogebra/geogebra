package org.geogebra.common.exam;

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
    public void testConicRestrictions() {
        // Check visible elements
        List.of("Circle((0, 0), 2)",
                "x"
        ).forEach(expression -> {
            GeoElement geoElement = evaluateGeoElement(expression);
            assertTrue(expression + " should be visible in the euclidian view.",
                    geoElement.isEuclidianVisible());
            assertTrue(expression + " should be toggleable in the algebra input.",
                    geoElement.isEuclidianToggleable());
            assertNotNull(expression + " should have a show object property.",
                    geoElementPropertiesFactory.createShowObjectProperty(
                            app.getLocalization(), List.of(geoElement)));
        });

        // Check elements with restricted visibility
        List.of("x^2 + y^2 = 4"
        ).forEach(expression -> {
            GeoElement geoElement = evaluateGeoElement(expression);
            assertFalse(expression + " shouldn't be visible in the euclidian view.",
                    geoElement.isEuclidianVisible());
            assertFalse(expression + " shouldn't be toggleable in the algebra input.",
                    geoElement.isEuclidianToggleable());
            assertNull(expression + " shouldn't have a show object property.",
                    geoElementPropertiesFactory.createShowObjectProperty(
                            app.getLocalization(), List.of(geoElement)));
        });
    }

    @Test
    public void testEquationRestrictions() {
        // Check linear equations
        List.of("x = 0",
                "x + y = 0",
                "2x - 3y = 4",
                "y = 2x"
        ).forEach(expression -> {
            GeoElement geoElement = evaluateGeoElement(expression);
            assertTrue(expression + " should be visible in the euclidian view.",
                    geoElement.isEuclidianVisible());
            assertTrue(expression + " should be toggleable in the algebra input.",
                    geoElement.isEuclidianToggleable());
            assertNotNull(expression + " should have a show object property.",
                    geoElementPropertiesFactory.createShowObjectProperty(
                            app.getLocalization(), List.of(geoElement)));
        });

        // Check non-linear equations
        List.of("x^2 = 0",
                "2^x = 0",
                "sin(x) = 0",
                "ln(x) = 0",
                "|x - 3| = 0"
        ).forEach(expression -> {
            GeoElement geoElement = evaluateGeoElement(expression);
            assertFalse(expression + " shouldn't be visible in the euclidian view.",
                    geoElement.isEuclidianVisible());
            assertFalse(expression + " shouldn't be toggleable in the algebra input.",
                    geoElement.isEuclidianToggleable());
            assertNull(expression + " shouldn't have a show object property.",
                    geoElementPropertiesFactory.createShowObjectProperty(
                            app.getLocalization(), List.of(geoElement)));
        });
    }

    @Test
    public void testNonLinearEquations() {
        List.of("f(x) = x^2",
                "x^2",
                "y = x^2",
                "y = x^3"
        ).forEach(expression -> {
            GeoElement geoElement = evaluateGeoElement(expression);
            assertTrue(expression + " should be visible in the euclidian view.",
                    geoElement.isEuclidianVisible());
            assertTrue(expression + " should be toggleable in the algebra input.",
                    geoElement.isEuclidianToggleable());
            assertNotNull(expression + " should have a show object property.",
                    geoElementPropertiesFactory.createShowObjectProperty(
                            app.getLocalization(), List.of(geoElement)));
        });
    }

    @Test
    public void testGraphicalOutputRestrictions() {
        List.of("Circle((1,1), 2)",
                "x",
                "x = 0",
                "x + y = 0",
                "2 * x - 3y = 4",
                "y = 2x",
                "f(x) = x^2",
                "x^2",
                "y = x^2",
                "y = x^3",
                "A = (1, 2)"
        ).forEach(expression -> {
            GeoElement geoElement = evaluateGeoElement(expression);
            assertTrue(expression + " should be visible in the euclidian view.",
                    geoElement.isEuclidianVisible());
            assertTrue(expression + " should be toggleable in the algebra input.",
                    geoElement.isEuclidianToggleable());
            assertNotNull(expression + " should have a show object property.",
                    geoElementPropertiesFactory.createShowObjectProperty(
                            app.getLocalization(), List.of(geoElement)));
        });

        List.of("x^2 = 1",
                "2^x = 2",
                "sin(x) = 0",
                "ln(x) = 0",
                "|x - 3| = 0",
                //"y - x^2 = 0",
                //"x^2 = y",
                "x^3 = y",
                "y^2 = x",
                "x^2 + y^2 = 4",
                "x^2 / 9 + y^2 / 4 = 1",
                "x^2 - y^2 = 4",
                "x^3 + y^2 = 2",
                "y^3 = x",
                "v = (1, 2)",
                "x < -1",
                "y < -1",
                "y > x",
                "x - y > 1",
                "x^2 + 2y^2 < 1"
        ).forEach(expression -> {
            GeoElement geoElement = evaluateGeoElement(expression);
            assertFalse(expression + " shouldn't be visible in the euclidian view.",
                    geoElement.isEuclidianVisible());
            assertFalse(expression + " shouldn't be toggleable in the algebra input.",
                    geoElement.isEuclidianToggleable());
            assertNull(expression + " shouldn't have a show object property.",
                    geoElementPropertiesFactory.createShowObjectProperty(
                            app.getLocalization(), List.of(geoElement)));
        });
    }
}
