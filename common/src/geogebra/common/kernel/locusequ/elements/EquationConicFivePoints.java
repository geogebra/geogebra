/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.det5;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.times;
import geogebra.common.kernel.algos.AlgoConicFivePoints;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;

/**
 * @author sergio
 * EquationElement for {@link AlgoConicFivePoints}
 */
public class EquationConicFivePoints extends EquationGenericConic {
	
	/**
	 * General constructor
	 * @param element conic
	 * @param scope {@link EquationScope}
	 */
	public EquationConicFivePoints(final GeoElement element, final EquationScope scope) {
        super(element, scope);
        this.computeMatrix();
    }

    @Override
    protected void computeMatrix() {
        EquationExpression[] matrix = new EquationExpression[6];

        AlgoConicFivePoints algo = (AlgoConicFivePoints) this.getResult().getParentAlgorithm();
        
        EquationPoint[] p = new EquationPoint[5];
        EquationExpression[] x = new EquationExpression[5];
        EquationExpression[] y = new EquationExpression[5];
        EquationExpression[] x2 = new EquationExpression[5];
        EquationExpression[] y2 = new EquationExpression[5];
        EquationExpression[] xy = new EquationExpression[5];
        
        for(int i = 0; i < 5; i++) {
            p[i]  = this.getScope().getPoint(algo.getAllPoints()[i]);
            x[i]  = p[i].getXExpression();
            y[i]  = p[i].getYExpression();
            x2[i] = times(x[i], x[i]);
            y2[i] = times(y[i], y[i]);
            xy[i] = times(x[i], y[i]);
        }

        matrix[0] = det5(xy[0], y2[0], x[0], y[0], EquationNumericValue.from(1),
                         xy[1], y2[1], x[1], y[1], EquationNumericValue.from(1),
                         xy[2], y2[2], x[2], y[2], EquationNumericValue.from(1),
                         xy[3], y2[3], x[3], y[3], EquationNumericValue.from(1),
                         xy[4], y2[4], x[4], y[4], EquationNumericValue.from(1));
        
        matrix[1] = det5(x2[0], xy[0], x[0], y[0], EquationNumericValue.from(1),
                         x2[1], xy[1], x[1], y[1], EquationNumericValue.from(1),
                         x2[2], xy[2], x[2], y[2], EquationNumericValue.from(1),
                         x2[3], xy[3], x[3], y[3], EquationNumericValue.from(1),
                         x2[4], xy[4], x[4], y[4], EquationNumericValue.from(1));
        
        matrix[2] = det5(x2[0], xy[0], y2[0], x[0], y[0],
                         x2[1], xy[1], y2[1], x[1], y[1],
                         x2[2], xy[2], y2[2], x[2], y[2],
                         x2[3], xy[3], y2[3], x[3], y[3],
                         x2[4], xy[4], y2[4], x[4], y[4]).getOpposite();
        
        matrix[3] = det5(x2[0], y2[0], x[0], y[0], EquationNumericValue.from(1),
                         x2[1], y2[1], x[1], y[1], EquationNumericValue.from(1),
                         x2[2], y2[2], x[2], y[2], EquationNumericValue.from(1),
                         x2[3], y2[3], x[3], y[3], EquationNumericValue.from(1),
                         x2[4], y2[4], x[4], y[4], EquationNumericValue.from(1)).getOpposite();
        
        matrix[4] = det5(x2[0], xy[0], y2[0], y[0], EquationNumericValue.from(1),
                         x2[1], xy[1], y2[1], y[1], EquationNumericValue.from(1),
                         x2[2], xy[2], y2[2], y[2], EquationNumericValue.from(1),
                         x2[3], xy[3], y2[3], y[3], EquationNumericValue.from(1),
                         x2[4], xy[4], y2[4], y[4], EquationNumericValue.from(1)).getOpposite();
        
        matrix[5] = det5(x2[0], xy[0], y2[0], x[0], EquationNumericValue.from(1),
                         x2[1], xy[1], y2[1], x[1], EquationNumericValue.from(1),
                         x2[2], xy[2], y2[2], x[2], EquationNumericValue.from(1),
                         x2[3], xy[3], y2[3], x[3], EquationNumericValue.from(1),
                         x2[4], xy[4], y2[4], x[4], EquationNumericValue.from(1));
        
        this.setMatrix(matrix);
    }
}
