/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.shared.components;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import org.geogebra.common.main.settings.PenToolsSettings;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public final class PenHighlighterEraserSlider extends FlowPanel {
	private static final int MAX_ERASER_SIZE = 200;
	private static final int MIN_ERASER_SIZE = 10;
	private static final int ERASER_STEP = 10;
	/** default step size to increase line thickness of pen/highlighter */
	private static final int DEFAULT_PEN_STEP = 1;

	private final AppW appW;
	private SliderW slider;
	private Label sliderLabel;
	private Label displayValue;
	private int lastSelectedMode = MODE_PEN;

	/**
	 * Creates a slider component for pen, highlighter thickness and eraser size.
	 * @param appW application
	 */
	public PenHighlighterEraserSlider(AppW appW) {
		this.appW = appW;
		addStyleName("sliderComponent");
		buildGui();
	}

	private void buildGui() {
		sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu("Thickness"), "sliderLabel");
		displayValue = BaseWidgetFactory.INSTANCE.newPrimaryText("", "displayValue");

		FlowPanel labelDisplayHolder = new FlowPanel();
		labelDisplayHolder.addStyleName("labelPreviewHolder");
		labelDisplayHolder.add(sliderLabel);
		labelDisplayHolder.add(displayValue);

		add(labelDisplayHolder);
		buildSlider();
		add(slider);
	}

	private void buildSlider() {
		slider = new SliderW(0, 60);
		slider.addStyleName("slider");
		slider.addInputHandler(() -> sliderValueChanged(slider.getValue()));
	}

	/**
	 * update preview and slider
	 */
	public void update(int mode) {
		lastSelectedMode = mode;
		if (sliderLabel != null) {
			sliderLabel.setText(
					appW.getLocalization().getMenu(mode == MODE_ERASER ? "Size" : "Thickness"));
		}
		setSliderRange(mode != MODE_ERASER);
		updateSliderValue(mode);
	}

	private void sliderValueChanged(double value) {
		if (lastSelectedMode == MODE_ERASER) {
			appW.getSettings().getPenTools().setDeleteToolSize((int) value);
		} else {
			appW.getActiveEuclidianView().getEuclidianController().getPen().setPenSize((int) value);
			update(lastSelectedMode);
		}
		displayValue.setText(String.valueOf(value));
	}

	private void updateSliderValue(int mode) {
		PenToolsSettings settings = appW.getSettings().getPenTools();
		int sliderValue =
				switch (mode) {
					case MODE_ERASER -> settings.getDeleteToolSize();
					case MODE_HIGHLIGHTER -> settings.getLastHighlighterThickness();
					default -> settings.getLastPenThickness();
				};

		slider.setValue((double) sliderValue);
		displayValue.setText(String.valueOf(sliderValue));
	}

	private void setSliderRange(boolean isPenOrHighlighter) {
		slider.setMinimum(
				isPenOrHighlighter ? ThicknessProperty.DEFAULT_MIN_THICKNESS : MIN_ERASER_SIZE);
		slider.setMaximum(
				isPenOrHighlighter ? ThicknessProperty.MAX_PEN_HIGHLIGHTER_SIZE : MAX_ERASER_SIZE);
		slider.setStep(isPenOrHighlighter ? DEFAULT_PEN_STEP : ERASER_STEP);
	}
}
