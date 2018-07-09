package org.geogebra.web.html5.video;

import java.util.ArrayList;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.Persistable;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Frame;

/**
 * Represents a placeholder for videos.
 * 
 * @author Laszlo Gal
 *
 */
public abstract class VideoPlayer extends Frame implements Persistable {
	private static boolean youTubeAPI;
	/** The application */
	protected App app;

	/** Video geo to play */
	protected GeoVideo video;
	private String embedUrl = null;
	private JavaScriptObject ytPlayer;
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
		this.video = video;
		app = video.getKernel().getApplication();
		addStyleName("mowVideo");
		addStyleName("mowWidget");
		playerId = "video_player" + id;
		getElement().setId(playerId);
	}

	/**
	 * Updates the player based on video object.
	 */
	public abstract void update();

	/**
	 * 
	 * @return the associated GeoVideo object.
	 */
	public GeoVideo getVideo() {
		return video;
	}

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

	/**
	 * 
	 * @return if iframe is valid.
	 */
	protected native boolean isFrameValid() /*-{
		return this.contentWindow != null;
	}-*/;

	/**
	 * @return if the player is valid.
	 */
	public abstract boolean isValid();

	/**
	 * Play the video.
	 */
	public abstract void play();

	/**
	 * Pause the video.
	 */
	public abstract void pause();

	/**
	 * Sends the player background.
	 */
	public void sendBackground() {
		video.setBackground(true);
		update();
	}

	public String getEmbedUrl() {
		return video.getEmbeddedUrl();
	}

}

