package org.geogebra.common.gui.view.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.kernel.Kernel;
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
	private Kernel kernel;
	private AlgebraProcessor algebraProcessor;
	private TableValuesView view;
	private TableValuesProcessor processor;

	@Before
	public void setup() {
		app = AlgebraTest.createApp();
		kernel = app.getKernel();
		kernel.setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		algebraProcessor = kernel.getAlgebraProcessor();
		view = new TableValuesView(kernel);
		processor = view.getProcessor();
		kernel.attach(view);
	}

	@Test
	public void singleVariableFunctionShouldWorkWithTV() {
		GeoElement f = add("f:x+1");
		view.showColumn((GeoEvaluatable) f);
		Assert.assertEquals(2, view.getTableValuesModel().getColumnCount());
	}

	@Test
	public void multiVariableFunctionShouldNotWorkWithTV() {
		GeoElement f = add("f:x+y");
		view.showColumn((GeoEvaluatable) f);
		Assert.assertEquals(1, view.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testUndoRedo() {
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		processor.processInput("2", null, 1);
		kernel.undo();
		assertThat(view.getTableValuesModel().getValueAt(0, 0), is(1.0));
	}

	private GeoElement add(String string) {
		return algebraProcessor.processAlgebraCommand(string, false)[0].toGeoElement();
	}
}
