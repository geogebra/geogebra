package org.geogebra.web.full.gui.components;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

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
			expanded = booleanProperty.getValue();
		}
		this.titleTransKey = titleTransKey;

		addStyleName("expandableList");
		AriaHelper.setRole(this, "checkbox");
		buildExpandableList();
		updateUISelectedState();
	}

	private void buildExpandableList() {
		FlowPanel header = new FlowPanel();
		header.addStyleName("header");

		addArrowTo(header);
		addTitleTo(header);
		if (booleanProperty != null) {
			addCheckBoxTo(header);
		}

		contentPanel = new FlowPanel();
		contentPanel.addStyleName("containerPanel");

		add(header);
		add(contentPanel);

		Dom.addEventListener(header.getElement(), "click", (e) -> {
			expanded = !expanded;
			if (expanded) {
				if (booleanProperty != null) {
					checkbox.setSelected(true);
					booleanProperty.setValue(expanded);
				}
			}
			updateUISelectedState();
		});
	}

	private void addArrowTo(FlowPanel header) {
		SimplePanel arrow = new SimplePanel();
		arrow.addStyleName("arrow");
		arrow.getElement().setInnerHTML(KeyboardResources.INSTANCE.keyboard_arrowRight_black()
				.getSVG());
		header.add(arrow);
	}

	private void addTitleTo(FlowPanel header) {
		title = BaseWidgetFactory.INSTANCE.newPrimaryText(appW.getLocalization()
				.getMenu(titleTransKey), "title");
		header.add(title);
	}

	private void addCheckBoxTo(FlowPanel header) {
		checkbox = new ComponentCheckbox(appW.getLocalization(), expanded, "",
				value -> {
					boolean selected = !booleanProperty.getValue();
					booleanProperty.setValue(selected);
					expanded = selected;
					updateUISelectedState();
				});
		header.add(checkbox);
	}

	private void updateUISelectedState() {
		Dom.toggleClass(this, "extended", expanded);
		AriaHelper.setAriaSelected(this, expanded);
	}

	/**
	 * Fills the content with given UI element.
	 * @param widget UI element
	 */
	public void addToContent(Widget widget) {
		contentPanel.add(widget);
	}

	@Override
	public void setLabels() {
		title.setText(appW.getLocalization().getMenu(titleTransKey));
	}
}
