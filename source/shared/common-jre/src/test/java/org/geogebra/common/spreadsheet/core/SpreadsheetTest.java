package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.SpreadsheetTestHelpers.simulateCellMouseClick;
import static org.geogebra.common.spreadsheet.core.SpreadsheetTestHelpers.simulateDownArrowPress;
import static org.geogebra.common.spreadsheet.core.SpreadsheetTestHelpers.simulateRightArrowPress;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.ArrayList;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.spreadsheet.StringCapturingGraphics;
import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.spreadsheet.kernel.GeoElementCellRendererFactory;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SpreadsheetTest extends BaseUnitTest {

	private Spreadsheet spreadsheet;
	private TabularData<?> tabularData;
	private TableLayout layout;
	private UndoProvider undoProvider;
	private SpreadsheetDelegate delegate;

	@Before
	public void setupSpreadsheet() {
		tabularData = new TestTabularData();
        undoProvider = mock();
		spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory(),
                undoProvider);
		spreadsheet.setHeightForRows(20, 0, 5);
		spreadsheet.setWidthForColumns(40, 0, 5);
		layout = spreadsheet.getController().getLayout();
		resetViewport();
		spreadsheet.setViewportAdjustmentHandler(new DummyViewportAdjuster());
		delegate = mockSpreadsheetDelegate();
		spreadsheet.setSpreadsheetDelegate(delegate);
	}

	private void resetViewport() {
		Rectangle viewport = new Rectangle(0, 100, 0, 120);
		spreadsheet.setViewport(viewport);
	}

	@Test
	public void testTextDataRendering() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		tabularData.setContent(0, 0, "foo");
		tabularData.setContent(0, 1, "bar");
		spreadsheet.draw(graphics);
		assertThat(graphics.toString(), equalTo("A,B,1,foo,bar,2,3,4,5"));
	}

	@Test
	public void testSingleColumnResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.setViewport(new Rectangle(0, 120, 0, 100));
		spreadsheet.draw(graphics);
		// initially we have 2 columns
		assertThat(graphics.toString(), startsWith("A,B,1"));
		spreadsheet.handlePointerDown(layout.getRowHeaderWidth() + 40, 5, Modifiers.NONE);
		spreadsheet.handlePointerMove(layout.getRowHeaderWidth() + 10, 5, Modifiers.NONE);
		spreadsheet.handlePointerUp(layout.getRowHeaderWidth() + 10, 5, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 3
		assertThat(graphics.toString(), startsWith("A,B,C,1"));
	}

	@Test
	public void testMultiColumnResize() {
		spreadsheet.setViewport(new Rectangle(0, 140, 0, 100));
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 3 columns
		assertThat(graphics.toString(), startsWith("A,B,C,1"));
		spreadsheet.selectColumn(1, false, false);
		spreadsheet.selectColumn(2, true, false);
		spreadsheet.selectColumn(3, true, false);
		spreadsheet.selectColumn(4, true, false);
		spreadsheet.handlePointerDown(layout.getRowHeaderWidth() + 80, 5, Modifiers.NONE);
		spreadsheet.handlePointerMove(layout.getRowHeaderWidth() + 50, 5, Modifiers.NONE);
		spreadsheet.handlePointerUp(layout.getRowHeaderWidth() + 50, 5, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 6
		assertThat(graphics.toString(), startsWith("A,B,C,D,E,F,1"));
	}

	@Test
	public void testSingleRowResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		spreadsheet.handlePointerDown(15, layout.getColumnHeaderHeight() + 20, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, layout.getColumnHeaderHeight() + 40, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, layout.getColumnHeaderHeight() + 40, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 4
		assertThat(graphics.toString(), endsWith(",4"));
	}

	@Test
	public void testNoHeaderRowResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		assertThat(spreadsheet.getCursor(15, 23), equalTo(MouseCursor.DEFAULT));
		spreadsheet.handlePointerDown(15, 23, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, 23, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, 40, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// resizing header row should not work
		assertThat(graphics.toString(), endsWith(",5"));
	}

	@Test
	public void testRowNumbersAfterResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		// jump to a small viewport below the current screen
		spreadsheet.setViewport(new Rectangle(0, 140, 400, 500));
		spreadsheet.draw(graphics);
		graphics = new StringCapturingGraphics();
		// paint the whole area
		spreadsheet.setViewport(new Rectangle(0, 140, 0, 500));
		spreadsheet.draw(graphics);
		assertEquals("A,B,C,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16",
				graphics.toString());
	}

	@Test
	public void testMultiRowResize() {
		spreadsheet.setHeightForRows(20, 0, 5);
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		spreadsheet.selectRow(1, false, false);
		spreadsheet.selectRow(2, true, false);
		spreadsheet.selectRow(3, true, false);
		spreadsheet.selectRow(4, true, false);
		spreadsheet.handlePointerDown(15, layout.getColumnHeaderHeight() + 20, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, layout.getColumnHeaderHeight() + 45, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, layout.getColumnHeaderHeight() + 45, Modifiers.NONE);
		resetViewport();
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 3
		assertThat(graphics.toString(), endsWith(",3"));
	}

	@Test
	public void spreadsheetShouldRepaintAfterUpdatingSlider() {
		tabularData = new KernelTabularDataAdapter(getApp());
		tabularData.addChangeListener(spreadsheet);
		getKernel().attach((KernelTabularDataAdapter) tabularData);

		GeoNumeric slider = add("a = 3");
		tabularData.setContent(0, 0, slider);
		Mockito.verify(delegate, Mockito.times(2)).notifyRepaintNeeded();
		slider.update();
		Mockito.verify(delegate, Mockito.times(3)).notifyRepaintNeeded();
	}

	@Test
	public void spreadsheetShouldReflectColorChanges() {
		tabularData = new KernelTabularDataAdapter(getApp());
		spreadsheet = new Spreadsheet(tabularData,
				new GeoElementCellRendererFactory(graphics -> null),
				undoProvider);
		getKernel().attach((KernelTabularDataAdapter) tabularData);
		spreadsheet.setViewport(new Rectangle(0, 300, 0, 300));
		add("A1 = 1");
		spreadsheet.draw(new GGraphicsCommon());
		add("SetBackgroundColor(A1,red)");
		ArrayList<GColor> usedColors = new ArrayList<>();
		GGraphics2D g2 = new GGraphicsCommon() {
			public void setColor(GColor c) {
				usedColors.add(c);
			}
		};
		spreadsheet.draw(g2);
		assertTrue("Should contain red:" + usedColors, usedColors.contains(GColor.RED));
	}

	@Test
	public void testSelectionChangeShouldTriggerRepaint() {
		Mockito.verify(delegate, Mockito.times(0)).notifyRepaintNeeded();
		simulateCellMouseClick(spreadsheet.getController(), 0, 0, 1);
		Mockito.verify(delegate, Mockito.times(1)).notifyRepaintNeeded();
		simulateDownArrowPress(spreadsheet.getController());
		Mockito.verify(delegate, Mockito.times(2)).notifyRepaintNeeded();
		simulateRightArrowPress(spreadsheet.getController());
		Mockito.verify(delegate, Mockito.times(3)).notifyRepaintNeeded();
	}

	// Style bar

	@Test
	public void testStyleBarInitialState() {
		assertFalse(spreadsheet.getStyleBarModel().getState().isEnabled);
	}

	@Test
	public void testStyleBarWithSingleCellSelected() {
		spreadsheet.selectCell(0, 0, false, false);
		assertTrue(spreadsheet.getStyleBarModel().getState().isEnabled);
	}

	@Test
	public void testStyleBarDefaults() {
		spreadsheet.selectCell(0, 0, false, false);
		assertEquals(GeoGebraColorConstants.NEUTRAL_900,
				spreadsheet.getStyleBarModel().getState().textColor);
		assertEquals(GColor.WHITE, spreadsheet.getStyleBarModel().getState().backgroundColor);
	}

	@Test
	public void testStyleBarWithRangeSelected() {
		SpreadsheetStyleBarModel styleBarModel = spreadsheet.getStyleBarModel();
		spreadsheet.selectCell(0, 0, false, false);
		styleBarModel.setBold(true);
		// extend selection, style bar should reflect traits of first cell in selection
		spreadsheet.selectCell(2, 2, true, false);
		assertTrue(styleBarModel.getState().fontTraits.contains(SpreadsheetStyling.FontTrait.BOLD));
	}

	@Test
	public void testStyleBarChangeNotifications() {
		SpreadsheetStyleBarModel styleBarModel = spreadsheet.getStyleBarModel();
		spreadsheet.selectCell(0, 0, false, false);
		// start listening for change notifications
		Counter numberOfChangeNotifications = new Counter();
		styleBarModel.stateChanged.addListener(newState -> numberOfChangeNotifications.increment());
		styleBarModel.setBold(true); // should trigger change notification
		styleBarModel.setBold(true); // should NOT trigger change notification
		assertEquals(1, numberOfChangeNotifications.value);
		styleBarModel.setBold(false); // should trigger change notification
		assertEquals(2, numberOfChangeNotifications.value);
	}

	@Test
	@Issue("APPS-6534")
	public void testSelectionChangeShouldNotCreateUndoPoint() {
		SpreadsheetStyleBarModel styleBarModel = spreadsheet.getStyleBarModel();
		simulateCellMouseClick(spreadsheet.getController(), 0, 0, 1);
		verifyNoInteractions(undoProvider);
	}

	@Test
	public void testStyleChangesShouldCreateUndoPoints() {
		SpreadsheetStyleBarModel styleBarModel = spreadsheet.getStyleBarModel();
		simulateCellMouseClick(spreadsheet.getController(), 0, 0, 1);
		styleBarModel.setItalic(true);
		verify(undoProvider, times(1)).storeUndoInfo();
		styleBarModel.setItalic(true); // not an actual style change, should not create undo point
		verify(undoProvider, times(1)).storeUndoInfo();
		styleBarModel.setItalic(false);
		verify(undoProvider, times(2)).storeUndoInfo();
		simulateDownArrowPress(spreadsheet.getController());
		styleBarModel.setTextAlignment(SpreadsheetStyling.TextAlignment.LEFT);
		verify(undoProvider, times(3)).storeUndoInfo();
	}

	// Helpers

	private SpreadsheetDelegate mockSpreadsheetDelegate() {
		return Mockito.mock(SpreadsheetDelegate.class);
	}

	private static final class Counter {
		int value = 0;

		void increment() {
			value++;
		}
	}
}