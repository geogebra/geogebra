package org.geogebra.common.euclidian.measurement;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class to transform (or "sitck") the pen to the currently active measuremet tool (ruler or
 * one of the protractors
 */
public final class MeasurementToolTransformer implements PenTransformer {
	private EuclidianView view;
	private List<GPoint> previewPoints;
	private GPoint initialProjection;
	private List<MeasurementToolEdge> edges;
	private MeasurementToolEdge activeEdge;
	private final MeasurementController measurementController;

	public MeasurementToolTransformer(MeasurementController measurementController) {
		this.measurementController = measurementController;
	}

	/**
	 *
	 * @param edges of the measurement tool.
	 */
	public MeasurementToolTransformer(MeasurementController measurementController,
			List<MeasurementToolEdge> edges) {
		this(measurementController);
		this.edges = edges;
	}

	/**
	 * Should be only called after reset
	 * @return whether ruler snapping is active
	 */
	@Override
	public boolean isActive() {
		return initialProjection != null
				&& initialProjection.distance(previewPoints.get(0)) < snapThreshold();
	}

	/**
	 * Reset internal state after a preview point is added
	 */
	@Override
	public void reset(EuclidianView view, List<GPoint> previewPoints) {
		this.view = view;
		this.previewPoints = previewPoints;
		GeoImage toolImage = measurementController.getActiveToolImage();
		if (toolImage == null || previewPoints.isEmpty()) {
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
		if (activeEdge == null) {
			return;
		}

		previewPoints.set(0, initialProjection);
		previewPoints.set(previewPoints.size() - 1,
				getProjection(newPoint, activeEdge));
	}

	private void updateInitialProjection(GPoint p) {
		activeEdge = null;
		if (!onBottomEdge(p)) {
			initialProjection = null;
			return;
		}

		double oldDistance = Double.MAX_VALUE;
		for (MeasurementToolEdge edge: edges) {
			GPoint projection = getProjection(p, edge);
			double distance = p.distance(projection);
			if (distance < oldDistance) {
				initialProjection = projection;
				oldDistance = distance;
				activeEdge = edge;
			}
		}
	}

	private boolean onBottomEdge(GPoint bottom) {
		GeoImage toolImage = measurementController.getActiveToolImage();
		double x1 = view.toScreenCoordXd(toolImage.getStartPoint(0).getInhomX());
		double x2 = view.toScreenCoordXd(toolImage.getStartPoint(1).getInhomX());
		double y1 = view.toScreenCoordYd(toolImage.getStartPoint(0).getInhomY());
		double y2 = view.toScreenCoordYd(toolImage.getStartPoint(1).getInhomY());
		if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
			return (x1 - bottom.x) * (x2 - bottom.x) <= 0;
		} else {
			return (y1 - bottom.y) * (y2 - bottom.y) <= 0;
		}
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private GPoint getProjection(GPoint p, MeasurementToolEdge edge) {
		GeoImage toolImage = measurementController.getActiveToolImage();
		edge.update(toolImage);
		GeoPoint corner1 = edge.getEndpoint2();
		GeoPoint corner2 = edge.getEndpoint1();

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
