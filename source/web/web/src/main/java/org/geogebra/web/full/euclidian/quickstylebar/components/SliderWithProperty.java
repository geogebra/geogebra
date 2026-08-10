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

package org.geogebra.web.full.euclidian.quickstylebar.components;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertySupplier;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.OpacityProperty;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class SliderWithProperty extends FlowPanel {
	private final AppW appW;
	private RangePropertyListFacade<?> property;
	private final PropertySupplier propertySupplier;
	private Label unitLabel;
	private SliderW slider;
	private boolean dragging;

	/**
	 * Builds a slider component with {@link Property}.
	 * @param appW application
	 * @param property range property
	 */
	public SliderWithProperty(AppW appW, RangePropertyListFacade<?> property,
			PropertySupplier propertySupplier) {
		this.appW = appW;
		this.property = property;
		this.propertySupplier = propertySupplier;

		styleComponent();
		buildGui();
	}

	private void styleComponent() {
		addStyleName("sliderComponent");
		if (!(getFirstProperty() instanceof ImageOpacityProperty)) {
			addStyleName("withMargin");
		}
	}

	private void buildGui() {
		String sliderText  = getFirstProperty().getName();
		Label sliderLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu(sliderText), "sliderLabel");
		unitLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(getUnitText(), "sliderLabel");

		FlowPanel labelPreviewHolder = new FlowPanel();
		labelPreviewHolder.addStyleName("labelPreviewHolder");
		labelPreviewHolder.add(sliderLabel);
		labelPreviewHolder.add(unitLabel);

		add(labelPreviewHolder);
		buildSlider();
		add(slider);
	}

	private String getUnitText() {
		if (getFirstProperty() instanceof OpacityProperty opacityProperty) {
			return opacityProperty.getValue() + "%";
		} else {
			return String.valueOf(property.getValue());
		}
	}

	private Property getFirstProperty() {
		return property.getFirstProperty();
	}

	private void buildSlider() {
		slider = new SliderW(property.getMin(), property.getMax());
		slider.addStyleName("slider");
		setInitialValue();
		slider.addValueChangeHandler(event -> onInputChangeFinished(slider.getValue().intValue()));
		slider.addInputHandler(() -> onInputChange(slider.getValue().intValue()));
	}

	private void setInitialValue() {
		Integer val = property.getValue();
		slider.setValue(val.doubleValue());
		updateUnitLabel();
	}

	private void onInputChange(int val) {
		if (!dragging) {
			property = (RangePropertyListFacade<?>) propertySupplier.updateAndGet();
			dragging = true;
			property.beginSetValue();
		}
		property.setValue(val);

		updateUnitLabel();
	}

	private void onInputChangeFinished(int val) {
		property.setValue(val);
		if (dragging) {
			dragging = false;
			property.endSetValue();
		}
		updateUnitLabel();
	}

	/**
	 * Updates the unit label.
	 */
	public void updateUnitLabel() {
		if (unitLabel != null) {
			unitLabel.setText(getUnitText());
		}
	}
}
