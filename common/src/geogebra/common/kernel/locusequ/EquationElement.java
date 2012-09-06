package geogebra.common.kernel.locusequ;

import geogebra.common.kernel.algos.EquationElementInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * Represents an equation.
 * @author sergio
 *
 */
public abstract class EquationElement implements EquationElementInterface {

	private GeoElement result;
	private EquationScope scope;

	/**
	 * Empty constructor for subclasses.
	 */
	protected EquationElement() {}

    /**
     * @param element {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationElement(final GeoElement element, final EquationScope scope) {
        this.setResult(element);
        this.setScope(scope);
    }
    
    /**
     * Retrieves a new symbolic point incident on this element.
     * @return a new point.
     */
    public EquationAuxiliarSymbolicPoint getNewIncidentPoint() {
        EquationAuxiliarSymbolicPoint np = this.getScope().getNewAuxiliarPoint();
        
        np.addIncidence(this);
        
        return np;
    }
    
    /**
     * @return current scope.
     */
    public EquationScope getScope() {
        return this.scope;
    }
    
    
    /**
     * Sets current scope to a new one.
     * @param scope new scope.
     */
    protected void setScope(final EquationScope scope) {
        this.scope = scope;
    }
    
    /**
     * @param p A {@link GeoPoint} in the element.
     * @param scope2 The {@link EquationPointMap} where the {@link EquationPoint} for p is.
     * @return A {@link EquationList} containing the restriction for point.
     */
    public EquationList forPoint(GeoPoint p, EquationScope scope2) {
        return this.forPoint(scope2.getPoint(p));
    }
    
    /**
     * Given a point p, this method retrieves all restriction of this element applied to it.
     * @param p the point.
     * @return the equations.
     */
    public EquationList forPoint(final EquationPoint p) {
        EquationList equ = forPointImpl(p);
        equ.setAlgebraic(this.isAlgebraic());
        return equ;
    }
    
    /**
     * @param p The {@link EquationPoint} to be in the element.
     * @return A String containing the equation representing that p is in the element.
     */
    protected abstract EquationList forPointImpl(EquationPoint p);

    /**
     * @return true if the construction is algebraic.
     */
    public abstract boolean isAlgebraic();
    
    /**
     * @return the GeoElement whose EquationElement is <b>this</b>
     */
    public GeoElement getResult() {
        return this.result;
    }
    
    /**
     * Sets the {@link GeoElement} represented by this {@link EquationElement}.
     * @param result to set in this element.
     */
    protected void setResult(GeoElement result){
        this.result = result;
    }
    
    /**
     * @return true iff this object is a restriction.
     */
    public boolean isRestriction() { return false; }
}
