package org.geogebra.web.full.gui.util;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.euclidian.EuclidianLineStylePopup;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Line style button with preview
 * 
 * @author Laszlo
 */
public class MOWLineStyleButton extends EuclidianLineStylePopup {
	/** Size of the value canvas */
	private static final int CANVAS_WIDTH = 30;
	private static final int CANVAS_HEIGHT = 20;
	private LineStylePreview preview;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            GGB app.
	 */
	public MOWLineStyleButton(AppW app) {
		super(app, 5, true);
		// Rearranging content.
		VerticalPanel panel = ((ButtonPopupMenu) getMyPopup()).getPanel();
		panel.clear();
		panel.add(getMyTable());
		panel.add(sliderPanel);
		preview = new LineStylePreview(CANVAS_WIDTH, CANVAS_HEIGHT);
		sliderPanel.add(preview);

		addSliderTitle();
		panel.addStyleName("mowPopup");

		preview.addStyleName("preview");
		setKeepVisible(true);
		updatePreview();
	}

	@Override
	public void update(List<GeoElement> geos) {
		updatePanel(geos.toArray());
		updatePreview();
	}

	@Override
	public void handlePopupActionEvent() {
		super.handlePopupActionEvent();
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

	private void addSliderTitle() {
		titleLabel = new Label();
		titleLabel.addStyleName("thicknessLabel");
		sliderPanel.insert(titleLabel, 0);
		getMySlider().setWidth("140px");
		setLabels();
	}

	/**
	 * No text (but canvas) for slider so leave this empty.
	 */
	@Override
	protected void setSliderText(String text) {
		// intentionally left blank
	}

	@Override
	public void setLabels() {
		titleLabel.setText(app.getLocalization().getMenu("Thickness"));
	}

	private void updatePreview() {
		preview.update(getSliderValue(), getSelectedIndex(), GColor.BLACK);
	}
}
