package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GProperty;

/**
 * Algo that needs to be updated when a visual property of an input changes.
 */
public interface StyleSensitiveAlgo {
	/**
	 * @param prop property kind
	 * @return whether algo output depends on given property
	 */
	boolean dependsOnInputStyle(GProperty prop);
}
