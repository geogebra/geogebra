package org.geogebra.web.full.gui.properties.ui;

import static org.geogebra.common.properties.PropertyView.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.PropertyViewFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.util.MulticastEvent;
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
public class PropertiesPanelAdapter implements SetLabels, SettingListener {
	private final Localization loc;
	private final AppW app;
	private final List<Widget> widgets = new ArrayList<>();
	private final Map<AbstractSettings, MulticastEvent<AbstractSettings>>
			stateSettingsListener = new HashMap<>();

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

	/*private void synchronizeSelectedIndex(NamedEnumeratedProperty<?> property,
			ComponentDropDown dropDown) {
		if (property instanceof SettingsDependentProperty) {
			registerListener((SettingsDependentProperty) property,
					s -> dropDown.setSelectedIndex(property.getIndex()));
		}
	}*/

	/**
	 * Creates widget based on property
	 * @param property {@link PropertyView}
	 * @return {@link Widget}
	 */
	public Widget getWidget(PropertyView property) {
		Widget ret = createWidget(property);
		widgets.add(ret);
		/*if (property instanceof SettingsDependentProperty) {
			updateEnabledState(ret, property);
			registerListener((SettingsDependentProperty) property,
					s -> updateEnabledState(ret, property));
		}*/
		return ret;
	}

	/*private void updateEnabledState(Widget ret, Property prop) {
		if (ret instanceof HasDisabledState) {
			((HasDisabledState) ret).setDisabled(!prop.isEnabled());
		}
		ret.setVisible(prop.isAvailable());
	}*/

	private Widget createWidget(PropertyView property) {
		if (property == null) {
			return null;
		}
		if (property instanceof Checkbox) {
			return new ComponentCheckbox(loc, ((Checkbox) property)
					.isSelected(), ((Checkbox) property).getLabel(),
					((Checkbox) property)::setSelected);
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
		if (property instanceof MultiSelectionIconRow) {
			return new LabelStylePanel((MultiSelectionIconRow) property, app);
		}
		if (property instanceof DimensionRatioEditor) {
			return new DimensionRatioPanel(app, this, (DimensionRatioEditor) property);
		}
		if (property instanceof HorizontalSplitView) {
			FlowPanel panel = new FlowPanel();
			panel.addStyleName("horizontalSplitView");
			panel.add(getWidget(((HorizontalSplitView) property).getLeadingPropertyView()));
			panel.add(getWidget(((HorizontalSplitView) property).getTrailingPropertyView()));
			return panel;
		}
		if (property instanceof RelatedPropertyViewCollection) {
			FlowPanel panel = new FlowPanel();
			if (((RelatedPropertyViewCollection) property).getTitle() != null) {
				panel.add(new Label(app.getLocalization()
						.getMenu(((RelatedPropertyViewCollection) property).getTitle())));
			}
			for (PropertyView pw : ((RelatedPropertyViewCollection) property).getPropertyViews()) {
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
		if (property instanceof ExpandableList) {
			Checkbox leadProperty =
					((ExpandableList) property).getCheckbox();
			ComponentExpandableList expandableList = new ComponentExpandableList(app,
					leadProperty, ((ExpandableList) property).getTitle());
			for (PropertyView prop : ((ExpandableList) property).getItems()) {
				expandableList.addToContent(getWidget(prop));
			}
			return expandableList;
		}
		if (property instanceof Dropdown) {
			//if (property instanceof LanguageProperty) {
			//((LanguageProperty) property).addValueObserver(this::onLanguageChanged);
			//}
			ComponentDropDown dropDown = new ComponentDropDown(app,
					((Dropdown) property).getPropertyName(), (Dropdown) property);
			dropDown.setFullWidth(true);
			//synchronizeSelectedIndex((NamedEnumeratedProperty<?>) property, dropDown);
			return dropDown;
		}
		if (property instanceof ComboBox) {
			ComboBox comboBoxProperty = (ComboBox) property;
			ComponentComboBox comboBox = new ComponentComboBox(app, comboBoxProperty);
			comboBox.setDisabled(!comboBoxProperty.isEnabled());
			//if (property instanceof SettingsDependentProperty) {
			//registerListener((SettingsDependentProperty) property, s ->
			//comboBox.setValue(numProperty.getValue()));
			//}
			return comboBox;
		}
		if (property instanceof SingleSelectionIconRow) {
			return new IconButtonPanel(app, (SingleSelectionIconRow) property, true);
		}
		if (property instanceof ColorSelectorRow) {
			ColorSelectorRow colorSelectorRow = (ColorSelectorRow) property;
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
		/*if (property instanceof FilePropertyCollection) {
			StandardButton upload = new StandardButton(loc.getMenu("ChooseFromFile"));
			upload.addStyleName("openFileBtn");
			upload.addFastClickHandler(event ->
					UploadImagePanel.getUploadButton(app, (fn, data) -> {
						String fileName = ImageManagerW.getMD5FileName(fn, data);

						app.getImageManager().addExternalImage(fileName, data);
						app.getImageManager().triggerSingleImageLoading(fileName, app.getKernel());
						((FilePropertyCollection) property).setValue(fileName);
					}).click());
			return upload;
		}*/
		if (property instanceof TextField) {
			ComponentInputField inputField = new ComponentInputField(app, "",
					((TextField) property).getLabel(), "", ((TextField) property).getText());

			Runnable submit = () -> {
				String text = inputField.getText();
				String error = ((TextField) property).getErrorMessage();
				if (error == null) {
					((TextField) property).setText(text);
				} else {
					inputField.showError(error);
				}
			};
			inputField.getTextField().getTextComponent().addEnterPressHandler(submit);
			return inputField;
		}
		return new Label(property.toString());
	}

	/*private void registerListener(SettingsDependentProperty prop,
			MulticastEvent.Listener<AbstractSettings> listener) {
		stateSettingsListener.computeIfAbsent(prop.getSettings(), settings -> {
			settings.addListener(this);
			return new MulticastEvent<>();
		}).addListener(listener);
	}*/

	@Override
	public void setLabels() {
		for (Widget w : widgets) {
			if (w instanceof SetLabels) {
				((SetLabels) w).setLabels();
			}
		}
	}

	/*private void onLanguageChanged(ValuedProperty<String> property) {
		if (property instanceof LanguageProperty) {
			if (app.getLoginOperation() != null) {
				app.getLoginOperation().setUserLanguage(property.getValue());
			}
			app.getLAF().storeLanguage(property.getValue());
		}
	}*/

	@Override
	public void settingsChanged(AbstractSettings settings) {
		stateSettingsListener.getOrDefault(settings, new MulticastEvent<>())
				.notifyListeners(settings);
	}
}
