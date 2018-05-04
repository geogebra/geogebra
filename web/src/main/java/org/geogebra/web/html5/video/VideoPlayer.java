package org.geogebra.web.html5.video;

import java.util.ArrayList;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.Persistable;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Frame;

/**
 * Represents a placeholder for videos.
 * 
 * @author Laszlo Gal
 *
 */
public class VideoPlayer extends Frame implements Persistable {
	private static boolean youTubeAPI;
	private GeoVideo video;
	private String embedUrl = null;
	private JavaScriptObject ytPlayer;
	private App app;
	private String playerId;
	private static ArrayList<VideoPlayer> waiting = new ArrayList<>();
	/**
	 * Constructor.
	 * 
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player frame.
	 */
	public VideoPlayer(GeoVideo video, int id) {
		super(video.getEmbeddedUrl());
		initYouTubeApi();
		this.video = video;
		addStyleName("mowVideo");
		embedUrl = video.getEmbeddedUrl();
		playerId = "video_player" + id;
		getElement().setId(playerId);
		app = video.getKernel().getApplication();
		if (youTubeAPI) {
			createPlayerDeferred();
		} else {
			waiting.add(this);
		}
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
	 * Updates the player based on video object.
	 */
	public void update() {
		Style style = getElement().getStyle();
		style.setLeft(getVideo().getAbsoluteScreenLocX(), Unit.PX);
		style.setTop(getVideo().getAbsoluteScreenLocY(), Unit.PX);
		setWidth(getVideo().getWidth() + "px");
		setHeight(getVideo().getHeight() + "px");
		if (getVideo().isBackground()) {
			addStyleName("background");
			if (!getVideo().getEmbeddedUrl().equals(embedUrl)) {
				embedUrl = getVideo().getEmbeddedUrl();
			}
		} else {
			removeStyleName("background");
		}
		video.getKernel().getApplication().getActiveEuclidianView().repaintView();

	}

	/**
	 * 
	 * @return the associated GeoVideo object.
	 */
	public GeoVideo getVideo() {
		return video;
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
	public static void initYouTubeApi() {
		if (youTubeAPI) {
			return;
		}
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.youtube());
		loadYouTubeApi();
	}

	private static void onAPIReady() {
		youTubeAPI = true;
		for (VideoPlayer player : waiting) {
			player.createPlayerDeferred();
		}
		waiting.clear();
	}

	/**
	 * 
	 * @return if YouTube API is present and ready to use.
	 */
	public static boolean hasYouTubeApi() {
		return youTubeAPI;
	}

	private static native void loadYouTubeApi() /*-{
		$wnd.youtube_api_ready = function() {
			@org.geogebra.web.html5.video.VideoPlayer::onAPIReady()();
		}

		var tag = document.createElement('script');
		tag.id = 'youtube-iframe';
		tag.src = 'https://www.youtube.com/iframe_api';
		var firstScriptTag = $doc.getElementsByTagName('script')[0];
		firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
	}-*/;

	/**
	 * Called after video specified by its id is loaded.
	 * 
	 */
	public void onReady() {
		video.setBackground(true);
		EuclidianView view = app.getActiveEuclidianView();
		Drawable d = ((Drawable) view.getDrawableFor(video));
		d.update();
		if (d.getBoundingBox().getRectangle() != null) {
			view.setBoundingBox(d.getBoundingBox());
			view.repaintView();
			app.getSelectionManager().addSelectedGeo(video);
		}
	}

	private static void onPlayerStateChange() {
		// implement later;
	}

	private native JavaScriptObject createYouTubePlayer(String youtubeId) /*-{
		var that = this;
		var ytPlayer = new $wnd.YT.Player(
				that.@org.geogebra.web.html5.video.VideoPlayer::playerId,
				{
					videoId : youtubeId,
					events : {
						'onReady' : function(event) {
							that.@org.geogebra.web.html5.video.VideoPlayer::onReady()();
						}
					}
				});
		return ytPlayer;
	}-*/;

	/**
	 * 
	 * @return if iframe is valid.
	 */
	public native boolean isValid() /*-{
		return this.contentWindow != null;
	}-*/;

	/**
	 * Play the video.
	 */
	public void play() {
		video.play();
		if (video.isPlaying()) {
			play(ytPlayer);
		}
		update();
	}

	/**
	 * Pause the video.
	 */
	public void pause() {
		video.pause();
		pause(ytPlayer);
		update();
	}

	private native void play(JavaScriptObject player) /*-{
		player.playVideo();
	}-*/;

	private native void pause(JavaScriptObject player) /*-{
		player.pauseVideo();
	}-*/;

	/**
	 * Sends the player background.
	 */
	public void sendBackground() {
		video.setBackground(true);
		update();
	}
}

