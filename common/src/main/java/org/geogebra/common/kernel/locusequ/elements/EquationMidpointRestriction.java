/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Restriction coming from an {@link AlgoMidpoint}.
 */
public class EquationMidpointRestriction extends EquationGenericMidpointRestriction {

	/**
	 * General constructor.
	 * @param geo a point.
	 * @param algo an {@link AlgoElement}
	 * @param scope an {@link EquationScope}
	 */
	public EquationMidpointRestriction(final GeoElement geo, final AlgoMidpoint algo, final EquationScope scope) {
		super(geo, algo, scope);
        this.setEnds(algo.getP(), algo.getQ());
        this.setMidpoint(algo.getPoint());
	}
}
