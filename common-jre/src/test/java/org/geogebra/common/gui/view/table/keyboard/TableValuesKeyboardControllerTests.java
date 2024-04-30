package org.geogebra.common.gui.view.table.keyboard;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.Kernel;
import org.junit.Test;

public class TableValuesKeyboardControllerTests extends BaseUnitTest {

	private TableValuesView tableValuesView;
	private TableValuesKeyboardController keyboardController;

	@Override
	public void setup() {
		super.setup();

		Kernel kernel = getKernel();
		tableValuesView = new TableValuesView(kernel);
		kernel.attach(tableValuesView);

		keyboardController = new TableValuesKeyboardController(tableValuesView);
	}

	@Test
	public void testValuesListDown() throws InvalidValuesException {
		tableValuesView.setValues(0, 1, 1);
		keyboardController.select(0, 0);

		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertEquals(0, keyboardController.getSelectedColumn());
		assertEquals(1, keyboardController.getSelectedRow());

		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertEquals(0, keyboardController.getSelectedColumn());
		assertEquals(2, keyboardController.getSelectedRow());

		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertEquals(0, keyboardController.getSelectedColumn());
		assertEquals(2, keyboardController.getSelectedRow());
	}
}
