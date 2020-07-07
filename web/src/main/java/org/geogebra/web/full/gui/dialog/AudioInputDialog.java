package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.media.MediaFactory;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

/**
 * @author csilla
 *
 */
public class AudioInputDialog extends MediaDialog {

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public AudioInputDialog(AppWFull app) {
		super(app.getPanel(), app);
	}

	/**
	 * set button labels and dialog title
	 */
	@Override
	public void setLabels() {
		super.setLabels();
		// dialog title
		getCaption().setText(appW.getLocalization().getMenu("Audio"));
	}

	@Override
	protected void processInput() {
		if (appW.getGuiManager() != null) {
			String url = getUrlWithProtocol();
			mediaInputPanel.inputField.getTextComponent().setText(url);
			app.getSoundManager().checkURL(url, new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean ok) {
					if (ok) {
						addAudio();
					} else {
						mediaInputPanel.showError("InvalidInput");
					}
				}
			});
		}
	}

	/**
	 * Adds the GeoAudio instance.
	 */
	void addAudio() {
		mediaInputPanel.resetError();
		GeoElement audio = new MediaFactory(appW).addAudio(mediaInputPanel.getInput());
		hide();
		onMediaElementCreated(audio);
	}

	@Override
	public void hide() {
		super.hide();
		appW.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}
}
