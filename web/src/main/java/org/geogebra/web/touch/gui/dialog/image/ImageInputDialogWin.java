package org.geogebra.web.touch.gui.dialog.image;

import org.geogebra.web.html5.main.AppW;

/**
 * Image picker and camera dialog for Win store
 */
public class ImageInputDialogWin extends ImageInputDialogT {
	
	/**
	 * @param app
	 *            application
	 */
	public ImageInputDialogWin(AppW app) {
		super(app);
	}
	
	@Override
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