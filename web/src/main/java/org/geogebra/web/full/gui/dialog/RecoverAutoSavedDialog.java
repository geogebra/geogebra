package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

/**
 *  A dialog to ask the user to recover the autoSaved file.
 */
public class RecoverAutoSavedDialog extends ComponentDialog {

	/**
	 * only used from {@link AppWFull} with menu
	 * @param app {@link AppW}
	 * @param data dialog transkeys
	 */
	public RecoverAutoSavedDialog(AppWFull app, DialogData data) {
		super(app, data, false, true);
		addStyleName("RecoverAutoSavedDialog");
		buildContent();
	}

	private void buildContent() {
		Label infoText = BaseWidgetFactory.INSTANCE.newSecondaryText(
				app.getLocalization().getMenu("UnsavedChangesFound"), "infoText");
		addDialogContent(infoText);
	}
}