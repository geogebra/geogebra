package org.geogebra.common.spreadsheet;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.spreadsheet.core.CellRenderableFactory;
import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;
import org.junit.Test;

public class SpreadsheetTest extends BaseUnitTest {

	@Test
	public void testTextDataRendering() {
		TestTabularData tabularData = new TestTabularData();
		Spreadsheet spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory());
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		tabularData.setContent(0, 0, "foo");
		tabularData.setContent(0, 1, "bar");
		spreadsheet.setViewport(new Rectangle(0, 100, 0, 100));
		spreadsheet.draw(graphics);
		assertThat(graphics.toString(), equalTo("col0,col1,1,foo,bar,2,3,4,5"));
	}

	@Test
	public void testSingleColumnResize() {
		TestTabularData tabularData = new TestTabularData();
		Spreadsheet spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory());
		spreadsheet.setViewport(new Rectangle(0, 100, 0, 100));
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 2 columns
		assertThat(graphics.toString(), startsWith("col0,col1,1"));
		spreadsheet.handlePointerDown(80, 5, Modifiers.NONE);
		spreadsheet.handlePointerMove(50, 5, Modifiers.NONE);
		spreadsheet.handlePointerUp(50, 5, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 3
		assertThat(graphics.toString(), startsWith("col0,col1,col2,1"));
	}

	@Test
	public void testMultiColumnResize() {
		TestTabularData tabularData = new TestTabularData();
		Spreadsheet spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory());
		spreadsheet.setViewport(new Rectangle(0, 140, 0, 100));
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 3 columns
		assertThat(graphics.toString(), startsWith("col0,col1,col2,1"));
		spreadsheet.getController().selectColumn(1, false, false);
		spreadsheet.getController().selectColumn(2, true, false);
		spreadsheet.getController().selectColumn(3, true, false);
		spreadsheet.getController().selectColumn(4, true, false);
		spreadsheet.handlePointerDown(122, 5, Modifiers.NONE);
		spreadsheet.handlePointerMove(90, 5, Modifiers.NONE);
		spreadsheet.handlePointerUp(90, 5, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 6
		assertThat(graphics.toString(), startsWith("col0,col1,col2,col3,col4,col5,1"));
	}

	@Test
	public void testSingleRowResize() {
		TestTabularData tabularData = new TestTabularData();
		Spreadsheet spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory());
		spreadsheet.setViewport(new Rectangle(0, 100, 0, 100));
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		spreadsheet.handlePointerDown(15, 40, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, 60, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, 60, Modifiers.NONE);
		graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// after resize, we have 4
		assertThat(graphics.toString(), endsWith(",4"));
	}

	@Test
	public void testNoHeaderRowResize() {
		TestTabularData tabularData = new TestTabularData();
		Spreadsheet spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory());
		spreadsheet.setViewport(new Rectangle(0, 100, 0, 100));
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
		TestTabularData tabularData = new TestTabularData();
		Spreadsheet spreadsheet = new Spreadsheet(tabularData,
				new TestCellRenderableFactory());
		spreadsheet.setViewport(new Rectangle(0, 100, 0, 100));
		StringCapturingGraphics graphics = new StringCapturingGraphics();
		spreadsheet.draw(graphics);
		// initially we have 5 rows
		assertThat(graphics.toString(), endsWith(",5"));
		spreadsheet.getController().selectRow(1, false, false);
		spreadsheet.getController().selectRow(2, true, false);
		spreadsheet.getController().selectRow(3, true, false);
		spreadsheet.getController().selectRow(4, true, false);
		spreadsheet.handlePointerDown(15, 60, Modifiers.NONE);
		spreadsheet.handlePointerMove(15, 85, Modifiers.NONE);
		spreadsheet.handlePointerUp(15, 85, Modifiers.NONE);
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
					GFont.PLAIN, null, CellFormat.ALIGN_LEFT, data);
		}
	}
}
