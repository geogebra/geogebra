package org.geogebra.common.spreadsheet.style;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.GeoGebraColorConstants;

public final class SpreadsheetStyle {

	private final CellFormat format;
	private boolean showGrid = true;
	public static final GColor SPREADSHEET_ERROR_BORDER = GColor.newColorRGB(0xB00020);

	public SpreadsheetStyle(CellFormat format) {
		this.format = format;
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	public GColor getTextColor() {
		return GeoGebraColorConstants.NEUTRAL_900;
	}

	/**
	 * @param row cell row
	 * @param column cell column
	 * @return whether to show border for given cell
	 */
	public boolean showBorder(int row, int column) {
		Byte border = (Byte) format.getCellFormat(column, row, CellFormat.FORMAT_BORDER);
		return border != null && border != 0;
	}

	/**
	 * @param row cell row
	 * @param column cell column
	 * @return font style of given cell (see {@link GFont#getStyle()})
	 */
	public Integer getFontStyle(int row, int column) {
		return (Integer) format.getCellFormat(column, row, CellFormat.FORMAT_FONTSTYLE);
	}

	/**
	 * @param row cell row
	 * @param column cell column
	 * @param fallback color to use if no background set
	 * @return background color of given cell
	 */
	public GColor getBackgroundColor(int row, int column, GColor fallback) {
		GColor bgColor = (GColor) format.getCellFormat(column, row,
				CellFormat.FORMAT_BGCOLOR);
		return bgColor == null ? fallback : bgColor;
	}

	public Integer getAlignment(int row, int column) {
		return (Integer) format.getCellFormat(column, row, CellFormat.FORMAT_ALIGN);
	}

	public GColor getGridColor() {
		return GeoGebraColorConstants.NEUTRAL_300;
	}

	public GColor geErrorGridColor() {
		return SPREADSHEET_ERROR_BORDER;
	}

	public GColor getHeaderBackgroundColor() {
		return GeoGebraColorConstants.NEUTRAL_200;
	}

	public GColor getSelectionColor() {
		return GeoGebraColorConstants.PURPLE_100;
	}

	public GColor getSelectionBorderColor() {
		return GeoGebraColorConstants.PURPLE_600;
	}

	public GColor getDashedSelectionBorderColor() {
		return GeoGebraColorConstants.NEUTRAL_700;
	}

	public GColor getSelectionHeaderColor() {
		return GeoGebraColorConstants.NEUTRAL_700;
	}

	public GColor getSelectedTextColor() {
		return GColor.WHITE;
	}

	// grid lines, colors, fonts
}
