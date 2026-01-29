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

package org.geogebra.web.full.gui.components;

import static org.geogebra.common.properties.PropertyView.*;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentSlider extends FlowPanel implements ConfigurationUpdateDelegate {
	private final AppW appW;
	private final Slider sliderProperty;
	private SliderPanelW sliderPanel;
	private Label displayValue;

	/**
	 * Slider component based on {@link Slider}
	 * @param appW {@link AppW}
	 * @param sliderProperty {@link Slider}
	 */
	public ComponentSlider(AppW appW, Slider sliderProperty) {
		this.appW = appW;
		this.sliderProperty = sliderProperty;
		addStyleName("sliderComponent");
		buildSlider();
		sliderProperty.setConfigurationUpdateDelegate(this);
	}

	private void buildSlider() {
		Label sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu(sliderProperty.getLabel()), "sliderLabel");
		displayValue = BaseWidgetFactory.INSTANCE.newPrimaryText(
				sliderProperty.getDisplayValue(), "displayValue");

		FlowPanel labelDisplayHolder = new FlowPanel();
		labelDisplayHolder.addStyleName("labelPreviewHolder");
		labelDisplayHolder.add(sliderLabel);
		labelDisplayHolder.add(displayValue);

		add(labelDisplayHolder);
		initSlider();
		add(sliderPanel);
	}

	private void initSlider() {
		sliderPanel = new SliderPanelW(sliderProperty.getMin(), sliderProperty.getMax(),
				appW.getKernel(), false);
		sliderPanel.setValue((double) sliderProperty.getValue());
		sliderPanel.getSlider().addStyleName("slider");
		sliderPanel.getSlider().addValueChangeHandler(event ->
				onInputChange(sliderPanel.getValue()));
		sliderPanel.getSlider().addInputHandler(() -> onInputChange(sliderPanel.getValue()));
	}

	private void onInputChange(double val) {
		sliderProperty.setValue((int) val);
		displayValue.setText(sliderProperty.getDisplayValue());
	}

	@Override
	public void configurationUpdated() {
		sliderPanel.setValue((double) sliderProperty.getValue());
		displayValue.setText(sliderProperty.getDisplayValue());
	}
}
