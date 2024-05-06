package org.geogebra.common.gui.view.table.keyboard;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.ScientificDataTableController;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.Kernel;
import org.junit.Test;

public class TableValuesKeyboardControllerTests extends BaseUnitTest implements TableValuesKeyboardControllerDelegate {

    private TableValuesView tableValuesView;
    private TableValuesKeyboardController keyboardController;
    private CellIndex focusedCell;
    private boolean didRequestHideKeyboard;

    @Override
    public void setup() {
        super.setup();

        Kernel kernel = getKernel();
        tableValuesView = new TableValuesView(kernel);
        kernel.attach(tableValuesView);

        keyboardController = new TableValuesKeyboardController(tableValuesView);
        keyboardController.delegate = this;

        focusedCell = null;
        didRequestHideKeyboard = false;
    }

    @Test
    public void testEditableValuesList_EditingLastRow() throws InvalidValuesException {
        // 1 column by 2 rows (0, 1), adding columns allowed
        tableValuesView.setValues(0, 1, 1);

        // select (0, 0)
        keyboardController.select(0, 0);
        assertEquals(focusedCell, new CellIndex(0, 0));

        // arrow down -> new selection should be (1, 0)
        keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
        assertEquals(focusedCell, new CellIndex(1, 0));

        // arrow down -> new selection should be (2, 0) - editing temporary row
        keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
        assertEquals(focusedCell, new CellIndex(2, 0));

        // arrow down -> selection should not change
        keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
        assertEquals(focusedCell, new CellIndex(2, 0));
    }

    @Test
    public void testEditableValuesList_EditingLastColumn() throws InvalidValuesException {
        // 1 column by 2 rows (0, 1), adding columns allowed
        tableValuesView.setValues(0, 1, 1);

        // select (0, 0)
        keyboardController.select(0, 0);
        assertEquals(focusedCell, new CellIndex(0, 0));

        // arrow right -> editing temporary column
        keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
        assertEquals(focusedCell, new CellIndex(0, 1));

        // arrow right -> selection should not change
        keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
        assertEquals(focusedCell, new CellIndex(0, 1));

        // arrow down -> move down in temporary column
        keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
        assertEquals(focusedCell, new CellIndex(1, 1));
    }

    @Test
    public void testNonEditableValuesListRight() throws InvalidValuesException {
        // 3 columns (x, f(x), g(x)) by 5 rows (x: -2, -1, 0, 1, 2), adding columns not allowed
        ScientificDataTableController scientificDataTableController =
                new ScientificDataTableController(getKernel());
        scientificDataTableController.setup(tableValuesView);
        scientificDataTableController.defineFunctions("x", "x^2");

        // select (0, 0)
        keyboardController.select(0, 0);

        // arrow right -> selection should not change
        keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
        assertEquals(focusedCell, new CellIndex(0, 0));
    }

    // TableValuesKeyboardControllerDelegate

    @Override
    public void focusCell(int row, int column) {
        focusedCell = row >= 0 && column >= 0 ? new CellIndex(row, column) : null;
    }

    @Override
    public void commitCell(int row, int column) {
    }

    @Override
    public void hideKeyboard() {
        didRequestHideKeyboard = true;
    }
}
