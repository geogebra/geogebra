/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import geogebra.euclidian.clipping.ClipShape;
import geogebra.kernel.AlgoConicPartCircle;
import geogebra.kernel.AlgoConicPartCircumcircle;
import geogebra.kernel.AlgoSemicircle;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

/**
 *
 * @author  Markus Hohenwarter
 */
public class DrawConicPart extends Drawable
implements Previewable {
   
    private GeoConicPart conicPart;
     
    boolean isVisible, labelVisible;   
    
    private Arc2D.Double arc = new Arc2D.Double();  
    private Shape shape;
    //private GeoVec2D transVec;
    private double [] halfAxes;
    //private GeoVec2D center;
    private int closure;
    
    private static final int DRAW_TYPE_ELLIPSE = 1;
    private static final int DRAW_TYPE_SEGMENT = 2;
    private static final int DRAW_TYPE_RAYS = 3;
    private int draw_type;
    private AffineTransform transform = new AffineTransform();  
    
    // these are needed for degenerate arcs
    private DrawRay drawRay1, drawRay2;
    private DrawSegment drawSegment;
    //private Drawable degDrawable;
    
    private double [] coords = new double[2];
    private GeoPoint tempPoint;
    
    // preview
    private ArrayList prevPoints;
    private GeoPoint [] previewTempPoints;  
    private int previewMode, neededPrevPoints;
    
    public DrawConicPart(EuclidianView view, GeoConicPart conicPart) {
    	this.view = view;
    	hitThreshold = view.getCapturingThreshold();
    	initConicPart(conicPart);
        update();
    }
    
    private void initConicPart(GeoConicPart conicPart) {
    	this.conicPart = conicPart;
    	geo = conicPart;    	

    	//center = conicPart.getTranslationVector();
    	halfAxes = conicPart.getHalfAxes();
    	// arc or sector?
		closure = conicPart.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR ?
				Arc2D.PIE : Arc2D.OPEN;			
    }
    
	/**
	 * Creates a new DrawConicPart for preview.     
	 */
	DrawConicPart(EuclidianView view, int mode, ArrayList points) {
		this.view = view; 
		prevPoints = points;		
		previewMode = mode;	
		
		Construction cons = view.getKernel().getConstruction();
		neededPrevPoints = mode == EuclidianConstants.MODE_SEMICIRCLE ?
				1 : 2;
		previewTempPoints = new GeoPoint[neededPrevPoints+1];
		for (int i=0; i < previewTempPoints.length; i++) {
			previewTempPoints[i] =
				new GeoPoint(cons);			
		}
	
		initPreview();
	} 

	final public void update() {
        isVisible = geo.isEuclidianVisible() && geo.isDefined();
        if (isVisible) { 
			labelVisible = geo.isLabelVisible();       
			updateStrokes(conicPart);
				
			switch (conicPart.getType()) {
				case GeoConic.CONIC_CIRCLE:
			    case GeoConic.CONIC_ELLIPSE:
			    	updateEllipse();
					break;
				
				case GeoConic.CONIC_PARALLEL_LINES:
					updateParallelLines();
					break;
					
				default:
					//Application.debug("DrawConicPart: unsupported conic type: " + conicPart.getType());
					isVisible = false;
					return;
			}
			
	    	// shape on screen?		
			if (shape != null && !shape.intersects(0,0, view.width, view.height)) {				
				isVisible = false;
	        	// don't return here to make sure that getBounds() works for offscreen points too
			}
			
			// draw trace
			if (conicPart.trace) {
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
    }
	
	private void updateEllipse() {	
		draw_type = DRAW_TYPE_ELLIPSE;
		
		// check for huge pixel radius
		double xradius = halfAxes[0] * view.xscale;
		double yradius = halfAxes[1] * view.yscale;
		if (xradius > DrawConic.HUGE_RADIUS || yradius > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}
		
		// set arc
		arc.setArc(-halfAxes[0],-halfAxes[1],
					2*halfAxes[0],2*halfAxes[1],
					-Math.toDegrees(conicPart.getParameterStart()),
					-Math.toDegrees(conicPart.getParameterExtent()),
					closure
					);
			
		// transform to screen coords
		transform.setTransform(view.coordTransform);
		transform.concatenate(conicPart.getAffineTransform()); 
		
        // BIG RADIUS: larger than screen diagonal
        int BIG_RADIUS = view.width + view.height; // > view's diagonal 
		if (xradius < BIG_RADIUS && yradius < BIG_RADIUS) {
			shape = transform.createTransformedShape(arc); 
		} else {
			// clip big arc at screen
	        shape = ClipShape.clipToRect(arc, transform, new Rectangle(-1,-1,view.width+2, view.height+2));
		}  
                
        // label position
        if (labelVisible) {    
        	double midAngle = conicPart.getParameterStart() + conicPart.getParameterExtent()/2.0;
        	coords[0] = halfAxes[0] * Math.cos(midAngle);
        	coords[1] = halfAxes[1] * Math.sin(midAngle);
        	transform.transform(coords, 0, coords, 0, 1);
        	
			labelDesc = geo.getLabelDescription();
				   
			xLabel = (int) (coords[0]) + 6;
			yLabel = (int) (coords[1]) - 6;	  
			addLabelOffset();        
        }
	}
	
	private void updateParallelLines() {
		if (drawSegment == null
				// also needs re-initing when changing Rays <-> Segment
				|| (conicPart.positiveOrientation() && draw_type != DRAW_TYPE_SEGMENT)
				|| (!conicPart.positiveOrientation() && draw_type != DRAW_TYPE_RAYS) ) 
		{ // init
			GeoLine [] lines = conicPart.getLines();
			drawSegment = new DrawSegment(view, lines[0]);
			drawRay1 = new DrawRay(view, lines[0]);
			drawRay2 = new DrawRay(view, lines[1]);
			drawSegment.setGeoElement(conicPart);
			drawRay1.setGeoElement(conicPart);
			drawRay2.setGeoElement(conicPart);
		}
		
		if (conicPart.positiveOrientation()) {
			draw_type = DRAW_TYPE_SEGMENT;
			drawSegment.update();
		} else {
			draw_type = DRAW_TYPE_RAYS;
			drawRay1.update(false); // don't show labels
			drawRay2.update(false);
		}
	}
    
	final public void draw(Graphics2D g2) {
        if (isVisible) {	
        	switch (draw_type) {
        		case DRAW_TYPE_ELLIPSE:
        			fill(g2, shape, false); // fill using default/hatching/image as appropriate
					
		            if (geo.doHighlighting()) {
		                g2.setPaint(geo.getSelColor());
		                g2.setStroke(selStroke);            
		                g2.draw(shape);       
		            }
		            
		            g2.setPaint(geo.getObjectColor());             
		            g2.setStroke(objStroke);            
					g2.draw(shape);            
		                        
		            if (labelVisible) {
						g2.setPaint(geo.getLabelColor());
						g2.setFont(view.fontLine);
						drawLabel(g2);
		            }		
		            break;
        	
        		case DRAW_TYPE_SEGMENT:        
        			drawSegment.draw(g2);
        			break;
        		
        		case DRAW_TYPE_RAYS:  
        			drawRay1.setStroke(objStroke);
        			drawRay2.setStroke(objStroke);
        			drawRay1.draw(g2);
        			drawRay2.draw(g2);
        			break;
        	}
        }
    }
	
	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		
		switch (draw_type) {
			case DRAW_TYPE_ELLIPSE:
				return shape.getBounds();		            
		
			case DRAW_TYPE_SEGMENT:
				return drawSegment.getBounds();			
			
			default:
				return null;
		}	
	}
    
	final void drawTrace(Graphics2D g2) {
		switch (draw_type) {
			case DRAW_TYPE_ELLIPSE:
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);  
				g2.draw(shape);
				break;
			
			case DRAW_TYPE_SEGMENT:
    			drawSegment.drawTrace(g2);
    			break;
    			
			case DRAW_TYPE_RAYS:
    			drawRay1.setStroke(objStroke);
    			drawRay2.setStroke(objStroke);
    			drawRay1.drawTrace(g2);
    			drawRay2.drawTrace(g2);
    			break;
		}
	}
	
	private void initPreview() {
		//	init the conicPart for preview			
		Construction cons = previewTempPoints[0].getConstruction();
		int arcMode;
		switch (previewMode) {
			case EuclidianConstants.MODE_SEMICIRCLE:
				AlgoSemicircle alg = new AlgoSemicircle(cons, 
						previewTempPoints[0], 
						previewTempPoints[1]);
				cons.removeFromConstructionList(alg);				
				initConicPart(alg.getSemicircle());
				break;
			
			case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
				arcMode = previewMode == EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS ?
						GeoConicPart.CONIC_PART_ARC : GeoConicPart.CONIC_PART_SECTOR;
				AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, 
						previewTempPoints[0], 
						previewTempPoints[1], 
						previewTempPoints[2], arcMode);
				cons.removeFromConstructionList(algo);				
				initConicPart(algo.getConicPart());
				break;

			case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
				arcMode = previewMode == EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS ?
						GeoConicPart.CONIC_PART_ARC : GeoConicPart.CONIC_PART_SECTOR;			
				AlgoConicPartCircumcircle algo2 = 
					new AlgoConicPartCircumcircle(cons,
							previewTempPoints[0], 
							previewTempPoints[1], 
							previewTempPoints[2], arcMode);
				cons.removeFromConstructionList(algo2);			
				initConicPart(algo2.getConicPart());
				break;													
		}		
		
		if (conicPart != null) 
			conicPart.setLabelVisible(false);		
	}
    
	final public void updatePreview() {	
		// two selected points + mouse position needed for preview
		isVisible = conicPart != null && prevPoints.size() == neededPrevPoints;		
		if (isVisible) {
			for (int i=0; i < prevPoints.size(); i++) {
				previewTempPoints[i].setCoords((GeoPoint) prevPoints.get(i));					
			}						
			previewTempPoints[0].updateCascade();			
		}					
	}
	
	final public void updateMousePos(double xRW, double yRW) {			
		if (isVisible) {
			//double xRW = view.toRealWorldCoordX(x);
			//double yRW = view.toRealWorldCoordY(y);
			previewTempPoints[previewTempPoints.length-1].setCoords(xRW, yRW, 1.0);
			previewTempPoints[previewTempPoints.length-1].updateCascade();		
			update();
		}
	}
    
	final public void drawPreview(Graphics2D g2) {					           
		draw(g2);                        				
	}
	
	public void disposePreview() {	
		if (conicPart != null) {
			conicPart.remove();
		}
	}
    
	final public boolean hit(int x,int y) { 
		if (!isVisible) return false;
		
		switch (draw_type) {
			case DRAW_TYPE_ELLIPSE:
				if (strokedShape == null) {
        			strokedShape = objStroke.createStrokedShape(shape);
        		}    		
				if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled())
					return shape.intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold); 					
				else
					return strokedShape.intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold); 
				
				/*
				// sector: take shape for hit testing
				if (closure == Arc2D.PIE) {
					return shape.intersects(x-2, y-2, 4, 4) &&
								!shape.contains(x-2, y-2, 4, 4);
				} else {
					if (tempPoint == null) {
		       			 tempPoint = new GeoPoint(conicPart.getConstruction());
		       		}
		       		
		       		double rwX = view.toRealWorldCoordX(x);
		       		double rwY = view.toRealWorldCoordY(y);	       		
		       		double maxError = 4 * view.invXscale; // pixel	
		       		tempPoint.setCoords(rwX, rwY, 1.0);
		       		return conicPart.isOnPath(tempPoint, maxError);	
				} 
				*/           	
			
			case DRAW_TYPE_SEGMENT:
				return drawSegment.hit(x, y);
				
			case DRAW_TYPE_RAYS:
				return drawRay1.hit(x, y) || 
					   drawRay2.hit(x, y);
			
			default:
				return false;
		}
    }
	
	final public boolean isInside(Rectangle rect) {
		switch (draw_type) {
		case DRAW_TYPE_ELLIPSE:
			return rect.contains(shape.getBounds());			  	
		
		case DRAW_TYPE_SEGMENT:
			return drawSegment.isInside(rect);
			
		case DRAW_TYPE_RAYS:
		default:			
			return false;
	}
	}
	
	final public boolean hitLabel(int x, int y) {
		switch (draw_type) {
			case DRAW_TYPE_ELLIPSE:
				return super.hitLabel(x, y);
			
			case DRAW_TYPE_SEGMENT:
				return drawSegment.hitLabel(x, y);
				
			case DRAW_TYPE_RAYS:
				return drawRay1.hitLabel(x, y) || 
						drawRay2.hitLabel(x, y);
			
			default:
				return false;
		}
	}
	
	
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
 
    
}
