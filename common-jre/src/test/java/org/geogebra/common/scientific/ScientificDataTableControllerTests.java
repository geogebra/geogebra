package org.geogebra.common.scientific;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.ScientificDataTableController;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.undo.UndoManager;
import org.junit.Before;
import org.junit.Test;

public final class ScientificDataTableControllerTests extends BaseUnitTest {

	private TableValuesView tableValuesView;
	private ScientificDataTableController controller;
	private UndoManager undoManager;

	@Before
	public void setUp() {
		Kernel kernel = getKernel();
		activateUndo();
		undoManager = kernel.getConstruction().getUndoManager();

		tableValuesView = new TableValuesView(kernel);
		kernel.attach(tableValuesView);

		controller = new ScientificDataTableController(kernel);
		controller.setup(tableValuesView);
	}

	@Test
	public void testInitialSetup() {
		assertNull(controller.getDefinitionOfF());
		assertFalse(controller.isFDefined());
		assertNull(controller.getDefinitionOfG());
		assertFalse(controller.isGDefined());
		assertEquals(0, getUndoHistorySize());
	}

	@Test
	public void testInitialSetupWithExistingFunction() {
		Kernel kernel = getKernel();
		Construction construction = kernel.getConstruction();
		construction.clearConstruction();

		// create a function "f"
		GeoFunction function = new GeoFunction(construction);
		function.setAuxiliaryObject(true);
		function.rename("f");

		// this should cause an exception / conflict
		controller = new ScientificDataTableController(kernel);
		controller.setup(tableValuesView); // setting up for the first time: no name conflict
		assertNotNull(controller.getFunctionF());
		add(LabelManager.HIDDEN_PREFIX + "f:3x");
		MyError error = assertThrows(MyError.class, () -> controller.setup(tableValuesView));
		assertEquals("NameUsed", error.getMessage());
		assertTrue(error.toString().contains("This label is already in use"));
	}

	@Test
	public void testDefineFunctions() {
		// define f
		assertTrue(controller.defineFunctions("x", null));
		assertFalse(controller.hasFDefinitionErrorOccurred());
		assertEquals("x", controller.getDefinitionOfF());
		assertEquals(1, getUndoHistorySize());

		// define g
		assertTrue(controller.defineFunctions("x", "ln(x)"));
		assertFalse(controller.hasGDefinitionErrorOccurred());
		assertEquals("ln(x)", controller.getDefinitionOfG());
		assertEquals(2, getUndoHistorySize());
	}

	@Test
	public void testRedefineF() {
		// define f
		controller.defineFunctions("x", null);
		assertEquals(1, getUndoHistorySize());

		// redefine f using a different definition
		assertTrue(controller.defineFunctions("sqrt(x)", null));
		assertFalse(controller.hasFDefinitionErrorOccurred());
		assertEquals("sqrt(x)", controller.getDefinitionOfF());
		assertEquals(2, getUndoHistorySize());

		// redefine f using the same definition, no undo point should be created
		assertTrue(controller.defineFunctions("sqrt(x)", null));
		assertFalse(controller.hasFDefinitionErrorOccurred());
		assertEquals("sqrt(x)", controller.getDefinitionOfF());
		assertEquals(2, getUndoHistorySize());
	}

	@Test
	public void testInvalidInput() {
		controller.defineFunctions("abc", null);
		assertTrue(controller.hasFDefinitionErrorOccurred());
		assertFalse(controller.hasGDefinitionErrorOccurred());
		assertEquals(0, getUndoHistorySize());
	}

	@Test
	public void testUndo() {
		assertFalse(undoManager.undoPossible());

		// define f
		assertTrue(controller.defineFunctions("x", null));
		assertEquals("x", controller.getDefinitionOfF());
		assertTrue(undoManager.undoPossible());

		// redefine f
		assertTrue(controller.defineFunctions("3*sqrt(x)", null));
		assertEquals("3sqrt(x)", controller.getDefinitionOfF());
		assertTrue(undoManager.undoPossible());

		// undo (redefine f)
		undoManager.undo();
		assertTrue(undoManager.undoPossible()); // still one more undo step in the history
		assertEquals("x", controller.getDefinitionOfF());

		// undo (define f)
		undoManager.undo();
		assertFalse(undoManager.undoPossible());
		assertNull(controller.getDefinitionOfF());
	}

	private int getUndoHistorySize() {
		return getKernel().getConstruction().getUndoManager().getHistorySize();
	}
}
