package org.geogebra.arbase;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;

abstract public class ARGestureManager{

    protected EuclidianView3D mView;
    protected float mScaleFactor = 1.0f;
    protected float[] mPos = new float[2];
    protected boolean isTouched = false;
    protected boolean mUpdateOriginIsWanted = false;
    protected float mAngle;

    public ARGestureManager(EuclidianView3D view) {
        mView = view;
    }

    public void onRotationStart() {
        mView.rememberOrigins();
    }

    protected void onRotation(double angle) {
        mView.setCoordSystemFromMouseMove((int) angle, 0, EuclidianController.MOVE_ROTATE_VIEW);
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }

    public float[] getXYPosition() {
        return new float[]{mPos[0], mPos[1]};
    }

    public boolean getIsTouched() {
        return isTouched;
    }

    public boolean getUpdateOriginIsWanted() {
        return mUpdateOriginIsWanted;
    }

    public void setUpdateOriginIsWanted(boolean updateOriginIsWanted) {
        mUpdateOriginIsWanted = updateOriginIsWanted;
    }

    public float getDAngle() {
        return mAngle;
    }
}
