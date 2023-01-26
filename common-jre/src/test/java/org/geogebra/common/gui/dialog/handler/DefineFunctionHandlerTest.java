package org.geogebra.common.gui.dialog.handler;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.test.UndoRedoTester;
import org.junit.Before;
import org.junit.Test;

public class DefineFunctionHandlerTest extends BaseUnitTest {
	private DefineFunctionHandler handler;
	private Construction cons;

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Before
	public void setUp() {
		handler = new DefineFunctionHandler(getApp());
		cons = getConstruction();
	}

	@Test
	public void testChangeF() {
		GeoEvaluatable f = createFunction(cons, "f");
		handler.handle("2x", f);
		functionShouldBe("f(x) = 2x", f);
	}

	private GeoEvaluatable createFunction(Construction construction, String label) {
		GeoFunction function = new GeoFunction(construction);
		function.rename(label);
		function.setAuxiliaryObject(true);
		return function;
	}

	@Test
	public void testChangeFToUndefined() {
		GeoEvaluatable f = createFunction(cons, "f");
		handler.handle("2x", f);
		functionShouldBe("f(x) = 2x", f);
		handler.handle("", f);
		functionShouldBe("f(x) = ?", f);
	}

	@Test
	public void testSimpleUndo() {
		GeoEvaluatable f = createFunction(cons, "f");
		App app = getApp();
		UndoRedoTester undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();
		app.storeUndoInfo();
		handler.handle("2x", f);
		app.storeUndoInfo();
		functionShouldBe("f(x) = ?", undoRedo.getAfterUndo("f"));
	}

	private void functionShouldBe(String expected, GeoEvaluatable geoEvaluatable) {
		assertEquals(expected, geoEvaluatable.toString(StringTemplate.defaultTemplate));
	}

}
