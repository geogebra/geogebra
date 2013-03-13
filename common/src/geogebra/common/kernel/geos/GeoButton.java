/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.common.kernel.geos;



import geogebra.common.awt.GFont;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.StringUtil;


/**
 * 
 * @author Michael
 */
public class GeoButton extends GeoElement implements AbsoluteScreenLocateable, TextProperties, Furniture{			

	private double fontSizeD = 1;
	private int fontStyle = GFont.PLAIN;

	private boolean serifFont = false;
	
	private boolean fixedSize=false;
	
	private int width=40;
	private int height=30;
	
	private Observer observer;
	
	
	/**
	 * Creates new button
	 * @param c construction
	 */
	public GeoButton(Construction c) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		setEuclidianVisible(true);
		setAuxiliaryObject(true);
	}
	/**
	 * Creates new button
	 * @param cons construction
	 * @param labelOffsetX x offset
	 * @param labelOffsetY y offset
	 */
	public GeoButton(Construction cons, int labelOffsetX, int labelOffsetY) {
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
	}

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_BUTTON_ACTION;
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
		if(!geo.isGeoButton())
			return;
		setCaption(geo.getRawCaption());
	}

	@Override
	final public void setUndefined() {
		//do nothing
	}

	@Override
	final public boolean isDefined() {
		return true;
	}			

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
	}
	
	@Override
	final public String toString(StringTemplate tpl) {
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
		//do nothing
	}

	public void setRealWorldLoc(double x, double y){
		//do nothing
	}
	/**
	 * @return true for textfields, false for buttons
	 */
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

	public double getFontSizeMultiplier() {
		return fontSizeD ;
	}
	public void setFontSizeMultiplier(double d) {
		fontSizeD = d;
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
		//do nothing
	}

	public void setPrintFigures(int printFigures, boolean update) {
		//do nothing
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
		GeoText.appendFontTag(sb, serifFont, fontSizeD, fontStyle, false, app);

		// name of image file
		if (getFillImage() != null) {
			sb.append("\t<file name=\"");
			sb.append(StringUtil.encodeXML(this.getGraphicsAdapter().getImageFileName()));
			sb.append("\"/>\n");
		}
		if (isFixedSize()){
			sb.append("\t<dimensions width=\"" + width + "\" height=\"" + height + "\" />\n");
		}
	}
	
	@Override
	public boolean hasBackgroundColor() {
		return true;
	}

	@Override
	public boolean isFillable() { 
		return true;
	}
	
	@Override
	public FillType getFillType(){
		return FillType.IMAGE;
	}
	
	public boolean isFurniture() {
		return true;
	}
	
	@Override
	public boolean isPinnable() {
		return false;
	}
	public boolean isFixedSize() {
		return fixedSize;
	}
	public void setFixedSize(boolean fixedSize) {
		this.fixedSize = fixedSize;
		if(observer!=null)
		observer.notifySizeChanged();
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
		if(observer!=null)
		observer.notifySizeChanged();
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void setObserver(Observer observer) {
		this.observer = observer;
	}
	public interface Observer{
		public void notifySizeChanged();
	}

}