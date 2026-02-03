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

package org.geogebra.common.spreadsheet.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSetup;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockedCasValuesExtension.class)
public class CellFreezeTest extends BaseAppTestSetup {

	private DefaultSpreadsheetCellProcessor processor;

	@BeforeEach
	public void setAppConfig() {
		setupApp(SuiteSubApp.CAS);
		AlgebraProcessor algebraProcessor = getKernel().getAlgebraProcessor();
		algebraProcessor.addGeoElementSetup(createRestrictedGeoElementVisibilitySetup(Set.of()));
		processor = new DefaultSpreadsheetCellProcessor(algebraProcessor);
		getKernel().attach(new KernelTabularDataAdapter(getApp()));
	}

	@Test
	@MockedCasValues({
			"Evaluate(1) -> 1",
			"Evaluate(2) -> 2",
			"Evaluate(3) -> 3",
			"Evaluate(55) -> 55",
			"Sum(A1:A98) -> 61",
			"Round(1, 13) -> 2",
			"Round(2, 13) -> 2",
			"Round(3, 13) -> 2",
			"Round(6, 13) -> 2",
			"Round(55, 13) -> 2",
			"Round(61, 13) -> 2",
			"Sum(CellRange(1, 0)) -> 61"
	})
	void testFreeze() {
		processor.process("1", "A1");
		processor.process("2", "A2");
		processor.process("3", "A3");
		processor.process("=Sum(A1:A98)", "A99");
		processor.process("55", "A55");
		GeoSymbolic a99 = (GeoSymbolic) lookup("A99");
		a99.updateRepaint();
		assertEquals(61, a99.evaluateDouble());
	}

	private static GeoElementSetup createRestrictedGeoElementVisibilitySetup(
			Set<VisibilityRestriction> visibilityRestrictions) {
		return geoElementND -> {
			if (VisibilityRestriction.isVisibilityRestricted(geoElementND.toGeoElement(),
					visibilityRestrictions)) {
				geoElementND.toGeoElement().setRestrictedEuclidianVisibility(true);
				return true;
			}
			return false;
		};
	}
}

