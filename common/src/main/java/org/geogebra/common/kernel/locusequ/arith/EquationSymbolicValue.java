/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;


/**
 * @author sergio
 * Represents a symbolic value.
 */
public class EquationSymbolicValue extends EquationValue {

	private int id;
    
    /**
     * Creates a new symbolic value with given integer as an id.
     * @param id
     */
    public EquationSymbolicValue(int id) {
        this.id = id;
    }
    
    /**
     * 
     * @return the id.
     */
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isSymbolicValue() {
        return true;
    }

    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        return translator.symbolic(this.getId());
    }

    @Override
    public long toLong() {
        return 0;
    }

    @Override
    public String toString() {
        return "x"+this.getId();
    }

    @Override
	protected boolean containsSymbolicValuesImpl() {
        return true;
    }

    @Override
    protected double computeValueImpl() {
        return Double.NaN;
    }
}
