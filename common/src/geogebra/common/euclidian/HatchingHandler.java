package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;

/**
 * Handles hatching of fillable geos
 */
public abstract class HatchingHandler {
	/**
	 * Prototype decides what implementation will be used for static methods
	 */
	
	public static HatchingHandler prototype;
	/**
	 * @param g3 graphics
	 * @param objStroke hatching stroke
	 * @param color stroke color
	 * @param bgColor background color
	 * @param backgroundTransparency alpha value of background
	 * @param dist distance between hatches
	 * @param angle hatching angle in degrees
	 */
	protected abstract void dosetHatching(geogebra.common.awt.GGraphics2D g3, geogebra.common.awt.GBasicStroke objStroke,
			geogebra.common.awt.GColor color, geogebra.common.awt.GColor bgColor, float backgroundTransparency,
			double dist, double angle);
	/**
	 * @param g3 graphics
	 * @param objStroke hatching stroke
	 * @param color stroke color
	 * @param bgColor background color
	 * @param backgroundTransparency alpha value of background
	 * @param dist distance between hatches
	 * @param angle hatching angle in degrees
	 */
	public static void setHatching(geogebra.common.awt.GGraphics2D g3, geogebra.common.awt.GBasicStroke objStroke,
			geogebra.common.awt.GColor color, geogebra.common.awt.GColor bgColor, float backgroundTransparency,
			double dist, double angle) {
		prototype.dosetHatching(g3, objStroke, color, bgColor, backgroundTransparency, dist, angle);
	}
	/**
	 * @param g3 graphics
	 * @param geo geo
	 * @param alpha alpha value
	 */
	protected abstract void doSetTexture(geogebra.common.awt.GGraphics2D g3, GeoElement geo, float alpha);
	/**
	 * @param g3 graphics
	 * @param geo geo
	 * @param alpha alpha value
	 */
	public static void setTexture(geogebra.common.awt.GGraphics2D g3, GeoElement geo, float alpha) {
		prototype.doSetTexture(g3, geo, alpha);
	}
}
