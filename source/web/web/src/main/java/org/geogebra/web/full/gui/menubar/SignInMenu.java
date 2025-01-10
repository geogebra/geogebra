package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Sign in menu
 */
public class SignInMenu extends Submenu {

	/**
	 * @param app
	 *            application
	 */
	public SignInMenu(AppW app) {
		super("signin", app);
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.signin_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "SignIn";
	}

	@Override
	public void handleHeaderClick() {
		if (getApp().getNetworkOperation().isOnline()
				&& !getApp().getLoginOperation().isLoggedIn()) {
			getApp().getLoginOperation().showLoginDialog();
		}
	}

}
