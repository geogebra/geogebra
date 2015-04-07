/**
 * 
 */
package org.geogebra.common.kernel.locusequ.arith;

import org.geogebra.common.kernel.locusequ.EquationTranslator;

/**
 * @author sergio
 * Represents an auxiliary symbolic value. This kind of value is useful for
 * overcoming limitations in some CAS systems like JAS.
 */
public class EquationAuxiliarSymbolicValue extends EquationSymbolicValue {


    /**
     * General constructor.
     * @param id for the point.
     */
    public EquationAuxiliarSymbolicValue(int id) {
        super(id);
    }

    @Override
    public boolean isAuxiliarSymbolicValue() {
        return true;
    }

    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        return translator.auxiliarSymbolic(this.getId());
    }

    @Override
    public long toLong() {
        return 0;
    }

    @Override
    public String toString() {
        return "u"+this.getId();
    }
}
