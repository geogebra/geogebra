package geogebra.tablet;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.html5.gui.browser.SignInButton;
import geogebra.html5.gui.browser.TabletSignInButton;
import geogebra.html5.gui.laf.GLookAndFeel;


public class TabletLookAndFeel extends GLookAndFeel {
	
	public static final int PROVIDER_PANEL_WIDTH = 0;

	@Override
    public void setCloseMessage(final Localization loc) {
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
    public SignInButton getSignInButton(final App app) {
	    return new TabletSignInButton(app);
    }
//	
//	public MaterialListElement getMaterialElement(final Material m, final AppWeb app) {
//	    return new TabletMaterialElement(m, app);
//    }

}
