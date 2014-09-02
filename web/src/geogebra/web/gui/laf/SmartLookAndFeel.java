package geogebra.web.gui.laf;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.web.gui.browser.EmbeddedMaterialElement;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.browser.SmartSignInButton;

import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class SmartLookAndFeel extends GLookAndFeel{
	
	
	@Override
    public boolean undoRedoSupported() {
	    return false;
    }
	
	@Override
    public boolean isSmart() {
		return true;
	}
	
	@Override
    public boolean isEmbedded() {
		return true;
	}

	@Override
    public void setCloseMessage(Localization loc) {
	    //no close message for SMART
		RootLayoutPanel.get().getElement().addClassName("AppFrameParent");
    }

	
	
	@Override
    public String getType() {
	    return "smart";
    }

	@Override
    public boolean copyToClipboardSupported(){
		return false;
	}
	@Override
    public String getLoginListener() {
	    return "loginListener";
    }

	@Override
    public SignInButton getSignInButton(App app) {
	    return new SmartSignInButton(app);
    }
	
	public MaterialListElement getMaterialElement(Material m, AppWeb app) {
	    return new EmbeddedMaterialElement(m, app, false);
    }
	
	
}
