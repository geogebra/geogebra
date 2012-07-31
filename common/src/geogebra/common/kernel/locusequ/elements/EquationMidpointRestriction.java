/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoMidpoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationScope;

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
