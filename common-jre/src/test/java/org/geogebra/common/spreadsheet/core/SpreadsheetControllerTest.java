package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.spreadsheet.TestTabularData;
import org.junit.Test;

public class SpreadsheetControllerTest {

	private final SpreadsheetController controller = new SpreadsheetController(new TestTabularData());

	@Test
	public void testMove() {
		controller.selectCell(1, 1, false, false);
		Selection initialCell = controller.getLastSelection();

		controller.moveRight(false);
		controller.moveDown(false);
		controller.moveLeft(false);
		controller.moveUp(false);

		assertTrue(equals(initialCell, Selection.getSingleCellSelection(1, 1)));
	}

	@Test
	public void testMoveLeft() {
		controller.selectCell(3, 1, false, false);

		controller.moveLeft(false);
		controller.moveLeft(false);

		assertTrue(equals(controller.getLastSelection(), Selection.getSingleCellSelection(3, 0)));
	}

	@Test
	public void testMoveRight() {
		controller.selectCell(3, controller.getLayout().numberOfColumns() - 3, false, false);

		controller.moveRight(false);
		controller.moveRight(false);

		assertTrue(equals(controller.getLastSelection(),
				Selection.getSingleCellSelection(3,
						controller.getLayout().numberOfColumns() - 2)));
	}

	@Test
	public void testMoveUp() {
		controller.selectCell(1, 3, false, false);

		controller.moveUp(false);
		controller.moveUp(false);

		assertTrue(equals(controller.getLastSelection(), Selection.getSingleCellSelection(0, 3)));
	}

	@Test
	public void testMoveDown() {
		controller.selectCell(controller.getLayout().numberOfRows() - 3, 3, false, false);

		controller.moveDown(false);
		controller.moveDown(false);

		assertTrue(equals(controller.getLastSelection(),
				Selection.getSingleCellSelection(controller.getLayout().numberOfRows() - 2, 3)));
	}

	@Test
	public void testExtendSelectionByMoving1() {
		controller.selectCell(1, 1, false, false);

		controller.moveRight(true);
		controller.moveRight(true);
		controller.moveDown(true);
		controller.moveDown(true);

		assertTrue(equals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, new TabularRange(1, 3, 1, 3))));
	}

	@Test
	public void testExtendSelectionByMoving2() {
		controller.selectCell(5, 5, false, false);

		controller.moveUp(true);
		controller.moveUp(true);
		controller.moveLeft(true);
		controller.moveLeft(true);

		assertTrue(equals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, new TabularRange(5, 3, 5, 3))));
	}

	@Test
	public void testExtendSelectionByMoving3() {
		controller.selectCell(5, 5, false, false);

		controller.moveUp(true);
		controller.moveUp(true);
		controller.moveLeft(true);
		controller.moveLeft(true);
		controller.moveRight(true);

		assertTrue(equals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, new TabularRange(5, 3, 5, 4))));
	}

	@Test
	public void testExtendSelectionByClicking1() {
		controller.selectCell(3, 3, false, false);
		controller.selectCell(5, 5, true, false);

		assertTrue(equals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, new TabularRange(3, 5, 3, 5))));
	}

	@Test
	public void testExtendSelectionByClicking2() {
		controller.selectCell(3, 3, false, false);
		controller.selectCell(1, 1, true, false);

		assertTrue(equals(controller.getLastSelection(),
				new Selection(SelectionType.CELLS, new TabularRange(3, 1, 3, 1))));
	}

	@Test
	public void testAddSelections() {
		controller.selectCell(3, 3, false, false);
		controller.selectCell(1, 1, false, true);
		controller.selectCell(5, 6, false, true);
		controller.selectCell(7, 7, true, true);

		assertEquals(controller.getSelections().size(), 3);
	}

	private boolean equals(Selection selection, Selection other) {
		TabularRange selectionRange = selection.getRange();
		TabularRange otherRange = other.getRange();
		return selection.getType() == other.getType()
				&& (selectionRange.fromRow == otherRange.fromRow
				&& selectionRange.toRow == otherRange.toRow
				&& selectionRange.fromCol == otherRange.fromCol
				&& selectionRange.toCol == otherRange.toCol);
	}
}
