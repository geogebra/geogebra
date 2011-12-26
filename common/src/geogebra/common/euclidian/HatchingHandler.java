package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;

public abstract class HatchingHandler {
	public static HatchingHandler prototype;
	protected abstract void dosetHatching(geogebra.common.awt.Graphics2D g3, geogebra.common.awt.BasicStroke objStroke,
			geogebra.common.awt.Color color, geogebra.common.awt.Color bgColor, float backgroundTransparency,
			double dist, double angle);
	public static void setHatching(geogebra.common.awt.Graphics2D g3, geogebra.common.awt.BasicStroke objStroke,
			geogebra.common.awt.Color color, geogebra.common.awt.Color bgColor, float backgroundTransparency,
			double dist, double angle) {
		prototype.dosetHatching(g3, objStroke, color, bgColor, backgroundTransparency, dist, angle);
	}
	protected abstract void doSetTexture(geogebra.common.awt.Graphics2D g3, GeoElement geo, float alpha);
	public static void setTexture(geogebra.common.awt.Graphics2D g3, GeoElement geo, float alpha) {
		prototype.doSetTexture(g3, geo, alpha);
	}
}
