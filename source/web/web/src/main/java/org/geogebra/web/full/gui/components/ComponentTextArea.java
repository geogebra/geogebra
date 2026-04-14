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

package org.geogebra.web.full.gui.components;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyView.ConfigurationUpdateDelegate;
import org.geogebra.common.properties.PropertyView.VisibilityUpdateDelegate;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.FocusHandler;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.TextArea;

public class ComponentTextArea extends FlowPanel implements SetLabels,
		ConfigurationUpdateDelegate, VisibilityUpdateDelegate {
	private final String title;
	private final Localization loc;
	private final TextArea textArea;
	private Label label;
	private org.geogebra.common.properties.PropertyView.TextArea propertyView;

	/**
	 * Creates a text area component with an optional title/label and localization support.
	 * @param loc the localization instance used for translating the title
	 * @param title the untranslated title key; may be {@code null} or empty
	 */
	public ComponentTextArea(Localization loc, String title) {
		this.loc = loc;
		this.title = title;
		this.textArea = new TextArea();
		buildGUI();
	}

	/**
	 * Creates a text area component bound to a {@link org.geogebra.common.properties.PropertyView.TextArea}.
	 * @param loc the localization instance used for translating the title
	 * @param propertyView the PropertyView backing this component
	 */
	public ComponentTextArea(Localization loc,
			org.geogebra.common.properties.PropertyView.TextArea propertyView) {
		this(loc, propertyView.getLabel());
		this.propertyView = propertyView;
		setContent(propertyView.getValue());
		propertyView.setConfigurationUpdateDelegate(this);
		propertyView.setVisibilityUpdateDelegate(this);
		addFocusHandler(event -> this.propertyView.startEditing());
		addInputHandler(() -> this.propertyView.setValue(getText()));
		addBlurHandler(event -> {
			this.propertyView.setValue(getText());
			this.propertyView.stopEditing();
		});
	}

	private void buildGUI() {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");
		if (title != null && !title.isEmpty()) {
			label = BaseWidgetFactory.INSTANCE.newSecondaryText(loc.getMenu(title),
					"label");
			optionHolder.add(label);
		}
		textArea.addStyleName("textArea");
		optionHolder.add(textArea);
		add(optionHolder);
		setStyleName("textEdit");

		textArea.addClickHandler(event -> addStyleName("active"));
		textArea.addFocusHandler(event -> addStyleName("active"));
		addBlurHandler(event -> removeStyleName("active"));
	}

	@Override
	public void setLabels() {
		if (label != null) {
			label.setText(loc.getMenu(title));
		}
	}

	/**
	 * Sets the content of the underlying text area.
	 * @param text the text to display inside the text area
	 */
	public void setContent(String text) {
		textArea.setText(text == null ? "" : text);
	}

	/**
	 * Registers a {@link BlurHandler} to be notified when the text area loses focus.
	 * @param handler the blur handler to register
	 */
	public void addBlurHandler(BlurHandler handler) {
		textArea.addBlurHandler(handler);
	}

	/**
	 * Registers a {@link FocusHandler} to be notified when the text area gains focus.
	 * @param handler the focus handler to register
	 */
	public void addFocusHandler(FocusHandler handler) {
		textArea.addFocusHandler(handler);
	}

	/**
	 * Adds a {@link KeyUpHandler} to the underlying text area.
	 * @param handler the {@link KeyUpHandler} to register
	 */
	public void addKeyUpHandler(KeyUpHandler handler) {
		textArea.addKeyUpHandler(handler);
	}

	/**
	 * Adds an input handler to the underlying text area.
	 * @param handler the action to run on text input
	 */
	public void addInputHandler(Runnable handler) {
		Dom.addEventListener(textArea.getElement(), "input", event -> handler.run());
	}

	/**
	 * Returns the current content of the text area.
	 * @return the text currently contained in the text area
	 */
	public String getText() {
		return textArea.getText();
	}

	@Override
	public void configurationUpdated() {
		setContent(propertyView.getValue());
	}

	@Override
	public void visibilityUpdated() {
		setVisible(propertyView.isVisible());
	}
}
