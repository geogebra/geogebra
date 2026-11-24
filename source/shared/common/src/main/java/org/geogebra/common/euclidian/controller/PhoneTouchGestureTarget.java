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

package org.geogebra.common.euclidian.controller;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.jspecify.annotations.NonNull;

import com.google.j2objc.annotations.Weak;

/**
 * Passes the decisions of {@link TouchGestureArbiter} on to the touch entry points of
 * {@link EuclidianController} that the Android and iOS apps use.
 */
public final class PhoneTouchGestureTarget implements TouchGestureArbiter.Target {

	@Weak
	private final EuclidianController controller;

	/**
	 * @param controller controller of a 2D graphics view
	 */
	public PhoneTouchGestureTarget(@NonNull EuclidianController controller) {
		this.controller = controller;
	}

	@Override
	public int getStartDelay() {
		return controller.getTouchStartDelay();
	}

	@Override
	public void touchStart(@NonNull AbstractEvent event) {
		controller.touchStartPhone(event);
	}

	@Override
	public void touchMove(@NonNull AbstractEvent event) {
		controller.touchMovePhone(event);
	}

	@Override
	public void touchEnd(@NonNull AbstractEvent event) {
		controller.touchEndPhone(event);
	}

	@Override
	public void navigationStart(double x1, double y1, double x2, double y2) {
		controller.touchNavigationStartPhone(x1, y1, x2, y2);
	}

	@Override
	public void navigationMove(double x1, double y1, double x2, double y2) {
		controller.twoTouchMovePhone(x1, y1, x2, y2);
	}

	@Override
	public void navigationEnd() {
		controller.touchNavigationEnd();
	}

	@Override
	public boolean lateTwoTouchStart(double x1, double y1, double x2, double y2) {
		// with a construction in progress the second finger pans the view under the first one
		boolean constructionInProgress = controller.getView().getPreviewDrawable() != null;
		if (!controller.keepsTwoTouchBehavior() && !constructionInProgress) {
			return false;
		}
		controller.twoTouchStartPhone(x1, y1, x2, y2);
		return true;
	}

	@Override
	public void lateTwoTouchMove(double x1, double y1, double x2, double y2) {
		controller.twoTouchMovePhone(x1, y1, x2, y2);
	}

	@Override
	public void lateTwoTouchEnd(@NonNull AbstractEvent lifted, @NonNull AbstractEvent remaining) {
		if (controller.getMovePosition() != null) {
			controller.setMovePosition(null);
			return;
		}
		// the remaining finger goes on as a single touch, without restarting the drag delay
		long pressedTime = controller.getLastMousePressedTime();
		controller.wrapMousePressed(remaining);
		controller.setLastMousePressedTime(pressedTime);
		controller.twoTouchEnd();
	}
}
