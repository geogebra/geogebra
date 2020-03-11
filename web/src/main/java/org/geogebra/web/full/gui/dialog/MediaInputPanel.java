package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
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

public class MediaInputPanel extends FlowPanel implements ProcessInput {

	private AppW app;
	private OptionDialog parentDialog;
	private boolean required;

	protected InputPanelW inputField;
	private Label errorLabel;
	private Label infoLabel;

	/**
	 * @param app
	 *         application
	 * @param parentDialog
	 *         parent dialog
	 * @param labelTransKey
	 *         label translation key
	 * @param required
	 *         whether nonempty string is expected
	 */
	public MediaInputPanel(AppW app, OptionDialog parentDialog,
						   String labelTransKey, boolean required) {
		this.app = app;
		this.parentDialog = parentDialog;
		this.required = required;

		setStyleName("mowInputPanelContent");
		addStyleName("emptyState");

		inputField = new InputPanelW("", app, 1, 25, false);

		FormLabel inputLabel = new FormLabel().setFor(inputField.getTextComponent());
		inputLabel.setText(app.getLocalization().getMenu(labelTransKey));
		inputLabel.addStyleName("inputLabel");
		inputField.addStyleName("inputText");

		errorLabel = new Label();
		errorLabel.addStyleName("msgLabel errorLabel");

		add(inputLabel);
		add(inputField);
		add(errorLabel);

		addHoverHandlers();
		addFocusBlurHandlers();
		addInputHandler();
	}

	/**
	 * Set focus the text field of the input panel
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				inputField.getTextComponent().setFocus(true);
				inputField.getTextComponent().selectAll();
			}
		});
	}

	/**
	 * Add placeholder to the text field of the input panel
	 * @param placeholder localized placeholder string
	 */
	public void addPlaceholder(String placeholder) {
		inputField.getTextComponent().getTextBox().getElement()
				.setAttribute("placeholder", placeholder);
	}

	/**
	 * Set input text and update error state.
	 * @param text
	 *         input text
	 */
	public void setText(String text) {
		inputField.getTextComponent().setText(text);
		resetError();
	}

	/**
	 * Add info label to the input panel
	 */
	public void addInfoLabel() {
		infoLabel = new Label();
		infoLabel.addStyleName("msgLabel");

		add(infoLabel);
	}

	/**
	 * @return trimmed input text
	 */
	public String getInput() {
		return inputField.getText().trim();
	}

	/**
	 * Set the input panel to the error state
	 * @param msg error message to show
	 */
	public void showError(String msg) {
		setStyleName("mowInputPanelContent");
		addStyleName("errorState");
		errorLabel.setText(app.getLocalization().getMenu("Error") + ": "
				+ app.getLocalization().getError(msg));
		parentDialog.setPrimaryButtonEnabled(false);
	}

	/**
	 * @param info permanent information message
	 */
	public void showInfo(String info) {
		infoLabel.setText(info);
	}

	/**
	 * Remove error state from input panel
	 */
	public void resetError() {
		setStyleName("mowInputPanelContent");
		addStyleName("emptyState");
		removeStyleName("errorState");
		if (required) {
			parentDialog.setPrimaryButtonEnabled(!isInputEmpty());
		}
	}

	private boolean isInputEmpty() {
		return StringUtil.emptyTrim(inputField.getText());
	}

	@Override
	public void onInput() {
		resetError();
		addStyleName("focusState");
		removeStyleName("emptyState");
	}

	@Override
	public void processInput() {
		parentDialog.processInput();
	}

	/**
	 * Add handler for input event
	 */
	private void addInputHandler() {
		new MediaInputKeyHandler(this).attachTo(inputField.getTextComponent());
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		inputField.getTextComponent().getTextBox().addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				MediaInputPanel.this.addStyleName("hoverState");
			}
		});

		inputField.getTextComponent().getTextBox().addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				MediaInputPanel.this.removeStyleName("hoverState");
			}
		});
	}

	private void addFocusBlurHandlers() {
		inputField.getTextComponent().getTextBox().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				setFocusState();
			}
		});

		inputField.getTextComponent().getTextBox().addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				resetInputField();
			}
		});
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	private void setFocusState() {
		setStyleName("mowInputPanelContent");
		addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	private void resetInputField() {
		removeStyleName("focusState");
		addStyleName("emptyState");
	}
}
