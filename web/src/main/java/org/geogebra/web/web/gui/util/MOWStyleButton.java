package org.geogebra.web.web.gui.util;

import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MOWStyleButton extends PointStylePopup {

	protected Canvas canvas;
	public MOWStyleButton(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode, boolean hasTable,
			boolean hasSlider, PointStyleModel model) {
		super(app, data, rows, columns, mode, hasTable, hasSlider, model);

		// Rearranging content.
		VerticalPanel panel = ((ButtonPopupMenu) getMyPopup()).getPanel();
		panel.clear();
		panel.add(sliderPanel);
		panel.add(getMyTable());
		canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceHeight(24);
		canvas.setCoordinateSpaceWidth(24);
		sliderPanel.add(canvas);
	}

	protected void setSliderText(String text) {

	}

	/**
	 * updates the canvas next to the slider.
	 */
	protected void updateCanvas() {
		Context2d ctx = canvas.getContext2d();
		ctx.setFillStyle("red");
		ctx.fillRect(4, 4, 20, 20);
	}
}
