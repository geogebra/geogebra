/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * Base class for Rays.
 */
public abstract class EquationGenericRay extends EquationGenericLine {

	/**
	 * General constructor.
	 * @param line {@link GeoElement}
	 * @param scope {@lin EquationScope}
	 */
	public EquationGenericRay(final GeoElement line, final EquationScope scope) {
        super(line, scope);
    }

    @Override
    public boolean isAlgebraic() {
        return false;
    }
}
