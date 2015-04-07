/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.locusequ.SymbolicVector;

/**
 * @author sergio
 * Generic base class for angular bisectors.
 */
public abstract class EquationGenericAngularBisector extends
		EquationGenericLine {

	/**
	 * Empty constructor in case a subclass needs it. 
	 */
    protected EquationGenericAngularBisector() {}
    
    /**
     * General constructor
     * @param el {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationGenericAngularBisector(final GeoElement el, final EquationScope scope) {
        super(el, scope);
    }
    
    /**
     * Set vectors for calculating the bisector.
     * @param a first vector.
     * @param b second vector.
     */
    protected void setVectors(final SymbolicVector a, final SymbolicVector b) {
        this.setVector(a.getBisector(b));
    }
}
