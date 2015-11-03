/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import java.util.HashSet;

import org.geogebra.common.kernel.algos.AlgoIntersect;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.EquationList;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationRestriction;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.locusequ.arith.Equation;
import org.geogebra.common.kernel.locusequ.arith.EquationArithHelper;
import org.geogebra.common.kernel.locusequ.arith.EquationExpression;
import org.geogebra.common.kernel.locusequ.arith.EquationNumericValue;
import org.geogebra.common.kernel.locusequ.arith.EquationSymbolicValue;

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
        
        // GGB-254
		EquationList el = new EquationList();
		HashSet<HashSet<GeoPoint>> tuples = new HashSet<HashSet<GeoPoint>>();
        for (GeoPoint p: points) {
			if (p.isDefined()) {
        		for (GeoPoint q: points) {
					HashSet<GeoPoint> tuple = new HashSet<GeoPoint>();
					tuple.add(p);
					tuple.add(q);
					if (q.isDefined() && !p.equals(q)
							&& !tuples.contains(tuple)) {
        				final EquationPoint pequ = this.getScope().getPoint(p);
        				final EquationPoint qequ = this.getScope().getPoint(q);
						final EquationExpression dist2 = EquationArithHelper
								.dist2(pequ, qequ);
						// TODO: maybe it is better to not use xi here but a
						// different variable
						int curInd = getScope().getPointMap().getCurInd();
						getScope().getPointMap().increaseCurInd();
						final EquationExpression z = new EquationSymbolicValue(
								curInd);
						EquationExpression ee = EquationArithHelper.sum(
								EquationArithHelper.times(dist2, z),
								EquationNumericValue.from(1));
						el.add(new Equation(ee));
						tuples.add(tuple);
					}
        		}
        	}
        }
		list.addAll(el);
        
        this.setEquationList(list);
	}
	
	@Override
	public boolean isAlgebraic() { return true; }
}
