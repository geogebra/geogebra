/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 *
 */
public class EquationGenericSegment extends EquationGenericRay {

    /**
     * General constructor
     * @param element {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationGenericSegment(final GeoElement element, final EquationScope scope) {
        super(element, scope);
    }
    
    @Override
    public boolean isAlgebraic() {
        return false;
    }
}
