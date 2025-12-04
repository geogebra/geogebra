package org.geogebra.web.full.gui.properties.ui;

import static org.geogebra.common.properties.PropertyView.*;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.PropertyViewFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentComboBox;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentExpandableList;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.properties.ui.panel.DimensionRatioPanel;
import org.geogebra.web.full.gui.properties.ui.panel.IconButtonPanel;
import org.geogebra.web.full.gui.properties.ui.panel.LabelStylePanel;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
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
	 * @param allowFlat whether to allow adding flat classname -- TODO make this always true
	 * @return panel with controls for all the properties
	 */
	public FlowPanel buildPanel(PropertiesArray props, boolean allowFlat) {
		FlowPanel panel = new FlowPanel();
		List<PropertyView> propertyViews = PropertyViewFactory.propertyViewListOf(props);
		for (PropertyView prop: propertyViews) {
			Widget widget = getWidget(prop);
			panel.add(widget);
		}
		panel.addStyleName("sideSheetTab");
		if (!(props.getProperties()[0] instanceof AbstractPropertyCollection) && allowFlat) {
			panel.addStyleName("flatProperties");
		}
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
		if (propertyView instanceof Checkbox) {
			Checkbox checkBoxProperty = (Checkbox) propertyView;
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
		/*if (property instanceof ActionablePropertyCollection<?>) {
			return new ActionableButtonPanel(
					(ActionablePropertyCollection<?>) property);
		}*/
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
		if (propertyView instanceof RelatedPropertyViewCollection) {
			FlowPanel panel = new FlowPanel();
			if (((RelatedPropertyViewCollection) propertyView).getTitle() != null) {
				panel.add(new Label(app.getLocalization()
						.getMenu(((RelatedPropertyViewCollection) propertyView).getTitle())));
			}
			for (PropertyView pw
					: ((RelatedPropertyViewCollection) propertyView).getPropertyViews()) {
				panel.add(getWidget(pw));
			}
			return panel;
		}
		/*
		if (property instanceof NavigationBarPropertiesCollection) {
			FlowPanel panel = createCollectionPanel((PropertyCollection<?>) property);
			panel.addStyleName("navigationBar");

			return panel;
		}

		if (property instanceof ObjectAllEventsProperty) {
			ScriptTabFactory tabBuilder = new ScriptTabFactory(app,
					(ObjectAllEventsProperty) property);
			ComponentTab scriptTab = tabBuilder.create();
			widgets.add(scriptTab);
			return scriptTab;
		}
*/
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
		if (propertyView instanceof ComboBox) {
			ComboBox comboBoxProperty = (ComboBox) propertyView;
			ComponentComboBox comboBox = new ComponentComboBox(app, comboBoxProperty);
			comboBox.setDisabled(!comboBoxProperty.isEnabled());
			return comboBox;
		}
		if (propertyView instanceof SingleSelectionIconRow) {
			return new IconButtonPanel(app, (SingleSelectionIconRow) propertyView, true);
		}
		if (propertyView instanceof ColorSelectorRow) {
			ColorSelectorRow colorSelectorRow = (ColorSelectorRow) propertyView;
			ColorChooserPanel colorPanel = new ColorChooserPanel(app, colorSelectorRow.getColors(),
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
				((TextField) propertyView).setText(text);
			});
			return inputField;
		}
		return new Label(propertyView.toString());
	}
}
