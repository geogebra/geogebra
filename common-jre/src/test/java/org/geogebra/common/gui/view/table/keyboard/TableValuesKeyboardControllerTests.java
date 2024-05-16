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

public class TableValuesKeyboardControllerTests extends BaseUnitTest
		implements TableValuesKeyboardControllerDelegate, TableValuesListener {

	private TableValuesView tableValuesView;
	private TableValuesKeyboardController keyboardController;
	private CellIndex focusedCell;
	private String cellContent;
	private boolean didReportInvalidCellContent;
	private boolean didReportModelChanged;
	private boolean didRequestHideKeyboard; // TODO

	@Override
	public void setup() {
		super.setup();

		Kernel kernel = getKernel();
		tableValuesView = new TableValuesView(kernel);
		tableValuesView.getTableValuesModel().registerListener(this);
		kernel.attach(tableValuesView);

		keyboardController = new TableValuesKeyboardController(tableValuesView, this);

		focusedCell = null;
		// reset to non-empty input by default, set to empty string to simulate an empty cell
		cellContent = "0";
		didReportInvalidCellContent = false;
		didReportModelChanged = false;
		didRequestHideKeyboard = false;
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

	// Scenario 0:
	// - empty table
	// - adding columns allowed

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
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(0, 0), focusedCell);

		// return in non-empty placeholder row
		// -> new data inserted in x column, editing new placeholder row
		cellContent = "1";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.RETURN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
		assertTrue(didReportModelChanged);
		didReportModelChanged = false;

		// return in non-empty placeholder row
		// -> new data inserted in x column, editing new placeholder row
		cellContent = "2";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.RETURN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getRowCount());
		assertTrue(didReportModelChanged);
		didReportModelChanged = false;
	}

	@Test
	public void testEmptyTable_EnterDataInPlaceholderColumn() {
		assertTrue(tableValuesView.isEmpty());

		// select (0, 1) - editing placeholder column
		assertTrue(keyboardController.isColumnEditableOrPlaceholder(1));
		keyboardController.select(0, 1);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());

		// arrow down in empty placeholder cell
		// -> selection should not change
		cellContent = "";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// return in non-empty placeholder cell
		// -> new data inserted in x column, editing new placeholder row
		cellContent = "1";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.RETURN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 1), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getRowCount());
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
		assertTrue(didReportModelChanged);
		didReportModelChanged = false;
	}

	@Test
	public void testEmptyTable_EnterInvalidDataInPlaceholderColumn() {
		// select (0, 1) - editing placeholder column
		assertTrue(keyboardController.isColumnEditableOrPlaceholder(1));
		keyboardController.select(0, 1);
		assertEquals(new CellIndex(0, 1), focusedCell);
		assertTrue(keyboardController.isEditingPlaceholderColumn());

		// return in non-empty placeholder cell
		// -> invalid data reported, editing new placeholder row
		cellContent = "a";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.RETURN);
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
	// - adding columns allowed

	@Test
	public void testValuesList() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		assertEquals(3, keyboardController.getNrOfNavigableRows());
		assertEquals(2, keyboardController.getNrOfNavigableColumns());
	}

	@Test
	public void testValuesList_ArrowDownInEmptyPlaceholderRow() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		didReportModelChanged = false;

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);
		assertFalse(didReportModelChanged);

		// arrow down
		// -> new selection should be (1, 0)
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertFalse(didReportModelChanged);

		// arrow down
		// -> new selection should be (2, 0) - editing placeholder row
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);

		// arrow down in empty placeholder row
		// -> selection should not change
		cellContent = "";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
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
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertEquals(new CellIndex(1, 0), focusedCell);

		// arrow down
		// -> new selection should be (2, 0) - editing placeholder row
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);

		// arrow down in non-empty placeholder row
		// -> new data inserted, editing new placeholder row
		cellContent = "2";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(3, 0), focusedCell);
	}

	@Test
	public void testValuesList_ArrowUpInEmptyPlaceholderRow() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		assertEquals(2, tableValuesView.getTableValuesModel().getRowCount());

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow down
		// -> new selection should be (1, 0)
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertEquals(new CellIndex(1, 0), focusedCell);

		// arrow down
		// -> new selection should be (2, 0) - editing placeholder row
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);

		// arrow up in empty placeholder row
		// -> no data inserted, selection should be (1, 0)
		cellContent = "";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_UP);
		assertFalse(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getRowCount());
	}

	@Test
	public void testValuesList_ArrowUpInNonEmptyPlaceholderRow() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		assertEquals(2, tableValuesView.getTableValuesModel().getRowCount());

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow down
		// -> new selection should be (1, 0)
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertEquals(new CellIndex(1, 0), focusedCell);

		// arrow down
		// -> new selection should be (2, 0) - editing placeholder row
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(2, 0), focusedCell);

		// arrow up in non-empty placeholder row
		// -> new data inserted, selection should be (1, 0)
		cellContent = "1";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_UP);
		assertFalse(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertEquals(3, tableValuesView.getTableValuesModel().getRowCount());
	}

	@Test
	public void testValuesList_ArrowRightInEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow right in empty placeholder column
		// -> selection should not change
		cellContent = "";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);
	}

	@Test
	public void testValuesList_ArrowRightInNonEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow right in non-empty placeholder column
		// -> new data inserted, editing new placeholder column
		cellContent = "1";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
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
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_LEFT);
		assertEquals(new CellIndex(0, 0), focusedCell);
	}

	@Test
	public void testValuesList_ArrowLeftInEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow left in empty placeholder column
		// -> no data inserted, selection should be (0, 0)
		cellContent = "";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_LEFT);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 0), focusedCell);
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testValuesList_ArrowLeftInNonEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(0, 1, 1);
		assertEquals(1, tableValuesView.getTableValuesModel().getColumnCount());

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> new selection should be (0, 1) - editing placeholder column
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 1), focusedCell);

		// arrow left in non-empty placeholder column
		// -> new data inserted, selection should be (0, 0)
		cellContent = "1";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_LEFT);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 0), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	// Scenario 2:
	// - x ("values") column [-2..2],
	// - f(x) column,
	// - adding columns allowed

	@Test
	public void testFunctionColumn() throws Exception {
		tableValuesView.setValues(-2, 2, 1);
		addFunction("f", "x");
		assertEquals(6, keyboardController.getNrOfNavigableRows());
		assertEquals(3, keyboardController.getNrOfNavigableColumns());
	}

	@Test
	public void testFunctionColumn_ArrowDownInEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(-2, 2, 1);
		addFunction("f", "x");
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> skip f(x) column, editing placeholder column
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 2), focusedCell);

		// arrow down in empty placeholder cell
		// -> move down in placeholder column, no new data
		cellContent = "";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(1, 2), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// arrow left
		// -> back in x column, no new data
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_LEFT);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(1, 0), focusedCell);
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());
	}

	@Test
	public void testFunctionColumn_ArrowDownInNonEmptyPlaceholderColumn() throws Exception {
		tableValuesView.setValues(-2, 2, 1);
		addFunction("f", "x");
		assertEquals(2, tableValuesView.getTableValuesModel().getColumnCount());

		// select (0, 0)
		keyboardController.select(0, 0);
		assertEquals(new CellIndex(0, 0), focusedCell);

		// arrow right
		// -> skip f(x) column, editing placeholder column
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
		assertTrue(keyboardController.isEditingPlaceholderColumn());
		assertEquals(new CellIndex(0, 2), focusedCell);

		// arrow down in non-empty placeholder column
		// -> insert data, move down in now no-longer-placeholder column
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertFalse(keyboardController.isEditingPlaceholderColumn());
		assertEquals(3, tableValuesView.getTableValuesModel().getColumnCount());
		assertEquals(new CellIndex(1, 2), focusedCell);
	}

	// Scenario 3 (SciCalc):
	// - x ("values") column,
	// - f(x), g(x) columns,
	// - adding columns not allowed

	@Test
	public void testSciCalc() {
		ScientificDataTableController scientificDataTableController =
				new ScientificDataTableController(getKernel());
		scientificDataTableController.setup(tableValuesView);
		scientificDataTableController.defineFunctions("x", "x^2");
		assertEquals(6, keyboardController.getNrOfNavigableRows());
		assertEquals(3, keyboardController.getNrOfNavigableColumns());
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
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_RIGHT);
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
			cellContent = "" + (i - 2); // [-2...2]
			keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
			assertFalse(didReportModelChanged);
		}
		assertEquals(new CellIndex(4, 0), focusedCell);

		// arrow down once more
		// -> selection should be (5, 0) - editing placeholder row
		keyboardController.keyPressed(TableValuesKeyboardController.Key.ARROW_DOWN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(5, 0), focusedCell);

		// return (=arrow down) in non-empty placeholder row
		// -> new data inserted, editing new placeholder row
		cellContent = "3";
		keyboardController.keyPressed(TableValuesKeyboardController.Key.RETURN);
		assertTrue(keyboardController.isEditingPlaceholderRow());
		assertEquals(new CellIndex(6, 0), focusedCell);
	}

	// TableValuesKeyboardControllerDelegate

	@Override
	public void focusCell(int row, int column) {
		focusedCell = row >= 0 && column >= 0 ? new CellIndex(row, column) : null;
	}

	@Override
	public void unfocusCell(int row, int column) {
		focusedCell = null;
	}

	@Override
	public String getCellEditorContent(int row, int column) {
		return cellContent;
	}

	@Override
	public void invalidCellContentDetected(int row, int column) {
		System.out.println("Invalid cell content at row " + row + ", column " + column);
		didReportInvalidCellContent = true;
	}

	@Override
	public void hideKeyboard() {
		didRequestHideKeyboard = true;
	}

	//  TableValuesListener

	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		didReportModelChanged = true;
	}

	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		didReportModelChanged = true;
	}

	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		didReportModelChanged = true;
	}

	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		didReportModelChanged = true;
	}

	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		didReportModelChanged = true;
	}

	public void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow) {
		didReportModelChanged = true;
	}

	public void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow) {
		didReportModelChanged = true;
	}

	public void notifyRowChanged(TableValuesModel model, int row) {
		didReportModelChanged = true;
	}

	public void notifyDatasetChanged(TableValuesModel model) {
		didReportModelChanged = true;
	}
}
