package org.geogebra.web.web.gui.util;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;

public class MOWPointStyleButton extends MOWStyleButton {

	private GGraphics2DW g2;
	private DrawPoint drawPoint;
	public MOWPointStyleButton(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode, boolean hasTable,
			boolean hasSlider, PointStyleModel model) {
		super(app, data, rows, columns, mode, hasTable, hasSlider, model);
		g2 = new GGraphics2DW(canvas);
		GeoPoint p = new GeoPoint(app.getKernel().getConstruction(), 1, 1, 0);
		p.setObjColor(GColor.RED);
		p.setPointSize(7);
		drawPoint = new DrawPoint(app.getActiveEuclidianView(), p);
		p.setEuclidianVisible(true);
		updateCanvas();
	}

	public static MOWPointStyleButton create(AppW app, int mode,
			boolean hasSlider, PointStyleModel model) {

		PointStylePopup.mode = mode;

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

		return new MOWPointStyleButton(app, pointStyleIcons, 2, -1,
				SelectionTable.MODE_ICON, true, hasSlider, model);
	}


	@Override
	protected void updateCanvas() {
		double coords[] = { 0, 0 };
		drawPoint.update(coords);
		drawPoint.draw(g2);
		g2.drawRect(1, 1, 20, 20);
	}
}
