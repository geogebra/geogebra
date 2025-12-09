/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.exam;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.AddLabel;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.AlgebraContextMenuItem;
import org.geogebra.common.exam.restrictions.MmsExamRestrictions;
import org.geogebra.common.exam.restrictions.mms.MmsAlgebraOutputFilter;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.gui.view.algebra.ProtectiveGeoElementValueConverter;
import org.geogebra.common.gui.view.algebra.SuggestionIntersectExtremum;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.geogebra.test.annotation.Issue;
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
				List.of(CreateTableValues, AddLabel, DuplicateInput, Delete, Settings),
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
		assertEquals(definition,
				converter.toLabelAndDescription(barchart, StringTemplate.defaultTemplate));
	}

	@Test
	@MockedCasValues({
			"Evaluate(2x) 			-> 2*x",
			"Evaluate(2 (x + 6)) 	-> 2*x+12",
	})
	public void testRestrictedFunctionOutput() {
		evaluateGeoElement("f(x) = 2x");
		assertFalse(new MmsAlgebraOutputFilter(null)
				.isAllowed(evaluateGeoElement("g(x) = f(x + 6)")));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Integral(x^2)",
			"IntegralSymbolic(x^2)",
			"Derivative(x^2)",
			"LeftSide(x + 2 = 3x + 1)",
			"RightSide(x + 3 = 3x + 1)",
			"Expand((2x - 1)^2 + 2x + 3)",
	})
	@MockedCasValues({
			"Integral(x²) 				-> 1/3*x^3+arbconst(1+33)",
			"IntegralSymbolic(x²) 		-> 1/3*x^3+arbconst(2+66)",
			"Derivative(x²) 			-> 2*x",
			"LeftSide(x + 2 = 3x + 1) 	-> x+2",
			"RightSide(x + 3 = 3x + 1) 	-> 3*x+1",
			"Expand((2x - 1)² + 2x + 3) -> 4*x^2-2*x+4",
	})
	public void testUnrestrictedFunctionOutputs(String expression) {
		assertTrue(new MmsAlgebraOutputFilter(null).isAllowed(evaluateGeoElement(expression)));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Union({1}, {2})",
			"Quartile1({1, 2, 3})",
			"Q1({1, 2, 3})",
			"CSolve(x^2 = 0)",
			"CSolutions(x^2 = 0)",
	})
	@MockedCasValues({
			"Union({1}, {2}) 		-> {1,2}",
			"Quartile1({1, 2, 3}) 	-> 1.0",
			"Round(1, 2) 			-> 1.0",
			"CSolve(x² = 0) 		-> {x=0}",
			"CSolutions(x² = 0) 	-> {0}",
	})
	public void testRestrictedCommands(String expression) {
		assertNull(evaluate(expression));
		assertThat(errorAccumulator.getErrorsSinceReset(), containsString("Unknown command"));
		errorAccumulator.resetError();
	}

	@SuppressWarnings("checkstyle:LineLength")
	@ParameterizedTest
	@ValueSource(strings = {
			"BinomialDist(10, 0.5)",
			"BinomialDist(10, 0.5, true)",
			"Invert(2x)",
			"Length(Vector((1, 1)))",
			"Length((1, 1))",
			"Length(\"text\")",
			"Length(2x, -5, 5)",
			"Length(2x, (-1, -1), (1, 1))",
			"Length(x^2, -5, 5)",
			"Length(x^2, (-1, -1), (1, 1))",
			"Length(2a, a, 0, 1)",
			"Normal(2, 0.5, 1, true)",
			"Normal(2, 0.5, x)",
			"Normal(2, 0.5, x, true)",
			"Product(2a, a, 1, 5)",
			"SampleSD({1, 2, 3}, {1, 2, 1})",
			"SigmaXX({(1, 1), (2, 2), (3, 3)})",
			"SigmaXX({1, 2, 3}, {1, 2, 1})",
			"SigmaXY({(1, 1), (2, 2)})",
			"stdev({1, 2, 3}, {1, 2, 1})",
			"Sum(2a, a, -2, 5)",
	})
	@MockedCasValues({
			"BinomialDist(10, 0.5) 																				-> 1",
			"Round(1, 13) 																						-> 1.0",
			"BinomialDist(10, 0.5, true) 																		-> ?",
			"Numeric(BinomialDist(10, 0.5, true)) 																-> ?",
			"Invert(2x) 																						-> 1/2*x",
			"Vector((1, 1)) 																					-> ggbvect(1,1)",
			"Length(Vector((1, 1))) 																			-> √2",
			"Length((1, 1)) 																					-> √2",
			"Round(sqrt(2), 13) 																				-> 1.414213562373",
			"Length(text) 																						-> 4",
			"Length(2x, -5, 5) 																					-> 10*√5",
			"Round(10sqrt(5), 13) 																				-> 22.360679775",
			"Length(2x, (-1, -1), (1, 1)) 																		-> ?",
			"Numeric(Length(2x, (-1, -1), (1, 1))) 																-> ?",
			"Length(x², -5, 5) 																					-> -1/4*(-ln(√101+10)-10*√101)+1/4*(-ln(√101-10)+10*√101)",
			"Round(-1 / 4 (-ln(sqrt(101) + 10) - 10sqrt(101)) + 1 / 4 (-ln(sqrt(101) - 10) + 10sqrt(101)), 13) 	-> 51.74848958075",
			"Length(x², (-1, -1), (1, 1)) 																		-> ?",
			"Numeric(Length(x², (-1, -1), (1, 1))) 																-> ?",
			"Length(2a, a, 0, 1) 																				-> √5",
			"Round(sqrt(5), 13) 																				-> 2.2360679775",
			"Normal(2, 0.5, 1, true) 																			-> (erf(-√2)+1)/2",
			"Round((erf(-sqrt(2)) + 1) / 2, 13) 																-> 0.0227501319482",
			"Normal(2, 0.5, x) 																					-> (erf(x*√2-2*√2)+1)/2",
			"Normal(2, 0.5, x, true) 																			-> (erf(x*√2-2*√2)+1)/2",
			"Product(2a, a, 1, 5) 																				-> 3840",
			"Round(3840, 13) 																					-> 3840.0",
			"stdev({1, 2, 3}, {1, 2, 1}) 																		-> √6/3",
			"Round(sqrt(6) / 3, 13) 																			-> 0.8164965809277",
			"SigmaXX({(1, 1), (2, 2), (3, 3)}) 																	-> 14",
			"Round(14, 13) 																						-> 14.0",
			"SigmaXX({1, 2, 3}, {1, 2, 1}) 																		-> 18",
			"Round(18, 13) 																						-> 18.0",
			"SigmaXY({(1, 1), (2, 2)}) 																			-> 5",
			"Round(5, 13) 																						-> 5.0",
			"Sum(2a, a, -2, 5) 																					-> 24",
			"Round(24, 13) 																						-> 24.0",
	})
	public void testRestrictedCommandArguments(String command) {
		assertNull(evaluate(command));
	}

	@SuppressWarnings("checkstyle:LineLength")
	@ParameterizedTest
	@ValueSource(strings = {
			"BinomialDist(10, 0.2, 3, true)",
			"BinomialDist(10, 0.2, {1, 2, 3})",
			"Invert({{1, 2}, {3, 4}})",
			"Length({1, 2, 3})",
			"Normal(2, 0.5, 1)",
			"Normal(2, 0.5, 3, 6)",
			"Product({1, 2, x})",
			"Product({1, 2, 3, 4}, 2)",
			"Product({1, 2, 3}, {1, 2, 1})",
			"SampleSD({1, 2, 3})",
			"SigmaXX({1, 2, 3})",
			"SigmaXY({1, 2, 3}, {4, 5, 6})",
			"stdev({1, 2, 3})",
			"Sum({1, 2, 3})",
			"Sum({1, 2, 3}, 2)",
			"Sum({1, 2, 3}, {1, 2, 1})",
	})
	@MockedCasValues({
			"BinomialDist(10, 0.2, 3, true) 								-> 8585216/9765625",
			"Round(8585216 / 9765625, 13) 									-> 0.8791261184",
			"BinomialDist(10, 0.2, {1, 2, 3}) 								-> 1507328/1953125",
			"Round(1507328 / 1953125, 13) 									-> 0.771751936",
			"Invert({{1, 2}, {3, 4}}) 										-> {{-2,1},{3/2,-1/2}}",
			"Length({1, 2, 3}) 												-> 3",
			"Round(3, 13) 													-> 3.0",
			"Normal(2, 0.5, 1) 												-> (erf(-√2)+1)/2",
			"Round((erf(-sqrt(2)) + 1) / 2, 13) 							-> 0.0227501319482",
			"Normal(2, 0.5, 3, 6) 											-> (erf(-√2)+1)/2-(erf(-4*√2)+1)/2",
			"Round((erf(-sqrt(2)) + 1) / 2 - (erf(-4 sqrt(2)) + 1) / 2, 13) -> 0.0227501319482",
			"Product({1, 2, x}) 											-> 2*x",
			"Product({1, 2, 3, 4}, 2) 										-> 2",
			"Round(2, 13) 													-> 2.0",
			"Product({1, 2, 3}, {1, 2, 1}) 									-> 12",
			"Round(12, 13) 													-> 12.0",
			"stdev({1, 2, 3}) 												-> 1",
			"Round(1, 13) 													-> 1.0",
			"SigmaXX({1, 2, 3}) 											-> 14",
			"Round(14, 13) 													-> 14.0",
			"SigmaXY({1, 2, 3}, {4, 5, 6}) 									-> 32",
			"Round(32, 13) 													-> 32.0",
			"Sum({1, 2, 3}) 												-> 6",
			"Round(6, 13) 													-> 6.0",
			"Sum({1, 2, 3}, 2) 												-> 3",
			"Sum({1, 2, 3}, {1, 2, 1}) 										-> 8",
			"Round(8, 13) 													-> 8.0",
	})
	public void testUnrestrictedCommandArguments(String command) {
		assertNotNull(evaluate(command));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Solve(x^2 = 0)",
			"Solutions(x^2 = 0)",
			"NSolve(x^2 = 0)",
			"NSolutions(x^2 = 0)",
	})
	@MockedCasValues({
			"Solve(x² = 0, x) 	-> {x=0}",
			"NSolve(x² = 0) 	-> {x=0.0}",
			"Solve(x² = 0) 		-> {x=0}",
			"Solutions(x² = 0) 	-> {0}",
			"NSolutions(x² = 0) -> {0.0}",
	})
	public void testUnrestrictedCommands(String command) {
		assertNotNull(evaluate(command));
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

	@Test
	@MockedCasValues({
			"Evaluate({{1, 2}, {3, 4}}) 	-> {{1,2},{3,4}}",
			"Evaluate({{1, 2}, {3, 4}} + 5) -> {poly1[1,7],poly1[3,9]}",
	})
	public void testAllowedMatrixVariableOperation() {
		evaluate("m1 = {{1, 2}, {3, 4}}");
		assertNotNull(evaluate("m1 + 5"));
	}

	@SuppressWarnings("checkstyle:LineLengthCheck")
	@ParameterizedTest
	@CsvSource(delimiter = ';', value = {
			"BinomialDist(1, 0.5); 									Illegal number of arguments",
			"BinomialDist(1, 0.5, false); 							Illegal argument: false",
			"Invert(sin(x)); 										Illegal argument",
			"Length((1, 2)); 										Illegal argument",
			"Length((a, b));										Illegal argument",
			"Product(a^2, a, 0, 5); 								Illegal number of arguments",
			"SampleSD({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3}); 	Illegal number of arguments",
			"stdev({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3}); 	Illegal number of arguments",
			"SigmaXX({(1, 2), (3, 4)}); 							Illegal argument",
			"SigmaXY({(1, 2), (3, 4)}); 							Illegal number of arguments",
			"Sum(a^2, a, 0, 5); 									Illegal number of arguments",
			"Normal(2, 0.5, 1, true); 								Illegal argument: true",
			"Normal(2, 0.5, x, true); 								Illegal argument: x",
	})
	@MockedCasValues({
			"BinomialDist(1, 0.5) 										-> 1",
			"Round(1, 2) 												-> 1.0",
			"BinomialDist(1, 0.5, false) 								-> ?",
			"Numeric(BinomialDist(1, 0.5, false)) 						-> ?",
			"Invert(sin(x)) 											-> -asin(x)+2*arbint(0)*pi+pi",
			"Length((1, 2)) 											-> √5",
			"Length((a, b)) 											-> √(ggbtmpvara^2+ggbtmpvarb^2)",
			"Round(sqrt(5), 2) 											-> 2.24",
			"Product(a², a, 0, 5) 										-> 0",
			"Round(0, 2) 												-> 0.0",
			"stdev({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3}) 			-> ?",
			"Numeric(stdev({1, 2, 3, 4, 5}, {0.2, 0.3, 0.1, 0.1, 0.3})) -> 18378519.31067*ί",
			"Round(18378519.31ί, 2) 									-> 18378519.31*ί",
			"SigmaXX({(1, 2), (3, 4)}) 									-> 10",
			"Round(10, 13) 												-> 10.0",
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
	@Issue("APPS-7193")
	@MockedCasValues({
			"Evaluate((a, b)) 	-> (ggbtmpvara,ggbtmpvarb)",
			"Length((a, b)) 	-> √(ggbtmpvara^2+ggbtmpvarb^2)",
	})
	public void testRestrictedLengthVectorArgument() {
		GeoElement geoElement = evaluateGeoElement("(a, b)");
		new LabelController().showLabel(geoElement);
		assertNull(evaluate("Length(u)"));
		assertThat(errorAccumulator.getErrorsSinceReset(), containsString("Illegal argument"));
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

	@Test
	@MockedCasValues({
			"Evaluate(1°) -> pi/180",
			"Round(π / 180, 13) -> 0.02",
			"Evaluate(π / °) -> 180",
			"Round(180, 13) -> 180",
			"Evaluate(π / π / 180) -> 1/180",
			"Numeric(Evaluate(π / π / 180)) -> 0.005",
			"Round(1 / 180, 13) -> 0.005",
			"Numeric(Evaluate(π / 180 + π / 180)) -> 0.03",
			"Evaluate(π / 180 + π / 180) -> 0.03",
			"Round(0.03, 13) -> 0.03",
			"Evaluate(sin(3°) + π / 180) -> sin(3°) + π / 180",
			"Numeric(Evaluate(sin(3°) + π / 180)) -> 0.03",
			"Numeric(Evaluate(sin(π / 180))) -> 0.005",
			"Round(sin(3°) + π / 180, 13) -> 0.03",
			"Round(0.005, 13) -> 0.005",
			"Round(sin(π / 180), 13) -> 0.005",
			"Evaluate(sin(π / 180)) -> sin(pi / 180)"
	})
	public void angleComputationsRadians() {
		getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
		evaluate("a=1 deg");
		MmsAlgebraOutputFilter filter = new MmsAlgebraOutputFilter(null);
		assertFalse(filter.isAllowed(evaluate("pi/deg")[0]));
		assertTrue(filter.isAllowed(evaluate("pi/a")[0]));
		assertTrue(filter.isAllowed(evaluate("a+a")[0]));
		assertTrue(filter.isAllowed(evaluate("sin(3deg)+a")[0]));
		assertTrue(filter.isAllowed(evaluate("sin(a)")[0]));
	}

	@Test
	public void testOneVariableStatistics() throws InvalidValuesException {
		TableValuesView tableValuesView = setupTableValues();
		assertEquals(List.of(
				"Sum",
				"Sum of squares",
				"Sample Standard Deviation",
				"Cardinality"
		), tableValuesView.getStatistics1Var(1).stream()
				.map(StatisticGroup::getHeading).collect(Collectors.toList()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Sum({1, 2, 3})",
			"SigmaXX({1, 2, 3})",
			"SampleSD({1, 2, 3})",
			"Length({1, 2, 3})",
	})
	@MockedCasValues({
			"Sum({1, 2, 3}) 	-> 6",
			"Round(6, 13) 		-> 6.0",
			"SigmaXX({1, 2, 3}) -> 14",
			"Round(14, 13) 		-> 14.0",
			"stdev({1, 2, 3}) 	-> 1",
			"Round(1, 13) 		-> 1.0",
			"Length({1, 2, 3}) 	-> 3",
			"Round(3, 13) 		-> 3.0",
	})
	public void testUnrestrictedCommandsNeededForOneVariableStatistics(String command) {
		assertNotNull(evaluate(command));
	}

	@Test
	public void testTwoVariableStatistics() throws InvalidValuesException {
		TableValuesView tableValuesView = setupTableValues();
		assertEquals(List.of(
				// x
				"Sum",
				"Sum of squares",
				"Sample Standard Deviation",
				// y
				"Sum",
				"Sum of squares",
				"Sample Standard Deviation",
				// xy
				"Sum of products",
				"Cardinality"
		), tableValuesView.getStatistics2Var(1).stream()
				.map(StatisticGroup::getHeading).collect(Collectors.toList()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"RemoveUndefined({1, 2, 3})",
			"x((1, 2))",
			"y((1, 2))",
			"Sum({1, 2, 3})",
			"SigmaXX({1, 2, 3})",
			"SampleSD({1, 2, 3})",
			"SigmaXY({1, 2, 3}, {4, 5, 6})",
			"Length({1, 2, 3})",
	})
	@MockedCasValues({
			"RemoveUndefined({1, 2, 3}) 	-> {1,2,3}",
			"Evaluate(1) 					-> 1",
			"Round(1, 13) 					-> 1.0",
			"Evaluate(2) 					-> 2",
			"Round(2, 13) 					-> 2.0",
			"Sum({1, 2, 3}) 				-> 6",
			"Round(6, 13) 					-> 6.0",
			"SigmaXX({1, 2, 3}) 			-> 14",
			"Round(14, 13) 					-> 14.0",
			"stdev({1, 2, 3}) 				-> 1",
			"SigmaXY({1, 2, 3}, {4, 5, 6}) 	-> 32",
			"Round(32, 13) 					-> 32.0",
			"Length({1, 2, 3}) 				-> 3",
			"Round(3, 13) 					-> 3.0",
	})
	public void testUnrestrictedCommandsNeededForTwoVariableStatistics(String command) {
		assertNotNull(evaluate(command));
	}

	@Test
	@MockedCasValues({"Evaluate(x² - 2) -> x^2-2"})
	public void testNoSpecialPointsOptionInAlgebraContextMenu() {
		GeoElement geoElement = evaluateGeoElement("x^2 - 2");
		List<AlgebraContextMenuItem> contextMenuItems = contextMenuFactory.makeAlgebraContextMenu(
				geoElement, getAlgebraProcessor(), CAS_APPCODE, getAlgebraSettings());
		assertFalse(contextMenuItems.contains(AlgebraContextMenuItem.SpecialPoints));
	}

	@Test
	@MockedCasValues({
			"Evaluate(x² - 2) 		-> x^2-2",
			"Intersect(f, xAxis) 	-> {(√2,0),(-√2,0)}",
			"Extremum(f) 			-> {(0,-2)}",
			"Intersect(f, yAxis) 	-> {(0,-2)}",
	})
	public void testNoSpecialPoints() {
		GeoElement geoElement = evaluateGeoElement("x^2 - 2");
		Objects.requireNonNull(SuggestionIntersectExtremum.get(geoElement)).execute(geoElement);
		assertEquals(0, getKernel().getConstructionStep());
	}

	@Test
	public void testSpreadsheetDisabled() {
		assertFalse(getApp().isSpreadsheetEnabled());
	}

	private TableValuesView setupTableValues() throws InvalidValuesException {
		TableValuesView tableValuesView = new TableValuesView(getKernel());
		tableValuesView.setValues(0, 5, 1);
		GeoList geo = new GeoList(getKernel().getConstruction());
		for (int i = 0; i < 3; i++) {
			geo.add(new GeoNumeric(getKernel().getConstruction(), 1));
		}
		geo.setTableColumn(1);
		tableValuesView.add(geo);
		return tableValuesView;
	}
}
