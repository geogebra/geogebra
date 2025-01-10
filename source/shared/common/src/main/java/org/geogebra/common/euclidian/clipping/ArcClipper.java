package org.geogebra.common.euclidian.clipping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasCoordinates;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.util.debug.Log;

public class ArcClipper {

	private final EuclidianView view;
	private final GeoElement geo;
	private final GArc2D arc;
	private final GeneralPathClipped arcCroppedToView;
	private final GeoConicND conic;
	// for 2D conics these are just the conic matrix entries, for 3D conics we transform the conic
	// to pixel coords
	double[] conicCoeffsForIntersect = new double[6];
	GAffineTransform unitCircleToScreen = AwtFactory.getPrototype().newAffineTransform();

	/**
	 * @param view parent view
	 * @param conic conic to be clipped
	 * @param geo element providing line thickness
	 */
	public ArcClipper(EuclidianView view, GeoElement geo, GeoConicND conic) {
		this.view = view;
		this.geo = geo;
		this.conic  = conic;
		arc = AwtFactory.getPrototype().newArc2D();
		arcCroppedToView = new GeneralPathClipped(view);
	}

	/**
	 * @param transform transform from eigenvector coords to view coords
	 *
	 * @return clipped path or empty if no intersections
	 */
	public Optional<GShape> clipArc(GAffineTransform transform) {
		GAffineTransform viewTransform = AwtFactory.getPrototype().newAffineTransform();
		viewTransform.setTransform(transform);
		try {
			viewTransform.concatenate(conic.getAffineTransform().createInverse());
		} catch (Exception e) {
			return Optional.empty();
		}
		unitCircleToScreen.setTransform(transform);
		double[] halfAxes = conic.halfAxes;
		unitCircleToScreen.scale(halfAxes[0], halfAxes[1]);
		List<Double> angles = findIntersectionAngles(
				conic, viewTransform, transform);
		if (angles.isEmpty()) {
			return Optional.empty();
		} else {
			prepareClippedPath();
			buildPath(angles, arc, unitCircleToScreen);
			return Optional.of(arcCroppedToView);
		}
	}

	private void buildPath(List<Double> angles, GArc2D arc,
			GAffineTransform unitCircleToScreen) {
		GPoint2D firstStartPoint = null;
		GPoint2D endPoint = null;
		for (int idx = 0; idx + 1 < angles.size(); idx += 2) {
			arc.setArcByCenter(0, 0, 1, angles.get(idx),
					angles.get(idx + 1) - angles.get(idx), GArc2D.OPEN);
			GShape conicArc = unitCircleToScreen.createTransformedShape(arc);
			GPoint2D startPoint = unitCircleToScreen.transform(arc.getStartPoint(), null);
			if (idx == 0) {
				arcCroppedToView.moveTo(startPoint.x, startPoint.y);
				firstStartPoint = startPoint;
			} else if (conic.isFilled()) {
				drawCorners(startPoint, endPoint);
				arcCroppedToView.lineTo(startPoint.x, startPoint.y);
			} else {
				arcCroppedToView.moveTo(startPoint.x, startPoint.y);
			}
			endPoint = unitCircleToScreen.transform(arc.getEndPoint(), null);
			arcCroppedToView.append(conicArc);
		}
		if (conic.isFilled()) {
			drawCorners(firstStartPoint, endPoint);
			arcCroppedToView.closePath();
		}
	}

	private void prepareClippedPath() {
		arcCroppedToView.resetWithThickness(geo.getLineThickness());
	}

	private void drawCorners(GPoint2D endPoint, GPoint2D startPoint) {
		int lastEdge = getEdge(startPoint);
		int nextEdge = getEdge(endPoint);
		if (nextEdge > lastEdge) {
			lastEdge += 4;
		}
		for (int corner = lastEdge; corner > nextEdge; corner--) {
			lineToCorner((corner + 3) % 4);
		}
	}

	private void lineToCorner(int corner) {
		switch (corner) {
		case 1:
			arcCroppedToView.lineTo(-getMargin(), view.getHeight() + getMargin());
			return;
		case 2:
			arcCroppedToView.lineTo(view.getWidth() + getMargin(),
					view.getHeight() + getMargin());
			return;
		case 3:
			arcCroppedToView.lineTo(view.getWidth() + getMargin(), -getMargin());
			return;
		case 0:
			arcCroppedToView.lineTo(-getMargin(), -getMargin());
			return;
		default:
			Log.trace("Invalid corner");
		}
	}

	private int getEdge(GPoint2D point) {
		if (point.x < -getMargin() + 1) {
			return 1;
		}
		if (point.y > view.getHeight() + getMargin() - 1) {
			return 2;
		}
		if (point.x > view.getWidth() + getMargin() - 1) {
			return 3;
		}
		if (point.y < -getMargin() + 1) {
			return 4;
		}
		return 0;
	}

	private List<Double> findIntersectionAngles(
			GeoConicND conic, GAffineTransform viewTransform, GAffineTransform conicTransform) {
		double dx = getMargin();
		double[][] edges = new double[][]{{1, 0, dx},
				{1, 0, -view.getWidth() - dx},
				{0, 1,  dx},
				{0, 1, -view.getHeight() - dx}};
		IntersectionPoint pt1 = new IntersectionPoint();
		IntersectionPoint pt2 = new IntersectionPoint();
		ArrayList<Double> angles = new ArrayList<>();
		GAffineTransform inverse;
		try {
			inverse = conic.isGeoElement3D() ? conicTransform.createInverse()
					: conic.getAffineTransform().createInverse();
		} catch (Exception e) {
			return Collections.emptyList();
		}
		double[] flatView = new double[]{
				viewTransform.getScaleX(), viewTransform.getShearY(), 0,
				viewTransform.getShearX(), viewTransform.getScaleY(), 0,
				viewTransform.getTranslateX(), viewTransform.getTranslateY(), 1
		};
		CoordMatrix viewTrans = new CoordMatrix(3, 3, flatView).inverse();
		int edgeCounter = 0;
		for (double[] edge : edges) {
			edgeCounter++;
			if (conic.isGeoElement3D()) {
				CoordMatrix rwMatrix = conic.getSymmetricMatrix();
				CoordMatrix viewMatrix = viewTrans.transposeCopy().mul(rwMatrix.mul(viewTrans));
				viewMatrix.flattenTo(conicCoeffsForIntersect);
			} else {
				conicCoeffsForIntersect = conic.getFlatMatrix();
				edge[2] = edgeCounter < 3 ? -view.toRealWorldCoordX(-edge[2])
						: -view.toRealWorldCoordY(-edge[2]);
			}
			AlgoIntersectLineConic.intersectLineConic(edge, conicCoeffsForIntersect,
					conic.getType(),
					Kernel.STANDARD_PRECISION, pt1, pt2);
			addAngle(pt1, angles, inverse, conic.halfAxes);
			addAngle(pt2, angles, inverse, conic.halfAxes);
		}
		if (angles.size() >= 2) {
			ArrayList<Double> onscreenAngles = new ArrayList<>();
			angles.sort(Double::compare);
			for (int idx = 1; idx < angles.size(); idx++) {
				if (isArcOnScreen(angles.get(idx), angles.get(idx - 1))) {
					onscreenAngles.add(angles.get(idx - 1));
					onscreenAngles.add(angles.get(idx));
				}
			}
			if (isArcOnScreen(angles.get(angles.size() - 1),
					angles.get(0) + 360)) {
				onscreenAngles.add(angles.get(angles.size() - 1));
				onscreenAngles.add(angles.get(0) + 360);
			}
			return onscreenAngles;
		}
		return angles.size() == 1 ? Collections.emptyList() : angles;
	}

	private double getMargin() {
		return Math.max(2, geo.getLineThickness());
	}

	private boolean isArcOnScreen(double from, double to) {
		GPoint2D out = new GPoint2D();
		double middle = Math.toRadians(from + to) / 2;
		unitCircleToScreen.transform(new GPoint2D(Math.cos(middle), -Math.sin(middle)),
				out);
		return -getMargin() < out.x && out.x < view.getWidth() + getMargin()
				&& -getMargin() < out.y && out.y < view.getHeight() + getMargin();
	}

	private void addAngle(IntersectionPoint pt1, ArrayList<Double> angles,
		GAffineTransform inverse, double[] halfAxes) {
		GPoint2D out = new GPoint2D();
		inverse.transform(
				new GPoint2D(pt1.x, pt1.y), out);
		double angle = -Math.atan2(out.y / halfAxes[1], out.x / halfAxes[0]);
		if (angle < 0) {
			angle += Kernel.PI_2;
		}
		if (Double.isFinite(angle)) {
			angles.add(Math.toDegrees(angle));
		}
	}

	private static class IntersectionPoint implements HasCoordinates {
		private double x;
		private double y;

		@Override
		public void setUndefined() {
			setCoords(Double.NaN, Double.NaN, Double.NaN);
		}

		@Override
		public void setCoords(double x, double y, double z) {
			this.x = x / z;
			this.y = y / z;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public double getZ() {
			return 1;
		}
	}
}
