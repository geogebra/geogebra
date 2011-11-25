/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.kernel.geos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.Construction;

import java.awt.Font;


/**
 * 
 * @author Michael
 * @version
 */
public class GeoButton extends GeoElement implements AbsoluteScreenLocateable, TextProperties {			

	private boolean buttonFixed = false;
	
	private int fontSize = 0;
	private int fontStyle = Font.PLAIN;

	private boolean serifFont = false;
	
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

	@Override
	public String getClassName() {
		return "GeoButton";
	}
	
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_BUTTON_ACTION;
    }
	
    @Override
	protected String getTypeString() {
		return "Button";
	}
    
    @Override
	public GeoClass getGeoClassType() {
    	return GeoClass.BUTTON;
    }
    
	@Override
	public GeoElement copy() {
		return new GeoButton(cons, labelOffsetX, labelOffsetY);
	}
	
	@Override
	public boolean isGeoButton() {
		return true;
	}

	@Override
	public void resolveVariables() {     
    }
		
	@Override
	public boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean showInAlgebraView() {		
		return false;
	}
	
	@Override
	public boolean isFixable() {
		return true;
	}

	@Override
	public void set(GeoElement geo) {
	}

	@Override
	final public void setUndefined() {
	}
	
	final public void setDefined() {
	}

	@Override
	final public boolean isDefined() {
		return true;
	}			
	
	// dummy implementation of mode
	final public void setMode(int mode) {
	}

	final public static int getMode() {
		return -1;
	}
	
	@Override
	public String toValueString() {
		return "";
	}
	
	@Override
	final public String toString() {
		return label;
	}
	
	@Override
	public boolean isVectorValue() {
		return false;
	}

	@Override
	public boolean isPolynomialInstance() {
		return false;
	}

	@Override
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
	
	@Override
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
	@Override
	final public boolean isEqual(GeoElement geo) {
		return false;
	}
	
	@Override
	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Returns whether the value (e.g. equation) should be shown
	 * as part of the label description
	 */
	@Override
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
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
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
		return serifFont ;
	}

	public void setSerifFont(boolean serifFont) {
		this.serifFont = serifFont;
	}

	public boolean useSignificantFigures() {
		return false;
	}

	public boolean justFontSize() {
		return true;
	}
	
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// font settings
		if (serifFont || fontSize != 0 || fontStyle != 0) {
			sb.append("\t<font serif=\"");
			sb.append(serifFont);
			sb.append("\" size=\"");
			sb.append(fontSize);
			sb.append("\" style=\"");
			sb.append(fontStyle);
			sb.append("\"/>\n");
		}
		
	}
	
	@Override
	public boolean hasBackgroundColor() {
		return true;
	}



}