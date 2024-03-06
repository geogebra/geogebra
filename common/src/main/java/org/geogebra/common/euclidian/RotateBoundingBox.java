package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.draw.DrawImageResizable;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MeasurementTool;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.Rotatable;
import org.geogebra.common.util.MyMath;

/**
 * Class to handle rotating of objects
 */
public class RotateBoundingBox {
	private static final double PROTRACTOR_CENTER_IN_PERCENT = 1 - (278.86 / 296);
	private final Construction construction;
	private final EuclidianController ec;
	private EuclidianView view;

	RotateBoundingBox(EuclidianController euclidianController) {
		this.ec = euclidianController;
		construction = ec.getKernel().getConstruction();
	}

	boolean rotate(GRectangle2D bounds, double eventX, double eventY) {
		if (ec.lastMouseLoc == null) {
			return true;
		}

		GPoint2D eventPoint = clampToView(eventX, eventY);
		GPoint2D center = isProtractorSelected()
				? calculateProtractorRotationCenter()
				: calculateRotationCenter(bounds);
		ensureRotationCenter(center);
		NumberValue rotationAngle = calculateAngle(center, eventPoint);

		if (ec.getResizedShape() != null || ec.isMultiResize) {
			ec.dontClearSelection = true;
			ec.hideDynamicStylebar();
			rotateSelectedGeos(rotationAngle);
			return true;
		}
		return false;
	}

	private GPoint2D calculateProtractorRotationCenter() {
		List<GPoint2D> points = getProtractorPoints();
		if (points == null || points.size() < 3) {
			return new GPoint2D(0, 0);
		}

		GPoint2D p0 = points.get(0);
		GPoint2D p1 = points.get(1);
		GPoint2D p2 = points.get(2);

		return new GPoint2D(getRotatedCoord(p0.x, p1.x, p2.x),
				getRotatedCoord(p0.y, p1.y, p2.y));
	}

	private List<GPoint2D> getProtractorPoints() {
		MeasurementTool tool = construction.getActiveMeasurementTool();
		if (tool == null || !tool.isProtactor()) {
			return null;
		}

		DrawImageResizable drawable =
				(DrawImageResizable) ec.getView().getDrawableFor(tool.getImage());
		return drawable != null ? drawable.toPoints() : null;
	}

	private double getRotatedCoord(double v0, double v1, double v2) {
		return ((v0 + v1) / 2) * PROTRACTOR_CENTER_IN_PERCENT
				+ ((2 * v2 + v1 - v0) / 2)
				* (1 - PROTRACTOR_CENTER_IN_PERCENT);
	}

	private void rotateSelectedGeos(NumberValue angle) {
		for (GeoElement geo : ec.selection.getSelectedGeos()) {
			if (isRotationAllowed(geo)) {
				((Rotatable) geo).rotate(angle, ec.rotationCenter);
				geo.updateRepaint();
			}
		}
	}

	private boolean isRotationAllowed(GeoElement geo) {
		return !geo.isGeoPoint() && !ec.isLockedForMultiuser(geo);
	}

	private void ensureRotationCenter(GPoint2D center) {
		if (ec.rotationCenter != null) {
			return;
		}

		ec.rotationCenter = new GeoPoint(
				construction,
				view.toRealWorldCoordX(center.x),
				view.toRealWorldCoordY(center.y), 1);
	}

	// lastMouseLoc is not updated outside the view, but the event
	// contains values in that region too, so we clamp them
	private GPoint2D clampToView(double eventX, double eventY) {
		return new GPoint2D(
				MyMath.clamp(eventX, 0, view.getWidth()),
				MyMath.clamp(eventY, 0, view.getHeight())
		);
	}

	private NumberValue calculateAngle(GPoint2D center, GPoint2D eventPoint) {
		return new GeoNumeric(construction,
				Math.atan2(-(eventPoint.y - center.y), eventPoint.x - center.x)
						- Math.atan2(-(ec.lastMouseLoc.getY() - center.y),
						ec.lastMouseLoc.getX() - center.x));
	}

	private GPoint2D calculateRotationCenter(GRectangle2D bounds) {
		double x = bounds.getMinX() + bounds.getWidth() / 2;
		double y = bounds.getMinY()  + bounds.getHeight() / 2;
		return new GPoint2D(x, y);
	}

	private boolean isProtractorSelected() {
		ArrayList<GeoElement> selectedGeos = ec.selection.getSelectedGeos();
		MeasurementTool tool = construction.getActiveMeasurementTool();

		return !selectedGeos.isEmpty() && selectedGeos.get(0) == tool.getImage()
				&& tool.isProtactor();
	}

	void setView(EuclidianView view) {
		this.view = view;
	}
}
