/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.common.util.SpreadsheetTraceSettings;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Helper class to evaluate expressions with GeoBoolean objects in it.
 * @see ExpressionNode#evaluate(StringTemplate)
 * @author Markus Hohenwarter
 */
public class MyBoolean extends ValidExpression implements BooleanValue, NumberValue {
    
    private boolean value;
    private Kernel kernel;
    /**
     * Creates new boolean
     * @param kernel kernel
     * @param value boolean value
     */
    public MyBoolean(Kernel kernel, boolean value) {
        this.value = value;
        this.kernel = kernel;
    }
    
    /**
     * Sets value of this boolean
     * @param value new value
     */
    final public void setValue(boolean value) { 
    	this.value = value; 
    }
       
    @Override
	public String toString(StringTemplate tpl) {    	
        return value ? "true" : "false";
    }

    public boolean isConstant() {
        return true;
    }

    final public boolean isLeaf() {
        return true;
    }
    
    public void resolveVariables(boolean forEquation) {
    	//do nothing
    }

    final public boolean isNumberValue() {
        return true;
    }

    final public boolean isVectorValue() {
        return false;
    }
    
    final public boolean isBooleanValue() {
        return true;
    }

    public boolean isPolynomialInstance() {
        return false;
    }

    public boolean isTextValue() {
        return false;
    }

    public ExpressionValue deepCopy(Kernel kernel1) {
        return new MyBoolean(kernel1, value);
    }

    public HashSet<GeoElement> getVariables() {
        return null;
    }

    @Override
	final public String toValueString(StringTemplate tpl) {
        return toString(tpl);
    }
    
    final public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
    	return toString(tpl);
    }
    
    final public boolean isExpressionNode() {
        return false;
    } 
  
    public boolean isListValue() {
        return false;
    }

    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }

	final public MyBoolean getMyBoolean() {		
		return new MyBoolean(kernel, value);
	}

	final public boolean getBoolean() {		
		return value;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Returns 1 for true and 0 for false.
	 */
	public double getDouble() {		
		return value ? 1 : 0;
	}
	
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	public Kernel getKernel() {
		return kernel;
	}

	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> al) {
		App.warn("dummy method: shouldn't get called");
	}

	public ArrayList<GeoText> getColumnHeadings() {
		App.warn("dummy method: shouldn't get called");
		return null;
	}

	public SpreadsheetTraceSettings getTraceSettings() {
		App.warn("dummy method: shouldn't get called");
		return null;
	}

	public GeoElement toGeoElement() {
		return new GeoBoolean(kernel.getConstruction(), value);
	}

	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble());
	}

	public boolean isAngle() {
		return false;
	}

	public boolean isDefined() {
		return true;
	}
}
