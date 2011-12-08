package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoConicInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

public interface TransformInterface {
	public boolean isAffine();
	public boolean changesOrientation();
	public boolean isSimilar();
	public GeoPointND[] transformPoints(GeoPointND[] points);
	public GeoElement[] transform(GeoElement geo, String label);
	public GeoElement getTransformedLine(GeoLineND line);
	public GeoConicInterface getTransformedConic(GeoConicInterface conic);
	public GeoElement doTransform(GeoElement geo);
}
