package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPoint;

/**
 * Controls the snapping behaviour of panning the Euclidean View.
 */
public class SnapController {

	/*
	 * Threshold to both directions, where value larger that this will end the
	 * snapping behaviour.
	 */
	private static final int MOVE_VIEW_THRESHOLD = 50;

	/*
	 * If this distance threshold is crossed without snapping, we consider the
	 * movement without any.
	 */
	private static final int DISTANCE_THRESHOLD = 40;

	/* Distance threshold to consider snapping to either direction. */
	private static final int INITIAL_DISTANE_TRESHOLD = 5;

	/*
	 * The size of the angle in degrees in which we consider snapping. We
	 * consider angles on both side of a directionality, e.g. with angle size
	 * 30, we consider the angles between 75 and 105 and between 255 and 285.
	 */
	private static final int ANGLE_OPENING = 50;

	/**
	 * Describes the state of the controller. The transitions are:
	 *
	 * MAY_SNAP -> SNAPPED_VERTICAL MAY_SNAP -> SNAPPED_HORIZONTAL MAY_SNAP ->
	 * FREE
	 *
	 * SNAPPED_VERTICAL -> FREE SNAPPED_HORIZONTAL -> FREE
	 */
	private enum State {
		MAY_SNAP, SNAPPED_VERTICAL, SNAPPED_HORIZONTAL, FREE
	}

	private State state;
	private GPoint startLocation;
	private GPoint movedLocation;

	/**
	 * Call this to set the initial touch location.
	 *
	 * @param location
	 *            touch location
	 */
	public void touchStarted(GPoint location) {
		startLocation = location;
		state = State.MAY_SNAP;
	}

	/**
	 * Call this to calculate the new state of the controller.
	 *
	 * @param location
	 *            the new touch location
	 */
	public void touchMoved(GPoint location) {
		movedLocation = location;
		transitionState(location);
	}

	/**
	 * Returns the point that describes the delta coordinates.
	 *
	 * @return point of dx and dy
	 */
	public GPoint getDeltaPoint() {
		int dx = movedLocation.x - startLocation.x;
		int dy = movedLocation.y - startLocation.y;
		if (state == State.SNAPPED_HORIZONTAL) {
			dy = 0;
		} else if (state == State.SNAPPED_VERTICAL) {
			dx = 0;
		}

		return new GPoint(dx, dy);
	}

	private void transitionState(GPoint newLocation) {
		int dx = movedLocation.x - startLocation.x;
		int dy = movedLocation.y - startLocation.y;
		double distance = startLocation.distance(movedLocation);
		switch (state) {
		case FREE:
			break;
		case SNAPPED_VERTICAL:
			if (Math.abs(dx) > MOVE_VIEW_THRESHOLD) {
				state = State.FREE;
			}
			break;
		case SNAPPED_HORIZONTAL:
			if (Math.abs(dy) > MOVE_VIEW_THRESHOLD) {
				state = State.FREE;
			}
			break;
		case MAY_SNAP:
			if (startLocation.distance(newLocation) > DISTANCE_THRESHOLD) {
				state = State.FREE;
			} else {
				float angle = calculateAngle(startLocation, movedLocation);
				if (distance > INITIAL_DISTANE_TRESHOLD) {
					if (angleInHorizontal(angle)
							&& Math.abs(dy) <= MOVE_VIEW_THRESHOLD) {
						state = State.SNAPPED_HORIZONTAL;
					} else if (angleInVertical(angle)
							&& Math.abs(dx) <= MOVE_VIEW_THRESHOLD) {
						state = State.SNAPPED_VERTICAL;
					}
				}
			}
		}
	}

	private boolean angleInHorizontal(float angle) {
		float halfOpening = ANGLE_OPENING / 2.0f;
		return angle < halfOpening || angle > 360 - halfOpening;
	}

	private boolean angleInVertical(float angle) {
		float halfOpening = ANGLE_OPENING / 2.0f;
		return (angle > 90 - halfOpening && angle < 90 + halfOpening)
				|| (angle > 270 - halfOpening && angle < 270 + halfOpening);
	}

	private float calculateAngle(GPoint point1, GPoint point2) {
		float angle = (float) Math.toDegrees(
				Math.atan2(point2.y - point1.y, point2.x - point1.x));

		if (angle < 0) {
			angle += 360;
		}

		return angle;
	}
}
