package org.geogebra.common.spreadsheet.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.jre.util.UtilFactoryJre;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeAll;
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
