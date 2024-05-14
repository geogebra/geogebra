package org.geogebra.common.spreadsheet.core;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.util.shape.Rectangle;

public class TestSpreadsheetCellEditor
		implements SpreadsheetCellEditor {

	private Object content;
	private boolean visible;
	private int targetRow;
	private int targetColumn;
	/**
	 * Used for {@link CellDragPasteHandlerTest}
	 */
	private final Map<Integer, Map<Integer, Object>> data = new HashMap<>();

	@Override
	public void setBounds(Rectangle editorBounds) {
		visible = true;
	}

	@Override
	public void setTargetCell(int row, int column) {
		targetRow = row;
		targetColumn = column;
	}

	@Override
	public void setContent(Object content) {
		this.content = content;
		data.computeIfAbsent(targetRow, ignore -> new HashMap<>()).put(targetColumn, content);
	}

	@Override
	public void type(String text) {
		setContent(text);
	}

	@Override
	public void setAlign(int align) {
		// not needed
	}

	@Override
	public void scrollHorizontally() {
		// not needed
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void hide() {
		visible = false;
	}

	@Override
	public void onEnter() {
		// not needed
	}

	public Object getContent() {
		return content;
	}

	public Object getContentAt(int row, int column) {
		return data.get(row) != null ? data.get(row).get(column) : null;
	}
}
