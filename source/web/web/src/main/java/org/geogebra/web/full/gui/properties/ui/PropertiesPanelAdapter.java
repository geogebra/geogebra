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

package org.geogebra.web.full.gui.properties.ui;

import static org.geogebra.common.properties.PropertyView.*;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.PropertyViewFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentComboBox;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentExpandableList;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.properties.ui.panel.ActionableButtonPanel;
import org.geogebra.web.full.gui.properties.ui.panel.DimensionRatioPanel;
import org.geogebra.web.full.gui.properties.ui.panel.IconButtonPanel;
import org.geogebra.web.full.gui.properties.ui.panel.LabelStylePanel;
import org.geogebra.web.full.gui.properties.ui.tabs.ScriptTabFactory;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.KeyboardEvent;
import jsinterop.base.Js;

/**
 * Maps properties to UI components for the properties view.
 */
public class PropertiesPanelAdapter {
	private final Localization loc;
	private final AppW app;
	private final List<Widget> widgets = new ArrayList<>();

	/**
	 * @param loc localization
	 * @param app application
	 */
	public PropertiesPanelAdapter(Localization loc, AppW app) {
		this.loc = loc;
		this.app = app;
	}

	/**
	 * @param props properties
	 * @return panel with controls for all the properties
	 */
	public FlowPanel buildPanel(PropertiesArray props) {
		FlowPanel panel = new FlowPanel();
		List<PropertyView> propertyViews = PropertyViewFactory.propertyViewListOf(props);
		for (PropertyView prop: propertyViews) {
			Widget widget = getWidget(prop);
			panel.add(widget);
		}
		panel.addStyleName("sideSheetTab");
		Dom.addEventListener(panel.getElement(), "keydown", event -> {
			KeyboardEvent kbd = Js.uncheckedCast(event);
			if ("Space".equals(kbd.code)) {
				event.preventDefault(); // prevent scroll of panel on SPACE
			}
		});

		new FocusableWidget(AccessibilityGroup.SETTINGS_ITEM,
				AccessibilityGroup.ViewControlId.SETTINGS_VIEW,
				widgets.toArray(widgets.toArray(new Widget[0]))) {
			@Override
			public void focus(Widget widget) {
				widget.addStyleName("keyboardFocus");
				widget.getElement().focus();
			}
		}.attachTo(app);
		return panel;
	}

	/**
	 * Creates widget based on property
	 * @param propertyView {@link PropertyView}
	 * @return {@link Widget}
	 */
	public Widget getWidget(PropertyView propertyView) {
		Widget ret = createWidget(propertyView);
		ret.setVisible(propertyView.isVisible());
		propertyView.setVisibilityUpdateDelegate(() ->
				ret.setVisible(propertyView.isVisible()));
		widgets.add(ret);
		return ret;
	}

	private Widget createWidget(PropertyView propertyView) {
		if (propertyView == null) {
			return null;
		}
		if (propertyView instanceof Checkbox checkBoxProperty) {
			return new ComponentCheckbox(loc, checkBoxProperty,
					checkBoxProperty.getLabel(), checkBoxProperty::setSelected, false);
		}
		/*if (property instanceof RangeProperty) {
			FlowPanel wrapper = new FlowPanel();
			RangeProperty<Integer> rp = (RangeProperty<Integer>) property;
			SliderPanel sliderPanel = new SliderPanel(rp.getMin(), rp.getMax());
			sliderPanel.setTickSpacing(rp.getStep());
			sliderPanel.addValueChangeHandler(change ->
				rp.setValue(change.getValue())
			);
			sliderPanel.setValue(rp.getValue());
			wrapper.add(new Label(property.getName()));
			wrapper.add(sliderPanel);
			return wrapper;
		}*/
		if (propertyView instanceof ActionableButtonRow) {
			return new ActionableButtonPanel((ActionableButtonRow) propertyView);
		}
		if (propertyView instanceof MultiSelectionIconRow) {
			return new LabelStylePanel((MultiSelectionIconRow) propertyView, app);
		}
		if (propertyView instanceof DimensionRatioEditor) {
			return new DimensionRatioPanel(app, this, (DimensionRatioEditor) propertyView);
		}
		if (propertyView instanceof HorizontalSplitView) {
			FlowPanel panel = new FlowPanel();
			panel.addStyleName("horizontalSplitView");
			panel.add(getWidget(((HorizontalSplitView) propertyView).getLeadingPropertyView()));
			panel.add(getWidget(((HorizontalSplitView) propertyView).getTrailingPropertyView()));
			return panel;
		}
		if (propertyView instanceof RelatedPropertyViewCollection relatedPropertyView) {
			FlowPanel panel = new FlowPanel();
			if (relatedPropertyView.getTitle() != null) {
				panel.add(new Label(app.getLocalization().getMenu(relatedPropertyView.getTitle())));
			}
			int contentSpacing = relatedPropertyView.getContentSpacing();
			if (contentSpacing > 0) {
				panel.getElement().addClassName("contentSpacing" + contentSpacing);
			}
			for (PropertyView pw : relatedPropertyView.getPropertyViews()) {
				panel.add(getWidget(pw));
			}
			return panel;
		}
		if (propertyView instanceof ScriptEditor scriptEditor) {
			ScriptTabFactory tabBuilder = new ScriptTabFactory(app, scriptEditor);
			ComponentTab scriptTab = tabBuilder.create();
			widgets.add(scriptTab);
			return scriptTab;
		}
		if (propertyView instanceof ExpandableList) {
			Checkbox leadProperty =
					((ExpandableList) propertyView).getCheckbox();
			ComponentExpandableList expandableList = new ComponentExpandableList(app,
					leadProperty, ((ExpandableList) propertyView).getTitle());
			for (PropertyView prop : ((ExpandableList) propertyView).getItems()) {
				expandableList.addToContent(getWidget(prop));
			}
			return expandableList;
		}
		if (propertyView instanceof Dropdown) {
			ComponentDropDown dropDown = new ComponentDropDown(app,
					((Dropdown) propertyView).getPropertyName(), (Dropdown) propertyView);
			dropDown.setFullWidth(true);
			return dropDown;
		}
		if (propertyView instanceof ComboBox comboBoxProperty) {
			ComponentComboBox comboBox = new ComponentComboBox(app, comboBoxProperty);
			comboBox.setDisabled(!comboBoxProperty.isEnabled());
			return comboBox;
		}
		if (propertyView instanceof SingleSelectionIconRow) {
			return new IconButtonPanel(app, (SingleSelectionIconRow) propertyView, true);
		}
		if (propertyView instanceof ColorSelectorRow colorSelectorRow) {
			List<GColor> colors = colorSelectorRow.getColors();
			// Copy and add null value to enable plus button
			colors = new ArrayList<>(colors);
			colors.add(null);
			ColorChooserPanel colorPanel = new ColorChooserPanel(app, colors,
					color -> {
						boolean handled = false;
						for (int i = 0; i < colorSelectorRow.getColors().size(); i++) {
							if (colorSelectorRow.getColors().get(i) == color) {
								colorSelectorRow.setSelectedColorIndex(i);
								handled = true;
							}
						}
						if (!handled) {
							colorSelectorRow.setCustomColor(color);
						}
					},
					colorSelectorRow.getPropertyName());
			Integer index = colorSelectorRow.getSelectedColorIndex();
			if (index == null) {
				index = 0;
			}
			colorPanel.updateColorSelection(colorSelectorRow.getColors().get(index));
			colorPanel.addStyleName("colorPanel");
			return colorPanel;
		}
		/*if (property instanceof FilePropertyFacade) {
			StandardButton upload = new StandardButton(loc.getMenu("ChooseFromFile"));
			upload.addStyleName("openFileBtn");
			upload.addFastClickHandler(event ->
					UploadImagePanel.getUploadButton(app, (fn, data) -> {
						String fileName = ImageManagerW.getMD5FileName(fn, data);

						app.getImageManager().addExternalImage(fileName, data);
						app.getImageManager().triggerSingleImageLoading(fileName, app.getKernel());
						((FilePropertyListFacade) property).setValue(fileName);
					}).click());
			return upload;
		}*/
		if (propertyView instanceof TextField) {
			ComponentInputField inputField = new ComponentInputField(app, "", "",
					(TextField) propertyView);
			inputField.getTextField().getTextComponent().addEnterPressHandler(() -> {
				String text = inputField.getText();
				((TextField) propertyView).setValue(text);
			});
			inputField.setDisabled(!((TextField) propertyView).isEnabled());
			return inputField;
		}
		return new Label(propertyView.toString());
	}
}
