package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Class for drawing vectors
 * 
 * @author matthieu
 *
 */
public class DrawVector3D extends DrawJoinPoints {

	/**
	 * Common constructor
	 * 
	 * @param view3D
	 * @param vector
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
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawVector3D(EuclidianView3D view3D, ArrayList selectedPoints) {

		super(view3D, selectedPoints, new GeoVector3D(view3D.getKernel()
				.getConstruction()));

	}

	@Override
	protected void setPreviewableCoords(GeoPointND firstPoint,
			GeoPointND secondPoint) {
		((GeoVector3D) getGeoElement()).setCoords(secondPoint
				.getInhomCoordsInD3().sub(firstPoint.getInhomCoordsInD3())
				.get());
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

		Coords p1;
		if (geo.getStartPoint() == null) {
			p1 = new Coords(4);
			p1.setW(1);
		} else
			p1 = geo.getStartPoint().getInhomCoordsInD3();
		Coords p2 = p1.add(geo.getCoordsInD3());

		return new Coords[] { p1, p2 };
	}

	private Coords boundsMin = new Coords(3), boundsMax = new Coords(3);

	@Override
	protected void setStartEndPoints(Coords p1, Coords p2) {
		super.setStartEndPoints(p1, p2);

		double radius = getLineThickness() * PlotterBrush.LINE3D_THICKNESS
				/ getView3D().getScale();

		for (int i = 1; i <= 3; i++) {
			if (p1.get(i) < p2.get(i)) {
				boundsMin.set(i, p1.get(i) - radius);
				boundsMax.set(i, p2.get(i) + radius);
			} else {
				boundsMin.set(i, p2.get(i) - radius);
				boundsMax.set(i, p1.get(i) + radius);
			}
		}
	}

	@Override
	public void enlargeBounds(Coords min, Coords max) {
		enlargeBounds(min, max, boundsMin, boundsMax);
	}

}
