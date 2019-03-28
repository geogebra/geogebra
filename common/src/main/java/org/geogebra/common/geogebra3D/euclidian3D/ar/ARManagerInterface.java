package org.geogebra.common.geogebra3D.euclidian3D.ar;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Interface for Augmented Reality managers
 */
public interface ARManagerInterface<TouchEventType> {

    Coords getHittingDirection();

    void proceed(TouchEventType event);

}
