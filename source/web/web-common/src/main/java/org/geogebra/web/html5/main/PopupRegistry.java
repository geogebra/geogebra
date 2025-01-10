package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.gui.GPopupPanel;

/**
 * List of popups
 */
public class PopupRegistry {
	private List<GPopupPanel> popups = new ArrayList<>();

	/**
	 * @param popup
	 *            popup to add
	 */
	public void add(GPopupPanel popup) {
		if (!popups.contains(popup)) {
			popups.add(popup);
		}
	}

	/**
	 * close all popups
	 */
	public void closeAll() {
		for (GPopupPanel popup : popups) {
			popup.hide();
		}
	}
}
