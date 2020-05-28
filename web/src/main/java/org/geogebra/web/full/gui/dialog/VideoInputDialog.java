package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.media.MediaFactory;
import org.geogebra.common.media.MediaURLParser;
import org.geogebra.common.media.VideoURL;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

/**
 * @author csilla
 *
 */
public class VideoInputDialog extends MediaDialog {

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public VideoInputDialog(AppWFull app) {
		super(app.getPanel(), app);
	}

	/**
	 * set button labels and dialog title
	 */
	@Override
	public void setLabels() {
		super.setLabels();
		// dialog title
		getCaption().setText(appW.getLocalization().getMenu("Video"));
	}

	@Override
	protected void processInput() {
		if (appW.getGuiManager() != null) {
			String url = getUrlWithProtocol();
			// inputField.getTextComponent().setText("https://www.youtube.com/watch?v=Kc2iLAubras");
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
		new MediaFactory(appW).addVideo(videoURL);
	}

	@Override
	public void hide() {
		super.hide();
		appW.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}
}
