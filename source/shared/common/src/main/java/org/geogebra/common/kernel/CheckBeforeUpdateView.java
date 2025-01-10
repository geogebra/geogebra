package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * View that checks if it needs to update visual style for a specific property,
 * and if it will show a specific geo
 */
public interface CheckBeforeUpdateView extends View {

	/**
	 * 
	 * @param property
	 *            visual style property
	 * @return true if changes for this property needs update in AV
	 */
	boolean needsUpdateVisualstyle(GProperty property);

	/**
	 * 
	 * @param geo
	 *            geo
	 * @return true if geo is shown in view
	 */
	boolean show(GeoElement geo);

}
