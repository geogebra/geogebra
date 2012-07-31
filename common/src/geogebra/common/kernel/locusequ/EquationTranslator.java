/**
 * 
 */
package geogebra.common.kernel.locusequ;

import geogebra.common.kernel.locusequ.arith.EquationExpression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sergio
 * Translates an abstract tree to a proper representation for
 * the CAS.
 * 
 * Probably, letting the translator both translate and eliminate
 * violates the single responsability principle.
 */
public abstract class EquationTranslator<T> {

    private Map<EquationExpression, T> container;
    private EquationSystem system;
    
    protected EquationTranslator() {
        this.container = new HashMap<EquationExpression, T>();
    }
    
    protected void setSystem(EquationSystem system) {
        this.system = system;
    }
    
    public abstract Collection<T> translate(EquationSystem system);
    
    protected Map<EquationExpression,T> getContainer() {
        return this.container;
    }
    
    protected void setNewContainer(Map<EquationExpression, T> newContainer) {
        this.container = newContainer;
    }
    
    protected EquationSystem getSystem() {
        return this.system;
    }
    
    public boolean containsKey(EquationExpression equationExpression) {
        return this.getContainer().containsKey(equationExpression);
    }

    public T get(EquationExpression equationExpression) {
        return this.getContainer().get(equationExpression);
    }
    public abstract EquationList getLocus();
    
    public abstract T abs(T value);
    public abstract T sum(T a, T b);
    public abstract T diff(T a, T b);
    public abstract T product(T a, T b);
    public abstract T div(T num, T denom);
    public abstract T exp(T base, long exp);
    public abstract T inverse(T value);
    public abstract T number(double number);
    public abstract T auxiliarSymbolic(int id);
    public abstract T specialSymbolic(int id);
    public abstract T symbolic(int id);
    public abstract T opposite(T value);
    public abstract T sqrt(T value);
    
    public double[][] eliminateSystem(EquationSystem system) {
    	return this.eliminate(this.translate(system));
    }

    public abstract double[][] eliminate(Collection<T> translatedRestrictions);
}
