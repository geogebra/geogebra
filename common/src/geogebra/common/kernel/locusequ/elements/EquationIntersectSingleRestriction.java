/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoIntersectSingle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationList;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationRestriction;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * {@link AlgoIntersectSingle} selects a single intersection point and forgets about the rest.
 * I do not care, I'm still generating all equations.
 * It may be ignored if I know for sure that actual algorithm is always called.
 */
public class EquationIntersectSingleRestriction extends
		EquationIntersectRestriction {

	private EquationRestriction internalRestriction;

	/**
	 * General constructor
	 * @param geo {@link GeoElement}
	 * @param algo {@link AlgoIntersectSingle}
	 * @param scope {@link EquationScope}
	 */
	public EquationIntersectSingleRestriction(final GeoElement geo, final AlgoIntersectSingle algo, final EquationScope scope) {
		super(geo, algo, scope);
		this.internalRestriction = (EquationRestriction) algo.getAlgo().buildEquationElementForGeo(geo, scope);
	}
	
	@Override
    protected void computeEquationList() {
		AlgoIntersectSingle algo = (AlgoIntersectSingle) this.getAlgo();
		EquationPoint actualPoint = this.getScope().getPoint(algo.getPoint());
		this.setEquationList(this.internalRestriction.forPoint(actualPoint));
    }

	@Override
	protected EquationList forPointImpl(EquationPoint p) {
		// Do nothing, since this class uses forPointImpl
		// of internal restriction.
		return null;
	}
}
