package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.HatchingHandler;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

public class ChartFilling {
	private final App application;
	private ArrayList<HatchingHandler> hatchingHandlers = null;

	/**
	 * @param app application
	 */
	public ChartFilling(App app) {
		this.application = app;
	}

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
		HatchingHandler hatchingHandler = getHatchingHandler(index);
		if (chartStyle.getBarFillType(index).isHatch()) {
			setHatchPaint(drawable, barColor, hatchingHandler, chartStyle, index, g2);
			hatchingHandler.fill(g2, fillShape, application);
		} else if (chartStyle.getBarFillType(index) == FillType.IMAGE
				&& !StringUtil.empty(chartStyle.getBarImage(index))) {
			setTextureFromBarOrGeo(g2, geo, chartStyle.getBarImage(index), hatchingHandler);
			g2.fill(fillShape);
		} else if (geo.isHatchingEnabled()
				|| (geo.getFillType() == FillType.IMAGE && geo.getFillImage() != null)) {
			drawable.fillWithHatchOrImage(g2, fillShape, barColor);
		}  else if (geo.getAlphaValue() > 0.0f) {
			setColorFromBarOrGeo(g2, geo, barColor, chartStyle.getBarAlpha(index));
			// magic for switching off dash emulation moved to GGraphics2DW
			g2.fill(fillShape);
		}
	}

	private void setHatchPaint(Drawable drawable, GColor barColor, HatchingHandler hatchingHandler,
			ChartStyle chartStyle, int index, GGraphics2D g2) {
		GPaint gpaint = hatchingHandler.setHatching(g2, drawable.getDecoStroke(),
				barColor, drawable.getGeoElement().getBackgroundColor(),
				chartStyle.getBarAlpha(index),
				chartStyle.getBarHatchDistance(index),
				chartStyle.getBarHatchAngle(index),
				chartStyle.getBarFillType(index),
				chartStyle.getBarSymbol(index),
				application);
		g2.setPaint(gpaint);
	}

	private void setColorFromBarOrGeo(GGraphics2D g2, GeoElement geo,
			GColor barColor, double barAlpha) {
		double alpha = barAlpha >= 0 ? barAlpha : geo.getAlphaValue();
		GColor fillColor = GColor.newColor(barColor.getRed(), barColor.getGreen(),
				barColor.getBlue(), (int) (255 * alpha));
		g2.setPaint(fillColor);
	}

	private void setTextureFromBarOrGeo(GGraphics2D g2, GeoElement geo,
			String fn, HatchingHandler hatchingHandler) {
		MyImage externalImageAdapter =
				application.getExternalImageAdapter(fn, 0, 0);
		hatchingHandler.setTexture(g2,
				externalImageAdapter, geo, geo.getBackgroundColor(), geo.getAlphaValue());
	}
}
