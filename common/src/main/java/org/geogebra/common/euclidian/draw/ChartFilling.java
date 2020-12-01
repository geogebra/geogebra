package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.HatchingHandler;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

public class ChartFilling {
	private ArrayList<HatchingHandler> hatchingHandlers = null;

	private HatchingHandler getHatchingHandler(int i) {
		initHatchingHandlerArray(i);
		HatchingHandler handler = hatchingHandlers.get(i);
		if (handler == null) {
			handler = new HatchingHandler();
			hatchingHandlers.set(i, handler);
		}
		return handler;
	}

	private void initHatchingHandlerArray(int minSize) {
		if (hatchingHandlers == null) {
			hatchingHandlers = new ArrayList<>();
		}
		for (int i = hatchingHandlers.size() - 1; i < minSize; i++) {
			hatchingHandlers.add(null);
		}
	}

	/**
	 * Fills given shape
	 *
	 * @param g2
	 *            graphics
	 * @param fillShape
	 *            shape to be filled
	 */
	public void fill(GGraphics2D g2, GShape fillShape, ChartStyle chartStyle,
			int index, Drawable drawable) {
		if (drawable.isForceNoFill()) {
			return;
		}
		GeoElement geo = drawable.getGeoElement();
		GColor barColor = chartStyle.getBarColor(index);
		if (barColor == null) {
			barColor = geo.getObjectColor();
		}
		App application = geo.getKernel().getApplication();
		HatchingHandler hatchingHandler = getHatchingHandler(index);
		if (chartStyle.getBarFillType(index).isHatch()) {
			GPaint gpaint = hatchingHandler.setHatching(g2, drawable.getDecoStroke(),
					barColor, geo.getBackgroundColor(),
					chartStyle.getBarAlpha(index),
					chartStyle.getBarHatchDistance(index),
					chartStyle.getBarHatchAngle(index),
					chartStyle.getBarFillType(index),
					chartStyle.getBarSymbol(index),
					application);

			g2.setPaint(gpaint);

			if (!application.isHTML5Applet()) {
				g2.fill(fillShape);
			} else {
				GBufferedImage subImage2 =  hatchingHandler.getSubImage();
				// take care of filling after the image is loaded
				AwtFactory.getPrototype().fillAfterImageLoaded(fillShape, g2,
						subImage2, application);
			}
		} else if (chartStyle.getBarFillType(index) == FillType.IMAGE
				&& !StringUtil.empty(chartStyle.getBarImage(index))) {
			MyImage externalImageAdapter =
					application.getExternalImageAdapter(chartStyle.getBarImage(index), 0, 0);
			hatchingHandler.setTexture(g2,
					externalImageAdapter, geo, geo.getBackgroundColor(), geo.getAlphaValue());
			g2.fill(fillShape);
		} else if (geo.isHatchingEnabled()
				|| (geo.getFillType() == FillType.IMAGE && geo.getFillImage() != null)) {
			drawable.fillWithHatchOrImage(g2, fillShape, barColor);
		}  else if (geo.getAlphaValue() > 0.0f) {
			double barAlpha = chartStyle.getBarAlpha(index);
			double alpha = barAlpha >= 0 ? barAlpha : geo.getAlphaValue();
			GColor fillColor = GColor.newColor(barColor.getRed(), barColor.getGreen(),
					barColor.getBlue(), (int) (255 * alpha));
			g2.setPaint(fillColor);
			// magic for switching off dash emulation moved to GGraphics2DW
			g2.fill(fillShape);
		}
	}
}
