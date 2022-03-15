package org.geogebra.web.full.gui.laf;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.browser.EmbeddedMaterialElement;
import org.geogebra.web.full.gui.browser.MaterialListElement;
import org.geogebra.web.full.gui.browser.SmartSignInController;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SignInController;

/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class SmartLookAndFeel extends GLookAndFeel {
	/**
	 * Creates smart LAF
	 */
	public SmartLookAndFeel() {
		ToolTipManagerW.setEnabled(false);
	}
	
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
    public void addWindowClosingHandler(AppW app) {
		//no close message for SMART
    }
	
	@Override
	public void removeWindowClosingHandler() {
		//no close message for SMART
	}

	@Override
    public String getType() {
	    return "smart";
    }

	@Override
	public boolean copyToClipboardSupported() {
		return false;
	}

	@Override
    public String getLoginListener() {
	    return "loginListener";
    }

	@Override
    public SignInController getSignInController(App app) {
	    return new SmartSignInController(app);
    }
	
	@Override
    public MaterialListElement getMaterialElement(Material m, AppW app, boolean isLocal) {
	    return new EmbeddedMaterialElement(m, app, isLocal);
    }

	@Override
    public boolean autosaveSupported() {
	    return false;
    }
	
	@Override
    public boolean exportSupported() {
	    return false;
    }

	@Override
	public boolean supportsGoogleDrive() {
		return false;
	}

	@Override
	public boolean printSupported() {
		return false;
	}

	@Override
	public Platform getPlatform(int dim, String appName) {
		return Platform.SMART;
	}

	@Override
	public void toggleFullscreen(boolean full) {
		// nothing to do
	}
}
