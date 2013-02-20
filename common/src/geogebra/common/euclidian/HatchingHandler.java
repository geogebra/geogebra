package geogebra.common.euclidian;

import geogebra.common.awt.GGeneralPath;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
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
	 * @param fillType type of pattern
	 */
	protected abstract void dosetHatching(geogebra.common.awt.GGraphics2D g3, geogebra.common.awt.GBasicStroke objStroke,
			geogebra.common.awt.GColor color, geogebra.common.awt.GColor bgColor, float backgroundTransparency,
			double dist, double angle, GeoElement.FillType fillType);
	
	/**
	 * @param g3 graphics
	 * @param objStroke hatching stroke
	 * @param color stroke color
	 * @param bgColor background color
	 * @param backgroundTransparency alpha value of background
	 * @param dist distance between hatches
	 * @param angle hatching angle in degrees
	 * @param fillType type of pattern
	 */
	public static void setHatching(geogebra.common.awt.GGraphics2D g3, geogebra.common.awt.GBasicStroke objStroke,
			geogebra.common.awt.GColor color, geogebra.common.awt.GColor bgColor, float backgroundTransparency,
			double dist, double angle, GeoElement.FillType fillType) {
		prototype.dosetHatching(g3, objStroke, color, bgColor, backgroundTransparency, dist, angle, fillType);
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
	
	protected void drawBricks(int xInt, int yInt, GGraphics2D g2d) {
		g2d.drawLine(0, yInt, xInt * 3, yInt);
		g2d.drawLine(0, yInt * 2, xInt * 3, yInt * 2);
		g2d.drawLine(0, yInt + yInt / 2, xInt * 3, yInt + yInt / 2);
		g2d.drawLine(xInt + xInt / 4, yInt, xInt + xInt / 4, yInt + yInt / 2);
		g2d.drawLine(xInt + (xInt * 3) / 4, yInt + yInt / 2, xInt + (xInt * 3)
		        / 4, 2 * yInt);
	}

	protected void drawDotted(double dist, GGraphics2D g2d) {
		int distInt = (int) dist;
		int size = 2;
		g2d.fill(AwtFactory.prototype.newEllipse2DFloat(distInt, distInt, size, size));
		g2d.fill(AwtFactory.prototype.newEllipse2DFloat(2 * distInt, distInt, size, size));
		g2d.fill(AwtFactory.prototype.newEllipse2DFloat(distInt, 2 * distInt, size, size));
		g2d.fill(AwtFactory.prototype.newEllipse2DFloat(2 * distInt, 2 * distInt, size, size));
	}

	protected boolean drawChessboard(double angle, float dist, GGraphics2D g2d) {
		if (Kernel.isEqual(Math.PI / 4, angle, 10E-8)) { // 45 degrees
			GGeneralPath path = AwtFactory.prototype.newGeneralPath();
			dist = (float) (dist * Math.sin(angle));
			path.moveTo(dist / 2, dist / 2 - 1);
			path.lineTo(2 * dist + dist / 2, dist / 2 - 1);
			path.lineTo(dist + dist / 2, dist + dist / 2);
			g2d.fill(path);
			path.reset();
			path.moveTo(dist + dist / 2, dist + dist / 2);
			path.lineTo(2 * dist + dist / 2, 2 * dist + dist / 2);
			path.lineTo(dist / 2, dist * 2 + dist / 2);
			g2d.fill(path);
		} else { // 0 degrees
			int distInt = (int) dist;
			g2d.fillRect(distInt / 2, distInt / 2, distInt, distInt);
			g2d.fillRect(distInt + distInt / 2, distInt + distInt / 2, distInt,
			        distInt);
		}
		return true;
	}

	protected void drawHoneycomb(float dist, GGraphics2D g2d) {
		float sin30dist = (float) (Math.sin(Math.PI / 6) * dist / 2);
		float side = dist - 2 * sin30dist;
		GGeneralPath path = AwtFactory.prototype.newGeneralPath();
		path.moveTo(dist - side / 2, dist);
		path.lineTo(dist, dist);
		path.lineTo(dist + sin30dist, dist + dist / 2);
		path.lineTo(2 * dist - sin30dist, dist + dist / 2);
		path.lineTo(2 * dist, dist);
		path.lineTo(2 * dist + side / 2, dist);
		g2d.draw(path);
		path.reset();
		path.moveTo(dist - side / 2, 2 * dist);
		path.lineTo(dist, 2 * dist);
		path.lineTo(dist + sin30dist, dist + dist / 2);
		g2d.draw(path);
		path.reset();
		path.moveTo(2 * dist - sin30dist, dist + dist / 2);
		path.lineTo(2 * dist, 2 * dist);
		path.lineTo(2 * dist + side / 2, 2 * dist);
		g2d.draw(path);
	}

	protected void drawHatching(double angle, double y, int xInt, int yInt,
	        GGraphics2D g2d) {
		if (angle == 0) { // horizontal

			g2d.drawLine(0, yInt, xInt * 3, yInt);
			g2d.drawLine(0, yInt * 2, xInt * 3, yInt * 2);

		} else if (Kernel.isEqual(Math.PI / 2, angle, 10E-8)) { // vertical
			g2d.drawLine(xInt, 0, xInt, yInt * 3);
			g2d.drawLine(xInt * 2, 0, xInt * 2, yInt * 3);

		} else if (y > 0) {
			g2d.drawLine(xInt * 3, 0, 0, yInt * 3);
			g2d.drawLine(xInt * 3, yInt, xInt, yInt * 3);
			g2d.drawLine(xInt * 2, 0, 0, yInt * 2);
		} else {
			g2d.drawLine(0, 0, xInt * 3, yInt * 3);
			g2d.drawLine(0, yInt, xInt * 2, yInt * 3);
			g2d.drawLine(xInt, 0, xInt * 3, yInt * 2);
		}
	}

}