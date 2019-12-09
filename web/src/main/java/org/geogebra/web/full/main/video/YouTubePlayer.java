package org.geogebra.web.full.main.video;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.html5.util.PersistableFrame;

import com.google.gwt.user.client.ui.Widget;

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
	YouTubePlayer(GeoVideo video, int id) {
		super(video, id);
	}

	@Override
	protected void initPlayerAPI() {
		loadYouTubeApi();
	}

	@Override
	protected void createGUI() {
		frame = new PersistableFrame(video.getEmbeddedUrl());
		frame.getElement().setAttribute("allowfullscreen", "1");
	}

	private static native void loadYouTubeApi() /*-{
		var tag = document.createElement('script');
		tag.id = 'youtube-iframe';
		tag.src = 'https://www.youtube.com/iframe_api';
		var firstScriptTag = $doc.getElementsByTagName('script')[0];
		firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
	}-*/;

	@Override
	public boolean isValid() {
		return isFrameValid();
	}

	@Override
	public Widget asWidget() {
		return frame;
	}

	@Override
	public boolean matches(GeoVideo video2) {
		return video2.getEmbeddedUrl().equals(video2.getEmbeddedUrl());
	}
}

