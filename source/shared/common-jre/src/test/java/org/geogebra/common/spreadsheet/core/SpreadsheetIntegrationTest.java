package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.SpreadsheetTestHelpers.simulateCellMouseClick;
import static org.geogebra.common.spreadsheet.core.SpreadsheetTestHelpers.simulateColumnResize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.BaseAppTestSetup;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.settings.SpreadsheetSettingsAdapter;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.test.LocalizationCommonUTF;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * A Spreadsheet test with App, Kernel, and GuiManager (for full undo testing) integration.
 */
public final class SpreadsheetIntegrationTest extends BaseAppTestSetup {

	private Spreadsheet spreadsheet;
	private TabularData<?> tabularData;

	@BeforeAll
	public static void setupOnce() {
		// required by MathField
		FactoryProvider.setInstance(new FactoryProviderCommon());
		// required by StringTemplate static initializer
		FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
	}

	@BeforeEach
	public void setup() {
		setApp(createAppCommonWithGuiManager());

		getApp().setUndoActive(true);
		UndoProvider undoProvider = getApp().getUndoManager();

		tabularData = new KernelTabularDataAdapter(getApp());
		getKernel().attach((KernelTabularDataAdapter) tabularData);
		spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory(),
				undoProvider);
		spreadsheet.setViewportAdjustmentHandler(new DummyViewportAdjuster());
		new SpreadsheetSettingsAdapter(spreadsheet, getApp()).registerListeners();

		spreadsheet.setHeightForRows(20, 0, 5);
		spreadsheet.setWidthForColumns(40, 0, 5);
		spreadsheet.setViewport(new Rectangle(0, 100, 0, 120));
	}

	private AppCommon createAppCommonWithGuiManager() {
		return new AppCommon(new LocalizationCommonUTF(2), new AwtFactoryCommon(),
				new AppConfigDefault()) {
			@Override
			public boolean isUsingFullGui() {
				return true;
			}

			@Override
			public GuiManagerInterface getGuiManager() {
				return Mockito.mock(GuiManagerInterface.class);
			}
		};
	}

	@Test
	@Issue("APPS-6566")
	public void testInitialSize() {
		SpreadsheetSettings spreadsheetSettings = getApp().getSettings().getSpreadsheet();
		spreadsheetSettings.setColumnsNoFire(3);
		spreadsheetSettings.getColumnWidths().put(1, 500.0);
		Spreadsheet spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory(),
				null);
		new SpreadsheetSettingsAdapter(spreadsheet, getApp()).registerListeners();
		Assertions.assertEquals(500 + 2 * 120 + 52, spreadsheet.getTotalWidth());
	}

	@Test
	public void testDefaultTextAlignment() {
		tabularData.setContent(0, 0, new GeoText(getConstruction(), "GeoText"));
		tabularData.setContent(1, 0, new GeoNumeric(getConstruction(), 123));
		spreadsheet.getController().select(new TabularRange(0, 0), false, false);
		assertEquals(SpreadsheetStyling.TextAlignment.LEFT,
				spreadsheet.getStyleBarModel().getState().textAlignment);
		spreadsheet.getController().select(new TabularRange(2, 0), false, false);
		assertEquals(SpreadsheetStyling.TextAlignment.RIGHT,
				spreadsheet.getStyleBarModel().getState().textAlignment);
	}

	@Test
	public void testUndoStyleChange() {
		UndoManager undoManager = getApp().getUndoManager();
		Construction construction = getKernel().getConstruction();
		SpreadsheetStyleBarModel styleBarModel = spreadsheet.getStyleBarModel();

		// select a cell
		simulateCellMouseClick(spreadsheet.getController(), 0, 0, 1);
		String xmlBeforeStyling = construction.getCurrentUndoXML(false).toString();
		assertNull(spreadsheet.getStyling().getCellFormatXml());

		// apply styling
		styleBarModel.setItalic(true);
		assertTrue(spreadsheet.getStyling().getFontTraits(0, 0)
				.contains(SpreadsheetStyling.FontTrait.ITALIC));
		String xmlAfterStyling = construction.getCurrentUndoXML(false).toString();
		assertNotEquals(xmlBeforeStyling, xmlAfterStyling);
		assertEquals("0,0,f,2", spreadsheet.getStyling().getCellFormatXml());

		// undo the styling change
		undoManager.undo();
		String xmlAfterUndo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlBeforeStyling, xmlAfterUndo);
		assertFalse(spreadsheet.getStyling().getFontTraits(0, 0)
				.contains(SpreadsheetStyling.FontTrait.ITALIC));
		assertNull(spreadsheet.getStyling().getCellFormatXml());

		// redo the styling change
		undoManager.redo();
		String xmlAfterRedo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlAfterStyling, xmlAfterRedo);
		assertTrue(spreadsheet.getStyling().getFontTraits(0, 0)
				.contains(SpreadsheetStyling.FontTrait.ITALIC));
		assertEquals("0,0,f,2", spreadsheet.getStyling().getCellFormatXml());

		simulateCellMouseClick(spreadsheet.getController(), 0, 0, 1);
		assertTrue(styleBarModel.getState().fontTraits
				.contains(SpreadsheetStyling.FontTrait.ITALIC));
	}

	@Test
	public void testUndoColumnResize() {
		UndoManager undoManager = getApp().getUndoManager();
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

		// undo column A resize
		undoManager.undo();
		String xmlAfterSecondUndo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlAfterSecondUndo, initialXml);

		// redo column A resize
		undoManager.redo();
		String xmlAfterFirstRedo = construction.getCurrentUndoXML(false).toString();
		assertEquals(xmlAfterFirstRedo, xmlAfterColAResize);
	}

	@Issue("APPS-6783")
	@Test
	public void testUndoColumnDeletion() {
		// select first two columns (out of 26 total)
		spreadsheet.selectColumn(0, false, false);
		spreadsheet.selectColumn(1, true, false);
		// delete the two columns
		spreadsheet.getController().deleteColumnAt(0);
		assertEquals(24, spreadsheet.getController().getLayout().numberOfColumns());
		// undo column deletion
		getConstruction().getUndoManager().undo();
		assertEquals(26, spreadsheet.getController().getLayout().numberOfColumns());
	}
}