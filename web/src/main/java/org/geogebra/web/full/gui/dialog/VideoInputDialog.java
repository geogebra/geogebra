package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.media.VideoURL;
import org.geogebra.common.util.AsyncOperation;
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
	public VideoInputDialog(AppW app) {
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
			inputField.getTextComponent().setText(url);
			app.getVideoManager().checkURL(url, new AsyncOperation<VideoURL>() {

				@Override
				public void callback(VideoURL videoURL) {
					if (videoURL.isValid()) {
						addVideo(videoURL);
					} else {
						showError("error");
					}
				}
			});
		}
	}

	/**
	 * Adds the proper GeoVideo instance.
	 * 
	 * @param videoURL
	 *            the validated URL of the video.
	 */
	void addVideo(VideoURL videoURL) {
		resetError();
		appW.getGuiManager().addVideo(videoURL);
		hide();
	}
}
