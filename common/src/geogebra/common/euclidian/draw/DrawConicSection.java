package geogebra.common.euclidian.draw;

import geogebra.common.awt.GArc2D;
import geogebra.common.awt.GGeneralPath;
import geogebra.common.awt.GLine2D;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.clipping.ClipShape;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicSectionInterface;


/**
 * Class for drawing a conic section (limited quadric and plane)
 * @author mathieu
 *
 */
public class DrawConicSection extends DrawConic {
	
	private GArc2D arc;
	private GLine2D line;

	/**
	 * constructor
	 * @param view view
	 * @param c conic
	 */
	public DrawConicSection(EuclidianView view, GeoConicND c) {
		super(view, c);
	}

	/**
	 * 
	 * @param i index
	 * @return i-th start parameter for the section
	 */
	protected double getStart(int i){
		return ((GeoConicSectionInterface) getGeoElement()).getParameterStart(i);
	}
	
	/**
	 * 
	 * @param i index
	 * @return i-th extent parameter for the section
	 */
	protected double getExtent(int i){
		return ((GeoConicSectionInterface) getGeoElement()).getParameterExtent(i);
	}
	

	/**
	 * 
	 * @param i index
	 * @return i-th end parameter for the section
	 */
	protected double getEnd(int i){
		return ((GeoConicSectionInterface) getGeoElement()).getParameterEnd(i);
	}
	
	
	@Override
	protected void updateEllipse() {
		
		Double start0 = getStart(0);
		// if no hole, just draw an ellipse
		if (Double.isNaN(start0)){
			super.updateEllipse();
			return;
		}
		
		//draw_type = DRAW_TYPE_ELLIPSE;

		// check for huge pixel radius
		double xradius = halfAxes[0] * view.getXscale();
		double yradius = halfAxes[1] * view.getYscale();
		if (xradius > DrawConic.HUGE_RADIUS || yradius > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}
		
		//use shape
		GShape arcs;

		// set arc
		if (arc==null){
			arc = AwtFactory.prototype.newArc2D();
		}
		
		Double extent0 = getExtent(0);
		Double start1 = getStart(1);
		
		// set the arc type : if one hole, add chord to close the arc, if two holes, let arcs open
		int type;
		if (Double.isNaN(start1)){
			type = GArc2D.CHORD;
		}else{
			type = GArc2D.OPEN;
		}
		
		arc.setArc(-halfAxes[0], -halfAxes[1], 2 * halfAxes[0],
				2 * halfAxes[1],
				-Math.toDegrees(start0),
				-Math.toDegrees(extent0), type);

		// if no second hole, just draw one arc
		if (Double.isNaN(start1)){
			arcs = arc;
		}else{
			arcs = AwtFactory.prototype.newGeneralPath();

			((GGeneralPath) arcs).append(arc,true);

			
			//second arc
			Double extent1 = getExtent(1);
			arc.setArc(-halfAxes[0], -halfAxes[1], 2 * halfAxes[0],
					2 * halfAxes[1],
					-Math.toDegrees(start1),
					-Math.toDegrees(extent1), GArc2D.OPEN);
			
			((GGeneralPath) arcs).append(arc,true);
			((GGeneralPath) arcs).closePath();
		}
		
		
		
		
		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		transform.concatenate(conic.getAffineTransform());
		

		// BIG RADIUS: larger than screen diagonal
		int BIG_RADIUS = view.getWidth() + view.getHeight(); // > view's diagonal
		if (xradius < BIG_RADIUS && yradius < BIG_RADIUS) {
			shape = transform.createTransformedShape(arcs);
		} else {
			// clip big arc at screen
			shape = ClipShape.clipToRect(arcs, transform, AwtFactory.prototype.newRectangle(-1, -1,
					view.getWidth() + 2, view.getHeight() + 2));
		}

		// label position
		if (labelVisible) {
			double midAngle = start0
					+ start1 / 2.0;
			double[] coords = new double[2];
			coords[0] = halfAxes[0] * Math.cos(midAngle);
			coords[1] = halfAxes[1] * Math.sin(midAngle);
			transform.transform(coords, 0, coords, 0, 1);

			labelDesc = geo.getLabelDescription();

			xLabel = (int) (coords[0]) + 6;
			yLabel = (int) (coords[1]) - 6;
			addLabelOffset();
		}
		
	}
	
	@Override
	protected void updateLines() {
		
		Coords[] points = new Coords[4];

		Coords m = conic.getOrigin3D(0);
		Coords d = conic.getDirection3D(0);

		points[0] = view.getCoordsForView(m.add(d.mul(getStart(0))));
		points[1] = view.getCoordsForView(m.add(d.mul(getEnd(0))));
		
		m = conic.getOrigin3D(1);
		d = conic.getDirection3D(1);

		points[3] = view.getCoordsForView(m.add(d.mul(getStart(1))));
		points[2] = view.getCoordsForView(m.add(d.mul(getEnd(1))));
		
		
		
		GGeneralPath path = AwtFactory.prototype.newGeneralPath();
		
		boolean firstPoint = true;

		for (int i=0; i<4; i++){
			if (Kernel.isZero(points[i].getZ())){
				if (firstPoint){
					path.moveTo((float) points[i].getX(), (float) points[i].getY());
					firstPoint = false;
				}else{
					path.lineTo((float) points[i].getX(), (float) points[i].getY());
				}
			}
		}
		
		if(!firstPoint){//close path only if at least one point
			path.closePath();
		}
		

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		shape = transform.createTransformedShape(path);
	}
	
	
	
	@Override
	protected void updateDoubleLine() {

		Coords d = conic.getDirection3D(0);
		Coords m = conic.getMidpoint3D();

		Coords A = view.getCoordsForView(m.add(d.mul(getStart(0))));
		Coords B = view.getCoordsForView(m.add(d.mul(getEnd(0))));
		

		if (Kernel.isZero(A.getZ()) && Kernel.isZero(B.getZ())){
			if (line == null)
				line = AwtFactory.prototype.newLine2D();
			line.setLine(A.getX(), A.getY(), B.getX(), B.getY());
		}else{
			isVisible = false;
			return;
		}

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		shape = transform.createTransformedShape(line);
	}
	
	@Override
	protected void drawLines(geogebra.common.awt.GGraphics2D g2){

		fill(g2, shape, false);
		if (geo.doHighlighting()) {
			g2.setStroke(selStroke);
			g2.setColor(geo.getSelColor());
			g2.draw(shape);
		}
		
		g2.setStroke(objStroke);
		g2.setColor(geo.getObjectColor());
		g2.draw(shape);
		
		if (labelVisible) {
			g2.setFont(view.getFontConic());
			g2.setColor(geo.getLabelColor());
			drawLabel(g2);
		}
	}
	
	@Override
	public boolean hitLines(int hitX, int hitY) {
		//TODO ?
		return false;
	}
}
