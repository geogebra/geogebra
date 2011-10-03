/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.main.Application;

/**
 * This class is needed to handle dependent multivariate functions like
 * e.g. f(x,y) = a x^2 + b y that depends on a and b.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentFunctionNVar extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private FunctionNVar fun;
    private GeoFunctionNVar f; // output         

    /**
     * @param cons
     * @param label
     * @param fun
     */
    public AlgoDependentFunctionNVar(Construction cons, String label, FunctionNVar fun) {
        this(cons, fun);
       	f.setLabel(label);
    }
    
    /**
     * @param cons
     * @param fun
     */
    AlgoDependentFunctionNVar(Construction cons, FunctionNVar fun) {
        super(cons);
        this.fun = fun;
        f = new GeoFunctionNVar(cons);
        f.setFunction(fun);
        
        setInputOutput(); // for AlgoElement
        
        compute();
    }
    
    /**
     * @param cons
     */
    public AlgoDependentFunctionNVar(Construction cons) {
		super(cons);
	}

	public String getClassName() {
        return "AlgoDependentFunctionNVar";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = fun.getGeoElementVariables();

        setOutputLength(1);
        setOutput(0,f);
        setDependencies(); // done by AlgoElement
    }

    /**
     * @return resulting function
     */
    public GeoFunctionNVar getFunction() {
        return f;
    }

    protected final void compute() {
        // evaluation of function will be done in view (see geogebra.euclidian.DrawFunction)
        
        // check if function is defined
        boolean isDefined = true;
        for (int i=0; i < input.length; i++) {
            if (!input[i].isDefined()) {
                isDefined = false;
                break;
            }
        }
        f.setDefined(isDefined);
    }
    
    private StringBuilder sb;
    public String toString() {    	
        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
        if (f.isLabelSet() && !f.isBooleanFunction()) {
            sb.append(f.label);
            sb.append("(");
			sb.append(f.getVarString());
			sb.append(") = ");
        }  
        sb.append(fun.toString());
        return sb.toString();
    }
    
    public String toRealString() {
    	Application.printStacktrace("wrong string");
        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
        if (f.isLabelSet() && !f.isBooleanFunction()) {
            sb.append(f.getRealLabel());
            sb.append("(");
			sb.append(f.getVarString());
			sb.append(") = ");
        }  
        sb.append(fun.getExpression().toRealString());
        return sb.toString();
    }
    
}
