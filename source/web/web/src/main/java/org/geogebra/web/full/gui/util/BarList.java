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

package org.geogebra.web.full.gui.util;

import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.gwtproject.user.client.ui.ListBox;

public class BarList extends ListBox {

	private App app;
	private Localization loc;
	private int barCount;
	private String allPartsKey;
	private String partKey;

	/**
	 * @param app
	 *            application
	 */
	public BarList(App app) {
		this.app = app;
		loc = app.getLocalization();
	}

	/**
	 * Update the translation keys for options (slice x bar).
	 * 
	 * @param geos
	 *            related construction elements
	 */
	public void updateTranslationKeys(Object[] geos) {
		this.allPartsKey = geos[0] instanceof GeoPieChart ? "AllSlices" : "AllBars";
		this.partKey = geos[0] instanceof GeoPieChart ? "SliceA" : "BarA";
	}

	/**
	 * Update visibility and content of this panel.
	 *
	 * @param enabled
	 *            whether this should be visible
	 */
	public void update(boolean enabled) {
		setVisible(enabled);
		if (!enabled) {
			return;
		}

		int idx = getSelectedIndex();
		clear();
		addItem(loc.getMenu(allPartsKey));
		for (int i = 1; i < getBarCount() + 1; i++) {
			addItem(app.getLocalization().getPlain(partKey, i + ""));
		}
		if (idx != -1) {
			setSelectedIndex(idx);
		}
	}

	public int getBarCount() {
		return barCount;
	}

	/**
	 * @param barCount
	 *            number of bars
	 */
	public void setBarCount(int barCount) {
		this.barCount = barCount;
		update(barCount > 0);
	}
}
