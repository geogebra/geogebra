package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

public class OverwriteDataDialog extends ComponentDialog {

	/**
	 * Overwrite data dialog
	 * @param appW - see {@link AppW}
	 * @param data - dialog data
	 */
	public OverwriteDataDialog(AppW appW, DialogData data) {
		super(appW, data, true, true);
		addStyleName("overwriteDialog");

		Label label = BaseWidgetFactory.INSTANCE.newPrimaryText(
				appW.getLocalization().getMenu("overwriteCurrentData"));
		setDialogContent(label);
	}
}
