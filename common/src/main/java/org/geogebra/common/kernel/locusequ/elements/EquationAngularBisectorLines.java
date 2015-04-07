/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoAngularBisectorLines;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * EquationElement for {@link AlgoAngularBisectorLines}
 */
public class EquationAngularBisectorLines extends
		EquationGenericAngularBisector {


    /**
     * General constructor
     * @param line {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationAngularBisectorLines(final GeoElement line, final EquationScope scope) {
        super(line, scope);

        AlgoAngularBisectorLines algo = (AlgoAngularBisectorLines) line.getParentAlgorithm();
        
        EquationGenericLine g, h;
        g = (EquationGenericLine) this.getScope().getElement(algo.getg());
        h = (EquationGenericLine) this.getScope().getElement(algo.geth());
        EquationPoint b = this.getScope().getPoint(algo.getB());
        
        this.setPoint(b);
        
        this.setVectors(g.getVector(), h.getVector());
        
        // It may be [0] instead.
        if(line == algo.getLines()[1]) {
            this.setVector(this.getVector().getNormal());
        }
    }
}
