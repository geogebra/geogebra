/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMidpointSegment;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Restriction for {@link AlgoMidpointSegment}.
 */
public class EquationMidpointSegmentRestriction extends
		EquationGenericMidpointRestriction {

	
	/**
	 * General constructor.
	 * @param geo point.
	 * @param algo {@link AlgoElement}
	 * @param scope {@link EquationScope}
	 */
	public EquationMidpointSegmentRestriction(final GeoElement geo, final AlgoMidpointSegment algo, final EquationScope scope) {
		super(geo, algo, scope);
        
        this.setEnds(algo.getP(), algo.getQ());
        this.setMidpoint(algo.getPoint());
	}
}
