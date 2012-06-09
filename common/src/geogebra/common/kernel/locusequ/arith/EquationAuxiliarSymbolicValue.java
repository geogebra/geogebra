/**
 * 
 */
package geogebra.common.kernel.locusequ.arith;

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

    /*
     * FIXME: in case translator is kept.
    @Override
    protected <T> T translateImpl(EquationTranslator<T> translator) {
        return translator.auxiliarSymbolic(this.getId());
    }
     */

    @Override
    public long toLong() {
        return 0;
    }

    @Override
    public String toString() {
        return "u"+this.getId();
    }
}
