/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.measurement.MeasurementController;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
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
	public RotateBoundingBox(@Nonnull EuclidianController euclidianController,
			@Nonnull MeasurementController measurementController) {
		this.ec = euclidianController;
		this.view = ec.getView();
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
				ec.rotateElement(geo, angle);
			}
		}
		view.repaintView();
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
}
