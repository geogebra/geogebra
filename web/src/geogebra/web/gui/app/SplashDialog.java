package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;
import geogebra.html5.css.GuiResources;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

public class SplashDialog extends DialogBox {

	boolean appLoaded = false;
	boolean timerEllapsed = false;
	Element existingSplash = null;
	
	private Timer t = new Timer() {
		@Override
        public void run() {
			if (appLoaded) {
				hideSplash();
			}
			timerEllapsed = true;
		}
	};

	public SplashDialog() {
		//do we already has splash?
		existingSplash = DOM.getElementById("ggbsplash");
		t.schedule(GeoGebraConstants.SPLASH_DIALOG_DELAY);
		if (existingSplash == null) {
			setWidget(new HTML(GuiResources.INSTANCE.ggb4Splash().getText()));
			center();
			show();
		}
	}

	public void canNowHide() {
	   appLoaded = true;
	   if (timerEllapsed) {
		   hideSplash();
	   }	    
    }

	private void hideSplash() {
	    if (existingSplash != null) {
			   existingSplash.removeFromParent();
		   } else {
			   hide();
		   }
    }

}
