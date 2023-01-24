package org.geogebra.web.full.gui.view.table;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.gui.dialog.handler.DefineFunctionHandler;
import org.geogebra.common.gui.view.table.ScientificEvaluatables;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class ScientificEvaluatablesTest {
	private AppW app;
	protected TableValuesView view;
	protected TableValuesModel model;

	@Before
	public void setUp() {
		app = AppMocker.mockScientific();
		view = new TableValuesView(app.kernel);
		app.kernel.attach(view);
		model = view.getTableValuesModel();
		view.clearView();
		ScientificEvaluatables evaluatables =
				new ScientificEvaluatables(app.kernel.getConstruction());
		evaluatables.addToTableOfValues(view);
		app.getUndoManager().storeUndoInfo();
	}

	@Test
	public void testInitialSetup() {
		tableShouldContain("?", "?");
	}

	private void tableShouldContain(String fBody, String gBody) {
		functionShouldBe("f(x) = " + fBody, f());
		functionShouldBe("g(x) = " + gBody, g());
	}

	private GeoEvaluatable g() {
		return view.getEvaluatable(2);
	}

	private GeoEvaluatable f() {
		return view.getEvaluatable(1);
	}

	@Test
	public void testFirstChange() {
		changeColumns("x", "2x");
		tableShouldContain("x", "2x");
	}

	private void changeColumns(String fBody, String gBody) {
		DefineFunctionHandler handler = new DefineFunctionHandler(app);
		handler.handle(fBody, f());
		handler.handle(gBody, g());

	}

	private void functionShouldBe(String expected, GeoEvaluatable geoEvaluatable) {
		assertEquals(expected, geoEvaluatable.toString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testChangeAndUndo() {
		changeColumns("x", "2x");
		tableShouldContain("x", "2x");
		app.getUndoManager().undo();
		tableShouldContain("?", "?");
	}
}