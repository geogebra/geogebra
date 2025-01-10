package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.web.full.html5.Sandbox;
import org.geogebra.web.html5.util.PersistableFrame;
import org.gwtproject.user.client.ui.Widget;

public class YouTubePlayer extends VideoPlayer {

	private static boolean scriptLoadingStarted;
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
		if (!scriptLoadingStarted) {
			loadYouTubeApi();
		}
	}

	@Override
	protected void createGUI() {
		frame = new PersistableFrame(video.getVideo().getEmbeddedUrl());
		frame.getElement().setAttribute("allowfullscreen", "1");
		frame.getElement().setAttribute("sandbox", Sandbox.videos());
	}

	private static void loadYouTubeApi() {
		JavaScriptInjector.loadJS("https://www.youtube.com/iframe_api", null);
		scriptLoadingStarted = true;
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