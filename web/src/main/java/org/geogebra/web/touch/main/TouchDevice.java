package org.geogebra.web.touch.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.PhoneGapManager;
import org.geogebra.web.touch.gui.dialog.image.ImageInputDialogT;
import org.geogebra.web.touch.gui.view.ConstructionProtocolViewT;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.web.main.GDevice;

import com.google.gwt.user.client.Window;
import com.googlecode.gwtphonegap.client.connection.Connection;

/**
 * Common for tablet app and Win Store app
 *
 */
public abstract class TouchDevice implements GDevice {

	/**
	 * @param base64
	 *            base64
	 * @param title
	 *            construction title
	 */
	protected native void nativeShare(String base64, String title)/*-{
		
	}-*/;

	@Override
	public void setMinWidth(GeoGebraFrameBoth frame) {
		frame.syncPanelSizes();
		frame.setStyleName("minWidth", Window.getClientWidth() <= 760);
	}

	@Override
	public boolean isOffline(AppW app) {
		return PhoneGapManager.getPhoneGap().getConnection().getType()
		        .equals(Connection.NONE);
	}

	@Override
	public UploadImageDialog getImageInputDialog(AppW app) {
		return new ImageInputDialogT(app);
	}

	@Override
	public ConstructionProtocolView getConstructionProtocolView(AppW app) {
		return new ConstructionProtocolViewT(app);
	}
}
