package org.geogebra.common.media;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;

/**
 * Creates construction elements for audio and video
 */
public class MediaFactory {

	private App app;

	/**
	 * @param app
	 *            application
	 */
	public MediaFactory(App app) {
		this.app = app;
	}

	/**
	 * Add audio to construction
	 *
	 * @param url
	 *            audio URL
	 * @return the created audio geoElement
	 */
	public GeoElement addAudio(String url) {
		EuclidianView ev = app.getActiveEuclidianView();
		GeoAudio audio = new GeoAudio(app.getKernel().getConstruction(), url);
		audio.getLocation().setLocation(
				ev.toRealWorldCoordX((ev.getWidth() - audio.getWidth()) / 2),
				ev.toRealWorldCoordY((ev.getHeight() - audio.getHeight()) / 2)
		);
		audio.setLabel(null);
		app.storeUndoInfo();
		app.getActiveEuclidianView().repaint();
		return audio;
	}

	/**
	 * Create video and add it to construction.
	 *
	 * @param videoUrl
	 *            video URL
	 * @return the created video geoElement
	 */
	public GeoElement addVideo(VideoURL videoUrl) {
		final EuclidianView ev = app.getActiveEuclidianView();
		final GeoVideo video = app.getVideoManager().createVideo(
				app.getKernel().getConstruction(),
				videoUrl);
		video.setBackground(true);
		video.afterSizeSet(new Runnable() {

			@Override
			public void run() {
				video.getLocation().setLocation(
						ev.toRealWorldCoordX((ev.getWidth() - video.getWidth()) / 2),
						ev.toRealWorldCoordY((ev.getHeight() - video.getHeight()) / 2)
				);
				video.notifyUpdate();
				ev.getEuclidianController().selectAndShowSelectionUI(video);
				app.storeUndoInfo();
			}
		});

		video.setLabel(null);
		return video;
	}
}
