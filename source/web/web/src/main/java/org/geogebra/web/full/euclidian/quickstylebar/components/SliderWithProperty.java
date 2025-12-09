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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertySupplier;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.OpacityProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.web.full.gui.util.LineStylePreview;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class SliderWithProperty extends FlowPanel {
	private final AppW appW;
	private RangePropertyListFacade<?> property;
	private final PropertySupplier propertySupplier;
	private LineStylePreview preview;
	private Label unitLabel;
	private SliderPanelW sliderPanel;
	private int rangeValue;
	private int lineType;
	private GColor color;
	private boolean dragging;

	/**
	 * constructor
	 * @param appW - application
	 * @param property - range property
	 * @param lineType - line type
	 * @param color - line color
	 */
	public SliderWithProperty(AppW appW, RangePropertyListFacade<?> property,
			PropertySupplier propertySupplier,
			int lineType, GColor color) {
		this.appW = appW;
		this.property = property;
		this.rangeValue = property.getValue();
		this.lineType = lineType;
		this.color = color;
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

		FlowPanel labelPreviewHolder = new FlowPanel();
		labelPreviewHolder.addStyleName("labelPreviewHolder");
		labelPreviewHolder.add(sliderLabel);
		addPropertyBasedPreview(labelPreviewHolder);

		add(labelPreviewHolder);
		buildSlider();
		add(sliderPanel);
	}

	private void addPropertyBasedPreview(FlowPanel parent) {
		if (getFirstProperty() instanceof ThicknessProperty) {
			preview = new LineStylePreview(30, 30);
			preview.addStyleName("preview");
			parent.add(preview);
		} else if (getFirstProperty() instanceof OpacityProperty) {
			unitLabel = BaseWidgetFactory.INSTANCE.newPrimaryText(((OpacityProperty)
					getFirstProperty()).getValue() + "%", "sliderLabel");
			parent.add(unitLabel);
		}
	}

	private Property getFirstProperty() {
		return property.getFirstProperty();
	}

	private void buildSlider() {
		sliderPanel = new SliderPanelW(property.getMin(),
				property.getMax(), appW.getKernel(), false);
		sliderPanel.getSlider().addStyleName("slider");
		setInitialValue();
		sliderPanel.getSlider().addValueChangeHandler(event -> {
			onInputChangeFinished(sliderPanel.getSlider().getValue().intValue());
		});
		sliderPanel.getSlider().addInputHandler(()
				-> onInputChange(sliderPanel.getSlider().getValue().intValue()));
	}

	private void setInitialValue() {
		Integer val = property.getValue();
		sliderPanel.setValue(val.doubleValue());
		updatePreview();
	}

	private void onInputChange(int val) {
		if (!dragging) {
			property = (RangePropertyListFacade<?>) propertySupplier.updateAndGet();
			dragging = true;
			property.beginSetValue();
		}
		property.setValue(val);

		setRangeValue(val);
	}

	private void onInputChangeFinished(int val) {
		property.setValue(val);
		if (dragging) {
			dragging = false;
			property.endSetValue();
		}
		setRangeValue(val);
	}

	private void updatePreview() {
		if (preview != null) {
			preview.update(rangeValue, lineType, color);
		} else if (unitLabel != null) {
			unitLabel.setText(rangeValue + "%");
		}
	}

	/**
	 * @param rangeValue - line thickness or opacity
	 */
	public void setRangeValue(int rangeValue) {
		this.rangeValue = rangeValue;
		updatePreview();
	}

	/**
	 * @param lineType - line type
	 */
	public void setLineType(int lineType) {
		this.lineType = lineType;
		updatePreview();
	}

	/**
	 * @param color - line color
	 */
	public void setLineColor(GColor color) {
		this.color = color;
		updatePreview();
	}
}
