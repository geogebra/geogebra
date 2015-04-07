package org.geogebra.common.kernel.locusequ.elements;

import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.equation;
import static org.geogebra.common.kernel.locusequ.arith.EquationArithHelper.middle;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.EquationList;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationRestriction;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Base class for all midpoint restrictions.
 * Subclasses need to set ends and midpoint in initialization.
 */
public abstract class EquationGenericMidpointRestriction extends EquationRestriction {

	private GeoPoint firstEnd, secondEnd;
	private GeoPoint midpoint;
	
	/**
	 * General constructor
	 * @param geo point.
	 * @param algo {@link AlgoElement}
	 * @param scope {@link EquationScope}
	 */
	public EquationGenericMidpointRestriction(final GeoElement geo, final AlgoElement algo, final EquationScope scope) {
		super(geo, algo, scope);
	}
	
	/**
	 * Set ends for midpoint.
	 * @param first end.
	 * @param second end.
	 */
	protected void setEnds(final GeoPoint first, final GeoPoint second) {
		this.firstEnd = first;
		this.secondEnd = second;
	}
	
	/**
	 * Sets output for this algo.
	 * @param midpoint the midpoint.
	 */
	protected void setMidpoint(final GeoPoint midpoint) {
		this.midpoint = midpoint;
	}
	
	@Override
	protected EquationList forPointImpl(EquationPoint p) {
        
        EquationPoint pEqu = this.getScope().getPoint(getFirstEnd());     // end
        EquationPoint qEqu = this.getScope().getPoint(getSecondEnd());     // end
        EquationPoint mEqu = this.getScope().getPoint(getMidpoint()); // middle point
        EquationList res = new EquationList(2);
        
        res.add(equation(middle(pEqu.getXExpression(), qEqu.getXExpression(), mEqu.getXExpression())));
        res.add(equation(middle(pEqu.getYExpression(), qEqu.getYExpression(), mEqu.getYExpression())));
        
        return res;
	}

	@Override
	public boolean isAlgebraic() {
		return true;
	}

	/**
	 * @return one of the ends for the midpoint.
	 */
	protected GeoPoint getFirstEnd() {
		return firstEnd;
	}

	/**
	 * @return other end for the midpoint.
	 */
	protected GeoPoint getSecondEnd() {
		return secondEnd;
	}

	/**
	 * @return the midpoint
	 */
	protected GeoPoint getMidpoint() {
		return midpoint;
	}
}
