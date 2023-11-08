package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.ContextMenuItems.HEADER_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.spreadsheet.TestTabularData;
import org.junit.Before;
import org.junit.Test;

public final class ContextMenuItemsTest {
	private ContextMenuItems items;
	private final SpreadsheetSelectionController selectionController =
			new SpreadsheetSelectionController();
	private TabularData<Object> data;
	private TestClipboard clipboard;

	@Before
	public void setUp() {
		data = new TestTabularData();
		fillTestData();
		clipboard = new TestClipboard();
		CopyPasteCutTabularDataImpl copyPasteCut = new CopyPasteCutTabularDataImpl(data, clipboard);
		items = new ContextMenuItems(data, selectionController, copyPasteCut);
	}

	private void fillTestData() {
		for (int column = 0; column < data.numberOfColumns(); column++) {
			for (int row = 0; row < data.numberOfRows(); row++) {
				data.setContent(row, column, cellData(row, column));
			}
		}
	}

	private static String cellData(int row, int column) {
		return "cell" + row + column;
	}

	@Test
	public void testCellMenuKeys() {
		testMenuKeys(1, 1,
				"Delete", "Copy", "Cut", "Paste");
	}

	private void testMenuKeys(int row, int column, String... keys) {
		Map<String, Runnable> map = items.get(row, column);
		assertEquals(map.keySet(), Set.of(keys));
	}

	@Test
	public void testRowMenuKeys() {
		testMenuKeys(1, HEADER_INDEX,
				"ContextMenu.insertRowAbove",
				"ContextMenu.insertRowBelow", "ContextMenu.deleteRow");
	}

	@Test
	public void testColumnMenuKeys() {
		testMenuKeys(HEADER_INDEX, 1,
				"ContextMenu.insertColumnLeft", "ContextMenu.insertColumnRight",
					"ContextMenu.deleteColumn");
	}

	@Test
	public void testDeleteCell() {
		runItemAt(2, 1, "Delete");
		assertNull(data.contentAt(2, 1));
	}

	@Test
	public void testDeleteSelectedCells() {
		TabularRange range = new TabularRange(2, 6, 4, 8);
		selectionController.select(new Selection(SelectionType.CELLS, range), false, true);
		runItemAt(2, 4, "Delete");
		checkRangeIsDeleted(range);
	}

	private void checkRangeIsDeleted(TabularRange range) {
		int count = 0;
		for (int row = range.getFromRow(); row < range.getToRow(); row++) {
			for (int column = range.getFromColumn(); column < range.getToColumn(); column++) {
				if (data.contentAt(row, column) == null) {
					count++;
				}
			}
		}
		int allSelectedCells = (range.getToRow() - range.getFromRow()) * (range.getToColumn() - range.getFromColumn());
		assertEquals(allSelectedCells, count);
	}

	private void runItemAt(int row, int column, String menuItemKey) {
		Runnable action = items.get(row, column).get(menuItemKey);
		if (action == null) {
			fail("No such menu item at (" + row + ", " + column + "): " + menuItemKey);
		}
		action.run();
	}

	@Test
	public void testDeleteSingleRow() {
		runItemAt(4, HEADER_INDEX, "ContextMenu.deleteRow");
		checkRowReplaced(4, 5);
	}

	@Test
	public void testDeleteSelectedRows() {
		selectRows(3, 6);
		runItemAt(3, HEADER_INDEX, "ContextMenu.deleteRow");
		checkRowReplaced(3, 7);
	}

	private void selectRows(int fromRow, int toRow) {
		selectionController.select(new Selection(SelectionType.ROWS, new TabularRange(fromRow,
				HEADER_INDEX, toRow,
				HEADER_INDEX)), false, true);
	}

	private void checkRowReplaced(int fromRow, int toRow) {
		int count = 0;
		for (int column = 0; column < data.numberOfColumns(); column++) {
			if (data.contentAt(fromRow, column).equals(cellData(toRow, column))) {
				count++;
			}
		}
		assertEquals(data.numberOfColumns(), count);
	}

	@Test
	public void testDeleteSingleColumn() {
		runItemAt(HEADER_INDEX, 4, "ContextMenu.deleteColumn");
		checkColumnReplaced(4, 5);
	}

	private void checkColumnReplaced(int fromColumn, int toColumn) {
		int count = 0;
		for (int row = 0; row < data.numberOfRows(); row++) {
			if (data.contentAt(row, fromColumn).equals(cellData(row, toColumn))) {
				count++;
			}
		}
		assertEquals(data.numberOfRows(), count);
	}

	@Test
	public void testDeleteSelectedColumns() {
		selectColumns(2, 6);
		runItemAt(HEADER_INDEX, 4, "ContextMenu.deleteColumn");
		checkColumnReplaced(2, 7);
	}

	private void selectColumns(int fromColumn, int toColumn) {
		selectionController.select(new Selection(SelectionType.COLUMNS,
				new TabularRange(HEADER_INDEX, fromColumn, HEADER_INDEX, toColumn)),
				false, true);
	}

	@Test
	public void testInsertRowAbove() {
		runItemAt(5, HEADER_INDEX, "ContextMenu.insertRowAbove");
		checkNewRowAt(5);
	}

	private void checkNewRowAt(int row) {
		int count = 0;
		for (int column = 0; column < data.numberOfColumns(); column++) {
			if (data.contentAt(row, column) == null) {
				count++;
			}
		}
		assertEquals(data.numberOfColumns(), count);
	}

	@Test
	public void testInsertRowBelow() {
		runItemAt(5, HEADER_INDEX, "ContextMenu.insertRowBelow");
		checkNewRowAt(6);
	}

	@Test
	public void testInsertColumnLeft() {
		runItemAt(HEADER_INDEX, 5,  "ContextMenu.insertColumnLeft");
		checkNewColumnAt(5);
	}

	private void checkNewColumnAt(int column) {
		int count = 0;
		for (int row = 0; row < data.numberOfRows(); row++) {
			if (data.contentAt(row, column) == null) {
				count++;
			}
		}
		assertEquals(data.numberOfRows(), count);
	}

	@Test
	public void testInsertColumnRight() {
		runItemAt(HEADER_INDEX, 5,  "ContextMenu.insertColumnRight");
		checkNewColumnAt(6);
	}

	@Test
	public void testCopySingleCell() {
		runItemAt(1, 1, "Copy");
		assertEquals("cell11", clipboard.getContent());
	}

	@Test
	public void testCopyCellSelection() {
		selectCells(1, 4, 1, 2);
		runItemAt(1, 1, "Copy");
		assertEquals("cell11\tcell12\ncell21\tcell22\ncell31\tcell32\ncell41\tcell42",
				clipboard.getContent());
	}

	private void selectCells(int fromRow, int toRow, int fromColumn, int toColumn) {
		selectionController.select(new Selection(SelectionType.COLUMNS,
						new TabularRange(fromRow, fromColumn, toRow, toColumn)),
				false, true);
	}

	@Test
	public void testCutSingleCell() {
		runItemAt(1, 1, "Cut");
		assertEquals("cell11", clipboard.getContent());
		assertNull(data.contentAt(1, 1));
	}

	@Test
	public void testCutCellSelection() {
		selectCells(1, 4, 1, 2);
		runItemAt(1, 1, "Cut");
		assertEquals("cell11\tcell12\ncell21\tcell22\ncell31\tcell32\ncell41\tcell42",
				clipboard.getContent());
		TabularRange range = new TabularRange(1, 1, 4, 2);
		for (int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
			for (int column = range.getFromColumn(); column < range.getToColumn() + 1; column++) {
				assertNull(data.contentAt(row, column));
			}
		}
	}

	@Test
	public void testPasteSingleCell() {
		runItemAt(1, 1, "Copy");
		runItemAt(2, 2, "Paste");
		assertEquals("cell11", data.contentAt(2, 2));
	}

	@Test
	public void testPasteCellSelection() {
		selectCells(1, 2, 1, 2);
		runItemAt(1, 1, "Copy");
		selectionController.clearSelection();
		runItemAt(2, 4, "Paste");
		assertEquals("cell11", data.contentAt(2, 4));
		assertEquals("cell12", data.contentAt(2, 5));
		assertEquals("cell21", data.contentAt(3, 4));
		assertEquals("cell22", data.contentAt(3, 5));
	}

	@Test
	public void testPasteCellsToSelection() {
		selectCells(1, 2, 1, 2);
		runItemAt(1, 1, "Copy");
		selectCells(2, 4, 2, 6);
		runItemAt(2, 4, "Paste");
		assertEquals("cell11", data.contentAt(2, 4));
		assertEquals("cell12", data.contentAt(2, 5));
		assertEquals("cell21", data.contentAt(3, 4));
		assertEquals("cell22", data.contentAt(3, 5));
		assertEquals("cell11", data.contentAt(2, 6));
		assertEquals("cell12", data.contentAt(2, 7));
		assertEquals("cell21", data.contentAt(3, 6));
		assertEquals("cell22", data.contentAt(3, 7));
	}
}