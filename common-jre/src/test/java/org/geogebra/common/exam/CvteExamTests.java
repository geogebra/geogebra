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
        assertNull(evaluate("{If(true, l1}}"));
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
        GeoElement circleCreatedByCommand = evaluateGeoElement("Circle((0, 0), 2)");
        GeoElement linearFunction = evaluateGeoElement("x");
        GeoElement quadraticFunction = evaluateGeoElement("x^2");
        GeoElement circleCreatedManually = evaluateGeoElement("x^2 + y^2 = 4");

        assertTrue(circleCreatedByCommand.isEuclidianVisible());
        assertTrue(linearFunction.isEuclidianVisible());
        assertFalse(circleCreatedManually.isEuclidianVisible());
        assertFalse(quadraticFunction.isEuclidianVisible());

        assertTrue(circleCreatedByCommand.isEuclidianToggleable());
        assertTrue(linearFunction.isEuclidianToggleable());
        assertFalse(circleCreatedManually.isEuclidianToggleable());
        assertFalse(quadraticFunction.isEuclidianToggleable());

        assertNotNull(geoElementPropertiesFactory.createShowObjectProperty(
                app.getLocalization(), List.of(circleCreatedByCommand)));
        assertNotNull(geoElementPropertiesFactory.createShowObjectProperty(
                app.getLocalization(), List.of(linearFunction)));
        assertNull(geoElementPropertiesFactory.createShowObjectProperty(
                app.getLocalization(), List.of(circleCreatedManually)));
        assertNull(geoElementPropertiesFactory.createShowObjectProperty(
                app.getLocalization(), List.of(quadraticFunction)));
    }

    @Test
    public void testEquationRestrictions() {
        List.of(evaluateGeoElement("x = 0"),
                evaluateGeoElement("x + y = 0")
        ).forEach(linearEquation -> {
            assertTrue(linearEquation.isEuclidianVisible());
            assertNotNull(geoElementPropertiesFactory.createShowObjectProperty(
                    app.getLocalization(), List.of(linearEquation)));
        });

        List.of(evaluateGeoElement("x^2 = 0"),
                evaluateGeoElement("2^x = 0"),
                evaluateGeoElement("sin(x) = 0"),
                evaluateGeoElement("ln(x) = 0"),
                evaluateGeoElement("|x - 3| = 0")
        ).forEach(nonLinearEquation -> {
            assertFalse(nonLinearEquation.isEuclidianVisible());
            assertNull(geoElementPropertiesFactory.createShowObjectProperty(
                    app.getLocalization(), List.of(nonLinearEquation)));
        });
    }
}
