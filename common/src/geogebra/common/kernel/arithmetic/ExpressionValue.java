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
 * @version 
 */
@SuppressWarnings("javadoc")
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
    public ExpressionValue evaluate(StringTemplate tpl);
    public HashSet<GeoElement> getVariables();   
    @Deprecated
    public String toString();
    public String toOutputValueString(StringTemplate tpl);
    public String toLaTeXString(boolean symbolic,StringTemplate tpl);   
    public void resolveVariables();
	public String toRealString(StringTemplate tpl);
	public Kernel getKernel();
	public String toString(StringTemplate tpl);
	public String toValueString(StringTemplate tpl);
}

