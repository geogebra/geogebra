package org.geogebra.web.touch.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.full.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.PhoneGapManager;
import org.geogebra.web.touch.gui.dialog.image.ImageInputDialogT;
import org.geogebra.web.touch.gui.view.ConstructionProtocolViewT;

import com.googlecode.gwtphonegap.client.connection.Connection;

/**
 * Common for tablet app and Win Store app
 *
 */
public abstract class TouchDevice implements GDevice {

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
