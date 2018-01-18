package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;

public class WebcamErrorDialog extends WebcamPermissionDialog {
	private String caption;
	private String message;

	public WebcamErrorDialog(AppW app, String caption, String message) {
		super(app);
		this.caption = caption;
		this.message = message;
	}

	/**
	 * set button labels and dialog title
	 */
	@Override
	public void setLabels() {
		Localization loc = app1.getLocalization();
		getCaption().setText(caption);
		text.setText(message);
		cancelBtn.setText(loc.getMenu("Ok"));
	}
}
