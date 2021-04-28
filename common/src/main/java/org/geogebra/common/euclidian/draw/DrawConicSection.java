package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.euclidian.clipping.ClipShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicSectionInterface;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class for drawing a conic section (limited quadric and plane)
 * 
 * @author mathieu
 *
 */
public class DrawConicSection extends DrawConic {

	private GArc2D arc;
	private GLine2D line;

	private GLine2D[] lines;

	private GeneralPathClipped hyp;
	private boolean onlyEdge;
	private Coords[] endPoints;
	private boolean drawLeft;
	private GShape arcs = null;

	/**
	 * constructor
	 * 
	 * @param view
	 *            view
	 * @param c
	 *            conic
	 */
	public DrawConicSection(EuclidianView view, GeoConicND c) {
		super(view, c, false);
	}

	/**
	 * 
	 * @param i
	 *            index
	 * @return i-th start parameter for the section
	 */
	protected double getStart(int i) {
		return ((GeoConicSectionInterface) getGeoElement())
				.getParameterStart(i);
	}

	/**
	 * 
	 * @param i
	 *            index
	 * @return i-th extent parameter for the section
	 */
	protected double getExtent(int i) {
		return ((GeoConicSectionInterface) getGeoElement())
				.getParameterExtent(i);
	}

	/**
	 * 
	 * @param i
	 *            index
	 * @return i-th end parameter for the section
	 */
	protected double getEnd(int i) {
		return ((GeoConicSectionInterface) getGeoElement()).getParameterEnd(i);
	}

	/**
	 * 
	 * @param m
	 *            midpoint
	 * @param ev0
	 *            first eigen vec
	 * @param ev1
	 *            second eigen vec
	 * @param r0
	 *            first half axis
	 * @param r1
	 *            second half axis
	 * @param parameter
	 *            angle parameter
	 * @return ellipse point
	 */
	public static final Coords ellipsePoint(Coords m, Coords ev0, Coords ev1,
			double r0, double r1, double parameter) {
		return m.copy().addInsideMul(ev0, r0 * Math.cos(parameter))
				.addInsideMul(ev1, r1 * Math.sin(parameter));
	}

	/**
	 * draw an edge of the ellipse (if not all in the view)
	 */
	private void updateEllipseEdge() {

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

		if (!Double.isNaN(start1)) { // there is two segments

			// try first segment
			A = view.getCoordsForView(ellipsePoint(m, ev0, ev1, r0, r1, end0));
			if (DoubleUtil.isZero(A.getZ())) {
				B = view.getCoordsForView(
						ellipsePoint(m, ev0, ev1, r0, r1, start1));
			} else { // try second segment
				A = view.getCoordsForView(
						ellipsePoint(m, ev0, ev1, r0, r1, end1));
				B = view.getCoordsForView(
						ellipsePoint(m, ev0, ev1, r0, r1, start0));
			}
		} else { // only one segment
			A = view.getCoordsForView(ellipsePoint(m, ev0, ev1, r0, r1, end0));
			B = view.getCoordsForView(
					ellipsePoint(m, ev0, ev1, r0, r1, start0));
		}

		if (DoubleUtil.isZero(B.getZ())) {
			if (line == null) {
				line = AwtFactory.getPrototype().newLine2D();
			}
			line.setLine(A.getX(), A.getY(), B.getX(), B.getY());
		} else {
			isVisible = false;
			return;
		}

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		fillShape = transform.createTransformedShape(line);
	}

	@Override
	protected void updateCircle() {
		onlyEdge = false;
		super.updateCircle();
	}

	@Override
	protected void updateHyperbola() {
		onlyEdge = false;
		super.updateHyperbola();
	}

	@Override
	protected void updateParabola() {
		onlyEdge = false;
		super.updateParabola();
	}

	@Override
	protected boolean checkIsOnFilling() {
		return super.checkIsOnFilling() && !onlyEdge;
	}

	@Override
	public boolean hitEllipse(int hitX, int hitY, int hitThreshold) {

		if (onlyEdge) {
			return fillShape.intersects(hitX - hitThreshold, hitY - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold);
		}

		return super.hitEllipse(hitX, hitY, hitThreshold);
	}

	@Override
	protected void updateEllipse() {

		onlyEdge = false;

		Double start0 = getStart(0);
		// if no hole, just draw an ellipse
		if (Double.isNaN(start0)) {
			super.updateEllipse();
			return;
		}

		// check if in view
		Coords M = view.getCoordsForView(conic.getMidpoint3D());
		if (!DoubleUtil.isZero(M.getZ())) { // check if in view
			updateEllipseEdge();
			onlyEdge = true;
			return;
		}
		if (ev == null) {
			ev = new Coords[2];
		}
		for (int j = 0; j < 2; j++) {
			ev[j] = view.getCoordsForView(conic.getEigenvec3D(j));
			if (!DoubleUtil.isZero(ev[j].getZ())) { // check if in view
				updateEllipseEdge();
				onlyEdge = true;
				return;
			}
		}

		// check for huge pixel radius
		double xradius = halfAxes[0] * view.getXscale();
		double yradius = halfAxes[1] * view.getYscale();
		/*
		 * if (xradius > DrawConic.HUGE_RADIUS || yradius >
		 * DrawConic.HUGE_RADIUS) { isVisible = false; return; }
		 */

		// set arc
		if (arc == null) {
			arc = AwtFactory.getPrototype().newArc2D();
		}

		Double extent0 = getExtent(0);
		Double start1 = getStart(1);

		// set the arc type : if one hole, add chord to close the arc, if two
		// holes, let arcs open
		int type;
		if (Double.isNaN(start1)) {
			type = GArc2D.CHORD;
		} else {
			type = GArc2D.OPEN;
		}

		arc.setArc(-halfAxes[0], -halfAxes[1], 2 * halfAxes[0], 2 * halfAxes[1],
				-Math.toDegrees(start0), -Math.toDegrees(extent0), type);

		// if no second hole, just draw one arc
		if (Double.isNaN(start1)) {
			arcs = arc;
		} else {
			if (arcs instanceof GGeneralPath) {
				((GGeneralPath) arcs).reset();
			} else {
				arcs = AwtFactory.getPrototype().newGeneralPath();
			}
			((GGeneralPath) arcs).append(arc, true);

			// second arc
			Double extent1 = getExtent(1);
			arc.setArc(-halfAxes[0], -halfAxes[1], 2 * halfAxes[0],
					2 * halfAxes[1], -Math.toDegrees(start1),
					-Math.toDegrees(extent1), GArc2D.OPEN);

			((GGeneralPath) arcs).append(arc, true);
			((GGeneralPath) arcs).closePath();
		}

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		transform.concatenate(view.getCompanion().getTransform(conic, M, ev));

		// BIG RADIUS: larger than screen diagonal
		int BIG_RADIUS = view.getWidth() + view.getHeight(); // > view's
																// diagonal
		if (xradius < BIG_RADIUS && yradius < BIG_RADIUS) {
			fillShape = transform.createTransformedShape(arcs);
		} else {
			// clip big arc at screen
			fillShape = ClipShape.clipToRect(arcs, transform, -1, -1,
					view.getWidth() + 2, view.getHeight() + 2);
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

		if (endPoints == null) {
			endPoints = new Coords[4];
		}

		Coords m = conic.getOrigin3D(0);
		Coords d = conic.getDirection3D(0);

		endPoints[0] = view
				.getCoordsForView(m.copy().addInsideMul(d, getStart(0)));
		endPoints[1] = view
				.getCoordsForView(m.copy().addInsideMul(d, getEnd(0)));

		m = conic.getOrigin3D(1);
		d = conic.getDirection3D(1);

		endPoints[3] = view
				.getCoordsForView(m.copy().addInsideMul(d, getStart(1)));
		endPoints[2] = view
				.getCoordsForView(m.copy().addInsideMul(d, getEnd(1)));

		GGeneralPath path = AwtFactory.getPrototype().newGeneralPath();

		int numPoints = -1;

		int tx0 = 0, ty0 = 0, x1 = 0, y1 = 0, x2, y2;
		double x, y;

		for (int i = 0; i < 4; i++) {
			if (DoubleUtil.isZero(endPoints[i].getZ())) {
				if (numPoints == -1) { // first point
					x = endPoints[i].getX();
					y = endPoints[i].getY();
					path.moveTo(x, y);
					numPoints++;
					tx0 = view.toScreenCoordX(x);
					ty0 = view.toScreenCoordY(y);
					x1 = tx0;
					y1 = ty0;
				} else {
					x = endPoints[i].getX();
					y = endPoints[i].getY();
					path.lineTo(x, y);
					x2 = view.toScreenCoordX(x);
					y2 = view.toScreenCoordY(y);
					if (lines == null) {
						lines = new GLine2D[4];
					}
					if (lines[numPoints] == null) {
						lines[numPoints] = AwtFactory.getPrototype()
								.newLine2D();
					}
					lines[numPoints].setLine(x1, y1, x2, y2);
					x1 = x2;
					y1 = y2;
					numPoints++;
				}
			}
		}

		if (numPoints > 0) { // close path only if at least two points
			path.closePath();
			if (lines[numPoints] == null) {
				lines[numPoints] = AwtFactory.getPrototype().newLine2D();
			}
			lines[numPoints].setLine(x1, y1, tx0, ty0);
		}

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		fillShape = transform.createTransformedShape(path);

	}

	@Override
	protected void updateDoubleLine() {

		Coords m = conic.getOrigin3D(0);
		Coords d = conic.getDirection3D(0);

		Coords A = view.getCoordsForView(m.copy().addInsideMul(d, getStart(0)));
		Coords B = view.getCoordsForView(m.copy().addInsideMul(d, getEnd(0)));

		if (DoubleUtil.isZero(A.getZ()) && DoubleUtil.isZero(B.getZ())) {
			if (line == null) {
				line = AwtFactory.getPrototype().newLine2D();
			}
			line.setLine(A.getX(), A.getY(), B.getX(), B.getY());
		} else {
			isVisible = false;
			return;
		}

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		fillShape = transform.createTransformedShape(line);
	}

	@Override
	protected void drawLines(GGraphics2D g2) {
		fill(g2, fillShape);
		if (isHighlighted()) {
			g2.setStroke(selStroke);
			g2.setColor(geo.getSelColor());
			g2.draw(fillShape);
		}

		g2.setStroke(objStroke);
		g2.setColor(geo.getObjectColor());
		g2.draw(fillShape);

		if (labelVisible) {
			g2.setFont(view.getFontConic());
			g2.setColor(geo.getLabelColor());
			drawLabel(g2);
		}
	}

	@Override
	public boolean hitLines(int screenx, int screeny, int hitThreshold) {
		if (lines == null) {
			return false;
		}

		for (int i = 0; i < 4; i++) {
			line = lines[i];
			if (line != null) {
				if (line.intersects(screenx - hitThreshold,
						screeny - hitThreshold, 2 * hitThreshold,
						2 * hitThreshold)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected void updateParabolaX0Y0(GAffineTransform trans) {
		// TODO consider not symmetric parabola
		y0 = getEnd(0) * conic.p;
		x0 = y0 * y0 / (conic.p * 2);
	}

	@Override
	protected void updateParabolaEdge() {
		Coords m = conic.getMidpoint3D();
		Coords ev1 = conic.getEigenvec3D(0);
		Coords ev2 = conic.getEigenvec3D(1);

		double t, u, v;

		t = getStart(0);
		u = conic.p * t * t / 2;
		v = conic.p * t;
		Coords A = view.getCoordsForView(
				m.copy().addInsideMul(ev1, u).addInsideMul(ev2, v));

		t = getEnd(0);
		u = conic.p * t * t / 2;
		v = conic.p * t;
		Coords B = view.getCoordsForView(
				m.copy().addInsideMul(ev1, u).addInsideMul(ev2, v));

		if (DoubleUtil.isZero(A.getZ()) && DoubleUtil.isZero(B.getZ())) {
			if (line == null) {
				line = AwtFactory.getPrototype().newLine2D();
			}
			line.setLine(A.getX(), A.getY(), B.getX(), B.getY());
		} else {
			isVisible = false;
			return;
		}

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		fillShape = transform.createTransformedShape(line);
	}

	@Override
	protected void updateParabolaPath() {
		super.updateParabolaPath();
		parabola.closePath();
	}

	@Override
	protected void updateParabolaLabelCoords() {
		labelCoords[0] = 0;
		labelCoords[1] = 0;
	}

	@Override
	protected void updateHyperbolaEdge() {

		Coords m = conic.getMidpoint3D();
		Coords ev1 = conic.getEigenvec3D(0);
		Coords ev2 = conic.getEigenvec3D(1);
		double e1 = conic.getHalfAxis(0);
		double e2 = conic.getHalfAxis(1);

		Coords A = null, B = null;

		double start = getStart(0);
		double end;
		if (!Double.isNaN(start)) { // try first segment
			end = getEnd(0);
			A = view.getCoordsForView(
					m.copy().addInsideMul(ev1, e1 * Math.cosh(start))
							.addInsideMul(ev2, e2 * Math.sinh(start)));
			B = view.getCoordsForView(
					m.copy().addInsideMul(ev1, e1 * Math.cosh(end))
							.addInsideMul(ev2, e2 * Math.sinh(end)));
		} else { // try second segment
			start = getStart(1);
			if (!Double.isNaN(start)) {
				end = getEnd(1);
				A = view.getCoordsForView(
						m.copy().addInsideMul(ev1, -e1 * Math.cosh(start))
								.addInsideMul(ev2, e2 * Math.sinh(start)));
				B = view.getCoordsForView(
						m.copy().addInsideMul(ev1, -e1 * Math.cosh(end))
								.addInsideMul(ev2, e2 * Math.sinh(end)));
			}
		}

		if (A != null && DoubleUtil.isZero(A.getZ()) && DoubleUtil.isZero(B.getZ())) {
			if (line == null) {
				line = AwtFactory.getPrototype().newLine2D();
			}
			line.setLine(A.getX(), A.getY(), B.getX(), B.getY());
		} else {
			isVisible = false;
			return;
		}

		// transform to screen coords
		transform.setTransform(view.getCoordTransform());
		fillShape = transform.createTransformedShape(line);
	}

	@Override
	protected void updateHyperbolaResetPaths() {
		if (firstHyperbola) {
			firstHyperbola = false;
			points = PLOT_POINTS;
			hyp = new GeneralPathClipped(view);
		}
		hyp.resetWithThickness(geo.getLineThickness());
	}

	@Override
	protected void updateHyperbolaX0() {

		double end = getEnd(0);
		if (Double.isNaN(end)) {
			x0 = a * Math.cosh(getEnd(1));
			drawLeft = false;
		} else {
			x0 = a * Math.cosh(end);
			drawLeft = true;
		}
	}

	@Override
	protected void updateHyperbolaAddPoint(int index, double x, double y) {
		if (drawLeft) {
			hyp.addPoint(index, x, y);
		} else {
			hyp.addPoint(index, -x, y);
		}

	}

	@Override
	protected void updateHyperboalSetTransformToPaths() {

		hyp.transform(transform);
	}

	@Override
	protected void updateHyperbolaClosePaths() {

		hyp.closePath();
	}

	@Override
	protected void updateHyperbolaSetShape() {
		fillShape = hyp;
	}

	@Override
	protected void drawHyperbola(GGraphics2D g2) {

		fill(g2, fillShape);

		if (isHighlighted()) {
			g2.setStroke(selStroke);
			g2.setColor(geo.getSelColor());
			g2.draw(fillShape);
		}

		g2.setStroke(objStroke);
		g2.setColor(geo.getObjectColor());
		g2.draw(fillShape);

		if (labelVisible) {
			g2.setFont(view.getFontConic());
			g2.setColor(geo.getLabelColor());
			drawLabel(g2);
		}
	}

	@Override
	protected void updateHyperbolaLabelCoords() {
		if (drawLeft) {
			labelCoords[0] = a;
		} else {
			labelCoords[0] = -a;
		}
		labelCoords[1] = 0;
	}

	@Override
	protected boolean checkHyperbolaOnScreen(GRectangle viewRect) {
		// TODO ?
		return true;
	}

	@Override
	protected boolean checkCircleEllipseParabolaOnScreen(GRectangle viewRect) {
		// TODO ?
		return true;
	}

	@Override
	public boolean hitHyperbola(int hitX, int hitY, int hitThreshold) {
		// TODO ?
		return false;
	}
}
