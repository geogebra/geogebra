/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents a special symbolic value.
 */
public class EquationSpecialSymbolicValue extends EquationSymbolicValue {
    
    /**
     * Constructor.
     * @param id for this point.
     */
    public EquationSpecialSymbolicValue(final int id) {
        super(id);
    }

    @Override
    public boolean isSpecialSymbolicValue() {
        return true;
    }

    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        return translator.specialSymbolic(this.getId());
    }

    @Override
    public long toLong() {
        return 0;
    }

    @Override
    public String toString() {
        return (this.getId() == 1) ? "x" : "y";
    }
}
