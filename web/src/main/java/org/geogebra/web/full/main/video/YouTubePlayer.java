package org.geogebra.web.full.main.video;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.util.PersistableFrame;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a placeholder for YouTube videos.
 *
 * @author Laszlo Gal
 *
 */
public class YouTubePlayer extends VideoPlayer {

	private static boolean youTubeAPI;
	private static ArrayList<YouTubePlayer> waiting = new ArrayList<>();

	private JavaScriptObject ytPlayer;
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
		Log.debug("video player has been created");
	}

	@Override
	protected void initPlayerAPI() {
		initYouTubeApi();
		if (youTubeAPI) {
			createPlayerDeferred();
		} else {
			waiting.add(this);
		}
	}

	@Override
	protected void createGUI() {
		frame = new PersistableFrame(video.getEmbeddedUrl());
		frame.getElement().setAttribute("allowfullscreen", "1");
	}

	private void createPlayerDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				setYouTubePlayer(createYouTubePlayer(video.getYouTubeId()));
			}
		});
	}

	/**
	 *
	 * @return the JS YouTube player itself.
	 */
	public JavaScriptObject getYouTubePlayer() {
		return ytPlayer;
	}

	/**
	 *
	 * sets the JS YouTube player.
	 *
	 * @param ytPlayer
	 *            to set.
	 */
	public void setYouTubePlayer(JavaScriptObject ytPlayer) {
		this.ytPlayer = ytPlayer;
	}

	/**
	 * Initializes YouTube API.
	 */
	private static void initYouTubeApi() {
		if (youTubeAPI) {
			return;
		}
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.youtube());
		loadYouTubeApi();
	}

	@ExternalAccess
	private static void onAPIReady() {
		youTubeAPI = true;
		for (YouTubePlayer player : waiting) {
			player.createPlayerDeferred();
		}
		waiting.clear();
	}

	private static native void loadYouTubeApi() /*-{
		$wnd.youtube_api_ready = function() {
			@org.geogebra.web.full.main.video.YouTubePlayer::onAPIReady()();
		};

		var tag = document.createElement('script');
		tag.id = 'youtube-iframe';
		tag.src = 'https://www.youtube.com/iframe_api';
		var firstScriptTag = $doc.getElementsByTagName('script')[0];
		firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
	}-*/;

	/**
	 * @param youtubeId
	 *            Youtube ID
	 * @return native player
	 */
	protected native JavaScriptObject createYouTubePlayer(
			String youtubeId) /*-{
		var that = this;
        return new $wnd.YT.Player(
				that.@org.geogebra.web.full.main.video.AbstractVideoPlayer::playerId,
				{
					videoId : youtubeId
				});
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

