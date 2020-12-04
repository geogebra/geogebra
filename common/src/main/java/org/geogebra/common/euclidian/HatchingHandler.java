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
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Handles hatching of fillable geos
 */
public class HatchingHandler {

	private GBufferedImage bufferedImage = null;
	private GBufferedImage subImage = null;
	private GGeneralPath path;
	private GRectangle rect;
	private String svgPath = "";

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
	public final GPaint setHatching(GGraphics2D g3, GBasicStroke defObjStroke,
			GColor color, GColor bgColor, double backgroundTransparency,
			double hatchDist, double angleDegrees, FillType fillType,
			String symbol, App app) {

		// round to nearest 5 degrees
		double angle = Math.round(angleDegrees / 5) * Math.PI / 36;
		GBasicStroke objStroke = defObjStroke;
		// constrain angle between 0 and 175 degrees
		if (angle < 0 || angle >= Math.PI) {
			angle = 0;
		}

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
				|| DoubleUtil.isEqual(Kernel.PI_HALF, angle, 10E-8)) { // vertical

			xInt = (int) dist;
			yInt = xInt;

		}

		// special vector hatching for SVG export
		boolean svg = app.isHTML5Applet() && app.isExporting()
				&& ExportType.SVG.equals(app.getExportType());

		if (svg) {
			String svgString = "";
			String fill = "none";
			String stroke = "stroke:#" + StringUtil.toHexString(color)
					+ "; stroke-width:" + defObjStroke.getLineWidth();
			double width = xInt, height = yInt;

			switch (fillType) {

			case HONEYCOMB:
				double side = dist * Math.sqrt(3) / 2;
				svgString = drawHoneycombSVG(dist);
				width = (2 * side);
				height = (dist * 3);
				break;
			case HATCH:
				svgString = drawHatchingSVG(angle, y, xInt, yInt);
				angle = 0;
				break;
			case CROSSHATCHED:
				svgString = drawHatchingSVG(angle, y, xInt, yInt)
						+ drawHatchingSVG(Math.PI / 2 - angle, -y, xInt, yInt);
				angle = 0;

				break;
			case CHESSBOARD:
				fill = "#" + StringUtil.toHexString(color);
				stroke = "stroke:none";
				if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) {
					dist = dist * Math.sin(angle);
				}
				svgString = drawChessboardSVG(angle, dist);
				height = width = (int) (dist * 2);
				angle = 0;
				break;

			case IMAGE:
			case STANDARD:
			case SYMBOLS:
			case WEAVING:
				Log.debug("not supported");
				//$FALL-THROUGH$
			case BRICK:
				if (angle == 0 || DoubleUtil.isEqual(Math.PI, angle, 10E-8)
						|| DoubleUtil.isEqual(Math.PI / 2, angle, 10E-8)) {
					// startY = startX = xInt / 2;
					height = width *= 2;
				}
				svgString = drawBricksSVG(angle, xInt, yInt);
				angle = 0;
				break;
			case DOTTED:
				angle = 0;
				fill = "#" + StringUtil.toHexString(color);
				stroke = "stroke:none";
				svgString = drawDottedSVG(dist);
				break;
			}

			return new GPaintSVG(svgString, stroke, width, height, angle, fill);
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
			drawChessboard(angle, dist, g2d);
			// multiply for sin for to have the same size in 0 and 45
			if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) {
				dist = dist * Math.sin(angle);
			}
			// use a frame around middle square of our 3 x 3 grid
			height = width = (int) (dist * 2);
			startX = startY = (int) (dist / 2);
			break;
		case HONEYCOMB:
			drawHoneycomb(dist, g2d);
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
			if (angle == 0 || DoubleUtil.isEqual(Math.PI, angle, 10E-8)
					|| DoubleUtil.isEqual(Math.PI / 2, angle, 10E-8)) {
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
					g2d.getFont(), g2d.getFontRenderContext());
			g2d = createImage(objStroke, color, bgColor, backgroundTransparency,
					(int) (Math.round(t.getAscent() + t.getDescent()) / 3),
					(int) (Math.round(t.getAscent() + t.getDescent()) / 3));
			g2d.setFont(
					app.getFontCanDisplay(symbol).deriveFont(GFont.PLAIN, 24));
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
				AwtFactory.getPrototype().newRectangle(0, 0,
						width / exportScale, height / exportScale));
		g3.setPaint(ret);
		return ret;
	}

	private GGraphics2D createImage(GBasicStroke objStroke, GColor color,
			GColor bgColor, double backgroundTransparency, int xInt, int yInt) {
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
			g2d.setColor(GColor.newColor(bgColor.getRed(), bgColor.getGreen(),
					bgColor.getBlue(), (int) (backgroundTransparency * 255f)));

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
	protected void setTexture(GGraphics2D g3, GeoElementND geo, double alpha) {
		setTexture(g3, geo.getFillImage(), geo, geo.getBackgroundColor(), alpha);
	}

	/**
	 * @param g3       graphics
	 * @param image    image
	 * @param fallback geo to be used as foreground color if image is not valid
	 * @param alpha    alpha value
	 */
	public void setTexture(GGraphics2D g3, MyImage image, GeoElementND fallback,
			GColor bgColor, double alpha) {
		if (image == null || image.isSVG()) {
			g3.setPaint(fallback.getFillColor());
			return;
		}

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
				GAlphaComposite ac = AwtFactory.getPrototype()
						.newAlphaComposite(alpha);
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
		if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) { // 45 degrees
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

	private void drawBricks(double angle, int xInt, int yInt, GGraphics2D g2d) {
		if (angle == 0 || DoubleUtil.isEqual(Math.PI, angle, 10E-8)) {
			rect.setRect(xInt / 2.0, yInt, 2 * xInt, yInt);
			g2d.draw(rect);
			g2d.drawLine(xInt + xInt / 2, yInt / 2, xInt + xInt / 2, yInt);
			g2d.drawLine(xInt + xInt / 2, yInt * 2, xInt + xInt / 2,
					yInt * 2 + yInt / 2);
		} else if (DoubleUtil.isEqual(Math.PI / 2, angle, 10E-8)) {
			rect.setRect(xInt, yInt / 2.0, xInt, 2 * yInt);
			g2d.draw(rect);
			g2d.drawLine(xInt / 2, yInt + yInt / 2, xInt, yInt + yInt / 2);
			g2d.drawLine(xInt * 2, yInt + yInt / 2, 2 * xInt + xInt / 2,
					yInt + yInt / 2);
		} else if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) {
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

	private String drawBricksSVG(double angle, int xInt, int yInt) {
		svgReset();

		if (angle == 0 || DoubleUtil.isEqual(Math.PI, angle, 10E-8)) {

			// rectangle
			svgMoveTo(0, yInt / 2.0);
			svgLineTo(2 * xInt, yInt / 2.0);
			svgLineTo(2 * xInt, yInt * 1.5);
			svgLineTo(0, yInt * 1.5);
			svgLineTo(0, yInt / 2.0);

			// whiskers above and below
			svgDrawLine(xInt, 0, xInt, yInt / 2.0);
			svgDrawLine(xInt, yInt * 1.5, xInt, yInt * 2);

		} else if (DoubleUtil.isEqual(Kernel.PI_HALF, angle, 10E-8)) {

			// rect.setRect(xInt/2, 0, xInt/2, 1.5 * yInt);
			// g2d.draw(rect);
			svgMoveTo(xInt / 2.0, 0);
			svgLineTo(1.5 * xInt, 0);
			svgLineTo(1.5 * xInt, 2 * yInt);
			svgLineTo(xInt / 2.0, 2 * yInt);
			svgLineTo(xInt / 2.0, 0);

			svgDrawLine(0, yInt, xInt / 2.0, yInt);
			svgDrawLine(xInt * 1.5, yInt, 2 * xInt, yInt);
		} else if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) {
			svgDrawLine(0, yInt, xInt, 0);
			svgDrawLine(xInt / 2.0, yInt / 2.0, xInt, yInt);
			// avoid missing pixels bottom-left
			svgDrawLine(-xInt, yInt, xInt, -yInt);
		} else {
			svgDrawLine(0, 0, xInt, yInt);
			svgDrawLine(xInt / 2.0, yInt / 2.0, 0, yInt);
			// avoid missing pixels bottom-right
			svgDrawLine(0, -yInt, 2 * xInt, yInt);
		}

		return getSvgPath();
	}

	private static void drawDotted(double dist, GGraphics2D g2d) {
		final double size = 2;
		g2d.fill(AwtFactory.getPrototype().newEllipse2DDouble(dist, dist,
				size, size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DDouble(2 * dist, dist,
				size, size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DDouble(dist, 2 * dist,
				size, size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DDouble(2 * dist,
				2 * dist, size, size));
	}

	private String drawDottedSVG(double dist) {
		svgReset();
		final double size = 1;
		svgCircle(dist / 2, dist / 2, size);
		return getSvgPath();
	}

	private boolean drawChessboard(double angle, double hatchDist,
			GGraphics2D g2d) {
		if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) { // 45 degrees
			double dist = (hatchDist * Math.sin(angle));
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

	private String drawChessboardSVG(double angle, double dist) {
		svgReset();
		if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) { // 45 degrees
			svgMoveTo(dist, 0);
			svgLineTo(2 * dist, dist);
			svgLineTo(dist, 2 * dist);
			svgLineTo(0, dist);
			svgLineTo(dist, 0);

		} else { // 0 degrees
			svgMoveTo(0, 0);
			svgLineTo(dist, 0);
			svgLineTo(dist, dist);
			svgLineTo(0, dist);
			svgLineTo(0, 0);

			svgMoveTo(dist, dist);
			svgLineTo(2 * dist, dist);
			svgLineTo(2 * dist, 2 * dist);
			svgLineTo(dist, 2 * dist);
			svgLineTo(dist, dist);
		}
		return getSvgPath();
	}

	private void drawHoneycomb(double dist, GGraphics2D g2d) {
		double centerX = (dist * Math.sqrt(3) / 2);
		path.moveTo(centerX, dist);
		path.lineTo(centerX, 2 * dist);
		path.lineTo(0, 2 * dist + dist / 2);
		path.lineTo(0, 3 * dist);
		g2d.draw(path);

		double width = centerX + centerX;
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

	private String drawHoneycombSVG(double dist) {
		double centerX = (dist * Math.sqrt(3) / 2);
		double width = centerX + centerX;

		// svgPath = "<pattern id='hexagonggb' patternUnits='userSpaceOnUse'
		// width='"
		// + (dist * Math.sqrt(3)) + "' height='" + (dist * 3)
		// + "'><path fill='none' style='stroke:black; stroke-width:1' d='";
		svgReset();
		svgMoveTo(centerX, dist);
		svgLineTo(centerX, 2 * dist);
		svgLineTo(0, 2 * dist + dist / 2);
		svgLineTo(0, 3 * dist);
		svgMoveTo(centerX, 2 * dist);
		svgLineTo(width, 2 * dist + dist / 2);
		svgLineTo(width, 3 * dist);
		svgMoveTo(0, 0);
		svgLineTo(0, dist / 2);
		svgLineTo(centerX, dist);
		svgLineTo(width, dist / 2);
		svgLineTo(width, 0);

		// svgPath += "'/></pattern>";
		//
		// Log.debug(svgPath);

		return getSvgPath();

	}

	private void svgReset() {
		svgPath = "";
	}

	private void svgLineTo(double x, double y) {
		svgPath += "L" + x + "," + y;

	}

	private void svgCircle(double x, double y, double radius) {
		// two arcs to make a circle
		svgPath += "M" + x + "," + y + "m" + radius + ",0" + "a" + radius + ","
				+ radius + " 0 1,1 " + -2 * radius + ",0" + "a" + radius + ","
				+ radius + " 0 1,1 " + 2 * radius + ",0";
	}

	private void svgMoveTo(double x, double y) {
		svgPath += "M" + x + "," + y;
	}

	private void svgDrawLine(double x0, double y0, double x1, double y1) {
		svgMoveTo(x0, y0);
		svgLineTo(x1, y1);

	}

	private String getSvgPath() {
		return svgPath;
	}

	private static void drawHatching(double angle, double y, int xInt, int yInt,
			GGraphics2D g2d) {
		if (angle == 0) { // horizontal

			g2d.drawLine(0, yInt, xInt * 3, yInt);
			g2d.drawLine(0, yInt * 2, xInt * 3, yInt * 2);

		} else if (DoubleUtil.isEqual(Math.PI / 2, angle, 10E-8)) { // vertical
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

	private String drawHatchingSVG(double angle, double y, int xInt, int yInt) {
		svgReset();
		if (angle == 0) { // horizontal

			svgDrawLine(0, 0, xInt, 0);
			svgDrawLine(0, yInt, xInt, yInt);

		} else if (DoubleUtil.isEqual(Math.PI / 2, angle, 10E-8)) { // vertical
			svgDrawLine(0, 0, 0, yInt);
			svgDrawLine(xInt, 0, xInt, yInt);

		} else if (y > 0) {
			// positive gradient
			svgDrawLine(xInt, 0, 0, yInt);
			svgDrawLine(-xInt, yInt, xInt, -yInt);
			svgDrawLine(xInt * 2, 0, 0, yInt * 2);
		} else {
			// negative gradient
			svgDrawLine(0, 0, xInt, yInt);
			svgDrawLine(0, -yInt, xInt * 2, yInt);
			svgDrawLine(-xInt, 0, xInt, yInt * 2);
		}

		return getSvgPath();
	}

	/**
	 * Used to check whether the hatching image is already loaded
	 * 
	 * @return GBufferedImage subImage
	 */
	public GBufferedImage getSubImage() {
		return subImage;
	}

	/**
	 * @param g2 graphics
	 * @param shape shape
	 * @param app app to decide sync/async fill method
	 */
	public void fill(GGraphics2D g2, GShape shape, App app) {
		if (!app.isHTML5Applet()) {
			g2.fill(shape);
		} else {
			// take care of filling after the image is loaded
			AwtFactory.getPrototype().fillAfterImageLoaded(shape, g2,
					subImage, app);
		}
	}
}
