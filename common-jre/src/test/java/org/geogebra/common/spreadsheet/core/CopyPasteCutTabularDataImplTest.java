package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.junit.Test;

public class CopyPasteCutTabularDataImplTest extends BaseUnitTest {

	private KernelTabularDataAdapter tabularData;
	private CopyPasteCutTabularDataImpl<?> copyPasteCut;
	private final TableLayout layout = new TableLayout(100, 26, 10, 10);
	private final SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();

	@Override
	public void setup() {
		super.setup();
		tabularData = new KernelTabularDataAdapter(getSettings().getSpreadsheet(), getKernel());
		copyPasteCut = new CopyPasteCutTabularDataImpl<>(tabularData, getClipboard(),
				layout, selectionController);
	}

	@Test
	public void testCopyingRowIncludesAllCellsInGivenRow() {
		copyPasteCut.copyDeep(TabularRange.range(-1, -1, 1, 1));
		assertEquals(100, copyPasteCut.getInternalClipboard().getSourceRange().getHeight());
	}

	@Test
	public void testCopyingColumnIncludesAllCellsInGivenColumn() {
		copyPasteCut.copyDeep(TabularRange.range(2, 3, -1, -1));
		assertEquals(26, copyPasteCut.getInternalClipboard().getSourceRange().getWidth());
	}

	@Test
	public void testCopyingEntireSpreadsheetIncludesAllCells() {
		copyPasteCut.copyDeep(TabularRange.range(-1, -1, -1, -1));
		assertEquals(100, copyPasteCut.getInternalClipboard().getSourceRange().getHeight());
		assertEquals(26, copyPasteCut.getInternalClipboard().getSourceRange().getWidth());
	}

	@Test
	public void testPastingSelectionAddsRows1() {
		copyPasteCut.copyDeep(TabularRange.range(0, 1, 1, 1));
		copyPasteCut.paste(TabularRange.range(99, 99, 1, 1));
		assertEquals(101, tabularData.numberOfRows());
	}

	@Test
	public void testPastingSelectionAddsRows2() {
		copyPasteCut.copyDeep(TabularRange.range(2, 4, 1, 2));
		copyPasteCut.paste(TabularRange.range(99, 99, 2, 3));
		assertEquals(102, tabularData.numberOfRows());
	}

	@Test
	public void testPastingSelectionAddsColumns1() {
		copyPasteCut.copyDeep(TabularRange.range(3, 3, 1, 2));
		copyPasteCut.paste(TabularRange.range(5, 5, 25, 25));
		assertEquals(27, tabularData.numberOfColumns());
	}

	@Test
	public void testPastingSelectionAddsColumns2() {
		copyPasteCut.copyDeep(TabularRange.range(5, 6, 2, 5));
		copyPasteCut.paste(TabularRange.range(7, 7, 25, 25));
		assertEquals(29, tabularData.numberOfColumns());
	}

	@Test
	public void testPastingRowClearsExistingCells() {
		tabularData.setContent(1, 3, add("123"));
		tabularData.setContent(3, 5, add("123"));
		copyPasteCut.copyDeep(TabularRange.range(1, 1, -1, -1));
		copyPasteCut.paste(TabularRange.range(3, 3, -1, -1));
		assertNull(tabularData.contentAt(3, 5));
	}

	@Test
	public void testPastingRows1() {
		tabularData.setContent(2, 3, add("1 + 2"));
		copyPasteCut.copyDeep(TabularRange.range(2, 2, -1, -1));
		copyPasteCut.paste(TabularRange.range(3, 3, -1, -1));
		assertCellContentIsEqual(2, 3, 3, 3);
	}

	@Test
	public void testPastingRows2() {
		tabularData.setContent(2, 3, add("1 + 2"));
		tabularData.setContent(3, 4, add("1 + 2"));
		copyPasteCut.copyDeep(TabularRange.range(2, 3, -1, -1));
		copyPasteCut.paste(TabularRange.range(4, 5, -1, -1));
		assertCellContentIsEqual(2, 3, 4, 3);
		assertCellContentIsEqual(3, 4, 5, 4);
	}

	@Test
	public void testPastingRows3() {
		tabularData.setContent(4, 4, add("8 / 3"));
		copyPasteCut.cut(TabularRange.range(4, 4, -1, -1));
		copyPasteCut.paste(TabularRange.range(6, 6, 1, 1));
		assertEquals(27, tabularData.numberOfColumns());
	}

	@Test
	public void testPastingRowToLeftmostCell() {
		tabularData.setContent(2, 4, add("1 + 2 * 3"));
		copyPasteCut.copyDeep(TabularRange.range(2, 2, -1, -1));
		copyPasteCut.paste(TabularRange.range(3, 3, 0, 0));
		copyPasteCut.selectPastedContent();
		Selection lastSelection = selectionController.getLastSelection();
		assertNotNull(lastSelection);
		assertTrue(lastSelection.getRange().isRow());
	}

	@Test
	public void testPastingColumns1() {
		tabularData.setContent(1, 4, add("4 - 3"));
		copyPasteCut.copyDeep(TabularRange.range(-1, -1, 4, 4));
		copyPasteCut.paste(TabularRange.range(-1, -1, 6, 6));
		assertCellContentIsEqual(1, 4, 1, 6);
	}

	@Test
	public void testPastingColumns2() {
		tabularData.setContent(2, 3, add("4 / 7"));
		tabularData.setContent(4, 4, add("4 * 2"));
		copyPasteCut.copyDeep(TabularRange.range(-1, -1, 3, 4));
		copyPasteCut.paste(TabularRange.range(-1, -1, 5, 5));
		assertCellContentIsEqual(2, 3, 2, 5);
		assertCellContentIsEqual(4, 4, 4, 6);
	}

	@Test
	public void testPastingColumns3() {
		tabularData.setContent(1, 4, add("123"));
		copyPasteCut.cut(TabularRange.range(-1, -1, 4, 4));
		copyPasteCut.paste(TabularRange.range(1, 1, 5, 5));
		assertEquals(101, tabularData.numberOfRows());
	}

	@Test
	public void testPastingColumnToTopmostCell() {
		tabularData.setContent(1, 3, add("\"Sample Text\""));
		copyPasteCut.copyDeep(TabularRange.range(-1, -1, 3, 3));
		copyPasteCut.paste(TabularRange.range(-1, -1, 5, 5));
		copyPasteCut.selectPastedContent();
		Selection lastSelection = selectionController.getLastSelection();
		assertNotNull(lastSelection);
		assertTrue(lastSelection.getRange().isColumn());
	}

	private void assertCellContentIsEqual(int originRow, int originColumn,
			int destinationRow, int destinationColumn) {
		assertEquals(String.format("The content of cell (%d, %d) should be equal to the content "
								+ "of cell (%d, %d)!", originRow, originColumn, destinationRow,
						destinationColumn), getValueStringForCell(originRow, originColumn),
				getValueStringForCell(destinationRow, destinationColumn));
	}

	private String getValueStringForCell(int row, int column) {
		return lookup(GeoElementSpreadsheet.getSpreadsheetCellName(column, row))
				.toValueString(StringTemplate.defaultTemplate);
	}

	private ClipboardInterface getClipboard() {
		return new ClipboardInterface() {

			private String content;

			@Override
			public String getContent() {
				return content;
			}

			@Override
			public void setContent(String content) {
				this.content = content;
			}
		};
	}

}
