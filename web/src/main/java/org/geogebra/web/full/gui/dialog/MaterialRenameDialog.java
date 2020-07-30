package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Material rename dialog
 */
public class MaterialRenameDialog extends ComponentDialog {
	private InputPanelW inputField;
	private boolean inputChanged;
	private MaterialCardI card;

	/**
	 * @param app
	 *            app
	 * @param data
	 * 			  dialog transkeys
	 * @param card
	 *            card of file being renamed
	 */
	public MaterialRenameDialog(AppW app, DialogData data, MaterialCardI card) {
		super(app, data, false, true);
		this.card = card;
		addStyleName("materialRename");
		addStyleName("mebis");
		buildContent();
		setOnPositiveAction(() -> card.rename(inputField.getText().trim()));
	}

	private void buildContent() {
		FlowPanel contentPanel = new FlowPanel();
		inputField = new InputPanelW("", app, 1, 25, false);
		FormLabel inputLabel = new FormLabel().setFor(inputField.getTextComponent());
		inputLabel.addStyleName("inputLabel");
		contentPanel.add(inputLabel);
		contentPanel.add(inputField);
		inputField.getTextComponent().setText(card.getMaterialTitle());
		initInputFieldActions();
		setPosBtnDisabled(true);
		addDialogContent(contentPanel);
	}

	/**
	 * @return input text field of dialog
	 */
	public InputPanelW getInputField() {
		return inputField;
	}

	private void initInputFieldActions() {
		// on input change
		inputField.getTextComponent().addKeyUpHandler(
				event -> validate());
		// set focus to input field!
		Scheduler.get().scheduleDeferred(() -> getInputField().getTextComponent().setFocus(true));
		addHoverHandlers();
		addFocusHandlers();
	}

	/**
	 * Add focus / blur handlers
	 */
	private void addFocusHandlers() {
		inputField.getTextComponent().getTextBox()
				.addFocusHandler(event -> {
					getInputField().setStyleName("mowInputPanelContent");
					getInputField().addStyleName("focusState");
				});
		inputField.getTextComponent().getTextBox()
				.addBlurHandler(event -> {
					getInputField().removeStyleName("focusState");
					getInputField().addStyleName("emptyState");
				});
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		inputField.getTextComponent().getTextBox()
				.addMouseOverHandler(event -> getInputField().addStyleName("hoverState"));
		inputField.getTextComponent().getTextBox()
				.addMouseOutHandler(event -> getInputField().removeStyleName("hoverState"));
	}

	/**
	 * Enable or disable
	 */
	protected void validate() {
		inputChanged = inputChanged
				|| !inputField.getText().trim().equals(card.getMaterialTitle());
		if (StringUtil.emptyTrim(inputField.getText())
				|| inputField.getText().length() > Material.MAX_TITLE_LENGTH
				|| !inputChanged) {
			setPosBtnDisabled(true);
		} else {
			setPosBtnDisabled(false);
		}
	}
}