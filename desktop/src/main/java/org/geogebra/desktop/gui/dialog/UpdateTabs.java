package org.geogebra.desktop.gui.dialog;

public interface UpdateTabs {
	/**
	 *
	 * Update tabs of properties view
	 * after one or many geos are selected.
	 *
	 * @param geos to update with.
	 */
	void updateTabs(Object[] geos);
}
