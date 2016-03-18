package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

public interface HasSymbolicMode extends GeoElementND {
	public void setSymbolicMode(boolean mode);

	public boolean isSymboicMode();
}
