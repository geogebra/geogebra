package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Class for drawing 1D coord sys (lines, segments, ...)
 * 
 * @author matthieu
 *
 */
public class DrawPolyLine3D extends Drawable3DCurves implements Previewable {

	private double[] drawMinMax = new double[2];

	/**
	 * common constructor
	 * 
	 * @param a_view3D
	 * @param cs1D
	 */
	public DrawPolyLine3D(EuclidianView3D a_view3D, GeoElement p) {

		super(a_view3D, p);
	}

	/**
	 * common constructor for previewable
	 * 
	 * @param a_view3d
	 */
	public DrawPolyLine3D(EuclidianView3D a_view3d) {
		super(a_view3d);

	}

	/**
	 * sets the values of drawable extremities
	 * 
	 * @param drawMin
	 * @param drawMax
	 */
	public void setDrawMinMax(double drawMin, double drawMax) {
		this.drawMinMax[0] = drawMin;
		this.drawMinMax[1] = drawMax;
	}

	/**
	 * @return the min-max extremity
	 */
	public double[] getDrawMinMax() {
		return drawMinMax;
	}

	// ///////////////////////////////////////
	// DRAWING GEOMETRIES

	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}
	
	
	protected boolean updateForItSelf() {

		// updateColors();

		GeoPolyLine p = (GeoPolyLine) getGeoElement();
		int num = p.getNumPoints();

		double[] minmax = getDrawMinMax();

		if (Math.abs(minmax[0]) > 1E10)
			return true;

		if (Math.abs(minmax[1]) > 1E10)
			return true;

		if (minmax[0] > minmax[1])
			return true;

		Renderer renderer = getView3D().getRenderer();

		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.start(getReusableGeometryIndex());
		brush.setThickness(getLineThickness(), (float) getView3D().getScale());
		// brush.setColor(getGeoElement().getObjectColor());
		brush.setAffineTexture(
				(float) ((0.5 - minmax[0]) / (minmax[1] - minmax[0])), 0.25f);

		if (num > 0){
			Coords previous = p.getPointND(0).getInhomCoordsInD3();
			boundsMin.setValues(previous, 3);
			boundsMax.setValues(previous, 3);
			for (int i = 1; i < num; i++){
				Coords current = p.getPointND(i).getInhomCoordsInD3();
				brush.segment(previous, current);
				previous = current;
				enlargeBounds(boundsMin, boundsMax, current);
			}
		}

		setGeometryIndex(brush.end());

		return true;
	}
	
	private Coords boundsMin = new Coords(3), boundsMax = new Coords(3);

	
	@Override
	public void enlargeBounds(Coords min, Coords max) {
		enlargeBounds(min, max, boundsMin, boundsMax);
	}

	/**
	 * update the drawable as a segment from p1 to p2
	 * 
	 * @param p1
	 * @param p2
	 */
	protected void updateForItSelf(Coords p1, Coords p2) {

		// TODO prevent too large values

	}

	/**
	 * @return the line thickness
	 */
	protected int getLineThickness() {
		return getGeoElement().getLineThickness();
	}

	public int getPickOrder() {
		return DRAW_PICK_ORDER_PATH;
	}

	// //////////////////////////////
	// Previewable interface

	@SuppressWarnings("unchecked")
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * 
	 * @param a_view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawPolyLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints,
			GeoPolyLine p) {

		super(a_view3D);

		p.setIsPickable(false);
		setGeoElement(p);

		this.selectedPoints = selectedPoints;

		updatePreview();

	}

	public void updateMousePos(double xRW, double yRW) {

	}

	public void updatePreview() {

		if (selectedPoints.size() > 0) {
			GeoPointND[] points = new GeoPointND[selectedPoints.size()];

			for (int i = 0; i < selectedPoints.size(); i++) {
				points[i] = (GeoPointND) selectedPoints.get(i);
			}

			((GeoPolyLine) getGeoElement()).setPoints(points);

			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		} else {
			getGeoElement().setEuclidianVisible(false);
		}

	}

	protected void updateForView() {
		if (getView3D().viewChangedByZoom())
			updateForItSelf();
	}

}
