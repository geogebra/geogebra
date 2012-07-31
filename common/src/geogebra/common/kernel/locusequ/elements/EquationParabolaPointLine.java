/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.diff;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sqr;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sum;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.times;
import geogebra.common.kernel.algos.AlgoParabolaPointLine;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
/**
 * @author sergio
 * {@link EquationElement} for {@link AlgoParabolaPointLine}
 */
public class EquationParabolaPointLine extends EquationGenericConic {


    /**
     * General constructor
     * @param conic {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationParabolaPointLine(final GeoElement conic, final EquationScope scope) {
        super(conic, scope);
        this.computeMatrix();
    }

    @Override
    protected void computeMatrix() {
        EquationExpression[] matrix = new EquationExpression[6];

        AlgoParabolaPointLine algo = (AlgoParabolaPointLine) this.getResult().getParentAlgorithm();
        
        EquationGenericLine line = (EquationGenericLine) this.getScope().getElement(algo.getLine());
        EquationPoint f = this.getScope().getPoint(algo.getFocus());
        
        EquationExpression b2 = sqr(line.getB());
        EquationExpression a2 = sqr(line.getA());
        EquationExpression a2b2 = sum(a2, b2);
        
        matrix[0] = b2;
        matrix[1] = a2;
        matrix[2] = diff(times(	a2b2,
        						sum(sqr(f.getXExpression()),
        							sqr(f.getYExpression()))),
                         sqr(line.getC()));
        matrix[3] = times(line.getA(), line.getB()).getOpposite();
        matrix[4] = sum(times(a2b2,f.getX()),
        				times(line.getA(), line.getC())).getOpposite();
        matrix[5] = sum(times(a2b2, f.getY()),
        				times(line.getB(), line.getC())).getOpposite();
        
        this.setMatrix(matrix);
    }
}
