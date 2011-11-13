/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolyLine;
import geogebra.kernel.kernelND.GeoPointND;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawPolyLine extends Drawable
implements Previewable {
   
    private GeoPolyLine poly;            
    private boolean isVisible, labelVisible;
    
    private GeneralPathClipped gp;
    private double [] coords = new double[2];
	private ArrayList<GeoPoint> points;              
      
    public DrawPolyLine(EuclidianView view, GeoPolyLine poly) {
		this.view = view; 
		this.poly = poly;		
		geo = poly;

		update();
    }
    
    /**
     * Creates a new DrawPolygon for preview.     
     */
	DrawPolyLine(EuclidianView view, ArrayList points) {
		this.view = view; 
    	hitThreshold = view.getCapturingThreshold();
		this.points = points;

		updatePreview();
	} 

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (isVisible) { 
			labelVisible = geo.isLabelVisible();       
			updateStrokes(poly);
			
            // build general path for this polygon
			addPointsToPath(poly.getPoints());
        	
        	 // polygon on screen?		
    		if (!gp.intersects(0,0, view.width, view.height)) {				
    			isVisible = false;
            	// don't return here to make sure that getBounds() works for offscreen points too
    		}             
        }
        
		// draw trace
		if (poly.getTrace()) {
			isTracing = true;
			Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}			

    }
	
	final void drawTrace(Graphics2D g2) {
    	if (isVisible) {
    		g2.setPaint(geo.getObjectColor());
    		g2.setStroke(objStroke);  
            Drawable.drawWithValueStrokePure(gp, g2);
    	}
	}
    

	
	private void addPointsToPath(GeoPointND[] points) {
		if (gp == null)
			gp = new GeneralPathClipped(view);
		else
			gp.reset();
		
		// first point
		points[0].getInhomCoords(coords);
		view.toScreenCoords(coords);			
        gp.moveTo(coords[0], coords[1]);   
		
		// for centroid calculation (needed for label pos)
		double xsum = coords[0];
		double ysum = coords[1];
        
        for (int i=1; i < points.length; i++) {
			points[i].getInhomCoords(coords);
			view.toScreenCoords(coords);	
			if (labelVisible) {
				xsum += coords[0];
				ysum += coords[1];
			}			
        	gp.lineTo(coords[0], coords[1]);                  	
        }

		if (labelVisible) {
			labelDesc = geo.getLabelDescription();  
			xLabel = (int) (xsum / points.length);
			yLabel = (int) (ysum / points.length);
			addLabelOffset();                                       
		}  
	}
        
	final public void draw(Graphics2D g2) {
        if (isVisible) {

			g2.setPaint(poly.getObjectColor());                       
            g2.setStroke(objStroke);            
			g2.draw(gp);  				
   	        	
            	
            if (geo.doHighlighting()) {
                g2.setPaint(poly.getSelColor());
                g2.setStroke(selStroke);            
                g2.draw(gp);                
            }        
        	
                                 
            if (labelVisible) {
				g2.setPaint(poly.getLabelColor());
				g2.setFont(view.fontPoint);
				drawLabel(g2);
            }			
        }
    }
    
	final public void updatePreview() {
		int size = points.size();
		isVisible = size > 0;
		
		if (isVisible) { 		
			GeoPoint[] pointsArray = new GeoPoint[size];
			for (int i=0; i < size; i++) {
				pointsArray[i] = (GeoPoint) points.get(i);
			}
			addPointsToPath(pointsArray);								              
		}	
	}
	
	private Point2D.Double endPoint = new Point2D.Double();

	final public void updateMousePos(double xRW, double yRW) {	
		if (isVisible) { 											
			//double xRW = view.toRealWorldCoordX(mx);
			//double yRW = view.toRealWorldCoordY(my);
			
			int mx = view.toScreenCoordX(xRW);
			int my = view.toScreenCoordY(yRW);
			
			// round angle to nearest 15 degrees if alt pressed
			if (view.getEuclidianController().altDown) {
				GeoPoint p = (GeoPoint)points.get(points.size() - 1);
				double px = p.inhomX;
				double py = p.inhomY;
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt((py - yRW) * (py - yRW) + (px - xRW) * (px - xRW));
				
				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15; 
				
				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);
				
				mx = view.toScreenCoordX(xRW);
				my = view.toScreenCoordY(yRW);
				
				endPoint.x = xRW;
				endPoint.y = yRW;
				view.getEuclidianController().setLineEndPoint(endPoint);
				gp.lineTo(mx, my);
			}
			else
				view.getEuclidianController().setLineEndPoint(null);
				gp.lineTo(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW));                                   			                                            
		}
	}

	final public void drawPreview(Graphics2D g2) {
    	if (isVisible) {
       						
			g2.setPaint(ConstructionDefaults.colPreview);             
			g2.setStroke(objStroke);            
			g2.draw(gp);
    	}		            	
    }
	
	public void disposePreview() {	
	}
    
	final public boolean hit(int x,int y) {		
    	if (isVisible) {
    		if (strokedShape == null) {
    			strokedShape = objStroke.createStrokedShape(gp);
    		}    		
    		return strokedShape.intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold);
    	} else
    		return false;
    }
	
    final public boolean isInside(Rectangle rect) {
    	return gp != null && rect.contains(gp.getBounds());  
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
    /**
	 * Returns the bounding box of this Drawable in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		else 
			return gp.getBounds();	
	}

    
}
