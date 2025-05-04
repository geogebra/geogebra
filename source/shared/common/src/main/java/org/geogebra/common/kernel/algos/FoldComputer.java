package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Sum command helper
 * 
 * @author Zbynek
 *
 */
public interface FoldComputer {

	/**
	 * Create element of the resulting type.
	 * @param cons construction
	 * @param listElement element class of the folded list
	 * @return template element
	 */
	GeoElement getTemplate(Construction cons, GeoClass listElement);

	/**
	 * Apply operation to current state and given element.
	 * @param geoElement element
	 * @param op operation
	 */
	void add(GeoElement geoElement, Operation op);

	/**
	 * @param geoElement element
	 * @param kernel kernel
	 */
	void setFrom(GeoElement geoElement, Kernel kernel);

	/**
	 * @param geoElement construction element
	 * @return whether it can be used as argument
	 */
	boolean check(GeoElement geoElement);

	/**
	 * Update result after last computation.
	 */
	void finish();

}