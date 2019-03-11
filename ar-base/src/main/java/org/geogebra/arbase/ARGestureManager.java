package org.geogebra.arbase;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

abstract public class ARGestureManager{

    private EuclidianView3D mView;
    protected float mScaleFactor = 1.0f;
    protected Coords mPos = new Coords(2);
    protected boolean isTouched = false;
    private boolean mUpdateOriginIsWanted = false;
    protected float mAngle;
    private boolean actionPointerLeftPreviously = false;
    private float x, y;
    private boolean mTaped;
    private float rotationOffset;

    public ARGestureManager(EuclidianView3D view) {
        mView = view;
    }

    public void onRotationStart() {
        if (mView.getApplication().has(Feature.G3D_AR_REGULAR_TOOLS)) {
            mView.rememberOrigins();
        }
    }

    protected void onRotation(double angle) {
        if (mView.getApplication().has(Feature.G3D_AR_REGULAR_TOOLS)) {
            mView.setCoordSystemFromMouseMove((int) angle, 0, EuclidianController.MOVE_ROTATE_VIEW);
        } else {
            mAngle = ((float) angle) + rotationOffset;
        }
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }

    public void copyXYPosition(Coords ret) {
        ret.setX(mPos.getX());
        ret.setY(mPos.getY());
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

    protected void firstFingerDown(ARMotionEvent event, App app){
        mUpdateOriginIsWanted = true;
        if (app.has(Feature.G3D_AR_REGULAR_TOOLS)) {
            isTouched = false;
        } else {
            isTouched = true;
        }
        updatePos(event);
    }

    protected void secondFingerDown(ARMotionEvent event){
        isTouched = true;
        mUpdateOriginIsWanted = true;
        updatePos(event);
    }

    protected void onMove(ARMotionEvent event, App app){
        if (app.has(Feature.G3D_AR_REGULAR_TOOLS)) {
            isTouched = event.getPointerCount() > 1;
        } else {
            isTouched = true;
        }

        if (actionPointerLeftPreviously) {
            mUpdateOriginIsWanted = true;
            actionPointerLeftPreviously = false;
        }
        updatePos(event);
    }

    protected void firstFingerUp(App app){
        isTouched = false;
        if (app.has(Feature.G3D_AR_REGULAR_TOOLS)) {
            mView.getEuclidianController().clearSelections();
        }
    }

    protected void secondFingerUp(App app){
        if (app.has(Feature.G3D_AR_REGULAR_TOOLS)) {
            isTouched = false;
        } else {
            isTouched = true;
            rotationOffset = mAngle;
        }
        mUpdateOriginIsWanted = true;
        actionPointerLeftPreviously = true;
    }

    protected void actionCancelled(){
        isTouched = false;
    }

    protected void updatePos(ARMotionEvent event) {
        if (event.getPointerCount() == 2) {
            float nfX, nfY, nsX, nsY;
            nsX = event.getX(0);
            nsY = event.getY(0);
            nfX = event.getX(1);
            nfY = event.getY(1);

            x = (nfX + nsX) / 2;
            y = (nfY + nsY) / 2;

        } else if (event.getPointerCount() == 1) {
            x = event.getX(0);
            y = event.getY(0);
        }
        mPos.setX(x);
        mPos.setY(y);
    }

    abstract public void addGestureRecognizers();

    public boolean isTaped() {
        return mTaped;
    }

    public void setTaped(boolean taped) {
        mTaped = taped;
    }
}
