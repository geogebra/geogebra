package geogebra.web.euclidian;

import geogebra.common.awt.GAlphaComposite;
import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement.FillType;
import geogebra.web.awt.GAlphaCompositeW;
import geogebra.web.awt.GBufferedImageW;
import geogebra.web.awt.GRectangle2DW;
import geogebra.web.awt.GRectangleW;
import geogebra.web.awt.GTexturePaintW;

/**
 * 
 *
 */
public class HatchingHandlerW extends geogebra.common.euclidian.HatchingHandler {

	private static GBufferedImageW bufferedImage = null;

	@Override
	protected void dosetHatching(GGraphics2D g3, GBasicStroke objStroke,
	        GColor color, GColor bgColor, float backgroundTransparency,
	        double dist, double angle, FillType fillType) {
		GGraphics2D g2 = g3;
		// round to nearest 5 degrees
		angle = Math.round(angle / 5) * Math.PI / 36;

		// constrain angle between 0 and 175 degrees
		if (angle < 0 || angle >= Math.PI) {
			angle = 0;
		}
		
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

		if (angle == 0 || Kernel.isEqual(Math.PI / 2, angle, 10E-8)) { // vertical

			xInt = (int) dist;
			yInt = xInt;

		}

		int currentWidth = bufferedImage == null ? 0 : bufferedImage.getWidth();
		int currentHeight = bufferedImage == null ? 0 : bufferedImage
		        .getHeight();

		// if (bufferedImage == null || currentWidth < xInt * 3
		// || currentHeight < yInt * 3) //somewhy we get null on
		// createPattern...
		bufferedImage = new GBufferedImageW(Math.max(currentWidth, xInt * 3),
		        Math.max(currentHeight, yInt * 3),
		        GBufferedImage.TYPE_INT_ARGB);

		GGraphics2D g2d = bufferedImage.createGraphics();

		// enable anti-aliasing
		EuclidianViewW.setAntialiasingStatic(g2d);

		// enable transparency
		g2d.setComposite(GAlphaCompositeW.Src);

		// paint background transparent
		if (bgColor == null)
			g2d.setColor(new geogebra.web.awt.GColorW(255, 255, 255,
			        (int) (backgroundTransparency * 255f)));
		else
			g2d.setColor(new geogebra.web.awt.GColorW(bgColor.getRed(), bgColor
			        .getGreen(), bgColor.getBlue(),
			        (int) (backgroundTransparency * 255f)));
		g2d.fillRect(0, 0, xInt * 3, yInt * 3);

		// g2d.setColor(color);
		g2d.setColor(new geogebra.web.awt.GColorW(color.getRed(), color
		        .getGreen(), color.getBlue(), 255));

		g2d.setStroke(new geogebra.web.awt.GBasicStrokeW(objStroke));

		// use the middle square of our 3 x 3 grid to fill with
		g2.setPaint(null);
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
			break;
		case HONEYCOMB:
			drawHoneycomb((float)dist, g2d);
			break;
		case BRICK:
			drawBricks(angle, xInt, yInt, g2d);
			break;
		case DOTTED:
			drawDotted(dist, g2d);
			break;
		}

		int size;
		
		if (fillType==FillType.CHESSBOARD) {
			// multiply for sin for to have the same size in 0 and 45
			if (Kernel.isEqual(Math.PI / 4, angle, 10E-8)) { 
				dist = dist * Math.sin(angle);
			}
			
			// use a frame around middle square of our 3 x 3 grid			
			size = (int) (dist * 2);
						
			GRectangleW rect = new GRectangleW(0, 0, size, size);

			g2.setPaint(new GTexturePaintW(bufferedImage.getSubimage(size/4,
					size / 4, size, size), rect));
		
		} else if (fillType==FillType.HONEYCOMB) {
			
			double side=dist*Math.sqrt(3)/2;
			
			GRectangleW rect = new GRectangleW(0, 0, (int)(dist*3), (int)(2*side));

			g2.setPaint(new GTexturePaintW(bufferedImage.getSubimage((int)(0),
					(int)(dist+dist/2-side) , (int)(dist*3), (int)(2*side)), rect));
		} else {
			// paint with the texturing brush
			GRectangleW rect = new GRectangleW(0, 0, xInt, yInt);

			// use the middle square of our 3 x 3 grid to fill with
			g2.setPaint(new GTexturePaintW(bufferedImage.getSubimage(xInt, yInt,
					xInt, yInt), rect));
		}

	}

	@Override
	protected void doSetTexture(GGraphics2D g2, GeoElement geo, float alpha) {
		if (geo.getFillImage() == null) {
			g2.setPaint(geo.getFillColor());
			return;
		}

		GBufferedImageW image = new geogebra.web.awt.GBufferedImageW(
		        geo.getFillImage());
		GRectangle2DW tr = new GRectangle2DW(0, 0, image.getWidth(),
		        image.getHeight());

		GTexturePaintW tp;

		if (alpha < 1.0f) {
			GBufferedImageW copy = new GBufferedImageW(image.getWidth(),
			        image.getHeight(), GBufferedImage.TYPE_INT_ARGB);

			GGraphics2D g2d = copy.createGraphics();

			// enable anti-aliasing
			EuclidianViewW.setAntialiasingStatic(g2d);

			// set total transparency
			g2d.setComposite(GAlphaCompositeW.Src);

			GColor bgColor = geo.getBackgroundColor();

			// paint background transparent
			if (bgColor == null)
				g2d.setColor(new geogebra.web.awt.GColorW(0, 0, 0, 0));
			else
				g2d.setColor(new geogebra.web.awt.GColorW(bgColor.getRed(),
				        bgColor.getGreen(), bgColor.getBlue(), 0));
			g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

			if (alpha > 0.0f) {
				// set partial transparency
				GAlphaCompositeW alphaComp = GAlphaCompositeW.getInstance(
				        GAlphaComposite.SRC_OVER, alpha);
				g2d.setComposite(alphaComp);

				// paint image with specified transparency
				g2d.drawImage(image, null, 0, 0);
			}

			tp = new GTexturePaintW(copy, tr);
		} else {
			tp = new GTexturePaintW(image, tr);
		}

		// tr = new Rectangle2D.Double(0, 0, 200, 200);
		// tp = new TexturePaint(getSeamlessTexture(4, 50), tr);

		g2.setPaint(tp);

	}

}
