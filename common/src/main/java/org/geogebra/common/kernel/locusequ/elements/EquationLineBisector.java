/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoLineBisector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 *
 */
public class EquationLineBisector extends EquationGenericBisector {

    /**
     * @param line {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationLineBisector(final GeoElement line, final EquationScope scope) {
        super(line, scope);

        AlgoLineBisector algo = (AlgoLineBisector) line.getParentAlgorithm();
        
        this.setExtremePoints(algo.getA(), algo.getB());
    }
    
    /**
     * Constructor for when a geo is not involved.
     * @param a first extreme.
     * @param b second extreme.
     * @param scope {@link EquationScope}
     */
    public EquationLineBisector(final EquationPoint a, final EquationPoint b,  final EquationScope scope) {
        super();
        
        this.setScope(scope);
        
        this.setExtremePoints(a, b);
    }
}
