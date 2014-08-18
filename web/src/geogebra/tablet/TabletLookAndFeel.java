package geogebra.tablet;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.html5.gui.browser.SignInButton;
import geogebra.html5.gui.browser.TabletSignInButton;
import geogebra.html5.gui.laf.GLookAndFeel;


public class TabletLookAndFeel extends GLookAndFeel {

	@Override
    public void setCloseMessage(Localization loc) {
	   //no close message on tablet
    }

	@Override
    public String getType() {
	    return "tablet";
    }

	@Override
    public boolean copyToClipboardSupported() {
	    return false;
    }

	@Override
    public SignInButton getSignInButton(App app) {
	    return new TabletSignInButton(app);
    }

}
