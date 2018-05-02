package org.geogebra.web.html5.video;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.html5.gui.Persistable;

import com.google.gwt.core.client.JavaScriptObject;
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
	private GeoVideo video;
	private String embedUrl = null;
	private JavaScriptObject ytPlayer;
	/**
	 * Constructor.
	 * 
	 * @param video
	 *            the video object.
	 */
	public VideoPlayer(GeoVideo video) {
		super(video.getEmbeddedUrl());
		this.video = video;
		addStyleName("mowVideo");
		getElement().setId(video.getYouTubeId());
		embedUrl = video.getEmbeddedUrl();
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
}

