package org.geogebra.common.euclidian;

import org.geogebra.common.awt.AwtFactory;
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
import org.geogebra.common.awt.VectorPatternPaint;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.util.DoubleUtil;

/**
 * Handles hatching of fillable geos
 */
public class HatchingHandler {

	private final EuclidianView view;
	private GBufferedImage bufferedImage = null;
	private final GGeneralPath path;

	/**
	 * 
	 */
	public HatchingHandler(EuclidianView view) {
		path = AwtFactory.getPrototype().newGeneralPath();
		this.view = view;
	}

	/**
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
	public final GPaint getHatchingTexture(GBasicStroke defObjStroke,
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

		int xInt = (int) Math.abs(Math.round(x));
		int yInt = (int) Math.abs(Math.round(y));
		if (angle == 0 // horizontal
				|| DoubleUtil.isEqual(Kernel.PI_HALF, angle, 10E-8)) { // vertical

			xInt = (int) dist;
			yInt = (int) dist;
		}

		// special vector hatching for vector graphics export
		ExportType exportType = app.isHTML5Applet() && app.isExporting()
				? app.getExportType() : ExportType.NONE;

		int exportScale = 1;

		// use higher resolution when exporting
		// to avoid blockiness
		if (app.isExporting()) {
			// arbitrary (can run out of memory if too high though)
			exportScale = (int) Math.ceil(app.getExportScale());
			xInt *= exportScale;
			yInt *= exportScale;
			dist *= exportScale;
		}
		bufferedImage = null;
		GGraphics2D g2d = createImage(bgColor, backgroundTransparency,
				xInt * exportScale, yInt * exportScale, exportType);
		g2d.setColor(color);
		if (exportScale != 1) {
			objStroke = AwtFactory.getPrototype()
					.newBasicStroke(objStroke.getLineWidth() * exportScale);
		}
		g2d.setStroke(objStroke);

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

			// multiply for sin for to have the same size in 0 and 45
			if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) {
				dist = Math.round(dist * Math.sin(angle) / 2) * 2;
				drawChessboardDiagonal(dist / 2, g2d);
			} else {
				drawChessboard(dist, g2d);
			}
			// use a frame around middle square of our 3 x 3 grid
			height = width = (int) (dist * 2);
			startX = startY = (int) (dist / 2);
			break;
		case HONEYCOMB:
			double centerX = Math.round(dist * Math.sqrt(3) / 2);
			drawHoneycomb(dist, centerX, g2d);
			startY = 0;
			startX = 0;
			height = (int) (dist * 3);
			width = (int) (2 * centerX);
			break;
		case WEAVING:
			startY = startX = xInt / 2;
			height = width = xInt * 2;
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
			drawDotted(dist, g2d, 2 * exportScale);
			break;
		case SYMBOLS:
			if (symbol == null) {
				break;
			}
			GFont font = app.getFontCanDisplay(symbol).deriveFont(GFont.PLAIN,
					(int) (dist * 2.5));
			g2d.setFont(font);
			GTextLayout t = AwtFactory.getPrototype().newTextLayout(symbol,
					font, g2d.getFontRenderContext());
			int tileSize = (int) (Math.round(t.getAscent() + t.getDescent()) / 3);
			g2d = createImage(bgColor, backgroundTransparency,
					tileSize, tileSize, exportType);
			g2d.setColor(color);
			g2d.setStroke(objStroke);
			g2d.setFont(
					app.getFontCanDisplay(symbol).deriveFont(GFont.PLAIN, 24 * exportScale));
			g2d.drawString(symbol, 0, Math.round(t.getAscent()));
			startY = 0;
			startX = 0;
			width = tileSize * 3;
			height = tileSize * 3;
			break;
		case IMAGE:
			break;
		case STANDARD:
			break;
		}

		// use the middle square of our 3 x 3 grid to fill with
		if (bufferedImage == null) {
			return new VectorPatternPaint(g2d, width, height, startX, startY,
					exportType == ExportType.SVG ? VectorPatternPaint.VectorType.SVG
							: VectorPatternPaint.VectorType.PDF);
		} else {
			return AwtFactory.getPrototype().newTexturePaint(
					bufferedImage.getSubimage(startX, startY, width,
							height),
					AwtFactory.getPrototype().newRectangle(0, 0,
							width / exportScale, height / exportScale));
		}
	}

	private GGraphics2D createImage(
			GColor bgColor, double backgroundTransparency,
			int xInt, int yInt, ExportType exportType) {
		GGraphics2D g2d;
		if (exportType == ExportType.PDF_HTML5) {
			g2d = AwtFactory.getPrototype().getPDFGraphics(xInt, yInt);
		} else if (exportType == ExportType.SVG) {
			g2d = AwtFactory.getPrototype().getSVGGraphics(xInt, yInt);
		} else {
			bufferedImage = AwtFactory.getPrototype().newBufferedImage(xInt * 3,
					yInt * 3, 1);
			g2d = bufferedImage.createGraphics();
		}

		// enable anti-aliasing
		g2d.setAntialiasing();

		// enable transparency
		g2d.setTransparent();

		// paint background transparent
		if (backgroundTransparency > 0) {
			GColor base = bgColor == null ? GColor.WHITE : bgColor;
			if (exportType == ExportType.PDF_HTML5) {
				GColor global = view.getBackgroundCommon();
				g2d.setColor(GColor.mixColors(global, base, backgroundTransparency, 255));
			} else {
				g2d.setColor(GColor.newColor(base.getRed(), base.getGreen(),
						base.getBlue(), (int) (backgroundTransparency * 255f)));
			}
			g2d.fillRect(0, 0, xInt * 3, yInt * 3);
		}
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
	 * @param bgColor  object background color
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

			GGraphics2D copyGraphics = copy.createGraphics();

			// enable anti-aliasing
			copyGraphics.setAntialiasing();

			// set total transparency
			copyGraphics.setTransparent();

			// paint background transparent
			if (bgColor == null) {
				copyGraphics.setColor(GColor.newColor(0, 0, 0, 0));
			} else {
				copyGraphics.setColor(bgColor);
			}
			copyGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());
			if (alpha > 0.0f) {
				// set partial transparency
				// AlphaComposite alphaComp = AlphaComposite.getInstance(
				// AlphaComposite.SRC_OVER, alpha);
				GAlphaComposite ac = AwtFactory.getPrototype()
						.newAlphaComposite(alpha);
				copyGraphics.setComposite(ac);

				// paint image with specified transparency
				copyGraphics.drawImage(image, 0, 0);
			}

			tp = AwtFactory.getPrototype().newTexturePaint(copy, tr);
		} else {
			tp = AwtFactory.getPrototype().newTexturePaint(image, tr);
		}
		// setColor is overridden by setPaint for most graphics objects
		// except for canvas2pdf that needs the background color to emulate transparency
		g3.setColor(view.getBackgroundCommon().deriveWithAlpha(0));
		g3.setPaint(tp);
	}

	private void drawWeaving(double angle, int dist, GGraphics2D g2d) {
		if (DoubleUtil.isEqual(Math.PI / 4, angle, 10E-8)) { // 45 degrees
			g2d.drawLine(2 * dist, dist, 5 * dist, 4 * dist);
			g2d.drawLine(3 * dist, 0, 6 * dist, 3 * dist);
			g2d.drawLine(3 * dist, 2 * dist, 0, 5 * dist);
			g2d.drawLine(4 * dist, 3 * dist, dist, 6 * dist);
			g2d.drawLine(2 * dist, dist, dist, 2 * dist);
			g2d.drawLine(2 * dist, 3 * dist, dist, 2 * dist);
			g2d.drawLine(4 * dist, 5 * dist, 6 * dist, 3 * dist);
			g2d.drawLine(3 * dist, 4 * dist, 5 * dist, 6 * dist);
			path.reset();
			path.moveTo(dist, 2 * dist);
			path.lineTo(2 * dist, dist);
			path.lineTo(3 * dist, 2 * dist);
			path.lineTo(2 * dist, 3 * dist);
			g2d.fill(path);
			path.reset();
			path.moveTo(3 * dist, 4 * dist);
			path.lineTo(4 * dist, 3 * dist);
			path.lineTo(5 * dist, 4 * dist);
			path.lineTo(4 * dist, 5 * dist);
			g2d.fill(path);
		} else { // 0 degrees
			g2d.drawRect(dist, dist, 3 * dist, dist);
			g2d.drawRect(2 * dist, 2 * dist, dist, 3 * dist);
			g2d.drawRect(3 * dist, 3 * dist, 3 * dist, dist);
			g2d.drawRect(4 * dist, 0, dist, 3 * dist);
			g2d.drawRect(-1 * dist, 3 * dist, 3 * dist, dist);
			g2d.drawRect(4 * dist, 4 * dist, dist, 3 * dist);
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
			g2d.drawRect(xInt / 2, yInt, 2 * xInt, yInt);
			g2d.drawLine(xInt + xInt / 2, yInt / 2, xInt + xInt / 2, yInt);
			g2d.drawLine(xInt + xInt / 2, yInt * 2, xInt + xInt / 2,
					yInt * 2 + yInt / 2);
		} else if (DoubleUtil.isEqual(Math.PI / 2, angle, 10E-8)) {
			g2d.drawRect(xInt, yInt / 2, xInt, 2 * yInt);
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

	private static void drawDotted(double dist, GGraphics2D g2d, double size) {
		g2d.fill(AwtFactory.getPrototype().newEllipse2DDouble(dist, dist,
				size, size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DDouble(2 * dist, dist,
				size, size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DDouble(dist, 2 * dist,
				size, size));
		g2d.fill(AwtFactory.getPrototype().newEllipse2DDouble(2 * dist,
				2 * dist, size, size));
	}

	private void drawChessboard(double hatchDist, GGraphics2D g2d) {
			int distInt = (int) hatchDist;
			g2d.fillRect(distInt / 2, distInt / 2, distInt, distInt);
			g2d.fillRect(distInt + distInt / 2, distInt + distInt / 2, distInt,
					distInt);
	}

	private void drawChessboardDiagonal(double dist, GGraphics2D g2d) {
			path.reset();
			path.moveTo(dist, dist);
			path.lineTo(5 * dist, dist);
			path.lineTo(3 * dist, 3 * dist);
			g2d.fill(path);
			path.reset();
			path.moveTo(3 * dist, 3 * dist);
			path.lineTo(5 * dist, 5 * dist);
			path.lineTo(dist, 5 * dist);
			g2d.fill(path);
	}

	private void drawHoneycomb(double dist, double centerX, GGraphics2D g2d) {
		path.reset();
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
}
