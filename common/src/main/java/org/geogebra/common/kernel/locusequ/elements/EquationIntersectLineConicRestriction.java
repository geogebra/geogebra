/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationElement;
import org.geogebra.common.kernel.locusequ.EquationList;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 *
 */
public class EquationIntersectLineConicRestriction extends
		EquationIntersectRestriction {

	private EquationElement line;
	private EquationElement conic;

	/**
	 * General constructor
	 * @param geo {@link GeoElement}
	 * @param algo {@link AlgoIntersectLineConic}
	 * @param scope {@link EquationScope}
	 */
	public EquationIntersectLineConicRestriction(final GeoElement geo, final AlgoIntersectLineConic algo, final EquationScope scope) {
		super(geo, algo, scope);
	}
	
	@Override
	public AlgoIntersectLineConic getAlgo() {
		return (AlgoIntersectLineConic) super.getAlgo();
	}
	
	public EquationElement getLine() {
		if(this.line == null) {
			this.line = this.getScope().getElement(this.getAlgo().getLine());
		}
		return this.line;
	}
	
	public EquationElement getConic() {
		if(this.conic == null) {
			this.conic = this.getScope().getElement(this.getAlgo().getConic());
		}
		return this.conic;
	}

	@Override
	protected EquationList forPointImpl(EquationPoint p) {
		EquationList list = new EquationList();
		
		list.addAll(this.getLine().forPoint(p));
		list.addAll(this.getConic().forPoint(p));
		
		return list;
	}
}
