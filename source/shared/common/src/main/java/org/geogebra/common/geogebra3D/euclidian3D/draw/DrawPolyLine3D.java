package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyLine3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing 1D coord sys (lines, segments, ...)
 * 
 * @author matthieu
 *
 */
public class DrawPolyLine3D extends Drawable3DCurves implements Previewable {

	private double[] drawMinMax = new double[2];
	private Coords boundsMin = new Coords(3);
	private Coords boundsMax = new Coords(3);
	private ArrayList<GeoPointND> selectedPoints;

	/**
	 * common constructor
	 * 
	 * @param a_view3D
	 *            view
	 * @param p
	 *            polyline
	 */
	public DrawPolyLine3D(EuclidianView3D a_view3D, GeoElement p) {
		super(a_view3D, p);
	}

	/**
	 * common constructor for previewable
	 * 
	 * @param a_view3d
	 *            view
	 * @param points
	 *            preview points
	 */
	public DrawPolyLine3D(EuclidianView3D a_view3d,
			ArrayList<GeoPointND> points) {
		super(a_view3d);
		// p.setIsPickable(false);
		// setGeoElement(p);

		setGeoElement(
				new GeoPolyLine3D(a_view3d.getKernel().getConstruction()));
		this.selectedPoints = points;

		updatePreview();

	}

	/**
	 * @return the min-max extremity
	 */
	public double[] getDrawMinMax() {
		return drawMinMax;
	}

	// ///////////////////////////////////////
	// DRAWING GEOMETRIES

	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}

	@Override
	protected boolean updateForItSelf() {
		double[] minmax = getDrawMinMax();

		if (Math.abs(minmax[0]) > 1E10) {
			return true;
		}

		if (Math.abs(minmax[1]) > 1E10) {
			return true;
		}

		if (minmax[0] > minmax[1]) {
			return true;
		}

		Renderer renderer = getView3D().getRenderer();

		setPackCurve();
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.start(getReusableGeometryIndex());
		brush.setThickness(getLineThickness(), (float) getView3D().getScale());
		// brush.setColor(getGeoElement().getObjectColor());
		brush.setAffineTexture(
				(float) ((0.5 - minmax[0]) / (minmax[1] - minmax[0])), 0.25f);
		GeoPolyLine p = (GeoPolyLine) getGeoElement();
		int num = p.getNumPoints();
		if (num > 0) {
			Coords previous = p.getPointND(0).getInhomCoordsInD3();
			boundsMin.setValues(previous, 3);
			boundsMax.setValues(previous, 3);
			for (int i = 1; i < num; i++) {
				Coords current = p.getPointND(i).getInhomCoordsInD3();
				brush.segment(previous, current);
				previous = current;
				enlargeBounds(boundsMin, boundsMax, current);
			}
		}

		setGeometryIndex(brush.end());
		endPacking();

		return true;
	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
		enlargeBounds(min, max, boundsMin, boundsMax);
	}

	/**
	 * @return the line thickness
	 */
	protected int getLineThickness() {
		return getGeoElement().getLineThickness();
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_PATH;
	}

	// //////////////////////////////
	// Previewable interface

	@Override
	public void updateMousePos(double xRW, double yRW) {
		// TODO
	}

	@Override
	public void updatePreview() {
		if (selectedPoints.size() > 0) {
			GeoPointND[] points = new GeoPointND[selectedPoints.size() + 1];

			for (int i = 0; i < selectedPoints.size(); i++) {
				points[i] = selectedPoints.get(i);
			}
			points[selectedPoints.size()] = getView3D().getCursor3D();
			((GeoPolyLine) getGeoElement()).setPoints(points);
			((GeoPolyLine) getGeoElement()).setDefined();
			getGeoElement().setEuclidianVisible(true);
			getGeoElement().setVisibleInView3D(true);
			setWaitForUpdate();
		} else {
			getGeoElement().setEuclidianVisible(false);
		}

	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()) {
			updateForItSelf();
		}
	}

	@Override
	protected void updateGeometriesColor() {
		updateGeometriesColor(false);
	}

	@Override
	protected void setGeometriesVisibility(boolean visible) {
		setGeometriesVisibilityNoSurface(visible);
	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible() && getLineThickness() > 0) {
			exportToPrinter3D.exportCurve(this, ExportToPrinter3D.Type.CURVE);
		}
	}

}
