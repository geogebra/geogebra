package geogebra3D.euclidian3D;


import java.awt.Font;

import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;

public class DrawText3D extends Drawable3DCurves {


	public DrawText3D(EuclidianView3D a_view3d, GeoText text) {
		super(a_view3d, text);
		
		((DrawLabel3DForText) label).setGeo(text);
		
	}
	
	protected DrawLabel3D newDrawLabel3D(EuclidianView3D view3D){
		return new DrawLabel3DForText(view3D);
	}
	

	protected void updateForView() {

	}

	protected boolean updateForItSelf() {
		return true;
	}


	public void drawGeometry(Renderer renderer) {
		
	}

	protected void updateLabel(){	
		
		GeoText text = (GeoText) getGeoElement();
				
		label.update(text.getTextString(), 
				getFont(), 
				getGeoElement().getObjectColor(),
				getLabelPosition(),
				getLabelOffsetX(),-getLabelOffsetY());


	}
	
	
	private Font getFont() {
		GeoText text = (GeoText) getGeoElement();
		
		// text's font size is relative to the global font size
		int newFontSize = Math.max(4, getView3D().getFontSize() + text.getFontSize());		
		int newFontStyle = text.getFontStyle();	
		boolean newSerifFont = text.isSerifFont();
		
		/*
		if (textFont.canDisplayUpTo(text.getTextString()) != -1 ||
				fontSize !=newFontSize || fontStyle != newFontStyle || newSerifFont != serifFont) {					
			super.updateFontSize();
			
			fontSize = newFontSize;
			fontStyle = newFontStyle;
			serifFont = newSerifFont;
			*/
						
			//if (isLaTeX) {
			//	//setEqnFontSize();				
			//} else {				
				Font textFont = getView3D().getApplication().getFontCanDisplay(text.getTextString(), newSerifFont, newFontStyle, newFontSize);				
			//}	
				
				return textFont;
	}
	
	private boolean isLocationDefined;
	
    protected boolean isLabelVisible(){
    	return isVisible() && isLocationDefined;
    }
    
    public Coords getLabelPosition(){
    	
    	isLocationDefined = true;
    	
    	GeoText text = (GeoText) getGeoElement();
    	
    	// compute location of text		
		if (text.isAbsoluteScreenLocActive()) 
			return new Coords(text.getAbsoluteScreenLocX(),text.getAbsoluteScreenLocY(),0,1);
		
		
		GeoPointND loc = text.getStartPoint();
	       
		if (loc == null)
			//return new Coords(getView3D().getXZero(),getView3D().getYZero(),0,1);
			return new Coords(0,0,0,1);
				 
		
	    if (!loc.isDefined()){
	    	isLocationDefined = false;
	    	return null;
	    }
	    
	    return loc.getInhomCoordsInD(3);

    }
	

	public int getPickOrder() {
		
		return DRAW_PICK_ORDER_0D;
	}

}
