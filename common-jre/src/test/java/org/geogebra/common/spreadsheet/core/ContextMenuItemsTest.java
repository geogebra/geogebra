package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.ContextMenuItems.HEADER_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.spreadsheet.TestTabularData;
import org.junit.Test;

public class ContextMenuItemsTest {
	private ContextMenuItems items;
	private SpreadsheetSelectionController selectionController =
			new SpreadsheetSelectionController();
	private TabularData<Object> data;

	@Test
	public void testCellMenuKeys() {
		testMenuKeys(1, 1,
				"Delete", "Copy", "Cut", "Paste");
	}

	private void testMenuKeys(int row, int column, String... keys) {
		items = new ContextMenuItems(null, null);
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
		createTabularData();
		items = new ContextMenuItems(data, null);
		runItemAt(2, 1, "Delete");
		assertNull(data.contentAt(2, 1));
	}

	private void createTabularData() {
		data = new TestTabularData();
		for (int column = 0; column < data.numberOfColumns(); column++) {
			for (int row = 0; row < data.numberOfRows(); row++) {
				data.setContent(row, column, "cell" + row + "" + column);
			}
		}
	}

	@Test
	public void testDeleteSingleRow() {
		createTabularData();
		items = new ContextMenuItems(data, new SpreadsheetSelectionController());
		assertEquals("cell40", data.contentAt(4, 0));
		runItemAt(4, HEADER_INDEX, "ContextMenu.deleteRow");
		assertEquals("cell50", data.contentAt(4, 0));
	}

	private void runItemAt(int row, int column, String menuItemKey) {
		Runnable action = items.get(row, column).get(menuItemKey);
		if (action == null) {
			fail("No such menu item at (" + row + ", " + column + "): " + menuItemKey);
		}
		action.run();
	}

	@Test
	public void testDeleteSelectedRows() {
		createTabularData();
		items = new ContextMenuItems(data, selectionController);
		assertEquals("cell40", data.contentAt(4, 0));
		selectRows(4, 6);
		runItemAt(4, HEADER_INDEX, "ContextMenu.deleteRow");
		assertEquals("cell70", data.contentAt(4, 0));
	}

	private void selectRows(int fromRow, int toRow) {
		selectionController.select(new Selection(SelectionType.ROWS, new TabularRange(fromRow,
				toRow,
				HEADER_INDEX, HEADER_INDEX)), false);
	}

	@Test
	public void testDeleteSingleColumn() {
		createTabularData();
		items = new ContextMenuItems(data, selectionController);
		assertEquals("cell04", data.contentAt(0, 4));
		runItemAt(HEADER_INDEX, 4, "ContextMenu.deleteColumn");
		assertEquals("cell05", data.contentAt(0, 4));
	}

	@Test
	public void testDeleteSelectedColumns() {
		createTabularData();
		items = new ContextMenuItems(data, selectionController);
		assertEquals("cell04", data.contentAt(0, 4));
		selectColumns(4, 6);
		runItemAt(HEADER_INDEX, 4, "ContextMenu.deleteColumn");
		assertEquals("cell07", data.contentAt(0, 4));
	}

	private void selectColumns(int fromColumn, int toColumn) {
		selectionController.select(new Selection(SelectionType.COLUMNS, new TabularRange(HEADER_INDEX,
				HEADER_INDEX, fromColumn, toColumn)), false);
	}

	@Test
	public void testInsertRowAbove() {
		createTabularData();
		items = new ContextMenuItems(data, selectionController);
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
		createTabularData();
		items = new ContextMenuItems(data, selectionController);
		runItemAt(5, HEADER_INDEX, "ContextMenu.insertRowBelow");
		checkNewRowAt(6);
	}

	@Test
	public void testInsertColumnLeft() {
		createTabularData();
		items = new ContextMenuItems(data, selectionController);
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
		createTabularData();
		items = new ContextMenuItems(data, selectionController);
		runItemAt(HEADER_INDEX, 5,  "ContextMenu.insertColumnRight");
		checkNewColumnAt(6);
	}
}
