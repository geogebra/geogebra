/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoLineBisectorSegment;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * EquationElement for {@link AlgoLineBisectorSegment}
 */
public class EquationLineBisectorSegment extends EquationGenericBisector {

    /**
     * @param line {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationLineBisectorSegment(final GeoElement line, final EquationScope scope) {
        super(line, scope);

        AlgoLineBisectorSegment algo = (AlgoLineBisectorSegment) line.getParentAlgorithm();
        
        GeoSegment segment = algo.getSegment();

        this.setExtremePoints(segment.getStartPoint(), segment.getEndPoint());
    }
    
    @Override
    public boolean isAlgebraic() { return false; }
}
