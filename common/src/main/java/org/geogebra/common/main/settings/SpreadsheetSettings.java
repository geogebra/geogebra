package org.geogebra.common.main.settings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.spreadsheet.core.PersistenceListener;
import org.geogebra.common.spreadsheet.core.SpreadsheetDimensions;

/**
 * Settings for the spreadsheet view.
 */
public class SpreadsheetSettings extends AbstractSettings implements SpreadsheetDimensions {

	public static final int TABLE_CELL_WIDTH = 70;
	public static final int TABLE_CELL_HEIGHT = 21; // G.Sturr (old height 20) +
													// 1 to stop cell editor
													// clipping
	// layout settings
	private boolean showFormulaBar = Defaults.SHOW_FORMULA_BAR;
	private boolean showGrid = Defaults.SHOW_GRID;
	private boolean showRowHeader = Defaults.SHOW_ROW_HEADER;
	private boolean showColumnHeader = Defaults.SHOW_COLUMN_HEADER;
	private boolean showVScrollBar = Defaults.SHOW_VSCROLLBAR;
	private boolean showHScrollBar = Defaults.SHOW_HSCROLLBAR;
	private boolean isColumnSelect = Defaults.IS_COLUMN_SELECT; // TODO: do we
																// need forced
																// column
																// select?
	private boolean allowSpecialEditor = Defaults.ALLOW_SPECIAL_EDITOR;
	private boolean allowToolTips = Defaults.ALLOW_TOOLTIPS;
	private boolean equalsRequired = Defaults.EQUALS_REQUIRED;
	private boolean enableAutoComplete = Defaults.ENABLE_AUTOCOMPLETE;

	// row and column size
	private HashMap<Integer, Integer> widthMap;
	private HashMap<Integer, Integer> heightMap;
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
	private int rows = 100;
	private int columns = 26;
	private PersistenceListener persistenceListener;

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

	public boolean hasInitialized() {
		return !(heightMap == null && widthMap == null);
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

	@Override
	public HashMap<Integer, Integer> getWidthMap() {
		if (widthMap == null) {
			widthMap = new HashMap<>();
		}
		return widthMap;
	}

	/**
	 * @param index
	 *            column
	 * @param width
	 *            width
	 */
	public void addWidth(int index, int width) {
		getWidthMap().put(index, width);
		settingChanged();
	}

	public void addWidthNoFire(int index, int width) {
		getWidthMap().put(index, width);
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

	@Override
	public HashMap<Integer, Integer> getHeightMap() {
		if (heightMap == null) {
			heightMap = new HashMap<>();
		}
		return heightMap;
	}

	/**
	 * @param index
	 *            row
	 * @param height
	 *            height in px
	 */
	public void addHeight(int index, int height) {
		getHeightMap().put(index, height);
		settingChanged();
	}

	public void addHeightNoFire(int row, int height) {
		getHeightMap().put(row, height);
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
	public boolean isRowColumnSizeDefaults() {
		return preferredColumnWidth == TABLE_CELL_WIDTH
				&& preferredRowHeight == TABLE_CELL_HEIGHT
				&& getWidthMap().size() == 0 && getHeightMap().size() == 0;
	}

	/**
	 * @return whether the selection is default
	 */
	public boolean isSelectionDefaults() {
		return hScrollBarValue == 0 && vScrollBarValue == 0
				&& selectedCell.getX() == 0 && selectedCell.getY() == 0;
	}

	/**
	 * @return whether layout settings are all default
	 */
	public boolean isLayoutDefaults() {
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
	 * @return whether prefered cell size is default
	 */
	public boolean isDefaultPreferredSize() {
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
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            whether this is for preference
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {
		if (!hasInitialized()) {
			return;
		}
		sb.append("<spreadsheetView>\n");

		GDimension size = preferredSize();
		int width = size.getWidth();
		int height = size.getHeight();

		if (!isDefaultPreferredSize()) {
			sb.append("\t<size ");
			if (width != 0) {
				sb.append(" width=\"");
				sb.append(width);
				sb.append("\"");
			}

			if (height != 0) {
				sb.append(" height=\"");
				sb.append(height);
				sb.append("\"");
			}

			sb.append("/>\n");
		}

		int prefWidth = preferredColumnWidth();
		int prefHeight = preferredRowHeight();

		if (prefWidth != TABLE_CELL_WIDTH || prefHeight != TABLE_CELL_HEIGHT) {
			sb.append("\t<prefCellSize ");
			if (prefWidth != TABLE_CELL_WIDTH) {
				sb.append(" width=\"");
				sb.append(prefWidth);
				sb.append("\"");
			}

			if (prefHeight != TABLE_CELL_HEIGHT) {

				sb.append(" height=\"");
				sb.append(prefHeight);
				sb.append("\"");
			}
			sb.append("/>\n");
		}

		if (!asPreference) {

			getWidthsAndHeightsXML(sb);

			// initial selection

			if (!isSelectionDefaults()) {
				sb.append("\t<selection ");

				if (hScrollBarValue != 0) {
					sb.append(" hScroll=\"");
					sb.append(hScrollBarValue);
					sb.append("\"");
				}

				if (vScrollBarValue != 0) {
					sb.append(" vScroll=\"");
					sb.append(vScrollBarValue);
					sb.append("\"");
				}

				if (selectedCell.getX() != 0) {
					sb.append(" column=\"");
					sb.append(selectedCell.getX());
					// sb.append(table.getColumnModel().getSelectionModel()
					// .getAnchorSelectionIndex());
					sb.append("\"");
				}

				if (selectedCell.getY() != 0) {
					sb.append(" row=\"");
					sb.append(selectedCell.getY());
					// sb.append(table.getSelectionModel().getAnchorSelectionIndex());
					sb.append("\"");

				}
				sb.append("/>\n");
			}
		}

		// layout
		getLayoutXML(sb);

		// cell formats

		if (!asPreference && hasCellFormat()) {
			sb.append("\t<spreadsheetCellFormat formatMap=\"");
			sb.append(cellFormat);
			sb.append("\"/>\n");
		}

		sb.append("</spreadsheetView>\n");

	}

	/**
	 * Add layout settings.
	 * 
	 * @param sb
	 *            XML string builder
	 */
	public void getLayoutXML(StringBuilder sb) {
		if (!isLayoutDefaults()) {
			sb.append("\t<layout ");

			if (showFormulaBar) {
				sb.append(" showFormulaBar=\"true\"");
			}

			if (showGrid) {
				sb.append(" showGrid=\"true\"");
			}

			if (showHScrollBar) {
				sb.append(" showHScrollBar=\"true\"");
			}

			if (showVScrollBar) {
				sb.append(" showVScrollBar=\"true\"");
			}

			if (showColumnHeader) {
				sb.append(" showColumnHeader=\"true\"");
			}

			if (showRowHeader) {
				sb.append(" showRowHeader=\"true\"");
			}

			if (allowSpecialEditor) {
				sb.append(" allowSpecialEditor=\"true\"");
			}

			if (allowToolTips) {
				sb.append(" allowToolTips=\"true\"");

			}

			if (equalsRequired) {
				sb.append(" equalsRequired=\"true\"");
			}

			if (enableAutoComplete) {
				sb.append(" autoComplete=\"true\"");
			}

			sb.append("/>\n");
		}

	}

	/**
	 * Append width / height settings to XML.
	 * 
	 * @param sb
	 *            XML string builder
	 */
	public void getWidthsAndHeightsXML(StringBuilder sb) {
		if (persistenceListener != null) {
			persistenceListener.persist(this);
		}
		// column widths
		HashMap<Integer, Integer> widthMap1 = getWidthMap();
		for (Entry<Integer, Integer> entry : widthMap1.entrySet()) {
			Integer col = entry.getKey();
			int colWidth = entry.getValue();
			if (colWidth != preferredColumnWidth()) {
				sb.append("\t<spreadsheetColumn id=\"").append(col)
						.append("\" width=\"").append(colWidth).append("\"/>\n");
			}
		}

		// row heights
		HashMap<Integer, Integer> heightMap1 = getHeightMap();
		for (Entry<Integer, Integer> entry : heightMap1.entrySet()) {
			Integer row = entry.getKey();
			int rowHeight = entry.getValue();
			if (rowHeight != preferredRowHeight()) {
				sb.append("\t<spreadsheetRow id=\"").append(row)
						.append("\" height=\"").append(rowHeight).append("\"/>\n");
			}
		}

	}

	/**
	 * Remove all custom heights
	 */
	public void clearHeights() {
		if (heightMap != null) {
			heightMap.clear();
		}
	}

	/**
	 * Remove all custom widths
	 */
	public void clearWidths() {
		if (widthMap != null) {
			widthMap.clear();
		}
	}

	@Override
	public int getColumns() {
		return columns;
	}

	@Override
	public int getRows() {
		return rows;
	}

	/**
	 * Print size XML tag to a builder
	 * @param sb output string builder
	 */
	public void getSizeXML(StringBuilder sb) {
		sb.append("<dimensions rows=\"").append(rows)
				.append("\" columns=\"").append(columns).append("\"/>");
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

	public void setPersistenceListener(PersistenceListener layout) {
		this.persistenceListener = layout;
	}

}
