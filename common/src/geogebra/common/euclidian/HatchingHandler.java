package geogebra.common.euclidian;

import geogebra.common.awt.GAlphaComposite;
import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GGeneralPath;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPaint;
import geogebra.common.awt.GRectangle;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement.FillType;

/**
 * Handles hatching of fillable geos
 */
public class HatchingHandler {

	private GBufferedImage bufferedImage = null;

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
	final protected void setHatching(geogebra.common.awt.GGraphics2D g3,
			GBasicStroke objStroke,
			GColor color,
			GColor bgColor, float backgroundTransparency,
			double dist, double angle, FillType fillType) {
		// round to nearest 5 degrees
		angle = Math.round(angle / 5) * Math.PI / 36;

		// constrain angle between 0 and 175 degrees
		if (angle < 0 || angle >= Math.PI)
			angle = 0;

		// constrain distance between 5 and 50 pixels
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
		int currentWidth = bufferedImage == null ? 0 : bufferedImage.getWidth();
		int currentHeight = bufferedImage == null ? 0 : bufferedImage
				.getHeight();

		if (bufferedImage == null || currentWidth < xInt * 3
				|| currentHeight < yInt * 3)
			bufferedImage = AwtFactory.prototype.newBufferedImage(Math.max(currentWidth, xInt * 3),
					Math.max(currentHeight, yInt * 3),
					GBufferedImage.TYPE_INT_ARGB);

		GGraphics2D g2d = bufferedImage.createGraphics();

		// enable anti-aliasing
		g2d.setAntialiasing();

		// enable transparency
		g2d.setTransparent();

		// paint background transparent
		if (bgColor == null) {
			g2d.setColor(AwtFactory.prototype.newColor(255, 255, 255,
					(int) (backgroundTransparency * 255f)));
		} else {
			g2d.setColor(bgColor);
		}


		g2d.fillRect(0, 0, xInt * 3, yInt * 3);

		g2d.setColor(color);

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
			drawHoneycomb( (float) dist, g2d);
			double side = dist * Math.sqrt(3) / 2;
			width = (int) (dist * 3);
			height = (int) (2 * side);
			startX = 0;
			startY = (int) (dist + dist / 2 - side);
			width = (int) (dist * 3);
			height = (int) (2 * side);
			break;
		case BRICK:
			drawBricks(angle, xInt, yInt, g2d);
			if (angle == 0 || Kernel.isEqual(Math.PI, angle, 10E-8)) {
				startY = 0;
				height = width *= 2;
			} else if(Kernel.isEqual(Math.PI/2 , angle, 10E-8)){
				startX = startY = 0;
				height = width *= 2;
			}

			break;
		case DOTTED:
			drawDotted(dist, g2d);
			break;
		}


		// paint with the texturing brush
		GRectangle rect = AwtFactory.prototype.newRectangle(0, 0, width, height);

		// use the middle square of our 3 x 3 grid to fill with
		g3.setPaint(AwtFactory.prototype.newTexturePaint(bufferedImage.getSubimage(startX,
				startY, width, height), rect));


	}

	/**
	 * @param g3 graphics
	 * @param geo geo
	 * @param alpha alpha value
	 */
	protected void setTexture(geogebra.common.awt.GGraphics2D g3,
			GeoElement geo, float alpha) {
		//Graphics2D g2 = geogebra.awt.GGraphics2DD.getAwtGraphics(g3);
		if (geo.getFillImage() == null) {
			g3.setPaint(geo.getFillColor());
			return;
		}

		GBufferedImage image = geo.getFillImage();
		GRectangle tr = AwtFactory.prototype.newRectangle(0, 0, image.getWidth(),
				image.getHeight());

		GPaint tp;

		if (alpha < 1.0f) {
			GBufferedImage copy = AwtFactory.prototype.newBufferedImage(image.getWidth(),
					image.getHeight(), GBufferedImage.TYPE_INT_ARGB);

			GGraphics2D g2d = copy.createGraphics();

			// enable anti-aliasing
			g2d.setAntialiasing();

			// set total transparency
			g2d.setTransparent();

			GColor bgColor = geo
					.getBackgroundColor();

			// paint background transparent
			if (bgColor == null)
				g2d.setColor(AwtFactory.prototype.newColor(0, 0, 0, 0));
			else
				g2d.setColor(bgColor);
			g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

			if (alpha > 0.0f) {
				// set partial transparency
				//AlphaComposite alphaComp = AlphaComposite.getInstance(
				//		AlphaComposite.SRC_OVER, alpha);
				GAlphaComposite ac = AwtFactory.prototype.newAlphaComposite(GAlphaComposite.SRC_OVER, alpha);
				g2d.setComposite(ac);

				// paint image with specified transparency
				g2d.drawImage(image, 0, 0);
			}

			tp = AwtFactory.prototype.newTexturePaint(copy, tr);
		} else {
			tp = AwtFactory.prototype.newTexturePaint(image, tr);
		}

		// tr = new Rectangle2D.Double(0, 0, 200, 200);
		// tp = new TexturePaint(getSeamlessTexture(4, 50), tr);

		g3.setPaint(tp);
	}

	private static void drawBricks(double angle, int xInt, int yInt, GGraphics2D g2d) {
		if (angle == 0 || Kernel.isEqual(Math.PI , angle, 10E-8)){
			g2d.drawRect(xInt, 0, 2*xInt, yInt);
			g2d.drawLine(xInt * 2, yInt, xInt * 2 , yInt * 2);
		} else if (Kernel.isEqual(Math.PI/2 , angle, 10E-8)){
			g2d.drawRect(0, 0, xInt, 2 * yInt);
			g2d.drawLine(xInt , yInt, 2 * xInt , yInt);		
		} else if (Kernel.isEqual(Math.PI/4 , angle, 10E-8)){
			g2d.drawLine(xInt * 3, 0, 0, yInt * 3);
			g2d.drawLine(xInt * 3, yInt, xInt, yInt * 3);
			g2d.drawLine(xInt * 2, 0, 0, yInt * 2);				
			g2d.drawLine(xInt +xInt/2, yInt+yInt/2, 2*xInt, yInt * 2);			
		} else {
			g2d.drawLine(0, 0, xInt * 3, yInt * 3);
			g2d.drawLine(0, yInt, xInt * 2, yInt * 3);
			g2d.drawLine(xInt, 0, xInt * 3, yInt * 2);
			g2d.drawLine(xInt +xInt/2, yInt+yInt/2, xInt, yInt * 2);
		}
	}

	private static void drawDotted(double dist, GGraphics2D g2d) {
		int distInt = (int) dist;
		int size = 2;
		g2d.fill(AwtFactory.prototype.newEllipse2DFloat(distInt, distInt, size, size));
		g2d.fill(AwtFactory.prototype.newEllipse2DFloat(2 * distInt, distInt, size, size));
		g2d.fill(AwtFactory.prototype.newEllipse2DFloat(distInt, 2 * distInt, size, size));
		g2d.fill(AwtFactory.prototype.newEllipse2DFloat(2 * distInt, 2 * distInt, size, size));
	}

	private static boolean drawChessboard(double angle, float dist, GGraphics2D g2d) {
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

	private static void drawHoneycomb(float dist, GGraphics2D g2d) {
		float halfSide=(float)(dist*Math.sqrt(3)/2);	    
		GGeneralPath path = AwtFactory.prototype.newGeneralPath();
		path.moveTo(dist, dist+dist/2);
		path.lineTo( 2*dist, dist+dist/2 );
		path.lineTo(2*dist+dist/2 ,dist+dist/2-halfSide );
		path.lineTo(3*dist, dist+dist/2-halfSide);
		g2d.draw(path);
		path.reset();
		path.moveTo( 2*dist, dist+dist/2 );
		path.lineTo(2*dist+dist/2 , dist+dist/2+halfSide);
		path.lineTo(3*dist, dist+dist/2+halfSide);
		g2d.draw(path);
		path.reset();
		path.moveTo(dist , dist+dist/2);
		path.lineTo(dist/2 , dist+dist/2-halfSide);
		path.lineTo(0 , dist+dist/2-halfSide);
		g2d.draw(path);
		path.reset();
		path.moveTo(dist , dist+dist/2);
		path.lineTo(dist/2 , dist+dist/2+halfSide);
		path.lineTo(0 , dist+dist/2+halfSide);
		g2d.draw(path);		
	}

	private static void drawHatching(double angle, double y, int xInt, int yInt,
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