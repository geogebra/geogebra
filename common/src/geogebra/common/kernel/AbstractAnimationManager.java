package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElementInterface;

public abstract class AbstractAnimationManager {
	public abstract void addAnimatedGeo(GeoElementInterface ge);
	public abstract void removeAnimatedGeo(GeoElementInterface ge);

}
