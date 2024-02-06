package org.geogebra.common.spreadsheet.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.util.shape.Rectangle;
import org.junit.Test;

public class SpreadsheetControllerTest {

	private final SpreadsheetController controller =
			new SpreadsheetController(new TestTabularData());

	@Test
	public void testMove() {
		controller.selectCell(1, 1, false, false);
		Selection initialCell = controller.getLastSelection();

		controller.moveRight(false);
		controller.moveDown(false);
		controller.moveLeft(false);
		controller.moveUp(false);

		assertRangeEquals(initialCell, Selection.getSingleCellSelection(1, 1));
	}

	@Test
	public void testMoveLeft() {
		controller.selectCell(3, 1, false, false);

		controller.moveLeft(false);
		controller.moveLeft(false);

		assertRangeEquals(controller.getLastSelection(), Selection.getSingleCellSelection(3, 0));
	}

	@Test
	public void testMoveRight() {
		controller.selectCell(3, controller.getLayout().numberOfColumns() - 3, false, false);

		controller.moveRight(false);
		controller.moveRight(false);

		assertRangeEquals(controller.getLastSelection(),
				Selection.getSingleCellSelection(3,
						controller.getLayout().numberOfColumns() - 2));
	}

	@Test
	public void testMoveUp() {
		controller.selectCell(1, 3, false, false);

		controller.moveUp(false);
		controller.moveUp(false);

		assertRangeEquals(controller.getLastSelection(), Selection.getSingleCellSelection(0, 3));
	}

	@Test
	public void testMoveDown() {
		controller.selectCell(controller.getLayout().numberOfRows() - 3, 3, false, false);

		controller.moveDown(false);
		controller.moveDown(false);

		assertRangeEquals(controller.getLastSelection(),
				Selection.getSingleCellSelection(controller.getLayout().numberOfRows() - 2, 3));
	}

	@Test
	public void testExtendSelectionByMoving1() {
		controller.selectCell(1, 1, false, false);

		controller.moveRight(true);
		controller.moveRight(true);
		controller.moveDown(true);
		controller.moveDown(true);

		assertRangeEquals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, TabularRange.range(1, 3, 1, 3)));
	}

	@Test
	public void testExtendSelectionByHorizontalDrag() {
		Rectangle viewport = new Rectangle(0, 500, 0, 500);
		controller.handlePointerDown(101, 3, Modifiers.NONE, viewport, null, null);
		controller.handlePointerMove(241, 3, Modifiers.NONE, viewport);
		controller.handlePointerUp(241, 3, Modifiers.NONE, viewport);

		assertRangeEquals(controller.getLastSelection(),
				new Selection(SelectionType.COLUMNS, TabularRange.range(-1, -1, 0, 1)));
	}

	@Test
	public void testExtendSelectionByVerticalDrag() {
		Rectangle viewport = new Rectangle(0, 500, 0, 500);
		controller.handlePointerDown(3, 50, Modifiers.NONE, viewport, null, null);
		controller.handlePointerMove(3, 150, Modifiers.NONE, viewport);
		controller.handlePointerUp(3, 150, Modifiers.NONE, viewport);

		assertRangeEquals(controller.getLastSelection(),
				new Selection(SelectionType.ROWS, TabularRange.range(0, 3, -1, -1)));
	}

	@Test
	public void testExtendSelectionByMoving2() {
		controller.selectCell(5, 5, false, false);
		controller.moveUp(true);
		controller.moveUp(true);
		controller.moveLeft(true);
		controller.moveLeft(true);

		assertRangeEquals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, TabularRange.range(3, 5, 3, 5)));
	}

	@Test
	public void testExtendSelectionByMoving3() {
		controller.selectCell(5, 5, false, false);
		controller.moveUp(true);
		controller.moveUp(true);
		controller.moveLeft(true);
		controller.moveLeft(true);
		controller.moveRight(true);
		assertRangeEquals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, TabularRange.range(3, 5, 4, 5)));
	}

	@Test
	public void testExtendSelectionByClicking1() {
		controller.selectCell(3, 3, false, false);
		controller.selectCell(5, 5, true, false);

		assertRangeEquals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, TabularRange.range(3, 5, 3, 5)));
	}

	@Test
	public void testExtendSelectionByClicking2() {
		controller.selectCell(3, 3, false, false);
		controller.selectCell(1, 1, true, false);

		assertRangeEquals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, TabularRange.range(3, 1, 3, 1)));
	}

	@Test
	public void testAddSelections() {
		controller.selectCell(3, 3, false, false);
		controller.selectCell(1, 1, false, true);
		controller.selectCell(5, 6, false, true);
		controller.selectCell(7, 7, true, true);

		assertEquals(controller.getSelections().size(), 3);
	}

	@Test
	public void testIsSelected() {
		controller.selectColumn(2, false, false);
		controller.selectColumn(3, true, false);
		assertThat(controller.isSelected(3, 3), equalTo(true));
		assertThat(controller.isSelected(-1, 3), equalTo(true));
		assertThat(controller.isSelected(3, 4), equalTo(false));
		assertThat(controller.isSelected(-1, 4), equalTo(false));
	}

	private void assertRangeEquals(Selection selection, Selection other) {
		TabularRange selectionRange = selection.getRange();
		TabularRange otherRange = other.getRange();
		assertEquals(selection.getType(), other.getType());
		assertEquals(selectionRange.getMinRow(), otherRange.getMinRow());
		assertEquals(selectionRange.getMaxRow(), otherRange.getMaxRow());
		assertEquals(selectionRange.getMinColumn(), otherRange.getMinColumn());
		assertEquals(selectionRange.getMaxColumn(), otherRange.getMaxColumn());
	}
}
