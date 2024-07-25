package org.geogebra.common.spreadsheet.core;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.spreadsheet.StringCapturingGraphics;
import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;
import org.junit.Before;
import org.junit.Test;

public class SpreadsheetTest extends BaseUnitTest {

	private final int colHeader = TableLayout.DEFAULT_CELL_HEIGHT;
	private final int rowHeader = TableLayout.DEFAULT_ROW_HEADER_WIDTH;
	private Spreadsheet spreadsheet;
	private TestTabularData tabularData;

	@Before
	public void setupSpreadsheet() {
		tabularData = new TestTabularData();
		spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory(), null);
		spreadsheet.setHeightForRows(20, 0, 5);
		spreadsheet.setWidthForColumns(40, 0, 5);
		resetViewport();
		spreadsheet.setViewportAdjustmentHandler(new ViewportAdjusterDelegate() {
			@Override
			public void setScrollPosition(int x, int y) {
				// no UI to update
			}

			@Override
			public int getScrollBarWidth() {
				return 5;
			}

			@Override
			public void updateScrollPanelSize(Size size) {
				// no UI to update
			}
		});
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
		spreadsheet.selectColumn(1, false, false);
		spreadsheet.selectColumn(2, true, false);
		spreadsheet.selectColumn(3, true, false);
		spreadsheet.selectColumn(4, true, false);
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
		spreadsheet.selectRow(1, false, false);
		spreadsheet.selectRow(2, true, false);
		spreadsheet.selectRow(3, true, false);
		spreadsheet.selectRow(4, true, false);
		spreadsheet.handlePointerDown(15, colHeader + 20, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, colHeader + 45, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, colHeader + 45, Modifiers.NONE);
		resetViewport();
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 3
		assertThat(graphics.toString(), endsWith(",3"));
	}

	private static class TestCellRenderableFactory implements CellRenderableFactory {
		@Override
		public SelfRenderable getRenderable(Object data, SpreadsheetStyle style,
				int row, int column) {
			return data == null ? null : new SelfRenderable(new StringRenderer(),
					GFont.PLAIN, CellFormat.ALIGN_LEFT, data);
		}
	}
}