package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;
import geogebra.web.css.GuiResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class SplashDialog extends DialogBox {

	private static SplashDialogUiBinder uiBinder = GWT
	        .create(SplashDialogUiBinder.class);

	interface SplashDialogUiBinder extends UiBinder<PopupPanel, SplashDialog> {
	}
	
	boolean appLoaded = false;
	boolean timerEllapsed = false;
	
	private Timer t = new Timer() {
		@Override
        public void run() {
			if (appLoaded) {
				hide();
			}
			timerEllapsed = true;
		}
	};

	public SplashDialog() {
		setWidget(uiBinder.createAndBindUi(this));
		t.schedule(GeoGebraConstants.SPLASH_DIALOG_DELAY);
		center();
		show();
	}

	public void canNowHide() {
	   appLoaded = true;
	   if (timerEllapsed) {
		   hide();
	   }	    
    }

}
