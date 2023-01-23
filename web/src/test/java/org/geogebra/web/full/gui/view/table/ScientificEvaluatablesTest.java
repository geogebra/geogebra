package org.geogebra.web.full.gui.view.table;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.gui.dialog.handler.DefineFunctionHandler;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class ScientificEvaluatablesTest {
	private AppW app;
	private TableValues tableValues;

	@Before
	public void setUp() {
		app = AppMocker.mockScientific();
		tableValues = ((GuiManagerW) app.getGuiManager()).getTableValuesView();
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
		return tableValues.getEvaluatable(2);
	}

	private GeoEvaluatable f() {
		return tableValues.getEvaluatable(1);
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