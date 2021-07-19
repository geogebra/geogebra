package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CellFormatText {
	private static AppDNoGui app;

	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
	}

	@Test
	public void tableTextRowsColumnsMatch() {
		t("A1=1");
		t("A2=1");
		t("B1=1");
		t("B2=1");
		CellRangeProcessor cp = new CellRangeProcessor(null, app);
		GeoElementND table = cp.createTableText(0, 1, 0, 1, false, false);
		Assert.assertEquals("TableText({{A1, B1}, {A2, B2}}, \"|_ll\")",
				table.getDefinition(StringTemplate.defaultTemplate));
	}

	private void t(String string) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(string,
				false);
	}
}
