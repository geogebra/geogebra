/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.cas.AlgoIntegralDefinite;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Draws definite Integral of a GeoFunction
 * @author  Markus Hohenwarter
 */
public class DrawIntegral extends Drawable {
   
    private GeoNumeric n;
    private GeoFunction f;
    private NumberValue a, b;
    private GeneralPathClipped gp;
    private boolean isVisible, labelVisible;
   
    /**
     * Creates new drawable for integral
     * @param view
     * @param n
     */
    public DrawIntegral(EuclidianView view, GeoNumeric n) {
    	this.view = view; 	
    	this.n = n;
		geo = n;
		
		n.setDrawable(true);
    	
    	init();
        update();
    }
    
    private void init(){
    	AlgoIntegralDefinite algo = (AlgoIntegralDefinite) n.getDrawAlgorithm();
		f = algo.getFunction();
		a = algo.getA();
		b = algo.getB();
    }

    final public void update() {					
    	isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();            
		updateStrokes(n);
		if(!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();
        
		// init gp
		double aRW = a.getDouble();
		double bRW = b.getDouble();

		// for DrawParametricCurve.plotCurve to work with special values,
		// these changes are needed (also filter out out of screen integrals)
		// see #1234
		aRW = Math.max(aRW, view.getXmin() - EuclidianView.CLIP_DISTANCE);
		if (aRW > view.getXmax() + EuclidianView.CLIP_DISTANCE)
			return;

		bRW = Math.min(bRW, view.getXmax() + EuclidianView.CLIP_DISTANCE);
		if (bRW < view.getXmin() - EuclidianView.CLIP_DISTANCE)
			return;

		double ax = view.toScreenCoordXd(aRW);
		double bx = view.toScreenCoordXd(bRW);
		float y0 = (float) view.yZero;
				
		// plot definite integral
		if (gp == null)
			gp = new GeneralPathClipped(view);
		else
			gp.reset(); 	
		
		if (Kernel.isEqual(aRW, bRW)) {
			gp.moveTo(ax, y0); 
			gp.lineTo(ax, view.toScreenCoordYd(f.evaluate(aRW)));
			gp.lineTo(ax, y0); 						
			return;
		}

		
		gp.moveTo(ax, y0); 
		DrawParametricCurve.plotCurve(f, aRW, bRW, view, gp, false, DrawParametricCurve.GAP_LINE_TO);
		gp.lineTo(bx, y0);
		gp.lineTo(ax, y0);			
		
//		 gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}

		if (labelVisible) {
			xLabel = (int) Math.round((ax + bx) / 2) - 6;
			yLabel = (int) view.yZero - view.fontSize;
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}		
    }
    
	final public void draw(Graphics2D g2) {
        if (isVisible) {        	
            if (geo.doHighlighting()) {
                g2.setPaint(n.getSelColor());
                g2.setStroke(selStroke);            
                Drawable.drawWithValueStrokePure(gp, g2);     
            } 
            
			fill(g2, gp, true); // fill using default/hatching/image as appropriate
			
			
			if (geo.lineThickness > 0) {
				g2.setPaint(n.getObjectColor());
				g2.setStroke(objStroke);                                   
				Drawable.drawWithValueStrokePure(gp, g2);   	
			}
			
            if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
            }        
        }
    }
    
	final public boolean hit(int x,int y) {  
	       return gp != null && (gp.contains(x, y) || gp.intersects(x-3, y-3, 6, 6));        
    }
	
	final public boolean isInside(Rectangle rect) {  
    	return false;   
    }
	
	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		else 
			return gp.getBounds();	
	}
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
}
