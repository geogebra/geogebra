/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoIntersectConics;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationList;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Restriction for {@link AlgoIntersectConics}
 */
public class EquationIntersectConicsRestriction extends
		EquationIntersectRestriction {

	private EquationElement firstConic;
	private EquationElement secondConic;
	
	/**
	 * General constructor.
	 * @param geo point
	 * @param algo {@link AlgoIntersectConics}
	 * @param scope {@link EquationScope}
	 */
	public EquationIntersectConicsRestriction(final GeoElement geo, final AlgoIntersectConics algo, final EquationScope scope) {
		super(geo, algo, scope);
	}
	
	@Override
	public AlgoIntersectConics getAlgo() { return (AlgoIntersectConics) super.getAlgo(); }
	
	protected EquationElement getFirstConic() {
		if(this.firstConic == null) {
			this.firstConic = this.getScope().getElement(this.getAlgo().getA());
		}
		
		return this.firstConic;
	}
	
	protected EquationElement getSecondConic() {
		if(this.secondConic == null) {
			this. secondConic = this.getScope().getElement(this.getAlgo().getB());
		}
		return secondConic;
	}

	@Override
	protected EquationList forPointImpl(EquationPoint p) {
		EquationList list = new EquationList(2);
        
        list.addAll(this.getFirstConic().forPoint(p));
        list.addAll(this.getSecondConic().forPoint(p));
        
        return list;
	}
}
