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


/**
 * 
 * @author Michael
 * @version
 */
public class GeoButton extends GeoElement implements AbsoluteScreenLocateable, TextProperties {			

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean buttonFixed = false;
	
	private int fontSize = 0;
	
	public GeoButton(Construction c) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		setEuclidianVisible(true);
		setAuxiliaryObject(true);
	}

	public GeoButton(Construction cons, int labelOffsetX, int labelOffsetY) {
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
	}

	public String getClassName() {
		return "GeoButton";
	}
	
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_BUTTON_ACTION;
    }
	
    protected String getTypeString() {
		return "Button";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_BUTTON;
    }
    
	public GeoElement copy() {
		return new GeoButton(cons, labelOffsetX, labelOffsetY);
	}
	
	public boolean isGeoButton() {
		return true;
	}

	public void resolveVariables() {     
    }
		
	public boolean showInEuclidianView() {
		return true;
	}

	public boolean showInAlgebraView() {		
		return false;
	}
	
	public boolean isFixable() {
		return true;
	}

	public void set(GeoElement geo) {
	}

	final public void setUndefined() {
	}
	
	final public void setDefined() {
	}

	final public boolean isDefined() {
		return true;
	}			
	
	// dummy implementation of mode
	final public void setMode(int mode) {
	}

	final public int getMode() {
		return -1;
	}
	
	public String toValueString() {
		return "";
	}
	
	final public String toString() {
		return label;
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
		return true;
	}

	public void setAbsoluteScreenLoc(int x, int y) {		
		if (buttonFixed) return;
		
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

	public final boolean isButtonFixed() {
		return buttonFixed;
	}

	public final void setButtonFixed(boolean buttonFixed) {
		this.buttonFixed = buttonFixed;
	}
	
	public boolean isTextField() {
		return false;
	}
	
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		return false;
	}
	
	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Returns whether the value (e.g. equation) should be shown
	 * as part of the label description
	 */
	final public boolean isLabelValueShowable() {
		return false;
	}

	public int getFontSize() {
		return fontSize ;
	}
	public void setFontSize(int size) {
		fontSize = size;
	}

	public int getFontStyle() {
		return 0;
	}

	public void setFontStyle(int fontStyle) {
		
	}

	public int getPrintDecimals() {
		return 0;
	}

	public int getPrintFigures() {
		return 0;
	}

	public void setPrintDecimals(int printDecimals, boolean update) {
	
	}

	public void setPrintFigures(int printFigures, boolean update) {
	}

	public boolean isSerifFont() {
		return false;
	}

	public void setSerifFont(boolean serifFont) {
	}

	public boolean useSignificantFigures() {
		return false;
	}

	public boolean justFontSize() {
		return true;
	}
	
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		// font settings
		if (fontSize != 0) {
			sb.append("\t<font size=\"");
			sb.append(fontSize);
			sb.append("\"/>\n");
		}
	}



}