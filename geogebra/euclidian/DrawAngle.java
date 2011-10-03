/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.euclidian;

import geogebra.kernel.AlgoAngleLines;
import geogebra.kernel.AlgoAnglePoints;
import geogebra.kernel.AlgoAnglePolygon;
import geogebra.kernel.AlgoAngleVector;
import geogebra.kernel.AlgoAngleVectors;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
/**
 * 
 * @author Markus Hohenwarter, Loic De Coq
 * @version
 */
public class DrawAngle extends Drawable implements Previewable {

	private GeoAngle angle;

	private GeoPoint vertex, point, point2;

	private GeoLine line, line2;

	private GeoVector vector;

	private boolean isVisible, labelVisible, show90degrees;

	final private static int DRAW_MODE_POINTS = 0;

	final private static int DRAW_MODE_VECTORS = 1;

	final private static int DRAW_MODE_LINES = 2;

	final private static int DRAW_MODE_SINGLE_VECTOR = 3;

	final private static int DRAW_MODE_SINGLE_POINT = 4;

	private int angleDrawMode;

	//private Arc2D.Double fillArc = new Arc2D.Double();
	private Arc2D.Double drawArc = new Arc2D.Double();
    private GeneralPath polygon = new GeneralPath(); // Michael Borcherds 2007-11-19
	private Ellipse2D.Double dot90degree;
	private Shape shape;
	private double m[] = new double[2];
	private double coords[] = new double[2];
	private double[] firstVec = new double[2];
	private GeoPoint tempPoint;
	private boolean drawDot;
	private GeoPoint [] previewTempPoints;  


	private Kernel kernel;
	
	// For decoration
	// added by Lo�c BEGIN
	private Shape shapeArc1,shapeArc2;
	private Arc2D.Double decoArc = new Arc2D.Double();
	private Line2D.Double[] tick;
	private double[] angleTick=new double[2];
	/** maximum angle distance between two ticks.*/
	public static final double MAX_TICK_DISTANCE=Math.toRadians(15);
	private GeneralPath square;

	private ArrayList<GeoPointND> prevPoints;
	//END
	

	/**
	 * @param view Euclidian view
	 * @param angle Angle to be drawn
	 */
	public DrawAngle(EuclidianView view, GeoAngle angle) {
		this.view = view;
		kernel = view.getKernel();
		this.angle = angle;
		geo = angle;

		angleDrawMode = -1;

		init();

		if (angleDrawMode > -1) {
			angle.setDrawable(true);
			update();
		}
	}
	
	/**
	 * Creates a new DrawAngle for preview 
	 * @param view 
	 * @param mode 
	 * @param points 
	 */
	DrawAngle(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view; 
		prevPoints = points;

		Construction cons = view.getKernel().getConstruction();
		previewTempPoints = new GeoPoint[3];
		for (int i=0; i < previewTempPoints.length; i++) {
			previewTempPoints[i] = new GeoPoint(cons);			
		}
		
		initPreview();
	} 
	


	private void init(){
		AlgoElement algo = geo.getDrawAlgorithm();
		Construction cons = geo.getConstruction();
		tempPoint = new GeoPoint(cons);
		tempPoint.setCoords(0.0, 0.0, 1.0);

		// angle defined by three points
		if (algo instanceof AlgoAnglePoints) {
			angleDrawMode = DRAW_MODE_POINTS;
			AlgoAnglePoints pa = (AlgoAnglePoints) algo;
			vertex = pa.getB();
			point = pa.getA();
			point2 = pa.getC();
		}
		// angle between two vectors
		else if (algo instanceof AlgoAngleVectors) {
			angleDrawMode = DRAW_MODE_VECTORS;
			AlgoAngleVectors va = (AlgoAngleVectors) algo;
			GeoVector v = va.getv();
			vector = v;
		}
		// angle between two lines
		else if (algo instanceof AlgoAngleLines) {
			angleDrawMode = DRAW_MODE_LINES;
			AlgoAngleLines la = (AlgoAngleLines) algo;
			line = la.getg();
			line2 = la.geth();
			vertex = tempPoint;
		}
		// angle of a single vector or a single point
		else if (algo instanceof AlgoAngleVector) {
			AlgoAngleVector av = (AlgoAngleVector) algo;
			GeoVec3D vec = av.getVec3D();
			if (vec instanceof GeoVector) {
				angleDrawMode = DRAW_MODE_SINGLE_VECTOR;
				vector = (GeoVector) vec;
			} else if (vec instanceof GeoPoint) {
				angleDrawMode = DRAW_MODE_SINGLE_POINT;
				point = (GeoPoint) vec;
				vertex = tempPoint;
			}
			firstVec[0] = 1;
			firstVec[1] = 0;
		} else if (algo instanceof AlgoAnglePolygon) {
			AlgoAnglePolygon va = (AlgoAnglePolygon) algo;

			GeoAngle[] angles = va.getAngles();
			
			GeoPointND[] points = va.getPolygon().getPoints();
			
			int l = points.length;		
			
			for (int i = 0 ; i < angles.length ; i++) {
				if (angles[i] == angle) {
					point2 = (GeoPoint) points[(i - 1 + l) % l];
					vertex = (GeoPoint) points[i];
					point = (GeoPoint) points[(i + 1) % l];
					break;
				}
			}
			
			angleDrawMode = DRAW_MODE_POINTS;
		
		} else Application.debug("missing case in DrawAngle");
	}
	
	final public void update() {
		if(!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();
		
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			shape = null;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}
		labelVisible = geo.isLabelVisible();
		updateStrokes(angle);
		
		double maxRadius = Double.POSITIVE_INFINITY;

		// set vertex and first vector to determine start angle
		switch (angleDrawMode) {
		case DRAW_MODE_POINTS: // three points
			// vertex
			vertex.getInhomCoords(m);

			// first vec
			firstVec[0] = point.inhomX - m[0];
			firstVec[1] = point.inhomY - m[1];
			
			double vertexScreen[] = new double[2];
			vertexScreen[0] = m[0];
			vertexScreen[1] = m[1];
			
			double firstVecScreen[] = new double[2];
			firstVecScreen[0] = point.inhomX;
			firstVecScreen[1] = point.inhomY;
			
			
			double secondVecScreen[] = new double[2];
			secondVecScreen[0] = point2.inhomX;
			secondVecScreen[1] = point2.inhomY;
			
			view.toScreenCoords(vertexScreen);
			view.toScreenCoords(firstVecScreen);
			view.toScreenCoords(secondVecScreen);
			
			firstVecScreen[0] -= vertexScreen[0];
			firstVecScreen[1] -= vertexScreen[1];
			secondVecScreen[0] -= vertexScreen[0];
			secondVecScreen[1] -= vertexScreen[1];
			
			maxRadius = 0.5 * Math.sqrt(Math.min(firstVecScreen[0] * firstVecScreen[0] + firstVecScreen[1] * firstVecScreen[1], secondVecScreen[0] * secondVecScreen[0] + secondVecScreen[1] * secondVecScreen[1]));
			
			
			break;

		case DRAW_MODE_VECTORS: // two vectors
			// vertex
			vertex = vector.getStartPoint();
			if (vertex == null)
				vertex = tempPoint;
			vertex.getInhomCoords(m);

			// first vec
			vector.getInhomCoords(firstVec);
			break;

		case DRAW_MODE_LINES: // two lines
			// intersect lines to get vertex
			GeoVec3D.cross(line, line2, vertex);
			vertex.getInhomCoords(m);

			// first vec
			line.getDirection(firstVec);
			break;

		case DRAW_MODE_SINGLE_VECTOR: // single GeoVector
			// vertex
			vertex = vector.getStartPoint();
			if (vertex == null)
				vertex = tempPoint;
			vertex.getInhomCoords(m);

			// first vec is constant (1,0)
			break;

		case DRAW_MODE_SINGLE_POINT: // single GeoPoint
			// vertex
			vertex.getInhomCoords(m);

			// first vec is constant (1,0)
			break;

		default:
			/*
			 * if (vertex == null) {
			 * Application.debug(Util.toHTMLString("vertex null for: " + geo + ",
			 * parent: " + geo.getParentAlgorithm().getCommandDescription())); }
			 */
			return;
		}

		// check vertex
		if (!vertex.isDefined() || vertex.isInfinite()) {
			isVisible = false;
			return;
		}

		// calc start angle
		double angSt = Math.atan2(firstVec[1], firstVec[0]);
		if (Double.isNaN(angSt) || Double.isInfinite(angSt)) {
			isVisible = false;
			return;
		}
		// Michael Borcherds 2007-11-19 BEGIN
//		double angExt = angle.getValue();
		double angExt = angle.getRawAngle();

		// if this angle was not allowed to become a reflex angle
		// (i.e. greater than pi) we got (2pi - angleValue) for angExt
//		if (angle.changedReflexAngle()) {
//			angSt = angSt - angExt;
//		}

		switch (angle.getAngleStyle()) {
			case GeoAngle.ANGLE_ISCLOCKWISE:
				angSt+=angExt;
				angExt=2.0*Math.PI-angExt;
				break;
				
			case GeoAngle.ANGLE_ISNOTREFLEX:
				if (angExt>Math.PI)
				{
					angSt+=angExt;
					angExt=2.0*Math.PI-angExt;
				}
				break;
				
			case GeoAngle.ANGLE_ISREFLEX:
				if (angExt<Math.PI)
				{
					angSt+=angExt;
					angExt=2.0*Math.PI-angExt;
				}
				break;
		}		
		// Michael Borcherds 2007-11-19 END

		double as = Math.toDegrees(angSt);
		double ae = Math.toDegrees(angExt);
		
		int arcSize = Math.min((int)maxRadius, angle.getArcSize());
		
		double r = arcSize * view.invXscale;

		// check whether we need to take care for a special 90 degree angle appearance
		show90degrees = view.getRightAngleStyle() != EuclidianView.RIGHT_ANGLE_STYLE_NONE &&
						angle.isEmphasizeRightAngle() &&  
						kernel.isEqual(angExt, Kernel.PI_HALF);
		
		// set coords to screen coords of vertex
		coords[0]=m[0];
		coords[1]=m[1];
		view.toScreenCoords(coords);
		
		// for 90 degree angle
		drawDot = false;
		
		// SPECIAL case for 90 degree angle, by Loic and Markus
		if (show90degrees) {						
			switch (view.getRightAngleStyle()) {									
			case EuclidianView.RIGHT_ANGLE_STYLE_SQUARE:
				// set 90 degrees square									
				if (square == null) 
					square = new GeneralPath();
				else					
					square.reset();
				double length = arcSize * 0.7071067811865;
	     		square.moveTo((float)coords[0],(float)coords[1]);
				square.lineTo((float)(coords[0]+length*Math.cos(angSt)),(float)(coords[1]-length*Math.sin(angSt)*view.getScaleRatio()));
				square.lineTo((float)(coords[0]+arcSize*Math.cos(angSt+Kernel.PI_HALF/2)),(float)(coords[1]-arcSize*Math.sin(angSt+Kernel.PI_HALF/2)*view.getScaleRatio()));
				square.lineTo((float)(coords[0]+length*Math.cos(angSt+Kernel.PI_HALF)),(float)(coords[1]-length*Math.sin(angSt+Kernel.PI_HALF)*view.getScaleRatio()));
				square.lineTo((float)coords[0],(float)coords[1]);
				shape = square;
				break;								
				
			case EuclidianView.RIGHT_ANGLE_STYLE_L:
				// Belgian offset |_						
				if (square == null) 
					square = new GeneralPath();
				else					
					square.reset();
				length = arcSize * 0.7071067811865;
				double offset = length * 0.4;
				square.moveTo((float)(coords[0]+length*Math.cos(angSt)+offset*Math.cos(angSt)+offset*Math.cos(angSt+Kernel.PI_HALF)),(float)(coords[1]-length*Math.sin(angSt)*view.getScaleRatio() - offset*Math.sin(angSt) - offset*Math.sin(angSt+Kernel.PI_HALF)));
				square.lineTo((float)(coords[0]+offset*Math.cos(angSt)+offset*Math.cos(angSt+Kernel.PI_HALF)),(float)(coords[1] - offset*Math.sin(angSt) - offset*Math.sin(angSt+Kernel.PI_HALF)));
				square.lineTo((float)(coords[0]+length*Math.cos(angSt+Kernel.PI_HALF)+offset*Math.cos(angSt)+offset*Math.cos(angSt+Kernel.PI_HALF)),(float)(coords[1]-length*Math.sin(angSt+Kernel.PI_HALF)*view.getScaleRatio() - offset*Math.sin(angSt) - offset*Math.sin(angSt+Kernel.PI_HALF)));
				shape = square;
				
				break;								
				
				case EuclidianView.RIGHT_ANGLE_STYLE_DOT:					
					//	set 90 degrees dot			
					drawDot = true;
					
					if (dot90degree == null) 
						dot90degree = new Ellipse2D.Double();
					int diameter = 2 * geo.lineThickness;
					double radius = r / 1.7;
					double labelAngle = angSt + angExt / 2.0;
					coords[0] = m[0] + radius * Math.cos(labelAngle);
					coords[1] = m[1] + radius * Math.sin(labelAngle);
					view.toScreenCoords(coords);
					dot90degree.setFrame(coords[0] - geo.lineThickness, coords[1]
							- geo.lineThickness, diameter, diameter);											
				
					// set arc in real world coords and transform to screen coords
					drawArc.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.PIE);
					shape = view.coordTransform.createTransformedShape(drawArc);					
					break;
			}																				
		}
		// STANDARE case: draw arc with possible decoration 
		else {
			// set arc in real world coords and transform to screen coords
			drawArc.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.PIE);			
			shape = view.coordTransform.createTransformedShape(drawArc);
			
			double rdiff;
			
			// For Decoration
			// Added By Lo�c BEGIN
	    	switch(geo.decorationType){	
		    	case GeoElement.DECORATION_ANGLE_TWO_ARCS:
		    		rdiff = 4 + geo.lineThickness/2d;
		    		r=(arcSize-rdiff)*view.invXscale;
					decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.OPEN);
					// transform arc to screen coords
					shapeArc1 = view.coordTransform.createTransformedShape(decoArc);
					break;
				
				case GeoElement.DECORATION_ANGLE_THREE_ARCS:
					rdiff = 4 + geo.lineThickness/2d;
					r = (arcSize-rdiff) * view.invXscale;
					decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.OPEN);
					// transform arc to screen coords
					shapeArc1 = view.coordTransform.createTransformedShape(decoArc);
					r = (arcSize-2*rdiff) * view.invXscale;
					decoArc.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.OPEN);
					// transform arc to screen coords
					shapeArc2 = view.coordTransform.createTransformedShape(decoArc);
					break;
					
				case GeoElement.DECORATION_ANGLE_ONE_TICK:
					angleTick[0]=-angSt-angExt/2;
					updateTick(angleTick[0],arcSize,0);
					break;
				
				case GeoElement.DECORATION_ANGLE_TWO_TICKS:
					angleTick[0]=-angSt-2*angExt/5;
					angleTick[1]=-angSt-3*angExt/5;
					if (Math.abs(angleTick[1]-angleTick[0])>MAX_TICK_DISTANCE){
						angleTick[0]=-angSt-angExt/2-MAX_TICK_DISTANCE/2;
						angleTick[1]=-angSt-angExt/2+MAX_TICK_DISTANCE/2;
					}
					updateTick(angleTick[0],arcSize,0);
					updateTick(angleTick[1],arcSize,1);
					break;
				
				case GeoElement.DECORATION_ANGLE_THREE_TICKS:
					angleTick[0]=-angSt-3*angExt/8;
					angleTick[1]=-angSt-5*angExt/8;
					if (Math.abs(angleTick[1]-angleTick[0])>2*MAX_TICK_DISTANCE){
						angleTick[0]=-angSt-angExt/2-MAX_TICK_DISTANCE;
						angleTick[1]=-angSt-angExt/2+MAX_TICK_DISTANCE;
					}
					updateTick(angleTick[0],arcSize,0);
					updateTick(angleTick[1],arcSize,1);
					//middle tick
					angleTick[0]=-angSt-angExt/2;
					updateTick(angleTick[0],arcSize,2);
					break;
//					 Michael Borcherds 2007-11-19 START
				case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
				case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
					double n2[] = new double[2]; // actual angle for arrow point
					double n[] = new double[2];  // adjusted to rotate arrow slightly
					double v[] = new double[2];  // adjusted to rotate arrow slightly
					
					double rotateangle=0.25d; // rotate arrow slightly
					
					if (geo.decorationType==GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE)
					{
						n2[0]=Math.cos(angSt);
						n2[1]=Math.sin(angSt);
						n[0]=Math.cos(angSt+rotateangle);
						n[1]=Math.sin(angSt+rotateangle);
						v[0]=-n[1];
						v[1]=n[0];
					}
					else
					{
						n2[0]=Math.cos(angExt+angSt);
						n2[1]=Math.sin(angExt+angSt);
						n[0]=Math.cos(angExt+angSt-rotateangle);
						n[1]=Math.sin(angExt+angSt-rotateangle);
						v[0]=n[1];
						v[1]=-n[0];
					}
					
					double p1[] = new double[2];
					double p2[] = new double[2];
					double p3[] = new double[2];
		    		rdiff = 4 + geo.lineThickness/2d;
		    		r=(arcSize)*view.invXscale;
					
					p1[0]=m[0]+r*n2[0];
					p1[1]=m[1]+r*n2[1]; // arrow tip
					
					double size=4d+(double)geo.lineThickness/4d;
					size=size*0.9d;
					
					p2[0]=p1[0]+(1*n[0]+3*v[0])*size*view.invXscale;;
					p2[1]=p1[1]+(1*n[1]+3*v[1])*size*view.invYscale;; // arrow end 1
					
					p3[0]=p1[0]+(-1*n[0]+3*v[0])*size*view.invXscale;;
					p3[1]=p1[1]+(-1*n[1]+3*v[1])*size*view.invYscale;; // arrow end 2
					
					view.toScreenCoords(p1);
					view.toScreenCoords(p2);
					view.toScreenCoords(p3);

					polygon.reset();
				    polygon.moveTo((float) p1[0], (float) p1[1]);
				    polygon.lineTo((float) p2[0], (float) p2[1]);
				    polygon.lineTo((float) p3[0], (float) p3[1]);
				    polygon.lineTo((float) p1[0], (float) p1[1]);

				    polygon.moveTo((float)p1[0], (float)p1[1]);
				    polygon.lineTo((float)p2[0], (float)p2[1]);
				    polygon.lineTo((float)p3[0], (float)p3[1]);
				    polygon.lineTo((float)p1[0], (float)p1[1]);
				    polygon.closePath();
					
					break;
//					 Michael Borcherds 2007-11-19 END
				
	    	}
			// END
		}
		
		// shape on screen?
		if (!shape.intersects(0, 0, view.width, view.height)) {
			isVisible = false;
			return;
		}	
	
		if (labelVisible) {
			// calculate label position
			double radius = r / 1.7;
			double labelAngle = angSt + angExt / 2.0;
			coords[0] = m[0] + radius * Math.cos(labelAngle);
			coords[1] = m[1] + radius * Math.sin(labelAngle);
			view.toScreenCoords(coords);
				
			labelDesc = angle.getLabelDescription();
			xLabel = (int) (coords[0] - 3);
			yLabel = (int) (coords[1] + 5);			
			
			if (!addLabelOffset() && drawDot)
				xLabel = (int) (coords[0] + 2 * geo.lineThickness);
		}
		
		//G.Sturr 2010-6-28 spreadsheet trace is now handled in GeoElement.update()
	//	if (angle.getSpreadsheetTrace())
	//	    recordToSpreadsheet(angle);
							
	}

	final public void draw(Graphics2D g2) {
		
		if (isVisible) {
			if (!show90degrees || view.getRightAngleStyle() != EuclidianView.RIGHT_ANGLE_STYLE_L) {
					fill(g2, shape, false); // fill using default/hatching/image as appropriate
	        	
			}

			if (geo.doHighlighting()) {
				g2.setPaint(angle.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(shape);
			}

			if (geo.lineThickness > 0) {
				g2.setPaint(angle.getObjectColor());
				g2.setStroke(objStroke);
				g2.draw(shape);
			}
			
			// special handling of 90 degree dot
			if (show90degrees) {
				switch (view.getRightAngleStyle()) {
					case EuclidianView.RIGHT_ANGLE_STYLE_DOT:
						g2.fill(dot90degree);
						break;
						
					default:
						// nothing to do as square for EuclidianView.RIGHT_ANGLE_STYLE_SQUARE
						// was already drawn as shape						
				}
			} 			
			else {
				// if we don't have a special 90 degrees appearance we might need to draw
				// other decorations							
				switch(geo.decorationType){
					case GeoElement.DECORATION_ANGLE_TWO_ARCS:
						g2.draw(shapeArc1);
						break;
						
					case GeoElement.DECORATION_ANGLE_THREE_ARCS:
						g2.draw(shapeArc1);
						g2.draw(shapeArc2);
						break;
						
					case GeoElement.DECORATION_ANGLE_ONE_TICK:
						g2.setStroke(decoStroke);
						g2.draw(tick[0]);
						break;
						
					case GeoElement.DECORATION_ANGLE_TWO_TICKS:
						g2.setStroke(decoStroke);
						g2.draw(tick[0]);
						g2.draw(tick[1]);
						break;
						
					case GeoElement.DECORATION_ANGLE_THREE_TICKS:
						g2.setStroke(decoStroke);
						g2.draw(tick[0]);
						g2.draw(tick[1]);
						g2.draw(tick[2]);
						break;
//						 Michael Borcherds 2007-11-19 START
					case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
					case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
						g2.setStroke(decoStroke);
						g2.fill(polygon);
						break;
// Michael Borcherds 2007-11-19
				}
			}
			
			if (labelVisible) {
				g2.setPaint(angle.getLabelColor());
				g2.setFont(view.fontAngle);
				drawLabel(g2);
			}
		}
	}
	
	// update coords for the tick decoration
	// tick is at distance radius and oriented towards angle
	// id = 0,1, or 2 for tick[0],tick[1] or tick[2]
	private void updateTick(double angle,int radius,int id){
		// coords have to be set to screen coords of m before calling this method 	
		if (tick == null) {
			tick = new Line2D.Double[3];
			for(int i=0; i < tick.length; i++){
				tick[i] = new Line2D.Double();
			}							
		}			
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		double length = 2.5 + geo.lineThickness / 4d;		

		tick[id].setLine(coords[0]+ (radius-length)*cos, 
				coords[1]+(radius-length)* sin * view.getScaleRatio(), 
				coords[0]+(radius+length)* cos,
				coords[1]+(radius+length)* sin *view.getScaleRatio());
	}

	final public boolean hit(int x, int y) {
		return shape != null && shape.contains(x, y);
	}
	
	final public boolean isInside(Rectangle rect) {
		return  shape != null && rect.contains(shape.getBounds());		
	}

	public GeoElement getGeoElement() {
		return geo;
	}

	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}
	
	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || shape == null || !geo.isEuclidianVisible())
			return null;
		
		// return selection circle's bounding box
		return shape.getBounds();		
	}

    private void initPreview() {
		//	init the conic for preview			    	
		Construction cons = previewTempPoints[0].getConstruction();
		
				AlgoAnglePoints algo = new AlgoAnglePoints(cons, 
						previewTempPoints[0], 
						previewTempPoints[1],
						previewTempPoints[2]);
				cons.removeFromConstructionList(algo);				
				
				geo = algo.getAngle();
				angle = (GeoAngle)geo;
				geo.setEuclidianVisible(true);
				init();
				//initConic(algo.getCircle());
    }

	final public void updatePreview() {
		isVisible = geo != null && prevPoints.size() == 2;
		if (isVisible) {
			for (int i=0; i < prevPoints.size(); i++) {
				Coords p = view.getCoordsForView(prevPoints.get(i).getInhomCoordsInD(3));
				previewTempPoints[i].setCoords(p,true);					
			}						
			previewTempPoints[0].updateCascade();			
		}	
	}

	final public void updateMousePos(double xRW, double yRW) {
		if (isVisible) {
			previewTempPoints[previewTempPoints.length-1].setCoords(xRW, yRW, 1.0);
			previewTempPoints[previewTempPoints.length-1].updateCascade();		
			update();
		}
	}

	final public void drawPreview(Graphics2D g2) {
		isVisible = geo != null && prevPoints.size() == 2;
		draw(g2);
	}

	public void disposePreview() {
	}
}