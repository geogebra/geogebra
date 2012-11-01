package geogebra.common.main.settings;


import geogebra.common.awt.GDimension;
import geogebra.common.awt.GPoint;
import geogebra.common.factories.AwtFactory;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Settings for the spreadsheet view.
 */
public class SpreadsheetSettings extends AbstractSettings {

	public static final int TABLE_CELL_WIDTH = 70;
	public static final int TABLE_CELL_HEIGHT = 21;  //G.Sturr (old height 20) + 1 to stop cell editor clipping
	// layout settings
	private boolean showFormulaBar = false;
	private boolean showGrid = true;
	private boolean showRowHeader = true;
	private boolean showColumnHeader = true;	
	private boolean showVScrollBar = true;
	private boolean showHScrollBar = true;
	private boolean showBrowserPanel = false;
	private boolean isColumnSelect = false; //TODO: do we need forced column select?
	private boolean allowSpecialEditor = false;
	private boolean allowToolTips = true;
	private boolean equalsRequired; 
	private boolean enableAutoComplete;
	
	// file browser settings
	private String defaultFile; 
	private String initialURL;
	private String initialFilePath; 
	private int initialBrowserMode = -1;
	private boolean isDefaultBrowser = true;

	// row and column size
	private HashMap<Integer,Integer> widthMap;
	private HashMap<Integer,Integer> heightMap;
	private int preferredColumnWidth = TABLE_CELL_WIDTH;
	private int preferredRowHeight = TABLE_CELL_HEIGHT;

	// cell format
	private String cellFormat;
	
	// initial selection
	private GPoint scrollPosition = new GPoint(0,0);
	private GPoint selectedCell = new GPoint(0,0);
	
	// preferred size
	private GDimension preferredSize;
	
	
	//============================================
	//  Row/Column Dimension Settings
	//============================================

	public SpreadsheetSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
		preferredSize = AwtFactory.prototype.newDimension(0,0);
	}

	public SpreadsheetSettings() {
		super();
		preferredSize = AwtFactory.prototype.newDimension(0,0);
	}

	public HashMap<Integer,Integer> getWidthMap(){
		if(widthMap == null)
			widthMap = new HashMap<Integer,Integer>();
		return widthMap;
	}

	public void addWidth(int index, int width){
		getWidthMap().put(index,width);
		settingChanged();		
	}
	
	public int preferredColumnWidth(){
		return preferredColumnWidth;
	}

	public void setPreferredColumnWidth(int prefWidth){
		this.preferredColumnWidth = prefWidth;
		settingChanged();		
	}	
	
	public HashMap<Integer,Integer> getHeightMap(){
		if(heightMap == null)
			heightMap = new HashMap<Integer,Integer>();
		return heightMap;
	}

	public void addHeight(int index, int height){
		getHeightMap().put(index,height);
		settingChanged();	
	}
	
	public int preferredRowHeight(){
		return preferredRowHeight;
	}

	public void setPreferredRowHeight(int preferredRowHeight){
		this.preferredRowHeight = preferredRowHeight;
		settingChanged();
	}
	

	//============================================
	//  Layout Settings
	//============================================

	/**
	 * @return the showFormulaBar
	 */
	public boolean showFormulaBar() {
		return showFormulaBar;
	}

	/**
	 * @param showFormulaBar the showFormulaBar to set
	 */
	public void setShowFormulaBar(boolean showFormulaBar) {
		if(this.showFormulaBar != showFormulaBar) {
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
	 * @param showGrid the showGrid to set
	 */
	public void setShowGrid(boolean showGrid) {
		if(this.showGrid != showGrid) {
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
	 * @param showRowHeader the showRowHeader to set
	 */
	public void setShowRowHeader(boolean showRowHeader) {
		if(this.showRowHeader != showRowHeader) {
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
	 * @param showColumnHeader the showColumnHeader to set
	 */
	public void setShowColumnHeader(boolean showColumnHeader) {
		if(this.showColumnHeader != showColumnHeader) {
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
	 * @param showVScrollBar the showVScrollBar to set
	 */
	public void setShowVScrollBar(boolean showVScrollBar) {
		if(this.showVScrollBar != showVScrollBar) {
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
	 * @param showHScrollBar the showHScrollBar to set
	 */
	public void setShowHScrollBar(boolean showHScrollBar) {
		if(this.showHScrollBar != showHScrollBar) {
			this.showHScrollBar = showHScrollBar;
			settingChanged();
		}
	}

	/**
	 * @return the showBrowserPanel
	 */
	public boolean showBrowserPanel() {
		return showBrowserPanel;
	}

	/**
	 * @param showBrowserPanel the showBrowserPanel to set
	 */
	public void setShowFileBrowser(boolean showBrowserPanel) {
		if(this.showBrowserPanel != showBrowserPanel) {
			this.showBrowserPanel = showBrowserPanel;
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
	 * @param allowSpecialEditor the allowSpecialEditor to set
	 */
	public void setAllowSpecialEditor(boolean allowSpecialEditor) {
		if(this.allowSpecialEditor != allowSpecialEditor) {
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
	 * @param allowToolTips the allowToolTips to set
	 */
	public void setAllowToolTips(boolean allowToolTips) {
		if(this.allowToolTips != allowToolTips) {
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
	 * @param equalsRequired the equalsRequired to set
	 */
	public void setEqualsRequired(boolean equalsRequired) {
		if(this.equalsRequired != equalsRequired) {
			this.equalsRequired = equalsRequired;
			//settingChanged();
		}
	}

	/**
	 * @return the isColumnSelect
	 */
	public boolean isColumnSelect() {
		return isColumnSelect;
	}

	/**
	 * @param isColumnSelect the isColumnSelect to set
	 */
	public void setColumnSelect(boolean isColumnSelect) {
		if(this.isColumnSelect != isColumnSelect) {
			this.isColumnSelect = isColumnSelect;
			settingChanged();
		}
	}


	//============================================
	//  Cell Format Settings
	//============================================
	
	/**
	 * @return the cellFormat
	 */
	public String cellFormat() {
		return cellFormat;
	}

	/**
	 * @param cellFormat the cellFormat to set
	 */
	public void setCellFormat(String cellFormat) {
		if(this.cellFormat != null && this.cellFormat.equals(cellFormat)) return;
			this.cellFormat = cellFormat;
			settingChanged();
	}
	

	//============================================
	//  Initial Position Settings
	//============================================
	/**
	 * @return the scrollPosition
	 */
	public GPoint scrollPosition() {
		return scrollPosition;
	}

	/**
	 * @param scrollPosition the scrollPosition to set
	 */
	public void setScrollPosition(GPoint scrollPosition) {
		if(this.scrollPosition == null || !this.scrollPosition.equals(scrollPosition)) {
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
	 * @param selectedCell the selectedCell to set
	 */
	public void setSelectedCell(GPoint selectedCell) {
		if(this.selectedCell == null || !this.selectedCell.equals(selectedCell)) {
			this.selectedCell = selectedCell;
			settingChanged();
		}
	}
		

	//============================================
	//  PreferredSize Settings
	//============================================
	/**
	 * @return the preferredSize
	 */
	public GDimension preferredSize() {
		return preferredSize;
	}

	/**
	 * @param preferredSize the preferredSize to set
	 */
	public void setPreferredSize(GDimension preferredSize) {
		if(this.preferredSize == null || !this.preferredSize.equals(preferredSize)) {
			this.preferredSize = preferredSize;
			settingChanged();
		}
	}
	
	
	//============================================
	//  File Browser Settings
	//============================================

	/**
	 * @return the defaultFile
	 */
	public String defaultFile() {
		return defaultFile;
	}

	/**
	 * @param defaultFile the defaultFile to set
	 */
	public void setDefaultFile(String defaultFile) {
		if(this.defaultFile != null && this.defaultFile.equals(defaultFile)) return;
			this.defaultFile = defaultFile;
			settingChanged();
	}

	/**
	 * @return the initialURL
	 */
	public String initialURL() {
		return initialURL;
	}

	/**
	 * @param initialURL the initialURL to set
	 */
	public void setInitialURL(String initialURL) {
		if(this.initialURL != null && this.initialURL.equals(initialURL)) return;
		this.initialURL = initialURL;
		settingChanged();
	}
	
	/**
	 * @return the initialFilePath
	 */
	public String initialFilePath() {
		return initialFilePath;
	}

	/**
	 * @param initialFilePath the initialFilePath to set
	 */
	public void setInitialFilePath(String initialFilePath) {
		if(this.initialFilePath != null && this.initialFilePath.equals(initialFilePath)) return;
			this.initialFilePath = initialFilePath;
			settingChanged();
	}
	
	/**
	 * @return the initialBrowserMode
	 */
	public int initialBrowserMode() {
		return initialBrowserMode;
	}

	/**
	 * @param initialBrowserMode the initialBrowserMode to set
	 */
	public void setInitialBrowserMode(int initialBrowserMode) {
		if(this.initialBrowserMode != initialBrowserMode) {
			this.initialBrowserMode = initialBrowserMode;
			settingChanged();
		}
	}

	/**
	 * @return the isDefaultBrowser
	 */
	public boolean isDefaultBrowser() {
		return isDefaultBrowser;
	}

	/**
	 * @param isDefaultBrowser the isDefaultBrowser to set
	 */
	public void setDefaultBrowser(boolean isDefaultBrowser) {
		if(this.isDefaultBrowser != isDefaultBrowser) {
			this.isDefaultBrowser = isDefaultBrowser;
			settingChanged();
		}
	}

	/**
	 * @param enableAutoComplete	flag to allow auto-complete in the editor
	 */
	public void setEnableAutoComplete(boolean enableAutoComplete) {
		if(this.enableAutoComplete != enableAutoComplete) {
			this.enableAutoComplete = enableAutoComplete;
			settingChanged();
		}
	}
	
	/**
	 * @return	is auto-complete allowed in the editor
	 */
	public boolean isEnableAutoComplete() {
		return enableAutoComplete;
	}


}
