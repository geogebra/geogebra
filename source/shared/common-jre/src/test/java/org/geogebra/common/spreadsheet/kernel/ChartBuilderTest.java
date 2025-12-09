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

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChartBuilderTest extends BaseAppTestSetup {

	private KernelTabularDataAdapter tabularData;

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		tabularData = new KernelTabularDataAdapter(getApp());
		getKernel().attach(tabularData);
	}

	@Test
	public void testBoxPlot() {
		assertEquals("BoxPlot(0, 1, A1:A3)",
				ChartBuilder.getBoxPlotCommand(tabularData,
						List.of(new TabularRange(0, 0, 2, 0))));
		assertEquals("BoxPlot(0, 1, A1:A3, C1:C3, false)",
				ChartBuilder.getBoxPlotCommand(tabularData,
						List.of(new TabularRange(0, 0, 2, 0),
								new TabularRange(0, 2, 2, 2))));
		assertEquals("BoxPlot(0, 1, A1:A3, B1:B3, false)",
				ChartBuilder.getBoxPlotCommand(tabularData,
						List.of(new TabularRange(0, 0, 2, 1))));
	}
}
