package org.geogebra.common.gui.view.algebra;

import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.APPROXIMATION;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.ENGINEERING;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.EXACT;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.FRACTION;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputOperator.APPROXIMATELY_EQUALS;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputOperator.EQUALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Set;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseAppTestSetup;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("checkstyle:RegexpSingleline") // Tabs in MockedCasValues
@ExtendWith(MockedCasValuesExtension.class)
public class AlgebraOutputFormatTests extends BaseAppTestSetup {
    @ParameterizedTest
    @ValueSource(strings = {
            "x^2",
            "1",
            "x = 0",
            "sqrt(4)",
            "2000",
            "-10",
    })
    @MockedCasValues({
            "Evaluate(x²) 		-> x^2",
            "Evaluate(1) 		-> 1",
            "Round(1, 13) 		-> 1.0",
            "Evaluate(x = 0) 	-> x=0",
            "Evaluate(sqrt(4)) 	-> 2",
            "Round(2, 13) 		-> 2.0",
            "Evaluate(2000) 	-> 2000",
            "Round(2000, 13) 	-> 2000.0",
            "Evaluate(-10) 		-> -10",
            "Round(-10, 13) 	-> -10.0",
    })
    public void testNoToggleButtonInCas(String expression) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluateGeoElement(expression), false, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "x^2",
            "1",
            "x = 0",
            "sqrt(4)",
            "sin(30°)",
            "1.23456789",
            "sin(1 / 2)",
            "e",
            "π",
            "sqrt(2)",
            "2000",
            "-10",
            "text1 = \"my text\"",
            "Take(\"hello\", 2, 3)",
    })
    public void testNoToggleButtonInGraphing(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(
                List.of(),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluateGeoElement(expression), false, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 / 2",
            "(5(2)/(3))",
            "2^(-1)",
            "sin(30°)",
            "1.234",
            "1.23456789",
    })
    @MockedCasValues({
            "Evaluate(1 / 2) 					-> 1/2",
            "Round(1 / 2, 13) 					-> 0.5",
            "Evaluate(5 * 2 / 3) 				-> 10/3",
            "Round(10 / 3, 13) 					-> 3.333333333333",
            "Evaluate(2⁻¹) 						-> 1/2",
            "Evaluate(sin(30°)) 				-> 1/2",
            "Evaluate(1.234) 					-> 617/500",
            "Round(617 / 500, 13) 				-> 1.234",
            "Evaluate(1.23456789) 				-> 123456789/100000000",
            "Round(123456789 / 100000000, 13) 	-> 1.23456789",
    })
    public void testFractionalOutputsInCas(String expression) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(FRACTION, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluateGeoElement(expression), false, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "(5(2)/(3))",
            "2^(-1)",
            "1.234",
            "1 / 2",
            "12 / 7",
    })
    public void testFractionalOutputsInGraphing(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(
                List.of(FRACTION, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluateGeoElement(expression), false, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "sin(1 / 2)",
            "sqrt(2)",
            "e",
            "π",
    })
    @MockedCasValues({
            "Evaluate(sin(1 / 2)) 	-> sin(1/2)",
            "Round(sin(1 / 2), 13) 	-> 0.4794255386042",
            "Evaluate(sqrt(2)) 		-> √2",
            "Round(sqrt(2), 13) 	-> 1.414213562373",
            "Evaluate(ℯ) 			-> ℯ",
            "Round(ℯ, 13) 			-> 2.718281828459",
            "Evaluate(π) 			-> pi",
            "Round(π, 13) 			-> 3.14159265359",
    })
    public void testNonFractionalDecimalOutputsInCas(String expression) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(EXACT, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluateGeoElement(expression), false, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-40000",
            "200",
            "5",
    })
    public void testIntegersInGraphingWithEngineeringNotation(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(
                List.of(EXACT, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluateGeoElement(expression), true, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1.234",
            "-1.234",
            "0.005",
    })
    public void testFractionalDecimalsInGraphingWithEngineeringNotation(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(
                List.of(FRACTION, APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluateGeoElement(expression), true, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1.23456789",
            "-1.23456789",
            "0.000000005",
    })
    public void testNonFractionalDecimalsInGraphingWithEngineeringNotation(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(
                List.of(EXACT, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluateGeoElement(expression), true, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Solve(x^2 = 1 / 2, x)",
            "Solve(x^2 = 2, x)",
    })
    @MockedCasValues({
            "Solve(x² = 1 / 2, x)   -> {x=(-√2/2),x=(√2/2)}",
            "NSolve(x² = 1 / 2, x)  -> {x=-0.7071067811865,x=0.7071067811865}",
            "Solve(x² = 2, x)       -> {x=(-√2),x=(√2)}",
            "NSolve(x² = 2, x)      -> {x=-1.414213562373,x=1.414213562373}"
    })
    public void testApproximationAndSymbolicOutputInSolveResult(String expression) {
		setupApp(SuiteSubApp.CAS);
		assertEquals(
				List.of(EXACT, APPROXIMATION),
				AlgebraOutputFormat.getPossibleFormats(
						evaluateGeoElement(expression), false, Set.of()));
	}

    @ParameterizedTest
    @ValueSource(strings = {
            "1 / sqrt(2)",
            "sqrt(3) / sqrt(2)",
    })
    public void testRationalizableFractions(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(
                List.of(EXACT, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(
						evaluateGeoElement(expression), false, Set.of()));
    }

    @Test
    @MockedCasValues({
            "Evaluate(sqrt(2)) 	-> √2",
            "Round(sqrt(2), 13) -> 1.414213562373",
    })
    public void testSwitchingBetweenApproximationAndEquals() {
        setupApp(SuiteSubApp.CAS);
        GeoElement geoElement = evaluateGeoElement("sqrt(2)");
        assertEquals(
                List.of(EXACT, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(geoElement, false, Set.of()));

        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(EXACT, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(EXACT, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
    }

    @Test
    public void testSwitchingBetweenFractionAndApproximationInGraphing() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1 / 2");
        assertEquals(
                List.of(FRACTION, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(geoElement, false, Set.of()));

        assertEquals(FRACTION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(FRACTION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
    }

    @Test
    @MockedCasValues({
            "Evaluate(1 / 2) 	-> 1/2",
            "Round(1 / 2, 13) 	-> 0.5",
    })
    public void testSwitchingBetweenFractionAndApproximationInCas() {
        setupApp(SuiteSubApp.CAS);
        GeoElement geoElement = evaluateGeoElement("1 / 2");
        assertEquals(
                List.of(FRACTION, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(geoElement, false, Set.of()));

        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(FRACTION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, false, Set.of());
        assertEquals(FRACTION, AlgebraOutputFormat.getNextFormat(geoElement, false, Set.of()));
    }

    @Test
    public void testSwitchingBetweenEqualsAndEngineering() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1.2345678");
        assertEquals(
                List.of(EXACT, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement, true, Set.of()));

        assertEquals(ENGINEERING, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(EXACT, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(ENGINEERING, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(EXACT, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
    }

    @Test
    public void testSwitchingBetweenFractionApproximationAndEngineering() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1.234");
        assertEquals(
                List.of(FRACTION, APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement, true, Set.of()));

        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(ENGINEERING, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(FRACTION, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(ENGINEERING, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(FRACTION, AlgebraOutputFormat.getNextFormat(geoElement, true, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 / 2",
            "sqrt(3) + 1",
            "sqrt(2)",
    })
    public void testOutputsWithEqualSignOperator(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(EQUALS, AlgebraOutputFormat.getOutputOperator(evaluateGeoElement(expression)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 / 3",
            "5 / 6",
    })
    public void testOutputsWithApproximatelyEqualSignOperator(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(APPROXIMATELY_EQUALS, AlgebraOutputFormat
                .getOutputOperator(evaluateGeoElement(expression)));
    }

    @Test
    public void testPossibleFormatsWithFilter() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1.234");

        AlgebraOutputFormatFilter approximationFormatFilter =
                (element, format) -> format != APPROXIMATION;

        assertEquals(
                List.of(FRACTION, APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement, true, Set.of()));
        assertEquals(
                List.of(FRACTION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(
                        geoElement, true, Set.of(approximationFormatFilter)));
    }

    @Test
    public void testPossibleFormatsWithMultipleFilters() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1.234");

        AlgebraOutputFormatFilter approximationFormatFilter =
                (element, format) -> format != APPROXIMATION;
        AlgebraOutputFormatFilter fractionFormatFilter =
                (element, format) -> format != FRACTION;

        assertEquals(
                List.of(FRACTION, APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement, true, Set.of()));
        assertEquals(
                List.of(ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(
                        geoElement, true, Set.of(approximationFormatFilter, fractionFormatFilter)));
    }

    @Test
    public void testSwitchFromDisabledFormatWhileInEnabledFormat() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1.234");
        AlgebraOutputFormatFilter approximationFormatFilter =
                (element, format) -> format != APPROXIMATION;

        assertEquals(
                List.of(FRACTION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(
                        geoElement, true, Set.of(approximationFormatFilter)));
        assertEquals(FRACTION, AlgebraOutputFormat.getActiveFormat(geoElement));
        assertEquals(ENGINEERING, AlgebraOutputFormat.getNextFormat(
                geoElement, true, Set.of(approximationFormatFilter)));

        AlgebraOutputFormat.switchFromDisabledFormat(
                geoElement, true, Set.of(approximationFormatFilter));

        assertEquals(FRACTION, AlgebraOutputFormat.getActiveFormat(geoElement));
        assertEquals(ENGINEERING, AlgebraOutputFormat.getNextFormat(
                geoElement, true, Set.of(approximationFormatFilter)));
    }

    @Test
    public void testSwitchFromDisabledFormatWhileInDisabledFormat1() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1.234");
        AlgebraOutputFormat.switchToNextFormat(geoElement, true, Set.of());
        assertEquals(
                List.of(FRACTION, APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement, true, Set.of()));
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getActiveFormat(geoElement));

        AlgebraOutputFormatFilter approximationFormatFilter =
                (element, format) -> format != APPROXIMATION;

        AlgebraOutputFormat.switchFromDisabledFormat(
                geoElement, true, Set.of(approximationFormatFilter));
        assertEquals(
                List.of(FRACTION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(
                        geoElement, true, Set.of(approximationFormatFilter)));
        assertEquals(FRACTION, AlgebraOutputFormat.getActiveFormat(geoElement));
    }

    @Test
    public void testSwitchFromDisabledFormatWhileInDisabledFormat2() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1.234");
        assertEquals(
                List.of(FRACTION, APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement, true, Set.of()));

        AlgebraOutputFormatFilter fractionFormatFilter = (element, format) -> format != FRACTION;

        assertEquals(
                List.of(APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(
                        geoElement, true, Set.of(fractionFormatFilter)));
        assertEquals(FRACTION, AlgebraOutputFormat.getActiveFormat(geoElement));
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(
                geoElement, true, Set.of(fractionFormatFilter)));

        AlgebraOutputFormat.switchFromDisabledFormat(
                geoElement, true, Set.of(fractionFormatFilter));

        assertEquals(APPROXIMATION, AlgebraOutputFormat.getActiveFormat(geoElement));
        assertEquals(ENGINEERING, AlgebraOutputFormat.getNextFormat(
                geoElement, true, Set.of(fractionFormatFilter)));
    }

    @Test
    public void testSwitchFromDisabledFormatByDefault() {
        setupApp(SuiteSubApp.GRAPHING);
        getAlgebraSettings().setEngineeringNotationEnabled(true);
        GeoElement geoElement1 = evaluateGeoElement("1.234");

        assertEquals(
                List.of(FRACTION, APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement1, true,
                        getAlgebraSettings().getAlgebraOutputFormatFilters()));
        assertEquals(FRACTION, AlgebraOutputFormat.getActiveFormat(geoElement1));

        AlgebraOutputFormatFilter fractionFormatFilter = (element, format) -> format != FRACTION;
        getAlgebraSettings().addAlgebraOutputFormatFilter(fractionFormatFilter);

        GeoElement geoElement2 = evaluateGeoElement("1.234");
        assertEquals(
                List.of(APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement2, true,
                        getAlgebraSettings().getAlgebraOutputFormatFilters()));
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getActiveFormat(geoElement2));
    }

    @Test
    public void testNextFormatWhileInOnlyPossibleFormat() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluateGeoElement("1.234");
        AlgebraOutputFormatFilter fractionFormatFilter = (element, format) -> format != FRACTION;
        AlgebraOutputFormat.switchFromDisabledFormat(
                geoElement, false, Set.of(fractionFormatFilter));

        assertEquals(
                List.of(APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(
                        geoElement, false, Set.of(fractionFormatFilter)));
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getActiveFormat(geoElement));
        assertNull(AlgebraOutputFormat.getNextFormat(
                geoElement, false, Set.of(fractionFormatFilter)));
    }

    @ParameterizedTest
    @MockedCasValues({
            "Solve(x x = 2) -> {x=-sqrt(2), x = sqrt(2)}",
            "NSolve(x x = 2) -> {x=-1.4, x = 1.4}",
            "Solutions(x x = 2) -> {-sqrt(2), sqrt(2)}",
            "NSolutions(x x = 2) -> {-1.4, 1.4}"
    })
    @ValueSource(strings = {"Solve(x x = 2)", "Solutions(x x = 2)"})
    public void solveInGraphing(String input) {
        setApp(AppCommonFactory.create(new AppConfigUnrestrictedGraphing()));
        mockedCasGiac.applyTo(getApp());
        GeoList list = evaluateGeoElement(input);
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getNextFormat(list, false, Set.of()));
        AlgebraOutputFormat.switchToNextFormat(list, false, Set.of());
        assertEquals(EXACT, AlgebraOutputFormat.getNextFormat(list, false, Set.of()));
    }
}
