package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElementInterface;

public abstract class AbstractAnimationManager {
	abstract void addAnimatedGeo(GeoElementInterface ge);
	abstract void removeAnimatedGeo(GeoElementInterface ge);

}
