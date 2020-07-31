package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.media.MediaFactory;
import org.geogebra.web.html5.main.AppW;

/**
 * audio dialog
 */
public class AudioInputDialog extends MediaDialog {

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public AudioInputDialog(AppW app) {
		super(app, "Audio");
	}

	@Override
	public void onPositiveAction() {
		if (app.getGuiManager() != null) {
			String url = getUrlWithProtocol();
			mediaInputPanel.inputField.getTextComponent().setText(url);
			app.getSoundManager().checkURL(url, ok -> {
				if (ok) {
					addAudio();
				} else {
					mediaInputPanel.showError("InvalidInput");
				}
			});
		}
	}

	/**
	 * Adds the GeoAudio instance.
	 */
	void addAudio() {
		mediaInputPanel.resetError();
		GeoElement audio = new MediaFactory(app).addAudio(mediaInputPanel.getInput());
		hide();
		onMediaElementCreated(audio);
	}

	@Override
	public void hide() {
		super.hide();
		app.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}
}
