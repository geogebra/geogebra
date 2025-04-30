package org.geogebra.common.exam;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateTableValues;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Delete;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateInput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Settings;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.APPROXIMATION;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.restrictions.MmsExamRestrictions;
import org.geogebra.common.exam.restrictions.mms.MmsValueConverter;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.gui.view.algebra.GeoElementValueConverter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("checkstyle:RegexpSinglelineCheck") // Tabs in CsvSources
public class MmsExamTests extends BaseExamTests {
	private static final Set<VisibilityRestriction> visibilityRestrictions =
			MmsExamRestrictions.createVisibilityRestrictions();

	@BeforeEach
	public void setupMmsExam() {
		setInitialApp(SuiteSubApp.CAS);
		examController.startExam(ExamType.MMS, null);
	}

	@ParameterizedTest
	@CsvSource({
			// Restricted inequalities
			"x > 0,						x > 0",
			"y <= 1,					y <= 1",
			"x < y,						x < y",
			"x - y > 2,					x - y > 2",
			"x^2 + 2y^2 < 1,			x^2 + 2y^2 < 1",
			"f: x > 0,					f: x > 0",
			"f(x) = x > 2,				f(x) = x > 2",
			// Restricted integrals
			"'Integral(g, -5, 5)',		0",
			"'Integral(g, x, -5, 5)',	0",
			"'NIntegral(g, -5, 5)',		0.0",
			// Restricted vectors
			"'a = (1, 2)',				'(1, 2)'",
			"'b = (1, 2) + 0',			'(1, 2)'",
			// Restricted implicit curves
			"x^2 = 1,					x^2 = 1",
			"2^x = 2,					2^x = 2",
			"sin(x) = 0,				sin(x) = 0",
			"y - x^2 = 0,				-x^2 + y = 0",
			"x^2 = y,					x^2 = y",
			"x^2 + y^2 = 4,				x^2 + y^2 = 4",
			"x^2 / 9 + y^2 / 4 = 1,		1 / 9 * x^2 + 1 / 4 * y^2 = 1",
			"x^2 - y^2 = 4,				x^2 - y^2 = 4",
			"x^3 + y^2 = 2,				x^3 + y^2 = 2",
			"y^3 = x,					y^3 = x",
			// Restricted lines
			"x = 0,						x = 0",
			"x + y = 0,					x + y = 0",
			"2x - 3y = 4,				2*x - 3*y = 4"
	})
	public void testRestrictedVisibility(String expression, String mockedCasOutput) {
		evaluateGeoElement("g(x) = x", "x"); // For integrals
		assertTrue(VisibilityRestriction.isVisibilityRestricted(
				evaluateGeoElementNumeric(expression, mockedCasOutput), visibilityRestrictions));
	}

	@ParameterizedTest
	@CsvSource({
			// Unrestricted integrals
			"Integral(g),		1 / 2 * x^2 + arbconst(1 + 33)",
			"'Integral(g, x)', 	1 / 2 * x^2 + arbconst(1 + 66)",
			// Unrestricted functions
			"y = 2x,			y = (2 * x)",
			"y = 5x - 2,		y = (5 * x - 2)",
			"f(x) = x^2,		x^2",
			"y = x^2,			y = (x^2)",
			"y = x^3,			y = (x^3)",
	})
	public void testUnrestrictedVisibility(String expression, String mockedCasOutput) {
		evaluateGeoElement("g(x) = x", "x"); // For integrals
		assertFalse(VisibilityRestriction.isVisibilityRestricted(
				evaluateGeoElement(expression, mockedCasOutput), visibilityRestrictions));
	}

	@Test
	public void testRestrictedStatisticsContextMenuItems() {
		assertEquals(
				List.of(CreateTableValues, RemoveLabel, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						algebraProcessor, CAS_APPCODE, app.getSettings().getAlgebra()));
	}

	@ParameterizedTest
	@CsvSource({
			"1 + i",
			"(1 + i) * (2 - 3i)",
			"5 - i + 2",
	})
	public void testRestrictedComplexNumberInputs(String expression) {
		assertNull(evaluate(expression));
	}

	@Test
	public void testRestrictedComplexNumberOutput() {
		assertNull(evaluate("sqrt(-5)", "ί*√5"));
		assertEquals("Please check your input", errorAccumulator.getErrorsSinceReset());
		errorAccumulator.resetError();
	}

	@Test
	public void testRestrictedSpecialPointsContextMenuItem() {
		assertEquals(
				List.of(CreateTableValues, RemoveLabel, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(
						evaluateGeoElement("f(x)=xx", "x^2"),
						algebraProcessor, CAS_APPCODE, app.getSettings().getAlgebra()));
	}

	@Test
	public void testRestrictedSpecialPoints() {
		GeoElement f = evaluateGeoElement("xx");
		app.getSpecialPointsManager().updateSpecialPoints(f);
		assertNull(app.getSpecialPointsManager().getSelectedPreviewPoints());
	}

	@Test
	public void testRestrictedChartOutput() {
		String definition = "BarChart({10, 11, 12}, {5, 8, 12})";
		GeoElement barchart = evaluateGeoElementNumeric(definition, "?");
		MmsValueConverter converter = new MmsValueConverter(new GeoElementValueConverter());
		assertEquals(definition,
				converter.toValueString(barchart, StringTemplate.defaultTemplate));
		assertEquals(definition,
				converter.toOutputValueString(barchart, StringTemplate.defaultTemplate));
		assertEquals("a = " + definition,
				converter.toLabelAndDescription(barchart, StringTemplate.defaultTemplate));
	}

	/**
	 * @return an array of parameters for grid testing list and number operations
	 */
	public static String[] createParameters() {
		String[] lists = {"{1, 2, 3}", "{{1}, 2, 3}"};
		String[] numbers = {"2", "(1 + 2)"};
		String[] operators = {"+", "-", "*", "/", "^"};
		List<String> parameters = new ArrayList<>();
		for (String list : lists) {
			for (String number : numbers) {
				for (String operator : operators) {
					parameters.add(list + " " + operator + " " + number);
					parameters.add(number + " " + operator + " " + list);
					parameters.add(list + " " + operator + " " + list);
				}
			}
		}
		return parameters.toArray(new String[0]);
	}

	@ParameterizedTest
	@MethodSource(value = "createParameters")
	public void testRestrictedListNumberOperation(String expression) {
		assertNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Union({1}, {2})",
			"Quartile1({1, 2, 3})",
			"Q1({1, 2, 3})",
	})
	public void testRestrictedCommands(String expression) {
		assertNull(evaluate(expression, "1"));
		assertThat(errorAccumulator.getErrorsSinceReset(), containsString("Unknown command"));
		errorAccumulator.resetError();
	}

	@ParameterizedTest
	@CsvSource(delimiterString = "->", value = {
			"{1, 2} ⊆ {1, 2, 3}			-> true",
			"{1, 2} ⊂ {1, 2, 3}			-> true",
			"{1, 2} \\ {1, 2, 3}		-> {}",
			"{1, 2, 3} + 5				-> {6, 7, 8}",
			"{1, 5, 11} / 5				-> {1 / 5, 1, 11 / 5}",
			"{1, 2, 3} * 5				-> {5, 10, 15}",
			"{1, 2, 3}^2				-> {1, 4, 9}",
			"5 + {1, 2, 3}				-> {5, 10, 15}",
			"{1, 2, 3} + a				-> {ggbtmpvara 1,ggbtmpvara 2,ggbtmpvara 3}",
			"{1, 2, 3} + {4, 5, 6}		-> {5, 7, 9}",
			"{1, 2, 3} ^ {3, 2, 1}		-> {1, 4, 3}",
			"{1, 2, {3, 4, 5} + 6, 7}	-> {1, 2, {9, 10, 11}, 7}",
			"{1, 2, {3, 4, 5}, 6} + 7	-> {8, 9, poly1[3, 4, 12], 13}",
	})
	public void testRestrictedListOperations(String expression, String mockedCasOutput) {
		assertNull(evaluate(expression, mockedCasOutput));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"f({1, 2, 3})",
			"sin({1, 2, 3})",
			"tan({1, 2, 3})",
	})
	public void testRestrictedFunctions(String expression) {
		evaluate("f(x) = x^2", "x^2");
		// mocked output: sin(1,2,3) should not be {1,2,3}, but only types matter here
		assertNull(evaluate(expression, "{1,2,3}"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{{1, 2}, {3, 4}}",
			"{(1, 2), (3, 4)}",
			"{{1, 2}, {3, 4}} + {{5, 6}, {7, 8}}",
			"{{1, 2}, {3, 4}} - {{5, 6}, {7, 8}}",
			"{{1, 2}, {3, 4}} * {{5, 6}, {7, 8}}",
			"{{1, 2}, {3, 4}} / {{5, 6}, {7, 8}}",
	})
	public void testAllowedLists(String expression) {
		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@CsvSource(value = {
			"BinomialDist(1, 0.5); Illegal number of arguments",
			"BinomialDist(1, 0.5, false); Illegal argument: false",
			"Invert(sin(x)); Illegal argument",
			"Length((1, 2)); Illegal argument",
			"Product(a^2, a, 0, 5); Illegal number of arguments",
			"SampleSD({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3}); Illegal number of arguments",
			"SigmaXX({(1, 2), (3, 4)}); Illegal argument",
			"SigmaXY({(1, 2), (3, 4)}); Illegal number of arguments",
			"stdev({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3}); Illegal number of arguments",
			"Sum(a^2, a, 0, 5); Illegal number of arguments",
			"Normal(2, 0.5, 1, true); Illegal argument: true",
			"Normal(2, 0.5, x, true); Illegal argument: true",
	}, delimiter = ';')
	public void testRestrictedArguments(String expression, String expectedError) {
		// Specify a valid output, so that we know that the input has been filtered or not.
		assertNull(evaluate(expression, "1"));
		assertThat(errorAccumulator.getErrorsSinceReset(), containsString(expectedError));
		errorAccumulator.resetError();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Normal(2, 0.5, 1)",
			"Normal(2, 0.5, 1, 2)",
	})
	public void testUnrestrictedArguments(String expression) {
		assertNotNull(evaluate(expression, "1"));
	}

	@Test
	public void testRestrictedSumSyntax() {
		String sumSyntax = app.getLocalization().getCommandSyntax(Commands.Sum.getCommand());
		assertFalse(sumSyntax.toLowerCase().contains("end value"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1 == 2",
			"1 != 2",
			"1 < 2",
			"1 > 2",
			"1 <= 2",
			"1 >= 2",
			"true && false",
			"true || false",
			"!true",
			"true ⊕ false",
			"true -> false",
			"3x < 5 + (true && true)",
			"5 ∈ {1, 2, 3, 4, 5}"
	})
	public void testRestrictedOperators(String expression) {
		assertNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"2 x – 1 < 3",
			"4 >= 5x^2"
	})
	public void testInequalitiesAllowed(String expression) {
		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"a ∥ b",
			"a ⊥ b",
	})
	public void testRestrictedLineOperators(String expression) {
		evaluate("a : x + 5 = 0");
		evaluate("b : x - 5 = 0");
		assertNull(evaluate(expression));
		errorAccumulator.resetError();
	}

	@ParameterizedTest
	@CsvSource({
			"'(3; π / 3)', 	'(3 / 2, 3 * √3 / 2)'",
			"'(1; 2)', 		'(cos(2), sin(2))'",
	})
	public void testRestrictedCartesianOutputFormatForPolarCoordinates(
			String expression, String mockedCasOutput) {
		AlgebraSettings algebraSettings = app.getSettings().getAlgebra();
		GeoElement geoElement = evaluateGeoElement(expression, mockedCasOutput);
		assertEquals(
				List.of(APPROXIMATION),
				AlgebraOutputFormat.getPossibleFormats(geoElement,
						algebraSettings.isEngineeringNotationEnabled(),
						algebraSettings.getAlgebraOutputFormatFilters()));
		assertEquals(APPROXIMATION, AlgebraOutputFormat.getActiveFormat(geoElement));
		assertNull(AlgebraOutputFormat.getNextFormat(geoElement,
				algebraSettings.isEngineeringNotationEnabled(),
				algebraSettings.getAlgebraOutputFormatFilters()));
	}
}
