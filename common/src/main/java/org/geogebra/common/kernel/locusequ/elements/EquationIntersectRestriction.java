/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoIntersect;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.EquationList;
import org.geogebra.common.kernel.locusequ.EquationRestriction;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Base class for intersect restrictions.
 */
public abstract class EquationIntersectRestriction extends EquationRestriction {

	
	/**
	 * General constructor.
	 * @param geo point.
	 * @param algo {@link AlgoIntersect}
	 * @param scope {@link EquationScope}
	 */
	public EquationIntersectRestriction(final GeoElement geo, final AlgoIntersect algo, final EquationScope scope) {
		super(geo, algo, scope);
	}
	
	@Override
	public AlgoIntersect getAlgo() { return (AlgoIntersect) super.getAlgo(); }
	
	@Override
	protected void computeEquationList() {
		GeoPoint[] points = this.getAlgo().getCopyOfIntersectionPoints();
        
        EquationList list = new EquationList(points.length * 2);
        
        for(GeoPoint p: points) {
            if(p.isDefined()){
                list.addAll(this.forPoint(p, this.getScope()));
            }
        }
        
        this.setEquationList(list);
	}
	
	@Override
	public boolean isAlgebraic() { return true; }
}
