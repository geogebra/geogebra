package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.full.html5.Sandbox;
import org.geogebra.web.html5.util.PersistableFrame;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class YouTubePlayer extends VideoPlayer {

	private static final String SCRIPT_ID = "youtube-iframe";
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
		if (DOM.getElementById(SCRIPT_ID) == null) {
			loadYouTubeApi(SCRIPT_ID);
		}
	}

	@Override
	protected void createGUI() {
		frame = new PersistableFrame(video.getVideo().getEmbeddedUrl());
		frame.getElement().setAttribute("allowfullscreen", "1");
		frame.getElement().setAttribute("sandbox", Sandbox.videos());
	}

	private static native void loadYouTubeApi(String scriptId) /*-{
		var tag = document.createElement('script');
		tag.id = scriptId;
		tag.src = 'https://www.youtube.com/iframe_api';
		var firstScriptTag = $doc.getElementsByTagName('script')[0];
		firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
	}-*/;

	@Override
	public Widget asWidget() {
		return frame;
	}

	@Override
	public boolean matches(GeoVideo video2) {
		return getVideo().getEmbeddedUrl().equals(video2.getEmbeddedUrl());
	}
}