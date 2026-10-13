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

package org.geogebra.common.gui.view.table.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests that undefined regressions do not expose uncomputed coefficients as valid statistics. */
@Issue("APPS-7950")
class RegressionBuilderTest extends BaseAppTestSetup {

	@Test
	void undefinedFitDoesNotReportZeroCoefficients() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues table = setupTableValues("xs = {1, 1, 1, 1, 1, 1}", "ys = {1, 2, 3, 4, 5, 6}");
		RegressionSpecification linear = table.getRegressionSpecifications(1).get(0);

		assertUnavailableParameters(table.getRegression(1, linear), 2);
	}

	@Test
	void validZeroPolynomialReportsZeroCoefficients() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues table = setupTableValues("xs = {0, 1, 2, 3, 4, 5}", "ys = {0, 0, 0, 0, 0, 0}");
		RegressionSpecification linear = table.getRegressionSpecifications(1).get(0);
		List<StatisticGroup> groups = table.getRegression(1, linear);
		assertFalse(groups.isEmpty());
		assertEquals("0", groups.get(0).rows().get(1).value());
		assertEquals("0", groups.get(0).rows().get(2).value());
	}

	@Test
	void undefinedNonPolynomialFitDoesNotReportCoefficients() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues table = setupTableValues("xs = {1, 2, 3}", "ys = {-1, 1, -1}");
		RegressionSpecification exponential = table.getRegressionSpecifications(1).stream()
				.filter(spec -> spec.getLabel().equals("Exponential"))
				.findFirst()
				.orElseThrow();
		assertUnavailableParameters(table.getRegression(1, exponential), 2);
	}

	@ParameterizedTest
	@ValueSource(strings = {"Sin", "Growth", "Exponential"})
	void smallCoordinateFitRetainsModelWhenParametersAreUnavailable(String label) {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues table = setupTableValues(
				"xs = {1.2, 2.5, 4, 6.2, 6.9, 7.4} * 10^(-9)",
				"ys = {39.1, 86.6, 140, 204, 241, 246} * 10^(-6)");
		RegressionSpecification specification = table.getRegressionSpecifications(1).stream()
				.filter(spec -> spec.getLabel().equals(label))
				.findFirst()
				.orElseThrow();
		assertUnavailableParameters(
				table.getRegression(1, specification), specification.getCoeffOrdering().length());
	}

	private void assertUnavailableParameters(List<StatisticGroup> groups, int parameterCount) {
		assertFalse(groups.isEmpty(), "An unavailable fit must retain the model panel");
		List<StatisticGroup.Row> rows = groups.get(0).rows();
		assertEquals(parameterCount + 1, rows.size());
		assertTrue(rows.get(0).isLaTeX(), "The model formula must remain visible");
		for (int i = 1; i < rows.size(); i++) {
			assertEquals("?", rows.get(i).value());
			assertEquals("?", rows.get(i).clipboardValue());
		}
		assertFalse(groups.get(1).rows().isEmpty());
		for (StatisticGroup.Row row : groups.get(1).rows()) {
			assertTrue(row.value().endsWith(" = ?"));
			assertEquals("?", row.clipboardValue());
		}
	}
}
