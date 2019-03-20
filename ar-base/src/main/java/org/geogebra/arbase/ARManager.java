package org.geogebra.arbase;


import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

abstract public class ARManager<TouchEventType> {

    protected CoordMatrix4x4 viewMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 projectMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 mModelMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 mAnchorMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 scaleMatrix = CoordMatrix4x4.identity();
    protected CoordMatrix4x4 tmpMatrix1 = new CoordMatrix4x4();
    protected CoordMatrix4x4 tmpMatrix2 = new CoordMatrix4x4();
    protected float mScaleFactor = 1;
    protected float rotateAngel = 0;
    protected Coords hittingFloor = Coords.createInhomCoorsInD3();
    protected boolean hittingFloorOk;
    protected double hittingDistance;

    private Coords tmpCoords1 = new Coords(4);
    private Coords tmpCoords2 = new Coords(4);
    private Coords tmpCoords3 = new Coords(4);

    private Coords lastHitOrigin = new Coords(3);
    protected Coords rayEndOrigin = new Coords(3);
    private Coords translationOffset = new Coords(3);
    private Coords previousTranslationOffset = new Coords(3);
    private Coords mPosXY = new Coords(2);

    protected float mDistance;
    protected boolean objectIsRendered = false;
    protected boolean mDrawing = false;
    protected boolean mARIsRendering = false;

    protected CoordMatrix4x4 cHitMatrix = new CoordMatrix4x4();
    private Coords rayOrigin = new Coords(4);
    private Coords rayDirection = new Coords(4);
    private Coords projection = Coords.createInhomCoorsInD3();

    protected ARGestureManager arGestureManager;

    abstract public void onSurfaceCreated();

    abstract public void onSurfaceChanged(int width, int height);

    abstract public void onResume();

    abstract public void onPause();

    abstract public void proceedARLogic();

    abstract public void arButtonClicked() throws ARException;

    abstract public void setSession() throws ARException;

    protected void addGestureRecognizers(){
        arGestureManager.addGestureRecognizers();
    }

    public boolean getARIsRendering(){
        return false;
    }

    public boolean hasSessionStarted(){
        return false;
    }

    public boolean isDrawing(){
        return false;
    }

    public CoordMatrix4x4 getViewMatrix() {
        return viewMatrix;
    }

    public CoordMatrix4x4 getProjectMatrix() {
        return projectMatrix;
    }

    public CoordMatrix4x4 getAnchorMatrixForGGB() {
        return mModelMatrix;
    }

    public float getDistance() {
        return mDistance;
    }

    abstract public void setBackgroundColor();

    abstract public void setBackgroundStyle(Renderer.BackgroundStyle backgroundStyle);

    public  Renderer.BackgroundStyle getBackgroundStyle() {
        return null;
    }

    public Coords getHittingDirection() {
        return rayDirection;
    }

    public Coords getHittingOrigin() {
        return rayOrigin;
    }

    public Coords getHittingFloor() {
        return hittingFloorOk ? hittingFloor : null;
    }

    abstract public double checkHittingFloorZ(double z);

    public double getHittingDistance() {
        return hittingDistance;
    }

    abstract public void setHittingOriginAndDirection(float x, float y);

    abstract public void setHittingOriginAndDirectionFromScreenCenter();

    abstract public void proceed(TouchEventType event);

    protected void wrapMouseMoved(int x, int y) {

    }

    protected MouseTouchGestureQueueHelper mouseTouchGestureQueueHelper;

    protected void setMouseTouchGestureController() {

    }

    public void onDrawFrame(Renderer renderer, float scaleFactor) {
        renderer.getRendererImpl().glViewPort();
        proceedARLogic(); // Feature.G3D_AR_REGULAR_TOOLS: pass the touch event
        ARMotionEvent arMotionEvent = null;
        if (renderer.getView().getApplication().has(Feature.G3D_AR_REGULAR_TOOLS)) {
            arMotionEvent = mouseTouchGestureQueueHelper.poll();
        }
        // to update hitting o&d
        if (isDrawing()) {
            renderer.getView().setARDrawing(true);
            renderer.setARMatrix(getViewMatrix(), getProjectMatrix(),
                    getAnchorMatrixForGGB(), scaleFactor);
            if (renderer.getView().getApplication().has(Feature.G3D_AR_REGULAR_TOOLS)) {
                if (renderer.getView().getApplication().has(Feature.G3D_AR_TARGET)) {
                    if (((EuclidianController3D) renderer.getView().getEuclidianController())
                            .isCurrentModeForCreatingPoint()) {
                        if (arMotionEvent == null) {
                            if (mouseTouchGestureQueueHelper.isCurrentlyUp()) {
                                // create a "mouse move" event
                                setHittingOriginAndDirectionFromScreenCenter();
                                wrapMouseMoved(renderer.getWidth() / 2, renderer.getHeight() / 2);
                            } else {
                                // force a drag (device may have moved)
                                arMotionEvent = new ARMotionEvent(renderer.getWidthInPixels() / 2,
                                        renderer.getHeightInPixels() / 2);
                                arMotionEvent.setAction(ARMotionEvent.ON_MOVE);
                                setHittingOriginAndDirectionFromScreenCenter();
                            }
                        } else {
                            // force event to be screen-centered
                            arMotionEvent.setLocation(renderer.getWidthInPixels() / 2,
                                    renderer.getHeightInPixels() / 2);
                            setHittingOriginAndDirectionFromScreenCenter();
                        }
                    } else {
                        // process motionEvent at touch location (if exists)
                        if (arMotionEvent != null) {
                            setHittingOriginAndDirection(
                                    arMotionEvent.getX(),
                                    arMotionEvent.getY());
                        }
                    }
                } else {
                    if (arMotionEvent != null) {
                        setHittingOriginAndDirection(arMotionEvent
                                        .getX(),
                                arMotionEvent.getY());
                    }
                }
                renderer.getView().setEuclidianPanelOnTouchListener();
                setMouseTouchGestureController();
            }
            if (renderer.getView().getApplication().has(Feature.G3D_AR_REGULAR_TOOLS)) {
                proceedARMotionEvent(arMotionEvent);
            }
            renderer.drawScene();
        } else {
            renderer.getView().setARDrawing(false);
            renderer.endOfDrawScene();
        }
    }

    public MouseTouchGestureQueueHelper getMouseTouchGestureQueueHelper() {
        return mouseTouchGestureQueueHelper;
    }

    protected void proceedARMotionEvent(ARMotionEvent arMotionEvent) {

    }

    protected void updateModelMatrixFields() {
        /* Scaling */
        mScaleFactor = arGestureManager.getScaleFactor();

        /* Scaling */
        scaleMatrix.setDiag(mScaleFactor);

        /* translating */
        translationOffset.setSub3(rayEndOrigin, lastHitOrigin);
    }

    protected void updateModelMatrix(App app) {
        /* Scaling */
        mModelMatrix.setMul(mAnchorMatrix, scaleMatrix);

        updateModelMatrixForRotation(app);

        /* translating */
        Coords modelOrigin = mModelMatrix.getOrigin();
        Coords anchorOrigin = mAnchorMatrix.getOrigin();
        modelOrigin.setX(anchorOrigin.getX() + translationOffset.getX() +
                previousTranslationOffset.getX());
        modelOrigin.setY(anchorOrigin.getY());
        modelOrigin.setZ(anchorOrigin.getZ() + translationOffset.getZ() +
                previousTranslationOffset.getZ());
    }

    private void updateModelMatrixForRotation(App app) {
        // TODO: remove this when G3D_AR_REGULAR_TOOLS released
        if (!app.has(Feature.G3D_AR_REGULAR_TOOLS)) {
            // below not-so-nice (but temporary) code
            CoordMatrix.setRotation3DMatrix(CoordMatrix.Y_AXIS,
                    rotateAngel * Math.PI / 180.0, tmpMatrix1);
            tmpMatrix2.set(mModelMatrix);
            mModelMatrix.setMul(tmpMatrix2, tmpMatrix1);
        }
    }

    protected Coords setRay() {
        viewMatrix.solve(Coords.O, rayOrigin);
        rayDirection.setSub3(cHitMatrix.getOrigin(), rayOrigin);
        rayOrigin.projectPlane(mModelMatrix.getVx(), mModelMatrix.getVz(), rayDirection,
                mModelMatrix.getOrigin(), projection);
        return projection;
    }

    protected void updateTranslationIfNeeded() {
        if (arGestureManager.getUpdateOriginIsWanted()) {
            arGestureManager.setUpdateOriginIsWanted(false);
            Coords modelOrigin = mModelMatrix.getOrigin();
            Coords anchorOrigin = mAnchorMatrix.getOrigin();
            previousTranslationOffset.setSub3(modelOrigin, anchorOrigin);

            lastHitOrigin.set3(rayEndOrigin);
        }
    }

    protected void setClipCenterAndComputeRayDirection() {
        // set clip center to projection matrix near plane location
        tmpCoords2.setX(0);
        tmpCoords2.setY(0);
        tmpCoords2.setZ(projectMatrix.getOrigin().getZ()/(1-projectMatrix.getVz().getZ()));
        tmpCoords2.setW(1);
        viewMatrix.solve(tmpCoords2, rayOrigin);
        // compute ray direction (which is z- in camera coord sys)
        viewMatrix.solve(Coords.VZm, rayDirection);
        rayDirection.normalize();
    }

    protected void countHittingOriginAndDirection() {
        // "ground" hitting point
        tmpCoords1.setMul(viewMatrix, cHitMatrix.getOrigin());
        tmpCoords1.setW(0);
        tmpCoords1.normalize();
        // set clip center to projection matrix near plane location
        tmpCoords2.setX(0);
        tmpCoords2.setY(0);
        tmpCoords2.setZ(projectMatrix.getOrigin().getZ()/(1-projectMatrix.getVz().getZ()));
        tmpCoords2.setW(1);
        // get clip hitting point
        Coords.O.projectPlane(Coords.VX, Coords.VY, tmpCoords1, tmpCoords2, tmpCoords3);
        // projection may not be possible
        if (tmpCoords3.isDefined()) {
            viewMatrix.solve(tmpCoords3, rayOrigin);
            rayDirection.setSub3(cHitMatrix.getOrigin(), rayOrigin);
            rayDirection.normalize();
        }
    }

    protected void copyPosFromGestureManager() {
        arGestureManager.copyXYPosition(mPosXY);
    }

    protected float getPosX() {
        return (float) mPosXY.getX();
    }

    protected float getPosY() {
        return (float) mPosXY.getY();
    }

    protected void resetTranslationOffset() {
        // used in iOS
        translationOffset.set(0,0,0);
        previousTranslationOffset.set(0,0,0);
    }

    /**
     * set first hit floor z value
     * @param z altitude
     */
    abstract public void setFirstFloor(double z);
}
