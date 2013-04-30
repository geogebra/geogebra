package geogebra.common.euclidian.draw;

import geogebra.common.awt.GArc2D;
import geogebra.common.awt.GGeneralPath;
import geogebra.common.awt.GLine2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
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
	
	private GeneralPathClipped hyp;

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
	
	/**
	 * 
	 * @param m midpoint
	 * @param ev0 first eigen vec
	 * @param ev1 second eigen vec
	 * @param r0 first half axis
	 * @param r1 second half axis
	 * @param parameter angle parameter
	 * @return ellipse point
	 */
	public static final Coords ellipsePoint(Coords m, Coords ev0, Coords ev1, double r0, double r1, double parameter){
		return m.add(ev0.mul(r0*Math.cos(parameter))).add(ev1.mul(r1*Math.sin(parameter)));
	}
	
	/**
	 * draw an edge of the ellipse (if not all in the view)
	 */
	private void updateEllipseEdge(){
		
		Coords m = conic.getMidpoint3D();
		Coords ev0 = conic.getEigenvec3D(0);
		Coords ev1 = conic.getEigenvec3D(1);
		double r0 = conic.getHalfAxis(0);
		double r1 = conic.getHalfAxis(1);
		
		double start0 = getStart(0);
		double end0 = getEnd(0);
		double start1 = getStart(1);
		double end1 = getEnd(1);

		Coords A, B; 
		
		if(!Double.isNaN(start1)){ // there is two segments
			
			//try first segment
			A = view.getCoordsForView(ellipsePoint(m, ev0, ev1, r0, r1, end0));
			if (Kernel.isZero(A.getZ())){
				B = view.getCoordsForView(ellipsePoint(m, ev0, ev1, r0, r1, start1));
			}else{ //try second segment
				A = view.getCoordsForView(ellipsePoint(m, ev0, ev1, r0, r1, end1));
				B = view.getCoordsForView(ellipsePoint(m, ev0, ev1, r0, r1, start0));
			}
		}else{ // only one segment
			A = view.getCoordsForView(ellipsePoint(m, ev0, ev1, r0, r1, end0));
			B = view.getCoordsForView(ellipsePoint(m, ev0, ev1, r0, r1, start0));
		}

		if (Kernel.isZero(B.getZ())){
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
	protected void updateEllipse() {
		
		Double start0 = getStart(0);
		// if no hole, just draw an ellipse
		if (Double.isNaN(start0)){
			super.updateEllipse();
			return;
		}
		
		// check if in view
		Coords M = view.getCoordsForView(conic.getMidpoint3D());
		if (!Kernel.isZero(M.getZ())) {// check if in view
			updateEllipseEdge();
			return;
		}
		Coords[] ev = new Coords[2];
		for (int j = 0; j < 2; j++) {
			ev[j] = view.getCoordsForView(conic.getEigenvec3D(j));
			if (!Kernel.isZero(ev[j].getZ())) {// check if in view
				updateEllipseEdge();
				return;
			}
		}

		// check for huge pixel radius
		double xradius = halfAxes[0] * view.getXscale();
		double yradius = halfAxes[1] * view.getYscale();
		/*
		if (xradius > DrawConic.HUGE_RADIUS || yradius > DrawConic.HUGE_RADIUS) {
			isVisible = false;
			return;
		}
		*/
		
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
		transform.concatenate(view.getTransform(conic, M, ev));
		

		// BIG RADIUS: larger than screen diagonal
		int BIG_RADIUS = view.getWidth() + view.getHeight(); // > view's diagonal
		if (xradius < BIG_RADIUS && yradius < BIG_RADIUS) {
			shape = transform.createTransformedShape(arcs);
		} else {
			// clip big arc at screen
			shape = ClipShape.clipToRect(arcs, transform, AwtFactory.prototype.newRectangle(-1, -1,
					view.getWidth() + 2, view.getHeight() + 2));
		}

		// set label coords
		labelCoords[0] = halfAxes[0] * Math.cos(start0);
		labelCoords[1] = halfAxes[1] * Math.sin(start0);
		transform.transform(labelCoords, 0, labelCoords, 0, 1);
		xLabel = (int) labelCoords[0];
		yLabel = (int) labelCoords[1];
		
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

		Coords m = conic.getOrigin3D(0);
		Coords d = conic.getDirection3D(0);

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
	
	
	@Override
	protected void updateParabolaX0Y0(){
		//TODO consider not symmetric parabola
		y0 = getEnd(0) * conic.p;
		x0 = y0*y0/(conic.p * 2);
	}
	
	@Override
	protected void updateParabolaPath(){
		super.updateParabolaPath();
		parabola.closePath();
	}
	
	
	@Override
	protected void updateParabolaLabelCoords(){
		labelCoords[0] = 0;
		labelCoords[1] = 0;
	}
	
	
	
	
	@Override
	protected void updateHyperbolaEdge(){

		Coords m = conic.getMidpoint3D();
		Coords ev1 = conic.getEigenvec3D(0);
		Coords ev2 = conic.getEigenvec3D(1);
		double e1 = conic.getHalfAxis(0);
		double e2 = conic.getHalfAxis(1);
		

		Coords A = null, B = null;		

		double start = getStart(0);
		double end;
		if(!Double.isNaN(start)){ //try first segment
			end  = getEnd(0);
			A = view.getCoordsForView(m.add(ev1.mul(e1*Math.cosh(start))).add(ev2.mul(e2*Math.sinh(start))));
			B = view.getCoordsForView(m.add(ev1.mul(e1*Math.cosh(end))).add(ev2.mul(e2*Math.sinh(end))));
		}else{ //try second segment
			start = getStart(1);
			if(!Double.isNaN(start)){ 
				end  = getEnd(1);
				A = view.getCoordsForView(m.add(ev1.mul(-e1*Math.cosh(start))).add(ev2.mul(e2*Math.sinh(start))));
				B = view.getCoordsForView(m.add(ev1.mul(-e1*Math.cosh(end))).add(ev2.mul(e2*Math.sinh(end))));
			}
		}
		

		if (A != null && Kernel.isZero(A.getZ()) && Kernel.isZero(B.getZ())){
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
	
	private boolean drawLeft;
	
	@Override
	protected void updateHyperbolaResetPaths(){

		if (firstHyperbola) {
			firstHyperbola = false;
			points = PLOT_POINTS;
			hyp = new GeneralPathClipped(view); 
		} else {
			hyp.reset();
		}
	}
	
	@Override
	protected void updateHyperbolaX0(){
		
		double end = getEnd(0);
		if (Double.isNaN(end)){
			x0 = a*Math.cosh(getEnd(1));
			drawLeft = false;
		}else{
			x0 = a*Math.cosh(end);
			drawLeft = true;
		}
	}

	@Override
	protected void updateHyperbolaAddPoint(int index, double x, double y){
		if (drawLeft){
			hyp.addPoint(index, x, y);
		}else{
			hyp.addPoint(index, -x, y);
		}

	}
	
	@Override
	protected void updateHyperboalSetTransformToPaths(){
		
		hyp.transform(transform);
	}
	
	
	@Override
	protected void updateHyperbolaClosePaths(){

		hyp.closePath();
	}
	
	@Override
	protected void updateHyperbolaSetShape(){
		shape = hyp;
	}

	@Override
	protected void drawHyperbola(geogebra.common.awt.GGraphics2D g2){

		fill(g2, shape, true);

		if (geo.doHighlighting()) {
			g2.setStroke(selStroke);
			g2.setColor(geo.getSelColor());
			EuclidianStatic.drawWithValueStrokePure(shape, g2);
		}

		g2.setStroke(objStroke);
		g2.setColor(geo.getObjectColor());
		EuclidianStatic.drawWithValueStrokePure(shape, g2);

		if (labelVisible) {
			g2.setFont(view.getFontConic());
			g2.setColor(geo.getLabelColor());
			drawLabel(g2);
		}
	}
	
	@Override
	protected void updateHyperbolaLabelCoords(){
		if (drawLeft){
			labelCoords[0] = a;
		}else{
			labelCoords[0] = -a;
		}
		labelCoords[1] = 0;
	}

	@Override
	protected boolean checkHyperbolaOnScreen(GRectangle viewRect){
		//TODO ?
		return true;
	}
	
	@Override
	public boolean hitHyperbola(int hitX, int hitY) {
		//TODO ?
		return false;
	}
}
