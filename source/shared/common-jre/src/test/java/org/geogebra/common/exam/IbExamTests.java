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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.algebra.SuggestionIntersectExtremum;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("checkstyle:RegexpSinglelineCheck") // Tabs in CsvSources
public class IbExamTests extends BaseExamTestSetup {

	@BeforeEach
	public void setupIbExam() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamType.IB, null);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"f'(1)",
			"f'(p)"
	})
	public void testUnrestrictedDerivatives(String expression) {
		evaluate("f(x) = x^2");
		evaluate("p = 2");

		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"f'",
			"f'(x)",
			"g = f'",
			"g(x) = f'",
			"g(x) = f'(x)"
	})
	public void testRestrictedDerivatives(String expression) {
		evaluate("f(x) = x^2");

		assertNull(evaluate(expression));
	}

	@Test
	public void testRestrictedOperationsAreFreeFromSideEffect() {
		assertAll(
				() -> assertNotNull(evaluate("f(x) = x^3")),
				() -> assertNotNull(evaluate("l1 = {x}")),
				() -> assertNull(evaluate("SetValue(l1, 1, f')")),
				() -> assertEquals(getKernel().getConstruction().lookupLabel("l1")
						.toString(StringTemplate.defaultTemplate), "l1 = {x}"));
	}

	@Test
	public void testAutoCompleteProvider() {
		assertEquals(7, autocompleteProvider.getCompletions("Sol").count());
	}

	@ParameterizedTest
	@CsvSource(delimiter = ';', value = {
			// Integral
			"Integral(x^2, 3, 5);",
			"Integral(x^2); 									Illegal number of arguments",
			"Integral(x^3, x); 									Illegal number of arguments",
			"Integral(x^3, 1, 3, true); 						Illegal number of arguments",
			// Invert
			"Invert({{1,2},{3,4}});",
			"Invert(sqrt(x)); 									Illegal argument",
			// Tangent
			"Tangent((1, 0), x^2);",
			"Tangent(1, x^2);",
			"Tangent((6, 3), x^2+y^2=1); 						Illegal argument",
			"Tangent(x^2+y^2=1, (6, 3)); 						Illegal argument",
			"Tangent(x^2+y^2=1, (x-4)^2+y^2=1); 				Illegal argument",
			"Tangent(x+y=1, x^2+y^2=1); 						Illegal argument",
			"Tangent((1,2), Curve(t,t^2,t,1,2));				Illegal argument",
			"Tangent(Curve(t,t^2,t,1,2),(1,2)); 				Illegal argument",
			"Tangent((1,2), x^4+y^4=1); 						Illegal argument",
			"Tangent(x^4+y^4=1, (1,2)); 						Illegal argument",
	})
	public void testCommandArgumentFilter(String command, String expectedError) {
		if (expectedError == null) {
			assertNotNull(evaluate(command));
		} else {
			assertNull(evaluate(command));
			assertThat(errorAccumulator.getErrorsSinceReset(), containsString(expectedError));
			errorAccumulator.resetError();
		}
	}

	@Test
	public void testNumberOfIntersectSpecialPoints() {
		GeoElement geoElement = evaluateGeoElement("sin(x)");
		Objects.requireNonNull(SuggestionIntersectExtremum.get(geoElement)).execute(geoElement);
		assertEquals(3, getKernel().getConstructionStep());
	}

	@Test
	public void testOneVariableStatistics() throws InvalidValuesException {
		TableValuesView tableValuesView = setupTableValues();
		assertEquals(List.of(
				"Mean",
				"Sum",
				"Sample Standard Deviation",
				"Population Standard Deviation",
				"Minimum",
				"Lower quartile",
				"Median",
				"Upper quartile",
				"Maximum"
		), tableValuesView.getStatistics1Var(1).stream()
				.map(StatisticGroup::getHeading).collect(Collectors.toList()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"mean({1, 2, 3})",
			"Sum({1, 2, 3})",
			"SampleSD({1, 2, 3})",
			"SD({1, 2, 3})",
			"Min({1, 2, 3})",
			"Quartile1({1, 2, 3})",
			"Median({1, 2, 3})",
			"Quartile3({1, 2, 3})",
			"Max({1, 2, 3})",
	})
	public void testUnrestrictedCommandsNeededForOneVariableStatistics(String command) {
		assertNotNull(evaluate(command));
	}

	@Test
	public void testTwoVariableStatistics() throws InvalidValuesException {
		TableValuesView tableValuesView = setupTableValues();
		assertEquals(List.of(
				// x
				"Mean",
				"Sum",
				"Sample Standard Deviation",
				"Population Standard Deviation",
				// y
				"Mean",
				"Sum",
				"Sample Standard Deviation",
				"Population Standard Deviation",
				// xy
				"Correlation Coefficient",
				// x
				"Minimum",
				"Maximum",
				// y
				"Minimum",
				"Maximum"
		), tableValuesView.getStatistics2Var(1).stream()
				.map(StatisticGroup::getHeading).collect(Collectors.toList()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"RemoveUndefined({1, 2, 3})",
			"x((1, 2))",
			"y((1, 2))",
			"mean({1, 2, 3})",
			"Sum({1, 2, 3})",
			"SampleSD({1, 2, 3})",
			"SD({1, 2, 3})",
			"CorrelationCoefficient({1, 2, 3}, {4, 5, 6})",
			"Min({1, 2, 3})",
			"Max({1, 2, 3})",
	})
	public void testUnrestrictedCommandsNeededForTwoVariableStatistics(String command) {
		assertNotNull(evaluate(command));
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
