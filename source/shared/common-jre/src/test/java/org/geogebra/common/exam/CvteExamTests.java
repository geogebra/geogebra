package org.geogebra.common.exam;

import static org.geogebra.common.kernel.commands.Commands.Curve;
import static org.geogebra.common.kernel.commands.Commands.CurveCartesian;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.exam.restrictions.CvteExamRestrictions;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public final class CvteExamTests extends BaseExamTests {
    private static final Set<VisibilityRestriction> visibilityRestrictions =
            CvteExamRestrictions.createVisibilityRestrictions();

    @BeforeEach
    public void setupCvteExam() {
        setInitialApp(SuiteSubApp.GRAPHING);
        examController.startExam(ExamType.CVTE, null);
    }

    @Test
    public void testMatrixOutputRestrictions() {
        evaluate("l1={1,2}");
        evaluate("l2={1,2}");

        assertAll(
                () -> assertNull(evaluate("{l1, l2}")),
                () -> assertNull(evaluate("{If(true, l1)}")),
                () -> assertNull(evaluate("{IterationList(x^2,3,2)}")),
                () -> assertNull(evaluate("{Sequence(k,k,1,3)}")));
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
        assertAll(
                () -> assertTrue(app.getAvailableTools().contains(EuclidianConstants.MODE_MOVE)),
                () -> assertFalse(app.getAvailableTools().contains(EuclidianConstants.MODE_POINT)),
                () -> assertTrue(commandDispatcher.isAllowedByCommandFilters(Curve)),
                () -> assertTrue(commandDispatcher.isAllowedByCommandFilters(CurveCartesian)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Enabled conics
            "Circle((0, 0), 2)",
            // Enabled equations
            "x = 0",
            "y = 5",
            "x + y = 0",
            "x = y",
            "2x - 3y = 4",
            "2x = y",
            "y = 2x",
            "y = x^2",
            "y = x^3",
            "y = x^2 - 5x + 2",
            // Other enabled inputs
            "x",
            "f(x) = x^2",
            "x^2",
            "A = (1, 2)",
    })
    public void testUnrestrictedVisibility(String expression) {
        assertFalse(VisibilityRestriction.isVisibilityRestricted(evaluateGeoElement(expression),
                visibilityRestrictions));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // Restricted conics
            "x^2 + y^2 = 4",
            "x^2 / 9 + y^2 / 4 = 1",
            "x^2 - y^2 = 4",
            // Restricted equations
            "x^2 = 0",
            "x^2 = 1",
            "2^x = 0",
            "sin(x) = 0",
            "ln(x) = 0",
            "|x - 3| = 0",
            "y - x^2 = 0",
            "x^2 = y",
            "x^3 = y",
            "y^2 = x",
            "x^3 + y^2 = 2",
            "y^3 = x",
            // Restricted inequalities
            "x > 0",
            "y <= 1",
            "x < y",
            "x - y > 2",
            "x^2 + 2y^2 < 1",
            "f: x > 0",
            "f(x) = x > 2",
            // Restricted vectors
            "a = (1, 2)",
            "b = (1, 2) + 0"
    })
    public void testRestrictedVisibility(String expression) {
        assertTrue(VisibilityRestriction.isVisibilityRestricted(evaluateGeoElement(expression),
                visibilityRestrictions));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a + 2",
            "a - 5",
            "a - b",
            "a + b",
            "a * 2"
    })
    public void testAllowedVectorOperations(String expression) {
        assertNotNull(evaluate("a = (1, 2)"));
        assertNotNull(evaluate("b = (3, 4)"));
        assertNotNull(evaluate(expression));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a * b",
            "a ⊗ b",
            "a * (1, 2)",
            "(1, 2) ⊗ a",
            "(1, 2) * (1, 2)",
            "(3, 4) ⊗ (5, 6)"
    })
    public void testRestrictedVectorOperations(String expression) {
        assertNotNull(evaluate("a = (1, 2)"));
        assertNotNull(evaluate("b = (3, 4)"));
        assertNull(evaluate(expression));
    }

    @Issue("APPS-5919")
    @Test
    public void testAbsRestrictions() {
        // points
        assertNotNull(evaluate("A = (1, 2)"));
        assertNotNull(evaluate("B = (3, 4)"));
        assertNull(evaluate("abs(A-B)"));
        assertNull(evaluate("a=abs(A^2)"));
        // vectors
        assertNotNull(evaluate("u = (1, 2)"));
        assertNotNull(evaluate("v = (3, 4)"));
        assertNull(evaluate("abs(u-v)"));
        assertNull(evaluate("|u^2|"));
        assertNull(evaluate("|u v|"));
        // complex numbers
        assertNull(evaluate("|1+i|"));

        // allowed:
        assertNotNull(evaluate("abs(1-4)"));
        assertNotNull(evaluate("y=|x|"));
    }

    @Test
    public void testIntersectCommandWithRestrictedObjects() {
        // A line, a circle and 2 parabolas, all intersecting.
        // The visibility of 'h' and 'i' are restricted.
        assertFalse(VisibilityRestriction.isVisibilityRestricted(
                evaluateGeoElement("f(x) = x + 3"),
                visibilityRestrictions));
        assertFalse(VisibilityRestriction.isVisibilityRestricted(
                evaluateGeoElement("g(x) = x^2"),
                visibilityRestrictions));
        assertTrue(VisibilityRestriction.isVisibilityRestricted(
                evaluateGeoElement("h: (x + 1)^2 = y"),
                visibilityRestrictions));
        assertTrue(VisibilityRestriction.isVisibilityRestricted(
                evaluateGeoElement("i: x^2 + y^2 = 5"),
                visibilityRestrictions));

        // Intersection of any 2 unrestricted objects is allowed.
        assertNotNull(evaluate("Intersect(f, g)"));
        // Intersection of 2 objects where at least one of them is restricted is not allowed.
        assertNull(evaluate("Intersect(f, h)"));
        assertNull(evaluate("Intersect(g, h"));
        assertNull(evaluate("Intersect(h, g)"));
        assertNull(evaluate("Intersect(h, c)"));

        // Intersect command with wrong number of arguments are not allowed
        errorAccumulator.resetError();
        assertNull(evaluate("Intersect(f)"));
        assertTrue(errorAccumulator.getErrorsSinceReset()
                .contains("Illegal number of arguments: 1"));
        errorAccumulator.resetError();
        assertNull(evaluate("Intersect(f, g, h, i, f)"));
        assertTrue(errorAccumulator.getErrorsSinceReset()
                .contains("Illegal number of arguments: 5"));
    }

    @Test
    public void testEquationForm() {
        GeoElement line = evaluateGeoElement("y = 4");
        assertEquals(LinearEquationRepresentable.Form.USER,
                ((LinearEquationRepresentable) line).getEquationForm());
        GeoElement parabola = evaluateGeoElement("y = x^2");
        assertEquals(QuadraticEquationRepresentable.Form.USER,
                ((QuadraticEquationRepresentable) parabola).getEquationForm());
        GeoElement circle = evaluateGeoElement("x^2 + y^2 = 4");
        assertEquals(QuadraticEquationRepresentable.Form.USER,
                ((QuadraticEquationRepresentable) circle).getEquationForm());
        GeoElement implicitCurve = evaluateGeoElement("x^3 = y^3");
        assertEquals(GeoImplicit.Form.USER,
                ((GeoImplicitCurve) implicitCurve).getEquationForm());
    }

    @Test
    public void testEquationFormPropertyFrozen() {
        GeoElement line = evaluateGeoElement("y = 4");
        PropertiesArray properties = geoElementPropertiesFactory
                .createGeoElementProperties(algebraProcessor, app.getLocalization(), List.of(line));
        LinearEquationFormProperty equationFormProperty = null;
        for (Property property : properties.getProperties()) {
            if (property instanceof NamedEnumeratedPropertyCollection<?, ?>) {
                Property firstProperty = ((NamedEnumeratedPropertyCollection) property)
                        .getProperties()[0];
                if (firstProperty instanceof LinearEquationFormProperty) {
                    equationFormProperty = (LinearEquationFormProperty) firstProperty;
                    break;
                }
            }
        }
        assertNotNull(equationFormProperty);
        assertTrue(equationFormProperty.isFrozen());
    }

    @Test
    public void testSurdsAreDisabled() {
        GeoElement element = evaluateGeoElement("sqrt(8)");
        assertFalse(AlgebraItem.shouldShowSymbolicOutputButton(element));
    }

    @Test
    public void testRationalizationIsDisabled() {
        GeoElement element = evaluateGeoElement("1/sqrt(8)");
        assertFalse(AlgebraItem.shouldShowSymbolicOutputButton(element));
    }
}
