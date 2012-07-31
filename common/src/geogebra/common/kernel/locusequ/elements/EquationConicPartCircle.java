/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.dist2;
import geogebra.common.kernel.algos.AlgoConicPartCircle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationList;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
/**
 * @author sergio
 *
 */
public class EquationConicPartCircle extends EquationGenericConicPart {


    /**
     * General Constructor
     * @param conic {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationConicPartCircle(final GeoElement conic, final EquationScope scope) {
        super(conic, scope);
        this.computeMatrix();
    }

    @Override
    protected void computeMatrix() {

        AlgoConicPartCircle algo = (AlgoConicPartCircle) this.getResult().getParentAlgorithm();
        
        EquationPoint center = this.getScope().getPoint(algo.getCenter());
        EquationPoint startPoint = this.getScope().getPoint(algo.getStartPoint());
        
        this.setMatrix(this.matrixForCircle(center, dist2(center, startPoint)));
    }

    @Override
    protected EquationList forPointImpl(EquationPoint p) {
        EquationList el = super.forPointImpl(p);
        
        if(this.isSector()) {

            AlgoConicPartCircle algo = (AlgoConicPartCircle) this.getResult().getParentAlgorithm();
            
            EquationPoint center = this.getScope().getPoint(algo.getCenter());
            EquationPoint startPoint = this.getScope().getPoint(algo.getStartPoint());
            EquationPoint endPoint = this.getScope().getPoint(algo.getEndPoint());
            
            el.addAll(new EquationJoinPoints(center, startPoint, this.getScope()).forPoint(p));
            el.addAll(new EquationJoinPoints(center, endPoint, this.getScope()).forPoint(p));
            
            // Join all expressions.
            
            el = orAllExpressions(el);
        }
        
        return el;
    }
}
