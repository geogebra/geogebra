package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPoint;

/**
 * Controls the snapping behaviour of panning the Euclidean View.
 */
public class SnapController {

    private static final int MOVE_VIEW_THRESHOLD = 50;
    private static final int DISTANCE_THRESHOLD = 40;

    /**
     * Describes the state of the controller.
     * The transitions are:
     *
     *  MAY_SNAP -> SNAPPED_VERTICAL
     *  MAY_SNAP -> SNAPPED_HORIZONTAL
     *  MAY_SNAP -> FREE
     *
     *  SNAPPED_VERTICAL -> FREE
     *  SNAPPED_HORIZONTAL -> FREE
     */
    private enum State {
        MAY_SNAP,
        SNAPPED_VERTICAL,
        SNAPPED_HORIZONTAL,
        FREE
    }

    private State state;
    private GPoint startLocation;
    private GPoint movedLocation;

    /**
     * Call this to set the initial touch location.
     *
     * @param location touch location
     */
    public void touchStarted(GPoint location) {
        startLocation = location;
        state = State.MAY_SNAP;
    }

    /**
     * Call this to calculate the new state of the controller.
     *
     * @param location the new touch location
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
                if (distance > 5) {
                    if ((angle < 25 || angle > 335) && Math.abs(dy) <= MOVE_VIEW_THRESHOLD) {
                        state = State.SNAPPED_HORIZONTAL;
                    } else if ((((angle > 65 && angle < 115) || (angle > 245 && angle < 295)))
                            && Math.abs(dx) <= MOVE_VIEW_THRESHOLD) {
                        state = State.SNAPPED_VERTICAL;
                    }
                }
            }
        }
    }

    private float calculateAngle(GPoint point1, GPoint point2) {
        float angle = (float) Math.toDegrees(Math.atan2(point2.y - point1.y, point2.x - point1.x));

        if (angle < 0) {
            angle += 360;
        }

        return angle;

    }
}
