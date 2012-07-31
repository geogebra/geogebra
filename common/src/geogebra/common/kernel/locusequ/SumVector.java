/**
 * 
 */
package geogebra.common.kernel.locusequ;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sum;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;

/**
 * @author sergio
 * Vector coming from the sum of other two.
 */
public class SumVector extends SymbolicVector {


    private SymbolicVector vector1;
    private SymbolicVector vector2;

    /**
     * General Constructor
     * @param a first vector.
     * @param b second vector.
     */
    public SumVector(final SymbolicVector a, final SymbolicVector b) {
        this.vector1 = a;
        this.vector2 = b;
        this.xExpr = sum(this.vector1.getX(), this.vector2.getX());
        this.yExpr = sum(this.vector1.getY(), this.vector2.getY());
        this.zExpr = EquationNumericValue.from(1);
    }
    
    @Override
    public EquationExpression getXExpression() {
        return this.xExpr;
    }

    @Override
    public EquationExpression getYExpression() {
        return this.yExpr;
    }

    @Override
    public EquationExpression getZExpression() {
        return this.zExpr;
    }
}
