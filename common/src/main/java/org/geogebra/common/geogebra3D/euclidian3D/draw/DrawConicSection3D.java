package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.draw.DrawConicSection;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.Type;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;

public class DrawConicSection3D extends DrawConic3D {

	public DrawConicSection3D(EuclidianView3D view3d, GeoConicSection conic) {
		super(view3d, conic);
	}

	protected double getStart(int i) {
		return ((GeoConicSection) getGeoElement()).getParameterStart(i);
	}

	protected double getExtent(int i) {
		return ((GeoConicSection) getGeoElement()).getParameterExtent(i);
	}

	protected double getEnd(int i) {
		return ((GeoConicSection) getGeoElement()).getParameterEnd(i);
	}

	@Override
	protected void updateCircle(PlotterBrush brush) {

		updateEllipse(brush);
	}

	@Override
	protected boolean updateForItSelf() {
		createPointsIfNeeded();
		return super.updateForItSelf();
	}

	@Override
	protected void updateEllipse(PlotterBrush brush) {

		double start0 = getStart(0);
		double extent0 = getExtent(0);
		double start1 = getStart(1);
		double extent1 = getExtent(1);

		if (!Double.isNaN(start0)) { // there is at least one hole

			brush.arcEllipse(m, ev1, ev2, e1, e2, start0, extent0);

			if (!Double.isNaN(start1)) { // there is two holes
				brush.setAffineTexture(0.5f, 0.25f);
				brush.segment(
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start0 + extent0),
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start1));
				brush.arcEllipse(m, ev1, ev2, e1, e2, start1, extent1);
				brush.setAffineTexture(0.5f, 0.25f);
				brush.segment(
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start1 + extent1),
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start0));
			} else {
				brush.setAffineTexture(0.5f, 0.25f);
				brush.segment(
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start0 + extent0),
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start0));
			}

		} else { // no hole
			super.updateEllipse(brush);
		}

		// updateSectorSegments(brush, conic.getConicPartType(), m, ev0, ev1,
		// r0, r1, start0, start0+extent0);
	}

	@Override
	protected void updateEllipse(PlotterSurface surface) {

		double start0 = getStart(0);
		double extent0 = getExtent(0);
		double start1 = getStart(1);
		double extent1 = getExtent(1);

		if (!Double.isNaN(start0)) { // there is at least one hole

			surface.ellipsePart(this, m, ev1, ev2, e1, e2, start0, extent0,
					false);

			if (!Double.isNaN(start1)) { // there is two holes
				surface.ellipsePart(this, m, ev1, ev2, e1, e2, start1, extent1,
						false);
				surface.drawQuad(this,
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start0),
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start0 + extent0),
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start1),
						DrawConicSection.ellipsePoint(m, ev1, ev2, e1, e2,
								start1 + extent1));
			}

		} else { // no hole
			super.updateEllipse(surface);
		}

	}

	@Override
	protected double[] getLineMinMax(int i) {
		return new double[] { getStart(i), getEnd(i) };
	}

	@Override
	protected void updateLines(PlotterBrush brush) {

		super.updateLines(brush);

		brush.segment(points[1], points[2]);
		brush.segment(points[3], points[0]);

	}

	@Override
	protected void updateHyperbola(PlotterBrush brush) {

		// first branch
		double start = getStart(0);
		if (!Double.isNaN(start)) {
			double end = getEnd(0);
			brush.hyperbolaBranch(m, ev1, ev2, e1, e2, start, end);
			brush.setAffineTexture(0.5f, 0.25f);
			brush.segment(
					m.add(ev1.mul(e1 * Math.cosh(start)))
							.add(ev2.mul(e2 * Math.sinh(start))),
					m.add(ev1.mul(e1 * Math.cosh(end)))
							.add(ev2.mul(e2 * Math.sinh(end))));
		}

		// second branch
		start = getStart(1);
		if (!Double.isNaN(start)) {
			double end = getEnd(1);
			brush.hyperbolaBranch(m, ev1.mul(-1), ev2, e1, e2, start, end);
			brush.setAffineTexture(0.5f, 0.25f);
			brush.segment(
					m.add(ev1.mul(-e1 * Math.cosh(start)))
							.add(ev2.mul(e2 * Math.sinh(start))),
					m.add(ev1.mul(-e1 * Math.cosh(end)))
							.add(ev2.mul(e2 * Math.sinh(end))));
		}

	}

	@Override
	protected void updateHyperbola(PlotterSurface surface) {

		// first branch
		double start = getStart(0);
		if (!Double.isNaN(start)) {
			surface.hyperbolaPart(this, m, ev1, ev2, e1, e2, start, getEnd(0));
		}

		// second branch
		start = getStart(1);
		if (!Double.isNaN(start)) {
			surface.hyperbolaPart(this, m, ev1.mul(-1), ev2, e1, e2, start,
					getEnd(1));
		}

	}

	@Override
	protected void updateIntersectingLines(PlotterSurface surface) {
		surface.drawTriangle(this, points[0], points[1], points[3]);
	}

	@Override
	protected double[] getParabolaMinMax() {
		return new double[] { getStart(0), getEnd(0) };
	}

	@Override
	protected void updateParabola(PlotterBrush brush) {
		super.updateParabola(brush);
		brush.setAffineTexture(0.5f, 0.25f);
		brush.segment(points[0], points[1]);
	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible()) {
			if (exportSurface) {
				exportToPrinter3D.exportSurface(this, true, true);
			} else {
				exportToPrinter3D.exportCurve(this, Type.CURVE);
			}
		}
	}
}
