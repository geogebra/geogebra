package org.geogebra.web.touch.gui.dialog.image;

import org.geogebra.common.main.App;

/**
 * Image picker and camera dialog for Win store
 * 
 * @author Zbynek
 *
 */
public class ImageInputDialogWin extends ImageInputDialogT {
	
	/**
	 * @param app
	 *            application
	 */
	public ImageInputDialogWin(App app) {
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
