package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.util.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.JavaKeyCodes;

public class SpreadsheetCellEditorTest {

	private TestTabularData data;
	private SpreadsheetController controller;
	private final Rectangle viewport = new Rectangle(0, 0, 500, 500);
	private TestSpreadsheetCellEditor editor;

	@Before
	public void setupEditor() {
		data = new TestTabularData();
		controller = new SpreadsheetController(data, null);
		editor = new TestSpreadsheetCellEditor();
		controller.setControlsDelegate(new SpreadsheetControlsDelegate() {
			@Override
			public SpreadsheetCellEditor getCellEditor() {
				return editor;
			}

			@Override
			public void showContextMenu(List<ContextMenuItem> actions, GPoint coords) {
				// not needed
			}

			@Override
			public void hideContextMenu() {
				// not needed
			}

			@Override
			public ClipboardInterface getClipboard() {
				return null;
			}
		});
	}

	@Test
	public void shouldLoadData() {
		data.setContent(0, 0, "foo");
		data.setContent(1, 2, "bar");

		controller.showCellEditor(0,  0);
		assertEquals("foo", editor.getContent());

		controller.showCellEditor(1,  2);
		assertEquals("bar", editor.getContent());
	}

	@Test
	public void testTypingIntoCell() {
		controller.selectCell(1, 1, false, false);
		controller.handleKeyPressed(JavaKeyCodes.VK_A, "a", Modifiers.NONE);
		assertEquals("a", editor.getContent());
	}
}
