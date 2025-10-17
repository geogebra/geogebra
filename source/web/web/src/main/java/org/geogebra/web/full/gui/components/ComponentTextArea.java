package org.geogebra.web.full.gui.components;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.TextArea;

public class ComponentTextArea extends FlowPanel implements SetLabels {
	private final String title;
	private final Localization loc;
	private final TextArea textArea;
	private Label label;

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
		addBlurHandler(event -> removeStyleName("active"));
	}

	@Override
	public void setLabels() {
		label.setText(loc.getMenu(title));
	}

	/**
	 * Sets the content of the underlying text area.
	 * @param text the text to display inside the text area
	 */
	public void setContent(String text) {
		textArea.setText(text);
	}

	/**
	 * Registers a {@link BlurHandler} to be notified when the text area loses focus.
	 * @param handler the blur handler to register
	 */
	public void addBlurHandler(BlurHandler handler) {
		textArea.addBlurHandler(handler);
	}

	/**
	 * Adds a {@link KeyUpHandler} to the underlying text area.
	 * @param handler the {@link KeyUpHandler} to register
	 */
	public void addKeyUpHandler(KeyUpHandler handler) {
		textArea.addKeyUpHandler(handler);
	}

	/**
	 * Returns the current content of the text area.
	 * @return the text currently contained in the text area
	 */
	public String getText() {
		return textArea.getText();
	}
}

