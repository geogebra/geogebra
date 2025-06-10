package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.ContextMenuBuilder.HEADER_INDEX;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.CALCULATE;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.COPY;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.CREATE_CHART;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.CUT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.DELETE;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.DELETE_COLUMN;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.DELETE_ROW;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.DIVIDER;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_COLUMN_LEFT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_COLUMN_RIGHT;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_ROW_ABOVE;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.INSERT_ROW_BELOW;
import static org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier.PASTE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.ActionableItem;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.Identifier;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.junit.Before;
import org.junit.Test;

public final class ContextMenuBuilderTest {
	private ContextMenuBuilder builder;
	private SpreadsheetController controller;
	private TabularData<String> data;
	private TestClipboard clipboard;

	@Before
	public void setUp() {
		data = new TestTabularData();
		fillTestData();
		clipboard = new TestClipboard();
		controller = new SpreadsheetController(data, new SpreadsheetStyling());
		builder = new ContextMenuBuilder(controller);
		CopyPasteCutTabularDataImpl<?> copyPasteCut =
				new CopyPasteCutTabularDataImpl<>(data, clipboard, controller.getLayout(),
						new SpreadsheetSelectionController());
		controller.setCopyPasteCut(copyPasteCut);
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
	public void testCellMenuOrder() {
		testMenuOrder(1, 1,
				List.of(CUT, COPY, PASTE, DIVIDER, CALCULATE, CREATE_CHART, DIVIDER,
						INSERT_ROW_ABOVE, INSERT_ROW_BELOW, INSERT_COLUMN_LEFT,
						INSERT_COLUMN_RIGHT, DIVIDER, DELETE_ROW, DELETE_COLUMN));
	}

	private void testMenuOrder(int row, int column, List<Identifier> expected) {
		List<ContextMenuItem> menuItems = builder.build(row, column);
		assertEquals(expected, getIdentifiers(menuItems));
	}

	private List<Identifier> getIdentifiers(List<ContextMenuItem> menuItems) {
		return menuItems.stream().map(ContextMenuItem::getIdentifier)
				.collect(Collectors.toList());
	}

	@Test
	public void testRowMenuOrder() {
		testMenuOrder(1, HEADER_INDEX,
				List.of(CUT, COPY, PASTE, DIVIDER, CALCULATE, CREATE_CHART, DIVIDER,
						INSERT_ROW_ABOVE, INSERT_ROW_BELOW, DIVIDER, DELETE_ROW));
	}

	@Test
	public void testRowMenuOrderFull() {
		List<ContextMenuItem> menuItems = builder.build(0, 99, HEADER_INDEX, HEADER_INDEX);
		assertEquals(List.of(CUT, COPY, PASTE, DIVIDER, CALCULATE, CREATE_CHART, DIVIDER,
						INSERT_ROW_ABOVE, INSERT_ROW_BELOW),
				getIdentifiers(menuItems));
	}

	@Test
	public void testColumnMenuOrder() {
		testMenuOrder(HEADER_INDEX, 1,
				List.of(CUT, COPY, PASTE, DIVIDER, CALCULATE, CREATE_CHART, DIVIDER,
						INSERT_COLUMN_LEFT, INSERT_COLUMN_RIGHT, DIVIDER, DELETE_COLUMN));
	}

	@Test
	public void testColumnMenuOrderFull() {
		List<ContextMenuItem> menuItems = builder.build(HEADER_INDEX, HEADER_INDEX, 0, 99);
		assertEquals(List.of(CUT, COPY, PASTE, DIVIDER, CALCULATE, CREATE_CHART, DIVIDER,
						INSERT_COLUMN_LEFT, INSERT_COLUMN_RIGHT),
				getIdentifiers(menuItems));
	}

	private void runItemAt(int row, int column, Identifier id) {
		ContextMenuItem contextMenuItem = builder.build(row, column).stream()
				.filter(item -> item.getIdentifier().equals(id)).findAny().orElse(null);
		if (contextMenuItem == null) {
			fail("No such menu item at (" + row + ", " + column + "): " + id);
		}
		if (!(contextMenuItem instanceof ActionableItem)) {
			fail("Item at (" + row + ", " + column + "): " + id + " is not runnable");
		}
		((ActionableItem) contextMenuItem).performAction();
	}

	@Test
	public void testDeleteSingleRow() {
		runItemAt(4, HEADER_INDEX, DELETE_ROW);
		checkRowReplaced(4, 5);
	}

	@Test
	public void testDeleteSelectedRows() {
		selectRows(3, 6);
		runItemAt(3, HEADER_INDEX, DELETE_ROW);
		checkRowReplaced(3, 7);
	}

	private void selectRows(int fromRow, int toRow) {
		controller.select(new TabularRange(fromRow, -1, toRow, -1), false, false);
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
		runItemAt(HEADER_INDEX, 4, DELETE_COLUMN);
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
		runItemAt(HEADER_INDEX, 4, DELETE_COLUMN);
		checkColumnReplaced(2, 7);
	}

	private void selectColumns(int fromColumn, int toColumn) {
		controller.select(new TabularRange(-1, fromColumn, -1, toColumn), false, false);
	}

	@Test
	public void testInsertRowAbove() {
		runItemAt(3, HEADER_INDEX, DELETE_ROW);
		assertThat(data.numberOfRows(), equalTo(99));
		runItemAt(5, HEADER_INDEX, INSERT_ROW_ABOVE);
		checkNewRowAt(5);
		assertThat(data.numberOfRows(), equalTo(100));
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
		runItemAt(4, HEADER_INDEX, DELETE_ROW);
		assertThat(data.numberOfRows(), equalTo(99));
		runItemAt(5, HEADER_INDEX, INSERT_ROW_BELOW);
		checkNewRowAt(6);
		assertThat(data.numberOfRows(), equalTo(100));
	}

	@Test
	public void testInsertColumnLeft() {
		runItemAt(HEADER_INDEX, 3, DELETE_COLUMN);
		assertThat(data.numberOfColumns(), equalTo(99));
		runItemAt(HEADER_INDEX, 5,  INSERT_COLUMN_LEFT);
		checkNewColumnAt(5);
		assertThat(data.numberOfColumns(), equalTo(100));
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
		runItemAt(HEADER_INDEX, 4, DELETE_COLUMN);
		assertThat(data.numberOfColumns(), equalTo(99));
		runItemAt(HEADER_INDEX, 5,  INSERT_COLUMN_RIGHT);
		checkNewColumnAt(6);
		assertThat(data.numberOfColumns(), equalTo(100));
	}

	@Test
	public void testCopySingleCell() {
		runItemAt(1, 1, COPY);
		assertEquals("cell11", clipboard.getContent());
	}

	@Test
	public void testCopyCellSelection() {
		selectCells(1, 1, 4, 2);
		runItemAt(1, 1, COPY);
		assertEquals("cell11\tcell12\ncell21\tcell22\ncell31\tcell32\ncell41\tcell42",
				clipboard.getContent());
	}

	private void selectCells(int fromRow, int fromColumn, int toRow, int toColumn) {
		controller.selectionController.select(new Selection(
						new TabularRange(fromRow, fromColumn, toRow, toColumn)),
				false, false);
	}

	@Test
	public void testCutSingleCell() {
		runItemAt(1, 1, CUT);
		assertEquals("cell11", clipboard.getContent());
		assertNull(data.contentAt(1, 1));
	}

	@Test
	public void testCutCellSelection() {
		selectCells(1, 1, 4, 2);
		runItemAt(1, 1, CUT);
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
		runItemAt(1, 1, COPY);
		runItemAt(2, 2, PASTE);
		assertEquals("cell11", data.contentAt(2, 2));
	}

	@Test
	public void testPasteCellSelection() {
		selectCells(1, 1, 2, 2);
		runItemAt(1, 1, COPY);
		controller.selectionController.clearSelections();
		runItemAt(2, 4, PASTE);
		assertEquals("cell11", data.contentAt(2, 4));
		assertEquals("cell12", data.contentAt(2, 5));
		assertEquals("cell21", data.contentAt(3, 4));
		assertEquals("cell22", data.contentAt(3, 5));
	}

	@Test
	public void testPasteCellsToSmallerSelection() {
		selectCells(1, 1, 1, 4);
		runItemAt(1, 1, COPY);
		selectCells(10, 1, 10, 2);
		runItemAt(10, 1, PASTE);
		assertEquals("cell11", data.contentAt(10, 1));
		assertEquals("cell12", data.contentAt(10, 2));
		assertEquals("cell13", data.contentAt(10, 3));
		assertEquals("cell14", data.contentAt(10, 4));
	}

	@Test
	public void testPasteCellsToBiggerSelection() {
		selectCells(1, 1, 2, 2);
		runItemAt(1, 1, COPY);
		selectCells(10, 1, 14, 5);
		runItemAt(11, 1, PASTE);
		assertEquals("cell11", data.contentAt(10, 1));
		assertEquals("cell12", data.contentAt(10, 2));
		assertEquals("cell11", data.contentAt(10, 3));
		assertEquals("cell12", data.contentAt(10, 4));
		shouldStayDefault(10, 5);
		shouldStayDefault(11, 5);
		assertEquals("cell21", data.contentAt(11, 1));
		assertEquals("cell22", data.contentAt(11, 2));
		assertEquals("cell21", data.contentAt(11, 3));
		assertEquals("cell22", data.contentAt(11, 4));

		shouldStayDefault(12, 5);
		assertEquals("cell11", data.contentAt(12, 1));
		assertEquals("cell12", data.contentAt(12, 2));
		assertEquals("cell11", data.contentAt(12, 3));
		assertEquals("cell12", data.contentAt(12, 4));
		assertEquals("cell21", data.contentAt(13, 1));
		assertEquals("cell22", data.contentAt(13, 2));
		assertEquals("cell21", data.contentAt(13, 3));
		assertEquals("cell22", data.contentAt(13, 4));

		shouldStayDefault(13, 5);
		shouldStayDefault(14, 1);
		shouldStayDefault(14, 2);
		shouldStayDefault(14, 3);
		shouldStayDefault(14, 4);
	}

	@Test
	public void testSelectingAllCellsDisablesDeletingColumn() {
		controller.selectAll();
		assertThrows("The DELETE_COLUMN item should not pop up if all cells are selected!",
				AssertionError.class, () -> runItemAt(1, 1, DELETE_COLUMN));
	}

	@Test
	public void testSelectingAllCellsDisablesDeletingRows() {
		controller.selectAll();
		assertThrows("The DELETE_ROW item should not pop up if all cells are selected!",
				AssertionError.class, () -> runItemAt(1, 1, DELETE_ROW));
	}

	@Test
	public void testClickingOnCellWithRowsAndColumnsSelectedEnablesInsertingRow() {
		controller.selectRow(1, false, false);
		controller.selectColumn(2, false, true);
		List<ContextMenuItem> contextMenuItems = builder.build(1, 2);
		assertTrue(contextMenuItems.stream().anyMatch(
				item -> item.getIdentifier() == INSERT_ROW_ABOVE));
	}

	private void shouldStayDefault(int row, int column) {
		assertEquals(cellData(row, column), data.contentAt(row, column));
	}
}
