package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GProperty;

public interface StyleSensitiveAlgo {
	boolean dependsOnInputStyle(GProperty prop);
}
