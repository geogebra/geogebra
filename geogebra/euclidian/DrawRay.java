/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawSegment
 *
 * Created on 21. 8 . 2003
 */

package geogebra.euclidian;

import geogebra.euclidian.clipping.ClipLine;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawRay extends Drawable
implements Previewable {
   
    private GeoLineND ray;
    //private GeoPoint A;
     
    boolean isVisible, labelVisible;
    private ArrayList<GeoPointND> points;
    
    private Line2D.Double line = new Line2D.Double();               
    private double [] a = new double[2];
	private double [] v = new double[2];
     
    /** Creates new DrawSegment */
    public DrawRay(EuclidianView view, GeoLineND ray) {
    	this.view = view;
    	hitThreshold = view.getCapturingThreshold();
    	this.ray = ray;
    	geo = (GeoElement) ray;
    	    	
    	
        update();
    }
    
	/**
	 * Creates a new DrawSegment for preview.     
	 * @param view 
	 * @param points 
	 */
	DrawRay(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view; 
		this.points = points;

		updatePreview();
	} 

	final public void update() {
		update(true);
	}
	
	void update(boolean showLabel) {
        isVisible = geo.isEuclidianVisible();
        if (isVisible) { 
			labelVisible = showLabel && geo.isLabelVisible();       
			updateStrokes((GeoElement) ray);
			
	    	
			

			// calc direction vector of ray in screen coords
			Coords equation = ray.getCartesianEquationVector(view.getMatrix());
			if (equation==null){
				isVisible = false;
				return;
			}

			// calc start point of ray in screen coords		
			Coords A = view.getCoordsForView(ray.getStartInhomCoords());
			a[0] = A.getX(); a[1] = A.getY();
			view.toScreenCoords(a);
			
			
			v[0] = equation.getY() * view.xscale;
			v[1] = equation.getX() * view.yscale;
			
			setClippedLine();
			
			 // line on screen?		
    		if (!line.intersects(0,0, view.width, view.height)) {				
    			isVisible = false;
            	// don't return here to make sure that getBounds() works for offscreen points too
    		}
			
			// draw trace
			if (ray.getTrace()) {
				isTracing = true;
				Graphics2D g2 = view.getBackgroundGraphics();
				if (g2 != null) drawTrace(g2);
			} else {
				if (isTracing) {
					isTracing = false;
					view.updateBackground();
				}
			}					           
                                   
            // label position
            // use unit perpendicular vector to move away from line
            if (labelVisible) {
				labelDesc = geo.getLabelDescription();
				
				double nx = v[0];
				double ny = -v[1];
				double length = GeoVec2D.length(nx, ny);
				double unit;
				if (length > 0.0) {
					unit = 16d / length;        		    		   				           					
				} else {
					nx = 0.0;
					ny = 1.0;
					unit = 16d;
				}				   
				xLabel = (int) (a[0] + v[0]/2.0 + nx * unit);
				yLabel = (int) (a[1] + v[1]/2.0 + ny * unit);	  
				addLabelOffset();        
            }		                                                
        }
    }
    
	  private void setClippedLine() {
			boolean onscreenA = view.isOnScreen(a);
  	    	
			// calc clip point C = a + lambda * v
			double lambda;
			if (Math.abs(v[0]) > Math.abs(v[1])) {
				if (v[0] > 0) // RIGHT
					lambda = (view.width - a[0]) / v[0];
				else // LEFT
					lambda = - a[0] / v[0];
			} else {
				if (v[1] > 0) // BOTTOM
					lambda = (view.height - a[1]) / v[1];
				else 
					lambda = -a[1] / v[1];
			}

			if (lambda < 0) { // ray is completely out of screen
				isVisible = false;
				return;
			}

			if (onscreenA ) {
				// A on screen
				line.setLine(a[0], a[1],  a[0] + lambda * v[0], a[1] + lambda * v[1]);
			} else {
				// A off screen
				// clip ray at screen, that's important for huge coordinates of A
				Point2D.Double [] clippedPoints = 
					ClipLine.getClipped(a[0], a[1],  a[0] + lambda * v[0], a[1] + lambda * v[1], -EuclidianView.CLIP_DISTANCE, view.width + EuclidianView.CLIP_DISTANCE, -EuclidianView.CLIP_DISTANCE, view.height + EuclidianView.CLIP_DISTANCE);
				if (clippedPoints == null) {
					isVisible = false;	
				} else {
					line.setLine(clippedPoints[0].x, clippedPoints[0].y, clippedPoints[1].x, clippedPoints[1].y);
				}
			}
	    }
    
    final public void draw(Graphics2D g2) {
        if (isVisible) {			
            if (geo.doHighlighting()) {
                g2.setPaint(geo.getSelColor());
                g2.setStroke(selStroke);            
                g2.draw(line);       
            }
            
            g2.setPaint(geo.getObjectColor());             
            g2.setStroke(objStroke);            
			g2.draw(line);            
                        
            if (labelVisible) {
				g2.setPaint(geo.getLabelColor());
				g2.setFont(view.fontLine);
				drawLabel(g2);
            }			
        }
    }

    final public void setStroke(BasicStroke objStroke) {
    	this.objStroke = objStroke;
    }
    
    final public void drawTrace(Graphics2D g2) {
		g2.setPaint(geo.getObjectColor());
		g2.setStroke(objStroke);  
		g2.draw(line);
    }
    
	final public void updatePreview() {		
		isVisible = points.size() == 1;
		if (isVisible) { 
			//	start point
			//Coords coords = ((GeoPointND) points.get(0)).getInhomCoordsInD(2);						   			
			Coords coords = view.getCoordsForView(points.get(0).getInhomCoordsInD(3));						   			
			a = coords.get();			                        
			view.toScreenCoords(a);						
			line.setLine(a[0], a[1], a[0], a[1]);                                   			                                            
		}
	}
	
	Point2D.Double endPoint = new Point2D.Double();

	final public void updateMousePos(double xRW, double yRW) {	
		
		if (isVisible) { 				

			// need these as we don't want rounding when Alt pressed (nearest 15 degrees)
			double xx = view.toScreenCoordX(xRW);
			double yy = view.toScreenCoordY(yRW);
			
			// round angle to nearest 15 degrees if alt pressed
			if (points.size() == 1 && view.getEuclidianController().altDown) {
				//double xRW = view.toRealWorldCoordX(x);
				//double yRW = view.toRealWorldCoordY(y);
				GeoPoint p = (GeoPoint)points.get(0);
				double px = p.inhomX;
				double py = p.inhomY;
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt((py - yRW) * (py - yRW) + (px - xRW) * (px - xRW));
				
				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15; 
				
				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);
				
				endPoint.x = xRW;
				endPoint.y = yRW;
				view.getEuclidianController().setLineEndPoint(endPoint);
				
				
				// don't use view.toScreenCoordX/Y() as we don't want rounding
				xx = view.xZero + xRW * view.xscale;
				yy = view.yZero - yRW * view.yscale;

			}
			else
				view.getEuclidianController().setLineEndPoint(null);

			/*
			a[0] = A.inhomX;
			a[1] = A.inhomY;
			view.toScreenCoords(a);
			*/
			v[0] = xx - a[0];
			v[1] = yy - a[1];
			setClippedLine();                                   			                                            
		}
	}
    
	final public void drawPreview(Graphics2D g2) {
		if (isVisible) {			            
			g2.setPaint(ConstructionDefaults.colPreview);             
			g2.setStroke(objStroke);            
			g2.draw(line);                        		
		}
	}
	
	public void disposePreview() {	
	}
    
	final public boolean hit(int x,int y) {        
        return line.intersects(x-hitThreshold, y-hitThreshold, 2*hitThreshold, 2*hitThreshold);        
    }
	
    final public boolean isInside(Rectangle rect) {
    	return false;  
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
}
