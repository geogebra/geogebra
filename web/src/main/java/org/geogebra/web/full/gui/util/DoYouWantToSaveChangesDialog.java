package org.geogebra.web.full.gui.util;

import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.SaveController;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogUtil;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public class DoYouWantToSaveChangesDialog extends ComponentDialog implements
		SaveController.SaveListener, SaveDialogI {
	private FlowPanel contentPanel;
	private FlowPanel inputPanel;
	private InputPanelW titleField;

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param autoHide - true if dialog should be hidden on background click
	 */
	public DoYouWantToSaveChangesDialog(AppW app,
			DialogData dialogData, boolean autoHide) {
		super(app, dialogData, autoHide, true);
		addStyleName("saveDialogMow");
		buildContent();
		initActions();
		DialogUtil.hideOnLogout(app, this);
	}

	/**
	 * build dialog content
	 */
	public void buildContent() {
		contentPanel = new FlowPanel();
		inputPanel = new FlowPanel();

		inputPanel.setStyleName("mowInputPanelContent");
		inputPanel.addStyleName("emptyState");

		titleField = new InputPanelW("", app, 1, 25, false);
		FormLabel titleLbl = new FormLabel().setFor(titleField.getTextComponent());
		titleLbl.setText(app.getLocalization().getMenu("Title"));
		titleLbl.addStyleName("inputLabel");
		titleField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", app.getLocalization().getMenu("Untitled"));
		titleField.addStyleName("inputText");

		inputPanel.add(titleLbl);
		inputPanel.add(titleField);

		contentPanel.add(inputPanel);
		addDialogContent(contentPanel);
	}

	public FlowPanel getContentPanel() {
		return contentPanel;
	}

	private void initActions() {
		// set focus to input field!
		Scheduler.get().scheduleDeferred(() -> getInputField().getTextComponent().setFocus(true));
		addFocusBlurHandlers();
		addHoverHandlers();
		setOnNegativeAction(() -> app.getSaveController().cancel());
		setOnPositiveAction(() -> {
			if (app.getPlatform() == GeoGebraConstants.Platform.OFFLINE) {
				((AppW) app).getGuiManager().exportGGB(false);
			} else {
				if (!app.getLoginOperation().isLoggedIn()) {
					hide();
					((AppW) app).getGuiManager().listenToLogin();
					app.getLoginOperation().showLoginDialog();
					((AppW) app).getGuiManager().setRunAfterLogin(this::onSave);
				} else {
					onSave();
				}
			}
		});
		titleField.getTextComponent().addKeyUpHandler(event ->
				setPosBtnDisabled(titleField.getText().isEmpty()));
		Window.addCloseHandler(event -> app.getSaveController().cancel());
	}

	private void onSave() {
		setSaveType(Material.MaterialType.ggs);
		app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggsTemplate);
		app.getSaveController().saveAs(getInputField().getText(),
				getSaveVisibility(), this);
	}

	/**
	 * Sets material type to be saved.
	 *
	 * @param saveType
	 *			for the dialog.
	 */
	@Override
	public void setSaveType(Material.MaterialType saveType) {
		app.getSaveController().setSaveType(saveType);
	}

	@Override
	public void setDiscardMode() {
		// nothing to do here
		// will be removed from interface with APPS-2066
	}

	protected MaterialVisibility getSaveVisibility() {
		Material activeMaterial = ((AppW) app).getActiveMaterial();
		if (activeMaterial == null) {
			return MaterialVisibility.Private;
		}

		MaterialVisibility visibility = MaterialVisibility.value(activeMaterial.getVisibility());
		if (visibility == MaterialVisibility.Shared && !sameMaterial(activeMaterial)) {
			return MaterialVisibility.Private;
		}
		return visibility;
	}

	private boolean sameMaterial(Material material) {
		return app.getLoginOperation().owns(material)
				&& material.getTitle().equals(getInputField().getText());
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		titleField.getTextComponent().getTextBox()
				.addMouseOverHandler(event -> getInputPanel().addStyleName("hoverState"));
		titleField.getTextComponent().getTextBox()
				.addMouseOutHandler(event -> getInputPanel().removeStyleName("hoverState"));
	}

	private void addFocusBlurHandlers() {
		titleField.getTextComponent().getTextBox()
				.addFocusHandler(event -> setFocusState());
		titleField.getTextComponent().getTextBox()
				.addBlurHandler(event -> resetInputField());
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	protected void setFocusState() {
		getInputPanel().setStyleName("mowInputPanelContent");
		getInputPanel().addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	protected void resetInputField() {
		getInputPanel().removeStyleName("focusState");
		getInputPanel().addStyleName("emptyState");
	}

	/**
	 * @return panel holding input with label and error label
	 */
	public FlowPanel getInputPanel() {
		return inputPanel;
	}

	/**
	 * @return input text field
	 */
	public InputPanelW getInputField() {
		return titleField;
	}

	/**
	 * Sets initial title for the material to save.
	 */
	@Override
	public void setTitle() {
		// suggest for the user the current date as title
		String currentDate = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm").format(new Date());
		app.getSaveController().updateSaveTitle(getInputField()
						.getTextComponent(), currentDate);
		Scheduler.get().scheduleDeferred(() -> getInputField().setFocusAndSelectAll());
	}

	@Override
	public void show() {
		super.show();
		setTitle();
	}
}