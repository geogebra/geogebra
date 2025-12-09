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
