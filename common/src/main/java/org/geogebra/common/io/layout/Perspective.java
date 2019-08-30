package org.geogebra.common.io.layout;

import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.util.StringUtil;

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
	private InputPosition showInputPanelOnTop = InputPosition.algebraView;

	// needs to be initialized so that files from ggb32 show the toolbar #2993
	private int toolBarPosition = SwingConstants.NORTH;

	private boolean showToolBarHelp;

	private boolean showDockBar;

	private boolean isDockBarEast;

	private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_NOT_SET;

	private int defaultID;
	/** translation keys for perspective names */
	final private static String[] perspectiveNames = new String[] { "Custom",
			"Graphing", "Perspective.Geometry",
			"Perspective.Spreadsheet", "Perspective.CAS",
			"Perspective.3DGraphics", "Perspective.Probability",
			"Notes", "Scientific", "Evaluator" };
	/** slugs for web app url / tutorials url */
	final private static String[] perspectiveSlugs = new String[] { "graphing",
			"geometry", "spreadsheet", "cas", "3d", "probability",
			"notes" };
	/** graphing calculator */
	public static final int GRAPHING = 1;
	/** geometry calculator */
	public static final int GEOMETRY = 2;
	/** spreadsheet calc */
	public static final int SPREADSHEET = 3;
	/** CAS calculator */
	public static final int CAS = 4;
	/** 3D calculator */
	public static final int GRAPHER_3D = 5;
	/** probability calculator */
	public static final int PROBABILITY = 6;
	/** Whiteboard */
	public static final int NOTES = 7;
	/** Scientific calc */
	public static final int SCIENTIFIC = 8;
	/** Evaluator */
	public static final int EVALUATOR = 9;

	/**
	 * 14.7.2016 when the feature flag "NEW_START_SCREEN" is removed, the
	 * perspectivesSlugs should be changed to the list below. because of the new
	 * order of the perspectives also the url strings need to be in the right
	 * order
	 * 
	 * if(app.has(Feature.NEW_START_SCREEN)){ final public static String[]
	 * perspectiveSlugs = new String[] { "graphing", "cas", "geometry", "3d",
	 * "spreadsheet", "probability" }; }
	 */

	/**
	 * Create a perspective with default layout.
	 * 
	 * @param defaultID
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
	 * @param inputPosition
	 *            position of the InputField/InputBox
	 */
	public Perspective(int defaultID, DockSplitPaneData[] splitPaneInfo,
			DockPanelData[] dockPanelInfo, String toolbarDefinition,
			boolean showToolBar, boolean showGrid, boolean showAxes,
			boolean showInputPanel, boolean showInputPanelCommands,
			InputPosition inputPosition) {
		this.defaultID = defaultID;
		this.id = perspectiveNames[defaultID];
		this.splitPaneData = splitPaneInfo;
		this.setDockPanelData(dockPanelInfo);
		this.setToolbarDefinition(toolbarDefinition);
		this.setShowToolBar(showToolBar);
		this.showAxes = showAxes;
		this.setShowGrid(showGrid);
		this.showInputPanel = showInputPanel;
		this.showInputPanelCommands = showInputPanelCommands;
		this.showInputPanelOnTop = inputPosition;

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
	 * @param inputPosition
	 *            position of the InputField/InputBox
	 * @param toolBarPosition
	 *            see {@link #setToolBarPosition(int)}
	 * @param showToolBarHelp
	 *            whether toolbar help should be visible
	 * @param showDockBar
	 *            whether dock bar should be visible
	 * @param isDockBarEast
	 *            whether dock bar should be on the eastern side
	 */
	public Perspective(String id, DockSplitPaneData[] splitPaneInfo,
			DockPanelData[] dockPanelInfo, String toolbarDefinition,
			boolean showToolBar, boolean showGrid, boolean showAxes,
			boolean showInputPanel, boolean showInputPanelCommands,
			InputPosition inputPosition, int toolBarPosition,
			boolean showToolBarHelp, boolean showDockBar,
			boolean isDockBarEast) {
		this.id = id;
		this.splitPaneData = splitPaneInfo;
		this.setDockPanelData(dockPanelInfo);
		this.setToolbarDefinition(toolbarDefinition);
		this.setShowToolBar(showToolBar);
		this.showAxes = showAxes;
		this.setShowGrid(showGrid);
		this.showInputPanel = showInputPanel;
		this.showInputPanelCommands = showInputPanelCommands;
		this.showInputPanelOnTop = inputPosition;
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
	 * @param inputPosition
	 *            new position of inputPanel (respective inputBox)
	 */
	public void setInputPosition(InputPosition inputPosition) {
		this.showInputPanelOnTop = inputPosition;
	}

	/**
	 * @return If the input panel should be displayed at the top of the screen
	 *         instead of the bottom.
	 */
	public InputPosition getInputPosition() {
		return showInputPanelOnTop;
	}

	/**
	 * @return toolbar position, see {@link #setToolBarPosition(int)}
	 */
	public int getToolBarPosition() {
		return toolBarPosition;
	}

	/**
	 * @param toolBarPosition
	 *            1 = NORTH, 3=EAST, 5=SOUTH, 7=WEST
	 */
	public void setToolBarPosition(int toolBarPosition) {
		this.toolBarPosition = toolBarPosition;
	}

	/**
	 * @return whether tool help should be shown
	 */
	public boolean getShowToolBarHelp() {
		return showToolBarHelp;
	}

	/**
	 * @param showToolBarHelp
	 *            whether toolbar help is shown
	 */
	public void setShowToolBarHelp(boolean showToolBarHelp) {
		this.showToolBarHelp = showToolBarHelp;
	}

	/**
	 * @return whether dockbar is shown
	 */
	public boolean getShowDockBar() {
		return showDockBar;
	}

	/**
	 * @param showDockBar
	 *            whether dockbar is shown
	 */
	public void setShowDockBar(boolean showDockBar) {
		this.showDockBar = showDockBar;
	}

	/**
	 * @return true for dockbar on eastern side
	 */
	public boolean isDockBarEast() {
		return isDockBarEast;
	}

	/**
	 * @param isDockBarEast
	 *            true to place the dockbar east, false for west
	 */
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

		getPanesXML(sb);

		getViewsXML(sb);

		getToolbarXML(sb);

		// skip axes & grid for document perspectives
		if (!"tmp".equals(id)) {
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
		sb.append(getInputPosition() == InputPosition.top ? "true"
				: (getInputPosition() == InputPosition.bottom ? "false"
						: "algebra"));
		sb.append("\" />\n");

		getDockbarXML(sb);

		sb.append("</perspective>\n");

		return sb.toString();
	}

	private void getPanesXML(StringBuilder sb) {
		sb.append("\t<panes>\n");
		for (int i = 0; i < splitPaneData.length; ++i) {
			sb.append("\t\t");
			sb.append(splitPaneData[i].getXml());
			sb.append("\n");
		}
		sb.append("\t</panes>\n");

	}

	private void getToolbarXML(StringBuilder sb) {
		// main toolbar
		sb.append("\t<toolbar show=\"");
		sb.append(getShowToolBar());
		if (getToolbarDefinition() != null) {
			sb.append("\" items=\"");
			sb.append(getToolbarDefinition());
		}
		sb.append("\" position=\"");
		sb.append(getToolBarPosition());
		sb.append("\" help=\"");
		sb.append(getShowToolBarHelp());
		sb.append("\" />\n");

	}

	private void getViewsXML(StringBuilder sb) {
		sb.append("\t<views>\n");
		for (int i = 0; i < getDockPanelData().length; ++i) {
			DockPanelData data = getDockPanelData()[i];
			if (data.storeXml()) {
				sb.append("\t\t");
				sb.append(data.getXml());
			}
		}
		sb.append("\t</views>\n");

	}

	private void getDockbarXML(StringBuilder sb) {
		// dockbar
		sb.append("\t<dockBar show=\"");
		sb.append(getShowDockBar());
		sb.append("\" east=\"");
		sb.append(isDockBarEast());
		sb.append("\" />\n");

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

	/**
	 * @return whether a view that works with keyboard is showing
	 */
	public boolean isKeyboardNeeded() {
		if (!this.showInputPanel) {
			return false;
		}
		for (DockPanelData dp : this.dockPanelData) {
			if (dp.getViewId() == App.VIEW_ALGEBRA && dp.isVisible()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return id of this perspective if it's default
	 */
	public int getDefaultID() {
		return defaultID;
	}

	/**
	 * @param perspID
	 *            perspective id
	 * @return perspective name
	 */
	public static String getPerspectiveName(int perspID) {
		return perspectiveNames[perspID];
	}

	/**
	 * @param perspID
	 *            perspective id
	 * @return perspective URL slug
	 */
	public static String getPerspectiveSlug(int perspID) {
		return perspectiveSlugs[perspID];
	}

	/**
	 * @param id
	 *            perspective ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	public int getLabelingStyle() {
		return labelingStyle;
	}

	public void setLabelingStyle(int labelingStyle) {
		this.labelingStyle = labelingStyle;
	}
}
