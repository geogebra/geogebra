package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoHyperbolaFociLength;
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
public class EquationHyperbolaFociLength extends EquationGenericConic {

	public EquationHyperbolaFociLength(GeoElement conic, EquationScope scope) {
		super(conic, scope);
		this.computeMatrix();
	}

	@Override
	protected void computeMatrix() {
		AlgoHyperbolaFociLength algo = (AlgoHyperbolaFociLength) this.getResult().getParentAlgorithm();
		
		EquationPoint focus1 = this.getScope().getPoint((GeoPoint) algo.getFocus1());
		EquationPoint focus2 = this.getScope().getPoint((GeoPoint) algo.getFocus2());
		EquationExpression length = EquationNumericValue.from(algo.getLength().getDouble());
		
		this.setEllipseHyperbola(focus1, focus2, length);
	}

}
