package org.geogebra.common.spreadsheet.core;

public class SpreadsheetEventHandler {

	private final Spreadsheet spreadsheet;
	private final TableLayout layout;

	public SpreadsheetEventHandler(Spreadsheet spreadsheet,
			TableLayout layout) {
		this.spreadsheet = spreadsheet;
		this.layout = layout;
	}

	public void handlePointerUp(int x, int y, int modifiers) {
		spreadsheet.select(new Selection(SelectionType.CELLS, new TabularRange(layout.findRow(y),
				layout.findRow(y), layout.findColumn(x), layout.findColumn(x))), modifiers > 0);

	}

	public void handlePointerDown(int x, int y, int modifiers) {
		spreadsheet.hideCellEditor();
		if (spreadsheet.isSelected(x, y)) {
			spreadsheet.showCellEditor(x, y);
		}
		// start selecting
	}

	public void handlePointerMove(int x, int y, int modifiers) {
		// extend selection
	}

	public void handleKeyPressed(int keyCode, int modifiers) {
		// extend selection
	}
}
