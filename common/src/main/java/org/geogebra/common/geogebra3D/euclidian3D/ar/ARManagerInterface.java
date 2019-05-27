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

    float getGestureScaleFactor();

    CoordMatrix4x4 getViewMatrix();

    CoordMatrix4x4 getProjectMatrix();

    CoordMatrix4x4 getAnchorMatrixForGGB();

}
