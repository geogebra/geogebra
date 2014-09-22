package geogebra.touch.gui.dialog;

import geogebra.common.main.App;
import geogebra.touch.PhoneGapManager;
import geogebra.web.gui.util.SaveDialogW;

import com.googlecode.gwtphonegap.client.connection.Connection;

public class SaveDialogT extends SaveDialogW {

	public SaveDialogT(final App app) {
	    super(app);
    }

	/**
	 * @return true if the device has no network-connection
	 */
	@Override
	protected boolean isOffline() {
		//TODO compare with ((AppW) app).getNetworkOperation().getOnline()
	    return PhoneGapManager.getPhoneGap().getConnection().getType().equals(Connection.NONE);
    }
}
