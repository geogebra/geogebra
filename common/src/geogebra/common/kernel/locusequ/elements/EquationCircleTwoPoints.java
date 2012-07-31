/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;


import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.diff;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.dist2;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sum;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.times;
import geogebra.common.kernel.algos.AlgoCircleTwoPoints;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;
/**
 * @author sergio
 * EquationElement for {@link AlgoCircleTwoPoints}
 */
public class EquationCircleTwoPoints extends EquationGenericConic {

    private EquationPoint externalEqu;

    /**
     * General constructor.
     * @param element {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationCircleTwoPoints(final GeoElement element, final EquationScope scope) {
        super(element, scope);
        this.computeMatrix();
    }

    @Override
    protected void computeMatrix() {
        EquationExpression[] matrix = new EquationExpression[6];
        
        AlgoCircleTwoPoints algo = (AlgoCircleTwoPoints) this.getResult().getParentAlgorithm();
        
        EquationPoint ext = this.getScope().getPoint((GeoPoint) algo.getExternalPoint());
        EquationPoint center = this.getScope().getPoint((GeoPoint) algo.getCenter());
        
        matrix[0] = EquationNumericValue.from(1);
        matrix[1] = EquationNumericValue.from(1);
        matrix[2] = diff(sum( times(center.getXExpression(), center.getXExpression()),
                              times(center.getYExpression(), center.getYExpression())),
                         dist2(ext,center));
        matrix[3] = EquationNumericValue.from(0);
        matrix[4] = center.getXExpression().getOpposite();
        matrix[5] = center.getYExpression().getOpposite();
        
        this.setMatrix(matrix);
    }
}
