package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;

public class SplashDialog extends DialogBox {

	private static SplashDialogUiBinder uiBinder = GWT
	        .create(SplashDialogUiBinder.class);

	interface SplashDialogUiBinder extends UiBinder<PopupPanel, SplashDialog> {
	}
	
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
			setWidget(uiBinder.createAndBindUi(this));
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
