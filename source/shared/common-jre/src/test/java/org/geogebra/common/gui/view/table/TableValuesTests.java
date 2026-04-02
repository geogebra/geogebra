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

package org.geogebra.common.gui.view.table;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class TableValuesTests extends BaseAppTestSetup {
	@Test
	@Issue("APPS-7435")
	void setPointsVisibleWithStaleColumnIndexDoesNotCrash() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues(
				"x = {1, 2, 3}", "y_1 = {2, 3, 4}", "f(x) = x^2");
		TableValuesPointsImpl tableValuesPoints = TableValuesPointsImpl.create(getKernel(),
				getKernel().getConstruction(), tableValues);

		// Removes the y₁ column, leaving the x and f(x) columns
		tableValues.hideColumn(tableValues.getEvaluatable(1));

		// Columns shift, there is no third column anymore.
		assertNull(tableValuesPoints.getPointListForColumn(2));
		// Checking visibility points on a non-existent column shouldn't cause a crash.
		assertDoesNotThrow(() -> tableValuesPoints.setPointsVisible(2, false));
	}
}
