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
        a2 = sqr(div(distance, EquationNumericValue.from(2)));     // a^2
        b2 = diff(a2, sqr(div(dF, EquationNumericValue.from(2)))); // b^2
        
        this.setEllipseHyperbola(ef1, ef2, half(distance));
    }

    // Translated from GeoConicND
	private void setEllipseHyperbola(EquationPoint B, EquationPoint C,
			EquationExpression a) {
		EquationExpression[] matrix = new EquationExpression[6];
		
		EquationExpression b1 = B.getX();
		EquationExpression b2 = B.getY();
		EquationExpression c1 = C.getX();
		EquationExpression c2 = C.getY();
		
		EquationExpression two = EquationNumericValue.from(2.0);
		EquationExpression four = EquationNumericValue.from(4.0);
		
		// precalculations
		EquationExpression diff1 = diff(b1,c1);
		EquationExpression diff2 = diff(b2,c2);
		EquationExpression sqsumb = sum(sqr(b1), sqr(b2));
		EquationExpression sqsumc = sum(sqr(c1), sqr(c2));
		EquationExpression sqsumdiff = diff(sqsumb, sqsumc);
		EquationExpression a2 = dbl(a);
		EquationExpression asq4 = sqr(a2);
		EquationExpression asq = sqr(a);
		EquationExpression afo = sqr(asq);
		
		matrix[0] = times(	four,
							diff(a2,diff1),
							sum(a2, diff1));
		
		matrix[3] = times(	four,
							diff1,
							diff2).getOpposite();
		
		matrix[1] = times(	four,
							diff(a2, diff2),
							sum(a2, diff2));
		
		matrix[4] = times(	two,
							diff(	times(	asq4,
											sum(b1,c1)),
									times(	diff1,
											sqsumdiff)
								)
							).getOpposite();
		matrix[5] = times(	two,
							diff(	times(	asq4,
											sum(b2,c2)),
									times(	diff2,
											sqsumdiff)
								)
							).getOpposite();
		
		matrix[2] = diff(	times(	EquationNumericValue.from(8.0),
									asq,
									sum(sqsumb, sqsumc)),
							sqr(sqsumdiff),
							times(	EquationNumericValue.from(16.0),
									afo)
						);
		
		this.setMatrix(matrix);
	}
}
