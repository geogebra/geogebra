package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.Label;

/**
 * dialog to ask user for webcam permission and show error messages
 */
public class WebcamPermissionDialog extends ComponentDialog {

	/**
	 * @param app
	 *            application
	 * @param data
	 * 			  dialog transkeys
	 * @param msgTranskey
	 * 			  message displayed in the dialog
	 */
	public WebcamPermissionDialog(AppW app, DialogData data,
			String msgTranskey) {
		super(app, data, false, false);
		addStyleName("mowPermissionDialog");
		String localizedMsg = app.getLocalization().getMenu(msgTranskey);
		if (localizedMsg.length() < 80) {
			addStyleName("narrowDialog");
		}
		buildContent(localizedMsg);
		setOnNegativeAction(() -> app.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR));
	}

	private void buildContent(String localizedMsg) {
		Label message = new Label(localizedMsg);
		addDialogContent(message);
	}
}