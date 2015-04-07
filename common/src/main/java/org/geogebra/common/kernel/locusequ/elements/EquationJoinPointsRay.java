/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoJoinPointsRay;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.locusequ.SymbolicVector;

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
