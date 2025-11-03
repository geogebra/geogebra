package org.geogebra.web.full.gui.properties.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;
import org.geogebra.common.properties.PropertyCollectionWithLead;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.collections.ActionablePropertyCollection;
import org.geogebra.common.properties.impl.collections.FilePropertyCollection;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.graphics.AxisCrossPropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisUnitPropertyCollection;
import org.geogebra.common.properties.impl.graphics.ClippingPropertyCollection;
import org.geogebra.common.properties.impl.graphics.Dimension2DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.Dimension3DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.common.properties.impl.graphics.NavigationBarPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.SettingsDependentProperty;
import org.geogebra.common.properties.impl.objects.DefinitionProperty;
import org.geogebra.common.properties.impl.objects.ObjectAllEventsProperty;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.MulticastEvent;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentComboBox;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentExpandableList;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.components.HasDisabledState;
import org.geogebra.web.full.gui.dialog.image.UploadImagePanel;
import org.geogebra.web.full.gui.properties.ui.panel.ActionableButtonPanel;
import org.geogebra.web.full.gui.properties.ui.panel.Dimension3DPanel;
import org.geogebra.web.full.gui.properties.ui.panel.DimensionPanel;
import org.geogebra.web.full.gui.properties.ui.panel.GridDistancePanel;
import org.geogebra.web.full.gui.properties.ui.panel.IconButtonPanel;
import org.geogebra.web.full.gui.properties.ui.panel.LabelStylePanel;
import org.geogebra.web.full.gui.properties.ui.tabs.ScriptTabFactory;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.shared.components.tab.ComponentTab;
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
		for (Property prop: props.getProperties()) {
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

	private void synchronizeSelectedIndex(NamedEnumeratedProperty<?> property,
			ComponentDropDown dropDown) {
		if (property instanceof SettingsDependentProperty) {
			registerListener((SettingsDependentProperty) property,
					s -> dropDown.setSelectedIndex(property.getIndex()));
		}
	}

	/**
	 * Creates widget based on property
	 * @param property {@link Property}
	 * @return {@link Widget}
	 */
	public Widget getWidget(Property property) {
		Widget ret = createWidget(property);
		widgets.add(ret);
		if (property instanceof SettingsDependentProperty) {
			updateEnabledState(ret, property);
			registerListener((SettingsDependentProperty) property,
					s -> updateEnabledState(ret, property));
		}
		return ret;
	}

	private void updateEnabledState(Widget ret, Property prop) {
		if (ret instanceof HasDisabledState) {
			((HasDisabledState) ret).setDisabled(!prop.isEnabled());
		}
		ret.setVisible(prop.isAvailable());
	}

	private Widget createWidget(Property property) {
		if (property instanceof IconAssociatedProperty) {
			IconButton button = new IconButton(app, null, new ImageIconSpec(PropertiesIconAdapter
					.getIcon(((IconAssociatedProperty) property).getIcon())),
					property.getRawName());
			button.setActive(((BooleanProperty) property).getValue());
			button.addFastClickHandler(source -> {
				button.setActive(!button.isActive());
				((BooleanProperty) property).setValue(!((BooleanProperty) property).getValue());
			});
			return button;
		}
		if (property instanceof BooleanProperty) {
			return new ComponentCheckbox(loc, ((BooleanProperty) property)
					.getValue(), property.getRawName(),
					((BooleanProperty) property)::setValue);
		}
		if (property instanceof RangeProperty) {
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
		}
		if (property instanceof ActionablePropertyCollection<?>) {
			return new ActionableButtonPanel(
					(ActionablePropertyCollection<?>) property);
		}
		if (property instanceof LabelStylePropertyCollection) {
			return new LabelStylePanel(
					(LabelStylePropertyCollection) property, this);
		}
		if (property instanceof GridDistancePropertyCollection) {
			return new GridDistancePanel(
					(GridDistancePropertyCollection) property, this);
		}
		if (property instanceof AxisDistancePropertyCollection
			|| property instanceof AxisUnitPropertyCollection
			|| property instanceof AxisCrossPropertyCollection) {
			FlowPanel axisCheckBoxComboBoxPanel = new FlowPanel();
			for (Property prop : ((PropertyCollection<?>) property).getProperties()) {
				Widget widget = getWidget(prop);
				axisCheckBoxComboBoxPanel.add(widget);
			}
			return axisCheckBoxComboBoxPanel;
		}
		if (property instanceof NavigationBarPropertiesCollection) {
			FlowPanel panel = createCollectionPanel((PropertyCollection<?>) property);
			panel.addStyleName("navigationBar");

			return panel;
		}
		if (property instanceof ClippingPropertyCollection) {
			FlowPanel panel = createCollectionPanel((PropertyCollection<?>) property);
			Label title = new Label(loc.getMenu(property.getName()));
			panel.insert(title, 0);
			widgets.add(title);
			return panel;
		}
		if (property instanceof Dimension3DPropertiesCollection) {
			return new Dimension3DPanel(app, this,
					(Dimension3DPropertiesCollection) property);
		}

		if (property instanceof ObjectAllEventsProperty) {
			ScriptTabFactory tabBuilder = new ScriptTabFactory(app,
					(ObjectAllEventsProperty) property);
			ComponentTab scriptTab = tabBuilder.create();
			widgets.add(scriptTab);
			return scriptTab;
		}

		if (property instanceof Dimension2DPropertiesCollection) {
			return new DimensionPanel(app, this,
					(PropertyCollection<?>) property);
		}

		if (property instanceof PropertyCollection) {
			BooleanProperty leadProperty = property instanceof PropertyCollectionWithLead
					? ((PropertyCollectionWithLead) property).leadProperty : null;
			ComponentExpandableList expandableList = new ComponentExpandableList(app,
					leadProperty, property.getName());
			for (Property prop : ((PropertyCollection<?>) property).getProperties()) {
				expandableList.addToContent(getWidget(prop));
			}
			return expandableList;
		}
		if (property instanceof NamedEnumeratedProperty) {
			if (property instanceof LanguageProperty) {
				((LanguageProperty) property).addValueObserver(this::onLanguageChanged);
			}
			ComponentDropDown dropDown = new ComponentDropDown(app, property.getRawName(),
					(NamedEnumeratedProperty<?>) property);
			dropDown.setFullWidth(true);
			synchronizeSelectedIndex((NamedEnumeratedProperty<?>) property, dropDown);
			return dropDown;
		}
		if (property instanceof StringPropertyWithSuggestions) {
			StringPropertyWithSuggestions numProperty = (StringPropertyWithSuggestions) property;
			ComponentComboBox comboBox = new ComponentComboBox(app, numProperty);
			comboBox.setDisabled(!property.isEnabled());
			if (property instanceof SettingsDependentProperty) {
				registerListener((SettingsDependentProperty) property, s ->
					comboBox.setValue(numProperty.getValue())
				);
			}
			return comboBox;
		}
		if (property instanceof IconsEnumeratedProperty) {
			return new IconButtonPanel(app, (IconsEnumeratedProperty<?>) property, true);
		}
		if (property instanceof ColorProperty) {
			ColorChooserPanel colorPanel =  new ColorChooserPanel(app,
					((ColorProperty) property).getValues(),
					((ColorProperty) property)::setValue, property.getRawName());
			colorPanel.updateColorSelection(((ColorProperty) property).getValue());
			colorPanel.addStyleName("colorPanel");
			return colorPanel;
		}
		if (property instanceof FilePropertyCollection) {
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
		}
		if (property instanceof StringProperty) {
			ComponentInputField inputField = new ComponentInputField(app, "",
					property.getRawName(), "", ((StringProperty) property).getValue());
			if (property instanceof DefinitionProperty) {
				((DefinitionProperty) property).setErrorHandler(
						new InputFieldErrorHandler(inputField, app));
			}
			Runnable submit = () -> {
				String text = inputField.getText();
				String error = ((StringProperty) property).validateValue(text);
				if (error == null) {
					((StringProperty) property).setValue(text);
				}
				if (!(property instanceof DefinitionProperty)) {
					inputField.setError(error);
				}
			};
			inputField.getTextField().getTextComponent().addEnterPressHandler(submit);
			return inputField;
		}
		return new Label(property.toString());
	}

	private FlowPanel createCollectionPanel(PropertyCollection<?> property) {
		FlowPanel panel = new FlowPanel();
		for (Property prop : property.getProperties()) {
			Widget widget = getWidget(prop);
			panel.add(widget);
		}
		return panel;
	}

	private void registerListener(SettingsDependentProperty prop,
			MulticastEvent.Listener<AbstractSettings> listener) {
		stateSettingsListener.computeIfAbsent(prop.getSettings(), settings -> {
			settings.addListener(this);
			return new MulticastEvent<>();
		}).addListener(listener);
	}

	@Override
	public void setLabels() {
		for (Widget w : widgets) {
			if (w instanceof SetLabels) {
				((SetLabels) w).setLabels();
			}
		}
	}

	private void onLanguageChanged(ValuedProperty<String> property) {
		if (property instanceof LanguageProperty) {
			if (app.getLoginOperation() != null) {
				app.getLoginOperation().setUserLanguage(property.getValue());
			}
			app.getLAF().storeLanguage(property.getValue());
		}
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		stateSettingsListener.getOrDefault(settings, new MulticastEvent<>())
				.notifyListeners(settings);
	}

	private static class InputFieldErrorHandler implements ErrorHandler {
		private final ComponentInputField inputField;
		private final AppW app;

		public InputFieldErrorHandler(ComponentInputField inputField, AppW app) {
			this.inputField = inputField;
			this.app = app;
		}

		@Override
		public void showError(@CheckForNull String msg) {
			inputField.setError(msg);
		}

		@Override
		public void showCommandError(String command, String message) {
			app.getDefaultErrorHandler().showCommandError(command, message);
		}

		@Override
		public String getCurrentCommand() {
			return "";
		}

		@Override
		public boolean onUndefinedVariables(String string,
				AsyncOperation<String[]> callback) {
			return false;
		}

		@Override
		public void resetError() {
			inputField.setError(null);
		}
	}
}
