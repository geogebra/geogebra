/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawVector.java
 *
 * Created on 16. Oktober 2001, 15:13
 */

package geogebra.euclidian;

import geogebra.euclidian.clipping.ClipLine;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
public class DrawVector extends Drawable implements Previewable {
   
    private GeoVectorND v;
    private GeoPointND P;
    
    boolean isVisible, labelVisible;
    private boolean traceDrawingNeeded = false;
           	          
    private Line2D.Double line;               
    private double [] coordsA = new double[2];
	private double [] coordsB = new double[2];   
	private double [] coordsV = new double[2]; 
    private GeneralPath gp; // for arrow   
    private boolean arrowheadVisible, lineVisible;
    private ArrayList<GeoPointND> points;
    
    /** Creates new DrawVector */
    public DrawVector(EuclidianView view, GeoVectorND v) {
    	this.view = view;
		this.v = v;
		geo = (GeoElement) v;
    			
		update();
    }
    
	DrawVector(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view;
		this.points = points;
		updatePreview();
	}

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    
        
		updateStrokes((GeoElement) v);
		
		Coords coords;
		
		//start point in real world coords
		P = v.getStartPoint();            		                            
        if (P != null && !P.isInfinite()) {
        	coords = view.getCoordsForView(P.getInhomCoordsInD(3));//P.getCoordsInD(3);
            if (!Kernel.isZero(coords.getZ())){
            	isVisible = false;
            	return;
            }else{
            	coordsA[0] = coords.getX();
            	coordsA[1] = coords.getY();
            }
        } else {
            coordsA[0] = 0;
           	coordsA[1] = 0;			
        }       
        
        // vector
        coords = view.getCoordsForView(v.getCoordsInD(3));//v.getCoordsInD(3);
        if (!Kernel.isZero(coords.getZ())){
        	isVisible = false;
        	return;
        }else{
        	coordsV[0] = coords.getX();
        	coordsV[1] = coords.getY();
        }
        
		// end point 
        coordsB[0] = coordsA[0] + coordsV[0];
        coordsB[1] = coordsA[1] + coordsV[1];
        
        // set line and arrow of vector and converts all coords to screen
		setArrow(((GeoElement) v).lineThickness);
        
		// label position
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();       
			// note that coordsV was normalized in setArrow()
			xLabel = (int) ((coordsA[0] + coordsB[0])/ 2.0 + coordsV[1]);
			yLabel = (int) ((coordsA[1] + coordsB[1])/ 2.0 - coordsV[0]);
			addLabelOffset();   
		}    
		
		if (v == view.getEuclidianController().recordObject)
		    recordToSpreadsheet((GeoElement) v);

		
		// draw trace
		// a vector is a Locateable and it might
		// happen that there are several update() calls
		// before the new trace should be drawn
		// so the actual drawing is moved to draw()
		traceDrawingNeeded = v.getTrace();		
		if (v.getTrace()) {
			isTracing = true;			
		} else {
			if (isTracing) {				
				isTracing = false;
				view.updateBackground();
			}
		}								 	                                
    }

    /**
     * Sets the line and arrow of the vector.
     */
    private void setArrow(float lineThickness) {
    	// screen coords of start and end point of vector
    	boolean onscreenA = view.toScreenCoords(coordsA);
		boolean onscreenB = view.toScreenCoords(coordsB);
        coordsV[0] = coordsB[0] - coordsA[0];
        coordsV[1] = coordsB[1] - coordsA[1];
    	
	      // calculate endpoint F at base of arrow
		  double factor = 12.0 + lineThickness;
		  double length = GeoVec2D.length(coordsV);
		  if (length > 0.0) {
			coordsV[0] = (coordsV[0] * factor) / length; 
			coordsV[1] = (coordsV[1] * factor) / length;
		  }
		  double [] coordsF = new double[2];
		  coordsF[0] = coordsB[0] - coordsV[0];
		  coordsF[1] = coordsB[1] - coordsV[1];
		  
        // set clipped line
		if (line == null) line = new Line2D.Double();
		lineVisible = true;
		if (onscreenA && onscreenB) {
			// A and B on screen
			line.setLine(coordsA[0], coordsA[1], coordsF[0], coordsF[1]);
		} else {
			// A or B off screen
			// clip at screen, that's important for huge coordinates
			// check if any of vector is on-screen
			Point2D.Double [] clippedPoints = 
				ClipLine.getClipped(coordsA[0], coordsA[1], coordsB[0], coordsB[1], -EuclidianView.CLIP_DISTANCE, view.width + EuclidianView.CLIP_DISTANCE, -EuclidianView.CLIP_DISTANCE, view.height + EuclidianView.CLIP_DISTANCE);
			if (clippedPoints == null) {
				isVisible = false;	
				lineVisible = false;
				arrowheadVisible = false;
			} else {
				
				// now re-clip at A and F
				clippedPoints = 
					ClipLine.getClipped(coordsA[0], coordsA[1], coordsF[0], coordsF[1], -EuclidianView.CLIP_DISTANCE, view.width + EuclidianView.CLIP_DISTANCE, -EuclidianView.CLIP_DISTANCE, view.height + EuclidianView.CLIP_DISTANCE);
				if (clippedPoints != null)
					line.setLine(clippedPoints[0].x, clippedPoints[0].y, clippedPoints[1].x, clippedPoints[1].y);
				else 
					lineVisible = false;
			}
		}
		
		// add triangle if visible
		  if (gp == null) 
			 gp = new GeneralPath();
		  else 
			gp.reset();
		  
		  if (isVisible) {

			if (length > 0) {
				  coordsV[0] /= 4.0;
				  coordsV[1] /= 4.0;  
				  
				  gp.moveTo((float) coordsB[0], (float) coordsB[1]); // end point
				  gp.lineTo((float) (coordsF[0] - coordsV[1]), (float)(coordsF[1] + coordsV[0]));
				  gp.lineTo((float)(coordsF[0] + coordsV[1]), (float)(coordsF[1] - coordsV[0]));
				  gp.closePath();	
			}
			
			arrowheadVisible = onscreenB || gp.intersects(0,0, view.width, view.height);
		  }
    }
    
    public void draw(Graphics2D g2) {
        if (isVisible) {
        	if (traceDrawingNeeded) {
        		traceDrawingNeeded = false;
        		Graphics2D g2d = view.getBackgroundGraphics();
    			if (g2d != null) drawTrace(g2d);    			
        	}
        	
            if (geo.doHighlighting()) {
                g2.setPaint(((GeoElement) v).getSelColor());
                g2.setStroke(selStroke);            
                if (lineVisible) g2.draw(line);       
            }
            
            g2.setPaint(((GeoElement) v).getObjectColor());
			g2.setStroke(objStroke);  
			if (lineVisible) g2.draw(line);              
			if (arrowheadVisible) g2.fill(gp);
                                              
            if (labelVisible) {
				g2.setFont(view.fontVector);
				g2.setPaint(((GeoElement) v).getLabelColor());
				drawLabel(g2);
            }            
        }
    }
    
    
	final void drawTrace(Graphics2D g2) {
		g2.setPaint(((GeoElement) v).getObjectColor());
		g2.setStroke(objStroke);  
		if (lineVisible) g2.draw(line);  
		if (arrowheadVisible) g2.fill(gp);       
	}
    
	final public void updatePreview() {		
		isVisible = points.size() == 1;
		if (isVisible) { 
			//	start point
			//GeoPoint P = (GeoPoint) points.get(0);	
			//P.getInhomCoords(coordsA);
			coordsA=view.getCoordsForView(points.get(0).getInhomCoordsInD(3)).get();
			coordsB[0] = coordsA[0];
			coordsB[1] = coordsA[1];
			setArrow(1);                              			                                            
		}
	}
    
	Point2D.Double endPoint = new Point2D.Double();
	
	final public void updateMousePos(double xRW, double yRW) {		
		if (isVisible) {
			//double xRW = view.toRealWorldCoordX(x);
			//double yRW = view.toRealWorldCoordY(y);
			
			// round angle to nearest 15 degrees if alt pressed
			if (points.size() == 1 && view.getEuclidianController().altDown) {
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
			}
			else
				view.getEuclidianController().setLineEndPoint(null);
  
			// set start and end point in real world coords
			//GeoPoint P = (GeoPoint) points.get(0);	
			//P.getInhomCoords(coordsA);
			coordsA=view.getCoordsForView(points.get(0).getInhomCoordsInD(3)).get();
			coordsB[0] = xRW;
			coordsB[1] = yRW;
			setArrow(1);
		}						    	                                 
	}
    
	final public void drawPreview(Graphics2D g2) {
		if (isVisible) {		
			g2.setPaint(ConstructionDefaults.colPreview);
			g2.setStroke(objStroke);  
			if (arrowheadVisible) g2.fill(gp);                                    
			if (lineVisible) g2.draw(line);                                    			      
		}
	}
	
	public void disposePreview() {		
	}
    
	final public boolean hit(int x,int y) {        
        return (lineVisible && line.intersects(x-3, y-3, 6, 6)) || (arrowheadVisible && gp.intersects(x-3, y-3, 6, 6));
    }
	
	final public boolean isInside(Rectangle rect) {  
    	return (lineVisible && rect.contains(line.getBounds())) || (arrowheadVisible && rect.contains(gp.getBounds()));   
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
		Rectangle ret = null;
		if (lineVisible) ret = line.getBounds();
		
		if (arrowheadVisible) ret = (ret == null) ? gp.getBounds() : ret.union(gp.getBounds());
		
		return ret;
	}
}
