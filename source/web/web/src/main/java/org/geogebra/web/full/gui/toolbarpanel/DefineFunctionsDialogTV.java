/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.view.table.ScientificDataTableController;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.activity.GeoGebraActivity;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;

public final class DefineFunctionsDialogTV extends ComponentDialog {
	private ComponentInputField fieldF;
	private ComponentInputField fieldG;
	ScientificDataTableController controller;

	/**
	 * Creates a dialog to define functions for table values.
	 * @param app see {@link AppW}
	 * @param dialogData contains trans keys for title and buttons
	 * @param selectFirstCell selects the first cell in the table values after closing the dialog
	 */
	public DefineFunctionsDialogTV(AppW app, DialogData dialogData, Runnable selectFirstCell) {
		super(app, dialogData, false, true);

		GeoGebraActivity activity = ((AppWFull) app).getCurrentActivity();
		controller = activity.getTableController();

		buildGUI();
		addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
			selectFirstCell.run();
		});
	}

	private void buildGUI() {
		fieldF = addFunctionRow("f(x) =");
		fieldF.addEnterHandler(input -> onEnter(fieldF), false);
		fieldF.addBlurHandler(ignore -> onBlur(fieldF, false));

		fieldG = addFunctionRow("g(x) =");
		fieldG.addEnterHandler(input -> onEnter(fieldG), false);
		fieldG.addBlurHandler(ignore -> onBlur(fieldG, false));
	}

	private boolean onBlur(ComponentInputField field, boolean enter) {
		field.resetInputField();
		boolean success = controller.defineFunctions(fieldF.getText(), fieldG.getText());
		setErrorState(field, field.equals(fieldF) ? controller.hasFDefinitionErrorOccurred()
				: controller.hasGDefinitionErrorOccurred(), enter);
		return success;
	}

	private void onEnter(ComponentInputField field) {
		boolean success = onBlur(field, true);
		if (success) {
			hide();
			app.storeUndoInfo();
		}
	}

	private ComponentInputField addFunctionRow(String functionLbl) {
		ComponentInputField inputField = new ComponentInputField((AppW) app, null, functionLbl,
				null, null, null, false, true);
		addDialogContent(inputField);

		return inputField;
	}

	@Override
	public void onPositiveAction() {
		boolean success = controller.defineFunctions(fieldF.getText(), fieldG.getText());
		setErrorState(fieldF, controller.hasFDefinitionErrorOccurred(), true);
		setErrorState(fieldG, controller.hasGDefinitionErrorOccurred(), true);
		if (success) {
			hide();
			app.storeUndoInfo();
		}
	}

	@Override
	public void hide() {
		super.hide();
		app.hideKeyboard();
	}

	@Override
	public void show() {
		if (!app.isWhiteboardActive()) {
			((AppW) app).registerPopup(this);
		}
		resetFields();
		super.show();
		Scheduler.get().scheduleDeferred(() -> {
			fieldF.focusDeferred();
			updateFocusIndex(fieldF);
		});
	}

	/**
	 * reset fields from construction
	 */
	public void resetFields() {
		fieldF.setText(text(controller.getDefinitionOfF()));
		fieldG.setText(text(controller.getDefinitionOfG()));
	}

	private void setErrorState(ComponentInputField field, boolean error, boolean enter) {
		if (error) {
			field.showError(app.getLocalization().getMenu("Error.InvalidInput"));
			if (enter) {
				field.focusDeferred();
				updateFocusIndex(field);
			}
		} else {
			field.setErrorResolved();
		}
	}

	private static String text(String string) {
		return string != null ? string : "";
	}
}
