package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for objects that can contain GeoElement and therefore need to
 * replace it by value sometimes.
 * 
 * @author Zbynek
 *
 */
public interface ReplaceChildrenByValues {
	/**
	 * Replaces geo and all its dependent geos in this tree by copies of their
	 * values.
	 * 
	 * @param geo
	 *            geo to be replaced
	 */
	public void replaceChildrenByValues(GeoElement geo);

}
