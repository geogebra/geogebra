package org.geogebra.common.main.selection;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * The instances of this interface can be saved in the SelectionRegistry and can be selected by the
 * SelectionManager.
 */
public interface Selectable {

	/**
	 * @return Whether the object can select/focus itself.
	 */
	boolean canSelectSelf();

	/**
	 * Selects/focuses itself.
	 */
	void selectSelf();

	/**
	 * @return If a GeoElement is wrapped then returns it, otherwise it returns null.
	 */
	@Nullable
	GeoElement getGeoElement();
}
