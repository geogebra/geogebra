package org.geogebra.web.full.gui.laf;

import org.geogebra.common.main.App;
import org.geogebra.web.shared.SignInButton;
import org.geogebra.web.shared.ggtapi.BASEURL;

/**
 * For offline browser
 */
public class MebisLookAndFeel extends GLookAndFeel {

	@Override
	public SignInButton getSignInButton(App app) {
		return new SignInButton(app, 0,
				BASEURL.getCallbackUrl().replace("file://", "app://"));
	}

}
