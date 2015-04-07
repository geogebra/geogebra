/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * EquationElement for AlgoCircleThreePoints.
 */
public class EquationCircleThreePoints extends EquationGenericConic {

	protected GeoPoint p,q,r;
	protected EquationPoint pequ, qequ, requ;
    
    /**
     * General constructor.
     * @param circle {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationCircleThreePoints(final GeoElement circle, final EquationScope scope) {
        super(circle, scope);
        this.setScope(scope);
        this.setResult(circle);
        AlgoCircleThreePoints algo = (AlgoCircleThreePoints) circle.getParentAlgorithm();
        this.p = algo.getA();
        this.q = algo.getB();
        this.r = algo.getC();
        this.pequ = scope.getPoint(p);
        this.qequ = scope.getPoint(q);
        this.requ = scope.getPoint(r);
        this.computeMatrix();
    }

    @Override
    public boolean isAlgebraic() {
        return true;
    }

    @Override
    protected void computeMatrix() {
        
        this.setMatrix(matrixForCircle(this.pequ, this.qequ, this.requ));
    }
}
