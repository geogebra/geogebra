package org.geogebra.web.full.gui.properties;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionableProperty;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;
import org.geogebra.common.properties.PropertyCollectionWithLead;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.ActionablePropertyCollection;
import org.geogebra.common.properties.impl.general.RestoreSettingsAction;
import org.geogebra.common.properties.impl.general.SaveSettingsAction;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentExpandableList;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Maps properties to UI components for the properties view.
 */
public class PropertiesPanelAdapter {

	private final Localization loc;
	private final AppW app;

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
		for (Property prop: props.getProperties()) {
			Widget widget = getWidget(prop);
			panel.add(widget);
		}
		panel.addStyleName("sideSheetTab");
		return panel;
	}

	private Widget getWidget(Property property) {
		if (property instanceof BooleanProperty) {
			return new ComponentCheckbox(loc, ((BooleanProperty) property).getValue(),
					property.getName(),
					checked -> ((BooleanProperty) property).setValue(checked));
		}
		if (property instanceof ActionablePropertyCollection<?>) {
			FlowPanel buttonPanel = new FlowPanel();
			buttonPanel.addStyleName("actionableButtonPanel");
			for (ActionableProperty actionableProperty : ((ActionablePropertyCollection<?>)
					property).getProperties()) {
				StandardButton button = new StandardButton(app.getLocalization().getMenu(
						actionableProperty.getName()));
				if (actionableProperty instanceof SaveSettingsAction) {
					button.addStyleName("dialogContainedButton");
				} else if (actionableProperty instanceof RestoreSettingsAction) {
					button.addStyleName("materialOutlinedButton");
				}
				button.addFastClickHandler(source -> actionableProperty.performAction());
				buttonPanel.add(button);
			}
			return buttonPanel;
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
		if (property instanceof IconsEnumeratedProperty) {
			FlowPanel iconPanel = new FlowPanel();
			PropertyResource[] icons = ((IconsEnumeratedProperty<?>) property).getValueIcons();
			int idx = 0;
			for (PropertyResource icon: icons) {
				IconButton btn = new IconButton(app, "",
						new ImageIconSpec(PropertiesIconAdapter.getIcon(icon)));
				iconPanel.add(btn);
				final int index = idx;
				btn.addClickHandler(app.getGlobalHandlers(),
						(w) -> ((IconsEnumeratedProperty<?>) property).setIndex(index));
				idx++;
			}
			return iconPanel;

		}
		if (property instanceof NamedEnumeratedProperty) {
			ComponentDropDown dropDown = new ComponentDropDown(app, property.getName(),
					(NamedEnumeratedProperty<?>) property);
			dropDown.setFullWidth(true);
			return dropDown;
		}
		if (property instanceof ColorProperty) {
			ColorChooserPanel colorPanel =  new ColorChooserPanel(app,
					((ColorProperty) property).getValues(),
					color -> ((ColorProperty) property).setValue(color));
			FlowPanel wrapper = new FlowPanel();
			wrapper.add(new Label(property.getName()));
			wrapper.add(colorPanel);
			return wrapper;
		}
		if (property instanceof StringProperty) {
			ComponentInputField inputField = new ComponentInputField(app, "",
					property.getName(), "", ((StringProperty) property).getValue());
			inputField.getTextField().getTextComponent().addBlurHandler(
					evt -> ((StringProperty) property).setValue(inputField.getText()));
			return inputField;
		}
		return new Label(property + "");
	}
}
