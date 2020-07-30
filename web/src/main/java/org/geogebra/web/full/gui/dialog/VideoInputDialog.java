package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.media.MediaFactory;
import org.geogebra.common.media.MediaURLParser;
import org.geogebra.common.media.VideoURL;
import org.geogebra.web.html5.main.AppW;

/** video dialog
 */
public class VideoInputDialog extends MediaDialog {

	/**
	 * @param app
	 *            see {@link AppW}
	 */

	public VideoInputDialog(AppW app) {
		super(app, "Video");
	}

	@Override
	public void onPositiveAction() {
		if (app.getGuiManager() != null) {
			String url = getUrlWithProtocol();
			mediaInputPanel.inputField.getTextComponent().setText(url);
			VideoURL videoURL = MediaURLParser.checkVideo(url);
			if (videoURL.isValid()) {
				addVideo(videoURL);
			} else {
				mediaInputPanel.showError("InvalidInput");
			}
		}
	}

	/**
	 * Adds the proper GeoVideo instance.
	 * 
	 * @param videoURL
	 *            the validated URL of the video.
	 */
	private void addVideo(VideoURL videoURL) {
		mediaInputPanel.resetError();
		hide();
		new MediaFactory(app).addVideo(videoURL);
	}

	@Override
	public void hide() {
		super.hide();
		app.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}
}