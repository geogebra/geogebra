/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * VarString.java
 *
 * Created on 18. November 2001, 14:49
 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoDummyVariable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.MyParseError;

import java.util.HashSet;


/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class Variable extends ValidExpression implements ExpressionValue {

    private String name;
    private Kernel kernel;
        
    /** Creates new VarString */
    public Variable(Kernel kernel, String name) {
        this.name = name;
        this.kernel = kernel;
    }      
    
	public ExpressionValue deepCopy(Kernel kernel) {
		return new Variable(kernel, name);
	}
    
    public String getName() { return toString(); }
    
    public boolean isConstant() {
        return false;
    }
    
    public boolean isLeaf() {
        return true;
    }
    
    public ExpressionValue evaluate() {
    	return this;
    }   
    
   
    /**
     * Looks up the name of this variable in the kernel and returns the 
     * according GeoElement object.
     */
    private GeoElement resolve() {
    	return resolve(!kernel.isResolveUnkownVarsAsDummyGeos());
    }
    	
	/**
     * Looks up the name of this variable in the kernel and returns the 
     * according GeoElement object.
     */
    GeoElement resolve(boolean allowAutoCreateGeoElement) {
    	// keep bound CAS variables when resolving a CAS expression
    	if (kernel.isResolveUnkownVarsAsDummyGeos()) 
    	{
    		// resolve unknown variable as dummy geo to keep its name and 
			// avoid an "unknown variable" error message
			return new GeoDummyVariable(kernel.getConstruction(), name);
    	}
       
    	// lookup variable name, create missing variables automatically if allowed
    	GeoElement geo = kernel.lookupLabel(name, allowAutoCreateGeoElement);    	
        if (geo != null)
			return  geo;     
	
        // if we get here we couldn't resolve this variable name as a GeoElement
        String [] str = { "UndefinedVariable", name };
        throw new MyParseError(kernel.getApplication(), str); 	
    }
    
    /**
     * Looks up the name of this variable in the kernel and returns the 
     * according GeoElement object. For absolute spreadsheet reference names
     * like A$1 or $A$1 a special ExpressionNode wrapper object is returned
     * that preserves this special name for displaying of the expression.
     */
    final public ExpressionValue resolveAsExpressionValue() {
    	GeoElement geo = resolve();
    	
    	// spreadsheet dollar sign reference
    	if (name.indexOf('$') > -1) {
    		// row and/or column dollar sign present?
    		boolean col$ = name.indexOf('$') == 0;
    		boolean row$ = name.length() > 2 && name.indexOf('$', 1) > -1;
    		int operation = 0;
    		if (row$ && col$)
    			operation = ExpressionNode.$VAR_ROW_COL;    			 
    		else if (row$)
    			operation = ExpressionNode.$VAR_ROW;  
    		else // if (col$)
    			operation = ExpressionNode.$VAR_COL;  
    
    		// build an expression node that wraps the resolved geo
    		return new ExpressionNode(kernel, geo, operation, null);    		
    	} 
    	// standard case: no dollar sign
    	else {    		
    		return geo;
    	}
    }
    
    public HashSet getVariables() {
        HashSet ret = new HashSet();
        ret.add(resolve());
        return ret;
    }
    
    public void resolveVariables() {
    	// this has to be handled in ExpressionNode
    }

    public String toString() {
		return kernel.printVariableName(name);
    }
    
	public String toValueString() {
		return toString();
	}
	
	
	final public String toLaTeXString(boolean symbolic) {
		return toString();
	}
 
	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}
		
	public boolean isBooleanValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}
	
	public boolean isTextValue() {
		return false;
	}
	
	final public boolean isExpressionNode() {
		return false;
	}
	
	final public boolean isVariable() {
		return true;
	}   
	
    public boolean isListValue() {
        return false;
    }

	
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	public boolean isVector3DValue() {
		return false;
	}    
    
	public String toOutputValueString() {
		return toValueString();
	}

	public Kernel getKernel() {
		return kernel;
	}

}
