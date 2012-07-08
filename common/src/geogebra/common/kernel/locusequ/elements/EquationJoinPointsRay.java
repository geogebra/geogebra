/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoJoinPointsRay;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.SymbolicVector;

/**
 * @author sergio
 * EquationElement for {@link AlgoJoinPointsRay}
 */
public class EquationJoinPointsRay extends EquationGenericRay {

	/**
	 * General constructor.
	 * @param element {@link GeoElement}
	 * @param scope {@link EquationScope}
	 */
	public EquationJoinPointsRay(final GeoElement element, final EquationScope scope) {
        super(element, scope);

        AlgoJoinPointsRay algo = (AlgoJoinPointsRay) element.getParentAlgorithm();
        
        this.setPoint(algo.getP());
        this.setVector(new SymbolicVector(this.getEquationPoint(), this.getScope().getPoint(algo.getQ())));
    }
}
