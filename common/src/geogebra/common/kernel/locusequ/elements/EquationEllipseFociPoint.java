/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoEllipseFociPoint;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationMidpoint;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.SymbolicVector;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.*;

/**
 * @author sergio
 * EquationElement for {@link AlgoEllipseFociPoint}
 */
public class EquationEllipseFociPoint extends EquationGenericConic {


    private GeoConic ellipse;

    private GeoPoint f1, f2, ep; // Focus 1, Focus 2, External Point
    private EquationPoint ef1, ef2, eep;
    private EquationPoint center;
    private EquationExpression distance;
    private EquationExpression a2, b2;
    private SymbolicVector ev1, ev2;
    
    /**
     * General constructor.
     * @param element conic
     * @param scope {@link EquationScope}
     */
    public EquationEllipseFociPoint(final GeoElement element, final EquationScope scope) {
        super(element, scope);
        this.computeMatrix();
    }
    
    @Override
    protected void computeMatrix() {
        EquationExpression[] matrix = new EquationExpression[6];
        
        AlgoEllipseFociPoint algo = (AlgoEllipseFociPoint) this.getResult().getParentAlgorithm();
        
        f1 = algo.getFocus1();
        f2 = algo.getFocus2();
        ep = algo.getExternalPoint();
        
        ef1 = this.getScope().getPoint(f1);
        ef2 = this.getScope().getPoint(f2);
        eep = this.getScope().getPoint(ep); // ExternalPoint
        center = new EquationMidpoint(ef1, ef2); // CenterPoint
        
        ev1 = new SymbolicVector(center, ef1).getUnitary();
        ev2 = ev1.normal();
        
        EquationExpression d1 = dist(ef1, eep); // Distance from f1 to ep
        EquationExpression d2 = dist(ef2, eep); // Distance from f2 to ep
        EquationExpression dF = dist(ef1, ef2); // Focal distance
        distance = sum(d1, d2);
        a2 = pow(div(distance, EquationNumericValue.from(2)), EquationNumericValue.from(2));     // a^2
        b2 = diff(a2, pow(div(dF, EquationNumericValue.from(2)), EquationNumericValue.from(2))); // b^2
        
        matrix[0] = b2;
        matrix[1] = a2;
        matrix[2] = times(a2, b2,
                          diff(sum( times(center.getX(), center.getX()),
                                    times(center.getY(), center.getY())),
                               EquationNumericValue.from(1)));
        matrix[3] = EquationNumericValue.from(0);
        matrix[4] = times(center.getX(), b2).getOpposite();
        matrix[5] = times(center.getY(), a2).getOpposite();
        
        this.setMatrix(matrix);
    }
}
