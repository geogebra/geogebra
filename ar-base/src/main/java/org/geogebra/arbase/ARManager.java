package org.geogebra.arbase;


import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.ar.ARManagerInterface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.util.DoubleUtil;

import java.util.HashMap;
import java.util.Map;

abstract public class ARManager<TouchEventType> implements ARManagerInterface<TouchEventType> {

    protected CoordMatrix4x4 viewMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 projectMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 mModelMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 mAnchorMatrix = new CoordMatrix4x4();
    private CoordMatrix4x4 undoRotationMatrix = new CoordMatrix4x4();
    private CoordMatrix4x4 viewModelMatrix = new CoordMatrix4x4();
    private CoordMatrix4x4 tmpMatrix1 = new CoordMatrix4x4();
    private CoordMatrix4x4 tmpMatrix2 = new CoordMatrix4x4();
    private CoordMatrix4x4 tmpMatrix3 = new CoordMatrix4x4();
    private CoordMatrix4x4 tmpMatrix4 = new CoordMatrix4x4();
    private float arScaleAtStart;
    private float scaleThickness = 1;
    private float lastScaleFactor = 1;
    private float arScaleAtStartDiff = 1;
    private double arRatio;
    protected float rotateAngel = 0;
    protected Coords hittingFloor = Coords.createInhomCoorsInD3();
    protected boolean hittingFloorOk;
    private Map<Object, Double> trackablesZ;
    protected Object hittingTrackable;
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

    protected EuclidianView3D mView;

    private ARMotionEvent lastARMotionEvent;

    protected ARSnackBarManagerInterface mArSnackBarManagerInterface;

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

    public double checkHittingFloorZ(double z) {
        createTrackableListIfNeeded();
        Double v = trackablesZ.get(hittingTrackable);
        if (v == null) {
            trackablesZ.put(hittingTrackable, z);
            return z;
        }
        return v;
    }

    private void createTrackableListIfNeeded() {
        if (trackablesZ == null) {
            trackablesZ = new HashMap<>();
        }
    }

    public double getHittingDistance() {
        return hittingDistance / arScaleAtStart;
    }

    abstract public void setHittingOriginAndDirection(float x, float y);

    abstract public void setHittingOriginAndDirectionFromScreenCenter();

    private void wrapMouseMoved(int x, int y) {
        mView.getEuclidianController().wrapMouseMoved(mView.getEuclidianController()
                .createTouchEvent(x,y));
    }

    protected MouseTouchGestureQueueHelper mouseTouchGestureQueueHelper;

    protected void setMouseTouchGestureController() {

    }

    public void onDrawFrame() {
        Renderer renderer = mView.getRenderer();
        renderer.getRendererImpl().glViewPort();
        proceedARLogic(); // Feature.G3D_AR_REGULAR_TOOLS: pass the touch event
        ARMotionEvent arMotionEvent = null;
        if (mView.getApplication().has(Feature.G3D_AR_REGULAR_TOOLS)) {
            arMotionEvent = mouseTouchGestureQueueHelper.poll();
        }
        // to update hitting o&d
        if (isDrawing()) {
            renderer.getView().setARDrawing(true);
            renderer.setView();
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
                                arMotionEvent = getARMotionEventMove(mView.getWidth() / 2,
                                        mView.getHeight() / 2);
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
                            setHittingOriginAndDirection(arMotionEvent);
                            lastARMotionEvent = arMotionEvent;
                        } else {
                            if (mouseTouchGestureQueueHelper.isCurrentlyUp()) {
                                lastARMotionEvent = null;
                            } else {
                                // create a new motionEvent
                                if (lastARMotionEvent != null) {
                                    if (lastARMotionEvent.getAction() ==
                                            ARMotionEvent.FIRST_FINGER_DOWN) {
                                        arMotionEvent = getARMotionEventMove(lastARMotionEvent.getX(),
                                                lastARMotionEvent.getY());
                                        setHittingOriginAndDirection(arMotionEvent);
                                    } else if (lastARMotionEvent.getAction() ==
                                            ARMotionEvent.ON_MOVE){
                                        arMotionEvent = lastARMotionEvent;
                                        setHittingOriginAndDirection(arMotionEvent);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (arMotionEvent != null) {
                        setHittingOriginAndDirection(arMotionEvent);
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
        /* translating */
        translationOffset.setSub3(rayEndOrigin, lastHitOrigin);

        /* update ratio */
        if (mView.getApplication().has(Feature.G3D_AR_SHOW_RATIO)) {
            showSnackbar();
        }
    }

    protected void clearAnchors() {
        if (trackablesZ != null) {
            trackablesZ.clear();
        }
        hittingTrackable = null;
    }

    protected void updateModelMatrix(App app) {
        mModelMatrix.set(mAnchorMatrix);

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
        if (arGestureManager != null && arGestureManager.getUpdateOriginIsWanted()) {
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
        if (arGestureManager != null) {
            arGestureManager.copyXYPosition(mPosXY);
        }
    }

    protected float getPosX() {
        return (float) mPosXY.getX();
    }

    protected float getPosY() {
        return (float) mPosXY.getY();
    }

    protected void resetTranslationOffsetAndScaleMatrix() {
        // used in iOS
        translationOffset.set(0,0,0);
        previousTranslationOffset.set(0,0,0);
    }

    /**
     * set first hit floor z value
     * @param z altitude
     */
    public void setFirstFloor(double z) {
        if (hittingTrackable != null) {
            createTrackableListIfNeeded();
            trackablesZ.put(hittingTrackable, z);
        }
    }

    protected ARMotionEvent getARMotionEventMove(float x, float y){
        return null;
    }

    private void setHittingOriginAndDirection(ARMotionEvent arMotionEvent) {
            setHittingOriginAndDirection(arMotionEvent.getX(), arMotionEvent.getY());
    }

    public void setARScaleAtStart() {
            arScaleAtStart = setARScaleAtStart(mDistance);
            if (mView.getApplication().has(Feature.G3D_AR_SHOW_RATIO)) {
                showSnackbar();
            }
    }

    public float setARScaleAtStart(double distance) {
        if (mView.getApplication().has(Feature.G3D_AR_SIMPLE_SCALE)) {
            // don't expect distance less than desk distance
            if (distance < DESK_DISTANCE_MAX) {
                distance = DESK_DISTANCE_MAX;
            }
            // 1 pixel thickness in ggb == 0.25 mm (for distance smaller than DESK_DISTANCE_MAX)
            double thicknessMin = THICKNESS_MIN * distance / DESK_DISTANCE_MAX;
            // 1 ggb unit ==  1 meter
            double ggbToRw = 1.0 / mView.getXscale();
            double ratio = thicknessMin / ggbToRw; // thicknessMin = ggbToRw * ratio
            double pot = DoubleUtil.getPowerOfTen(ratio);
            ratio = ratio / pot;
            if (ratio <= 2f) {
                ratio = 2f;
            } else if (ratio <= 5f) {
                ratio = 5f;
            } else {
                ratio = 10f;
            }
            if (mView.getApplication().has(Feature.G3D_AR_SHOW_RATIO)) {
                int mToCm = 100;
                arRatio = ratio * pot * mToCm;
            }
            return (float) (ggbToRw * ratio * pot);
        } else {
            float reductionFactor = 0.80f;
            return (mDistance / mView.getRenderer().getWidth())
                    * reductionFactor;
        }
    }

    public float getARScaleParameter() {
        if (mView.getApplication().has(Feature.G3D_AR_FIT_THICKNESS_BUTTON)) {
            return arScaleAtStart * arGestureManager.getScaleFactor() * scaleThickness;
        } else {
            return arScaleAtStart * arGestureManager.getScaleFactor();
        }
    }

    public void fromARCoordsToGGBCoords(Coords coords, Coords ret) {
        // undo model matrix
        mModelMatrix.solve(coords, ret);
        // undo scale matrix
        CoordMatrix4x4.setZero(tmpMatrix2);
        CoordMatrix4x4.setDilate(tmpMatrix2, getARScaleParameter());
        tmpMatrix2.solve(ret, tmpCoords1);
        // undo screen coordinates
        ret.setMul(mView.getToSceneMatrix(), tmpCoords1);
    }

    public void setProjectionMatrixViewForAR(CoordMatrix4x4 projectionMatrix) {
        // scaleMatrix
        CoordMatrix4x4.setZero(tmpMatrix1);
        CoordMatrix4x4.setDilate(tmpMatrix1, getARScaleParameter());

        // cameraView * modelMatrix and undo rotation matrix (keeping screen orientation)
        tmpMatrix3.setMul(viewMatrix, mModelMatrix);
        viewModelMatrix.set(tmpMatrix3);

        // invert cameraView * modelMatrix to keep labels towards to screen
        // calculate angle to keep labels upward
        tmpMatrix2.set(tmpMatrix3);
        tmpMatrix2.setOrigin(Coords.O);
        tmpMatrix4.set(tmpMatrix2.inverse());
        Coords vy = tmpMatrix4.getVy();
        Coords vz = tmpMatrix4.getVz();
        tmpCoords1.setSub3(Coords.VY,
                tmpCoords1.setMul3(vz, Coords.VY.dotproduct(vz)));
        tmpCoords1.setW(0);
        tmpCoords1.normalize();
        double c = tmpCoords1.dotproduct(vy);
        double s = vz.dotCrossProduct(tmpCoords1, vy);
        double rot = Math.atan2(s, c);
        CoordMatrix.setRotation3DMatrix(CoordMatrix.Z_AXIS, -rot, tmpMatrix2);
        undoRotationMatrix.setMul(tmpMatrix4, tmpMatrix2);

        // (cameraView * modelMatrix) * scaleMatrix
        tmpMatrix2.setMul(tmpMatrix3, tmpMatrix1);

        // cameraPerspective * (cameraView * (modelMatrix * scaleMatrix))
        projectionMatrix.setMul(projectMatrix, tmpMatrix2);
    }

    public CoordMatrix4x4 getUndoRotationMatrix() {
        return undoRotationMatrix;
    }

    public CoordMatrix4x4 getViewModelMatrix() {
        return viewModelMatrix;
    }

    private void showSnackbar() {
        double ratio;
        if (arGestureManager != null) {
            ratio = arRatio * arGestureManager.getScaleFactor();
        } else {
            ratio = arRatio;
        }
        String text;
        if (ratio >= 100) {
            // round double for precision 3 in m
            ratio = (double) Math.round(ratio) / 100d;
            text = getRatioMessage(ratio, "m");
        } else if (ratio < 0.5 ) {
            // round double for precision 3 in mm
            ratio = (double) Math.round(ratio * 1000d) / 100d;
            text = getRatioMessage(ratio, "mm");
        } else {
            // round double for precision 3 in cm
            ratio = (double) Math.round(ratio * 100d) / 100d;
            text = getRatioMessage(ratio, "cm");
        }
         mArSnackBarManagerInterface.showRatio(text);
    }

    private String getRatioMessage(double ratio, String units) {
        String message;
        if(DoubleUtil.isInteger(ratio)) {
            message = String.format("1 : %d %s", (long)ratio, units);
        } else {
            message = String.format("1 : %.4s %s", ratio, units);
        }
        return message;
    }

    public void fitThickness() {
        float scale = arGestureManager.getScaleFactor() / lastScaleFactor;
        EuclidianSettings3D settings =
                (EuclidianSettings3D) mView.getApplication().getSettings().getEuclidian(3);
        settings.setXscale(settings.getXscale() * scale);
        settings.setYscale(settings.getYscale() * scale);
        settings.setZscale(settings.getZscale() * scale);
        scaleThickness = scaleThickness / scale;
        lastScaleFactor = arGestureManager.getScaleFactor();

        float previousARScaleAtStart = arScaleAtStart;
        arScaleAtStart = setARScaleAtStart(viewModelMatrix.getOrigin().calcNorm3());
        arScaleAtStartDiff = arScaleAtStart / previousARScaleAtStart;
        settings.setXscale(settings.getXscale() * (1f / arScaleAtStartDiff));
        settings.setYscale(settings.getYscale() * (1f / arScaleAtStartDiff));
        settings.setZscale(settings.getZscale() * (1f / arScaleAtStartDiff));

        showSnackbar();
    }
}
