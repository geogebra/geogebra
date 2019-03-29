package org.geogebra.common.geogebra3D.euclidian3D.ar;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Interface for Augmented Reality managers
 */
public interface ARManagerInterface<TouchEventType> {

    Coords getHittingDirection();

    Coords getHittingOrigin();

    Coords getHittingFloor();

    void proceed(TouchEventType event);

    double checkHittingFloorZ(double z);

    double getHittingDistance();

    float getDistance();

    void setBackgroundColor();

    void setBackgroundStyle(Renderer.BackgroundStyle backgroundStyle);

    Renderer.BackgroundStyle getBackgroundStyle();

    void setFirstFloor(double z);

}
