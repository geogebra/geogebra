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
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.full.html5.Sandbox;
import org.geogebra.web.full.main.EmbedManagerW;
import org.geogebra.web.html5.util.PersistableFrame;
import org.gwtproject.user.client.ui.Widget;

public class YouTubePlayer extends VideoPlayer {

	private PersistableFrame frame;

	/**
	 * Constructor.
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player frame.
	 */
	YouTubePlayer(DrawVideo video, int id) {
		super(video, id);
	}

	@Override
	protected void createGUI() {
		frame = new PersistableFrame(video.getVideo().getEmbeddedUrl());
		frame.getElement().setAttribute("allowfullscreen", "1");
		frame.getElement().setAttribute("sandbox", Sandbox.videos());
		EmbedManagerW.setDefaultReferrerPolicy(frame.getElement());
	}

	@Override
	public Widget asWidget() {
		return frame;
	}

	@Override
	public boolean matches(GeoVideo video2) {
		return getVideo().getEmbeddedUrl().equals(video2.getEmbeddedUrl());
	}
}