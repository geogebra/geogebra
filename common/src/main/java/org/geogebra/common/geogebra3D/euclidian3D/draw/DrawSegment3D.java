package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Class for drawing segments
 * 
 * @author matthieu
 *
 */
public class DrawSegment3D extends DrawCoordSys1D {

	/**
	 * Common constructor
	 * 
	 * @param a_view3D
	 * @param segment
	 */
	public DrawSegment3D(EuclidianView3D a_view3D, GeoSegmentND segment) {

		super(a_view3D, (GeoElement) segment);

		setDrawMinMax(0, 1);
	}

	@Override
	public boolean doHighlighting() {

		// if the segments depends on a polygon (or polyhedron), look at the
		// poly' highlighting
		GeoElement meta = ((GeoSegmentND) getGeoElement()).getMetas()[0];
		if (meta != null && meta.doHighlighting())
			return true;

		return super.doHighlighting();
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
	 * Constructor for previewable
	 * 
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawSegment3D(EuclidianView3D a_view3D, ArrayList selectedPoints) {

		super(a_view3D, selectedPoints, new GeoSegment3D(a_view3D.getKernel()
				.getConstruction()));

		setDrawMinMax(0, 1);

	}

	@Override
	protected Coords[] calcPoints() {
		GeoSegmentND seg = (GeoSegmentND) getGeoElement();
		return new Coords[] { seg.getStartInhomCoords(),
				seg.getEndInhomCoords() };
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
