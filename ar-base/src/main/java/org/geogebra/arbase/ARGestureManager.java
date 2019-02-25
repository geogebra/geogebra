package org.geogebra.arbase;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;

public class ARGestureManager {

    protected EuclidianView3D mView;

    public ARGestureManager(EuclidianView3D view) {
        mView = view;
    }

    public void onRotationStart() {
        mView.rememberOrigins();
    }

    protected void onRotation(double angle) {
        mView.setCoordSystemFromMouseMove((int) angle, 0, EuclidianController.MOVE_ROTATE_VIEW);
    }
}
