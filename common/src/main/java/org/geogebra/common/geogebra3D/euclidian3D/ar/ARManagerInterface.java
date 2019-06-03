package org.geogebra.common.geogebra3D.euclidian3D.ar;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Interface for Augmented Reality managers
 * 
 * @param <T>
 *            touch event type
 */
public interface ARManagerInterface<T> {

    /** desk max distance is 50 cm */
    final static public double DESK_DISTANCE_MAX = 0.5;

    /** 1 pixel thickness in ggb == 0.25 mm (for distance smaller than DESK_DISTANCE_MAX) */
    final static public double THICKNESS_MIN = 0.00025;

    Coords getHittingDirection();

    Coords getHittingOrigin();

    Coords getHittingFloor();

    void proceed(T event);

    double checkHittingFloorZ(double z);

    double getHittingDistance();

    float getDistance();

    void setBackgroundColor();

    void setBackgroundStyle(Renderer.BackgroundStyle backgroundStyle);

    Renderer.BackgroundStyle getBackgroundStyle();

    void setFirstFloor(double z);

    void setARScaleAtStart();

    float getARScaleParameter();

    CoordMatrix4x4 getUndoRotationMatrix();

    CoordMatrix4x4 getViewModelMatrix();

    void fromARCoordsToGGBCoords(Coords coords, Coords ret);

    void setProjectionMatrixViewForAR(CoordMatrix4x4 projectionMatrix);

    void fitThickness(float scale);
}
