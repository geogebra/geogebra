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
		video.afterSizeSet(() -> {
			video.getLocation().setLocation(
					ev.toRealWorldCoordX((ev.getWidth() - video.getWidth()) / 2),
					ev.toRealWorldCoordY((ev.getHeight() - video.getHeight()) / 2)
			);
			video.notifyUpdate();
			ev.getEuclidianController().selectAndShowSelectionUI(video);
			app.storeUndoInfo();
		});

		video.setLabel(null);
		return video;
	}
}
