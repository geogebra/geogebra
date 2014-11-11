package geogebra.touch.gui;

import geogebra.html5.main.AppW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.main.GDevice;

public class GuiManagerT extends GuiManagerW {

	public GuiManagerT(AppW app, GDevice device) {
	    super(app, device);
    }
	
	@Override
	public boolean save() {
		if (!device.isOffline((AppW) app) && !app.getLoginOperation().isLoggedIn()) {
			listenToLogin();
			uploadWaiting = true;
			((SignInButton) ((AppW) app).getLAF().getSignInButton(app)).login();
		} else {
			((DialogManagerW) app.getDialogManager()).getSaveDialog().center();
		}
		return true;
	}
	
	
}
