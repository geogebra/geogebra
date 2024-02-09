package org.geogebra.common.spreadsheet;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.awt.Button;
import java.awt.event.KeyEvent;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.spreadsheet.core.CellRenderableFactory;
import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetController;
import org.geogebra.common.spreadsheet.core.TableLayout;
import org.geogebra.common.spreadsheet.core.ViewportAdjuster;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.Scrollable;
import org.geogebra.common.util.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SpreadsheetTest extends BaseUnitTest {

	private final int colHeader = TableLayout.DEFAUL_CELL_HEIGHT;
	private final int rowHeader = TableLayout.DEFAULT_ROW_HEADER_WIDTH;
	private Spreadsheet spreadsheet;
	private TestTabularData tabularData;
	private SpreadsheetController controller;
	private Rectangle viewport;
	private ViewportAdjuster viewportAdjuster;

	@Before
	public void setupSpreadsheet() {
		tabularData = new TestTabularData();
		spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory());
		spreadsheet.setHeightForRows(20, 0, 5);
		spreadsheet.setWidthForColumns(40, 0, 5);
		viewport = new Rectangle(0, 100, 0, 120);
		spreadsheet.setViewport(viewport);
		controller = spreadsheet.getController();
		viewportAdjuster = getViewportAdjuster();
		controller.setViewportAdjuster(viewportAdjuster);
	}

	@Test
	public void testTextDataRendering() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		tabularData.setContent(0, 0, "foo");
		tabularData.setContent(0, 1, "bar");
		spreadsheet.draw(graphics);
		assertThat(graphics.toString(), equalTo("col0,col1,1,foo,bar,2,3,4,5"));
	}

	@Test
	public void testSingleColumnResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.setViewport(new Rectangle(0, 120, 0, 100));
		spreadsheet.draw(graphics);
		// initially we have 2 columns
		assertThat(graphics.toString(), startsWith("col0,col1,1"));
		spreadsheet.handlePointerDown(rowHeader + 40, 5, Modifiers.NONE);
		spreadsheet.handlePointerMove(rowHeader + 10, 5, Modifiers.NONE);
		spreadsheet.handlePointerUp(rowHeader + 10, 5, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 3
		assertThat(graphics.toString(), startsWith("col0,col1,col2,1"));
	}

	@Test
	public void testMultiColumnResize() {
		spreadsheet.setViewport(new Rectangle(0, 140, 0, 100));
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 3 columns
		assertThat(graphics.toString(), startsWith("col0,col1,col2,1"));
		spreadsheet.getController().selectColumn(1, false, false);
		spreadsheet.getController().selectColumn(2, true, false);
		spreadsheet.getController().selectColumn(3, true, false);
		spreadsheet.getController().selectColumn(4, true, false);
		spreadsheet.handlePointerDown(rowHeader + 80, 5, Modifiers.NONE);
		spreadsheet.handlePointerMove(rowHeader + 50, 5, Modifiers.NONE);
		spreadsheet.handlePointerUp(rowHeader + 50, 5, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 6
		assertThat(graphics.toString(), startsWith("col0,col1,col2,col3,col4,col5,1"));
	}

	@Test
	public void testSingleRowResize() {
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		spreadsheet.handlePointerDown(15, colHeader + 20, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, colHeader + 40, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, colHeader + 40, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 4
		assertThat(graphics.toString(), endsWith(",4"));
	}

	@Test
	public void testNoHeaderRowResize() {
		controller.setViewportAdjuster(null);
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
	public void testMultiRowResize() {
		spreadsheet.setHeightForRows(20, 0, 5);
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		spreadsheet.getController().selectRow(1, false, false);
		spreadsheet.getController().selectRow(2, true, false);
		spreadsheet.getController().selectRow(3, true, false);
		spreadsheet.getController().selectRow(4, true, false);
		spreadsheet.handlePointerDown(15, colHeader + 20, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, colHeader + 45, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, colHeader + 45, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 3
		assertThat(graphics.toString(), endsWith(",3"));
	}

	@Test
	public void testViewportIsAdjustedRightwardsWithArrowKey() {
		controller.selectCell(1, 1, false, false);
		fakeRightArrowPress(viewport);
		assertNotEquals(0, viewportAdjuster.getScrollable().getHorizontalScrollPosition());
	}

	@Test
	public void testViewportIsAdjustedRightwardsWithMouseClick() {
		spreadsheet.handlePointerDown(rowHeader + 90, colHeader + 10, Modifiers.NONE);
		assertNotEquals(0, viewportAdjuster.getScrollable().getHorizontalScrollPosition());
	}

	@Test
	public void testViewportIsNotAdjustedRightwardsWithArrowKey() {
		viewport = new Rectangle(0, 500, 0, 500);
		controller.selectCell(2, 0, false, false);
		fakeRightArrowPress(viewport);
		assertEquals(0, viewportAdjuster.getScrollable().getHorizontalScrollPosition());
	}

	@Test
	public void testViewportIsNotAdjustedRightwardsWithMouseClick() {
		viewport = new Rectangle(0, 140, 0, 100);
		spreadsheet.setViewport(viewport);
		spreadsheet.handlePointerDown(rowHeader + 60, colHeader + 5, Modifiers.NONE);
		assertEquals(0, viewportAdjuster.getScrollable().getHorizontalScrollPosition());
	}

	@Test
	public void testViewportShouldNotBeAdjustedWhenMovingLeftAtLeftmostPositionWithArrowKey() {
		controller.selectCell(0, 0, false, false);
		fakeLeftArrowPress(viewport);
		assertEquals(0, viewportAdjuster.getScrollable().getHorizontalScrollPosition());
	}

	@Test
	public void testViewportIsNotAdjustedHorizontallyWithArrowKey() {
		viewport = new Rectangle(0, 300, 0, 300);
		controller.selectCell(2, 0, false, false);
		fakeRightArrowPress(viewport);
		assertEquals(0, viewportAdjuster.getScrollable().getHorizontalScrollPosition());
		fakeLeftArrowPress(viewport);
		assertEquals(0, viewportAdjuster.getScrollable().getHorizontalScrollPosition());
	}

	@Test
	public void testViewportIsAdjustedDownwardsWithArrowKey() {
		viewport = new Rectangle(0, 300, 0, 100);
		controller.selectCell(1, 1, false, false);
		fakeDownArrowPress(viewport);
		assertNotEquals(0, viewportAdjuster.getScrollable().getVerticalScrollPosition());
	}

	@Test
	public void testViewportIsAdjustedDownwardsWithMouseClick() {
		spreadsheet.handlePointerDown(rowHeader + 10, colHeader + 80, Modifiers.NONE);
		assertNotEquals(0, viewportAdjuster.getScrollable().getVerticalScrollPosition());
	}

	@Test
	public void testViewportIsNotAdjustedDownwardsWithArrowKey() {
		controller.selectCell(0, 0, false, false);
		fakeDownArrowPress(viewport);
		assertEquals(0, viewportAdjuster.getScrollable().getVerticalScrollPosition());
	}

	@Test
	public void testViewportIsNotAdjustedDownwardsWithMouseClick() {
		spreadsheet.handlePointerDown(rowHeader + 10, colHeader + 30, Modifiers.NONE);
		assertEquals(0, viewportAdjuster.getScrollable().getVerticalScrollPosition());
	}

	@Test
	public void testViewportShouldNotBeAdjustedWhenMovingUpAtTopmostPositionWithArrowKey() {
		controller.selectCell(0, 2, false, false);
		fakeUpArrowPress(viewport);
		assertEquals(0, viewportAdjuster.getScrollable().getVerticalScrollPosition());
	}

	@Test
	public void testViewportIsNotAdjustedUpwardsWithArrowKey() {
		controller.selectCell(2, 1, false, false);
		fakeDownArrowPress(viewport);
		int verticalScrollPosition = viewportAdjuster.getScrollable().getVerticalScrollPosition();
		fakeUpArrowPress(viewport);
		assertEquals(verticalScrollPosition, viewportAdjuster.getScrollable().getVerticalScrollPosition());
	}

	private static class TestCellRenderableFactory implements CellRenderableFactory {
		@Override
		public SelfRenderable getRenderable(Object data, SpreadsheetStyle style,
				int row, int column) {
			return data == null ? null : new SelfRenderable(new StringRenderer(),
					GFont.PLAIN, CellFormat.ALIGN_LEFT, data);
		}
	}

	private void fakeLeftArrowPress(Rectangle viewport) {
		KeyEvent e = new KeyEvent(new Button(), 1, System.currentTimeMillis(), 0, 37, ' ');
		controller.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE, viewport);
	}

	private void fakeUpArrowPress(Rectangle viewport) {
		KeyEvent e = new KeyEvent(new Button(), 1, System.currentTimeMillis(), 0, 38, ' ');
		controller.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE, viewport);
	}

	private void fakeRightArrowPress(Rectangle viewport) {
		KeyEvent e = new KeyEvent(new Button(), 1, System.currentTimeMillis(), 0, 39, ' ');
		controller.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE, viewport);
	}

	private void fakeDownArrowPress(Rectangle viewport) {
		KeyEvent e = new KeyEvent(new Button(), 1, System.currentTimeMillis(), 0, 40, ' ');
		controller.handleKeyPressed(e.getKeyCode(), e.getKeyChar() + "", Modifiers.NONE, viewport);
	}

	private ViewportAdjuster getViewportAdjuster() {
		return new ViewportAdjuster(controller.getLayout(), getMockForScrollable());
	}

	private Scrollable getMockForScrollable() {
		Scrollable scrollable = Mockito.mock(Scrollable.class);
		doAnswer(invocation -> {
			int position = invocation.getArgument(0);
			viewport = viewport.translatedBy(0, position);
			return null;
		}).when(scrollable).setVerticalScrollPosition(anyInt());
		doAnswer(invocation -> {
			int position = invocation.getArgument(0);
			viewport = viewport.translatedBy(position, 0);
			return null;
		}).when(scrollable).setHorizontalScrollPosition(anyInt());
		when(scrollable.getVerticalScrollPosition()).thenAnswer(
				invocation -> (int) viewport.getMinY());
		when(scrollable.getHorizontalScrollPosition()).thenAnswer(
				invocation -> (int) viewport.getMinX());
		when(scrollable.getScrollBarWidth()).thenReturn(5);
		return scrollable;
	}
}
