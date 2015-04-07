/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents a coordinate from a point.
 */
public abstract class EquationCoordinateValue extends EquationValue {

	private EquationPoint point;

	/**
	 * Creates an object for given point.
	 * @param point
	 */
    public EquationCoordinateValue(EquationPoint point) {
        this.point = point;
    }
    
    protected abstract EquationExpression getOriginalExpression();
    
    /**
     * Returns the underlying {@link EquationPoint}.
     * @return an {@link EquationPoint}
     */
    protected EquationPoint getPoint() {
        return this.point;
    }

    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        return this.getOriginalExpression().translate(translator);
    }

    @Override
    public long toLong() {
        return this.getOriginalExpression().toLong();
    }

    @Override
    public String toString() {
        return this.getOriginalExpression().toString();
    }

    @Override
    public boolean isNumericValue() {
        return this.getOriginalExpression().isNumericValue();
    }

    @Override
    public boolean isSymbolicValue() {
        return this.getOriginalExpression().isSymbolicValue();
    }

    @Override
    public boolean isSpecialSymbolicValue() {
        return this.getOriginalExpression().isSpecialSymbolicValue();
    }

    @Override
	protected boolean containsSymbolicValuesImpl() {
        return this.getOriginalExpression().containsSymbolicValuesImpl();
    }

    @Override
    protected double computeValueImpl() {
        return this.getOriginalExpression().computeValueImpl();
    }
    
    
    /**
     * Returns the value for the coordinate.
     * @return the coordinate as an {@link EquationExpression}
     */
    public EquationExpression getValue() {
        return this.getOriginalExpression();
    }
}
