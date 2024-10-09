package org.geogebra.common.io.layout;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * A storage container with all information which need to be stored for a
 * DockPanel.
 * 
 * @author Florian Sonner
 */
final public class DockPanelData {
	private int viewId;
	private boolean isVisible;
	private boolean openInFrame;
	private boolean showStyleBar;
	private GRectangle frameBounds;
	// see DockManager.show() for an explanation of this, eg "1,1,1"
	private String embeddedDef;
	private String toolbarString;
	private int embeddedSize;
	private String plane;
	private TabIds tabId = TabIds.ALGEBRA;

	/**
	 * Tab ids.
	 */
	public enum TabIds {
		/** tab one */
		ALGEBRA,

		/** tab two */
		TOOLS,

		/** tab three */
		TABLE,

		/** tab four */
		DISTRIBUTION,

		/** tab five */
		SPREADSHEET
	}

	/**
	 * @param viewId
	 *            The view ID.
	 * @param toolbar
	 *            The toolbar string of this panel or null.
	 * @param isVisible
	 *            If this view is visible at the moment.
	 * @param openInFrame
	 *            If this view should be opened in a separate frame.
	 * @param showStyleBar
	 *            If the style bar is visible
	 * @param windowRect
	 *            The rectangle which defines the location and size of the
	 *            window for this view.
	 * @param embeddedDef
	 *            The definition string for the location of the view in the main
	 *            window.
	 * @param embeddedSize
	 *            The size of the view in the main window.
	 * @param plane
	 *            Plane that created the view (for EuclidianViewForPlane)
	 */
	public DockPanelData(int viewId, String toolbar, boolean isVisible,
			boolean openInFrame, boolean showStyleBar, GRectangle windowRect,
			String embeddedDef, int embeddedSize, String plane) {
		this.viewId = viewId;
		this.toolbarString = toolbar;
		this.isVisible = isVisible;
		this.openInFrame = openInFrame;
		this.showStyleBar = showStyleBar;
		this.frameBounds = windowRect;
		this.embeddedDef = embeddedDef;
		this.embeddedSize = embeddedSize;
		this.plane = plane;
	}

	/**
	 * @param viewId
	 *            The view ID.
	 * @param toolbar
	 *            The toolbar string of this panel or null.
	 * @param isVisible
	 *            If this view is visible at the moment.
	 * @param openInFrame
	 *            If this view should be opened in a separate frame.
	 * @param showStyleBar
	 *            If the style bar is visible
	 * @param windowRect
	 *            The rectangle which defines the location and size of the
	 *            window for this view.
	 * @param embeddedDef
	 *            The definition string for the location of the view in the main
	 *            window.
	 * @param embeddedSize
	 *            The size of the view in the main window.
	 */
	public DockPanelData(int viewId, String toolbar, boolean isVisible,
			boolean openInFrame, boolean showStyleBar, GRectangle windowRect,
			String embeddedDef, int embeddedSize) {
		this(viewId, toolbar, isVisible, openInFrame, showStyleBar, windowRect,
				embeddedDef, embeddedSize, null);
	}

	/**
	 * @param viewId
	 *            The view ID.
	 * @param toolbar
	 *            The toolbar string of this view or null.
	 * @param isVisible
	 *            If this view is visible at the moment.
	 * @param inFrame
	 *            If this view is in an separate window at the moment.
	 * @param showStyleBar
	 *            If the style bar is visible
	 * @param windowX
	 *            The x location of the window.
	 * @param windowY
	 *            The y location of the window.
	 * @param windowWidth
	 *            The width of the window.
	 * @param windowHeight
	 *            The height of the window.
	 * @param embeddedDef
	 *            The definition string for the location of the view in the main
	 *            window.
	 * @param embeddedSize
	 *            The size of the view in the main window.
	 */
	public DockPanelData(int viewId, String toolbar, boolean isVisible,
			boolean inFrame, boolean showStyleBar, int windowX, int windowY,
			int windowWidth, int windowHeight, String embeddedDef,
			int embeddedSize) {
		this(viewId, toolbar, isVisible, inFrame, showStyleBar,
				AwtFactory.getPrototype().newRectangle(windowX, windowY,
						windowWidth, windowHeight),
				embeddedDef, embeddedSize, null);
	}

	/**
	 * @param viewId
	 *            The view ID.
	 * @param toolbar
	 *            The toolbar string of this view or null.
	 * @param isVisible
	 *            If this view is visible at the moment.
	 * @param inFrame
	 *            If this view is in an separate window at the moment.
	 * @param showStyleBar
	 *            If the style bar is visible
	 * @param windowLoc
	 *            The location of the window.
	 * @param windowSize
	 *            The size of the window.
	 * @param embeddedDef
	 *            The definition string for the location of the view in the main
	 *            window.
	 * @param embeddedSize
	 *            The size of the view in the main window.
	 */
	public DockPanelData(int viewId, String toolbar, boolean isVisible,
			boolean inFrame, boolean showStyleBar, GPoint windowLoc,
			GDimension windowSize, String embeddedDef, int embeddedSize) {
		this(viewId, toolbar, isVisible, inFrame, showStyleBar,
				AwtFactory.getPrototype().newRectangle(windowLoc.getX(),
						windowLoc.getY(), windowSize.getWidth(),
						windowSize.getHeight()),
				embeddedDef, embeddedSize, null);
	}

	/**
	 * @return The view ID.
	 */
	public int getViewId() {
		return viewId;
	}

	/**
	 * set the view id
	 * 
	 * @param id
	 *            id
	 */
	public void setViewId(int id) {
		viewId = id;
	}

	/**
	 * @return The toolbar string of this view (or an empty string).
	 */
	public String getToolbarString() {
		return toolbarString;
	}

	/**
	 * Set the toolbar string of this view (or an empty string).
	 * 
	 * @param toolbar
	 *            toolbar string
	 */
	public void setToolbarString(String toolbar) {
		toolbarString = toolbar;
	}

	/**
	 * @return If this view is visible at the moment.
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @return If the DockPanel was shown in a frame the last time it was
	 *         visible.
	 */
	public boolean isOpenInFrame() {
		return openInFrame;
	}

	/**
	 * @return If the style bar is visible
	 */
	public boolean showStyleBar() {
		return showStyleBar;
	}

	/**
	 * @return the frameBounds
	 */
	public GRectangle getFrameBounds() {
		return frameBounds;
	}

	/**
	 * @return the embeddedDef
	 */
	public String getEmbeddedDef() {
		return embeddedDef;
	}

	/**
	 * @return the embeddedSize
	 */
	public int getEmbeddedSize() {
		return embeddedSize;
	}

	/**
	 * 
	 * @return the plane creator
	 */
	public String getPlane() {
		return plane;
	}

	/**
	 * 
	 * @return view id for XML
	 */
	private int getViewIdForXML() {
		return plane == null ? getViewId() : App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	}

	/**
	 * Appends XML representation of the data stored in this class.
	 * @param sb builder
	 */
	public void getXml(StringBuilder sb) {
		sb.append("<view id=\"");
		sb.append(getViewIdForXML());
		if (getToolbarString() != null) {
			sb.append("\" toolbar=\"");
			sb.append(getToolbarString());
		}
		sb.append("\" visible=\"");
		sb.append(isVisible());
		sb.append("\" inframe=\"");
		sb.append(isOpenInFrame());
		sb.append("\" stylebar=\"");
		sb.append(showStyleBar());
		sb.append("\" location=\"");
		sb.append(getEmbeddedDef());
		sb.append("\" size=\"");
		sb.append(getEmbeddedSize());
		if (viewId == App.VIEW_ALGEBRA) {
			sb.append("\" tab=\"");
			sb.append(tabId.name());
		}
		sb.append("\" window=\"");
		appendBounds(sb);

		if (plane != null) {
			sb.append("\" plane=\"");
			sb.append(getPlane());
		}
		sb.append("\" />\n");

	}

	private void appendBounds(StringBuilder sb) {
		sb.append((int) getFrameBounds().getX());
		sb.append(",");
		sb.append((int) getFrameBounds().getY());
		sb.append(",");
		sb.append((int) getFrameBounds().getWidth());
		sb.append(",");
		sb.append((int) getFrameBounds().getHeight());
	}

	/**
	 * Maybe false for non-visible views from plane
	 * 
	 * @return true if will be stored in XML
	 */
	public boolean storeXml() {
		return plane == null || isVisible();
	}

	/**
	 * Clone this object. Required as dock panels would change the loaded
	 * perspective automatically otherwise.
	 * 
	 * @return clone
	 */
	public DockPanelData duplicate() {
		return new DockPanelData(viewId, toolbarString, isVisible, openInFrame,
				showStyleBar, frameBounds, embeddedDef, embeddedSize, plane);
	}

	/**
	 * Make sure this is visible in the main frame
	 */
	public void makeVisible() {
		isVisible = true;
		this.openInFrame = false;
	}

	/**
	 * @param visible
	 *            whether this should be visible
	 */
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	/**
	 * @param s
	 *            comma separated list of left/right/top/bottom a.k.a. 1,2,3,4
	 */
	public void setLocation(String s) {
		this.embeddedDef = s;

	}

	/**
	 * @param tabId
	 *            active tab ID
	 * @return this
	 */
	public DockPanelData setTabId(TabIds tabId) {
		if (tabId != null) {
			this.tabId = tabId;
		} else {
			Log.error("Tab ID cannot be null");
		}
		return this;
	}

	/**
	 * @return whether this is open as toolbar
	 */
	public TabIds getTabId() {
		return tabId;
	}

	/**
	 * @return key for right-to-left, bottom-to-top sorting of the panels
	 */
	public String getRightToLeftSortingKey() {
		if (embeddedDef.isEmpty()) {
			// position missing: keep as last
			return "5";
		}
		// already have (right=2) < (left=3)
		// replace to get (bottom=2) < (top=4)
		return getEmbeddedDef().replace('0', '4');
	}

	/**
	 * Validate and tokenize given definition
	 * @param embeddedDef location definition
	 * @return location
	 */
	public static int[] parseLocation(String embeddedDef) {
		String[] def = embeddedDef.split(",");
		int[] locations = new int[def.length];

		for (int i = 0; i < def.length; ++i) {
			if (def[i].isEmpty()) {
				def[i] = "1";
			}

			locations[i] = Integer.parseInt(def[i]);

			if (locations[i] > 3 || locations[i] < 0) {
				locations[i] = 3; // left as default direction
			}
		}

		// We insert this panel at the left by default
		if (locations.length == 0) {
			locations = new int[] { 3 };
		}
		return locations;
	}

	/**
	 * @return array of child selecting indices; 0 for left/top, 1 for right/bottom
	 */
	public int[] getChildSelectors() {
		String[] def = embeddedDef.split(",");
		int[] childSelectors = new int[def.length];
		for (int i = 0; i < def.length; i++) {
			childSelectors[i] = "0".equals(def[i]) || "3".equals(def[i]) ? 0 : 1;
		}
		return childSelectors;
	}
}
