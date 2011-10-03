/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawConic.java
 *
 * Created on 16. Oktober 2001, 15:13
 */

package geogebra.euclidian;

import geogebra.euclidian.clipping.ClipShape;
import geogebra.kernel.AlgoCirclePointRadius;
import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.AlgoCircleTwoPoints;
import geogebra.kernel.AlgoEllipseFociPoint;
import geogebra.kernel.AlgoHyperbolaFociPoint;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
final public class DrawConic extends Drawable implements Previewable {        
    
    // plotpoints per quadrant for hyperbola
    private static final int PLOT_POINTS = 32;
    /** maximum number of plot points */
	static final int MAX_PLOT_POINTS = 300;
    /** maximum of pixels for a standard circle radius
    * bigger circles are drawn via Arc2D */ 
    public static final double HUGE_RADIUS = 1E12;  
           
    private GeoConicND conic;
    
    private boolean isVisible, labelVisible;
    private int type;
            
    private double [] labelCoords = new double[2];      
    
    // CONIC_SINGLE_POINT
    private boolean firstPoint = true;
    private GeoPoint  point;
    private DrawPoint drawPoint;
    
    // CONIC_INTERSECTING_LINES
    private boolean firstLines = true;
    private GeoLine [] lines;
    private DrawLine [] drawLines;
    
    // CONIC_CIRCLE
    private boolean firstCircle = true;
    private GeoVec2D midpoint;    
    private Arc2D.Double arc;   
    private GeneralPathClipped arcFiller, gp;
    private RectangularShape circle;
    private double  mx, my, radius, yradius, angSt, angEnd;    
    
    // for ellipse, hyperbola, parabola
    private AffineTransform transform = new AffineTransform();   
    private Shape shape;     
    
        
    // CONIC_ELLIPSE    
    private boolean firstEllipse = true;
    private double [] halfAxes;      
    private Ellipse2D.Double ellipse;    
    
    // CONIC_PARABOLA   
    private boolean firstParabola = true;
    private double x0, y0;
    private double k2;
    private GeoVec2D vertex;
    private QuadCurve2D.Double parabola;    
    private double [] parpoints = new double[6];        
    
    // CONIC_HYPERBOLA   
    private boolean firstHyperbola = true;    
    private double a,b, tsq, step, t, denom;
    private double x, y;
    private int index0, index1, n, points;
    private GeneralPathClipped hypLeft, hypRight;    
    private boolean hypLeftOnScreen, hypRightOnScreen;      
    
    // preview of circle (two points or three points)
	private ArrayList<GeoPointND> prevPoints;
	private ArrayList<GeoSegment> prevSegments;
	private ArrayList<GeoConic> prevConics; 
	private GeoPoint [] previewTempPoints;  
	private GeoNumeric previewTempRadius;
	private int previewMode, neededPrevPoints;
	private boolean isPreview = false;
    
	public Area getShape(){	
		Area a = super.getShape()!=null? 
				super.getShape():(shape==null?new Area():new Area(shape));
		if(conic.isInverseFill()){                    	        	
        	Area b = new Area(view.getBoundingPath());
        	b.subtract(a);
        	return b;
        }
        return a;
	}
    /** Creates new DrawVector 
     * @param view 
     * @param c */
    public DrawConic(EuclidianView view, GeoConicND c) {
    	this.view = view;
    	isPreview = false;
    	hitThreshold = view.getCapturingThreshold();
        initConic(c);
        update();
    }
    
    private void initConic(GeoConicND c) {
    	conic = c;
        geo = c;
                
        vertex = c.getTranslationVector(); // vertex                            
        midpoint = vertex;
        halfAxes = c.getHalfAxes();
        c.getAffineTransform();                              
    }
    
	/**
	 * Creates a new DrawConic for preview of a circle 
	 * @param view 
	 * @param mode 
	 * @param points 
	 */
	DrawConic(EuclidianView view, int mode, ArrayList<GeoPointND> points) {
		this.view = view; 
		prevPoints = points;
		previewMode = mode;	
		
		Construction cons = view.getKernel().getConstruction();
		neededPrevPoints = mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS ?
							1 : 2;
		previewTempPoints = new GeoPoint[neededPrevPoints+1];
		for (int i=0; i < previewTempPoints.length; i++) {
			previewTempPoints[i] = new GeoPoint(cons);			
		}
		
		initPreview();
	} 
	
	/**
	 * Creates a new DrawConic for preview of a compass circle (radius or segment first, then center point) 
	 * @param view 
	 * @param mode 
	 * @param points 
	 * @param segments 
	 * @param conics 
	 */
	DrawConic(EuclidianView view, int mode, ArrayList<GeoPointND> points, ArrayList<GeoSegment> segments, ArrayList<GeoConic> conics) {
		this.view = view; 
		prevPoints = points;
		prevSegments = segments;
		prevConics = conics;
		previewMode = mode;
		
		Construction cons = view.getKernel().getConstruction();
		previewTempRadius = new GeoNumeric(cons);
		previewTempPoints = new GeoPoint[1];
		previewTempPoints[0] = new GeoPoint(cons);
		
		initPreview();
	} 
	

	final public void update() {		
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
        labelVisible = geo.isLabelVisible();
       
        updateStrokes(conic);          
        type = conic.getType();               
        
        switch (type) {
        	case GeoConic.CONIC_EMPTY:
        		setShape(conic.evaluate(0,0)<0?null:new Area(view.getBoundingPath()));
        		shape = null;
            case GeoConic.CONIC_SINGLE_POINT:                
                updateSinglePoint();
                break;       
                
            case GeoConic.CONIC_INTERSECTING_LINES:  
            case GeoConic.CONIC_DOUBLE_LINE: 
            case GeoConic.CONIC_PARALLEL_LINES:
            case GeoConic.CONIC_LINE:
                updateLines();
                break;                             
                
            case GeoConic.CONIC_CIRCLE:                                               
                updateCircle();
                break;                                                    
                
            case GeoConic.CONIC_ELLIPSE:   
                updateEllipse();
                break;
                
            case GeoConic.CONIC_HYPERBOLA:    
                updateHyperbola();
                break;
                
            case GeoConic.CONIC_PARABOLA:
                updateParabola();
                break;                          
        }
        
        if (!isVisible)
        	return;
        
        // shape on screen?
        Rectangle viewRect = new Rectangle(0,0,view.width,view.height);
        switch (type) {	                          	            
	        case GeoConic.CONIC_CIRCLE:                                               
	        case GeoConic.CONIC_ELLIPSE:   
	        case GeoConic.CONIC_PARABOLA:
	        	boolean includesScreenCompletely = shape.contains(viewRect);
	        	
	        	// offScreen = includesScreenCompletely or the shape does not intersect the view rectangle
	        	boolean offScreen =  includesScreenCompletely || !shape.getBounds2D().intersects(viewRect);
	        	if (geo.getAlphaValue() == 0f) {
	        		// no filling
	        		isVisible = !offScreen;
	        	} else {
	        		// filling
	        		if (includesScreenCompletely) {
	        			isVisible = true;
	        		} else {
	        			isVisible = !offScreen;
	        		}
	        	}	        	
	        	break;
	            
	        case GeoConic.CONIC_HYPERBOLA:
	        	// hyperbola wings on screen?
	        	hypLeftOnScreen = hypLeft.intersects(viewRect);
	        	hypRightOnScreen = hypRight.intersects(viewRect);
	        	if (!hypLeftOnScreen && !hypRightOnScreen) {
	        		isVisible = false;
	        	}	            
	            break;            
        }
        
        if (!isVisible)
        	return;
        
		// draw trace
		if (conic.trace) {
			isTracing = true;
			Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}	
        
        if (labelVisible) {
        	labelDesc = geo.getLabelDescription();
			addLabelOffset();
        }
    }
    
    final private void updateSinglePoint() {
    	//we want to determine the sign of the result but we can't use fixed point
    	//as it may be equal to the single point. Point (b.x+1,0) differs in one coord.
    	setShape(conic.evaluate(conic.b.x+1,0)<0?null:new Area(view.getBoundingPath()));
    	shape = null;
    	if (conic.isGeoElement3D()){//TODO implement for 3D conics
    		isVisible=false;
    		return;
    	}
    	
        if (firstPoint) {
            firstPoint = false;
            point = conic.getSinglePoint();
            if (point == null)
    			point = new GeoPoint(conic.getConstruction());
    			point.setCoords(conic.b.x, conic.b.y, 1.0d);
            drawPoint = new DrawPoint(view, point,isPreview);                
            drawPoint.setGeoElement(conic);
            //drawPoint.font = view.fontConic;            
        }
        point.copyLabel(conic);	
        point.setObjColor(conic.getObjectColor());
        point.setLabelColor(conic.getLabelColor());
        point.pointSize = conic.lineThickness;
        
        drawPoint.update();   
    }
    /**
     * Updates the lines and shape so that positive part is colored
     */
    final private void updateLines() {
    	
    	if (conic.isGeoElement3D()){//TODO implement for 3D conics
    		isVisible=false;
    		return;
    	}
    	
        if (firstLines) {
            firstLines = false;
            lines = conic.getLines();
            drawLines = new DrawLine[2];
            drawLines[0] = new DrawLine(view, lines[0]);
            drawLines[1] = new DrawLine(view, lines[1]);                    
            drawLines[0].setGeoElement(geo);
            drawLines[1].setGeoElement(geo);
            //drawLines[0].font = view.fontConic;
            //drawLines[1].font = view.fontConic;            
        }
        for (int i=0; i < 2; i++) {
			drawLines[i].forceLineType(conic.lineType);		
			drawLines[i].update();			
        }
        
        if(conic.type == GeoConic.CONIC_PARALLEL_LINES||
        		conic.type == GeoConic.CONIC_INTERSECTING_LINES
        		|| conic.type == GeoConic.CONIC_LINE){
        	
	       shape = lineToGpc(drawLines[0]);
	       if(conic.type != GeoConic.CONIC_LINE)
	    	   ((Area)shape).exclusiveOr(lineToGpc(drawLines[1]));
	       //FIXME: buggy when conic(RW(0),RW(0))=0
	       
	       	       
	       if(negativeColored() ){
	    	   Area b = new Area(view.getBoundingPath());
	    	   b.subtract((Area)shape);
	    	   shape = b;
	       
	       }
	       
        }
         
    }
    
    private boolean negativeColored() {
    	double[] xTry = new double[] {0,10,20,0,10,20};
	       double[] yTry = new double[] {0,0,0,10,10,20};	       
	       for(int i=0;i<6;i++){
	    	   double val1=conic.evaluate(view.toRealWorldCoordX(xTry[i]), view.toRealWorldCoordY(yTry[i]));
	    	   if(!Kernel.isZero(val1))
	    	    return (val1<0)^shape.contains(xTry[i], yTry[i]);
	       }
		return false;
	}
	private Area lineToGpc(DrawLine drawLine) {
    	GeneralPathClipped gpc = new GeneralPathClipped(view);
    	 boolean invert = false;
        if(drawLine.x1 > drawLine.x2){
        	double t = drawLine.x1;
        	drawLine.x1=drawLine.x2;
        	drawLine.x2=t;
        	t = drawLine.y1;
        	drawLine.y1=drawLine.y2;
        	drawLine.y2=t;
        }
        gpc.moveTo(drawLine.x1,drawLine.y1);
        gpc.lineTo(drawLine.x2,drawLine.y2);
    	//cross top and bottom
    	if(drawLine.x1>0 && drawLine.x2<=view.width){
    		Application.debug("top-bot");
        	if(drawLines[0].y2<drawLine.y1){
        		gpc.lineTo(0, 0);
        		gpc.lineTo(0, view.height);
        	}
        	else{
        		gpc.lineTo(0, view.height);
        		gpc.lineTo(0, 0);
        	}
    	}
    	//cross top/bottom and right
    	else if(drawLine.x1>0 && drawLine.x2>view.width){    		
        		gpc.lineTo(view.width,drawLine.y1);
        		invert = true;
        	}
    	//cros left and bottom/top
    	else if(drawLine.x1<=0 && drawLine.x2<=view.width){    		
    		gpc.lineTo(0,drawLine.y2);
    		invert = drawLine.y2 >0;
    	}
    	//cross left and right
        	else{        		           		
        		gpc.lineTo(view.width, 0);
        		gpc.lineTo(0, 0);
        		
        	}
    	gpc.closePath();
    	Area a = new Area(gpc);
    	if(!invert)
    		return a;
    	Area b= new Area(view.getBoundingPath());
    	b.subtract(a);
    	return b;
    	
	}
	final private void updateCircle() {
		setShape(null);
        // calc screen pixel of radius                        
        radius =  halfAxes[0] * view.xscale;
        yradius =  halfAxes[1] * view.yscale; // radius scaled in y direction
		if (radius > DrawConic.HUGE_RADIUS || yradius > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}        
		
        if (firstCircle) {
            firstCircle = false;  
            arc = new Arc2D.Double();     
            if (ellipse == null) ellipse = new Ellipse2D.Double();
        }        
        
        int i = -1; // bugfix
        
        // if circle is very big, draw arc: this is very important
        // for graphical continuity
        
        // BIG RADIUS: larger than screen diagonal
        int BIG_RADIUS = view.width + view.height; // > view's diagonal 
        if (radius < BIG_RADIUS && yradius < BIG_RADIUS) {              
            circle = ellipse;
            arcFiller = null;         
            // calc screen coords of midpoint
            Coords M;
            if (isPreview) //midpoint has been calculated in view coords
            	M = conic.getMidpoint3D().getInhomCoords();
            else{
            	M = view.getCoordsForView(conic.getMidpoint3D());            
            	if (!Kernel.isZero(M.getZ())){//check if in view
            		isVisible = false;
            		return;
            	}
            	//check if eigen vec are in view
            	for(int j=0; j<2; j++){
            		Coords ev = view.getCoordsForView(conic.getEigenvec3D(j));   
            		if (!Kernel.isZero(ev.getZ())){//check if in view
            			isVisible = false;
            			return;
            		}
            	}
            }
            mx =  M.getX() * view.xscale + view.xZero;
            my = -M.getY() * view.yscale + view.yZero;   
            ellipse.setFrame(mx-radius, my-yradius, 2.0*radius, 2.0*yradius);                                                      
        } else {            
        // special case: really big circle
        // draw arc according to midpoint position        	        	        	
        	// of the arc
        	Coords M = view.getCoordsForView(conic.getMidpoint3D());
            if (!Kernel.isZero(M.getZ())){//check if in view
        		isVisible = false;
        		return;
            }
            //check if eigen vec are in view
            for(int j=0; j<2; j++){
            	Coords ev = view.getCoordsForView(conic.getEigenvec3D(j));   
                if (!Kernel.isZero(ev.getZ())){//check if in view
            		isVisible = false;
            		return;
                }
            }
            mx =  M.getX() * view.xscale + view.xZero;
            my = -M.getY() * view.yscale + view.yZero;   
        	
            angSt = Double.NaN;            
            // left 
            if (mx < 0.0) { 
                // top
                if (my < 0.0) {                
                   angSt  = -Math.acos(-mx / radius);                    
                   angEnd = -Math.asin(-my / yradius);	
                   i = 0;			                      
                }
                // bottom
                else if (my > view.height) {                    
                    angSt  =  Math.asin((my - view.height) / yradius);
                    angEnd =  Math.acos(-mx / radius);
					i = 2;    
                }
                // middle
                else {                    
                    angSt  = -Math.asin((view.height - my) / yradius);
                    angEnd =  Math.asin(my / yradius);
					i = 1;                    
                }                                
            }
            // right 
            else if (mx > view.width) {                 
                // top
                if (my < 0.0) {                    
                    angSt  = Math.PI + Math.asin(-my / yradius);
                    angEnd = Math.PI + Math.acos((mx - view.width) / radius);
					i = 6;
                }
                // bottom
                else if (my > view.height) {                    
                    angSt  = Math.PI - Math.acos((mx - view.width) / radius);
                    angEnd = Math.PI - Math.asin((my - view.height) / yradius);
					i = 4;
                }
                // middle
                else {                    
                    angSt  = Math.PI - Math.asin(my / yradius);
                    angEnd = Math.PI + Math.asin((view.height - my) / yradius);
					i = 5;
                }                                                
            }
            // top middle
            else if (my < 0.0) {                                 
                angSt  = Math.PI + Math.acos(mx / radius);
                angEnd = 2*Math.PI - Math.acos((view.width - mx) / radius);
				i = 7;
            }
            // bottom middle
            else if (my > view.height) {                                 
                angSt  = Math.acos((view.width - mx) / radius);
                angEnd = Math.PI - Math.acos(mx / radius);
				i = 3;
            }      
            // center on screen 
            else {                
            	// huge circle with center on screen: use screen rectangle instead of circle for possible filling
            	shape = circle = new Rectangle(-1,-1,view.width+2, view.height+2);
                arcFiller = null; 
                xLabel = -100;                
                yLabel = -100;  
                return;
            }            
            
            if (Double.isNaN(angSt) || Double.isNaN(angEnd)) {                
                 // to ensure drawing ...                
                angSt = 0.0d;
                angEnd = 2*Math.PI;
            }                                   

            // set arc
            circle = arc;   
            arc.setArc(mx - radius, my - yradius, 2.0*radius, 2.0*yradius,
                   Math.toDegrees(angSt), Math.toDegrees(angEnd - angSt), 
				   Arc2D.OPEN);
                    
            // set general path for filling the arc to screen borders
			if (conic.getAlphaValue() > 0.0f || conic.isHatchingEnabled()) {
				if (gp == null) gp = new GeneralPathClipped(view);
				else gp.reset();
				Point2D sp = arc.getStartPoint();
				Point2D ep = arc.getEndPoint();
				
				switch (i) { // case number
					case 0: // left top
						gp.moveTo(0,0);
						gp.lineTo(sp.getX(), sp.getY());
						gp.lineTo(ep.getX(), ep.getY());						
					break;
					
					case 1: // left middle
						gp.moveTo(0,view.height);
						gp.lineTo(sp.getX(), sp.getY());
						gp.lineTo(ep.getX(), ep.getY());
						gp.lineTo(0,0);						
					break;
					
					case 2: // left bottom
						gp.moveTo(0,view.height);
						gp.lineTo(sp.getX(), sp.getY());
						gp.lineTo(ep.getX(), ep.getY());												
					break;
					
					case 3: // middle bottom
						gp.moveTo(view.width, view.height);
						gp.lineTo(sp.getX(), sp.getY());
						gp.lineTo(ep.getX(), ep.getY());
						gp.lineTo(0, view.height);						
					break;

					case 4: // right bottom
						gp.moveTo(view.width, view.height);
						gp.lineTo(sp.getX(), sp.getY());
						gp.lineTo(ep.getX(), ep.getY());											
					break;

					case 5: // right middle
						gp.moveTo(view.width, 0);
						gp.lineTo(sp.getX(), sp.getY());
						gp.lineTo(ep.getX(), ep.getY());
						gp.lineTo(view.width, view.height);											
					break;

					case 6: // right top
						gp.moveTo(view.width, 0);
						gp.lineTo(sp.getX(), sp.getY());
						gp.lineTo(ep.getX(), ep.getY());																
					break;

					case 7: // top middle
						gp.moveTo(0, 0);
						gp.lineTo(sp.getX(), sp.getY());
						gp.lineTo(ep.getX(), ep.getY());
						gp.lineTo(view.width, 0);											
					break;
					
					default:
					gp = null;
				}
				//gp.
				arcFiller = gp;
			}                                                              
        }                                        
		shape = circle;  
        
        // set label position
        xLabel = (int) (mx - radius/2.0) ;                
        yLabel = (int) (my - yradius * 0.85) + 20;  
    }        
    
    final private void updateEllipse() {
    	setShape(null);
		// check for huge pixel radius
		double xradius = halfAxes[0] * view.xscale;
		double yradius = halfAxes[1] * view.yscale;
		if (xradius > DrawConic.HUGE_RADIUS || yradius > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}
		
		//check if in view
        Coords M = view.getCoordsForView(conic.getMidpoint3D());            
        if (!Kernel.isZero(M.getZ())){//check if in view
    		isVisible = false;
    		return;
        }       
        Coords[] ev = new Coords[2];
        for(int j=0; j<2; j++){
        	ev[j] = view.getCoordsForView(conic.getEigenvec3D(j));   
            if (!Kernel.isZero(ev[j].getZ())){//check if in view
        		isVisible = false;
        		return;
            }
        }
        
        
		
        if (firstEllipse) {
            firstEllipse = false;
            if (ellipse == null) ellipse = new Ellipse2D.Double();               
        }
	       
		//	set transform
		transform.setTransform(view.coordTransform);
		transform.concatenate(view.getTransform(conic,M,ev));  
        
        // set ellipse
        ellipse.setFrameFromCenter(0, 0, halfAxes[0], halfAxes[1]); 
       
        // BIG RADIUS: larger than screen diagonal
        int BIG_RADIUS = view.width + view.height; // > view's diagonal 
		if (xradius < BIG_RADIUS && yradius < BIG_RADIUS) {
			shape = transform.createTransformedShape(ellipse); 
		} else {
			// clip big arc at screen
	        shape = ClipShape.clipToRect(ellipse, transform, new Rectangle(-1,-1,view.width+2, view.height+2));
	        
		}		
        // set label coords
        labelCoords[0] = -halfAxes[0] / 2.0d;
        labelCoords[1] = halfAxes[1] * 0.85d - 20.0/view.yscale;                                
        transform.transform(labelCoords, 0, labelCoords, 0, 1);
        xLabel = (int) labelCoords[0];
        yLabel = (int) labelCoords[1];        
    }
    
    final private void updateHyperbola() {  
    	

		//check if in view
        Coords M = view.getCoordsForView(conic.getMidpoint3D());            
        if (!Kernel.isZero(M.getZ())){//check if in view
    		isVisible = false;
    		return;
        }       
        Coords[] ev = new Coords[2];
        for(int j=0; j<2; j++){
        	ev[j] = view.getCoordsForView(conic.getEigenvec3D(j));   
            if (!Kernel.isZero(ev[j].getZ())){//check if in view
        		isVisible = false;
        		return;
            }
        }
        
    	
        if (firstHyperbola) {                                       
            firstHyperbola = false;
            points = PLOT_POINTS;
            hypRight = new GeneralPathClipped(view); // right wing
            hypLeft  = new GeneralPathClipped(view); // left wing            
            
        }     
        else {
        	hypRight.reset();
        	hypLeft.reset();
        }
	
        a = halfAxes[0];
		b = halfAxes[1];
			
		// draw hyperbola wing from x=a to x=x0                
		// the drawn hyperbola must be larger than the screen
		// get max distance from midpoint to screen edge
		x0 = Math.max(
			   Math.max( Math.abs(midpoint.x - view.xmin),
						 Math.abs(midpoint.x - view.xmax)),
			   Math.max( Math.abs(midpoint.y - view.ymin),
						 Math.abs(midpoint.y - view.ymax) )
			 );           
		// ensure that rotated hyperbola is fully on screen:   			         
		x0 *= 1.5; 
	
		//		init step width
		if (x0 <= a) { // hyperbola is not visible on screen		
			isVisible = false;
			return;	
		}  
		
		// set number of plot points according to size of x0
		// add ten points per screen width
		n = PLOT_POINTS + 
		   (int) (Math.abs(x0 - a) / (view.xmax - view.xmin)) * 10;		
		
		if (points != n) {				
			points = Math.min(n, MAX_PLOT_POINTS);  
		}		
				
		 // hyperbola is visible on screen	
		 step = Math.sqrt((x0 - a) / (x0 + a)) / (points - 1);		               		    
		 
	

        // build Polyline of parametric hyperbola
        // hyp(t) = 1/(1-t^2) {a(1+t^2), 2bt}, 0 <= t < 1
        // this represents the first quadrant's wing of a hypberola                                                
        hypRight.addPoint(points-1, a, 0);
        hypLeft.addPoint(points-1, -a, 0);
  
  		t = step;
  		int i = 1;
        index0 = points;    // points ... 2*points - 2
        index1 = points-2;  // points-2 ... 0
        while (index1 >= 0) {			        	
            tsq   = t * t;
            denom = 1.0 - tsq;
            // calc coords of first quadrant
            x = (a * (1.0 + tsq) / denom);
            y = (2.0 * b * t / denom);                              
			
            // first quadrant
            hypRight.addPoint(index0, x, y); 
            // second quadrant  
            hypLeft.addPoint(index0, -x, y);                   
            // third quadrant
            hypLeft.addPoint(index1, -x, -y);  
            // fourth quadrant
            hypRight.addPoint(index1, x, -y);
            
			index0++;
			index1--;
			i++;
			t = i * step;                                                            
        }     
        
        // we have drawn the hyperbola from x=a to x=x0
        // ensure correct filling by adding points at (2*x0, y)
        if (conic.getAlphaValue() > 0.0f || conic.isHatchingEnabled()) {
	        hypRight.lineTo(Float.MAX_VALUE, y);
	        hypRight.lineTo(Float.MAX_VALUE, -y);
	        hypLeft.lineTo(-Float.MAX_VALUE, y);
	        hypLeft.lineTo(-Float.MAX_VALUE, -y);
        }
        
        // set transform for Graphics2D 
        transform.setTransform(view.coordTransform);
		transform.concatenate(view.getTransform(conic,M,ev));
		
        // build general paths of hyperbola wings and transform them
		hypLeft.transform(transform);
		hypRight.transform(transform); 

        // set label coords
        labelCoords[0] = 2.0 * a; 
        // point on curve: y = b * sqrt(3) minus 20 pixels
        labelCoords[1] = b * 1.7 - 20.0/view.yscale;  
        transform.transform(labelCoords, 0, labelCoords, 0, 1);
        xLabel = (int) labelCoords[0];
        yLabel = (int) labelCoords[1];
        setShape(new Area(hypLeft));
        super.getShape().add(new Area(hypRight));
    }
    
    final private void updateParabola() {
		if (conic.p > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}
		


		//check if in view
        Coords M = view.getCoordsForView(conic.getMidpoint3D());            
        if (!Kernel.isZero(M.getZ())){//check if in view
    		isVisible = false;
    		return;
        }       
        Coords[] ev = new Coords[2];
        for(int j=0; j<2; j++){
        	ev[j] = view.getCoordsForView(conic.getEigenvec3D(j));   
            if (!Kernel.isZero(ev[j].getZ())){//check if in view
        		isVisible = false;
        		return;
            }
        }
        
    	
        if (firstParabola) {                                      
            firstParabola = false;
            parabola = new QuadCurve2D.Double();                    
        }                
        // calc control points coords of parabola y^2 = 2 p x                
        x0 = Math.max( Math.abs(vertex.x - view.xmin),
                       		   Math.abs(vertex.x - view.xmax) );                                      
        x0 = Math.max(x0, Math.abs(vertex.y - view.ymin));
		x0 = Math.max(x0, Math.abs(vertex.y - view.ymax));
		
        /*
        x0 *= 2.0d;
        // y^2 = 2px
        y0 = Math.sqrt(2*c.p*x0);
        */
        
        // avoid sqrt by choosing x = k*p with         
        // i = 2*k is quadratic number
        // make parabola big enough: k*p >= 2*x0 -> 2*k >= 4*x0/p
        x0 = 4*x0/conic.p;
        
        // changed these to doubles, see #654 y=x^2+100000x+1
        double i = 4; 
        k2 = 16;
        
        while (k2 < x0) {
            i += 2;
            k2 = i * i;
        }
        x0 = k2/2 * conic.p; // x = k*p
        y0 = i * conic.p;    // y = sqrt(2k p^2) = i p                
        
		//	set transform
		transform.setTransform(view.coordTransform);
		transform.concatenate(view.getTransform(conic,M,ev));
         
        // setCurve(P0, P1, P2)    
        //parabola.setCurve(x0, y0, -x0, 0.0, x0, -y0);  
        //shape = transform.createTransformedShape(parabola);
        parpoints[0] = x0;
        parpoints[1] = y0;
        parpoints[2] = -x0;
        parpoints[3] = 0.0;
        parpoints[4] = x0;
        parpoints[5] = -y0;
        transform.transform(parpoints, 0, parpoints, 0, 3);
        parabola.setCurve(parpoints, 0);
        shape = parabola;                     

        // set label coords
        labelCoords[0] = 2 * conic.p; 
        // y = 2p minus 20 pixels
        labelCoords[1] = labelCoords[0] - 20.0/view.yscale;                                                         
        transform.transform(labelCoords, 0, labelCoords, 0, 1);
        xLabel = (int) labelCoords[0];
        yLabel = (int) labelCoords[1];
    }
        
    
	final public void draw(Graphics2D g2) {
        if (!isVisible) return;                
        g2.setColor(geo.getObjectColor());        		
        switch (type) {
            case GeoConic.CONIC_SINGLE_POINT:                         
                drawPoint.draw(g2);
                break;     
                
            case GeoConic.CONIC_INTERSECTING_LINES:
            case GeoConic.CONIC_DOUBLE_LINE: 
            case GeoConic.CONIC_PARALLEL_LINES:
                drawLines[0].draw(g2);
                drawLines[1].draw(g2);                
                if(conic.isInverseFill()){                    	                	
                	fill(g2, getShape(), false);
                }
                else fill(g2, shape, false);
                break;             
                
            case GeoConic.CONIC_LINE:
                drawLines[0].draw(g2);
                break;             
                
            case GeoConic.CONIC_CIRCLE:                                                                                 
            case GeoConic.CONIC_ELLIPSE:                                
			case GeoConic.CONIC_PARABOLA: 	
                    if(conic.isInverseFill()){                    	
                    	fill(g2, getShape(), false);
                    }
                    else {                    	
                    	fill(g2, shape, false); // fill using default/hatching/image as appropriate
                    }
					if (arcFiller != null )
						fill(g2, arcFiller, true); // fill using default/hatching/image as appropriate

					
                if (geo.doHighlighting()) {
                    g2.setStroke(selStroke);
                    g2.setColor(geo.getSelColor());
                    g2.draw(shape);		
                }                  
                g2.setStroke(objStroke);
                g2.setColor(geo.getObjectColor());				
                g2.draw(shape);    
                if (labelVisible) {
					g2.setFont(view.fontConic); 
					g2.setColor(geo.getLabelColor());                   
					drawLabel(g2);                                                               
                }                
                break;            
            
           case GeoConic.CONIC_HYPERBOLA:               		          
        	   if(conic.isInverseFill()){                    	
	               	Area a1 = new Area(hypLeft);
	               	Area a2 = new Area(hypRight);
	               	Area b = new Area(view.getBoundingPath());
	               	b.subtract(a1);
	               	b.subtract(a2);
	               	fill(g2, b, false);
               }
               else {
				if (hypLeftOnScreen) fill(g2, hypLeft, true);                                            
				if (hypRightOnScreen) fill(g2, hypRight, true);
               }
	
				if (geo.doHighlighting()) {
					 g2.setStroke(selStroke);
					 g2.setColor(geo.getSelColor());
					 
					 if (hypLeftOnScreen) Drawable.drawWithValueStrokePure(hypLeft, g2);                                               
					 if (hypRightOnScreen) Drawable.drawWithValueStrokePure(hypRight, g2); 				
				 }  
				 g2.setStroke(objStroke);
				 g2.setColor(geo.getObjectColor());				 
				 if (hypLeftOnScreen) Drawable.drawWithValueStrokePure(hypLeft, g2);                                                
				 if (hypRightOnScreen) Drawable.drawWithValueStrokePure(hypRight, g2); 
				             
				 if (labelVisible) {
					 g2.setFont(view.fontConic); 
					 g2.setColor(geo.getLabelColor());                   
					 drawLabel(g2);                                                                     
				 }                            
                break;      
        }
    }
	
	/**
	 * Returns the bounding box of this Drawable in screen coordinates. 
	 * @return null when this Drawable is infinite or undefined	 
	 */
	final public Rectangle getBounds() {	
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		
		switch (type) {
	        case GeoConic.CONIC_SINGLE_POINT:                         
	            return drawPoint.getBounds();              
	                                         
	        case GeoConic.CONIC_CIRCLE:  
	        case GeoConic.CONIC_ELLIPSE:     
	        	return shape.getBounds();
	        	
	        default:
	        	return null;        
        }		
	}
    
	/**
	 * Draw trace of the conic
	 * @param g2
	 */
	final public void drawTrace(Graphics2D g2) {             
	    g2.setColor(conic.getObjectColor());
		switch (type) {
			case GeoConic.CONIC_SINGLE_POINT:                         
				drawPoint.drawTrace(g2);
				break;     
                
			case GeoConic.CONIC_INTERSECTING_LINES:
			case GeoConic.CONIC_DOUBLE_LINE: 
			case GeoConic.CONIC_PARALLEL_LINES:
				drawLines[0].drawTrace(g2);
				drawLines[1].drawTrace(g2);
				break;             
                
			case GeoConic.CONIC_LINE:
				drawLines[0].drawTrace(g2);
				break;             
                
			case GeoConic.CONIC_CIRCLE:                                                                                 
			case GeoConic.CONIC_ELLIPSE:                                
			case GeoConic.CONIC_PARABOLA: 			                                                  
				g2.setStroke(objStroke);
				g2.setColor(conic.getObjectColor());				
				g2.draw(shape);    				            
				break;            
            
		   case GeoConic.CONIC_HYPERBOLA:     
				 g2.setStroke(objStroke);
				 g2.setColor(conic.getObjectColor());				 
				 g2.draw(hypLeft);                                                
				 g2.draw(hypRight); 				  
				break;      
		}
	}
    
	final public boolean hit(int x, int y) {   
		if (!isVisible)
			return false;
		//set a flag that says if the point is on the filling
		boolean isOnFilling = false;
		if ((geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) 
				&& type != GeoConic.CONIC_SINGLE_POINT && type != GeoConic.CONIC_DOUBLE_LINE){
			double realX = view.toRealWorldCoordX(x);
			double realY = view.toRealWorldCoordY(y);
			double x3 = view.toRealWorldCoordX(3)-view.toRealWorldCoordX(0);
			double y3 = view.toRealWorldCoordY(3)-view.toRealWorldCoordY(0);
			int insideNeigbors =
			(conic.isInRegion(realX,realY) ? 1:0) +	
			(conic.isInRegion(realX-x3,realY-y3) ? 1:0) +
			(conic.isInRegion(realX+x3,realY-y3) ? 1:0) +
			(conic.isInRegion(realX-x3,realY+y3) ? 1:0) +
			(conic.isInRegion(realX+x3,realY+y3) ? 1:0);
			if(conic.isInverseFill())
				isOnFilling = (insideNeigbors < 5);
			else
				isOnFilling = (insideNeigbors > 0);
		}
		//set a flag to say if point is on the boundary
		boolean isOnBoundary = false;
        switch (type) {
            case GeoConic.CONIC_SINGLE_POINT:                         
                isOnBoundary = drawPoint.hit(x, y);                                
                break;
            case GeoConic.CONIC_INTERSECTING_LINES:  
            case GeoConic.CONIC_DOUBLE_LINE: 
            case GeoConic.CONIC_PARALLEL_LINES:                
            	isOnBoundary = drawLines[0].hit(x, y) || drawLines[1].hit(x, y);
            	break;                              
            case GeoConic.CONIC_LINE:                
            	isOnBoundary = drawLines[0].hit(x, y);
            	break;                               
            case GeoConic.CONIC_CIRCLE:  
            case GeoConic.CONIC_ELLIPSE:
            case GeoConic.CONIC_PARABOLA:
            	if (strokedShape == null) {
        			strokedShape = objStroke.createStrokedShape(shape);
        		}    		
            	isOnBoundary = strokedShape.intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold);            	
            	break;
            case GeoConic.CONIC_HYPERBOLA: 
            	if (strokedShape == null) {
        			strokedShape = objStroke.createStrokedShape(hypLeft);
        			strokedShape2 = objStroke.createStrokedShape(hypRight);
        		}    		
            	isOnBoundary = strokedShape.intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold) || strokedShape2.intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold);              	                                                                                                          
            	break;
        }   
        
        
        //Application.debug("isOnFilling="+isOnFilling+"\nisOnBoundary="+isOnBoundary);
        if (isOnFilling){
        	if (isOnBoundary){
        		conic.setLastHitType(GeoConicND.HIT_TYPE_ON_BOUNDARY);
        		return true;
        	}else{
        		conic.setLastHitType(GeoConicND.HIT_TYPE_ON_FILLING);
        		return true;
        	}
        }else{
        	if (isOnBoundary){
        		conic.setLastHitType(GeoConicND.HIT_TYPE_ON_BOUNDARY);
        		return true;
        	}else{
        		conic.setLastHitType(GeoConicND.HIT_TYPE_NONE);
        		return false;
        	}
        }
    }
	
	
	final public boolean isInside(Rectangle rect) {				
		switch (type) {
           case GeoConic.CONIC_SINGLE_POINT:                         
               return drawPoint.isInside(rect);                          
                                               
           case GeoConic.CONIC_CIRCLE:  
           case GeoConic.CONIC_ELLIPSE:                         	
        	   return rect != null && rect.contains(shape.getBounds());
		}        
		
	    return false;
	}

    public GeoElement getGeoElement() {
        return geo;
    }        
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
        if(drawLines != null)
        	for(int i=0; i<2 && drawLines[i]!=null; i++)
        		drawLines[i].setGeoElement(geo);
    }
    
    private void initPreview() {
		//	init the conic for preview			    	
		Construction cons = previewTempPoints[0].getConstruction();
		isPreview = true;
		
		switch (previewMode) {			
			case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:			
				AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, 
						previewTempPoints[0], 
						previewTempPoints[1]);
				cons.removeFromConstructionList(algo);				
				initConic(algo.getCircle());
				break;
			
			case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
				AlgoCircleThreePoints algo2 = new AlgoCircleThreePoints(cons, 
						previewTempPoints[0], 
						previewTempPoints[1],
						previewTempPoints[2]);
				cons.removeFromConstructionList(algo2);				
				initConic((GeoConic) algo2.getCircle());
				break;		

			case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
				AlgoEllipseFociPoint algo3 = new AlgoEllipseFociPoint(cons,
						previewTempPoints[0], 
						previewTempPoints[1],
						previewTempPoints[2]);
				cons.removeFromConstructionList(algo3);				
				initConic(algo3.getEllipse());
				break;												
				
			case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
				AlgoHyperbolaFociPoint algo4 = new AlgoHyperbolaFociPoint(cons,
						previewTempPoints[0], 
						previewTempPoints[1],
						previewTempPoints[2]);
				cons.removeFromConstructionList(algo4);				
				initConic(algo4.getHyperbola());
				break;	
				
			case EuclidianConstants.MODE_COMPASSES:			
				AlgoCirclePointRadius algo5 = new AlgoCirclePointRadius(cons, 
						previewTempPoints[0], 
						previewTempRadius);
				cons.removeFromConstructionList(algo5);				
				initConic(algo5.getCircle());
				break;
		}		
		
		if (conic != null) 
			conic.setLabelVisible(false);		
	}
    
    // preview of circle with midpoint through a second point
	final public void updatePreview() {		
		// compass: set radius of preview circle
		if (previewMode == EuclidianConstants.MODE_COMPASSES) {
			// two points or one segment selected to define radius
			isVisible = conic != null && (prevPoints.size() == 2 || prevSegments.size() == 1 || prevConics.size() == 1);
			if (isVisible) {
				if (prevPoints.size() == 2) {
					GeoPointND p1 = prevPoints.get(0);
					GeoPointND p2 = prevPoints.get(1);
					previewTempRadius.setValue(p1.distance(p2));
				}
				else if (prevSegments.size() == 1) {
					GeoSegment seg = (GeoSegment) prevSegments.get(0);
					previewTempRadius.setValue(seg.getLength());
				}								
				else if (prevConics.size() == 1) {
					GeoConic circle = (GeoConic) prevConics.get(0);
					previewTempRadius.setValue(circle.getCircleRadius());
				}								
				previewTempRadius.updateCascade();
			}			
		}
		
		// all other conic preview modes: use points to define preview conic
		else {		
			isVisible = conic != null && prevPoints.size() == neededPrevPoints;
			if (isVisible) {
				for (int i=0; i < prevPoints.size(); i++) {
					Coords p = view.getCoordsForView(prevPoints.get(i).getInhomCoordsInD(3));
					//Application.debug("p["+i+"]=\n"+p);
					previewTempPoints[i].setCoords(p.projectInfDim(),true);					
				}						
				previewTempPoints[0].updateCascade();			
			}	
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
		if (conic != null)
			conic.remove();
	}

	/**
	 * Returns the conic to be draw
	 * (might not be equal to geo, if this is part of bigger geo)
	 * @return conic
	 */
	public GeoConicND getConic() {
		return conic;
	}
}
