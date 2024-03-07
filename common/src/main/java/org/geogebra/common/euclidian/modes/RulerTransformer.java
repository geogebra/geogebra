package org.geogebra.common.euclidian.modes;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.matrix.Coords;

public class RulerTransformer implements PenTransformer {
	private static final double SNAP_THRESHOLD = 24;
	private GeoPoint corner1;
	private GeoPoint corner2;
	private final EuclidianView view;
	private final ArrayList<GPoint> previewPoints;
	private boolean rulerTop;
	private GPoint initialProjection;

	/**
	 * @param view view
	 * @param previewPoints pen preview points
	 */
	public RulerTransformer(EuclidianView view,
			ArrayList<GPoint> previewPoints) {
		this.view = view;
		this.previewPoints = previewPoints;
	}

	/**
	 * Should be only called after reset
	 * @return whether ruler snapping is active
	 */
	@Override
	public boolean isActive() {
		return initialProjection != null
				&& initialProjection.distance(previewPoints.get(0)) < SNAP_THRESHOLD;
	}

	/**
	 * Reset internal state after a preview point is added
	 */
	@Override
	public void reset() {
		GeoImage ruler = view.getKernel().getConstruction().getRuler();
		if (ruler == null || previewPoints.isEmpty()) {
			initialProjection = null;
		} else if (previewPoints.size() == 1) {
			updateInitialProjection(previewPoints.get(0));
		}
	}

	/**
	 * Update preview points to stick to ruler. Assumes that isActive() is true.
	 * @param newPoint newly added point
	 */
	@Override
	public void updatePreview(GPoint newPoint) {
		previewPoints.set(0, initialProjection);
		previewPoints.set(previewPoints.size() - 1,
				getProjection(newPoint, rulerTop));
	}

	private void updateInitialProjection(GPoint p) {
		GPoint top = getProjection(p, true);
		GPoint bottom = getProjection(p, false);
		if (!onBottomEdge(bottom)) {
			initialProjection = null;
		} else if (p.distance(top) > p.distance(bottom)) {
			rulerTop = false;
			initialProjection =  bottom;
		} else {
			rulerTop = true;
			initialProjection = top;
		}
	}

	private boolean onBottomEdge(GPoint bottom) {
		GeoImage ruler = view.getKernel().getConstruction().getRuler();
		double x1 = view.toScreenCoordXd(ruler.getStartPoints()[0].getInhomX());
		double x2 = view.toScreenCoordXd(ruler.getStartPoints()[1].getInhomX());
		double y1 = view.toScreenCoordYd(ruler.getStartPoints()[0].getInhomY());
		double y2 = view.toScreenCoordYd(ruler.getStartPoints()[1].getInhomY());
		if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
			return (x1 - bottom.x) * (x2 - bottom.x) <= 0;
		} else {
			return (y1 - bottom.y) * (y2 - bottom.y) <= 0;
		}
	}

	private GPoint getProjection(GPoint p, boolean top) {
		GeoImage ruler = view.getKernel().getConstruction().getRuler();
		if (corner2 == null) {
			corner2 = new GeoPoint(view.getKernel().getConstruction(), true);
			corner1 = new GeoPoint(view.getKernel().getConstruction(), true);
		}
		ruler.calculateCornerPoint(corner2, top ? 3 : 1);
		ruler.calculateCornerPoint(corner1, top ? 4 : 2);

		double x = corner2.getInhomY() - corner1.getInhomY();
		double y = corner1.getInhomX() - corner2.getInhomX();
		double z = corner2.getInhomX() * corner1.getInhomY()
				- corner2.getInhomY() * corner1.getInhomX();
		Coords line = new Coords(x, y, z);
		Coords normal = new Coords(y, -x, -y * view.toRealWorldCoordX(p.getX())
				+ x * view.toRealWorldCoordY(p.getY()));
		Coords intersect = line.crossProduct(normal);
		double xn = x / Math.hypot(x, y);
		double yn = y / Math.hypot(x, y);
		double thickness = view.getEuclidianController().getPen().getPenSize() / 2.0;
		double transformedX = view.toScreenCoordXd(intersect.getX() / intersect.getZ())
				- xn * thickness;
		double transformedY = view.toScreenCoordYd(intersect.getY() / intersect.getZ())
				+ yn * thickness;
		return new GPoint((int) Math.round(transformedX), (int) Math.round(transformedY));
	}

}
