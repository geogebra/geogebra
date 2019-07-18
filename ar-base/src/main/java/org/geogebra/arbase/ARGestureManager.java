package org.geogebra.arbase;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

abstract public class ARGestureManager{

    private EuclidianView3D mView;
    protected float mScaleFactor = 1.0f;
    private Coords mPos = new Coords(2);
    private boolean isTouched = false;
    private boolean mUpdateOriginIsWanted = false;
    private float mAngle;
    private boolean actionPointerLeftPreviously = false;
    private boolean mTaped;

    public ARGestureManager(EuclidianView3D view) {
        mView = view;
    }

    public void onRotationStart() {
        if (mView.getApplication().has(Feature.G3D_AR_REGULAR_TOOLS)) {
            mView.rememberOrigins();
        }
    }

    protected void onRotation(double angle) {
        mView.setCoordSystemFromMouseMove((int) angle, 0, EuclidianController.MOVE_ROTATE_VIEW);
    }

    synchronized public float getScaleFactor() {
        return mScaleFactor;
    }

    synchronized public void resetScaleFactor() {
        mScaleFactor = 1;
    }

    synchronized public void copyXYPosition(Coords ret) {
        ret.setX(mPos.getX());
        ret.setY(mPos.getY());
    }

    synchronized public boolean getIsTouched() {
        return isTouched;
    }

    synchronized public void setIsTouched(boolean flag) {
        isTouched = flag;
    }

    synchronized public boolean getUpdateOriginIsWanted() {
        return mUpdateOriginIsWanted;
    }

    synchronized public void setUpdateOriginIsWanted(boolean updateOriginIsWanted) {
        mUpdateOriginIsWanted = updateOriginIsWanted;
    }

    synchronized public float getDAngle() {
        return mAngle;
    }

    synchronized public void setDAngle(float value) {
        mAngle = value;
    }

    synchronized protected void firstFingerDown(ARMotionEvent event, App app){
        mUpdateOriginIsWanted = true;
        if (app.has(Feature.G3D_AR_REGULAR_TOOLS)) {
            isTouched = false;
        } else {
            isTouched = true;
        }
        updatePos(event);
    }

    synchronized protected void secondFingerDown(ARMotionEvent event){
        isTouched = true;
        mUpdateOriginIsWanted = true;
        updatePos(event);
    }

    synchronized protected void onMove(ARMotionEvent event, App app){
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

    synchronized protected void firstFingerUp(App app){
        isTouched = false;
        if (!app.has(Feature.G3D_AR_REGULAR_TOOLS)) {
            mView.getEuclidianController().clearSelections();
        }
    }

    synchronized protected void secondFingerUp(App app){
        if (app.has(Feature.G3D_AR_REGULAR_TOOLS)) {
            isTouched = false;
        } else {
            isTouched = true;
        }
        mUpdateOriginIsWanted = true;
        actionPointerLeftPreviously = true;
    }

    synchronized protected void actionCancelled(){
        isTouched = false;
    }

    private void updatePos(ARMotionEvent event) {
        float x, y;
        if (event.getPointerCount() == 2) {
            float nfX, nfY, nsX, nsY;
            nsX = event.getX(0);
            nsY = event.getY(0);
            nfX = event.getX(1);
            nfY = event.getY(1);

            x = (nfX + nsX) / 2;
            y = (nfY + nsY) / 2;

        } else {
            x = event.getX(0);
            y = event.getY(0);
        }
        setPos(x, y);
    }

    synchronized protected void setPos(double x, double y) {
        mPos.setX(x);
        mPos.setY(y);
    }

    abstract public void addGestureRecognizers();

    synchronized public boolean isTaped() {
        return mTaped;
    }

    synchronized public void setTaped(boolean taped) {
        mTaped = taped;
    }
}
