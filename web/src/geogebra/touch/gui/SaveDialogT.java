package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.touch.PhoneGapManager;
import geogebra.touch.main.AppT;
import geogebra.web.gui.util.SaveDialogW;
import geogebra.web.main.AppW;

import com.googlecode.gwtphonegap.client.connection.Connection;

public class SaveDialogT extends SaveDialogW {

	public SaveDialogT(final App app) {
	    super(app);
    }

	@Override
	protected void onSave() {
		if(tabletOrPhoneIsOffline()) {
			saveOnDevice();
		} else {
			super.onSave();
		}
	}

	/**
	 * saves the file on the device and
	 * closes the dialog.
	 */
	private void saveOnDevice() {
	    ((AppW) app).getKernel().getConstruction().setTitle(this.title.getText());
	    ((AppT) this.app).getFileManager().saveFile(app, this.cb);
	    resetCallback();
	    this.hide();
    }

	/**
	 * @return true if the device has no network-connection
	 */
	private boolean tabletOrPhoneIsOffline() {
	    return PhoneGapManager.getPhoneGap().getConnection().getType().equals(Connection.NONE);
    }

}
