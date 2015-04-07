/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoIntersectLines;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationList;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationRestriction;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Restriction for {@link AlgoIntersectLines
 */
public class EquationIntersectLinesRestriction extends
		EquationRestriction {
	
	/**
	 * General Constructor
	 * @param geo element.
	 * @param algo {@link AlgoIntersectLines}
	 * @param scope {@link EquationScope}
	 */
	public EquationIntersectLinesRestriction(final GeoElement geo, final AlgoIntersectLines algo, final EquationScope scope) {
		super(geo, algo, scope);
	}
	
	@Override
	public AlgoIntersectLines getAlgo() { return (AlgoIntersectLines) super.getAlgo(); }

	@Override
	protected EquationList forPointImpl(EquationPoint p) {
		EquationList list = new EquationList(2);
		
		list.addAll(this.getScope().getElement(this.getAlgo().getg()).forPoint(p));
		list.addAll(this.getScope().getElement(this.getAlgo().geth()).forPoint(p));
		
		return list;
	}

	@Override
	public boolean isAlgebraic() {
		return true;
	}
}
