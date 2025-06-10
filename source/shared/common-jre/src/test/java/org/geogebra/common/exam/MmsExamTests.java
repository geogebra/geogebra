package org.geogebra.common.exam;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateTableValues;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Delete;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateInput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Settings;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputFormat.APPROXIMATION;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.restrictions.MmsExamRestrictions;
import org.geogebra.common.exam.restrictions.mms.MmsAlgebraOutputFilter;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.gui.view.algebra.ProtectiveGeoElementValueConverter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("checkstyle:RegexpSinglelineCheck") // Tabs in CsvSources/MockedCasValues
@ExtendWith(MockedCasValuesExtension.class)
public class MmsExamTests extends BaseExamTestSetup {
	private static final Set<VisibilityRestriction> visibilityRestrictions =
			MmsExamRestrictions.createVisibilityRestrictions();

	@BeforeEach
	public void setupMmsExam() {
		setupApp(SuiteSubApp.CAS);
		examController.startExam(ExamType.MMS, null);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Restricted inequalities
			"x > 0",
			"y <= 1",
			"x < y",
			"x - y > 2",
			"x^2 + 2y^2 < 1",
			"f: x > 0",
			"f(x) = x > 2",
			// Restricted integrals
			"Integral(g, -5, 5)",
			"Integral(g, x, -5, 5)",
			"NIntegral(g, -5, 5)",
			// Restricted vectors
			"a = (1, 2)",
			"b = (1, 2) + 0",
			// Restricted implicit curves
			"x^2 = 1",
			"2^x = 2",
			"sin(x) = 0",
			"y - x^2 = 0",
			"x^2 = y",
			"x^2 + y^2 = 4",
			"x^2 / 9 + y^2 / 4 = 1",
			"x^2 - y^2 = 4",
			"x^3 + y^2 = 2",
			"y^3 = x",
			// Restricted lines
			"x = 0",
			"x + y = 0",
			"2x - 3y = 4",
	})
	@MockedCasValues({
			"Evaluate(x) 					-> x",
			"Evaluate(x > 0) 				-> x>0",
			"Evaluate(y ≤ 1) 				-> y<=1",
			"Evaluate(x < y) 				-> y>x",
			"Evaluate(x - y > 2) 			-> (x-y)>2",
			"Evaluate(x² + 2y² < 1) 		-> (x^2+2*y^2)<1",
			"Evaluate(x > 2) 				-> x>2",
			"Integral(x, -5, 5) 			-> 0",
			"Round(0, 13) 					-> 0.0",
			"Integral(x, x, -5, 5) 			-> 0",
			"NIntegral(x, -5, 5) 			-> 0.0",
			"Evaluate((1, 2)) 				-> (1,2)",
			"Evaluate((1, 2) + 0) 			-> (1,2)",
			"Evaluate(x² = 1) 				-> x^2=1",
			"Evaluate(2^x = 2) 				-> 2^x=2",
			"Evaluate(sin(x) = 0) 			-> sin(x)=0",
			"Evaluate(y - x² = 0) 			-> -x^2+y=0",
			"Evaluate(x² = y) 				-> x^2=y",
			"Evaluate(x² + y² = 4) 			-> x^2+y^2=4",
			"Evaluate(x² / 9 + y² / 4 = 1) 	-> 1/9*x^2+1/4*y^2=1",
			"Evaluate(x² - y² = 4) 			-> x^2-y^2=4",
			"Evaluate(x³ + y² = 2) 			-> x^3+y^2=2",
			"Evaluate(y³ = x) 				-> y^3=x",
			"Evaluate(x = 0) 				-> x=0",
			"Evaluate(x + y = 0) 			-> x+y=0",
			"Evaluate(2x - 3y = 4) 			-> 2*x-3*y=4",
	})
	public void testRestrictedVisibility(String expression) {
		evaluateGeoElement("g(x) = x"); // For integrals
		assertTrue(VisibilityRestriction.isVisibilityRestricted(evaluateGeoElement(
				expression), visibilityRestrictions));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Unrestricted integrals
			"Integral(g)",
			"Integral(g, x)",
			// Unrestricted functions
			"y = 2x",
			"y = 5x - 2",
			"f(x) = x^2",
			"y = x^2",
			"y = x^3",
	})
	@MockedCasValues({
			"Evaluate(x) 			-> x",
			"Integral(x) 			-> 1/2*x^2+arbconst(1+33)",
			"Integral(x, 0) 		-> 1/2*x^2+arbconst(2+66)",
			"Evaluate(y = 2x) 		-> y=(2*x)",
			"Evaluate(y = 5x - 2) 	-> y=(5*x-2)",
			"Evaluate(x²) 			-> x^2",
			"Evaluate(y = x²) 		-> y=(x^2)",
			"Evaluate(y = x³) 		-> y=(x^3)",
	})
	public void testUnrestrictedVisibility(String expression) {
		evaluateGeoElement("g(x) = x"); // For integrals
		assertFalse(VisibilityRestriction.isVisibilityRestricted(
				evaluateGeoElement(expression), visibilityRestrictions));
	}

	@Test
	@MockedCasValues({"Evaluate({1, 2, 3}) -> {1,2,3}"})
	public void testRestrictedStatisticsContextMenuItems() {
		assertEquals(
				List.of(CreateTableValues, RemoveLabel, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(evaluateGeoElement("{1, 2, 3}"),
						getAlgebraProcessor(), CAS_APPCODE, getAlgebraSettings()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1 + i",
			"(1 + i) * (2 - 3i)",
			"5 - i + 2",
	})
	public void testRestrictedComplexNumberInputs(String expression) {
		assertNull(evaluate(expression));
	}

	@Test
	@MockedCasValues({"Evaluate(sqrt(-5)) -> ί*√5"})
	public void testRestrictedComplexNumberOutput() {
		assertNull(evaluate("sqrt(-5)"));
		assertEquals("Please check your input", errorAccumulator.getErrorsSinceReset());
		errorAccumulator.resetError();
	}

	@Test
	@MockedCasValues({"Evaluate(x²) -> x^2"})
	public void testRestrictedSpecialPointsContextMenuItem() {
		assertEquals(
				List.of(CreateTableValues, RemoveLabel, DuplicateInput, Delete, Settings),
				contextMenuFactory.makeAlgebraContextMenu(
						evaluateGeoElement("f(x)=xx"),
						getAlgebraProcessor(), CAS_APPCODE, getAlgebraSettings()));
	}

	@Test
	@MockedCasValues({"Evaluate(x²) -> x^2"})
	public void testRestrictedSpecialPoints() {
		GeoElement f = evaluateGeoElement("xx");
		getApp().getSpecialPointsManager().updateSpecialPoints(f);
		assertNull(getApp().getSpecialPointsManager().getSelectedPreviewPoints());
	}

	@Test
	@MockedCasValues({
			"BarChart({10, 11, 12}, {5, 8, 12}) -> 25",
			"Round(25, 13) 						-> 25.0",
	})

	public void testRestrictedChartOutput() {
		String definition = "BarChart({10, 11, 12}, {5, 8, 12})";
		GeoElement barchart = evaluateGeoElement(definition);
		ProtectiveGeoElementValueConverter converter =
				new ProtectiveGeoElementValueConverter(new MmsAlgebraOutputFilter(null));
		assertEquals(definition,
				converter.toValueString(barchart, StringTemplate.defaultTemplate));
		assertEquals(definition,
				converter.toOutputValueString(barchart, StringTemplate.defaultTemplate));
		assertEquals("a = " + definition,
				converter.toLabelAndDescription(barchart, StringTemplate.defaultTemplate));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Union({1}, {2})",
			"Quartile1({1, 2, 3})",
			"Q1({1, 2, 3})",
	})
	@MockedCasValues({
			"Union({1}, {2}) 		-> {1,2}",
			"Quartile1({1, 2, 3}) 	-> 1.0",
			"Round(1, 2) 			-> 1.0",
	})
	public void testRestrictedCommands(String expression) {
		assertNull(evaluate(expression));
		assertThat(errorAccumulator.getErrorsSinceReset(), containsString("Unknown command"));
		errorAccumulator.resetError();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{1, 2} ⊆ {1, 2, 3}",
			"{1, 2} ⊂ {1, 2, 3}",
			"{1, 2} \\ {1, 2, 3}",
			"{1, 2, 3} + 5",
			"{1, 5, 11} / 5",
			"{1, 2, 3} * 5",
			"{1, 2, 3}^2",
			"5 + {1, 2, 3}",
			"{1, 2, 3} + a",
			"{1, 2, 3} + {4, 5, 6}",
			"{1, 2, 3} ^ {3, 2, 1}",
			"{1, 2, {3, 4, 5} + 6, 7}",
			"{1, 2, {3, 4, 5}, 6} + 7",
	})
	public void testRestrictedListOperations(String expression) {
		assertNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"f({1, 2, 3})",
			"sin({1, 2, 3})",
			"tan({1, 2, 3})",
	})
	@MockedCasValues({
			"Evaluate(x²) 				-> x^2",
			"Evaluate({1, 2, 3}²) 		-> {1,4,9}",
			"Evaluate(sin({1, 2, 3})) 	-> {sin(1),sin(2),sin(3)}",
			"Evaluate(tan({1, 2, 3})) 	-> {tan(1),tan(2),tan(3)}",
	})
	public void testRestrictedFunctions(String expression) {
		evaluate("f(x) = x^2");
		assertNull(evaluate(expression));
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
	@MockedCasValues({
			"Evaluate({{1, 2}, {3, 4}}) 					-> {{1,2},{3,4}}",
			"Evaluate({(1, 2), (3, 4)}) 					-> {(1,2),(3,4)}",
			"Evaluate({{1, 2}, {3, 4}} + {{5, 6}, {7, 8}}) 	-> {{6,8},{10,12}}",
			"Evaluate({{1, 2}, {3, 4}} - {{5, 6}, {7, 8}}) 	-> {{-4,-4},{-4,-4}}",
			"Evaluate({{1, 2}, {3, 4}} {{5, 6}, {7, 8}}) 	-> {{19,22},{43,50}}",
			"Evaluate({{1, 2}, {3, 4}} / {{5, 6}, {7, 8}}) 	-> {{1/5,1/3},{3/7,1/2}}",
	})
	public void testAllowedLists(String expression) {
		assertNotNull(evaluate(expression));
	}

	@SuppressWarnings("checkstyle:LineLengthCheck")
	@ParameterizedTest
	@CsvSource(delimiter = ';', value = {
			"BinomialDist(1, 0.5); 									Illegal number of arguments",
			"BinomialDist(1, 0.5, false); 							Illegal argument: false",
			"Invert(sin(x)); 										Illegal argument",
			"Length((1, 2)); 										Illegal argument",
			"Product(a^2, a, 0, 5); 								Illegal number of arguments",
			"SampleSD({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3}); 	Illegal number of arguments",
			"stdev({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3}); 	Illegal number of arguments",
			"SigmaXX({(1, 2), (3, 4)}); 							Illegal argument",
			"SigmaXY({(1, 2), (3, 4)}); 							Illegal number of arguments",
			"Sum(a^2, a, 0, 5); 									Illegal number of arguments",
			"Normal(2, 0.5, 1, true); 								Illegal argument: true",
			"Normal(2, 0.5, x, true); 								Illegal argument: true",
	})
	@MockedCasValues({
			"BinomialDist(1, 0.5) 										-> 1",
			"Round(1, 2) 												-> 1.0",
			"BinomialDist(1, 0.5, false) 								-> ?",
			"Numeric(BinomialDist(1, 0.5, false)) 						-> ?",
			"Invert(sin(x)) 											-> -asin(x)+2*arbint(0)*pi+pi",
			"Length((1, 2)) 											-> √5",
			"Round(sqrt(5), 2) 											-> 2.24",
			"Product(a², a, 0, 5) 										-> 0",
			"Round(0, 2) 												-> 0.0",
			"stdev({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3}) 			-> ?",
			"Numeric(stdev({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3})) -> 18378519.31067*ί",
			"Round(18378519.31ί, 2) 									-> 18378519.31*ί",
			"SigmaXX({(1, 2), (3, 4)}) 									-> 10",
			"Round(10, 2) 												-> 10.0",
			"SigmaXY({(1, 2), (3, 4)}) 									-> 14",
			"Round(14, 2) 												-> 14.0",
			"Sum(a², a, 0, 5) 											-> 55",
			"Round(55, 2) 												-> 55.0",
			"Normal(2, 0.5, 1, true) 									-> (erf(-√2)+1)/2",
			"Round((erf(-sqrt(2)) + 1) / 2, 2) 							-> 0.02",
			"Normal(2, 0.5, x, true) 									-> (erf(x*√2-2*√2)+1)/2",
	})
	public void testRestrictedArguments(String expression, String expectedError) {
		assertNull(evaluate(expression));
		assertThat(errorAccumulator.getErrorsSinceReset(), containsString(expectedError));
		errorAccumulator.resetError();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Normal(2, 0.5, 1)",
			"Normal(2, 0.5, 1, 2)",
	})
	@MockedCasValues({
			"Normal(2, 0.5, 1) 							-> (erf(-√2)+1)/2",
			"Round((erf(-sqrt(2)) + 1) / 2, 13) 		-> 0.0227501319482",
			"Normal(2, 0.5, 1, 2) 						-> 1/2-(erf(-√2)+1)/2",
			"Round(1 / 2 - (erf(-sqrt(2)) + 1) / 2, 13) -> 0.4772498680518",
	})
	public void testUnrestrictedArguments(String expression) {
		assertNotNull(evaluate(expression));
	}

	@Test
	public void testRestrictedSumSyntax() {
		String sumSyntax = getApp().getLocalization().getCommandSyntax(Commands.Sum.getCommand());
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
			"5 ∈ {1, 2, 3, 4, 5}",
	})
	public void testRestrictedOperators(String expression) {
		assertNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"2x – 1 < 3",
			"4 >= 5x^2",
	})
	@MockedCasValues({
			"Evaluate(2x - 1 < 3) 	-> (2*x-1)<3",
			"Evaluate(4 ≥ 5x²) 		-> (5*x^2)<=4",
	})
	public void testInequalitiesAllowed(String expression) {
		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"a ∥ b",
			"a ⊥ b",
	})
	@MockedCasValues({
			"Evaluate(x + 5 = 0) 				-> x+5=0",
			"Evaluate(x - 5 = 0) 				-> x-5=0",
			"Evaluate(x + 5 = 0 ∥ x - 5 = 0) 	-> x+5=(-5=0)",
			"Evaluate(x + 5 = 0 ⟂ x - 5 = 0) 	-> x+5=(-5=0)",
	})
	public void testRestrictedLineOperators(String expression) {
		evaluate("a : x + 5 = 0");
		evaluate("b : x - 5 = 0");
		assertNull(evaluate(expression));
		errorAccumulator.resetError();
	}

	@SuppressWarnings("checkstyle:LineLengthCheck")
	@ParameterizedTest
	@ValueSource(strings = {
			"(1 + cos(t), 2 + sin(t))",
			"(2t^2, t^3 - 1)",
			"sin(2 θ)",
			"(sin(2*t); t)"
	})
	@MockedCasValues({
			"Evaluate((1 + cos(t), 2 + sin(t))) -> (cos(ggbtmpvart)+1,sin(ggbtmpvart)+2)",
			"Evaluate((2t², t³ - 1)) 			-> (2*ggbtmpvart^2,ggbtmpvart^3-1)",
			"Evaluate(sin(2θ)) 					-> sin(2*ggbtmpvarθ)",
			"Evaluate((sin(2t); t)) 			-> (cos(ggbtmpvart)*sin(2*ggbtmpvart),sin(2*ggbtmpvart)*sin(ggbtmpvart))",
	})
	public void testRestrictedParametricOrPolarCurves(String expression) {
		assertNull(evaluate(expression));
		errorAccumulator.resetError();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Normal command outputs
			"Normal(2, 0.5, 1)",
			"Normal(2, 0.5, 1, 2)",
			// Cartesian output for polar coordinates
			"(3; π / 3)",
			"(1; 2)",
	})
	@MockedCasValues({
			"Normal(2, 0.5, 1) 							-> (erf(-√2)+1)/2",
			"Round((erf(-sqrt(2)) + 1) / 2, 13) 		-> 0.0227501319482",
			"Normal(2, 0.5, 1, 2) 						-> 1/2-(erf(-√2)+1)/2",
			"Round(1 / 2 - (erf(-sqrt(2)) + 1) / 2, 13) -> 0.4772498680518",
			"Evaluate((3; π / 3)) 						-> (3/2,3*√3/2)",
			"Evaluate((1; 2)) 							-> (cos(2),sin(2))",
	})

	public void testRestrictedOutputFormats(String expression) {
		GeoElement geoElement = evaluateGeoElement(expression);
		assertEquals(
				List.of(APPROXIMATION),
				AlgebraOutputFormat.getPossibleFormats(geoElement,
						getAlgebraSettings().isEngineeringNotationEnabled(),
						getAlgebraSettings().getAlgebraOutputFormatFilters()));
		assertEquals(APPROXIMATION, AlgebraOutputFormat.getActiveFormat(geoElement));
		assertNull(AlgebraOutputFormat.getNextFormat(geoElement,
				getAlgebraSettings().isEngineeringNotationEnabled(),
				getAlgebraSettings().getAlgebraOutputFormatFilters()));
	}

	@ParameterizedTest
	@CsvSource(delimiterString = "->", value = {
			"x + x + x + x 				-> x + x + x + x",
			"x^2 + x^2 					-> x² + x²",
			"x^2 + x^2 + y + y + 5 - 4 	-> x² + x² + y + y + 5 - 4",
	})
	@MockedCasValues({
			"Evaluate(x + x + x + x) 			-> 4*x",
			"Evaluate(x² + x²) 					-> 2*x^2",
			"Evaluate(x² + x² + y + y + 5 - 4) 	-> 2*x^2+2*y+1",
	})
	public void testRestrictedFunctionOutput(String expression, String expectedOutput) {
		GeoElement geoElement = evaluateGeoElement(expression);
		assertFalse(AlgebraItem.shouldShowBothRows(geoElement, getAlgebraSettings()));

		String actualOutput = getApp().getGeoElementValueConverter()
				.toOutputValueString(geoElement, StringTemplate.defaultTemplate);
		assertEquals(expectedOutput, actualOutput);
	}
}
