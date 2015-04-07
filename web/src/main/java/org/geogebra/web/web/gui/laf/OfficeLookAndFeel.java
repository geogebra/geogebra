package org.geogebra.web.web.gui.laf;

import org.geogebra.common.main.App;
import org.geogebra.web.web.gui.browser.SignInButton;


/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class OfficeLookAndFeel extends SmartLookAndFeel{
	
	@Override
    public boolean undoRedoSupported() {
	    return true;
    }
	
	@Override
    public boolean isSmart() {
		return false;
	}

	@Override
    public String getType() {
	    return "office";
    }

	
	public SignInButton getSignInButton(App app) {
	    return new SignInButton(app, 2000);
    }
}
