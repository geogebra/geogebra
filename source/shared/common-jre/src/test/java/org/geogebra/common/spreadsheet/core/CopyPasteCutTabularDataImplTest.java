/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.spreadsheet.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CopyPasteCutTabularDataImplTest extends BaseAppTestSetup {

	private KernelTabularDataAdapter tabularData;
	private CopyPasteCutTabularDataImpl<?> copyPasteCut;
	private final TableLayout layout = new TableLayout(100, 26, 10, 10);
	private final SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();
	private final TestClipboard clipboard = new TestClipboard();

	@BeforeEach
	public void setup() {
		setupClassicApp();
		tabularData = new KernelTabularDataAdapter(getApp());
		copyPasteCut = new CopyPasteCutTabularDataImpl<>(tabularData, clipboard,
				layout, selectionController);
	}

	@Test
	public void testPastingCells1() {
		tabularData.setContent(1, 1, evaluateGeoElement("12"));
		copyPasteCut.copyDeep(new TabularRange(1, 1, 1, 1));
		paste(new TabularRange(3, 3, 3, 3));
		assertCellContentIsEqual(1, 1, 3, 3);
		assertLabelIsEqualTo("D4", 3, 3);
	}

	@Test
	public void testPastingCells2() {
		tabularData.setContent(2, 2, evaluateGeoElement("123"));
		tabularData.setContent(3, 2, evaluateGeoElement("321"));
		copyPasteCut.copyDeep(new TabularRange(2, 2, 3, 2));
		paste(new TabularRange(5, 4, 7, 4));
		assertCellContentIsEqual(2, 2, 5, 4);
		assertCellContentIsEqual(3, 2, 6, 4);
		assertLabelIsEqualTo("E6", 5, 4);
		assertLabelIsEqualTo("E7", 6, 4);
	}

	@Test
	public void testPastingCells3() {
		tabularData.setContent(4, 4, evaluateGeoElement("1 + 3"));
		copyPasteCut.copyDeep(new TabularRange(4, 4, 4, 4));
		paste(new TabularRange(3, 1, 1, 1));
		assertCellContentIsEqual(4, 4, 1, 1);
		assertLabelIsEqualTo("B2", 1, 1);
	}

	@Test
	public void testPastingCells4() {
		tabularData.setContent(1, 3, evaluateGeoElement("7 / 4"));
		copyPasteCut.copyDeep(new TabularRange(1, 3, 1, 3));
		paste(new TabularRange(2, 4, 2, 1));
		assertCellContentIsEqual(1, 3, 2, 1);
		assertLabelIsEqualTo("B3", 2, 1);
	}

	@Test
	public void testCopyingRowIncludesAllCellsInGivenRow() {
		copyPasteCut.copyDeep(new TabularRange(-1, 1, -1, 1));
		assertEquals(100, copyPasteCut.getInternalClipboard().getSourceRange().getHeight());
	}

	@Test
	public void testCopyingColumnIncludesAllCellsInGivenColumn() {
		copyPasteCut.copyDeep(new TabularRange(2, -1, 3, -1));
		assertEquals(26, copyPasteCut.getInternalClipboard().getSourceRange().getWidth());
	}

	@Test
	public void testCopyingEntireSpreadsheetIncludesAllCells() {
		copyPasteCut.copyDeep(new TabularRange(-1, -1, -1, -1));
		assertEquals(100, copyPasteCut.getInternalClipboard().getSourceRange().getHeight());
		assertEquals(26, copyPasteCut.getInternalClipboard().getSourceRange().getWidth());
	}

	@Test
	public void testPastingSelectionAddsRows1() {
		copyPasteCut.copyDeep(new TabularRange(0, 1, 1, 1));
		paste(new TabularRange(99, 1, 99, 1));
		assertEquals(101, tabularData.numberOfRows());
	}

	@Test
	public void testPastingSelectionAddsRows2() {
		copyPasteCut.copyDeep(new TabularRange(2, 1, 4, 2));
		paste(new TabularRange(99, 2, 99, 3));
		assertEquals(102, tabularData.numberOfRows());
	}

	@Test
	public void testPastingSelectionAddsColumns1() {
		copyPasteCut.copyDeep(new TabularRange(3, 1, 3, 2));
		paste(new TabularRange(5, 25, 5, 25));
		assertEquals(27, tabularData.numberOfColumns());
	}

	@Test
	public void testPastingSelectionAddsColumns2() {
		copyPasteCut.copyDeep(new TabularRange(5, 2, 6, 5));
		paste(new TabularRange(7, 25, 7, 25));
		assertEquals(29, tabularData.numberOfColumns());
	}

	@Test
	public void testPastingRowClearsExistingCells() {
		tabularData.setContent(1, 3, evaluateGeoElement("123"));
		tabularData.setContent(3, 5, evaluateGeoElement("123"));
		copyPasteCut.copyDeep(new TabularRange(1, -1, 1, -1));
		paste(new TabularRange(3, -1, 3, -1));
		assertNull(tabularData.contentAt(3, 5));
	}

	@Test
	public void testPastingRows1() {
		tabularData.setContent(2, 3, evaluateGeoElement("1 + 2"));
		copyPasteCut.copyDeep(new TabularRange(2, -1, 2, -1));
		paste(new TabularRange(3, -1, 3, -1));
		assertCellContentIsEqual(2, 3, 3, 3);
		assertLabelIsEqualTo("D4", 3, 3);
	}

	@Test
	public void testPastingRows2() {
		tabularData.setContent(2, 3, evaluateGeoElement("1 + 2"));
		tabularData.setContent(3, 4, evaluateGeoElement("1 + 2"));
		copyPasteCut.copyDeep(new TabularRange(2, -1, 3, -1));
		paste(new TabularRange(4, -1, 5, -1));
		assertCellContentIsEqual(2, 3, 4, 3);
		assertCellContentIsEqual(3, 4, 5, 4);
		assertLabelIsEqualTo("D5", 4, 3);
		assertLabelIsEqualTo("E6", 5, 4);
	}

	@Test
	public void testPastingRows3() {
		tabularData.setContent(4, 4, evaluateGeoElement("8 / 3"));
		copyPasteCut.cut(new TabularRange(4, -1, 4, -1));
		paste(new TabularRange(6, 1, 6, 1));
		assertEquals(27, tabularData.numberOfColumns());
	}

	@Test
	public void testPastingRowToLeftmostCell() {
		tabularData.setContent(2, 4, evaluateGeoElement("1 + 2 * 3"));
		copyPasteCut.copyDeep(new TabularRange(2, -1, 2, -1));
		paste(new TabularRange(3, 0, 3, 0));
		copyPasteCut.selectPastedContent();
		Selection lastSelection = selectionController.getLastSelection();
		assertNotNull(lastSelection);
		assertTrue(lastSelection.getRange().isContiguousRows());
	}

	@Test
	public void testPastingColumns1() {
		tabularData.setContent(1, 4, evaluateGeoElement("4 - 3"));
		copyPasteCut.copyDeep(new TabularRange(-1, 4, -1, 4));
		paste(new TabularRange(-1, 6, -1, 6));
		assertCellContentIsEqual(1, 4, 1, 6);
		assertLabelIsEqualTo("G2", 1, 6);
	}

	@Test
	public void testPastingColumns2() {
		tabularData.setContent(2, 3, evaluateGeoElement("4 / 7"));
		tabularData.setContent(4, 4, evaluateGeoElement("4 * 2"));
		copyPasteCut.copyDeep(new TabularRange(-1, 3, -1, 4));
		paste(new TabularRange(-1, 5, -1, 5));
		assertCellContentIsEqual(2, 3, 2, 5);
		assertCellContentIsEqual(4, 4, 4, 6);
		assertLabelIsEqualTo("F3", 2, 5);
		assertLabelIsEqualTo("G5", 4, 6);
	}

	@Test
	public void testPastingColumns3() {
		tabularData.setContent(1, 4, evaluateGeoElement("123"));
		copyPasteCut.cut(new TabularRange(-1, 4, -1, 4));
		paste(new TabularRange(1, 5, 1, 5));
		assertEquals(101, tabularData.numberOfRows());
	}

	@Test
	public void testPastingColumnToTopmostCell() {
		tabularData.setContent(1, 3, evaluateGeoElement("\"Sample Text\""));
		copyPasteCut.copyDeep(new TabularRange(-1, 3, -1, 3));
		paste(new TabularRange(-1, 5, -1, 5));
		copyPasteCut.selectPastedContent();
		Selection lastSelection = selectionController.getLastSelection();
		assertNotNull(lastSelection);
		assertTrue(lastSelection.getRange().isContiguousColumns());
	}

	@Test
	public void testPastingShouldRegardDynamicReferences1() {
		tabularData.setContent(0, 0, evaluateGeoElement("=12"));
		tabularData.setContent(1, 0, evaluateGeoElement("=A1 + 3"));
		copyPasteCut.copyDeep(new TabularRange(1, 0, 1, 0));
		paste(new TabularRange(2, 0, 2, 0));
		assertEquals("18", getValueStringForCell(2, 0));
	}

	@Test
	public void testPastingShouldRegardDynamicReferences2() {
		tabularData.setContent(0, 0, evaluateGeoElement("=3"));
		tabularData.setContent(0, 1, evaluateGeoElement("=A1 + 4"));
		copyPasteCut.copyDeep(new TabularRange(0, 1, 0, 1));
		paste(new TabularRange(0, 2, 0, 2));
		assertEquals("11", getValueStringForCell(0, 2));
	}

	@Test
	public void testPastingShouldRegardDynamicReferences3() {
		tabularData.setContent(1, 1, evaluateGeoElement("=2"));
		tabularData.setContent(2, 1, evaluateGeoElement("=B2 * 2"));
		copyPasteCut.copyDeep(new TabularRange(2, 1, 2, 1));
		paste(new TabularRange(3, 1, 5, 1));
		assertEquals("8", getValueStringForCell(3, 1));
		assertEquals("16", getValueStringForCell(4, 1));
		assertEquals("32", getValueStringForCell(5, 1));
		assertLabelIsEqualTo("B6", 5, 1);
	}

	@Test
	public void testPastingShouldRegardDynamicReferences4() {
		tabularData.setContent(5, 1, evaluateGeoElement("=32"));
		tabularData.setContent(4, 1, evaluateGeoElement("=B6 / 2"));
		copyPasteCut.copyDeep(new TabularRange(4, 1, 4, 1));
		paste(new TabularRange(3, 1, 1, 1));
		assertEquals("8", getValueStringForCell(3, 1));
		assertEquals("4", getValueStringForCell(2, 1));
		assertEquals("2", getValueStringForCell(1, 1));
		assertLabelIsEqualTo("B4", 3, 1);
	}

	@Test
	public void testPastingShouldRegardDynamicReferences5() {
		tabularData.setContent(3, 5, evaluateGeoElement("=30"));
		tabularData.setContent(3, 4, evaluateGeoElement("=F4 + 3"));
		copyPasteCut.copyDeep(new TabularRange(3, 4, 3, 4));
		paste(new TabularRange(3, 3, 3, 1));
		assertEquals("42", getValueStringForCell(3, 1));
		assertEquals("39", getValueStringForCell(3, 2));
		assertEquals("36", getValueStringForCell(3, 3));
		assertLabelIsEqualTo("D4", 3, 3);
	}

	@Test
	public void testPastingShouldRegardDynamicReferences6() {
		tabularData.setContent(1, 1, evaluateGeoElement("=12"));
		tabularData.setContent(2, 1, evaluateGeoElement("=B2 + 3"));
		copyPasteCut.copyDeep(new TabularRange(2, 1, 2, 1));
		paste(new TabularRange(3, 3, 3, 3));
		assertEquals("?", getValueStringForCell(3, 3));
		assertEquals("?", getValueStringForCell(2, 3));
	}

	@Test
	public void testPasteExternal() {
		copyPasteCut.paste(new TabularRange(2, 3),
				new String[][]{{"1"}, {"2"}});
		assertEquals("1", getValueStringForCell(2, 3));
		assertEquals("2", getValueStringForCell(3, 3));
	}

	@Test
	public void testPasteExternalMultiple() {
		copyPasteCut.paste(new TabularRange(2, 3, 2, 4),
				new String[][]{{"1"}, {"2"}});
		assertEquals("1", getValueStringForCell(2, 3));
		assertEquals("2", getValueStringForCell(3, 3));
		assertEquals("1", getValueStringForCell(2, 4));
		assertEquals("2", getValueStringForCell(3, 4));
	}

	@Test
	public void internalClipboardShouldHavePriority() {
		tabularData.setContent(1, 1, evaluateGeoElement("=1"));
		tabularData.setContent(2, 1, evaluateGeoElement("=2"));
		copyPasteCut.copy(new TabularRange(1, 1, 2, 1));
		assertEquals("1\n2", clipboard.getContent());
		copyPasteCut.readExternalClipboard(Assertions::assertNull);
		clipboard.setContent("3");
		copyPasteCut.readExternalClipboard(content -> assertEquals("3", content));
	}

	@Test
	public void testCutRemovesElement() {
		tabularData.setContent(1, 1, evaluateGeoElement("=1"));
		int constructionSteps = getKernel().getConstruction().steps();
		copyPasteCut.cut(new TabularRange(1, 1, 1, 1));
		assertEquals(constructionSteps - 1, getKernel().getConstruction().steps());
	}

	@Test
	public void testCutRemovesElements() {
		tabularData.setContent(1, 1, evaluateGeoElement("=1"));
		tabularData.setContent(2, 1, evaluateGeoElement("3 + 2"));
		int constructionSteps = getKernel().getConstruction().steps();
		copyPasteCut.cut(new TabularRange(1, 1, 2, 1));
		assertEquals(constructionSteps - 2, getKernel().getConstruction().steps());
	}

	private void paste(TabularRange range) {
		copyPasteCut.paste(range, null);
	}

	private void assertCellContentIsEqual(int originRow, int originColumn,
			int destinationRow, int destinationColumn) {
		assertEquals(getValueStringForCell(originRow, originColumn),
				getValueStringForCell(destinationRow, destinationColumn),
				String.format("The content of cell (%d, %d) should be equal to the content "
								+ "of cell (%d, %d)!", originRow, originColumn, destinationRow,
						destinationColumn));
	}

	private void assertLabelIsEqualTo(String expected, int row, int column) {
		GeoElement geo = tabularData.contentAt(row, column);
		assert geo != null;
		assertEquals(expected, geo.getLabelSimple(), "The label does not match!");
	}

	private String getValueStringForCell(int row, int column) {
		return lookup(GeoElementSpreadsheet.getSpreadsheetCellName(column, row))
				.toValueString(StringTemplate.defaultTemplate);
	}

}
