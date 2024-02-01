package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Rectangle;

public class TestSpreadsheetCellEditor
		implements SpreadsheetCellEditor {

	private Object content;
	private boolean visible;

	@Override
	public void setBounds(Rectangle editorBounds) {
		visible = true;
	}

	@Override
	public void setTargetCell(int row, int column) {
		// not needed
	}

	@Override
	public void setContent(Object content) {
		this.content = content;
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

	public Object getContent() {
		return content;
	}
}
