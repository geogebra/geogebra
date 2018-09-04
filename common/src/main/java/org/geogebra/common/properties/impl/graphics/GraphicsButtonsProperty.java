package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ButtonsProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Property for Graphics Views buttons
 *
 */
public class GraphicsButtonsProperty implements ButtonsProperty {

	private EuclidianView ev;
	private Localization localization;
	private String[] captions;
	private PropertyResource[] icons;

	/**
	 * constructor
	 * 
	 * @param localization
	 *            localization
	 * @param ev
	 *            euclidian view
	 */
	public GraphicsButtonsProperty(Localization localization,
			EuclidianView ev) {
		this.ev = ev;
		this.localization = localization;
	}

	public String getName() {
		// no name
		return null;
	}

	public boolean isEnabled() {
		return true;
	}

	public String[] getCaptions() {
		if (captions == null) {
			captions = new String[] { localization.getMenu("StandardView"),
					localization.getMenu("ShowAllObjects") };
		}
		return captions;
	}

	public PropertyResource[] getIcons() {
		if (icons == null) {
			icons = new PropertyResource[] {
					PropertyResource.ICON_STANDARD_VIEW,
					PropertyResource.ICON_ZOOM_TO_FIT };
		}
		return icons;
	}

	public void onButtonClicked(int i) {
		if (i == 0) {
			ev.setStandardView(true);
		} else {
			ev.setViewShowAllObjects(true, false);
		}
	}


}
