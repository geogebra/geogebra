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
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

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
public class DrawSegment extends Drawable
implements Previewable {
   
    private GeoLineND s;
       
    private boolean isVisible, labelVisible;
    private ArrayList<GeoPointND> points;
    
    private Line2D.Double line;               
    private double [] coordsA = new double[2];
	private double [] coordsB = new double[2];
    
	// For drawing ticks
	private Line2D.Double [] decoTicks;	
    
	/** 
	 * Creates new DrawSegment
	 * @param view Euclidian view to be used
	 * @param s Segment to be drawn 
	 */
    public DrawSegment(EuclidianView view, GeoLineND s) {
    	this.view = view;
    	hitThreshold = view.getCapturingThreshold();
    	this.s = s;
    	geo = (GeoElement) s;
    	        
        update();
    }
    
	/**
	 * Creates a new DrawSegment for preview.     
	 * @param view Euclidian view to be used
	 * @param points endpoints of the segment
	 */
	DrawSegment(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view; 
		this.points = points;

		updatePreview();
	} 

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return; 
        labelVisible = geo.isLabelVisible();       
		updateStrokes(geo);
		
        Coords A = view.getCoordsForView(s.getStartInhomCoords());
        Coords B = view.getCoordsForView(s.getEndInhomCoords());
        
        //check if in view
        if (!Kernel.isZero(A.getZ()) || !Kernel.isZero(B.getZ())){
    		isVisible = false;
    		return;
        }
        
        /*
        if (s.getEndPoint().getLabel().equals("S3'"))
        	Application.debug("start=\n"+s.getStartInhomCoords()+"\nA=\n"+A);
        	*/
        
        coordsA[0] = A.getX(); coordsA[1] = A.getY();
        coordsB[0] = B.getX(); coordsB[1] = B.getY();
		
		boolean onscreenA = view.toScreenCoords(coordsA);
		boolean onscreenB = view.toScreenCoords(coordsB);	
		
		if (line == null)
			line = new Line2D.Double();
		
		if (onscreenA && onscreenB) {
			// A and B on screen
			line.setLine(coordsA[0], coordsA[1], coordsB[0], coordsB[1]);
		} else {
			// A or B off screen
			// clip at screen, that's important for huge coordinates
			Point2D.Double [] clippedPoints = 
				ClipLine.getClipped(coordsA[0], coordsA[1], coordsB[0], coordsB[1], -EuclidianView.CLIP_DISTANCE, view.width + EuclidianView.CLIP_DISTANCE, -EuclidianView.CLIP_DISTANCE, view.height + EuclidianView.CLIP_DISTANCE);
			if (clippedPoints == null) {
				isVisible = false;	
			} else {
				line.setLine(clippedPoints[0].x, clippedPoints[0].y, clippedPoints[1].x, clippedPoints[1].y);
			}
		}
		     		    	
		// draw trace
		if (s.getTrace()) {
			isTracing = true;
			Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}			
		
		// if no label and no decoration then we're done
		if (!labelVisible && geo.decorationType == GeoElement.DECORATION_NONE) 
			return;
		
		// calc midpoint (midX, midY) and perpendicular vector (nx, ny)
		double midX = (coordsA[0] + coordsB[0])/ 2.0;
		double midY = (coordsA[1] + coordsB[1])/ 2.0;		
		double nx = coordsA[1] - coordsB[1]; 			
		double ny = coordsB[0] - coordsA[0];		
		double nLength = GeoVec2D.length(nx, ny);			
			
		// label position
        // use unit perpendicular vector to move away from line
        if (labelVisible) {   
        	labelDesc = geo.getLabelDescription();	
        	if (nLength > 0.0) {    		
        		xLabel = (int) (midX + nx * 16 / nLength);
    			yLabel = (int) (midY + ny * 16 / nLength);	
    		} else {
    			xLabel = (int) midX;
    			yLabel = (int) (midY + 16);    			
    		}	        													  			  
			addLabelOffset();        
        }	
        
        // update decoration    		
		//added by Lo�c and Markus BEGIN,
		if (geo.decorationType != GeoElement.DECORATION_NONE && nLength > 0) {	
			if (decoTicks == null) {
				// only create these object when they are really needed
				decoTicks =	new Line2D.Double[6]; // Michael Borcherds 20071006 changed from 3 to 6
				for (int i = 0; i < decoTicks.length; i++)
					decoTicks[i] = new Line2D.Double();
			}
			
			// tick spacing and length.
			double tickSpacing = 2.5 + geo.lineThickness/2d;
			double tickLength =  tickSpacing + 1;	
//			 Michael Borcherds 20071006 start
			double arrowlength = 1.5;
//			 Michael Borcherds 20071006 end
			double vx, vy, factor;
																	
			switch(geo.decorationType){
			case GeoElement.DECORATION_SEGMENT_ONE_TICK:
				// use perpendicular vector to set tick	
				factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - nx, midY - ny,
									 midX + nx, midY + ny);	
				break;
		 	
		 	case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
		 		// vector (vx, vy) to get 2 points around midpoint		
		 		factor = tickSpacing / (2 * nLength);		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
		 		// use perpendicular vector to set ticks			 		
		 		factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX + vx - nx, midY + vy - ny,
									 midX + vx + nx, midY + vy + ny);						
				decoTicks[1].setLine(midX - vx - nx, midY - vy - ny,
						 			 midX - vx + nx, midY - vy + ny);
		 		break;
		 	
		 	case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
		 		// vector (vx, vy) to get 2 points around midpoint				 		
		 		factor = tickSpacing / nLength;		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
		 		// use perpendicular vector to set ticks			 		
		 		factor = tickLength / nLength;
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX + vx - nx, midY + vy - ny,
									 midX + vx + nx, midY + vy + ny);	
				decoTicks[1].setLine(midX - nx, midY - ny,
						 			 midX + nx, midY + ny);
				decoTicks[2].setLine(midX - vx - nx, midY - vy - ny,
			 			 			 midX - vx + nx, midY - vy + ny);
		 		break;
//		 	 Michael Borcherds 20071006 start
			case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
		 		// vector (vx, vy) to get 2 points around midpoint				 		
		 		factor = tickSpacing / (1.5 * nLength);		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
				// use perpendicular vector to set tick	
				factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - arrowlength*vx, midY - arrowlength*vy,
						 midX - arrowlength*vx + arrowlength*(nx + vx), midY - arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[1].setLine(midX - arrowlength*vx, midY - arrowlength*vy,
						 midX - arrowlength*vx + arrowlength*(-nx + vx), midY - arrowlength*vy + arrowlength*(-ny + vy));	
				break;
		 	
		 	case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
		 		// vector (vx, vy) to get 2 points around midpoint		
		 		factor = tickSpacing / (1.5 * nLength);		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
		 		// use perpendicular vector to set ticks			 		
		 		factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - 2*arrowlength*vx, midY - 2*arrowlength*vy,
						 midX - 2*arrowlength*vx + arrowlength*(nx + vx), midY - 2*arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[1].setLine(midX - 2*arrowlength*vx, midY - 2*arrowlength*vy,
						 midX - 2*arrowlength*vx + arrowlength*(-nx + vx), midY - 2*arrowlength*vy + arrowlength*(-ny + vy));	
				
				decoTicks[2].setLine(midX, midY,
						 midX + arrowlength*(nx + vx), midY + arrowlength*(ny + vy));	
				decoTicks[3].setLine(midX, midY,
						 midX + arrowlength*(-nx + vx), midY + arrowlength*(-ny + vy));	
		 		break;
		 	
		 	case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
		 		// vector (vx, vy) to get 2 points around midpoint				 		
		 		factor = tickSpacing / (1.5 * nLength);		
		 		vx = -ny * factor;
		 		vy =  nx * factor;	
		 		// use perpendicular vector to set ticks			 		
		 		factor = tickLength / (1.5 * nLength);
				nx *= factor;
				ny *= factor;
				decoTicks[0].setLine(midX - arrowlength*vx, midY - arrowlength*vy,
						 midX - arrowlength*vx + arrowlength*(nx + vx), midY - arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[1].setLine(midX - arrowlength*vx, midY - arrowlength*vy,
						 midX - arrowlength*vx + arrowlength*(-nx + vx), midY - arrowlength*vy + arrowlength*(-ny + vy));	
				
				decoTicks[2].setLine(midX + arrowlength*vx, midY + arrowlength*vy,
						 midX + arrowlength*vx + arrowlength*(nx + vx), midY + arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[3].setLine(midX + arrowlength*vx, midY + arrowlength*vy,
						 midX + arrowlength*vx + arrowlength*(-nx + vx), midY + arrowlength*vy + arrowlength*(-ny + vy));	
				
				decoTicks[4].setLine(midX - 3*arrowlength*vx, midY - 3*arrowlength*vy,
						 midX - 3*arrowlength*vx + arrowlength*(nx + vx), midY - 3*arrowlength*vy + arrowlength*(ny + vy));	
				decoTicks[5].setLine(midX - 3*arrowlength*vx, midY - 3*arrowlength*vy,
						 midX - 3*arrowlength*vx + arrowlength*(-nx + vx), midY - 3*arrowlength*vy + arrowlength*(-ny + vy));	
		 		break;
//		 	 Michael Borcherds 20071006 end
			}    		    		    		
    	}			                                           
    }
	
   
	final public void draw(Graphics2D g2) {
		// segments of polygons can have zero thickness
		if (geo.lineThickness == 0)
			return;
		
        if (isVisible) {		        	
            if (geo.doHighlighting()) {
                g2.setPaint(geo.getSelColor());
                g2.setStroke(selStroke);            
                g2.draw(line);       
            }
            
            g2.setPaint(geo.getObjectColor());             
            g2.setStroke(objStroke);            
			g2.draw(line);

			//added by Lo�c BEGIN			
			if (geo.decorationType != GeoElement.DECORATION_NONE){
				g2.setStroke(decoStroke);
				
				switch(geo.decorationType){
				case GeoElement.DECORATION_SEGMENT_ONE_TICK:
					g2.draw(decoTicks[0]);
					break;
					
				case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					break;
					
				case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					break;
// Michael Borcherds 20071006 start
				case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					break;
					
				case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					g2.draw(decoTicks[3]);
					break;
					
				case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
					g2.draw(decoTicks[0]);
					g2.draw(decoTicks[1]);
					g2.draw(decoTicks[2]);
					g2.draw(decoTicks[3]);
					g2.draw(decoTicks[4]);
					g2.draw(decoTicks[5]);
					break;
// Michael Borcherds 20071006 end
				}
			}
			//END

			if (labelVisible) {
				g2.setPaint(geo.getLabelColor());
				g2.setFont(view.fontLine);
				drawLabel(g2);
            }
        }
    }
    
	/**
	 * Draw segment's trace
	 * @param g2
	 */
	final void drawTrace(Graphics2D g2) {
		g2.setPaint(geo.getObjectColor());
		g2.setStroke(objStroke);  
		g2.draw(line);
	}
    
	final public void updatePreview() {		
		isVisible = points.size() == 1;
		if (isVisible) { 

			//	start point
			coordsA = view.getCoordsForView(points.get(0).getInhomCoordsInD(3)).get();
			//coordsA = points.get(0).getInhomCoordsInD(2).get();	
			view.toScreenCoords(coordsA);		

			
			if (line == null)
				line = new Line2D.Double();
			line.setLine(coordsA[0], coordsA[1], coordsA[0], coordsA[1]);                                   			                                            
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
				
				mx = view.toScreenCoordX(xRW);
				my = view.toScreenCoordY(yRW);
				
				endPoint.x = xRW;
				endPoint.y = yRW;
				view.getEuclidianController().setLineEndPoint(endPoint);
			}
			else
				view.getEuclidianController().setLineEndPoint(null);
			line.setLine(coordsA[0], coordsA[1], mx, my);                                   			                                            
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
        return line != null && line.intersects(x-hitThreshold, y-hitThreshold, 2*hitThreshold, 2*hitThreshold);        
    }
	
    final public boolean isInside(Rectangle rect) {
    	return line != null && rect.contains(line.getP1()) &&
    			rect.contains(line.getP2());  
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
		if (line == null || !geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		else 
			return line.getBounds();	
	}
    
}
