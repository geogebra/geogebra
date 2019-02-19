package org.geogebra.arbase;


import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;

public interface ARManagerInterface<TouchEventType> {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onResume();

    void onPause();

    void onDrawFrame();

    void arButtonClicked() throws ARException;

    void setSession() throws ARException;

    boolean getARCoreIsRendering();

    boolean hasSessionStarted();

    boolean isDrawing();

    CoordMatrix4x4 getViewMatrix();

    CoordMatrix4x4 getProjectMatrix();

    CoordMatrix4x4 getAnchorMatrixForGGB();

    float getDistance();

    float getScaleFactor();

    void freezeScreenRotation();

    void setBackgroundColor();

    void setBackgroundStyle(Renderer.BackgroundStyle backgroundStyle);

    Renderer.BackgroundStyle getBackgroundStyle();

    Coords getHittingDirection();

    Coords getHittingOrigin();

    Coords getHittingFloor();

    double getHittingDistance();

    void setHittingOriginAndDirection(float x, float y);

    public void setHittingOriginAndDirectionFromScreenCenter();

    void proceed(TouchEventType event);

}
