package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickHandler;

public class ImageInputDialogWin extends ImageInputDialog implements
        ClickHandler {
	
	;
	
	public ImageInputDialogWin(App app) {
		super((AppW) app);
	}
	
	
	@Override
	protected native void webcamClicked()/*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin("Camera", [ 1 ]);
		}
	}-*/;

	protected boolean webcamSupported() {
		return true;
	}
}
