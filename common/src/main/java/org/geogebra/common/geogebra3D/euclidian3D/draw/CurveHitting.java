package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;

public class CurveHitting {
	private GeoPoint3D hittingPoint;
	private Coords project;
	private double[] lineCoords;
	private EuclidianView3D view;
	private HasZPick parent;

	/**
	 * @param parent
	 *            parent drawable
	 * @param view
	 *            3D view
	 */
	public CurveHitting(HasZPick parent, EuclidianView3D view) {
		hittingPoint = new GeoPoint3D(parent.getGeoElement().getConstruction());
		project = new Coords(4);
		lineCoords = new double[2];
		this.parent = parent;
		this.view = view;
	}

	/**
	 * @param hitting
	 *            hitting
	 * 
	 * @param curve
	 *            curve
	 * @param hitLineThickness
	 *            thickness of the hitting curve
	 * @return whether the curve was hit
	 */
	public boolean hit(Hitting hitting, 
			Path curve, double hitLineThickness) {
		hittingPoint.setWillingCoords(hitting.getOrigin());
		hittingPoint.setWillingDirection(hitting.getDirection());
		GeoElement geo = parent.getGeoElement();
		curve.pointChanged(hittingPoint);

		Coords closestPoint = hittingPoint.getInhomCoordsInD3();
		closestPoint.projectLine(hitting.getOrigin(), hitting.getDirection(), project,
				lineCoords);

		// check if point on line is visible
		if (!hitting.isInsideClipping(project)) {
			return false;
		}

		double d = view.getScaledDistance(project, closestPoint);
		if (d <= hitLineThickness) {
			double z = -lineCoords[0];
			double dz = geo.getLineThickness() / view.getScale();
			parent.setZPickIfBetter(z + dz, z - dz,
					hitting.discardPositiveHits(),
					lineCoords[0]);
			return true;
		}

		return false;
	}
}
