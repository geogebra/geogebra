package org.geogebra.web.full.gui.components;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author csilla
 * 
 *         input field material design component
 *
 */
public class ComponentInputField extends FlowPanel implements SetLabels {
	private AppW appW;
	private String errorTxt;
	private String labelTxt;
	private FlowPanel contentPanel;
	private FormLabel labelText;
	private InputPanelW inputTextField;
	private Label errorLabel;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param placeholder
	 *            placeholder text (can be null)
	 * @param labelTxt
	 *            label of input field
	 * @param errorTxt
	 *            error label of input field
	 * @param defaultValue
	 *            default text of input text field
	 * @param width
	 *            of input text field
	 */
	public ComponentInputField(AppW app, String placeholder, String labelTxt,
			String errorTxt, String defaultValue, int width) {
		this.appW = app;
		this.labelTxt = labelTxt;
		this.errorTxt = errorTxt;
		buildGui(placeholder, width);
		if (defaultValue != null && !defaultValue.isEmpty()) {
			setInputText(defaultValue);
		}
	}

	private void buildGui(String placeholder, int width) {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("inputTextField");
		// input text field
		inputTextField = new InputPanelW("", appW, 1, width, false);
		inputTextField.addStyleName("textField");
		// label of text field
		labelText = new FormLabel().setFor(inputTextField.getTextComponent());
		labelText.setStyleName("inputLabel");
		// placeholder if there is any
		if (placeholder != null && !placeholder.isEmpty()) {
			inputTextField.getTextComponent().getTextBox().getElement()
				.setAttribute("placeholder",
						appW.getLocalization().getMenu(placeholder));
		}
		// build component
		contentPanel.add(labelText);
		contentPanel.add(inputTextField);
		// add error label if there is any
		if (errorTxt != null && !errorTxt.isEmpty()) {
			addErrorLabel(contentPanel);
		}
		add(contentPanel);
		setLabels();
	}

	private void addErrorLabel(FlowPanel root) {
		errorLabel = new Label();
		errorLabel.setStyleName("errorLabel");
		root.add(errorLabel);
	}

	public void setLabels() {
		labelText.setText(appW.getLocalization().getMenu(labelTxt));
		if (errorLabel != null) {
			errorLabel.setText(appW.getLocalization().getMenu(errorTxt));
		}
	}

	/**
	 * @return value of input text field
	 */
	public String getInputText() {
		return inputTextField.getText();
	}

	/**
	 * @param text
	 *            should appear in the input text field
	 */
	public void setInputText(String text) {
		inputTextField.getTextComponent().setText(text);
	}
}
