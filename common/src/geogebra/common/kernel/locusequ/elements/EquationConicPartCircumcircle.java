/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoConicPartCircumcircle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationAuxiliarSymbolicPoint;
import geogebra.common.kernel.locusequ.EquationList;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * EquationElement for {@link AlgoConicPartCircumcircle}
 */
public class EquationConicPartCircumcircle extends EquationGenericConicPart {
	
	/**
     * General Constructor
     * @param conic {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationConicPartCircumcircle(final GeoElement conic, final EquationScope scope) {
        super(conic, scope);
        this.computeMatrix();
    }

    /* (non-Javadoc)
     * @see geogebra.kernel.locusequ.EquationGenericConic#computeMatrix()
     */
    @Override
    protected void computeMatrix() {
        
        AlgoConicPartCircumcircle algo = (AlgoConicPartCircumcircle) this.getResult().getParentAlgorithm();

        EquationPoint A, B, C;
        
        A = this.getScope().getPoint(algo.getA());
        B = this.getScope().getPoint(algo.getB());
        C = this.getScope().getPoint(algo.getC());
        
        this.setMatrix(matrixForCircle(A, B, C));
    }

    @Override
    protected EquationList forPointImpl(EquationPoint p) {
        EquationList el = super.forPointImpl(p);
        
        if(this.isSector()) {
            
            AlgoConicPartCircumcircle algo = (AlgoConicPartCircumcircle) this.getResult().getParentAlgorithm();
            
            EquationPoint A, B, C;
            
            A = this.getScope().getPoint(algo.getA());
            B = this.getScope().getPoint(algo.getB());
            C = this.getScope().getPoint(algo.getC());
            
            EquationGenericLine l = new EquationLineBisector(A, B, this.getScope());
            EquationGenericLine m = new EquationLineBisector(A, C, this.getScope());
            
            EquationAuxiliarSymbolicPoint center = l.getNewIncidentPoint();
            center.addIncidence(m);

            el.addAll(new EquationJoinPoints(center, A, this.getScope()).forPoint(p));
            el.addAll(new EquationJoinPoints(center, C, this.getScope()).forPoint(p));
            

            
            // Join all expressions.
            el = orAllExpressions(el);
        }
        
        return el;
    }
}
