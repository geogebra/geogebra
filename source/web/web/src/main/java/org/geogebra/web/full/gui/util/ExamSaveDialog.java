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

package org.geogebra.web.full.gui.util;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

public class ExamSaveDialog {

	private ComponentInputDialog examSave;

	/**
	 * Create simple save dialog for exam mode (will save the file in
	 * the temporary storage)
	 * @param app application
	 * @param onDialogClosed to be executed when the dialog is closed
	 *  using either the positive or negative button
	 */
	public ExamSaveDialog(AppW app, Runnable onDialogClosed) {
		initGui(app);
		setActionHandlers(app, onDialogClosed);
	}

	/**
	 * Show the dialog.
	 */
	public void show() {
		examSave.center();
	}

	private void initGui(AppW app) {
		DialogData data = ((DialogManagerW) app.getDialogManager()).getSaveDialogData();
		Material activeMaterial = app.getActiveMaterial();
		String initString = activeMaterial != null ? activeMaterial.getTitle() : null;
		if (StringUtil.empty(initString)) {
			initString = app.getLocalization().getMenu("Untitled");
		}

		examSave = new ComponentInputDialog(app, data, false,
				true, null, "Title", initString);
		examSave.setPreventHide(false);
	}

	private void setActionHandlers(AppW app, Runnable onDialogClosed) {
		examSave.setOnPositiveAction(() -> saveAndConfirm(app, onDialogClosed));
		examSave.setOnNegativeAction(onDialogClosed);

		examSave.addInputHandler(() -> examSave.setPosBtnDisabled(
				examSave.getInputText().length() < 1));
	}

	private void saveAndConfirm(AppW app, Runnable onDialogClosed) {
		String msg = app.getLocalization().getMenu("SavedSuccessfully");
		try {
			Material material = app.getActiveMaterial();
			if (material != null) {
				material.setTitle(examSave.getInputText());
				material.setBase64(app.getGgbApi().getBase64());
				material.setThumbnailBase64(app.getGgbApi().getThumbnailDataURL());
				GlobalScope.examController.saveTempMaterial(material);
				app.setSaved();
			} else {
				msg = app.getLocalization().getError("SaveFileFailed");
			}
		} catch (RuntimeException ex) {
			msg = app.getLocalization().getError("SaveFileFailed");
		} finally {
			if (onDialogClosed != null) {
				onDialogClosed.run();
			}
		}

		app.getToolTipManager().showBottomMessage(
				app.getLocalization().getMenu(msg), app);
	}
}
