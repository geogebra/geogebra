package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.measurement.MeasurementController;
import org.geogebra.common.kernel.Construction;
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
	private final Construction construction;
	private final EuclidianController ec;
	private final MeasurementController measurementController;
	private EuclidianView view;

	/**
	 *
	 * @param euclidianController {@link EuclidianController}
	 * @param measurementController {@link MeasurementController}
	 */
	public RotateBoundingBox(EuclidianController euclidianController,
			MeasurementController measurementController) {
		this.ec = euclidianController;
		this.measurementController = measurementController;
		construction = ec.getKernel().getConstruction();
	}

	boolean rotate(GRectangle2D bounds, double eventX, double eventY) {
		if (ec.lastMouseLoc == null) {
			return true;
		}

		GPoint2D eventPoint = clampToView(eventX, eventY);
		GPoint2D center = calculateRotationCenter(bounds);
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

	private GPoint2D calculateRotationCenter(GRectangle2D bounds) {
		ArrayList<GeoElement> selectedGeos = ec.selection.getSelectedGeos();
		GeoElement selectedGeo = selectedGeos.isEmpty() ? null : selectedGeos.get(0);
		GPoint2D activeToolCenter =
				measurementController.getActiveToolCenter(selectedGeo, view);
		if (activeToolCenter != null) {
			return activeToolCenter;
		}

		double x = bounds.getMinX() + bounds.getWidth() / 2;
		double y = bounds.getMinY()  + bounds.getHeight() / 2;
		return new GPoint2D(x, y);
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

	void setView(EuclidianView view) {
		this.view = view;
	}
}
