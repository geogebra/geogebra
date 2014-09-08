package geogebra.tablet;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppW;
import geogebra.tablet.gui.browser.TabletMaterialElement;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.browser.TabletSignInButton;
import geogebra.web.gui.laf.GLookAndFeel;


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

	@Override
    public MaterialListElement getMaterialElement(final Material m, final AppW app, boolean isLocal) {
	    return new TabletMaterialElement(m, app, isLocal);
    }

}
