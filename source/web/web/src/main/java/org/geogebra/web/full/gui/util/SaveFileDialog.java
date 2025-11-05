package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.SaveController;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.geogebra.web.shared.DialogUtil;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.NativeEvent;

import elemental2.core.JsDate;
import elemental2.dom.DomGlobal;

public abstract class SaveFileDialog extends ComponentDialog implements
		SaveController.SaveListener, SaveDialogI {
	protected ComponentInputField titleField;

	/**
	 * Base dialog constructor
	 * @param app see {@link AppW}
	 * @param dialogData contains trans keys for title and buttons
	 * @param autoHide true if dialog should be hidden on background click
	 */
	public SaveFileDialog(AppW app,
			DialogData dialogData, boolean autoHide) {
		super(app, dialogData, autoHide, true);
		addStyleName("saveDialog");
		buildContent();
		initActions();
		DialogUtil.hideOnLogout(app, this);
		setSaveType(app.isWhiteboardActive()
				? Material.MaterialType.ggs : Material.MaterialType.ggb);
	}

	/**
	 * build dialog content
	 */
	public void buildContent() {
		titleField = new ComponentInputField((AppW) app, "Untitled",
				app.getLocalization().getMenu("Title"), "", "");

		addDialogContent(titleField);
	}

	private void initActions() {
		// set focus to input field!
		titleField.focusDeferred();
		setOnNegativeAction(app.getSaveController()::cancel);
		Runnable afterSave = () -> app.getSaveController().runAfterSaveCallback(true);
		setOnPositiveAction(() -> {
			if (((AppW) app).getFileManager().saveCurrentLocalIfPossible(app,
					afterSave)) {
				return;
			}
			if (!((AppW) app).getFileManager().isOnlineSavingPreferred()) {
				app.getSaveController().showLocalSaveDialog(afterSave);
			} else {
				if (!app.getLoginOperation().isLoggedIn()) {
					hide();
					((AppWFull) app).getActivity().markSaveProcess(getTitleText(),
							getSaveVisibility());
					((AppW) app).getGuiManager().listenToLogin(this::onSave);
					app.getLoginOperation().showLoginDialog();
				} else {
					onSave();
				}
			}
		});
		titleField.getTextField().getTextComponent().addKeyUpHandler(event -> {
			NativeEvent nativeEvent = event.getNativeEvent();
			// we started handling Ctrl+S in graphics view but then focus moved to this dialog
			// make sure the keyup event doesn't clear selection
			if (MathFieldW.checkCode(nativeEvent, "KeyS")
				&& (nativeEvent.getCtrlKey() || nativeEvent.getMetaKey())) {
				setTitle();
			}
		});
		titleField.getTextField().addTextComponentInputListener(
				ignore -> setPosBtnDisabled(isInvalidLength(getTitleText())));
		GlobalHandlerRegistry globalHandlers = ((AppW) app).getGlobalHandlers();
		globalHandlers.addEventListener(DomGlobal.window, "unload",
				event -> app.getSaveController().cancel());
	}

	private String getTitleText() {
		return titleField.getText().trim();
	}

	private boolean isInvalidLength(String title) {
		return title.isEmpty() || title.length() > Material.MAX_TITLE_LENGTH;
	}

	private void onSave() {
		app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggsTemplate);
		app.getSaveController().saveAs(getTitleText(),
				getSaveVisibility(), this);
	}

	/**
	 * Sets material type to be saved.
	 * @param saveType for the dialog.
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
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial == null) {
			return app.isByCS() ? MaterialVisibility.Private : MaterialVisibility.Shared;
		}

		MaterialVisibility visibility = MaterialVisibility.value(activeMaterial.getVisibility());
		if (visibility == MaterialVisibility.Shared && !sameMaterial(activeMaterial)) {
			return MaterialVisibility.Private;
		}
		return visibility;
	}

	private boolean sameMaterial(Material material) {
		return app.getLoginOperation().owns(material)
				&& material.getTitle().equals(getTitleText());
	}

	/**
	 * Sets initial title for the material to save.
	 */
	public void setTitle() {
		app.getSaveController().updateSaveTitle(titleField.getTextField().getTextComponent(),
				getDefaultTitle());
		titleField.setVisible(shouldInputPanelBeVisible());
		Scheduler.get().scheduleDeferred(() -> titleField.getTextField().setFocusAndSelectAll());
	}

	private String getDefaultTitle() {
		// for Mebis users suggest the current date as title
		return app.isByCS() ? DateTimeFormat.format(new JsDate())
				: app.getLocalization().getMenu("Untitled");
	}

	@Override
	public void show() {
		super.show();
		setTitle();
	}

	protected abstract boolean shouldInputPanelBeVisible();
}