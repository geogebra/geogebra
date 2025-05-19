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
import org.geogebra.common.cas.MockCASGiac;
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
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class AlgebraOutputFormatTests {
    private AppCommon app;
    private AlgebraProcessor algebraProcessor;
    private MockCASGiac mockCASGiac;
    private AlgebraSettings algebraSettings;

    @ParameterizedTest
    @CsvSource({
            "x^2,       x^2",
            "1,         1",
            "x = 0,     x = 0",
            "sqrt(4),   2",
            "2000,      2000",
            "-10,       -10",
    })
    public void testNoToggleButtonInCas(String expression, String mockedCasOutput) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluate(expression, mockedCasOutput), false, Set.of()));
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
    @CsvSource({
            "1 / 2,         1 / 2",
            "(5(2)/(3)),    17 / 3",
            "2^(-1),        1 / 2",
            "sin(30°),      1 / 2",
            "1.234,         617 / 500",
            "1.23456789,    123456789 / 1000000000",
    })
    public void testFractionalOutputsInCas(String expression, String mockedCasOutput) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(FRACTION, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(
                        evaluate(expression, mockedCasOutput), false, Set.of()));
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
    @CsvSource({                        // Mocked CAS outputs
            "'Solve(x^2 = 1 / 2, x)',   '{x=(-√2/2),x=(√2/2)}', '{x=-0.7071,x=0.7071}'",
            "'Solve(x^2 = 2, x)',       '{x=(-√2),x=(√2)}',     '{x=-1.4142,x=1.4142}'",
    })
    public void testApproximationAndSymbolicOutputInSolveResult(String expression,
            String mockedCasOutput1, String mockedCasOutput2) {
        setupApp(SuiteSubApp.CAS);
        assertEquals(
                List.of(EXACT, APPROXIMATION),
                AlgebraOutputFormat.getPossibleFormats(evaluate(expression,
                        // Calculated internally multiple times which requires multiple mock values
                        mockedCasOutput1, mockedCasOutput1,
                        mockedCasOutput2, mockedCasOutput2, mockedCasOutput2, mockedCasOutput2
                ), false, Set.of()));
    }

    @Test
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
        mockCASGiac = subApp == SuiteSubApp.CAS ? new MockCASGiac(app) : null;
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

    private GeoElement evaluate(String expression, String... mockedCasOutputs) {
        if (app.getConfig().getSubApp() == SuiteSubApp.CAS && mockCASGiac != null) {
            for (String mockedCasOutput : mockedCasOutputs) {
                mockCASGiac.memorize(mockedCasOutput);
            }
        }
        EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
        return (GeoElement) algebraProcessor.processAlgebraCommandNoExceptionHandling(
                expression, false, new ErrorAccumulator(), evalInfo, null)[0];
    }

    private GeoElement evaluate(String expression) {
        return evaluate(expression, expression);
    }
}
