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
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.cas.MockedCasGiac;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigProbability;
import org.geogebra.common.main.settings.config.AppConfigScientific;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(MockedCasValuesExtension.class)
public class AlgebraOutputFormatTests {
    private AppCommon app;
    private AlgebraProcessor algebraProcessor;
    private AlgebraSettings algebraSettings;
    private final MockedCasGiac mockedCasGiac = new MockedCasGiac();

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
            "Evaluate(x²)       -> x^2",
            "Evaluate(1)        -> 1",
            "Round(1, 2)        -> 1.0",
            "Evaluate(x = 0)    -> x=0",
            "Evaluate(sqrt(4))  -> 2",
            "Round(2, 2)        -> 2.0",
            "Evaluate(2000)     -> 2000",
            "Round(2000, 2)     -> 2000.0",
            "Evaluate(-10)      -> -10",
            "Round(-10, 2)      -> -10.0",
    })
    public void testNoToggleButtonInCas(String expression) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluate(expression), false, Set.of()));
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
                AlgebraOutputFormat.getPossibleFormats(evaluate(expression), false, Set.of()));
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
            "Evaluate(1 / 2)                    -> 1/2",
            "Round(1 / 2, 2)                    -> 0.5",
            "Evaluate(5 * 2 / 3)                -> 10/3",
            "Round(10 / 3, 2)                   -> 3.33",
            "Evaluate(2⁻¹)                      -> 1/2",
            "Round(1 / 2, 2)                    -> 0.5",
            "Evaluate(sin(30°))                 -> 1/2",
            "Round(1 / 2, 2)                    -> 0.5",
            "Evaluate(1.23)                     -> 123456789/100000000",
            "Round(617 / 500, 2)                -> 1.23",
            "Evaluate(1.23)                     -> 123456789/100000000",
            "Round(123456789 / 100000000, 2)    -> 1.23",
    })
    public void testFractionalOutputsInCas(String expression) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(FRACTION, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(evaluate(expression), false, Set.of()));
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
                AlgebraOutputFormat.getPossibleFormats(evaluate(expression), false, Set.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "sin(1 / 2)",
            "sqrt(2)",
            "e",
            "π",
    })
    @MockedCasValues({
            "Evaluate(sin(1 / 2))   -> sin(1/2)",
            "Round(sin(1 / 2), 2)   -> 0.48",
            "Evaluate(sqrt(2))      -> √2",
            "Round(sqrt(2), 2)      -> 1.41",
            "Evaluate(ℯ)            -> ℯ",
            "Round(ℯ, 2)            -> 2.72",
            "Evaluate(π)            -> pi",
            "Round(π, 2)            -> 3.14",
    })
    public void testNonFractionalDecimalOutputsInCas(String expression) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(EXACT, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(evaluate(expression), false, Set.of()));
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
                AlgebraOutputFormat.getPossibleFormats(evaluate(expression), true, Set.of()));
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
                AlgebraOutputFormat.getPossibleFormats(evaluate(expression), true, Set.of()));
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
                AlgebraOutputFormat.getPossibleFormats(evaluate(expression), true, Set.of()));
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
            AlgebraOutputFormat.getPossibleFormats(evaluate(expression), false, Set.of()));
    }

    @Test
    @MockedCasValues({
            "Evaluate(sqrt(2))  -> √2",
            "Round(sqrt(2), 2)  -> 1.41",
    })
    public void testSwitchingBetweenApproximationAndEquals() {
        setupApp(SuiteSubApp.CAS);
        GeoElement geoElement = evaluate("sqrt(2)");
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
        GeoElement geoElement = evaluate("1 / 2");
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
            "Evaluate(1 / 2)    -> 1/2",
            "Round(1 / 2, 2)    -> 0.5",
    })
    public void testSwitchingBetweenFractionAndApproximationInCas() {
        setupApp(SuiteSubApp.CAS);
        GeoElement geoElement = evaluate("1 / 2");
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
        GeoElement geoElement = evaluate("1.2345678");
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
        GeoElement geoElement = evaluate("1.234");
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
        assertEquals(EQUALS, AlgebraOutputFormat.getOutputOperator(evaluate(expression)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 / 3",
            "5 / 6",
    })
    public void testOutputsWithApproximatelyEqualSignOperator(String expression) {
        setupApp(SuiteSubApp.GRAPHING);
        assertEquals(APPROXIMATELY_EQUALS, AlgebraOutputFormat
                .getOutputOperator(evaluate(expression)));
    }

    @Test
    public void testPossibleFormatsWithFilter() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluate("1.234");

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
        GeoElement geoElement = evaluate("1.234");

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
        GeoElement geoElement = evaluate("1.234");
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
        GeoElement geoElement = evaluate("1.234");
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
        GeoElement geoElement = evaluate("1.234");
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
        algebraSettings.setEngineeringNotationEnabled(true);
        GeoElement geoElement1 = evaluate("1.234");

        assertEquals(
                List.of(FRACTION, APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement1, true,
                        algebraSettings.getAlgebraOutputFormatFilters()));
        assertEquals(FRACTION, AlgebraOutputFormat.getActiveFormat(geoElement1));

        AlgebraOutputFormatFilter fractionFormatFilter = (element, format) -> format != FRACTION;
        algebraSettings.addAlgebraOutputFormatFilter(fractionFormatFilter);

        GeoElement geoElement2 = evaluate("1.234");
        assertEquals(
                List.of(APPROXIMATION, ENGINEERING),
                AlgebraOutputFormat.getPossibleFormats(geoElement2, true,
                        algebraSettings.getAlgebraOutputFormatFilters()));
        assertEquals(APPROXIMATION, AlgebraOutputFormat.getActiveFormat(geoElement2));
    }

    @Test
    public void testNextFormatWhileInOnlyPossibleFormat() {
        setupApp(SuiteSubApp.GRAPHING);
        GeoElement geoElement = evaluate("1.234");
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

    private void setupApp(SuiteSubApp subApp) {
        app = AppCommonFactory.create(createConfig(subApp));
        if (subApp == SuiteSubApp.CAS) {
            mockedCasGiac.applyTo(app);
        }
        algebraProcessor = app.getKernel().getAlgebraProcessor();
        algebraSettings = app.getSettings().getAlgebra();
    }

    private AppConfig createConfig(SuiteSubApp subApp) {
        switch (subApp) {
            case CAS:
                return new AppConfigCas(GeoGebraConstants.SUITE_APPCODE);
            case GRAPHING:
                return new AppConfigUnrestrictedGraphing(GeoGebraConstants.SUITE_APPCODE);
            case GEOMETRY:
                return new AppConfigGeometry(GeoGebraConstants.SUITE_APPCODE);
            case SCIENTIFIC:
                return new AppConfigScientific(GeoGebraConstants.SUITE_APPCODE);
            case G3D:
                return new AppConfigGraphing3D(GeoGebraConstants.SUITE_APPCODE);
            case PROBABILITY:
                return new AppConfigProbability(GeoGebraConstants.SUITE_APPCODE);
        }
        return null;
    }

    private GeoElement evaluate(String expression) {
        EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
        return (GeoElement) algebraProcessor.processAlgebraCommandNoExceptionHandling(
                expression, false, new ErrorAccumulator(), evalInfo, null)[0];
    }
}
