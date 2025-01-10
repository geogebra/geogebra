package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Interface for macro algorithm (to separate Geos from Algos)
 *
 */
public interface AlgoMacroInterface {
	/**
	 * Replace references to macro geos in function
	 * 
	 * @param f
	 *            function
	 */
	public void initFunction(FunctionNVar f);

	/**
	 * Replace references to macro geos in list
	 * 
	 * @param l
	 *            macro list
	 * @param geoList
	 *            parent construction list
	 */
	public void initList(GeoList l, GeoList geoList);

	/**
	 * Compares drawing priority of two elements
	 * 
	 * @param geoElement
	 *            first element
	 * @param other
	 *            second element
	 * @return whether geoElement should be drawn before other
	 */
	public int drawBefore(GeoElement geoElement, GeoElement other);
}
