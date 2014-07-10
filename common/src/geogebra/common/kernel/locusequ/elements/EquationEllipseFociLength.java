/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoEllipseFociLength;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;

/**
 * @author sergio
 *
 */
public class EquationEllipseFociLength extends EquationGenericConic {

	public EquationEllipseFociLength(final GeoElement conic, final EquationScope scope) {
		super(conic, scope);
		this.computeMatrix();
	}

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.locusequ.elements.EquationGenericConic#computeMatrix()
	 */
	@Override
	protected void computeMatrix() {
		AlgoEllipseFociLength algo = (AlgoEllipseFociLength) this.getResult().getParentAlgorithm();
		
		EquationPoint focus1 = this.getScope().getPoint((GeoPoint) algo.getFocus1());
		EquationPoint focus2 = this.getScope().getPoint((GeoPoint) algo.getFocus2());
		EquationExpression length = EquationNumericValue.from(algo.getLength().getDouble());
		
		this.setEllipseHyperbola(focus1, focus2, length);
	}
}
