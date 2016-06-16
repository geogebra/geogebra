package org.geogebra.web.touch.gui.dialog.image;

import org.geogebra.common.main.App;

public class ImageInputDialogWin extends ImageInputDialogT {
	
	
	public ImageInputDialogWin(App app) {
		super(app);
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
