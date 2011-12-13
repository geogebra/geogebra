package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

public abstract class TransformInterface {

	public static String transformedGeoLabel(GeoElement geo) {
		if(geo.isGeoFunction()){
			if (geo.isLabelSet() && !geo.hasIndexLabel())
				return geo.getFreeLabel(geo.getLabel());
			return null;
		}
		
		if (geo.isLabelSet() && !geo.hasIndexLabel()
				&& !geo.label.endsWith("'''")) {
			return geo.getFreeLabel(geo.label + "'");
		} else {
			return null;
		}
	}

	public abstract boolean isAffine();
	public abstract boolean changesOrientation();
	public abstract boolean isSimilar();
	public abstract GeoPointND[] transformPoints(GeoPointND[] points);
	public abstract GeoElement[] transform(GeoElement geo, String label);
	public abstract GeoElement getTransformedLine(GeoLineND line);
	public abstract GeoConic getTransformedConic(GeoConic conic);
	public abstract GeoElement doTransform(GeoElement geo);
}
