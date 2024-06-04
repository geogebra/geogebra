package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.spreadsheet.TestTabularData;
import org.geogebra.common.spreadsheet.kernel.SpreadsheetCellProcessor;
import org.geogebra.common.spreadsheet.kernel.SpreadsheetEditorListener;
import org.geogebra.common.util.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.JavaKeyCodes;

public class SpreadsheetCellEditorTest extends BaseUnitTest {

	private TestTabularData data;
	private SpreadsheetController controller;
	private final Rectangle viewport = new Rectangle(0, 0, 500, 500);
	private TestSpreadsheetCellEditor editor;
	private SpreadsheetCellProcessor processor;
	private Spreadsheet spreadsheet;

	@Before
	public void setupEditor() {
		data = new TestTabularData();
		spreadsheet = new Spreadsheet(data, null, null);
		controller = spreadsheet.getController();
		editor = new TestSpreadsheetCellEditor();
		SpreadsheetEditorListener listener = new SpreadsheetEditorListener(null,
				getKernel(), 0, 0, editor, spreadsheet);
		processor = new SpreadsheetCellProcessor("A1",
				getKernel().getAlgebraProcessor(), listener);
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

	@Test
	public void testNumericInputShouldHaveNoError() {
		processor.process("=1");
		assertFalse(controller.hasError(0, 0));
	}

	@Test
	public void testNumericInputChangedToErrorShouldHaveError() {
		processor.process("=1");
		assertFalse(controller.hasError(0, 0));
		processor.process("=1+%");
		assertTrue(controller.hasError(0, 0));
	}
}
