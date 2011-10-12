/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */


package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.arithmetic.BooleanValue;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyBoolean;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author Markus
 * @version
 */
public class GeoBoolean extends GeoElement implements BooleanValue, NumberValue,
AbsoluteScreenLocateable {			

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean value = false;
	private boolean isDefined = true;	
	private boolean checkboxFixed = false;
	
	private ArrayList<GeoElement> condListenersShowObject;
		
	public GeoBoolean(Construction c) {
		super(c);			
		setEuclidianVisible(false);
	}

	public GeoBoolean(Construction cons, boolean b) {
		this(cons);
		value = b;
	}

	public String getClassName() {
		return "GeoBoolean";
	}
	
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX;
    }
	
    protected String getTypeString() {
		return "Boolean";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_BOOLEAN;
    }
    
    public void setValue(boolean val) {
    	value = val;
    }
    
    final public boolean getBoolean() {
    	return value;
    }
    
    final public MyBoolean getMyBoolean() {
    	return new MyBoolean(kernel, value);
    }

	public GeoElement copy() {
		GeoBoolean ret = new GeoBoolean(cons);
		ret.setValue(value);		
		return ret;
	}
	
	/**
	 * Registers geo as a listener for updates
	 * of this boolean object. If this object is
	 * updated it calls geo.updateConditions()
	 * @param geo
	 */
	public void registerConditionListener(GeoElement geo) {
		if (condListenersShowObject == null)
			condListenersShowObject = new ArrayList<GeoElement>();
		condListenersShowObject.add(geo);
	}
	
	public void unregisterConditionListener(GeoElement geo) {
		if (condListenersShowObject != null) {
			condListenersShowObject.remove(geo);
		}
	}
	
	
	/**
	 * Calls super.update() and update() for all registered condition listener geos.	 
	 */
	public void update() {  	
		super.update();
				
		// update all registered locatables (they have this point as start point)
		if (condListenersShowObject != null) {
			for (int i=0; i < condListenersShowObject.size(); i++) {
				GeoElement geo = (GeoElement) condListenersShowObject.get(i);		
				kernel.notifyUpdate(geo);					
			}		
		}
	}
	
	/**
	 * Tells conidition listeners that their condition is removed
	 * and calls super.remove()
	 */
	public void doRemove() {
		if (condListenersShowObject != null) {
			// copy conditionListeners into array
			Object [] geos = condListenersShowObject.toArray();	
			condListenersShowObject.clear();
			
			// tell all condition listeners 
			for (int i=0; i < geos.length; i++) {		
				GeoElement geo = (GeoElement) geos[i];
				geo.removeCondition(this);				
				kernel.notifyUpdate(geo);			
			}			
		}
		
		super.doRemove();
	}
	
	public void resolveVariables() {     
    }
		
	public boolean showInEuclidianView() {
		return isIndependent();
	}
	private static int lastLocY = 5;
	
	/**
	 * Set initial absolue screen location
	 */
	public void initLocation(){		
		setAbsoluteScreenLoc(5,lastLocY);
		lastLocY += 30;
	}

	public final boolean showInAlgebraView() {		
		return true;
	}
	
	public boolean isFixable() {
		// visible checkbox should not be fixable
		return isIndependent() && !isSetEuclidianVisible();
	}

	public void set(GeoElement geo) {
		if (geo.isGeoNumeric()) { // eg SetValue[checkbox, 0]
			// 1 = true
			// 0 = false
			setValue(Kernel.isZero(((GeoNumeric)geo).getDouble() - 1));
			isDefined = true;
		} else {
			GeoBoolean b = (GeoBoolean) geo;
			setValue(b.value);
			isDefined = b.isDefined;
		}
	}

	final public void setUndefined() {
		isDefined = false;
	}
	
	final public void setDefined() {
		isDefined = true;
	}

	final public boolean isDefined() {
		return isDefined;
	}			
	
	// dummy implementation of mode
	final public void setMode(int mode) {
	}

	final public int getMode() {
		return -1;
	}
	
	final public String toValueString() {
		switch (kernel.getCASPrintForm()) {
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
				return value ? "True" : "False";							
		
			default:
				return value ? "true" : "false";
		}
	}
	
	final public String toString() {
		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	
	private StringBuilder sbToString;
	private StringBuilder getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuilder();
		return sbToString;
	}

	/**
	 * interface BooleanValue
	 */
	final public boolean isConstant() {
		return false;
	}

	final public boolean isLeaf() {
		return true;
	}

	final public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> varset = new HashSet<GeoElement>();
		varset.add(this);
		return varset;
	}

	final public ExpressionValue evaluate() {
		return this;
	}		
	
	
	/**
	 * returns all class-specific xml tags for saveXML
	 */
	protected void getXMLtags(StringBuilder sb) {
		sb.append("\t<value val=\"");
		sb.append(value);
		sb.append("\"/>\n");				
				
		getXMLvisualTags(sb, isIndependent());
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		
		// checkbox fixed
		if (checkboxFixed) {			
			sb.append("\t<checkbox fixed=\"");
			sb.append(checkboxFixed);
			sb.append("\"/>\n");	
		}
		getScriptTags(sb);

	}	

	public boolean isBooleanValue() {
		return true;
	}
	
	public boolean isGeoBoolean() {
		return true;
	}	

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	

	public double getRealWorldLocX() {
		return 0;
	}

	public double getRealWorldLocY() {		
		return 0;
	}

	public boolean isAbsoluteScreenLocActive() {		
		return true;
	}
	
	public boolean isAbsoluteScreenLocateable() {
		return isIndependent();
	}

	public void setAbsoluteScreenLoc(int x, int y) {		
		if (checkboxFixed) return;
		
		labelOffsetX = x;
		labelOffsetY = y;		
	}

	public int getAbsoluteScreenLocX() {	
		return labelOffsetX;
	}

	public int getAbsoluteScreenLocY() {		
		return labelOffsetY;
	}

	public void setAbsoluteScreenLocActive(boolean flag) {				
	}

	public void setRealWorldLoc(double x, double y) {				
	}

	public final boolean isCheckboxFixed() {
		return checkboxFixed;
	}

	public final void setCheckboxFixed(boolean checkboxFixed) {
		this.checkboxFixed = checkboxFixed;
	}
	
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise check
		if (geo.isGeoBoolean()) return value == ((GeoBoolean)geo).getBoolean(); else return false;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isNumberValue() {
		return true;
	}
	
	
	/**
	 * Returns 1 for true and 0 for false.
	 */
	public double getDouble() {		
		return value ? 1 : 0;
	}

	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble() );
	}

	/**
	 * Returns whether the value (e.g. equation) should be shown
	 * as part of the label description
	 */
	final public boolean isLabelValueShowable() {
		return false;
	}
	
	public boolean canHaveClickScript() {
		return false;
	}


	final public boolean isCasEvaluableObject() {
		return true;
	}
	
	public void moveDependencies(GeoElement oldGeo) {
		if (oldGeo.isGeoBoolean()
				&& ((GeoBoolean) oldGeo).condListenersShowObject != null) {

			condListenersShowObject = ((GeoBoolean) oldGeo).condListenersShowObject;
			for (GeoElement geo : condListenersShowObject) 				
				geo.condShowObject = this;
			
			((GeoBoolean) oldGeo).condListenersShowObject = null;
		}
	}
}