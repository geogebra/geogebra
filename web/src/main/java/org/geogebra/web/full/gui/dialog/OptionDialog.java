package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog with options to cancel and do an action.
 */
public abstract class OptionDialog extends DialogBoxW
		implements FastClickHandler {
	private StandardButton insertBtn;
	private StandardButton cancelBtn;
	private FlowPanel buttonPanel;

	/**
	 * @param root
	 *            panel for positioning
	 * @param app
	 *            application
	 */
	public OptionDialog(Panel root, App app) {
		super(root, app);
		initButtonPanel();
		addStyleName("optionDialog");
	}

	/**
	 * @param root
	 *            panel for positioning
	 * @param app
	 *            application
	 * @param modal
	 *            whether the dialog is modal
	 */
	public OptionDialog(Panel root, App app, boolean modal) {
		super(false, modal, null, root, app);
		initButtonPanel();
		addStyleName("optionDialog");
	}

	private void initButtonPanel() {
		insertBtn = new StandardButton("", app);
		insertBtn.addStyleName("insertBtn");
		insertBtn.setEnabled(false);
		cancelBtn = new StandardButton("", app);
		cancelBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(cancelBtn);
		buttonPanel.add(insertBtn);
		insertBtn.addFastClickHandler(this);
		cancelBtn.addFastClickHandler(this);
	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == insertBtn) {
			processInput();
		}
	}

	/**
	 * Handle the input after enter was pressed / primary button clicked.
	 */
	protected abstract void processInput();

	/**
	 * @param key
	 *            primary button name key
	 */
	protected void updateButtonLabels(String key) {
		insertBtn.setText(app.getLocalization().getMenu(key)); // insert
		cancelBtn.setText(app.getLocalization().getMenu("Cancel")); // cancel
	}

	/**
	 * @param enable
	 *            whether to enable the primary button ("OK" / "Insert")
	 */
	protected void setPrimaryButtonEnabled(boolean enable) {
		insertBtn.setEnabled(enable);
	}

	/**
	 * @return panel with button
	 */
	protected Panel getButtonPanel() {
		return buttonPanel;
	}

	/**
	 * Focus OK / insert button
	 */
	protected void focusPrimaryButton() {
		insertBtn.getElement().focus();
	}

	/**
	 * give focus to input field
	 * 
	 * @param inputField
	 *            input field
	 */
	protected static void focusDeferred(final ComponentInputField inputField) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				inputField.getTextField().getTextComponent().setFocus(true);
			}
		});
	}

}
