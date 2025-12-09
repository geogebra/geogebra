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

package org.geogebra.web.full.euclidian;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

/**
 * Popup button with default value
 */
public class PopupMenuButtonWithDefault extends PopupMenuButtonW {

	private ImageOrText defaultIcon;

	/**
	 * @param app
	 *            app
	 * @param data
	 *            icons
	 */
	public PopupMenuButtonWithDefault(AppW app, ImageOrText[] data) {
		super(app, data, -1, data.length, SelectionTable.MODE_ICON);
		defaultIcon = data.length > 1 ? data[1] : null;
	}

	@Override
	public void setIcon(ImageOrText icon) {
		if (getSelectedIndex() == 0 && defaultIcon != null) {
			super.setIcon(defaultIcon);
			this.removeStyleName("selected");
		} else {
			super.setIcon(icon);
			this.addStyleName("selected");
		}
	}
}
