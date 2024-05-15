package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.geogebra.common.spreadsheet.TestTabularData;
import org.junit.Test;

public class CellDragPasteHandlerTest {

	private CellDragPasteHandler cellDragPasteHandler;
	private final  TabularData<?> tabularData = new TestTabularData();

	@Test
	public void testPasteSingleCell1() {
		tabularData.setContent(0, 0, "12");
		setRangeToCopy(0, 0, 0, 0);
		pasteToDestination(0, 1);
		assertCellContentIsEqual(0, 0, 0, 1);
	}

	@Test
	public void testPasteSingleCell2() {
		tabularData.setContent(1, 1, "1 + 3");
		setRangeToCopy(1, 1, 1, 1);
		pasteToDestination(2, 2);
		assertCellContentIsEqual(1, 1, 2, 1);
	}

	@Test
	public void testPasteSingleCell3() {
		tabularData.setContent(1, 1, "123");
		tabularData.setContent(1, 2, "456");
		setRangeToCopy(1, 1, 1, 1);
		pasteToDestination(1, 2);
		assertCellContentIsEqual(1, 1, 1, 2);
	}

	@Test
	public void testPasteSingleCell4() {
		tabularData.setContent(0, 0, "\"=12\"");
		tabularData.setContent(0, 1, "\"=A1\"");
		setRangeToCopy(0, 0, 1, 1);
		pasteToDestination(1, 1);
		assertCellContentIsEqual(0, 1, 1, 1);
	}

	@Test
	public void testPasteMultipleCells1() {
		tabularData.setContent(1, 1, "123");
		tabularData.setContent(1, 2, "456");
		setRangeToCopy(1, 1, 1, 2);
		pasteToDestination(2, 2);
		assertCellContentIsEqual(1, 1, 2, 1);
		assertCellContentIsEqual(1, 2, 2, 2);
	}

	@Test
	public void testPasteMultipleCells2() {
		tabularData.setContent(3, 3, "\"Sample Text\"");
		tabularData.setContent(4, 3, "1 / 2");
		setRangeToCopy(3, 4, 3, 3);
		pasteToDestination(6, 4);
		assertCellContentIsEqual(3, 3, 5, 3);
		assertCellContentIsEqual(4, 3, 6, 3);
	}

	@Test
	public void testPasteMultiplceCells3() {
		tabularData.setContent(0, 0, "7 - 3");
		setRangeToCopy(0, 0, 0, 0);
		pasteToDestination(2, 0);
		assertCellContentIsEqual(0, 0, 1, 0);
		assertCellContentIsEqual(0, 0, 2, 0);
	}

	@Test
	public void testPasteColumn() {
		tabularData.setContent(2, 2, "3 * 4");
		tabularData.setContent(4, 2, "123");
		setRangeToCopy(-1, -1, 2, 2);
		pasteToDestination(10, 3);
		assertCellContentIsEqual(2, 2, 2, 3);
		assertCellContentIsEqual(4, 2, 4, 3);
	}

	@Test
	public void testPasteMultipleColumns() {
		tabularData.setContent(1, 2, "12");
		tabularData.setContent(2, 3, "14 + 2");
		setRangeToCopy(-1, -1, 2, 3);
		pasteToDestination(10, 5);
		assertCellContentIsEqual(1, 2, 1, 4);
		assertCellContentIsEqual(2, 3, 2, 5);
	}

	@Test
	public void testPasteRow() {
		tabularData.setContent(1, 2, "\"Test\"");
		tabularData.setContent(1, 3, "pi");
		setRangeToCopy(1, 1, -1, -1);
		pasteToDestination(0, 25);
		assertCellContentIsEqual(1, 2, 0, 2);
		assertCellContentIsEqual(1, 3, 0, 3);
	}

	@Test
	public void testPasteMultipleRows() {
		tabularData.setContent(2, 3, "13");
		tabularData.setContent(3, 3, "1 + 2 + 3");
		setRangeToCopy(2, 3, -1, -1);
		pasteToDestination(0, 3);
		assertCellContentIsEqual(2, 3, 0, 3);
		assertCellContentIsEqual(3, 3, 1, 3);
	}

	@Test
	public void testInvalidDestination1() {
		setRangeToCopy(1, 1, 1, 1);
		cellDragPasteHandler.setDestinationForPaste(1, 1);
		assertNull(cellDragPasteHandler.getDestinationRange());
	}

	@Test
	public void testInvalidDestination2() {
		setRangeToCopy(-1, -1, 2, 2);
		cellDragPasteHandler.setDestinationForPaste(15, 2);
		assertNull(cellDragPasteHandler.getDestinationRange());
	}

	@Test
	public void testInvalidDestination3() {
		setRangeToCopy(2, 4, -1, -1);
		cellDragPasteHandler.setDestinationForPaste(3, 0);
		assertNull(cellDragPasteHandler.getDestinationRange());
	}

	private void setRangeToCopy(int fromRow, int toRow, int fromColumn, int toColumn) {
		cellDragPasteHandler = new CellDragPasteHandler(
				TabularRange.range(fromRow, toRow, fromColumn, toColumn), tabularData);
	}

	private void pasteToDestination(int destinationRow, int destinationColumn) {
		cellDragPasteHandler.setDestinationForPaste(destinationRow, destinationColumn);
		cellDragPasteHandler.pasteToDestination();
	}

	private void assertCellContentIsEqual(int originRow, int originColumn,
		int destinationRow, int destinationColumn) {
		String copied = (String) tabularData.contentAt(originRow, originColumn);
		String pasted = (String) tabularData.contentAt(
				destinationRow, destinationColumn);
		assertEquals(String.format("The content of cell (%d, %d) should be equal to the content"
						+ "of cell (%d, %d)!", originRow, originColumn, destinationRow,
				destinationColumn), copied, pasted);
	}
}
