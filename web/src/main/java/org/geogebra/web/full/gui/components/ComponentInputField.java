package org.geogebra.web.full.gui.components;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
		addFocusBlurHandlers();
		addHoverHandlers();
	}

	/**
	 * @return panel containing the whole component
	 */
	public FlowPanel getContentPanel() {
		return contentPanel;
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

	private void addFocusBlurHandlers() {
		inputTextField.getTextComponent().getTextBox()
				.addFocusHandler(new FocusHandler() {

					@Override
					public void onFocus(FocusEvent event) {
						setFocusState();
					}
				});
		inputTextField.getTextComponent().getTextBox()
				.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(BlurEvent event) {
						resetInputField();
					}
				});
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		inputTextField.getTextComponent().getTextBox()
				.addMouseOverHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						getContentPanel().addStyleName("hoverState");
					}
				});
		inputTextField.getTextComponent().getTextBox()
				.addMouseOutHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						getContentPanel().removeStyleName("hoverState");
					}
				});
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	protected void setFocusState() {
		contentPanel.setStyleName("inputTextField");
		contentPanel.addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	protected void resetInputField() {
		contentPanel.removeStyleName("focusState");
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
	 * @return text field
	 */
	public InputPanelW getTextField() {
		return inputTextField;
	}

	/**
	 * @param text
	 *            should appear in the input text field
	 */
	public void setInputText(String text) {
		inputTextField.getTextComponent().setText(text);
	}
}
