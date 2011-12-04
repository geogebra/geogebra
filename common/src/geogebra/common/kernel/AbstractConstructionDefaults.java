package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElementInterface;

public abstract class AbstractConstructionDefaults {
	public static final float DEFAULT_POLYGON_ALPHA = 0.1f;
	public abstract void setDefaultVisualStyles(GeoElementInterface geoElement, boolean b);

}
