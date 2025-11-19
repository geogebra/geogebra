package org.geogebra.common.io.layout;

import org.geogebra.common.io.XMLStringBuilder;

/**
 * A storage container for a split pane. Just used for saving &amp; loading a
 * perspective as not all information can be stored in the DockPanelInfo.
 * 
 * @author Florian Sonner
 */
public class DockSplitPaneData {
	private final String location;
	private double dividerLocation;
	private final int orientation;

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
	 * @param sb builder XML representation
	 */
	public void getXml(XMLStringBuilder sb) {
		sb.startTag("pane");
		sb.attrRaw("location", location);
		sb.attr("divider", dividerLocation);
		sb.attr("orientation", orientation);
		sb.endTag();
	}

	/**
	 * @param ratio
	 *            divider percentage
	 */
	public void setDivider(double ratio) {
		this.dividerLocation = ratio;
	}

	/**
	 * @return array of child selecting indices; 0 for left/top, 1 for right/bottom
	 */
	public int[] getChildSelectors() {
		String[] def = location.split(",");
		int[] selectors = new int[def.length];
		for (int i = 0; i < selectors.length; i++) {
			selectors[i] = "0".equals(def[i]) ? 0 : 1;
		}
		return selectors;
	}
}
