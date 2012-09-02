package geogebra.euclidian;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidianND.EuclidianViewND;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Michael Borcherds
 * 
 */
public class HatchingHandlerD extends geogebra.common.euclidian.HatchingHandler{

	private static BufferedImage bufferedImage = null;
	public static HatchingHandlerD prototype;
	
	@Override
	protected void dosetHatching(geogebra.common.awt.GGraphics2D g3, geogebra.common.awt.GBasicStroke objStroke,
			geogebra.common.awt.GColor color, geogebra.common.awt.GColor bgColor, float backgroundTransparency,
			double dist, double angle)
	{
		Graphics2D g2 = geogebra.awt.GGraphics2DD.getAwtGraphics(g3);
		// round to nearest 5 degrees
		angle = Math.round(angle / 5) * Math.PI / 36;

		// constrain angle between 0 and 175 degrees
		if (angle < 0 || angle >= Math.PI)
			angle = 0;

		// constrain distance between 5 and 50 pixels
		if (dist < 5)
			dist = 5;
		else if (dist > 50)
			dist = 50;

		double x = dist / Math.sin(angle);
		double y = dist / Math.cos(angle);

		int xInt = (int) Math.abs(Math.round((x)));
		int yInt = (int) Math.abs(Math.round((y)));

		if (angle == 0) { // horizontal

			xInt = 20;
			yInt = (int) dist;

		} else if (Kernel.isEqual(Math.PI / 2, angle, 10E-8)) { // vertical
			xInt = (int) dist;
			yInt = 20;

		}

		int currentWidth = bufferedImage == null ? 0 : bufferedImage.getWidth();
		int currentHeight = bufferedImage == null ? 0 : bufferedImage
				.getHeight();

		if (bufferedImage == null || currentWidth < xInt * 3
				|| currentHeight < yInt * 3)
			bufferedImage = new BufferedImage(Math.max(currentWidth, xInt * 3),
					Math.max(currentHeight, yInt * 3),
					BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = bufferedImage.createGraphics();

		// enable anti-aliasing
		EuclidianViewND.setAntialiasing(g2d);

		// enable transparency
		g2d.setComposite(AlphaComposite.Src);

		// paint background transparent
		if (bgColor == null)
			g2d.setColor(new Color(255, 255, 255,
					(int) (backgroundTransparency * 255f)));
		else
			g2d.setColor(new Color(bgColor.getRed(), bgColor.getGreen(),
					bgColor.getBlue(), (int) (backgroundTransparency * 255f)));
		g2d.fillRect(0, 0, xInt * 3, yInt * 3);

		// g2d.setColor(color);
		g2d.setColor(new Color(color.getRed(), color.getGreen(), color
				.getBlue(), 255));

		g2d.setStroke(geogebra.awt.GBasicStrokeD.getAwtStroke(objStroke));
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

		// paint with the texturing brush
		Rectangle rect = new Rectangle(0, 0, xInt, yInt);

		// use the middle square of our 3 x 3 grid to fill with
		g2.setPaint(new TexturePaint(bufferedImage.getSubimage(xInt, yInt,
				xInt, yInt), rect));

	}

	@Override
	protected void doSetTexture(geogebra.common.awt.GGraphics2D g3, GeoElement geo, float alpha) {
		Graphics2D g2= geogebra.awt.GGraphics2DD.getAwtGraphics(g3);
		if (geo.getFillImage() == null) {
			g2.setPaint(geogebra.awt.GColorD.getAwtColor(geo.getFillColor()));
			return;
		}

		BufferedImage image = geogebra.awt.GBufferedImageD
				.getAwtBufferedImage(geo.getFillImage());
		Rectangle2D tr = new Rectangle2D.Double(0, 0, image.getWidth(),
				image.getHeight());

		TexturePaint tp;

		if (alpha < 1.0f) {
			BufferedImage copy = new BufferedImage(image.getWidth(),
					image.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2d = copy.createGraphics();

			// enable anti-aliasing
			EuclidianViewND.setAntialiasing(g2d);

			// set total transparency
			g2d.setComposite(AlphaComposite.Src);

			Color bgColor = geogebra.awt.GColorD.getAwtColor(geo
					.getBackgroundColor());

			// paint background transparent
			if (bgColor == null)
				g2d.setColor(new Color(0, 0, 0, 0));
			else
				g2d.setColor(new Color(bgColor.getRed(), bgColor.getGreen(),
						bgColor.getBlue(), 0));
			g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

			if (alpha > 0.0f) {
				// set partial transparency
				AlphaComposite alphaComp = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha);
				g2d.setComposite(alphaComp);

				// paint image with specified transparency
				g2d.drawImage(image, 0, 0, null);
			}

			tp = new TexturePaint(copy, tr);
		} else {
			tp = new TexturePaint(image, tr);
		}

		// tr = new Rectangle2D.Double(0, 0, 200, 200);
		// tp = new TexturePaint(getSeamlessTexture(4, 50), tr);

		g2.setPaint(tp);
	}

}
