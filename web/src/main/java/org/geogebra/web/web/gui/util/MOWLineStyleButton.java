package org.geogebra.web.web.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.euclidian.EuclidianLineStylePopup;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Line style button with preview
 * 
 * @author Laszlo
 */
public class MOWLineStyleButton extends EuclidianLineStylePopup {
	/** Size of the value canvas */
	private static final int CANVAS_WIDTH = 30;
	private static final int CANVAS_HEIGHT = 30;
	private static final int STYLE_PREVIEW_MARGIN_X = 2;
	private static final int STYLE_PREVIEW_MARGIN_Y = CANVAS_HEIGHT / 2 - 1;
	private LineStylePreview preview;
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
		panel.addStyleName("mowStylePopup");
		preview = new LineStylePreview(app, CANVAS_WIDTH, CANVAS_HEIGHT);
		sliderPanel.add(preview);
		sliderPanel.addStyleName("mowLinePopup");
		preview.addStyleName("preview");
		setKeepVisible(true);
	}

	@Override
	public void update(Object[] geos) {
		updatePanel(geos);
		updatePreview();
	}

	@Override
	public void handlePopupActionEvent() {
		model.applyLineTypeFromIndex(getSelectedIndex());
		updatePreview();
	}

	@Override
	protected void onClickAction() {
		super.onClickAction();
		updatePreview();
	}

	@Override
	public void onSliderInput() {
		super.onSliderInput();
		updatePreview();
	}

	/**
	 * No text (but canvas) for slider so leave this empty.
	 */
	@Override
	protected void setSliderText(String text) {
		// intentionally left blank
	}

	private void updatePreview() {
		preview.update(getSliderValue(), getSelectedIndex(), GColor.BLACK);
	}

}
