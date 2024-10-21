package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Nullable;

import org.geogebra.common.spreadsheet.TestTabularData;
import org.junit.Test;

public class SpreadsheetSelectionControllerTest {

	private final SpreadsheetController controller =
			new SpreadsheetController(new TestTabularData());

	private final SpreadsheetSelectionController selectionController =
			new SpreadsheetSelectionController();
	private final int numberOfRows = 100;
	private final int numberOfColumns = 100;

	@Test
	public void testMove() {
		selectionController.selectCell(1, 1, false, false);
		selectionController.moveRight(false, numberOfColumns);
		selectionController.moveDown(false, numberOfRows);
		assertRangeEquals(selectionController.getLastSelection(),
				Selection.getSingleCellSelection(2, 2));
		selectionController.moveLeft(false);
		selectionController.moveUp(false);

		assertRangeEquals(selectionController.getLastSelection(),
				Selection.getSingleCellSelection(1, 1));
	}

	@Test
	public void testMoveLeft() {
		selectionController.selectCell(3, 1, false, false);

		selectionController.moveLeft(false);
		selectionController.moveLeft(false);

		assertRangeEquals(selectionController.getLastSelection(),
				Selection.getSingleCellSelection(3, 0));
	}

	@Test
	public void testMoveRight() {
		selectionController.selectCell(3, numberOfColumns - 3, false, false);

		selectionController.moveRight(false, numberOfColumns);
		selectionController.moveRight(false, numberOfColumns);

		assertRangeEquals(selectionController.getLastSelection(),
				Selection.getSingleCellSelection(3, numberOfColumns - 1));
	}

	@Test
	public void testMoveUp() {
		selectionController.selectCell(1, 3, false, false);

		selectionController.moveUp(false);
		selectionController.moveUp(false);

		assertRangeEquals(selectionController.getLastSelection(),
				Selection.getSingleCellSelection(0, 3));
	}

	@Test
	public void testMoveDown() {
		selectionController.selectCell(controller.getLayout().numberOfRows() - 3, 3, false, false);

		selectionController.moveDown(false, numberOfRows);
		selectionController.moveDown(false, numberOfRows);

		assertRangeEquals(selectionController.getLastSelection(),
				Selection.getSingleCellSelection(numberOfRows - 1, 3));
	}

	@Test
	public void testExtendSelectionByMoving1() {
		selectionController.selectCell(1, 1, false, false);

		selectionController.moveRight(true, numberOfColumns);
		selectionController.moveRight(true, numberOfColumns);
		selectionController.moveDown(true, numberOfRows);
		selectionController.moveDown(true, numberOfRows);

		assertRangeEquals(selectionController.getLastSelection(),
				new Selection(TabularRange.range(1, 3, 1, 3)));
	}

	@Test
	public void testExtendSelectionByHorizontalDrag() {
		controller.handlePointerDown(101, 3, Modifiers.NONE);
		controller.handlePointerMove(241, 3, Modifiers.NONE);
		controller.handlePointerUp(241, 3, Modifiers.NONE);

		assertRangeEquals(controller.getLastSelection(),
				new Selection(TabularRange.range(-1, -1, 0, 1)));
	}

	@Test
	public void testExtendSelectionByVerticalDrag() {
		controller.handlePointerDown(3, 50, Modifiers.NONE);
		controller.handlePointerMove(3, 150, Modifiers.NONE);
		controller.handlePointerUp(3, 150, Modifiers.NONE);

		assertRangeEquals(controller.getLastSelection(),
				new Selection(TabularRange.range(0, 3, -1, -1)));
	}

	@Test
	public void testExtendSelectionByMoving2() {
		selectionController.selectCell(5, 5, false, false);
		selectionController.moveUp(true);
		selectionController.moveUp(true);
		selectionController.moveLeft(true);
		selectionController.moveLeft(true);

		assertRangeEquals(selectionController.getLastSelection(),
				new Selection(TabularRange.range(3, 5, 3, 5)));
	}

	@Test
	public void testExtendSelectionByMoving3() {
		selectionController.selectCell(5, 5, false, false);
		selectionController.moveUp(true);
		selectionController.moveUp(true);
		selectionController.moveLeft(true);
		selectionController.moveLeft(true);
		selectionController.moveRight(true, numberOfColumns);
		assertRangeEquals(selectionController.getLastSelection(),
				new Selection(TabularRange.range(3, 5, 4, 5)));
	}

	@Test
	public void testExtendSelectionByClicking1() {
		selectionController.selectCell(3, 3, false, false);
		selectionController.selectCell(5, 5, true, false);

		assertRangeEquals(selectionController.getLastSelection(),
				new Selection(TabularRange.range(3, 5, 3, 5)));
	}

	@Test
	public void testExtendSelectionByClicking2() {
		selectionController.selectCell(3, 3, false, false);
		selectionController.selectCell(1, 1, true, false);

		assertRangeEquals(selectionController.getLastSelection(),
				new Selection(TabularRange.range(3, 1, 3, 1)));
	}

	@Test
	public void testAddSelections() {
		selectionController.selectCell(3, 3, false, false);
		selectionController.selectCell(1, 1, false, true);
		selectionController.selectCell(5, 6, false, true);
		selectionController.selectCell(7, 7, true, true);

		assertEquals(3, selectionController.getSelections().count());
	}

	@Test
	public void testIsSelected() {
		selectionController.selectColumn(2, false, false);
		selectionController.selectColumn(3, true, false);
		assertTrue(selectionController.isSelected(3, 3));
		assertTrue(selectionController.isSelected(-1, 3));
		assertFalse(selectionController.isSelected(3, 4));
		assertFalse(selectionController.isSelected(-1, 4));
	}

	@Test
	public void testIsOnlyRowSelected1() {
		selectionController.selectRow(1, false, false);
		assertTrue(selectionController.isOnlyRowSelected(1));
	}

	@Test
	public void testIsOnlyRowSelected2() {
		selectionController.selectRow(1, false, false);
		selectionController.selectRow(2, true, false);
		assertFalse(selectionController.isOnlyRowSelected(1));
	}

	@Test
	public void testIsOnlyRowSelected3() {
		selectionController.selectRow(1, false, false);
		selectionController.selectColumn(1, false, true);
		assertFalse(selectionController.isOnlyRowSelected(1));
	}

	@Test
	public void testIsOnlyColumnSelected1() {
		selectionController.selectColumn(1, false, false);
		assertTrue(selectionController.isOnlyColumnSelected(1));
	}

	@Test
	public void testIsOnlyColumnSelected2() {
		selectionController.selectColumn(1, false, false);
		selectionController.selectColumn(2, false, true);
		assertFalse(selectionController.isOnlyColumnSelected(2));
	}

	@Test
	public void testIsOnlyColumnSelected3() {
		selectionController.selectAll();
		assertFalse(selectionController.isOnlyColumnSelected(1));
	}

	@Test
	public void testAllCellsSelected() {
		selectionController.selectAll();
		assertTrue(selectionController.areAllCellsSelected());
	}

	@Test
	public void testAreOnlyRowsSelected1() {
		selectionController.select(new Selection(
				TabularRange.range(0, 2, -1, -1)), false, false);
		assertTrue(selectionController.areOnlyRowsSelected());
	}

	@Test
	public void testAreOnlyRowsSelected2() {
		selectionController.selectRow(1, false, false);
		selectionController.selectColumn(1, false, true);
		assertFalse(selectionController.areOnlyRowsSelected());
	}

	@Test
	public void testAreOnlyColumnsSelected1() {
		selectionController.select(new Selection(
				TabularRange.range(-1, -1, 0, 2)), false, false);
		assertTrue(selectionController.areOnlyColumnsSelected());
	}

	@Test
	public void testAreOnlyColumnsSelected2() {
		selectionController.selectColumn(1, false, false);
		selectionController.selectCell(3, 3, false, true);
		assertFalse(selectionController.areOnlyColumnsSelected());
	}

	@Test
	public void testAreOnlyCellsSelected1() {
		selectionController.select(new Selection(
				TabularRange.range(0, 2, 1, 2)), false, false);
		assertTrue(selectionController.areOnlyCellsSelected());
	}

	@Test
	public void testAreOnlyCellsSelected2() {
		selectionController.select(new Selection(
				TabularRange.range(0, 1, 0, 2)), false, false);
		selectionController.selectRow(4, false, true);
		assertFalse(selectionController.areOnlyCellsSelected());
	}

	@Test
	public void testGetAllRowIndexes1() {
		selectionController.selectCell(1, 1, false, false);
		selectionController.selectCell(5, 1, false, true);
		selectionController.selectCell(2, 1, false, true);
		List<Integer> rowIndexes = List.of(1, 2, 5);
		List<Integer> getAllRowIndexes = selectionController.getAllRowIndexes();
		assertTrue(assertListsAreEqual(rowIndexes, getAllRowIndexes));
	}

	@Test
	public void testGetAllColumnIndexes() {
		selectionController.selectCell(1, 3, false, false);
		selectionController.selectCell(5, 18, false, true);
		selectionController.selectCell(2, 2, false, true);
		List<Integer> rowIndexes = List.of(2, 3, 18);
		List<Integer> getAllColumnIndexes = selectionController.getAllColumnIndexes();
		assertTrue(assertListsAreEqual(rowIndexes, getAllColumnIndexes));
	}

	@Test
	public void testGetAllColumnIndexesContainsNoDuplicates() {
		selectionController.selectCell(1, 3, false, false);
		selectionController.selectCell(5, 18, false, true);
		selectionController.selectCell(2, 3, false, true);
		List<Integer> rowIndexes = List.of(3, 18);
		List<Integer> getAllColumnIndexes = selectionController.getAllColumnIndexes();
		assertTrue(assertListsAreEqual(rowIndexes, getAllColumnIndexes));
	}

	@Test
	public void testGetUppermostRowIndex() {
		selectionController.select(new Selection(
				TabularRange.range(1, 2, 0, 2)), false, false);
		selectionController.selectCell(3, 2, false, true);
		selectionController.selectCell(2, 2, false, true);
		assertEquals(1, selectionController.getUppermostSelectedRowIndex());
	}

	@Test
	public void testGetBottommostRowIndex() {
		selectionController.select(new Selection(
				TabularRange.range(1, 2, 0, 2)), false, false);
		selectionController.selectCell(4, 1, false, true);
		selectionController.selectCell(3, 1, false, true);
		assertEquals(4, selectionController.getBottommostSelectedRowIndex());
	}

	@Test
	public void testGetLeftmostColumnIndex() {
		selectionController.select(new Selection(
				TabularRange.range(1, 2, 0, 2)), false, false);
		selectionController.selectCell(2, 0, false, true);
		selectionController.selectCell(2, 4, false, true);
		assertEquals(0, selectionController.getLeftmostSelectedColumnIndex());
	}

	@Test
	public void testGetRightmostColumnIndex() {
		selectionController.select(new Selection(
				TabularRange.range(1, 2, 0, 2)), false, false);
		selectionController.selectCell(1, 3, false, true);
		selectionController.selectCell(1, 0, false, true);
		assertEquals(3, selectionController.getRightmostSelectedColumnIndex());
	}

	private void assertRangeEquals(@Nullable Selection selection, Selection other) {
		assertNotNull("Selection should not be null", selection);
		TabularRange selectionRange = selection.getRange();
		TabularRange otherRange = other.getRange();
		assertEquals(selection.getType(), other.getType());
		assertEquals(selectionRange.getMinRow(), otherRange.getMinRow());
		assertEquals(selectionRange.getMaxRow(), otherRange.getMaxRow());
		assertEquals(selectionRange.getMinColumn(), otherRange.getMinColumn());
		assertEquals(selectionRange.getMaxColumn(), otherRange.getMaxColumn());
	}

	private boolean assertListsAreEqual(List<Integer> list1, List<Integer> list2) {
		return list1.size() == list2.size() && list2.containsAll(list1);
	}
}
