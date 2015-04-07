/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationList;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationRestriction;
import org.geogebra.common.kernel.locusequ.EquationScope;

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
