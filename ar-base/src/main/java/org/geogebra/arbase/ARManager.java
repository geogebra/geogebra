package org.geogebra.arbase;


import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;

abstract public class ARManager<TouchEventType> {

    protected CoordMatrix4x4 viewMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 projectMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 mModelMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 mAnchorMatrix = new CoordMatrix4x4();
    private CoordMatrix4x4 scaleMatrix = CoordMatrix4x4.IDENTITY;
    protected float mScaleFactor = 1;
    protected float rotateAngel = 0;
    protected Coords hittingFloor = Coords.createInhomCoorsInD3();
    protected boolean hittingFloorOk;
    protected double hittingDistance;

    private Coords tmpCoords1 = new Coords(4);
    private Coords tmpCoords2 = new Coords(4);
    private Coords tmpCoords3 = new Coords(4);

    protected float[] hitMatrix = new float[16];
    private float[] lastHitOrigin = new float[3];
    protected float[] rayEndOrigin = new float[3];
    private float[] translationOffset = new float[3];
    private double[] previousTranslationOffset = new double[3];

    protected float mDistance;
    protected boolean objectIsRendered = false;
    protected boolean mDrawing = false;
    protected boolean mARIsRendering = false;

    private float[] projectionFloats = new float[3];
    protected CoordMatrix4x4 cHitMatrix = new CoordMatrix4x4();
    private Coords rayOrigin = new Coords(4);
    private Coords rayDirection = new Coords(4);
    private Coords projection = Coords.createInhomCoorsInD3();

    protected ARGestureManager gestureListener;

    abstract public void onSurfaceCreated();

    abstract public void onSurfaceChanged(int width, int height);

    abstract public void onResume();

    abstract public void onPause();

    abstract public void onDrawFrame();

    abstract public void arButtonClicked() throws ARException;

    abstract public void setSession() throws ARException;

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

    public double getHittingDistance() {
        return hittingDistance;
    }

    abstract public void setHittingOriginAndDirection(float x, float y);

    abstract public void setHittingOriginAndDirectionFromScreenCenter();

    abstract public void proceed(TouchEventType event);

    protected void updateModelMatrixFields() {
        /* Scaling */
        mScaleFactor = gestureListener.getScaleFactor();

        /* Scaling */
        scaleMatrix.setDiag(mScaleFactor);

        /* translating */
        translationOffset[0] = rayEndOrigin[0] - lastHitOrigin[0];
        translationOffset[1] = rayEndOrigin[1] - lastHitOrigin[1];
        translationOffset[2] = rayEndOrigin[2] - lastHitOrigin[2];
    }

    protected void updateModelMatrix() {
        /* Scaling */
        mModelMatrix.setMul(mAnchorMatrix, scaleMatrix);

        updateModelMatrixForRotation();

        /* translating */
        Coords modelOrigin = mModelMatrix.getOrigin();
        Coords anchorOrigin = mAnchorMatrix.getOrigin();
        modelOrigin.setX(anchorOrigin.getX() + translationOffset[0] + previousTranslationOffset[0]);
        modelOrigin.setY(anchorOrigin.getY());
        modelOrigin.setZ(anchorOrigin.getZ() + translationOffset[2] + previousTranslationOffset[2]);
    }

    protected void updateModelMatrixForRotation() {
        // TODO: remove this when G3D_AR_REGULAR_TOOLS released
    }

    protected float[] setRay() {
        cHitMatrix.setFromGL(hitMatrix);

        viewMatrix.solve(Coords.O, rayOrigin);
        rayDirection.setSub3(cHitMatrix.getOrigin(), rayOrigin);
        rayOrigin.projectPlane(mModelMatrix.getVx(), mModelMatrix.getVz(), rayDirection,
                mModelMatrix.getOrigin(), projection);
        projection.get3ForGL(projectionFloats);
        return projectionFloats;
    }

    protected void updateTranslationIfNeeded() {
        if (gestureListener.getUpdateOriginIsWanted()) {
            gestureListener.setUpdateOriginIsWanted(false);
            Coords modelOrigin = mModelMatrix.getOrigin();
            Coords anchorOrigin = mAnchorMatrix.getOrigin();
            previousTranslationOffset[0] = modelOrigin.getX() - anchorOrigin.getX();
            previousTranslationOffset[1] = modelOrigin.getY() - anchorOrigin.getY();
            previousTranslationOffset[2] = modelOrigin.getZ() - anchorOrigin.getZ();

            lastHitOrigin[0] = rayEndOrigin[0];
            lastHitOrigin[1] = rayEndOrigin[1];
            lastHitOrigin[2] = rayEndOrigin[2];
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
}
