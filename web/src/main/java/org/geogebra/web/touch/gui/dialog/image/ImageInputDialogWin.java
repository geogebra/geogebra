package org.geogebra.web.touch.gui.dialog.image;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.PhoneGapManager;

import com.google.gwt.event.dom.client.ClickHandler;

public class ImageInputDialogWin extends ImageInputDialogT implements
        ClickHandler {
	
	;
	
	public ImageInputDialogWin(App app) {
		super((AppW) app);
	}
	
	native void openFromFileClicked() /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin("ImageDialog", [ 1 ]);
		}
	}-*/;
	
	@Override
	protected native void cameraClicked()/*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin("Camera", [ 1 ]);
		}
	}-*/;


	
}
