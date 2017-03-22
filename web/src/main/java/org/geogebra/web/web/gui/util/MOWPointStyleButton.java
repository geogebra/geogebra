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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Button for point style with preview
 * 
 * @author Laszlo
 */
public class MOWPointStyleButton extends PointStylePopup {
	/** Size of the value canvas */
	private static final int CANVAS_SIZE = 32;

	/** The value canvas next to the slider */
	protected Canvas canvas;

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
		super(app, data, 2, -1, SelectionTable.MODE_ICON, true, true,
				new PointStyleModel(app));

		// Rearranging content.
		VerticalPanel panel = ((ButtonPopupMenu) getMyPopup()).getPanel();
		panel.clear();
		panel.add(sliderPanel);
		panel.add(getMyTable());
		canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceHeight(CANVAS_SIZE);
		canvas.setCoordinateSpaceWidth(CANVAS_SIZE);
		sliderPanel.add(canvas);
		g2 = new GGraphics2DW(canvas);
		double coords[] = { app.getActiveEuclidianView().getXmin() + RW_MARGIN,
				app.getActiveEuclidianView().getYmax() - RW_MARGIN };

		p = new GeoPoint(app.getKernel().getConstruction(),
				coords[0], coords[1], 0);
		drawPoint = new DrawPoint(app.getActiveEuclidianView(), p);
		p.setEuclidianVisible(true);
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

		MOWPointStyleButton btn = new MOWPointStyleButton(app, pointStyleIcons);
		btn.setKeepVisible(true);
		return btn;
	}

	@Override
	public void handlePopupActionEvent() {
		super.handlePopupActionEvent();
		updateCanvas();
	}

	@Override
	protected void onClickAction() {
		super.onClickAction();
		updateCanvas();
	}

	@Override
	public void onSliderInput() {
		super.onSliderInput();
		updateCanvas();
	}

	/**
	 * No text (but canvas) for slider so leave this empty.
	 */
	@Override
	protected void setSliderText(String text) {
		// intentionally blank
	}

	private void updateCanvas() {

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
