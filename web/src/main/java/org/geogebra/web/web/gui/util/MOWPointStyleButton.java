package org.geogebra.web.web.gui.util;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;

public class MOWPointStyleButton extends MOWStyleButton {

	private static final double RW_MARGIN = 0.3;
	private GGraphics2DW g2;
	private DrawPoint drawPoint;
	private GeoPoint p;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            GGB app.
	 * @param data
	 *            PointStyle icons.
	 */
	public MOWPointStyleButton(AppW app, ImageOrText[] data) {
		super(app, data, 2, -1, new PointStyleModel(app));
		g2 = new GGraphics2DW(canvas);
		double coords[] = { app.getActiveEuclidianView().getXmin() + RW_MARGIN,
				app.getActiveEuclidianView().getYmax() - RW_MARGIN };

		p = new GeoPoint(app.getKernel().getConstruction(),
				coords[0], coords[1], 0);
		drawPoint = new DrawPoint(app.getActiveEuclidianView(), p);
		p.setEuclidianVisible(true);
		setKeepVisible(true);
	}

	/**
	 * 
	 * @param app
	 *            GGB app.
	 * @return Point style button for MOW
	 */
	public static MOWPointStyleButton create(AppW app) {

		pointStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleMap.put(EuclidianView.getPointStyle(i), i);
		}

		ImageOrText[] pointStyleIcons = new ImageOrText[EuclidianView
				.getPointStyleLength()];
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleIcons[i] = GeoGebraIconW
					.createPointStyleIcon(EuclidianView.getPointStyle(i));
		}

		return new MOWPointStyleButton(app, pointStyleIcons);
	}

	@Override
	public void handlePopupActionEvent() {
		super.handlePopupActionEvent();
		updateCanvas();
	}

	@Override
	protected void updateCanvas() {

		canvas.getContext2d().clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
		double coords[] = { app.getActiveEuclidianView().getXmin() + RW_MARGIN,
				app.getActiveEuclidianView().getYmax() - RW_MARGIN };
		updateGeo();
		drawPoint.update();
		drawPoint.update(coords);
		drawPoint.draw(g2);

	}

	private void updateGeo() {
		p.setPointSize(getSliderValue());
		p.setObjColor(GColor.BLACK);
		p.setPointStyle(getMyTable().getSelectedIndex());
	}
}
