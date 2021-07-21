package org.geogebra.common.gui.view.table;

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
	private AlgebraProcessor ap;
	private TableValuesView tv;

	@Before
	public void setup() {
		App app = AlgebraTest.createApp();
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		ap = app.getKernel().getAlgebraProcessor();
		tv = new TableValuesView(app.getKernel());
		app.getKernel().attach(tv);
	}

	@Test
	public void singleVariableFunctionShouldWorkWithTV() {
		GeoElement f = add("f:x+1");
		tv.showColumn((GeoEvaluatable) f);
		Assert.assertEquals(2, tv.getTableValuesModel().getColumnCount());
	}

	@Test
	public void multiVariableFunctionShouldNotWorkWithTV() {
		GeoElement f = add("f:x+y");
		tv.showColumn((GeoEvaluatable) f);
		Assert.assertEquals(1, tv.getTableValuesModel().getColumnCount());
	}

	private GeoElement add(String string) {
		return ap.processAlgebraCommand(string, false)[0].toGeoElement();
	}
}
