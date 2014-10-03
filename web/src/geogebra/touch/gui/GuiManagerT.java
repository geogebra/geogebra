package geogebra.touch.gui;

import geogebra.html5.main.AppW;
import geogebra.touch.PhoneGapManager;
import geogebra.touch.gui.dialog.DialogManagerT;
import geogebra.touch.main.AppT;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.browser.SignInButton;

import com.googlecode.gwtphonegap.client.connection.Connection;

public class GuiManagerT extends GuiManagerW {

	public GuiManagerT(AppW app) {
	    super(app);
    }
	
	@Override
	public boolean save() {
		if (isOnline() && !app.getLoginOperation().isLoggedIn()) {
			listenToLogin();
			uploadWaiting = true;
			((SignInButton) ((AppT) app).getLAF().getSignInButton(app)).login();
		} else {
			((DialogManagerT) app.getDialogManager()).getSaveDialog().center();
		}
		return true;
	}
	
	private boolean isOnline() {
		return !PhoneGapManager.getPhoneGap().getConnection().getType().equals(Connection.NONE);
	}
}
