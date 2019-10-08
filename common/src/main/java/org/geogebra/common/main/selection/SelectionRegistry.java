package org.geogebra.common.main.selection;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Registry for select/focus events.
 */
public interface SelectionRegistry {

	/**
	 * Registers the selection.
	 * @param selected The selected element to be saved.
	 */
	void register(Selectable selected);

	/**
	 * Registers the selection.
	 * @param selected The selected element to be saved.
	 */
	void register(GeoElement selected);

	/**
	 * @return The last selected element.
	 */
	Selectable getLastSelectedElement();
}
