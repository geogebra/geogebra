package org.geogebra.common.gui.view.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.UndoRedoMode;
import org.geogebra.test.TestErrorHandler;
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
		view.setTableValuePoints(new TableValuesPointsImpl(kernel, kernel.getConstruction(), view));
		processor = view.getProcessor();
		kernel.attach(view);
	}

	@Test
	public void singleVariableFunctionShouldWorkWithTV() {
		GeoElement f = add("f:x+1");
		view.showColumn((GeoEvaluatable) f);
		assertEquals(2, view.getTableValuesModel().getColumnCount());
	}

	@Test
	public void multiVariableFunctionShouldNotWorkWithTV() {
		GeoElement f = add("f:x+y");
		view.showColumn((GeoEvaluatable) f);
		assertEquals(1, view.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testUndoRedo() {
		app.setUndoRedoMode(UndoRedoMode.GUI);
		app.setUndoActive(true);
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		processor.processInput("2", null, 1);
		kernel.undo();
		assertThat(view.getTableValuesModel().getValueAt(0, 0), is(1.0));
	}

	@Test
	public void testReloadFunction() {
		app.setUndoRedoMode(UndoRedoMode.GUI);
		app.setUndoActive(true);
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		GeoSymbolic f = (GeoSymbolic) add("f:x+1");
		view.showColumn(f);
		assertArrayEquals(new String[] {"x_{1}", "f", "TableValuesPoints"},
				app.getGgbApi().getAllObjectNames());
		app.setXML(app.getXML(), true);
		assertThat(view.getTableValuesModel().getValueAt(0, 1), is(2.0));
		assertArrayEquals(new String[] {"x_{1}", "f", "TableValuesPoints"},
				app.getGgbApi().getAllObjectNames());
	}

	@Test
	public void testRedefine() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		processor.processInput("2", null, 0);
		processor.processInput("4", null, 1);
		assertEquals("List", kernel.lookupLabel("x_1").getTypeString());
		assertEquals("List", kernel.lookupLabel("y_1").getTypeString());
		kernel.getAlgebraProcessor().changeGeoElement(kernel.lookupLabel("x_1"), "{1, 3}",
				true, false, TestErrorHandler.INSTANCE, null);
		kernel.getAlgebraProcessor().changeGeoElement(kernel.lookupLabel("y_1"), "{2, 6}",
				false, false, TestErrorHandler.INSTANCE, null);
		assertEquals("List", kernel.lookupLabel("x_1").getTypeString());
		assertEquals("List", kernel.lookupLabel("y_1").getTypeString());
	}

	private GeoElement add(String string) {
		return algebraProcessor.processAlgebraCommand(string, false)[0].toGeoElement();
	}
}
