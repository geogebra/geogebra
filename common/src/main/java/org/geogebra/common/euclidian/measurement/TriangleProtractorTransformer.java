package org.geogebra.common.euclidian.measurement;


import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.matrix.Coords;

public class TriangleProtractorTransformer
		implements PenTransformer {

	private GeoPoint corner1;
	private GeoPoint corner2;
	private GeoPoint corner3;
	private GeoPoint corner4;

	private enum SIDES {
		HYPO,
		LEG_A,
		LEG_B
	}

	private EuclidianView view;
	private List<GPoint> previewPoints;
	private SIDES side;
	private GPoint initialProjection;
	private Construction cons;
	private GeoPoint endPoint1;
	private GeoPoint endPoint2;

	@Override
	public boolean isActive() {
		return initialProjection != null
				&& initialProjection.distance(previewPoints.get(0)) < snapTreshold();
	}

	@Override
	public void reset(EuclidianView view, List<GPoint> previewPoints) {
		this.view = view;
		this.previewPoints = previewPoints;
		cons = view.getKernel().getConstruction();
		GeoImage ruler = cons.getRuler();
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
				getProjection(newPoint, side));
	}

	private void updateInitialProjection(GPoint p) {
		GPoint pA = getProjection(p, SIDES.LEG_A);
		GPoint pB = getProjection(p, SIDES.LEG_B);
		GPoint pHypo = getProjection(p, SIDES.HYPO);
		if (!onBottomEdge(pHypo)) {
			initialProjection = null;
		} else if (p.distance(pA) > p.distance(pHypo) && p.distance(pB) > p.distance(pHypo)) {
			side = SIDES.HYPO;
			initialProjection = pHypo;
		} else if (p.distance(pA) < p.distance(pB)) {
			side = SIDES.LEG_A;
			initialProjection = pA;
		} else {
			side = SIDES.LEG_B;
			initialProjection = pB;
		}
	}

	private boolean onBottomEdge(GPoint bottom) {
		GeoImage ruler = cons.getRuler();
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

	private GPoint getProjection(GPoint p, SIDES side) {
		GeoImage ruler = view.getKernel().getConstruction().getRuler();
		calculateEndPoinds(side, ruler);

		double x = endPoint1.getInhomY() - endPoint2.getInhomY();
		double y = endPoint2.getInhomX() - endPoint1.getInhomX();
		double z = endPoint1.getInhomX() * endPoint2.getInhomY()
				- endPoint1.getInhomY() * endPoint2.getInhomX();
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

	private void calculateEndPoinds(SIDES side, GeoImage image) {
		ensureCorners();

		image.calculateCornerPoint(corner1, 1);
		image.calculateCornerPoint(corner2, 2);
		image.calculateCornerPoint(corner3, 3);
		image.calculateCornerPoint(corner4, 4);

		switch (side) {
		case HYPO:
			endPoint1 = corner1;
			endPoint2 = corner2;
			break;
		case LEG_A:
			endPoint1.x = (corner3.x + corner4.x) / 2;
			endPoint1.y = (corner3.y + corner4.y) / 2;
			endPoint1.z = 1;
			endPoint2 = corner1;
			break;
		case LEG_B:
			endPoint1.x = (corner3.x + corner4.x) / 2;
			endPoint1.y = (corner3.y + corner4.y) / 2;
			endPoint1.z = 1;
			endPoint2 = corner2;
			break;
		}
	}

	private void ensureCorners() {
		if (endPoint1 == null) {
			endPoint1 = new GeoPoint(view.getKernel().getConstruction(), true);
			endPoint2 = new GeoPoint(view.getKernel().getConstruction(), true);
			corner1 = new GeoPoint(view.getKernel().getConstruction(), true);
			corner2 = new GeoPoint(view.getKernel().getConstruction(), true);
			corner3 = new GeoPoint(view.getKernel().getConstruction(), true);
			corner4 = new GeoPoint(view.getKernel().getConstruction(), true);
		}
	}

}
