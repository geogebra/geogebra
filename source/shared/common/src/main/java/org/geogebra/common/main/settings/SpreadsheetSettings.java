package org.geogebra.common.main.settings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.spreadsheet.core.SpreadsheetDimensions;

/**
 * Settings for the spreadsheet view.
 */
public class SpreadsheetSettings extends AbstractSettings implements SpreadsheetDimensions {

	public static final int TABLE_CELL_WIDTH = 70;
	public static final int TABLE_CELL_HEIGHT = 21; // (old height 20) + 1
													// to stop cell editor
													// clipping
	// layout settings
	private boolean showFormulaBar = Defaults.SHOW_FORMULA_BAR;
	private boolean showGrid = Defaults.SHOW_GRID;
	private boolean showRowHeader = Defaults.SHOW_ROW_HEADER;
	private boolean showColumnHeader = Defaults.SHOW_COLUMN_HEADER;
	private boolean showVScrollBar = Defaults.SHOW_VSCROLLBAR;
	private boolean showHScrollBar = Defaults.SHOW_HSCROLLBAR;
	private boolean isColumnSelect = Defaults.IS_COLUMN_SELECT;
	private boolean allowSpecialEditor = Defaults.ALLOW_SPECIAL_EDITOR;
	private boolean allowToolTips = Defaults.ALLOW_TOOLTIPS;
	private boolean equalsRequired = Defaults.EQUALS_REQUIRED;
	private boolean enableAutoComplete = Defaults.ENABLE_AUTOCOMPLETE;

	// row and column size
	private Map<Integer, Double> columnWidths;
	private Map<Integer, Double> rowHeights;
	private int preferredColumnWidth = TABLE_CELL_WIDTH;
	private int preferredRowHeight = TABLE_CELL_HEIGHT;

	// cell format
	private String cellFormat;

	// initial selection
	private GPoint scrollPosition = new GPoint(0, 0);
	private GPoint selectedCell = new GPoint(0, 0);

	// preferred size
	private GDimension preferredSize;
	private int hScrollBarValue;
	private int vScrollBarValue;
	private static final int DEFAULT_NR_ROWS = 100;
	private static final int DEFAULT_NR_COLUMNS = 26;
	private int rows = DEFAULT_NR_ROWS;
	private int columns = DEFAULT_NR_COLUMNS;

	public static class Defaults {
		public static final boolean SHOW_FORMULA_BAR = false;
		public static final boolean SHOW_GRID = true;
		public static final boolean SHOW_ROW_HEADER = true;
		public static final boolean SHOW_COLUMN_HEADER = true;
		public static final boolean SHOW_VSCROLLBAR = true;
		public static final boolean SHOW_HSCROLLBAR = true;
		public static final boolean IS_COLUMN_SELECT = false;
		public static final boolean ALLOW_SPECIAL_EDITOR = false;
		public static final boolean ALLOW_TOOLTIPS = true;
		public static final boolean EQUALS_REQUIRED = false;
		public static final boolean ENABLE_AUTOCOMPLETE = false;
	}

	// ============================================
	// Row/Column Dimension Settings
	// ============================================

	/**
	 * @param listeners
	 *            settings listeners
	 */
	public SpreadsheetSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
		preferredSize = AwtFactory.getPrototype().newDimension(0, 0);
	}

	/**
	 * New spreadsheet settings.
	 */
	public SpreadsheetSettings() {
		super();
		preferredSize = AwtFactory.getPrototype().newDimension(0, 0);
	}

	/**
	 * Reset internal state before reload (from XML).
	 */
	public void resetBeforeReload() {
		getRowHeights().clear();
		getColumnWidths().clear();
		setCellFormatXml(null);
		rows = DEFAULT_NR_ROWS;
		columns = DEFAULT_NR_COLUMNS;
		selectedCell = new GPoint(0, 0);
		// inform listeners about potential change in values, e.g., if cellFormatXml was non-null
		// before, and isn't present in the XML, no change notification would be fired
		settingChanged();
	}

	/**
	 * @param index
	 *            column
	 * @param width
	 *            width
	 */
	public void addWidth(int index, double width) {
		getColumnWidths().put(index, width);
		settingChanged();
	}

	/**
	 * Set column width, do not notify listeners.
	 * @param index column index
	 * @param width width
	 */
	public void addWidthNoFire(int index, double width) {
		getColumnWidths().put(index, width);
	}

	/**
	 * @return global preferred column width
	 */
	public int preferredColumnWidth() {
		return preferredColumnWidth;
	}

	/**
	 * @param prefWidth
	 *            global preferred column width
	 */
	public void setPreferredColumnWidth(int prefWidth) {
		this.preferredColumnWidth = prefWidth;
		settingChanged();
	}

	/**
	 * @param index
	 *            row
	 * @param height
	 *            height in px
	 */
	public void addHeight(int index, double height) {
		getRowHeights().put(index, height);
		settingChanged();
	}

	/**
	 * Set row height, do not notify listeners.
	 * @param row row index
	 * @param height height
	 */
	public void addHeightNoFire(int row, double height) {
		getRowHeights().put(row, height);
	}

	/**
	 * Set column widths and row heights without firing a settings changed event.
	 * @param columnWidths a map of column indices to column widths
	 * @param rowHeights a map of row indices to row heights
	 */
	public void setCellSizesNoFire(@Nonnull Map<Integer, Double> columnWidths,
			@Nonnull Map<Integer, Double> rowHeights) {
		this.columnWidths = columnWidths;
		this.rowHeights = rowHeights;
	}

	/**
	 * @return preferred row height
	 */
	public int preferredRowHeight() {
		return preferredRowHeight;
	}

	/**
	 * @param preferredRowHeight
	 *            preferred row height
	 */
	public void setPreferredRowHeight(int preferredRowHeight) {
		this.preferredRowHeight = preferredRowHeight;
		settingChanged();
	}

	public void setPreferredRowHeightNoFire(int height) {
		preferredRowHeight = height;
	}

	// ============================================
	// Layout Settings
	// ============================================

	/**
	 * @return the showFormulaBar
	 */
	public boolean showFormulaBar() {
		return showFormulaBar;
	}

	/**
	 * @param showFormulaBar
	 *            the showFormulaBar to set
	 */
	public void setShowFormulaBar(boolean showFormulaBar) {
		if (this.showFormulaBar != showFormulaBar) {
			this.showFormulaBar = showFormulaBar;
			settingChanged();
		}
	}

	/**
	 * @return the showGrid
	 */
	public boolean showGrid() {
		return showGrid;
	}

	/**
	 * @param showGrid
	 *            the showGrid to set
	 */
	public void setShowGrid(boolean showGrid) {
		if (this.showGrid != showGrid) {
			this.showGrid = showGrid;
			settingChanged();
		}
	}

	/**
	 * @return the showRowHeader
	 */
	public boolean showRowHeader() {
		return showRowHeader;
	}

	/**
	 * @param showRowHeader
	 *            the showRowHeader to set
	 */
	public void setShowRowHeader(boolean showRowHeader) {
		if (this.showRowHeader != showRowHeader) {
			this.showRowHeader = showRowHeader;
			settingChanged();
		}
	}

	/**
	 * @return the showColumnHeader
	 */
	public boolean showColumnHeader() {
		return showColumnHeader;
	}

	/**
	 * @param showColumnHeader
	 *            the showColumnHeader to set
	 */
	public void setShowColumnHeader(boolean showColumnHeader) {
		if (this.showColumnHeader != showColumnHeader) {
			this.showColumnHeader = showColumnHeader;
			settingChanged();
		}
	}

	/**
	 * @return the showVScrollBar
	 */
	public boolean showVScrollBar() {
		return showVScrollBar;
	}

	/**
	 * @param showVScrollBar
	 *            the showVScrollBar to set
	 */
	public void setShowVScrollBar(boolean showVScrollBar) {
		if (this.showVScrollBar != showVScrollBar) {
			this.showVScrollBar = showVScrollBar;
			settingChanged();
		}
	}

	/**
	 * @return the showHScrollBar
	 */
	public boolean showHScrollBar() {
		return showHScrollBar;
	}

	/**
	 * @param showHScrollBar
	 *            the showHScrollBar to set
	 */
	public void setShowHScrollBar(boolean showHScrollBar) {
		if (this.showHScrollBar != showHScrollBar) {
			this.showHScrollBar = showHScrollBar;
			settingChanged();
		}
	}

	/**
	 * @return the allowSpecialEditor
	 */
	public boolean allowSpecialEditor() {
		return allowSpecialEditor;
	}

	/**
	 * @param allowSpecialEditor
	 *            the allowSpecialEditor to set
	 */
	public void setAllowSpecialEditor(boolean allowSpecialEditor) {
		if (this.allowSpecialEditor != allowSpecialEditor) {
			this.allowSpecialEditor = allowSpecialEditor;
			settingChanged();
		}
	}

	/**
	 * @return the allowToolTips
	 */
	public boolean allowToolTips() {
		return allowToolTips;
	}

	/**
	 * @param allowToolTips
	 *            the allowToolTips to set
	 */
	public void setAllowToolTips(boolean allowToolTips) {
		if (this.allowToolTips != allowToolTips) {
			this.allowToolTips = allowToolTips;
			settingChanged();
		}
	}

	/**
	 * @return the equalsRequired
	 */
	public boolean equalsRequired() {
		return equalsRequired;
	}

	/**
	 * @param equalsRequired
	 *            the equalsRequired to set
	 */
	public void setEqualsRequired(boolean equalsRequired) {
		if (this.equalsRequired != equalsRequired) {
			this.equalsRequired = equalsRequired;
			// settingChanged();
		}
	}

	/**
	 * @return the isColumnSelect
	 */
	public boolean isColumnSelect() {
		return isColumnSelect;
	}

	/**
	 * @param isColumnSelect
	 *            the isColumnSelect to set
	 */
	public void setColumnSelect(boolean isColumnSelect) {
		if (this.isColumnSelect != isColumnSelect) {
			this.isColumnSelect = isColumnSelect;
			settingChanged();
		}
	}

	// ============================================
	// Cell Format Settings
	// ============================================

	/**
	 * (used only by SpreadsheetViewD/SpreadsheetViewW)
	 * @return the cellFormat
	 */
	public String cellFormat() {
		return cellFormat;
	}

	/**
	 * @param cellFormat
	 *            the cellFormat to set
	 */
	public void setCellFormat(String cellFormat) {
		if (this.cellFormat != null && this.cellFormat.equals(cellFormat)) {
			return;
		}
		this.cellFormat = cellFormat;
		settingChanged();
	}

	private boolean hasCellFormat() {
		return cellFormat != null;
	}

	// ============================================
	// Initial Position Settings
	// ============================================
	/**
	 * @return the scrollPosition
	 */
	public GPoint scrollPosition() {
		return scrollPosition;
	}

	/**
	 * @param scrollPosition
	 *            the scrollPosition to set
	 */
	public void setScrollPosition(GPoint scrollPosition) {
		if (this.scrollPosition == null
				|| !this.scrollPosition.equals(scrollPosition)) {
			this.scrollPosition = scrollPosition;
			settingChanged();
		}
	}

	/**
	 * @return the selectedCell
	 */
	public GPoint selectedCell() {
		return selectedCell;
	}

	/**
	 * @param selectedCell
	 *            the selectedCell to set
	 */
	public void setSelectedCell(GPoint selectedCell) {
		if (this.selectedCell == null
				|| !this.selectedCell.equals(selectedCell)) {
			this.selectedCell = selectedCell;
			settingChanged();
		}
	}

	// ============================================
	// PreferredSize Settings
	// ============================================
	/**
	 * @return the preferredSize
	 */
	public GDimension preferredSize() {
		return preferredSize;
	}

	/**
	 * @param preferredSize
	 *            the preferredSize to set
	 */
	public void setPreferredSize(GDimension preferredSize) {
		if (this.preferredSize == null
				|| !this.preferredSize.equals(preferredSize)) {
			this.preferredSize = preferredSize;
			settingChanged();
		}
	}

	/**
	 * @param enableAutoComplete
	 *            flag to allow auto-complete in the editor
	 */
	public void setEnableAutoComplete(boolean enableAutoComplete) {
		if (this.enableAutoComplete != enableAutoComplete) {
			this.enableAutoComplete = enableAutoComplete;
			settingChanged();
		}
	}

	/**
	 * @return is auto-complete allowed in the editor
	 */
	public boolean isEnableAutoComplete() {
		return enableAutoComplete;
	}

	/**
	 * @param hScrollBalValue
	 *            horizontal scrollbar position
	 */
	public void setHScrollBalValue(int hScrollBalValue) {
		this.hScrollBarValue = hScrollBalValue;
	}

	/**
	 * @param vScrollBalValue
	 *            vertical scrollbar position
	 */
	public void setVScrollBalValue(int vScrollBalValue) {
		this.vScrollBarValue = vScrollBalValue;
	}

	// ============================================
	// Defaults
	// ============================================

	/**
	 * @return whether all settings are default
	 */
	public boolean isAllDefaults() {
		return isDefaultPreferredSize() && isSelectionDefaults()
				&& isLayoutDefaults() && !hasCellFormat()
				&& isRowColumnSizeDefaults();
	}

	/**
	 * @return whether row and column sizes are default
	 */
	private boolean isRowColumnSizeDefaults() {
		return preferredColumnWidth == TABLE_CELL_WIDTH
				&& preferredRowHeight == TABLE_CELL_HEIGHT
				&& getColumnWidths().size() == 0 && getRowHeights().size() == 0;
	}

	/**
	 * @return whether the selection is default
	 */
	private boolean isSelectionDefaults() {
		return hScrollBarValue == 0 && vScrollBarValue == 0
				&& selectedCell.getX() == 0 && selectedCell.getY() == 0;
	}

	/**
	 * @return whether layout settings are all default
	 */
	private boolean isLayoutDefaults() {
		return isDefaultShowFormulaBar() && isDefaultShowGrid()
				&& isDefaultShowRowHeader() && isDefaultShowColumnHeader()
				&& isDefaultVScrollBar() && isDefaultHScrollBar()
				&& isDefaultColumnSelect() && isDefaultSpecialEditorAllowed()
				&& isDefaultToolTipsAllowed() && isDefaultSpecialEditorAllowed()
				&& !equalsRequired() && !isEnableAutoComplete();
	}

	private boolean isDefaultToolTipsAllowed() {
		return allowToolTips == Defaults.ALLOW_TOOLTIPS;
	}

	private boolean isDefaultSpecialEditorAllowed() {
		return allowSpecialEditor == Defaults.ALLOW_SPECIAL_EDITOR;
	}

	private boolean isDefaultColumnSelect() {
		return isColumnSelect == Defaults.IS_COLUMN_SELECT;
	}

	private boolean isDefaultVScrollBar() {
		return showVScrollBar == Defaults.SHOW_VSCROLLBAR;
	}

	private boolean isDefaultHScrollBar() {
		return showHScrollBar == Defaults.SHOW_HSCROLLBAR;
	}

	private boolean isDefaultShowColumnHeader() {
		return showColumnHeader == Defaults.SHOW_COLUMN_HEADER;
	}

	private boolean isDefaultShowRowHeader() {
		return showRowHeader == Defaults.SHOW_ROW_HEADER;
	}

	private boolean isDefaultShowFormulaBar() {
		return showFormulaBar == Defaults.SHOW_FORMULA_BAR;
	}

	private boolean isDefaultShowGrid() {
		return showGrid == Defaults.SHOW_GRID;
	}

	/**
	 * @return whether preferred cell size is default
	 */
	private boolean isDefaultPreferredSize() {
		int w = preferredSize.getWidth();
		int h = preferredSize.getHeight();

		return (w == 0 && h == 0)
				|| (w == TABLE_CELL_WIDTH && h == TABLE_CELL_HEIGHT);
	}

	// ============================================
	// XML
	// ============================================

	/**
	 * returns settings in XML format
	 * 
	 * @param xmlBuilder
	 *            string builder
	 * @param asPreference
	 *            whether this is for preference
	 */
	public void getXML(XMLStringBuilder xmlBuilder, boolean asPreference) {
		StringBuilder sb = new StringBuilder();
		XMLStringBuilder xb = new XMLStringBuilder(sb);

		if (!isDefaultPreferredSize()) {
			GDimension size = preferredSize();
			int width = size.getWidth();
			int height = size.getHeight();
			xb.startTag("size");
			if (width != 0) {
				xb.attr("width", width);
			}
			if (height != 0) {
				xb.attr("height", height);
			}
			xb.endTag();
		}

		int prefWidth = preferredColumnWidth();
		int prefHeight = preferredRowHeight();

		if (prefWidth != TABLE_CELL_WIDTH || prefHeight != TABLE_CELL_HEIGHT) {
			xb.startTag("prefCellSize");
			if (prefWidth != TABLE_CELL_WIDTH) {
				xb.attr("width", prefWidth);
			}
			if (prefHeight != TABLE_CELL_HEIGHT) {
				xb.attr("height", prefHeight);
			}
			xb.endTag();
		}

		if (!asPreference) {
			getDimensionsXML(xb);
			getWidthsAndHeightsXML(xb);

			// initial selection
			if (!isSelectionDefaults()) {
				xb.startTag("selection");
				if (hScrollBarValue != 0) {
					xb.attr("hScroll", hScrollBarValue);
				}
				if (vScrollBarValue != 0) {
					xb.attr("vScroll", vScrollBarValue);
				}
				if (selectedCell.getX() != 0) {
					xb.attr("column", selectedCell.getX());
				}
				if (selectedCell.getY() != 0) {
					xb.attr("row", selectedCell.getY());
				}
				xb.endTag();
			}
		}

		// layout
		getLayoutXML(xb);

		// cell formats
		if (!asPreference && hasCellFormat()) {
			xb.startTag("spreadsheetCellFormat")
					.attrRaw("formatMap", cellFormat).endTag();
		}

		if (sb.length() > 0) {
			xmlBuilder.startOpeningTag("spreadsheetView", 0).endTag();
			xmlBuilder.append(xb);
			xmlBuilder.closeTag("spreadsheetView");
		}
	}

	/**
	 * Add layout settings.
	 * 
	 * @param sb
	 *            XML string builder
	 */
	public void getLayoutXML(XMLStringBuilder sb) {
		if (!isLayoutDefaults()) {
			sb.startTag("layout");

			if (showFormulaBar) {
				sb.attr("showFormulaBar", true);
			}

			if (showGrid) {
				sb.attr("showGrid", true);
			}

			if (showHScrollBar) {
				sb.attr("showHScrollBar", true);
			}

			if (showVScrollBar) {
				sb.attr("showVScrollBar", true);
			}

			if (showColumnHeader) {
				sb.attr("showColumnHeader", true);
			}

			if (showRowHeader) {
				sb.attr("showRowHeader", true);
			}

			if (allowSpecialEditor) {
				sb.attr("allowSpecialEditor", true);
			}

			if (allowToolTips) {
				sb.attr("allowToolTips", true);

			}

			if (equalsRequired) {
				sb.attr("equalsRequired", true);
			}

			if (enableAutoComplete) {
				sb.attr("autoComplete", true);
			}

			sb.endTag();
		}

	}

	/**
	 * Append width / height settings to XML.
	 * 
	 * @param sb
	 *            XML string builder
	 */
	public void getWidthsAndHeightsXML(XMLStringBuilder sb) {
		if (isRowColumnSizeDefaults()) {
			return;
		}

		// column widths
		Map<Integer, Double> widthMap = getColumnWidths();
		for (Entry<Integer, Double> entry : widthMap.entrySet()) {
			int col = entry.getKey();
			double colWidth = entry.getValue();
			if (colWidth != preferredColumnWidth()) {
				sb.startTag("spreadsheetColumn").attr("id", col)
						.attr("width", colWidth).endTag();
			}
		}

		// row heights
		Map<Integer, Double> heightMap = getRowHeights();
		for (Entry<Integer, Double> entry : heightMap.entrySet()) {
			int row = entry.getKey();
			double rowHeight = entry.getValue();
			if (rowHeight != preferredRowHeight()) {
				sb.startTag("spreadsheetRow").attr("id", row)
						.attr("height", rowHeight).endTag();
			}
		}

	}

	/**
	 * Print size XML tag to a builder
	 * @param sb output string builder
	 */
	public void getDimensionsXML(XMLStringBuilder sb) {
		if (rows != DEFAULT_NR_ROWS || columns != DEFAULT_NR_COLUMNS) {
			sb.startTag("dimensions").attr("rows", rows)
					.attr("columns", columns).endTag();
		}
	}

	/**
	 * @param rows number of rows
	 * @param columns number of columns
	 */
	public void setDimensions(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		settingChanged();
	}

	public void setRowsNoFire(int rows) {
		this.rows  = rows;
	}

	public void setColumnsNoFire(int columns) {
		this.columns  = columns;
	}

	/**
	 * Remove all custom heights
	 */
	public void clearHeights() {
		if (rowHeights != null) {
			rowHeights.clear();
		}
	}

	/**
	 * Remove all custom widths
	 */
	public void clearWidths() {
		if (columnWidths != null) {
			columnWidths.clear();
		}
	}

	public @CheckForNull String getCellFormatXml() {
		return cellFormat;
	}

	public void setCellFormatXml(@CheckForNull String xml) {
		cellFormat = xml;
	}
	
	// -- SpreadsheetDimensions --

	@Override
	public @Nonnull Map<Integer, Double> getColumnWidths() {
		if (columnWidths == null) {
			columnWidths = new HashMap<>();
		}
		return columnWidths;
	}

	@Override
	public @Nonnull Map<Integer, Double> getRowHeights() {
		if (rowHeights == null) {
			rowHeights = new HashMap<>();
		}
		return rowHeights;
	}

	@Override
	public int getColumns() {
		return columns;
	}

	@Override
	public int getRows() {
		return rows;
	}
}
