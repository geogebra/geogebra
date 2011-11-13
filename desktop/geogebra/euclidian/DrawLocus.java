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
import geogebra.kernel.GeoLocus;
import geogebra.kernel.MyPoint;
import geogebra.kernel.Traceable;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;


public class DrawLocus extends Drawable {
	
 
	private GeoLocus locus;    	
    
    boolean isVisible, labelVisible;   
	private GeneralPathClipped gp;	
	private double [] lastPointCoords;
	    
    public DrawLocus(EuclidianView view, GeoLocus locus) {      
    	this.view = view;          
    	hitThreshold = view.getCapturingThreshold();
        this.locus = locus;
        geo = locus;                          
   
        update();
    }
    
    final public void update() {    	
        isVisible = geo.isEuclidianVisible(); 
        if (!isVisible) return;	
            
		buildGeneralPath(locus.getMyPointList());
		
		 // line on screen?		
		if (!gp.intersects(0, 0, view.width, view.height)) {				
			isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}		
		updateStrokes(geo);
				
		labelVisible = geo.isLabelVisible();
		if (labelVisible) {								
			labelDesc = geo.getLabelDescription();			
			xLabel = (int) (lastPointCoords[0] - 5);
			yLabel = (int) (lastPointCoords[1] + 4 + view.fontSize);   
			addLabelOffset();           
		}
		
		// draw trace
		if (geo.isTraceable() && (geo instanceof Traceable) && ((Traceable)geo).getTrace()) {
			isTracing = true;
			Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}		
		if(geo.isInverseFill())			{
			setShape(new Area(view.getBoundingPath()));
			getShape().subtract(new Area(gp));
		}

   }
    
	final void drawTrace(Graphics2D g2) {
    	if (isVisible) {
    		g2.setPaint(geo.getObjectColor());
    		g2.setStroke(objStroke);  
            Drawable.drawWithValueStrokePure(gp, g2);
    	}
	}
    

    
    private void buildGeneralPath(ArrayList pointList) {    
    	if (gp == null)
    		gp = new GeneralPathClipped(view);
    	else
    		gp.reset();     	  
    	double [] coords = new double[2];

    	// this is for making sure that there is no lineto from nothing
    	// and there is no lineto if there is an infinite point between the points
    	boolean linetofirst = true; 

    	int size = pointList.size();
		for (int i=0; i < size; i++) {
			MyPoint p = (MyPoint) pointList.get(i);    		
    		
    		// don't add infinite points
    		// otherwise hit-testing doesn't work
    		if (!Double.isInfinite(p.x) && !Double.isInfinite(p.y)
    				&& !Double.isNaN(p.x) && !Double.isNaN(p.y)) {
        		coords[0] = p.x;
        		coords[1] = p.y;
	    		view.toScreenCoords(coords);      		    		
	    		
	    		if (p.lineTo && !linetofirst) {
					gp.lineTo(coords[0], coords[1]);					
				} else {					
					gp.moveTo(coords[0], coords[1]);	   						
				}
	    		linetofirst = false;
    		} else {
    			linetofirst = true;
    		}
        }
    	
    	lastPointCoords = coords;    	
    }      

    final public void draw(Graphics2D g2) {   
    	if (isVisible) {    			    	
            if (geo.doHighlighting()) {
                // draw locus              
                g2.setPaint(geo.getSelColor());
                g2.setStroke(selStroke);
                Drawable.drawWithValueStrokePure(gp, g2);
            }      
        	
            // draw locus         
            g2.setPaint(geo.getObjectColor());
            g2.setStroke(objStroke);
            Drawable.drawWithValueStrokePure(gp, g2);
                        
        	if (geo.isFillable()&&(geo.getAlphaValue() > 0 || geo.isHatchingEnabled())) {
				try {
					
					fill(g2, geo.isInverseFill()?getShape():gp, false); // fill using default/hatching/image as appropriate

				} catch (Exception e) {
					System.err.println(e.getMessage());
				}   
        	}

        	// label
            if (labelVisible) {
				g2.setFont(view.fontLine);
				g2.setColor(geo.getLabelColor());
				drawLabel(g2);
            }                        
        }
    }     
    	
    
   
    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
		Shape t = geo.isInverseFill()?getShape():gp;
    	if (t == null) return false; // hasn't been drawn yet (hidden)

    	if (strokedShape == null) {
			strokedShape = objStroke.createStrokedShape(gp);
		}    		
		if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled())
			return t.intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold); 					
		else
			return strokedShape.intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold); 
    	
    	/*
        return gp.intersects(x-2,y-2,4,4)
				&& !gp.contains(x-2,y-2,4,4);
				*/        
    }
    
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(gp.getBounds());  
    }
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 
    
	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !locus.isClosedPath() || !geo.isEuclidianVisible())
			return null;
		else 
			return gp.getBounds();	
	}

}


