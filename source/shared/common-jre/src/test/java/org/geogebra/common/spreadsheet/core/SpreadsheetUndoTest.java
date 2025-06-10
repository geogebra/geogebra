package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.SpreadsheetTestHelpers.simulateCellMouseClick;
import static org.geogebra.common.spreadsheet.core.SpreadsheetTestHelpers.simulateColumnResize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.settings.SpreadsheetSettingsAdapter;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.junit.Before;
import org.junit.Test;

public class SpreadsheetUndoTest extends BaseUnitTest {

	private Spreadsheet spreadsheet;
	private UndoManager undoManager;

	@Before
	public void setup() {
		super.setup();
		AppCommon app = getApp();
		app.setUndoActive(true);

		KernelTabularDataAdapter tabularData = new KernelTabularDataAdapter(getApp());
		undoManager = getKernel().getConstruction().getUndoManager();
		spreadsheet = new Spreadsheet(tabularData,
				new SpreadsheetTest.TestCellRenderableFactory(),
				undoManager);
		new SpreadsheetSettingsAdapter(spreadsheet, getApp()).registerListeners();
	}

	@Test
	public void testUndoStyleChange() {
		Construction construction = getKernel().getConstruction();
		SpreadsheetStyleBarModel styleBarModel = spreadsheet.getStyleBarModel();

		// select a cell
		simulateCellMouseClick(spreadsheet.getController(), 0, 0, 1);
		String xmlBeforeStyling = construction.getCurrentUndoXML(false).toString();
		assertNull(spreadsheet.getStyling().getCellFormatXml());

		// apply styling
		styleBarModel.setItalic(true);
		String xmlAfterStyling = construction.getCurrentUndoXML(false).toString();
		assertNotEquals(xmlBeforeStyling, xmlAfterStyling);
		assertEquals("0,1,f,2", spreadsheet.getStyling().getCellFormatXml());

		// undo the styling change
		undoManager.undo();
		String xmlAfterUndo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlBeforeStyling, xmlAfterUndo);
		spreadsheet.getStyling().getFontTraits(0, 0).isEmpty();
		assertNull(spreadsheet.getStyling().getCellFormatXml());

		// redo the styling change
		undoManager.redo();
		String xmlAfterRedo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlAfterStyling, xmlAfterRedo);
		spreadsheet.getStyling().getFontTraits(0, 0)
				.contains(SpreadsheetStyling.FontTrait.ITALIC);
		assertEquals("0,1,f,2", spreadsheet.getStyling().getCellFormatXml());

		simulateCellMouseClick(spreadsheet.getController(), 0, 0, 1);
		styleBarModel.getState().fontTraits.contains(SpreadsheetStyling.FontTrait.ITALIC);
	}

	@Test
	public void testUndoColumnResize() {
		Construction construction = getKernel().getConstruction();
		String initialXml = construction.getCurrentUndoXML(false).toString();

		// resize column A
		simulateColumnResize(spreadsheet.getController(), 0, 10);
		String xmlAfterColAResize = construction.getCurrentUndoXML(false).toString();
		assertNotEquals(initialXml, xmlAfterColAResize);

		// resize column B
		simulateColumnResize(spreadsheet.getController(), 1, 10);
		String xmlAfterColBResize = construction.getCurrentUndoXML(false).toString();
		assertNotEquals(xmlAfterColAResize, xmlAfterColBResize);

		// undo the column B resize
		undoManager.undo();
		String xmlAfterFirstUndo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlAfterFirstUndo, xmlAfterColAResize);

		// undo columnn A resize
		undoManager.undo();
		String xmlAfterSecondUndo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlAfterSecondUndo, initialXml);

		// redo column A resize
		undoManager.redo();
		String xmlAfterFirstRedo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlAfterFirstRedo, xmlAfterColAResize);
	}
}