package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

/**
 * Handles hatching of fillable geos
 */
public class HatchingHandler {

	private GBufferedImage bufferedImage = null;
	private GBufferedImage subImage = null;
	private GGeneralPath path;
	private GRectangle rect;

	/**
	 * 
	 */
	public HatchingHandler() {
		path = AwtFactory.getPrototype().newGeneralPath();
		rect = AwtFactory.getPrototype().newRectangle(0, 0, 1, 1);
	}

	/**
	 * Prototype decides what implementation will be used for static methods
	 */

	/**
	 * @param g3
	 *            graphics
	 * @param defObjStroke
	 *            hatching stroke
	 * @param color
	 *            stroke color
	 * @param bgColor
	 *            background color
	 * @param backgroundTransparency
	 *            alpha value of background
	 * @param hatchDist
	 *            distance between hatches
	 * @param angleDegrees
	 *            hatching angle in degrees
	 * @param fillType
	 *            type of pattern
	 * @param symbol
	 *            for symbol filling
	 * @param app
	 *            needed to determine right font
	 * @return texture paint
	 */
	public final GPaint setHatching(GGraphics2D g3,
			GBasicStroke defObjStroke, GColor color, GColor bgColor,
			float backgroundTransparency, double hatchDist,
			double angleDegrees, FillType fillType, String symbol, App app) {
		// round to nearest 5 degrees
		double angle = Math.round(angleDegrees / 5) * Math.PI / 36;
		GBasicStroke objStroke = defObjStroke;
		// constrain angle between 0 and 175 degrees
		if (angle < 0 || angle >= Math.PI)
			angle = 0;

		// constrain distance between 5 and 50 pixels
		double dist = hatchDist;
		if (dist < 5) {
			dist = 5;
		} else if (dist > 50) {
			dist = 50;
		}

		double x = dist / Math.sin(angle);
		double y = dist / Math.cos(angle);

		int xInt = (int) Math.abs(Math.round((x)));
		int yInt = (int) Math.abs(Math.round((y)));
		if (angle == 0 // horizontal
				|| Kernel.isEqual(Math.PI / 2, angle, 10E-8)) { // vertical

			xInt = (int) dist;
			yInt = xInt;

		}

		int exportScale = 1;

		// use higher resolution when exporting
		// to avoid blockiness
		if (app.isExporting()) {
			// arbitrary (can run out of memory if too high though)
			exportScale = (int) Math.ceil(app.getExportScale());
			xInt *= exportScale;
			yInt *= exportScale;

			objStroke = AwtFactory.getPrototype()
					.newBasicStroke(objStroke.getLineWidth() * exportScale);

		}

		GGraphics2D g2d = createImage(objStroke, color, bgColor,
				backgroundTransparency, xInt * exportScale, yInt * exportScale);

		int startX = xInt;
		int startY = yInt;
		int height = yInt;
		int width = xInt;

		switch (fillType) {

		case HATCH:
			drawHatching(angle, y, xInt, yInt, g2d);
			break;
		case CROSSHATCHED:
			drawHatching(angle, y, xInt, yInt, g2d);
			// draw with complementary degrees
			drawHatching(Math.PI / 2 - angle, -y, xInt, yInt, g2d);
			break;
		case CHESSBOARD:
			drawChessboard(angle, (float) dist, g2d);
			// multiply for sin for to have the same size in 0 and 45
			if (Kernel.isEqual(Math.PI / 4, angle, 10E-8)) {
				dist = dist * Math.sin(angle);
			}
			// use a frame around middle square of our 3 x 3 grid
			height = width = (int) (dist * 2);
			startX = startY = (int) (dist / 2);
			break;
		case HONEYCOMB:
			drawHoneycomb((float) dist, g2d);
			double side = dist * Math.sqrt(3) / 2;
			startY = 0;
			startX = 0;
			height = (int) (dist * 3);
			width = (int) (2 * side);
			break;
		case WEAVING:
			startY = startX = xInt / 2;
			height = width = startX * 4;
			drawWeaving(angle, xInt / 2, g2d);
			break;
		case BRICK:
			if (angle == 0 || Kernel.isEqual(Math.PI, angle, 10E-8)
					|| Kernel.isEqual(Math.PI / 2, angle, 10E-8)) {
				startY = startX = xInt / 2;
				height = width *= 2;
			}
			drawBricks(angle, xInt, yInt, g2d);
			break;
		case DOTTED:
			drawDotted(dist, g2d);
			break;
		case SYMBOLS:
			g2d.setFont(app.getFontCanDisplay(symbol).deriveFont(GFont.PLAIN,
					(int) (dist * 2.5)));
			GTextLayout t = AwtFactory.getPrototype().newTextLayout(symbol,
					g2d.getFont(),
							g2d.getFontRenderContext());
			g2d = createImage(objStroke, color, bgColor,
					backgroundTransparency,
					(Math.round(t.getAscent() + t.getDescent()) / 3),
					(Math.round(t.getAscent() + t.getDescent()) / 3));
			g2d.setFont(app.getFontCanDisplay(symbol).deriveFont(GFont.PLAIN,
					24));
			g2d.drawString(symbol, 0, Math.round(t.getAscent()));
			startY = 0;
			startX = 0;
			width = (int) t.getAscent() + (int) t.getDescent() - 1;
			height = (int) t.getAscent() + (int) t.getDescent() - 1;
			break;
		case IMAGE:
			break;
		case STANDARD:
			break;
		}

		// use the middle square of our 3 x 3 grid to fill with
		GPaint ret = AwtFactory.getPrototype().newTexturePaint(
				subImage = bufferedImage.getSubimage(startX, startY, width,
						height),
				AwtFactory.getPrototype().newRectangle(0, 0, width / exportScale,
						height / exportScale));
		g3.setPaint(ret);
		return ret;
	}

	private GGraphics2D createImage(GBasicStroke objStroke, GColor color,
			GColor bgColor, float backgroundTransparency, int xInt, int yInt) {
		bufferedImage = AwtFactory.getPrototype().newBufferedImage(xInt * 3,
				yInt * 3, 1);

		GGraphics2D g2d = bufferedImage.createGraphics();

		// enable anti-aliasing
		g2d.setAntialiasing();

		// enable transparency
		g2d.setTransparent();

		// paint background transparent
		if (bgColor == null) {
			g2d.setColor(GColor.newColor(255, 255, 255,
					(int) (backgroundTransparency * 255f)));
		} else {
			g2d.setColor(GColor.newColor(bgColor.getRed(),
					bgColor.getGreen(), bgColor.getBlue(),
					(int) (backgroundTransparency * 255f)));

		}

		g2d.fillRect(0, 0, xInt * 3, yInt * 3);
		g2d.setColor(color);

		g2d.setStroke(objStroke);
		return g2d;
	}

	/**
	 * @param g3
	 *            graphics
	 * @param geo
	 *            geo
	 * @param alpha
	 *            alpha value
	 */
	protected void setTexture(GGraphics2D g3,
			GeoElementND geo, float alpha) {
		// Graphics2D g2 = geogebra.awt.GGraphics2DD.getAwtGraphics(g3);
		if (geo.getFillImage() == null || geo.getFillImage().isSVG()) {
			g3.setPaint(geo.getFillColor());
			return;
		}

		MyImage image = geo.getFillImage();
		GRectangle tr = AwtFactory.getPrototype().newRectangle(0, 0,
				image.getWidth(), image.getHeight());

		GPaint tp;

		if (alpha < 1.0f) {
			GBufferedImage copy = AwtFactory.getPrototype()
					.newBufferedImage(image.getWidth(), image.getHeight(), 1);

			GGraphics2D g2d = copy.createGraphics();

			// enable anti-aliasing
			g2d.setAntialiasing();

			// set total transparency
			g2d.setTransparent();

			GColor bgColor = geo.getBackgroundColor();

			// paint background transparent
			if (bgColor == null) {
				g2d.setColor(GColor.newColor(0, 0, 0, 0));
			} else {
				g2d.setColor(bgColor);
			}
			g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

			if (alpha > 0.0f) {
				// set partial transparency
				// AlphaComposite alphaComp = AlphaComposite.getInstance(
				// AlphaComposite.SRC_OVER, alpha);
				GAlphaComposite ac = AwtFactory.getPrototype().newAlphaComposite(
						GAlphaComposite.SRC_OVER, alpha);
				g2d.setComposite(ac);

				// paint image with specified transparency
				g2d.drawImage(image, 0, 0);
			}

			tp = AwtFactory.getPrototype().newTexturePaint(copy, tr);
		} else {
			tp = AwtFactory.getPrototype().newTexturePaint(image, tr);
		}

		// tr = new Rectangle2D.Double(0, 0, 200, 200);
		// tp = new TexturePaint(getSeamlessTexture(4, 50), tr);

		g3.setPaint(tp);
	}

	private void drawWeaving(double angle, int dist, GGraphics2D g2d) {
		if (Kernel.isEqual(Math.PI / 4, angle, 10E-8)) { // 45 degrees
			g2d.drawLine(2 * dist, dist, 5 * dist, 4 * dist);
			g2d.drawLine(3 * dist, 0, 5 * dist, 2 * dist);
			g2d.drawLine(3 * dist, 2 * dist, 0, 5 * dist);
			g2d.drawLine(4 * dist, 3 * dist, 2 * dist, 5 * dist);
			g2d.drawLine(2 * dist, dist, dist, 2 * dist);
			g2d.drawLine(2 * dist, 3 * dist, dist, 2 * dist);
			g2d.drawLine(4 * dist, 5 * dist, 6 * dist, 3 * dist);
			g2d.drawLine(3 * dist, 4 * dist, 5 * dist, 6 * dist);
			path.reset();
			path.moveTo(dist, 2 * dist);
			path.lineTo(2 * dist, dist);
			path.lineTo(3 * dist + 1, 2 * dist + 1);
			path.lineTo(2 * dist + 1, 3 * dist + 1);
			g2d.fill(path);
			path.reset();
			path.moveTo(3 * dist, 4 * dist);
			path.lineTo(4 * dist, 3 * dist);
			path.lineTo(5 * dist + 1, 4 * dist);
			path.lineTo(4 * dist, 5 * dist + 1);
			g2d.fill(path);
		} else { // 0 degrees
			rect.setRect(dist, dist, 3 * dist, dist);
			g2d.draw(rect);
			rect.setRect(2 * dist, 2 * dist, dist, 3 * dist);
			g2d.draw(rect);
			rect.setRect(3 * dist, 3 * dist, 3 * dist, dist);
			g2d.draw(rect);
			rect.setRect(4 * dist, 0, dist, 3 * dist);
			g2d.draw(rect);
			rect.setRect(-1 * dist, 3 * dist, 3 * dist, dist);
			g2d.draw(rect);
			rect.setRect(4 * dist, 4 * dist, dist, 3 * dist);
			g2d.draw(rect);
			g2d.drawLine(4 * dist, 3 * dist, 5 * dist, 3 * dist);
			g2d.drawLine(4 * dist, 4 * dist, 5 * dist, 4 * dist);
			g2d.fillRect(dist, 2 * dist, dist, dist);
			g2d.fillRect(3 * dist, 2 * dist, dist, dist);
			g2d.fillRect(dist, 4 * dist, dist, dist);
			g2d.fillRect(3 * dist, 4 * dist, dist, dist);
		}
	}

	private void drawBricks(double angle, int xInt, int yInt,
			GGraphics2D g2d) {
		if (angle == 0 || Kernel.isEqual(Math.PI, angle, 10E-8)) {
			rect.setRect(xInt / 2.0, yInt,
					2 * xInt, yInt);
			g2d.draw(rect);
			g2d.drawLine(xInt + xInt / 2, yInt / 2, xInt + xInt / 2, yInt);
			g2d.drawLine(xInt + xInt / 2, yInt * 2, xInt + xInt / 2, yInt * 2
					+ yInt / 2);
		} else if (Kernel.isEqual(Math.PI / 2, angle, 10E-8)) {
			rect.setRect(xInt, yInt / 2.0,
					xInt, 2 * yInt);
			g2d.draw(rect);
			g2d.drawLine(xInt / 2, yInt + yInt / 2, xInt, yInt + yInt / 2);
			g2d.drawLine(xInt * 2, yInt + yInt / 2, 2 * xInt + xInt / 2, yInt
					+ yInt / 2);
		} else if (Kernel.isEqual(Math.PI / 4, angle, 10E-8)) {
			g2d.drawLine(xInt * 3, 0, 0, yInt * 3);
			g2d.drawLine(xInt * 3, yInt, xInt, yInt * 3);
			g2d.drawLine(xInt * 2, 0, 0, yInt * 2);
			g2d.drawLine(xInt + xInt / 2, yInt + yInt / 2, 2 * xInt, yInt * 2);
		} else {
			g2d.drawLine(0, 0, xInt * 3, yInt * 3);
			g2d.drawLine(0, yInt, xInt * 2, yInt * 3);
			g2d.drawLine(xInt, 0, xInt * 3, yInt * 2);
			g2d.drawLine(xInt + xInt / 2, yInt + yInt / 2, xInt, yInt * 2);
		}
	}

	private static void drawDotted(double dist, GGraphics2D g2d) {
		int distInt = (int) dist;
		int size = 2;
		g2d.fill(AwtFactory.getPrototype().newEllipse2DFloat(distInt, distInt, size,
				size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DFloat(2 * distInt, distInt,
				size, size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DFloat(distInt, 2 * distInt,
				size, size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DFloat(2 * distInt,
				2 * distInt, size, size));
	}

	private boolean drawChessboard(double angle, float hatchDist,
			GGraphics2D g2d) {
		if (Kernel.isEqual(Math.PI / 4, angle, 10E-8)) { // 45 degrees
			float dist = (float) (hatchDist * Math.sin(angle));
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
			int distInt = (int) hatchDist;
			g2d.fillRect(distInt / 2, distInt / 2, distInt, distInt);
			g2d.fillRect(distInt + distInt / 2, distInt + distInt / 2, distInt,
					distInt);
		}
		return true;
	}

	private void drawHoneycomb(float dist, GGraphics2D g2d) {

		float centerX = (float) (dist * Math.sqrt(3) / 2);
		float width = centerX + centerX;
		path.moveTo(centerX, dist);
		path.lineTo(centerX, 2 * dist);
		path.lineTo(0, 2 * dist + dist / 2);
		path.lineTo(0, 3 * dist);
		g2d.draw(path);

		path.reset();
		path.moveTo(centerX, 2 * dist);
		path.lineTo(width, 2 * dist + dist / 2);
		path.lineTo(width, 3 * dist);
		g2d.draw(path);

		path.reset();
		path.moveTo(0, 0);
		path.lineTo(0, dist / 2);
		path.lineTo(centerX, dist);
		path.lineTo(width, dist / 2);
		path.lineTo(width, 0);
		g2d.draw(path);
	}

	private static void drawHatching(double angle, double y, int xInt,
			int yInt, GGraphics2D g2d) {
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

	/**
	 * Used to check whether the hatching image is already loaded
	 * 
	 * @return GBufferedImage subImage
	 */
	public GBufferedImage getSubImage() {
		return subImage;
	}
}