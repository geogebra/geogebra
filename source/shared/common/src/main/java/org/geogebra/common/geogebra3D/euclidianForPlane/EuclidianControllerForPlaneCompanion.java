package org.geogebra.common.geogebra3D.euclidianForPlane;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerFor3DCompanion;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.DialogManager.CreateGeoForRotate;

/**
 * controller creator for view from plane
 * 
 * @author mathieu
 *
 */
public class EuclidianControllerForPlaneCompanion
		extends EuclidianControllerFor3DCompanion {

	/**
	 * @param ec
	 *            euclidian controller
	 */
	public EuclidianControllerForPlaneCompanion(EuclidianController ec) {
		super(ec);
	}

	@Override
	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {
		return createCircle2For3D(p0, p1);
	}

	@Override
	protected GeoConicND circle(Construction cons, GeoPointND center,
			GeoNumberValue radius) {
		return circleFor3D(cons, center, radius);
	}

	@Override
	public GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b,
			boolean coords2D) {
		return super.getSingleIntersectionPoint(a, b, false);
	}

	private Coords getCoordsFromView(double x, double y) {
		Coords c = new Coords(4);
		ec.getView().getCompanion().getCoordsFromView(x, y, c);
		return c;
	}

	@Override
	public void movePoint(AbstractEvent event, @Nonnull GeoPointND movedPoint) {
		Coords coords = getCoordsFromView(ec.xRW, ec.yRW);
		// cancel 3D controller stuff
		if (movedPoint.isGeoElement3D()) {
			((GeoPoint3D) movedPoint).setWillingCoordsUndefined();
			((GeoPoint3D) movedPoint).setWillingDirectionUndefined();
		}

		movedPoint.setCoords(coords, true);
		movedPoint.updateCascade();
	}

	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable,
			boolean complex) {

		Coords coords = getCoordsFromView(ec.xRW, ec.yRW);

		GeoPointND ret = ec.getKernel().getManager3D().point3DIn(null,
				ec.getView().getPlaneContaining(), coords, !forPreviewable,
				false);
		return ret;
	}

	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path,
			boolean complex) {
		Coords coords = getCoordsFromView(ec.xRW, ec.yRW);
		return createNewPoint(null, forPreviewable, path, coords.getX(),
				coords.getY(), coords.getZ(), complex, false);
	}

	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region,
			boolean complex) {
		Coords coords = getCoordsFromView(ec.xRW, ec.yRW);
		return ec.createNewPoint(null, forPreviewable, region, coords.getX(),
				coords.getY(), coords.getZ(), complex, false);
	}

	@Override
	protected void processModeLock(GeoPointND point) {
		Coords coords = ec.getView().getCoordsForView(
				point.getInhomCoordsInD3());
		ec.setRwCoords(coords);
	}

	@Override
	protected void processModeLock(Path path) {
		GeoPointND p = createNewPoint(true, path, false);
		((GeoElement) p).update();
		Coords coords = ec.getView().getCoordsForView(p.getInhomCoordsInD3());
		ec.setRwCoords(coords);
	}

	@Override
	public ArrayList<GeoElement> removeParentsOfView(
			ArrayList<GeoElement> list) {
		ArrayList<GeoElement> ret = new ArrayList<>();
		for (GeoElement geo : list) {
			if (ec.getView().isMoveable(geo)) {
				ret.add(geo);
			}
		}
		return ret;
	}

	@Override
	public boolean viewOrientationForClockwise(boolean clockwise, CreateGeoForRotate creator) {
		return ((EuclidianViewForPlaneCompanion) ec.getView().getCompanion())
				.viewOrientationForClockwise(clockwise);
	}

	@Override
	public GeoElement[] rotateByAngle(GeoElement geoRot, GeoNumberValue phi,
			GeoPointND Q) {

		return ec.getKernel().getManager3D().rotate3D(null, geoRot, phi, Q,
				ec.getView().getDirection());

	}

	@Override
	public boolean setCoordsToMouseLoc(GeoPointND loc) {
		loc.setCoords(ec.mouseLoc.x, ec.mouseLoc.y, 1.0);
		return false;
	}

}
