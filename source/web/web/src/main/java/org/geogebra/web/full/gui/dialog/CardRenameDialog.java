package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Material rename dialog
 */
public abstract class CardRenameDialog extends ComponentDialog {
	private ComponentInputField inputField;
	private boolean inputChanged;

	/**
	 * @param app application
	 * @param data dialog translation keys
	 */
	public CardRenameDialog(AppW app, DialogData data) {
		super(app, data, false, true);
		addStyleName("materialRename");
		addStyleName("mebis");
		buildContent();
		setOnPositiveAction(this::renameCard);
	}

	/**
	 * Rename the card to the current input.
	 */
	protected abstract void renameCard(String text);

	private void renameCard() {
		renameCard(getInputText());
	}

	/**
	 * @return the trimmed text of the input field.
	 */
	protected String getInputText() {
		return inputField.getText().trim();
	}

	private void buildContent() {
		FlowPanel contentPanel = new FlowPanel();
		inputField = new ComponentInputField((AppW) app, "",
				app.getLocalization().getMenu("Rename"), "", "");
		contentPanel.add(inputField);
		initInputFieldActions();
		setPosBtnDisabled(true);
		addDialogContent(contentPanel);
	}

	protected void setText(String text) {
		inputField.setInputText(text);
	}

	private void initInputFieldActions() {
		inputField.addInputHandler(this::validate);
		Scheduler.get().scheduleDeferred(() -> inputField.getTextField()
				.getTextComponent().setFocus(true));
	}

	/**
	 * Enable or disable
	 */
	protected void validate() {
		inputChanged = inputChanged
				|| !getInputText().equals(getCardTitle());
		setPosBtnDisabled(isTextLengthInvalid() || !inputChanged);
	}

	protected boolean isTextLengthInvalid() {
		String text = getInputText();
		return StringUtil.empty(text) || text.length() > Material.MAX_TITLE_LENGTH;
	}

	protected abstract String getCardTitle();

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(() -> inputField.getTextField().setFocusAndSelectAll());
	}
}