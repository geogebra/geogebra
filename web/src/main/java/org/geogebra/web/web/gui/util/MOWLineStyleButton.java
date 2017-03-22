package org.geogebra.web.web.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.euclidian.EuclidianLineStylePopup;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MOWLineStyleButton extends EuclidianLineStylePopup {
	/** Size of the value canvas */
	private static final int CANVAS_WIDTH = 50;
	private static final int CANVAS_HEIGHT = 30;

	/** The value canvas next to the slider */
	protected Canvas canvas;

	private static final double RW_MARGIN = 0.3;
	private GGraphics2DW g2;
	private GeoLine line;
	private DrawLine drawLine;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            GGB app.
	 */
	public MOWLineStyleButton(AppW app) {
		super(app, -1, 6, SelectionTable.MODE_ICON, true, true);

		// Rearranging content.
		VerticalPanel panel = ((ButtonPopupMenu) getMyPopup()).getPanel();
		panel.clear();
		panel.add(sliderPanel);
		panel.add(getMyTable());
		canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceHeight(CANVAS_WIDTH);
		canvas.setCoordinateSpaceWidth(CANVAS_HEIGHT);
		sliderPanel.add(canvas);
		g2 = new GGraphics2DW(canvas);
		double coords[] = { app.getActiveEuclidianView().getXmin() + RW_MARGIN,
				app.getActiveEuclidianView().getYmax() - RW_MARGIN };

		line = new GeoLine(app.getKernel().getConstruction(), 0, 1, 0);
		line.setLineThrough(coords[0], coords[1]);
		line.setLineType(1);
		drawLine = new DrawLine(app.getActiveEuclidianView(), line);
		setKeepVisible(true);
	}

	@Override
	public void update(Object[] geos) {
		updatePanel(geos);
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

	}

	private void updateCanvas() {
		canvas.getContext2d().clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		updateGeo();
		drawLine.update();
		drawLine.draw(g2);
	}

	private void updateGeo() {
		line.setObjColor(GColor.BLACK);
		line.setLineThickness(getSliderValue());
		int lineStyle = EuclidianView.getLineType(getSelectedIndex());
		line.setLineType(lineStyle);
		line.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}
}
