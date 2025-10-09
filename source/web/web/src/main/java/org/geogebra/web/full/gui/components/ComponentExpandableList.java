package org.geogebra.web.full.gui.components;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.graphics.SettingsDependentProperty;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.KeyboardEvent;

public class ComponentExpandableList extends FlowPanel implements SetLabels {
	private final AppW appW;
	private final String titleTransKey;
	private BooleanProperty booleanProperty;
	private boolean expanded = false;
	private Label title;
	private ComponentCheckbox checkbox;
	private FlowPanel contentPanel;

	/**
	 * Creates expandable list component.
	 * @param appW see {@link AppW}
	 * @param booleanProperty property for checkbox
	 * @param titleTransKey translation key of title
	 */
	public ComponentExpandableList(AppW appW, @CheckForNull BooleanProperty booleanProperty,
			String titleTransKey) {
		this.appW = appW;
		if (booleanProperty != null) {
			this.booleanProperty = booleanProperty;
		}
		this.titleTransKey = titleTransKey;

		addStyleName("expandableList");
		buildExpandableList();
		updateUISelectedState();
		setAccessibilityProperties();
	}

	private void buildExpandableList() {
		FlowPanel header = new FlowPanel();
		header.addStyleName("header");
		AriaHelper.setTabIndex(header, 0);

		addArrowTo(header);
		addTitleTo(header);
		if (booleanProperty != null) {
			addCheckBoxTo(header);
		}

		if (booleanProperty instanceof SettingsDependentProperty) {
			((SettingsDependentProperty) booleanProperty).getSettings()
					.addListener(this::updateCheckbox);
		}

		contentPanel = new FlowPanel();
		contentPanel.addStyleName("containerPanel");

		add(header);
		add(contentPanel);
		addListeners(header);
	}

	private void addArrowTo(FlowPanel header) {
		SimplePanel arrow = new SimplePanel();
		arrow.addStyleName("headerArrow");
		arrow.getElement().setInnerHTML(KeyboardResources.INSTANCE.keyboard_arrowRight_black()
				.getSVG());
		AriaHelper.setAriaHidden(arrow);
		header.add(arrow);
	}

	private void addTitleTo(FlowPanel header) {
		title = BaseWidgetFactory.INSTANCE.newPrimaryText(appW.getLocalization()
				.getMenu(titleTransKey), "title");
		header.add(title);
	}

	private void addCheckBoxTo(FlowPanel header) {
		checkbox = new ComponentCheckbox(appW.getLocalization(), booleanProperty.getValue(), "",
				value -> {
					boolean selected = !booleanProperty.getValue();
					booleanProperty.setValue(selected);
					expanded = selected;
					updateUISelectedState();
				}, true);
		AriaHelper.setLabel(this, appW.getLocalization().getMenu(titleTransKey));
		header.add(checkbox);
	}

	private void addListeners(FlowPanel header) {
		Dom.addEventListener(header.getElement(), "click", (e) -> toggleComponent());

		Dom.addEventListener(header.getElement(), "keydown", event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if ("Enter".equals(e.code) || "Space".equals(e.code)) {
				if (Dom.getActiveElement() == header.getElement()) {
					toggleComponent();
				}
			}
		});
	}

	private void toggleComponent() {
		expanded = !expanded;
		if (expanded) {
			if (booleanProperty != null) {
				checkbox.setSelected(true);
				booleanProperty.setValue(expanded);
			}
		}
		updateUISelectedState();
	}

	private void updateUISelectedState() {
		Dom.toggleClass(this, "extended", expanded);
		AriaHelper.setAriaExpanded(this, expanded);
		AriaHelper.setTabIndex(contentPanel, expanded ? 0 : -1);
	}

	/**
	 * Fills the content with given UI element.
	 * @param widget UI element
	 */
	public void addToContent(Widget widget) {
		contentPanel.add(widget);
	}

	/**
	 * Check if settings update affects this property and update UI if needed.
	 */
	@SuppressWarnings("unused")
	private void updateCheckbox(AbstractSettings settings) {
		boolean selected = booleanProperty.getValue();
		if (checkbox.isSelected() != selected) {
			if (!selected) {
				expanded = false;
			}
			checkbox.setSelected(selected);
			updateUISelectedState();
		}
	}

	@Override
	public void setLabels() {
		String translatedTitle = appW.getLocalization().getMenu(titleTransKey);
		title.setText(translatedTitle);
		AriaHelper.setLabel(this, translatedTitle);
	}

	private void setAccessibilityProperties() {
		AriaHelper.setRole(this, "button");
		AriaHelper.setTabIndex(this, 0);
		AriaHelper.setAriaExpanded(this, false);
	}
}
