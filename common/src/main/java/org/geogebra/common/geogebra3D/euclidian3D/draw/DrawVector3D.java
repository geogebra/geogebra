package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing vectors
 * 
 * @author matthieu
 *
 */
public class DrawVector3D extends DrawJoinPoints {
	private Coords[] points = { Coords.createInhomCoorsInD3(),
			Coords.createInhomCoorsInD3() };
	private Coords boundsMin = new Coords(3);
	private Coords boundsMax = new Coords(3);
    private double radius = 0;

	/**
	 * Common constructor
	 * 
	 * @param view3D
	 *            view
	 * @param vector
	 *            vector
	 */
	public DrawVector3D(EuclidianView3D view3D, GeoVectorND vector) {

		super(view3D, (GeoElement) vector);

		setDrawMinMax(0, 1);
	}

	@Override
	protected void setArrowTypeBefore(PlotterBrush brush) {
		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
	}

	@Override
	protected void setArrowTypeAfter(PlotterBrush brush) {
		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()) {
			updateForItSelf();
		}
	}

	// //////////////////////////////
	// Previewable interface

	/**
	 * constructor for previewable
	 * 
	 * @param view3D
	 *            view
	 * @param selectedPoints
	 *            endpoints
	 */
	public DrawVector3D(EuclidianView3D view3D,
			ArrayList<GeoPointND> selectedPoints) {

		super(view3D, selectedPoints,
				new GeoVector3D(view3D.getKernel().getConstruction()));
	}

	@Override
	protected void setPreviewableCoords(GeoPointND firstPoint,
			GeoPointND secondPoint) {
		((GeoVector3D) getGeoElement())
				.setCoords(secondPoint.getInhomCoordsInD3()
						.sub(firstPoint.getInhomCoordsInD3()).get());
		try {
			((GeoVector3D) getGeoElement()).setStartPoint(firstPoint);
		} catch (CircularDefinitionException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Coords[] calcPoints() {
		GeoVectorND geo = ((GeoVectorND) getGeoElement());

		geo.updateStartPointPosition();

		if (geo.getStartPoint() == null) {
			points[0].set(0, 0, 0);
		} else {
			points[0].set3(geo.getStartPoint().getInhomCoordsInD3());
		}
		points[1].setAdd3(points[0], geo.getCoordsInD3());

		return points;
	}

	@Override
	protected void setStartEndPoints(Coords p1, Coords p2) {
		super.setStartEndPoints(p1, p2);

		radius = getLineThickness() * PlotterBrush.LINE3D_THICKNESS
				/ getView3D().getScale();

		for (int i = 1; i <= 3; i++) {
			if (p1.get(i) < p2.get(i)) {
				boundsMin.set(i, p1.get(i));
				boundsMax.set(i, p2.get(i));
			} else {
				boundsMin.set(i, p2.get(i));
				boundsMax.set(i, p1.get(i));
			}
		}
	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
        if (dontExtend) {
            enlargeBounds(min, max, boundsMin, boundsMax);
        } else {
            enlargeBounds(min, max, boundsMin, boundsMax, radius);
        }
	}

}
