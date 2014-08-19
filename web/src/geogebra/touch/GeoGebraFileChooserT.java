package geogebra.touch;

import geogebra.common.main.App;
import geogebra.web.gui.util.GeoGebraFileChooserW;
import geogebra.web.main.AppW;

import com.googlecode.gwtphonegap.client.connection.Connection;

public class GeoGebraFileChooserT extends GeoGebraFileChooserW {

	public GeoGebraFileChooserT(App app) {
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
	    ((AppW) this.app).getFileManager().saveFile(app);
	    this.hide();
    }

	/**
	 * @return true if the device has no network-connection
	 */
	private boolean tabletOrPhoneIsOffline() {
	    return PhoneGapManager.getPhoneGap().getConnection().getType().equals(Connection.NONE);
    }

}
