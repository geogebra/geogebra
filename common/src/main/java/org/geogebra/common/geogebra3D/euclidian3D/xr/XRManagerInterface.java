package org.geogebra.common.geogebra3D.euclidian3D.xr;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Interface for Mixed Reality managers and Augmented Reality managers
 * 
 * @param <T>
 *            touch event type
 */
public interface XRManagerInterface<T> {

    /** desk max distance is 50 cm */
    final static public double DESK_DISTANCE_MAX = 0.5;

    /** 1 pixel thickness in ggb == 0.25 mm (for distance smaller than DESK_DISTANCE_MAX) */
    final static public double THICKNESS_MIN = 0.00025;

    /** average distance for desk */
    final static public double DESK_DISTANCE_AVERAGE = 0.25;

    /** 1 thickness in ggb >> THICKNESS_MIN_FACTOR;
     * this is for to get the visible thickness (on screen) quite the same as in regular 3D view */
    final static public float THICKNESS_MIN_FACTOR = 0.001f;

    /** when setting scale at start, the ratio can't be emphasized more than 40% */
    final static public float MAX_FACTOR_TO_EMPHASIZE = 1.40f;

    /** in projection matrix, we don't trust factors more than 10% precision */
    final static public float PROJECT_FACTOR_RELATIVE_PRECISION = 0.10f;

    Coords getHittingDirection();

    Coords getHittingOrigin();

    Coords getHittingFloor();

    void proceed(T event);

    double checkHittingFloorZ(double z);

    double getHittingDistance();

    void setBackgroundColor();

    void setBackgroundStyle(Renderer.BackgroundStyle backgroundStyle);

    Renderer.BackgroundStyle getBackgroundStyle();

    void setFirstFloor(double z);

    void setXRScaleAtStart();

    CoordMatrix4x4 getUndoRotationMatrix();

    CoordMatrix4x4 getViewModelMatrix();

    void fromXRCoordsToGGBCoords(Coords coords, Coords ret);

    void setProjectionMatrixViewForXR(CoordMatrix4x4 projectionMatrix);

    void fitThickness();

    float getXRScaleFactor();

    void resetScaleFromXR();

    String getXRRatioInString();

    void setXRRatio(double ratio);

    void setRatioIsShown(boolean ratioIsShown);

    void calculateAndShowRatio();
}
