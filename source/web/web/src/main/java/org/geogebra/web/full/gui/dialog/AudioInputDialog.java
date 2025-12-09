/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
			mediaInputPanel.inputField.setInputText(url);
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
