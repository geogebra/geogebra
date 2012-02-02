package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoPointND;

public interface GeoTextInterface {
	public boolean isLaTeX();
	public String getTextString();
	public boolean hasAbsoluteLocation();
	public GeoPointND getStartPoint();
}
