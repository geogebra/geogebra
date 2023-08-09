package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GColor;

public final class SpreadsheetStyle {

	private boolean showGrid = true;

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	public GColor getTextColor() {
		return GColor.newColor(0, 0, 0, 0.87);
	}

	// grid lines, colors, fonts
}
