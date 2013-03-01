/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * NumberValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;

import java.util.HashSet;

/**
 *
 * @author  Markus
 * 
 */

public interface ExpressionValue {      
    /**
     * @return true if this is constant
     */
    public boolean isConstant();    
    /**
     * @return whether this is leaf if it occurs in ExpressionNode
     */
    public boolean isLeaf();
    /**
     * @return whether this is instance of NumberValue
     */
    public boolean isNumberValue();
	/**
	 * @return whether this is instance of VectorValue
	 */
	public boolean isVectorValue();	
	/**
	 * @return whether this is instance of Vector3DValue
	 */
	public boolean isVector3DValue();	
	/**
	 * @return whether this is instance of ListValue
	 */
	public boolean isListValue();
	/**
	 * @return whether this is instance of BooleanValue
	 */
	public boolean isBooleanValue();
	/**
	 * @return whether this is instance of Polynomial
	 */
	public boolean isPolynomialInstance();
	/**
	 * @return whether this is instance of TextValue
	 */
	public boolean isTextValue();
	/**
	 * @return whether this is instance of ExpressionNode
	 */
	public boolean isExpressionNode();
	/**
	 * @return whether this is instance of GeoElement
	 */
	public boolean isGeoElement();
	/**
	 * @return whether this is instance of Variable
	 */
	public boolean isVariable();
	/**
	 * @return whether this is part of some expression node tree
	 */
	public boolean isInTree(); 
	/**
	 * @param flag whether this is part of some expression node tree
	 */
	public void setInTree(boolean flag);
	/**
	 * @param ev expression value
	 * @return whether given value is contained in tree / list of this 
	 */
	public boolean contains(ExpressionValue ev);
	/**
	 * @param kernel kernel
	 * @return deep copy (duplicates all ExpressionValues used for definition of this)
	 */
	public ExpressionValue deepCopy(Kernel kernel);
    /**
     * @return evaluated value
     */
    public NumberValue evaluateNum();
    /**
     * @param tpl string template (in case concatenation of strings is involved)
     * @return evaluated value
     */
    public ExpressionValue evaluate(StringTemplate tpl);
    /**
     * @return set of GeoElement variables
     */
    public HashSet<GeoElement> getVariables();   
    @Deprecated
    public String toString();
    /**
     * @param tpl string template
     * @return value string that can be re-run as GGB command
     */
    public String toOutputValueString(StringTemplate tpl);
    /**
     * @param symbolic true to keep variable names
     * @param tpl string template
     * @return LaTeX string
     */
    public String toLaTeXString(boolean symbolic,StringTemplate tpl);   
    /**
     * Resolve variables
     * @param forEquation true to resolve xx as polynomial rather than product of function variables
     */
    public void resolveVariables(boolean forEquation);

	/**
	 * @return kernel
	 */
	public Kernel getKernel();
	/**
	 * @param tpl string template
	 * @return string representation of this object
	 */
	public String toString(StringTemplate tpl);
	/**
	 * @param tpl string template
	 * @return string representation of value of this object
	 */
	public String toValueString(StringTemplate tpl);
	
	/**
	 * Lets the traversing object go through the structure of this ExpressionValue
	 * and return changed value. This method may change content of this value, so
	 * you might need to use copy first.
	 * @param t traversing object
	 * @return changed value
	 */
	public ExpressionValue traverse(Traversing t);
	/**
	 * If this is an expression node wrapping some other ExpressionValue, retur its content, 
	 * otherwise return this.
	 * @return unwrapped content
	 */
	public ExpressionValue unwrap();
	/**
	 * Wraps this value in ExpressionNode if it's not already one.
	 * @return wrapped value
	 */
	public ExpressionNode wrap();
	
	/**
	 * 
	 * @return whether x(this) makes sense
	 */
	public boolean hasCoords();
	/**
	 * @param fv variable with respect to which the derivative is computed
	 * @return derivative
	 */
	public ExpressionValue derivative(FunctionVariable fv);
	
	/**
	 * @param fv variable with respect to which the integral is computed
	 * @return integral
	 */
	ExpressionValue integral(FunctionVariable fv);
}

