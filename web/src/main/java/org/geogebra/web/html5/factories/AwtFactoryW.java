package org.geogebra.web.html5.factories;

import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGradientPaint;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.factories.AwtFactoryHeadless;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.html5.awt.GAlphaCompositeW;
import org.geogebra.web.html5.awt.GFontRenderContextW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGradientPaintW;
import org.geogebra.web.html5.awt.GTexturePaintW;
import org.geogebra.web.html5.awt.font.GTextLayoutW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.GGraphics2DWI;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.MyImageW;

import com.google.gwt.core.client.Scheduler;

/**
 * Creates AWT wrappers for web
 *
 */
public class AwtFactoryW extends AwtFactoryHeadless {
	/** to make code more efficient in the following method */
	boolean repaintDeferred = false;

	/** to avoid infinite loop in the following method */
	int repaintsFromHereInProgress = 0;

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			double pixelRatio) {
		return new GBufferedImageW(pixelWidth, pixelHeight, pixelRatio);
	}

	@Override
	public GBufferedImage createBufferedImage(int width, int height,
			boolean transparency) {
		return new GBufferedImageW(width, height, 1.0f, !transparency);
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new Dimension(width, height);
	}

	@Override
	public GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc) {
		return new GTextLayoutW(string, fontLine, (GFontRenderContextW) frc);
	}

	@Override
	public GAlphaComposite newAlphaComposite(double alpha) {
		return new GAlphaCompositeW(alpha);
	}

	@Override
	public GGradientPaint newGradientPaint(double x, double y, GColor bg2,
			double x2, double i, GColor bg) {
		return new GGradientPaintW(x, y, bg2, x2, i, bg);
	}

	@Override
	public GFont newFont(String name, int style, int size) {
		return new GFontW(name, style, size);
	}

	@Override
	public MyImage newMyImage(int pixelWidth, int pixelHeight,
			int typeIntArgb) {
		return new MyImageW(new GBufferedImageW(pixelWidth, pixelHeight, 1)
				.getImageElement(), false);
	}

	@Override
	public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
		return new GTexturePaintW((GBufferedImageW) subimage);
	}

	@Override
	public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
		return new GTexturePaintW(
				new GBufferedImageW(((MyImageW) subimage).getImage()));
	}

	@Override
	public void fillAfterImageLoaded(final GShape shape, final GGraphics2D g3,
			GBufferedImage gi, final App app) {
		{
			if (((GBufferedImageW) gi).isLoaded()) {
				// when the image is already loaded, no new repaint is necessary
				// in theory, the image will be loaded after some repaints so
				// this will not be an infinite loop ...
				g3.fill(shape);
			} else if (repaintsFromHereInProgress == 0) {
				// the if condition makes sure there will be no infinite loop

				((GBufferedImageW) gi).getImageElement().addEventListener("load", (event) -> {
						if (!repaintDeferred) {
							repaintDeferred = true;
							// otherwise, at the first time, issue a
							// complete repaint
							// but schedule it deferred to avoid
							// conflicts
							// in repaints
							Scheduler.get().scheduleDeferred(() -> doRepaint(app));
						}
				});
			}
		}
	}

	/**
	 * Helper method for repainting
	 *
	 * @param app
	 *            application
	 */
	void doRepaint(App app) {
		repaintDeferred = false;
		repaintsFromHereInProgress++;
		((EuclidianViewW) app.getEuclidianView1()).doRepaint();
		if (app.hasEuclidianView2(1)) {
			((EuclidianViewW) app.getEuclidianView2(1)).doRepaint();
		}
		repaintsFromHereInProgress--;
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			GGraphics2D g2) {
		return newBufferedImage(pixelWidth, pixelHeight,
				((GGraphics2DWI) g2).getDevicePixelRatio());
	}

}
