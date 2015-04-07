package org.geogebra.common.io.layout;

/**
 * A storage container for a split pane. Just used for saving & loading a
 * perspective as not all information can be stored in the DockPanelInfo.
 * 
 * @author Florian Sonner
 */
public class DockSplitPaneData {
	private String location;
	private double dividerLocation;
	private int orientation;

	/**
	 * @param location
	 *            location
	 * @param dividerLocation
	 *            divider position
	 * @param orientation
	 *            vertical or horizontal (JSplitPane.HORIZONTAL_SPLIT or
	 *            JSplitPane.VERTICAL_SPLIT)
	 */
	public DockSplitPaneData(String location, double dividerLocation,
			int orientation) {
		this.location = location;
		this.dividerLocation = dividerLocation;
		this.orientation = orientation;
	}

	/**
	 * @return The location definition string of the split pane
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return The location of the divider
	 */
	public double getDividerLocation() {
		return dividerLocation;
	}

	/**
	 * @return The orientation of the saved split pane
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * @return XML representation
	 */
	public String getXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<pane location=\"");
		sb.append(location);
		sb.append("\" divider=\"");
		sb.append(dividerLocation);
		sb.append("\" orientation=\"");
		sb.append(orientation);
		sb.append("\" />");
		return sb.toString();
	}
}
