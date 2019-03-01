package org.geogebra.arbase;


import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;

abstract public class ARManager<TouchEventType> {

    protected CoordMatrix4x4 viewMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 projectMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 mModelMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 mAnchorMatrix = new CoordMatrix4x4();
    protected CoordMatrix4x4 scaleMatrix = CoordMatrix4x4.IDENTITY;
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

    abstract public double checkHittingFloorZ(double z);

    public double getHittingDistance() {
        return hittingDistance;
    }

    abstract public void setHittingOriginAndDirection(float x, float y);

    abstract public void setHittingOriginAndDirectionFromScreenCenter();

    abstract public void proceed(TouchEventType event);

    protected void updateModelMatrixFields() {
        /* Scaling */
        mScaleFactor = arGestureManager.getScaleFactor();

        /* Scaling */
        scaleMatrix.setDiag(mScaleFactor);

        /* translating */
        translationOffset.setX(rayEndOrigin.getX() - lastHitOrigin.getX());
        translationOffset.setY(rayEndOrigin.getY() - lastHitOrigin.getY());
        translationOffset.setZ(rayEndOrigin.getZ() - lastHitOrigin.getZ());
    }

    protected void updateModelMatrix() {
        /* Scaling */
        mModelMatrix.setMul(mAnchorMatrix, scaleMatrix);

        updateModelMatrixForRotation();

        /* translating */
        Coords modelOrigin = mModelMatrix.getOrigin();
        Coords anchorOrigin = mAnchorMatrix.getOrigin();
        modelOrigin.setX(anchorOrigin.getX() + translationOffset.getX() +
                previousTranslationOffset.getX());
        modelOrigin.setY(anchorOrigin.getY());
        modelOrigin.setZ(anchorOrigin.getZ() + translationOffset.getZ() +
                previousTranslationOffset.getZ());
    }

    protected void updateModelMatrixForRotation() {
        // TODO: remove this when G3D_AR_REGULAR_TOOLS released
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
            previousTranslationOffset.setX(modelOrigin.getX() - anchorOrigin.getX());
            previousTranslationOffset.setY(modelOrigin.getY() - anchorOrigin.getY());
            previousTranslationOffset.setZ(modelOrigin.getZ() - anchorOrigin.getZ());

            lastHitOrigin.setX(rayEndOrigin.getX());
            lastHitOrigin.setY(rayEndOrigin.getY());
            lastHitOrigin.setZ(rayEndOrigin.getZ());
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
