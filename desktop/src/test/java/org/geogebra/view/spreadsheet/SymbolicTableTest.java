package org.geogebra.view.spreadsheet;

import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SymbolicTableTest {
	private App app;
	private AlgebraProcessor ap;
	private TableValuesView tv;

	@Before
	public void setup() {
		app = AlgebraTest.createApp();
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		ap = app.getKernel().getAlgebraProcessor();
		app.getKernel().getGeoGebraCAS().evaluateGeoGebraCAS("1+1", null,
				StringTemplate.defaultTemplate, app.getKernel());
		tv = new TableValuesView(app.getKernel());
		app.getKernel().attach(tv);
	}

	@Test
	public void singleVariableFunctionShouldWorkWithTV() {
		GeoElement f = ap.processAlgebraCommand("f:x+1", false)[0]
				.toGeoElement();
		tv.showColumn((GeoEvaluatable) f);
		Assert.assertEquals(2, tv.getTableValuesModel().getColumnCount());
	}

	@Test
	public void multiVariableFunctionShouldNotWorkWithTV() {
		GeoElement f = ap.processAlgebraCommand("f:x+y", false)[0]
				.toGeoElement();
		tv.showColumn((GeoEvaluatable) f);
		Assert.assertEquals(1, tv.getTableValuesModel().getColumnCount());
	}
}
