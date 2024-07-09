package org.geogebra.common.gui.view.table.keyboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.dialog.handler.DefineFunctionHandler;
import org.geogebra.common.gui.view.table.ScientificDataTableController;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.junit.Test;

public class TableValuesKeyboardNavigationControllerTests extends BaseUnitTest
		implements TableValuesKeyboardNavigationControllerDelegate, TableValuesListener {

	private TableValuesView tableValuesView;
	private TableValuesKeyboardNavigationController keyboardController;
	private CellIndex focusedCell;
	private String cellContent;
	private boolean didReportInvalidCellContent;
	private boolean didReportModelChanged;
	private boolean traceEvents = false;

	@Override
	public void setup() {
		super.setup();

		Kernel kernel = getKernel();
		tableValuesView = new TableValuesView(kernel);
		tableValuesView.getTableValuesModel().registerListener(this);
		kernel.attach(tableValuesView);

		keyboardController = new TableValuesKeyboardNavigationController(tableValuesView, this);

		focusedCell = null;
		cellContent = "";
		didReportInvalidCellContent = false;
		didReportModelChanged = false;
		traceEvents = false;
	}

	private void trace(String msg) {
		if (traceEvents) {
			System.out.println(msg);
		}
	}

	private void addFunction(String label, String definition) {
		GeoFunction function = new GeoFunction(getKernel().getConstruction());
		function.setAuxiliaryObject(true);
		function.rename(LabelManager.HIDDEN_PREFIX + label);
		function.setCaption(label);
		tableValuesView.addAndShow(function);

		DefineFunctionHandler handler = new DefineFunctionHandler(getKernel());
		handler.handle(definition, function);
	}

	private String getFocusedCellContent() {
		if (focusedCell == null) {
			return null;
		}
		return tableValuesView.getTableValuesModel()
				.getCellAt(focusedCell.row, focusedCell.column).getInput();
	}

	// Not an actual test - this is just a testbed to run a certain sequence of modifications,
	// and see which listener events are created from it.
	@Test
	public void testTableValuesListenerEvents() {
		traceEvents = true;

		// select (0, 0)
		keyboardController.select(0, 0);

		// tap-select (0, 1) with non-empty placeholder cell
		// -> new data inserted in first column, editing new placeholder cell (0, 1)
		cellContent = "1";
		focusedCell = null;
		trace("select(0, 1)");
		keyboardController.select(0, 1);
		assertEquals(new CellIndex(0, 1), focusedCell);

		// hit return with non-empty placeholder cell
		// -> new data inserted in second column, editing new placeholder cell (1, 1)
		cellContent = "2";
		focusedCell = null;
		trace("RETURN");
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertEquals(new CellIndex(1, 1), focusedCell);

		// trace output:
		// select(0, 1)
		// notifyRowsAdded(0, 0) // no cellChanged(0, 0) here?
		// RETURN
		// notifyColumnAdded(1)
		// notifyColumnChanged(1)
		// notifyCellChanged(0, 1)
	}

	// Scenario 0:
	// - empty table
	// - editing allowed, adding columns allowed

	@Test
	public void testEmptyTable_EnterDataInValuesList() {
		assertTrue(tableValuesView.isEmpty());

		// select (0, 0) - editing placeholder row
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderRow());

		// arrow down in empty placeholder row
		// -> selection should not change
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(0, 0), focusedCell);

		// return in non-empty placeholder row
		// -> new data inserted in x column, editing new placeholder row
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
		assertTrue(didReportModelChanged);
		didReportModelChanged = false;

		// return in non-empty placeholder row
		// -> new data inserted in x column, editing new placeholder row
		cellContent = "2";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getRowCount());
		assertTrue(didReportModelChanged);
		didReportModelChanged = false;
	}

	@Test
	public void testEmptyTable_SelectPlaceholderColumn() {
		assertTrue(tableValuesView.isEmpty());

		// select (0, 1) - editing placeholder column
		assertTrue(keyboardController.isColumnEditable(1));
		keyboardController.select(0, 1);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
	}

	@Test
	public void testEmptyTable_EnterDataInPlaceholderColumn() {
		// select (0, 1) - editing placeholder column
		keyboardController.select(0, 1);
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow down in empty placeholder cell
		// -> selection should not change
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// return in non-empty placeholder cell
		// -> new data inserted in x column, editing new placeholder row
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 1), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
		assertTrue(didReportModelChanged);
		didReportModelChanged = false;
	}

	@Test
	public void testEmptyTable_DeleteCellInPlaceholderColumn() {
		// select (0, 0)
		keyboardController.select(0, 0);

		// arrow right in non-empty placeholder cell
		// -> new data inserted in first column, editing new placeholder cell (0, 1)
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow right in non-empty placeholder cell
		// -> new data inserted in second column, editing new placeholder cell (0, 2)
		cellContent = "2";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 2), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow left in placeholder column
		// -> new selection should be (0, 1)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// delete cell content + return
		// -> column 2 empty, new selection should be (1, 1)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertEquals(new CellIndex(1, 1), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testEmptyTable_DeleteLastColumnArrowLeft() {
		// select (0, 0)
		keyboardController.select(0, 0);

		// arrow right in non-empty placeholder cell
		// -> new data inserted in first column, editing new placeholder cell (0, 1)
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow right in non-empty placeholder cell
		// -> new data inserted in second column, editing new placeholder cell (0, 2)
		cellContent = "2";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 2), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow left in placeholder column
		// -> new selection should be (0, 1)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// delete cell content + arrow left
		// -> delete second column, new selection should be (0, 0)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
		assertEquals(new CellIndex(0, 0), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testEmptyTable_DeleteLastColumnArrowRight() {
		// select (0, 0)
		keyboardController.select(0, 0);

		// arrow right in non-empty placeholder cell
		// -> new data inserted in first column, editing new placeholder cell (0, 1)
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow right in non-empty placeholder cell
		// -> new data inserted in second column, editing new placeholder cell (0, 2)
		cellContent = "2";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 2), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow left in placeholder column
		// -> new selection should be (0, 1)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// delete cell content + arrow right
		// -> delete second column, selection should stay at (0, 1)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testEmptyTable_DeleteLastRow() {
		// select (0, 0)
		keyboardController.select(0, 0);

		// return in non-empty placeholder cell
		// -> new data inserted in (0, 0), editing new placeholder cell (1, 0)
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// return in non-empty placeholder cell
		// -> new data inserted in (1, 0), editing new placeholder cell (2, 0)
		cellContent = "2";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertEquals(new CellIndex(2, 0), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(2, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow up in placeholder row
		// -> new selection should be (1, 0)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_UP);
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertFalse(keyboardController.isEditingPlaceholderRow());
		assertEquals(2, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// delete cell content + return
		// -> row deleted, selection should stay at (1, 0), editing placeholder row
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testEmptyTable_ArrowRightTapArrowRight() {
		// select (0, 0)
		keyboardController.select(0, 0);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// select (0, 0)
		focusedCell = null;
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);
	}

	@Test
	public void testEmptyTable_PlaceholderRowCreatedOnTapSelect() {
		// select (0, 0)
		keyboardController.select(0, 0);

		// arrow right in non-empty placeholder cell
		// -> new data inserted in first column, editing new placeholder cell (0, 1)
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// tap-select (1, 1) with non-empty placeholder cell
		// -> new data inserted in second column, editing new placeholder cell (1, 1)
		cellContent = "2";
		focusedCell = null;
		keyboardController.select(1, 1);
		assertEquals(new CellIndex(1, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testEmptyTable_PlaceholderColumnCreatedOnTapSelect() {
		// select (0, 0)
		keyboardController.select(0, 0);

		// tap-select (0, 1) with non-empty placeholder cell
		// -> new data inserted in first column, editing new placeholder cell (0, 1)
		cellContent = "1";
		focusedCell = null;
		keyboardController.select(0, 1);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testEmptyTable_EnterInvalidDataInPlaceholderColumn() {
		// select (0, 1) - editing placeholder column
		keyboardController.select(0, 1);
		assertEquals(new CellIndex(0, 1), focusedCell);

		// return in non-empty placeholder cell
		// -> invalid data reported, editing new placeholder row
		cellContent = "a";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertTrue(didReportInvalidCellContent);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 1), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testEmptyTable_Deselect() {
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		keyboardController.deselect();
		assertNull(focusedCell);
	}

	// Scenario 1:
	// - just x ("values") column [0..1],
	// - editing allowed, adding columns allowed

	@Test
	public void testValuesList() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		assertEquals(3, keyboardController.getNavigableRowsCount());
		assertEquals(2, keyboardController.getNavigableColumnsCount());
	}

	@Test
	public void testValuesList_ArrowDownInEmptyPlaceholderRow() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		didReportModelChanged = false;

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow down
		// -> new selection should be (1, 0)
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertEquals(new CellIndex(1, 0), focusedCell);

		// arrow down
		// -> new selection should be (2, 0) - editing placeholder row
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);

		// arrow down in empty placeholder row
		// -> selection should not change
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);
	}

	@Test
	public void testValuesList_ArrowDownInNonEmptyPlaceholderRow() throws Exception {
		tableValuesView.setValues(0, 1, 1);

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow down
		// -> new selection should be (1, 0)
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertEquals(new CellIndex(1, 0), focusedCell);

		// arrow down
		// -> new selection should be (2, 0) - editing placeholder row
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);

		// arrow down in non-empty placeholder row
		// -> new data inserted, editing new placeholder row
		cellContent = "2";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(3, 0), focusedCell);
	}

	@Test
	public void testValuesList_ArrowUpInEmptyPlaceholderRow() throws Exception {
		tableValuesView.setValues(0, 1, 1);

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow down
		// -> new selection should be (1, 0)
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertEquals(new CellIndex(1, 0), focusedCell);

		// arrow down
		// -> new selection should be (2, 0) - editing placeholder row
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);

		// arrow up in empty placeholder row
		// -> no data inserted, selection should be (1, 0)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_UP);
		assertFalse(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getRowCount());
	}

	@Test
	public void testValuesList_ArrowUpInNonEmptyPlaceholderRow() throws Exception {
		tableValuesView.setValues(0, 1, 1);

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow down
		// -> new selection should be (1, 0)
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertEquals(new CellIndex(1, 0), focusedCell);

		// arrow down
		// -> new selection should be (2, 0) - editing placeholder row
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);

		// arrow up in non-empty placeholder row
		// -> new data inserted, selection should be (1, 0)
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_UP);
		assertFalse(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertEquals(3, tableValuesView.getTableValuesModel().getRowCount());
	}

	@Test
	public void testValuesList_ArrowRightInEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow right in empty placeholder column
		// -> selection should not change
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);
	}

	@Test
	public void testValuesList_ArrowRightInNonEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow right in non-empty placeholder column
		// -> new data inserted, editing new placeholder column
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 2), focusedCell);
	}

	@Test
	public void testValuesList_ArrowLeftInFirstColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow left
		// -> selection should not change
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
		assertEquals(new CellIndex(0, 0), focusedCell);
	}

	@Test
	public void testValuesList_ArrowLeftInEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow left in empty placeholder column
		// -> no data inserted, selection should be (0, 0)
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 0), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testValuesList_ArrowLeftInNonEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow left in non-empty placeholder column
		// -> new data inserted, selection should be (0, 0)
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 0), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	// Scenario 2:
	// - x ("values") column [-2..2],
	// - f(x) column,
	// - editing allowed, adding columns allowed

	@Test
	public void testFunctionColumn() throws Exception {
		tableValuesView.setValues(-2, 2, 1);
		addFunction("f", "x");
		assertEquals(6, keyboardController.getNavigableRowsCount());
		assertEquals(3, keyboardController.getNavigableColumnsCount());
	}

	@Test
	public void testFunctionColumn_ArrowDownInEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(-2, 2, 1);
		addFunction("f", "x");

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> skip f(x) column, editing placeholder column
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 2), focusedCell);

		// arrow down in empty placeholder cell
		// -> move down in placeholder column, no new data
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(1, 2), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow left
		// -> back in x column, no new data
		cellContent = "";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_LEFT);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testFunctionColumn_ArrowDownInNonEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(-2, 2, 1);
		addFunction("f", "x");

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> skip f(x) column, editing placeholder column
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 2), focusedCell);

		// arrow down in non-empty placeholder column
		// -> insert data, move down in now no-longer-placeholder column
		cellContent = "1";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(3, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals(new CellIndex(1, 2), focusedCell);
	}

	// Scenario 3 (SciCalc):
	// - x ("values") column,
	// - f(x), g(x) columns,
	// - editing allowed, adding columns not allowed

	@Test
	public void testSciCalc() {
		ScientificDataTableController scientificDataTableController =
				new ScientificDataTableController(getKernel());
		scientificDataTableController.setup(tableValuesView);
		scientificDataTableController.defineFunctions("x", "x^2");
		assertEquals(6, keyboardController.getNavigableRowsCount());
		assertEquals(3, keyboardController.getNavigableColumnsCount());
	}

	@Test
	public void testSciCalc_ArrowRight() {
		ScientificDataTableController scientificDataTableController =
				new ScientificDataTableController(getKernel());
		scientificDataTableController.setup(tableValuesView);
		scientificDataTableController.defineFunctions("x", "x^2");

		// select (0, 0)
		keyboardController.select(0, 0);

		// arrow right
		// -> selection should not change
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_RIGHT);
		assertEquals(new CellIndex(0, 0), focusedCell);
	}

	@Test
	public void testSciCalc_ArrowDown() {
		ScientificDataTableController scientificDataTableController =
				new ScientificDataTableController(getKernel());
		scientificDataTableController.setup(tableValuesView);
		scientificDataTableController.defineFunctions("x", "x^2");
		didReportModelChanged = false;

		// select (0, 0)
		keyboardController.select(0, 0);

		// arrow down 4 times
		// -> selection should be (4, 0)
		for (int i = 0; i < 4; i++) {
			cellContent = String.valueOf(i - 2); // [-2...2]
			focusedCell = null;
			keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
			assertFalse(didReportModelChanged);
		}
		assertEquals(new CellIndex(4, 0), focusedCell);

		// arrow down once more
		// -> selection should be (5, 0) - editing placeholder row
		cellContent = getFocusedCellContent();
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(5, 0), focusedCell);

		// return (=arrow down) in non-empty placeholder row
		// -> new data inserted, editing new placeholder row
		cellContent = "3";
		focusedCell = null;
		keyboardController.keyPressed(TableValuesKeyboardNavigationController.Key.RETURN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(6, 0), focusedCell);
	}

	// Scenario 4:
	// - just x ("values") column [0..3],
	// - editing not allowed

	@Test
	public void testNonEditableValuesList() throws Exception {
		keyboardController.setReadonly(true);
		tableValuesView.setValues(0, 3, 1);

		assertEquals(1, keyboardController.getNavigableColumnsCount());
		assertEquals(4, keyboardController.getNavigableRowsCount());

		keyboardController.select(0, 0);
		assertNull(focusedCell);
	}

	// TableValuesKeyboardControllerDelegate

	@Override
	public void focusCell(int row, int column) {
		focusedCell = row >= 0 && column >= 0 ? new CellIndex(row, column) : null;
	}

	@Override
	public void refocusCell(int row, int column) {
		focusedCell = row >= 0 && column >= 0 ? new CellIndex(row, column) : null;
	}

	@Override
	public void unfocusCell(int row, int column, boolean isTransferringFocus) {
		focusedCell = null;
	}

	@Override
	public String getCellEditorContent(int row, int column) {
		return cellContent;
	}

	@Override
	public void invalidCellContentDetected(int row, int column) {
		didReportInvalidCellContent = true;
	}

	//  TableValuesListener

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		trace("notifyColumnRemoved(" + column + ")");
		didReportModelChanged = true;
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		trace("notifyColumnChanged(" + column + ")");
		didReportModelChanged = true;
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		trace("notifyColumnAdded(" + column + ")");
		didReportModelChanged = true;
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		trace("notifyColumnHeaderChanged(" + column + ")");
		didReportModelChanged = true;
	}

	@Override
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		trace("notifyCellChanged(" + row + ", " + column + ")");
		didReportModelChanged = true;
	}

	@Override
	public void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow) {
		trace("notifyRowsRemoved(" + firstRow + ", " + lastRow + ")");
		didReportModelChanged = true;
	}

	@Override
	public void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow) {
		trace("notifyRowsAdded(" + firstRow + ", " + lastRow + ")");
		didReportModelChanged = true;
	}

	@Override
	public void notifyRowChanged(TableValuesModel model, int row) {
		trace("notifyRowChanged(" + row + ")");
		didReportModelChanged = true;
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		trace("notifyDatasetChanged");
		didReportModelChanged = true;
	}
}
