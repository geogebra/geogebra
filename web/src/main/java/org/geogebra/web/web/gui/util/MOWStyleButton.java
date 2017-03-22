package org.geogebra.web.web.gui.util;

import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Base class for MOW Point and Line Style buttons introducing slider on the top
 * and a canvas next to it to show the change result graphically.
 * 
 * @author Laszlo Gal
 * 
 */
public abstract class MOWStyleButton extends PointStylePopup {

	/** Size of the value canvas */
	protected static final int CANVAS_SIZE = 32;

	/** The value canvas next to the slider */
	protected Canvas canvas;

	/**
	 * Constructor.
	 * 
	 * @param app
	 *            ggb app.
	 * @param data
	 *            Button images.
	 * @param rows
	 *            rows.
	 * @param columns
	 *            Columns
	 * @param hasSlider
	 * @param model
	 */
	public MOWStyleButton(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, boolean hasSlider, PointStyleModel model) {
		super(app, data, rows, columns, SelectionTable.MODE_ICON, true,
				hasSlider, model);

		// Rearranging content.
		VerticalPanel panel = ((ButtonPopupMenu) getMyPopup()).getPanel();
		panel.clear();
		panel.add(sliderPanel);
		panel.add(getMyTable());
		canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceHeight(CANVAS_SIZE);
		canvas.setCoordinateSpaceWidth(CANVAS_SIZE);
		sliderPanel.add(canvas);
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

	/**
	 * updates the canvas next to the slider.
	 */
	protected abstract void updateCanvas();
}