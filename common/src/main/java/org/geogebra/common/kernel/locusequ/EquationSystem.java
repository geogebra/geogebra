/**
 * 
 */
package org.geogebra.common.kernel.locusequ;

import java.util.Collection;

/**
 * @author sergio
 * Represents a system with its equation list and
 * its scope.
 */
public class EquationSystem {

	private EquationList el;

    private String[] vars;

    private EquationScope scope;
    
    public EquationSystem(EquationList list, EquationScope scope) {
        this.el = list;
        this.scope = scope;
    }
    
    public Collection<EquationPoint> getAllPoints() {
        return scope.getAllPoints();
    }
    
    public void setVars(String[] vars) {
        this.vars = vars;
    }
    
    public EquationScope getScope() {
        return this.scope;
    }
    
    public String[] getVars() {
        return this.vars;
    }
    
    public EquationList getEquations(){
        return this.el;
    }
    
    protected void setEquations(EquationList el) {
        this.el = el;
    }
    
    /**
     * Some heuristic check if two equation systems looks same.
     * If their string representation are equal then we assume yes,
     * otherwise not.
     * @param system the equation system to compare
     * @return if the two systems can be assumed that they are the same 
     */
    public boolean looksSame(EquationSystem system) {
    	if (system == null && this != null) {
    		return false;
    	}
    	EquationList system_el = system.getEquations();
    	if (el.toString().equals(system_el.toString())) {
    		return true;
    	}
    	return false;
    }
}
