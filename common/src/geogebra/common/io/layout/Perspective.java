package geogebra.common.io.layout;

import javax.swing.SwingConstants;

import geogebra.common.util.StringUtil;

/**
 * Structure for a perspective which consists of the docks and the toolbar
 * definition. This structure is used to save and load perspectives, however, no
 * objects are serialized - this class stores strings only to save and restore
 * the layout of the user. This class is not intended to be updated in realtime
 * too. Perspectives may be loaded at the beginning and in between, but the
 * initially loaded perspective just needs to be updated if the user wants to
 * save his perspective.
 * 
 * @author Florian Sonner
 */
public class Perspective {
	/**
	 * The ID of this perspective.
	 */
	private String id;

	/**
	 * The information about every dock split pane
	 */
	private DockSplitPaneData[] splitPaneData;

	/**
	 * The information about every dock panel.
	 */
	private DockPanelData[] dockPanelData;

	/**
	 * The definition string for the toolbar.
	 */
	private String toolbarDefinition;

	/**
	 * If the tool bar should be displayed.
	 */
	private boolean showToolBar;

	/**
	 * If the grid should be displayed.
	 */
	private boolean showGrid;

	/**
	 * If the axes should be displayed.
	 */
	private boolean showAxes;

	/**
	 * If the axes should be displayed.
	 */
	private boolean unitAxesRatio;

	/**
	 * If the input panel should be displayed.
	 */
	private boolean showInputPanel;

	/**
	 * If the command list should be displayed.
	 */
	private boolean showInputPanelCommands;

	/**
	 * If the input panel should be displayed on top or at the bottom of the
	 * screen.
	 */
	private boolean showInputPanelOnTop;

	private int toolBarPosition;

	private boolean showToolBarHelp;
	
	private boolean showDockBar;
	
	private boolean isDockBarEast;

	private String iconString = null;

	
	/**
	 * Create a perspective with default layout. 
	 * 
	 * @param id
	 *            id
	 * @param splitPaneInfo
	 *            split settings
	 * @param dockPanelInfo
	 *            dock panel settings
	 * @param toolbarDefinition
	 *            toolbar string
	 * @param showToolBar
	 *            true to show toolbar
	 * @param showGrid
	 *            true to show grid
	 * @param showAxes
	 *            true to show axes
	 * @param showInputPanel
	 *            true to show input bar
	 * @param showInputPanelCommands
	 *            true to show input help
	 * @param showInputPanelOnTop
	 *            true to show input bar on top
	 */
	public Perspective(String id, DockSplitPaneData[] splitPaneInfo,
			DockPanelData[] dockPanelInfo, String toolbarDefinition,
			boolean showToolBar, boolean showGrid, boolean showAxes,
			boolean showInputPanel, boolean showInputPanelCommands,
			boolean showInputPanelOnTop) {
		this.id = id;
		this.splitPaneData = splitPaneInfo;
		this.setDockPanelData(dockPanelInfo);
		this.setToolbarDefinition(toolbarDefinition);
		this.setShowToolBar(showToolBar);
		this.showAxes = showAxes;
		this.setShowGrid(showGrid);
		this.showInputPanel = showInputPanel;
		this.showInputPanelCommands = showInputPanelCommands;
		this.showInputPanelOnTop = showInputPanelOnTop;
		
		// default layout options
		this.setShowToolBar(true);
		this.setShowToolBarHelp(false);
		this.setToolBarPosition(SwingConstants.NORTH);
		this.setDockBarEast(true);
		this.setShowDockBar(true);
				
	}
	
	
	/**
	 * Create a perspective with all available fields.
	 * 
	 * @param id
	 *            id
	 * @param splitPaneInfo
	 *            split settings
	 * @param dockPanelInfo
	 *            dock panel settings
	 * @param toolbarDefinition
	 *            toolbar string
	 * @param showToolBar
	 *            true to show toolbar
	 * @param showGrid
	 *            true to show grid
	 * @param showAxes
	 *            true to show axes
	 * @param showInputPanel
	 *            true to show input bar
	 * @param showInputPanelCommands
	 *            true to show input help
	 * @param showInputPanelOnTop
	 *            true to show input bar on top
	 * @param toolBarPosition
	 * @param showToolBarHelp
	 * @param showDockBar
	 * @param isDockBarEast 
	 */
	public Perspective(String id, DockSplitPaneData[] splitPaneInfo,
			DockPanelData[] dockPanelInfo, String toolbarDefinition,
			boolean showToolBar, boolean showGrid, boolean showAxes,
			boolean showInputPanel, boolean showInputPanelCommands,
			boolean showInputPanelOnTop, int toolBarPosition,
			boolean showToolBarHelp, boolean showDockBar, boolean isDockBarEast) {
		this.id = id;
		this.splitPaneData = splitPaneInfo;
		this.setDockPanelData(dockPanelInfo);
		this.setToolbarDefinition(toolbarDefinition);
		this.setShowToolBar(showToolBar);
		this.showAxes = showAxes;
		this.setShowGrid(showGrid);
		this.showInputPanel = showInputPanel;
		this.showInputPanelCommands = showInputPanelCommands;
		this.showInputPanelOnTop = showInputPanelOnTop;
		this.toolBarPosition = toolBarPosition;
		this.showToolBarHelp = showToolBarHelp;
		this.showDockBar = showDockBar;
		this.isDockBarEast = isDockBarEast;
	}

	/**
	 * Create an empty perspective.
	 * 
	 * @param id
	 *            perspective ID
	 */
	public Perspective(String id) {
		this.id = id;
	}

	/**
	 * @return The ID of the perspective.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param splitPaneData
	 *            The new description of the split panes.
	 */
	public void setSplitPaneData(DockSplitPaneData[] splitPaneData) {
		this.splitPaneData = splitPaneData;
	}

	/**
	 * @return The description of all split panes
	 */
	public DockSplitPaneData[] getSplitPaneData() {
		return splitPaneData;
	}

	/**
	 * @return The description of all DockPanels in the window.
	 */
	public DockPanelData[] getDockPanelData() {
		return dockPanelData;
	}

	/**
	 * @param dockPanelData
	 *            the dockPanelInfo to set
	 */
	public void setDockPanelData(DockPanelData[] dockPanelData) {
		this.dockPanelData = dockPanelData;
	}

	/**
	 * @param showToolBar
	 *            true to show toolbar
	 */
	public void setShowToolBar(boolean showToolBar) {
		this.showToolBar = showToolBar;
	}

	/**
	 * @return If the tool bar is visible.
	 */
	public boolean getShowToolBar() {
		return showToolBar;
	}

	/**
	 * @param toolbarDefinition
	 *            The definition string of the toolbar.
	 */
	public void setToolbarDefinition(String toolbarDefinition) {
		this.toolbarDefinition = toolbarDefinition;
	}

	/**
	 * @return The definition string of the toolbar.
	 */
	public String getToolbarDefinition() {
		return toolbarDefinition;
	}

	/**
	 * @param showGrid
	 *            If the grid should be displayed in this perspective.
	 */
	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	/**
	 * @return If the grid should be displayed.
	 */
	public boolean getShowGrid() {
		return showGrid;
	}

	/**
	 * @param showAxes
	 *            If the axes should be displayed.
	 */
	public void setShowAxes(boolean showAxes) {
		this.showAxes = showAxes;
	}

	/**
	 * @return If the axes should be displayed.
	 */
	public boolean getShowAxes() {
		return showAxes;
	}

	/**
	 * @param showInputPanel
	 *            If the input panel should be displayed.
	 */
	public void setShowInputPanel(boolean showInputPanel) {
		this.showInputPanel = showInputPanel;
	}

	/**
	 * @return If the input panel should be displayed.
	 */
	public boolean getShowInputPanel() {
		return showInputPanel;
	}

	/**
	 * @param showInputPanelCommands
	 *            true to show input help
	 */
	public void setShowInputPanelCommands(boolean showInputPanelCommands) {
		this.showInputPanelCommands = showInputPanelCommands;
	}

	/**
	 * @return If the command list should be displayed in the input panel.
	 */
	public boolean getShowInputPanelCommands() {
		return showInputPanelCommands;
	}

	/**
	 * @param showInputPanelOnTop
	 *            true to show input bar on top
	 */
	public void setShowInputPanelOnTop(boolean showInputPanelOnTop) {
		this.showInputPanelOnTop = showInputPanelOnTop;
	}

	/**
	 * @return If the input panel should be displayed at the top of the screen
	 *         instead of the bottom.
	 */
	public boolean getShowInputPanelOnTop() {
		return showInputPanelOnTop;
	}

	public int getToolBarPosition() {
		return toolBarPosition;
	}

	public void setToolBarPosition(int toolBarPosition) {
		this.toolBarPosition = toolBarPosition;
	}

	public boolean getShowToolBarHelp() {
		return showToolBarHelp;
	}

	public void setShowToolBarHelp(boolean showToolBarHelp) {
		this.showToolBarHelp = showToolBarHelp;
	}

	public boolean getShowDockBar() {
		return showDockBar;
	}

	public void setShowDockBar(boolean showDockBar) {
		this.showDockBar = showDockBar;
	}
	
	public boolean isDockBarEast() {
		return isDockBarEast;
	}

	public void setDockBarEast(boolean isDockBarEast) {
		this.isDockBarEast = isDockBarEast;
	}

	// *********************************************************
	// XML
	// *********************************************************

	/**
	 * @return The settings of this perspective as XML.
	 */
	public String getXml() {
		StringBuilder sb = new StringBuilder();

		sb.append("<perspective id=\"");
		StringUtil.encodeXML(sb, getId());
		sb.append("\">\n");

		sb.append("\t<panes>\n");
		for (int i = 0; i < splitPaneData.length; ++i) {
			sb.append("\t\t");
			sb.append(splitPaneData[i].getXml());
			sb.append("\n");
		}
		sb.append("\t</panes>\n");

		sb.append("\t<views>\n");
		for (int i = 0; i < getDockPanelData().length; ++i) {
			sb.append("\t\t");
			sb.append(getDockPanelData()[i].getXml());
		}
		sb.append("\t</views>\n");

		// main toolbar
		sb.append("\t<toolbar show=\"");
		sb.append(getShowToolBar());
		sb.append("\" items=\"");
		sb.append(getToolbarDefinition());
		sb.append("\" position=\"");
		sb.append(getToolBarPosition());
		sb.append("\" help=\"");
		sb.append(getShowToolBarHelp());
		sb.append("\" />\n");

		// skip axes & grid for document perspectives
		if (!id.equals("tmp")) {
			sb.append("\t<show axes=\"");
			sb.append(getShowAxes());
			sb.append("\" grid=\"");
			sb.append(getShowGrid());
			sb.append("\" />\n");
			if (isUnitAxesRatio()) {
				sb.append("<unitAxesRatio val=\"true\">");
			}
		}

		// algebra input bar
		sb.append("\t<input show=\"");
		sb.append(getShowInputPanel());
		sb.append("\" cmd=\"");
		sb.append(getShowInputPanelCommands());
		sb.append("\" top=\"");
		sb.append(getShowInputPanelOnTop());
		sb.append("\" />\n");

		// dockbar
		sb.append("\t<dockBar show=\"");
		sb.append(getShowDockBar());
		sb.append("\" east=\"");
		sb.append(isDockBarEast());
		sb.append("\" />\n");

		sb.append("</perspective>\n");

		return sb.toString();
	}

	/**
	 * @param unitAxesRatio
	 *            the unitAxesRatio to set
	 */
	public void setUnitAxesRatio(boolean unitAxesRatio) {
		this.unitAxesRatio = unitAxesRatio;
	}

	/**
	 * @return the unitAxesRatio
	 */
	public boolean isUnitAxesRatio() {
		return unitAxesRatio;
	}

	public String getIconString() {
		return iconString;
	}

	public void setIconString(String iconString) {
		this.iconString = iconString;
	}

}
