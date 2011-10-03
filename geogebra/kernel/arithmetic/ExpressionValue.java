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

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.util.HashSet;

/**
 *
 * @author  Markus
 * @version 
 */
public interface ExpressionValue {      
    public boolean isConstant();    
    public boolean isLeaf();
    public boolean isNumberValue();
	public boolean isVectorValue();	
	public boolean isVector3DValue();	
	public boolean isListValue();
	public boolean isBooleanValue();
	public boolean isPolynomialInstance();
	public boolean isTextValue();
	public boolean isExpressionNode();
	public boolean isGeoElement();
	public boolean isVariable();
	public boolean isInTree(); // used in ExpressionNode tree
	public void setInTree(boolean flag);
	public boolean contains(ExpressionValue ev);
	public ExpressionValue deepCopy(Kernel kernel);
    public ExpressionValue evaluate();
    public HashSet<GeoElement> getVariables();   
    public String toValueString();
    public String toOutputValueString();
    public String toLaTeXString(boolean symbolic);   
    public void resolveVariables();
	public String toRealString();
	public Kernel getKernel();
}

