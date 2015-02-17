package geogebra.common.geogebra3D.euclidianForPlane;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerFor3DCompanion;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

/**
 * controller creator for view from plane
 * 
 * @author mathieu
 *
 */
public class EuclidianControllerForPlaneCompanion extends
		EuclidianControllerFor3DCompanion {

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
			NumberValue radius) {
		return circleFor3D(cons, center, radius);
	}

	@Override
	public GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b,
			boolean coords2D) {
		return super.getSingleIntersectionPoint(a, b, false);
	}

	private Coords getCoordsFromView(double x, double y) {
		return ((EuclidianViewForPlaneCompanion) ec.view.getCompanion())
				.getCoordsFromView(x, y);
	}

	@Override
	public void movePoint(boolean repaint, AbstractEvent event) {

		Coords coords = getCoordsFromView(ec.xRW, ec.yRW);

		// Application.debug("xRW, yRW= "+xRW+", "+yRW+"\n3D coords:\n"+coords);

		// cancel 3D controller stuff
		if (((GeoElement) ec.movedGeoPoint).isGeoElement3D()) {
			((GeoPoint3D) ec.movedGeoPoint).setWillingCoordsUndefined();
			((GeoPoint3D) ec.movedGeoPoint).setWillingDirectionUndefined();
		}

		ec.movedGeoPoint.setCoords(coords, true);
		((GeoElement) ec.movedGeoPoint).updateCascade();

		ec.movedGeoPointDragged = true;

		if (repaint)
			ec.kernel.notifyRepaint();
	}

	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, boolean complex) {

		Coords coords = getCoordsFromView(ec.xRW, ec.yRW);

		GeoPointND ret = ec.kernel.getManager3D().Point3DIn(null,
				ec.view.getPlaneContaining(), coords, !forPreviewable, false);
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
		Coords coords = ec.view.getCoordsForView(point.getInhomCoordsInD3());
		ec.xRW = coords.getX();
		ec.yRW = coords.getY();
	}

	@Override
	protected void processModeLock(Path path) {
		GeoPointND p = createNewPoint(true, path, false);
		((GeoElement) p).update();
		Coords coords = ec.view.getCoordsForView(p.getInhomCoordsInD3());
		ec.xRW = coords.getX();
		ec.yRW = coords.getY();
	}

	@Override
	public ArrayList<GeoElement> removeParentsOfView(ArrayList<GeoElement> list) {
		ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
		for (GeoElement geo : list)
			if (ec.view.isMoveable(geo))
				ret.add(geo);
		return ret;
	}

	@Override
	public boolean viewOrientationForClockwise(boolean clockwise) {
		return ((EuclidianViewForPlaneCompanion) ec.view.getCompanion())
				.viewOrientationForClockwise(clockwise);
	}

	@Override
	public GeoElement[] rotateByAngle(GeoElement geoRot, GeoNumberValue phi,
			GeoPointND Q) {

		return ec.kernel.getManager3D().Rotate3D(null, geoRot, phi, Q,
				ec.view.getDirection());

	}

}
