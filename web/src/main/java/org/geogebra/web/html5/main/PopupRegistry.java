package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.gui.GPopupPanel;

public class PopupRegistry {
	private List<GPopupPanel> popups = new ArrayList<>();

	public void add(GPopupPanel popup) {
		if (!popups.contains(popup)) {
			popups.add(popup);
		}
	}

	public void closeAll() {
		for (GPopupPanel popup : popups) {
			popup.hide();
		}
	}
}
