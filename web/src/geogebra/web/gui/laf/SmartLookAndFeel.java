package geogebra.web.gui.laf;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.euclidian.EuclidianControllerW;
import geogebra.html5.main.AppW;
import geogebra.web.euclidian.SmartTouchHandler;
import geogebra.web.gui.browser.EmbeddedMaterialElement;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.browser.SmartSignInButton;

import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Widget;

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
	
	@Override
    public MaterialListElement getMaterialElement(Material m, AppW app, boolean isLocal) {
	    return new EmbeddedMaterialElement(m, app, isLocal);
    }
	
	@Override
    public boolean registerHandlers(Widget evPanel, EuclidianControllerW euclidiancontroller) {
		SmartTouchHandler sh = new SmartTouchHandler(euclidiancontroller);
		evPanel.addDomHandler(sh, TouchStartEvent.getType());
		evPanel.addDomHandler(sh, TouchEndEvent.getType());
		evPanel.addDomHandler(sh, TouchMoveEvent.getType());
		evPanel.addDomHandler(sh, TouchCancelEvent.getType());
		return true;
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
}
