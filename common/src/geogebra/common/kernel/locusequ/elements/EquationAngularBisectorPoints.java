/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoAngularBisectorPoints;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.SymbolicVector;

/**
 * @author sergio
 * EquationElement for {@link AlgoAngularBisectorPoints}
 */
public class EquationAngularBisectorPoints extends
		EquationGenericAngularBisector {

    /**
     * General constructor.
     * @param line {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationAngularBisectorPoints(final GeoElement line, final EquationScope scope) {
        super(line, scope);

        AlgoAngularBisectorPoints algo = (AlgoAngularBisectorPoints) line.getParentAlgorithm();
        
        EquationPoint a, b, c;
        a = this.getScope().getPoint(algo.getA());
        b = this.getScope().getPoint(algo.getB());
        c = this.getScope().getPoint(algo.getC());
        
        this.setPoint(algo.getB());
        
        this.setVectors(new SymbolicVector(b,a), new SymbolicVector(b,c));
    }
}
