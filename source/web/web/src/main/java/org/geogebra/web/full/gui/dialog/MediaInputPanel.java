package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class MediaInputPanel extends FlowPanel implements ProcessInput {
	private final AppW app;
	private final ComponentDialog parentDialog;
	private final boolean required;
	protected ComponentInputField inputField;
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
	public MediaInputPanel(AppW app, ComponentDialog parentDialog,
			String labelTransKey, boolean required) {
		this.app = app;
		this.parentDialog = parentDialog;
		this.required = required;

		inputField = new ComponentInputField(app, "", app.getLocalization().getMenu(labelTransKey),
				"", "");
		inputField.addInputHandler(this);
		add(inputField);
	}

	/**
	 * Set focus the text field of the input panel
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(() -> inputField.focusDeferred());
	}

	/**
	 * Add placeholder to the text field of the input panel
	 * @param placeholder localized placeholder string
	 */
	public void addPlaceholder(String placeholder) {
		inputField.getTextField().getTextComponent().getTextBox().getElement()
				.setAttribute("placeholder", placeholder);
	}

	/**
	 * Set input text and update error state.
	 * @param text
	 *         input text
	 */
	public void setText(String text) {
		inputField.setInputText(text);
		resetError();
	}

	/**
	 * Add info label to the input panel
	 */
	public void addInfoLabel() {
		infoLabel = BaseWidgetFactory.INSTANCE.newSecondaryText("", "msgLabel");
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
		inputField.setError(app.getLocalization().getMenu("Error") + ": "
						+ app.getLocalization().getError(msg));
		parentDialog.setPosBtnDisabled(true);
	}

	/**
	 * @param info permanent information message
	 */
	public void showInfo(String info) {
		if (infoLabel != null) {
			infoLabel.setText(info);
		}
	}

	/**
	 * Remove error state from input panel
	 */
	public void resetError() {
		inputField.setErrorResolved();
		if (required) {
			parentDialog.setPosBtnDisabled(isInputEmpty());
		}
	}

	private boolean isInputEmpty() {
		return StringUtil.emptyTrim(inputField.getText());
	}

	@Override
	public void onInput() {
		resetError();
	}
}