package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

public interface HasSymbolicMode extends GeoElementND {
	public void setSymbolicMode(boolean mode, boolean updateParent);

	public boolean isSymbolicMode();
}
