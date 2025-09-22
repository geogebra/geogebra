package org.geogebra.web.full.gui.properties.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHandler;
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
import org.geogebra.common.properties.impl.graphics.DimensionPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.common.properties.impl.graphics.NavigationBarPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.ProjectionPropertyCollection;
import org.geogebra.common.properties.impl.graphics.RulingGridLineStyleProperty;
import org.geogebra.common.properties.impl.graphics.RulingPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.RulingStyleProperty;
import org.geogebra.common.properties.impl.objects.DefinitionProperty;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentExpandableList;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.dialog.image.UploadImagePanel;
import org.geogebra.web.full.gui.properties.ui.panel.ActionableButtonPanel;
import org.geogebra.web.full.gui.properties.ui.panel.DimensionPanel;
import org.geogebra.web.full.gui.properties.ui.panel.GridDistancePanel;
import org.geogebra.web.full.gui.properties.ui.panel.IconButtonPanel;
import org.geogebra.web.full.gui.properties.ui.panel.LabelStylePanel;
import org.geogebra.web.full.gui.properties.ui.settingsListener.SelectionSettingsListener;
import org.geogebra.web.full.gui.properties.ui.settingsListener.StateSettingsListener;
import org.geogebra.web.full.gui.properties.ui.settingsListener.VisibilitySettingsListener;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.util.SliderPanel;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageManagerW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Maps properties to UI components for the properties view.
 */
public class PropertiesPanelAdapter implements SetLabels {
	private final Localization loc;
	private final AppW app;
	private final List<Widget> widgets = new ArrayList<>();
	private final SelectionSettingsListener selectionListenerWidgetCollection;

	/**
	 * @param loc localization
	 * @param app application
	 */
	public PropertiesPanelAdapter(Localization loc, AppW app) {
		this.loc = loc;
		this.app = app;
		selectionListenerWidgetCollection = new SelectionSettingsListener(app);
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
		return panel;
	}

	private void mayRegisterSettingsListenerWidget(NamedEnumeratedProperty<?> property,
			ComponentDropDown dropDown) {
		if (property instanceof RulingStyleProperty) {
			selectionListenerWidgetCollection.registerWidget(dropDown, property);
		}
	}

	/**
	 * Creates widget based on property
	 * @param property {@link Property}
	 * @return {@link Widget}
	 */
	public Widget getWidget(Property property) {
		if (property instanceof IconAssociatedProperty) {
			IconButton button = new IconButton(app, null, new ImageIconSpec(PropertiesIconAdapter
					.getIcon(((IconAssociatedProperty) property).getIcon())),
					property.getRawName());
			button.setActive(((BooleanProperty) property).getValue());
			button.addFastClickHandler(source -> {
				button.setActive(!button.isActive());
				((BooleanProperty) property).setValue(!((BooleanProperty) property).getValue());
			});
			widgets.add(button);
			return button;
		}
		if (property instanceof BooleanProperty) {
			ComponentCheckbox checkbox =  new ComponentCheckbox(loc, ((BooleanProperty) property)
					.getValue(), property.getRawName(),
					checked -> ((BooleanProperty) property).setValue(checked));
			widgets.add(checkbox);
			return checkbox;
		}
		if (property instanceof RangeProperty) {
			FlowPanel wrapper = new FlowPanel();
			RangeProperty<Integer> rp = (RangeProperty<Integer>) property;
			SliderPanel sliderPanel = new SliderPanel(rp.getMin(), rp.getMax());
			sliderPanel.setTickSpacing(rp.getStep());
			sliderPanel.addValueChangeHandler(change -> {
				rp.setValue(change.getValue());
			});
			sliderPanel.setValue(rp.getValue());
			wrapper.add(new Label(property.getName()));
			wrapper.add(sliderPanel);
			return wrapper;
		}
		if (property instanceof ActionablePropertyCollection<?>) {
			ActionableButtonPanel buttonPanel = new ActionableButtonPanel(
					(ActionablePropertyCollection<?>) property);
			widgets.add(buttonPanel);
			return buttonPanel;
		}
		if (property instanceof LabelStylePropertyCollection) {
			FlowPanel labelStylePanel = new LabelStylePanel(
					(LabelStylePropertyCollection) property, this);
			widgets.add(labelStylePanel);
			return labelStylePanel;
		}
		if (property instanceof GridDistancePropertyCollection) {
			GridDistancePanel gridDistancePanel = new GridDistancePanel(app,
					(GridDistancePropertyCollection) property);
			widgets.add(gridDistancePanel);
			return gridDistancePanel;
		}
		if (property instanceof NavigationBarPropertiesCollection) {
			FlowPanel panel = new FlowPanel();
			panel.addStyleName("navigationBar");
			StateSettingsListener stateSettingsListener = new StateSettingsListener(app, 1, 2);
			for (Property prop : ((PropertyCollection<?>) property).getProperties()) {
				Widget widget = getWidget(prop);
				panel.add(widget);
				stateSettingsListener.registerWidget(widget, prop);
			}
			return panel;
		}
		if (property instanceof ProjectionPropertyCollection
				|| property instanceof RulingPropertiesCollection) {
			ComponentExpandableList expandableList = new ComponentExpandableList(app,
					null, property.getName());
			VisibilitySettingsListener collection = new VisibilitySettingsListener(app, 1, 3);
			for (Property prop : ((PropertyCollection<?>) property).getProperties()) {
				Widget widget = getWidget(prop);
				expandableList.addToContent(widget);
				collection.registerWidget(widget, prop);
				widget.setVisible(prop.isEnabled());
			}
			widgets.add(expandableList);
			return expandableList;
		}
		if (property instanceof DimensionPropertiesCollection) {
			ComponentExpandableList expandableList = new DimensionPanel(app, this,
					(PropertyCollection<?>) property);
			widgets.add(expandableList);
			return expandableList;
		}
		if (property instanceof PropertyCollection) {
			BooleanProperty leadProperty = property instanceof PropertyCollectionWithLead
					? ((PropertyCollectionWithLead) property).leadProperty : null;
			ComponentExpandableList expandableList = new ComponentExpandableList(app,
					leadProperty, property.getName());
			for (Property prop : ((PropertyCollection<?>) property).getProperties()) {
				expandableList.addToContent(getWidget(prop));
			}
			widgets.add(expandableList);
			return expandableList;
		}
		if (property instanceof NamedEnumeratedProperty) {
			if (property instanceof LanguageProperty) {
				((LanguageProperty) property).addValueObserver(this::onLanguageChanged);
			}
			ComponentDropDown dropDown = new ComponentDropDown(app, property.getRawName(),
					(NamedEnumeratedProperty<?>) property);
			dropDown.setFullWidth(true);
			mayRegisterSettingsListenerWidget((NamedEnumeratedProperty<?>) property, dropDown);
			widgets.add(dropDown);
			return dropDown;
		}
		if (property instanceof IconsEnumeratedProperty) {
			IconButtonPanel iconButtonPanel = new IconButtonPanel(app,
					(IconsEnumeratedProperty<?>) property);
			widgets.add(iconButtonPanel);
			if (property instanceof RulingGridLineStyleProperty) {
				VisibilitySettingsListener collection = new VisibilitySettingsListener(
						app, 1, 2, 3);
				collection.registerWidget(iconButtonPanel, property);
			}
			iconButtonPanel.setVisible(property.isEnabled());
			return iconButtonPanel;
		}
		if (property instanceof ColorProperty) {
			ColorChooserPanel colorPanel =  new ColorChooserPanel(app,
					((ColorProperty) property).getValues(),
					color -> ((ColorProperty) property).setValue(color), property.getRawName());
			colorPanel.updateColorSelection(((ColorProperty) property).getValue());
			colorPanel.addStyleName("colorPanel");
			widgets.add(colorPanel);
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
			inputField.getTextField().getTextComponent().addBlurHandler(
					evt -> submit.run());
			inputField.getTextField().getTextComponent().addKeyHandler(e -> {
				if (e.isEnterKey()) {
					submit.run();
				}
			});
			widgets.add(inputField);
			return inputField;
		}
		return new Label(property + "");
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
