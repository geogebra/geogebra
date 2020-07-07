package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;

/**
 * Represents a placeholder for videos.
 *
 * @author Laszlo Gal
 *
 */
public abstract class VideoPlayer extends AbstractVideoPlayer {

	/**
	 * Constructor. *
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player frame.
	 */
	VideoPlayer(DrawVideo video, int id) {
		super(video);
		createGUI();
		stylePlayer(id);
	}

	/**
	 * Build the GUI here
	 */
	protected abstract void createGUI();

	@Override
	boolean isOffline() {
		return false;
	}
}
