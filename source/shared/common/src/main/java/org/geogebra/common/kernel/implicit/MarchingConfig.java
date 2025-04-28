package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

/**
 * Marching square/rectangle configuration.
 */
public interface MarchingConfig {

	int flag();

	MyPoint[] getPoints(MarchingRect r);

	boolean isValid();

	boolean isInvalid();

	boolean isEmpty();

}
