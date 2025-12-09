/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
