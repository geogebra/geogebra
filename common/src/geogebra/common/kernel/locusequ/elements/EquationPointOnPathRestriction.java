/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.advanced.AlgoPointOnPath;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationList;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationRestriction;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Restriction for {@link AlgoPointOnPath}.
 */
public class EquationPointOnPathRestriction extends EquationRestriction {

	/**
	 * General constructor.
	 * @param geo point.
	 * @param algo {@link AlgoPointOnPath}
	 * @param scope {@link EquationScope}
	 */
	public EquationPointOnPathRestriction(final GeoElement geo, final AlgoElement algo, final EquationScope scope) {
		super(geo, algo, scope);
	}

	@Override
	protected EquationList forPointImpl(EquationPoint p) {
		AlgoPointOnPath algo = (AlgoPointOnPath) this.getAlgo();
        
        EquationList elist = new EquationList(1);
        elist.addAll(this.getScope().getElement(algo.getPath().toGeoElement())
                  .forPoint(p));
        
        return elist;
	}

	@Override
	public boolean isAlgebraic() {
		return true;
	}
}
