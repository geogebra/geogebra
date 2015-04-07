/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.locusequ.SymbolicVector;

/**
 * @author sergio
 * Equation Element for {@link AlgoJoinPointsSegment}
 */
public class EquationJoinPointsSegment extends EquationGenericSegment {

    private GeoPoint q;
    private EquationPoint qequ;
    
    /**
     * General constructor.
     * @param element {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationJoinPointsSegment(final GeoElement element, final EquationScope scope) {
        super(element, scope);
        
        AlgoJoinPointsSegment algo = (AlgoJoinPointsSegment) this.getResult().getParentAlgorithm();
        this.q = algo.getQ();
        this.qequ = this.getScope().getPoint(this.q);
        
        this.setPoint(algo.getP());
        this.setVector(new SymbolicVector(this.qequ, this.getEquationPoint()));
    }
}
