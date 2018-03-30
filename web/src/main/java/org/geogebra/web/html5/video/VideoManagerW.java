package org.geogebra.web.html5.video;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.sound.VideoManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.MyImageW;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Class for managing audio content.
 * 
 * @author laszlo
 *
 */
public class VideoManagerW implements VideoManager {
	private AsyncOperation<Boolean> urlCallback = null;
	/**
	 * Head of a regular YouTube URL.
	 */
	public static final String YOUTUBE = "https://www.youtube.com/watch?v=";

	/**
	 * Head of a short form of YouTube URL.
	 */
	public static final String YOUTUBE_SHORT = "https://youtu.be/";

	/**
	 * Head of the preview image of YouTube URL.
	 */
	public static final String YOUTUBE_PREV = "https://img.youtube.com/vi/%ID%/0.jpg";

	/**
	 * Head of an embedding YouTube URL.
	 */
	public static final String YOUTUBE_EMBED = "https://www.youtube.com/embed/";

	@Override
	public void loadGeoVideo(GeoVideo geo) {
		// TODO implement this
	}

	@Override
	public int getDuration(String url) {
		// TODO implement this
		return 0;
	}

	@Override
	public int getCurrentTime(String url) {
		// TODO implement this
		return 0;
	}

	@Override
	public void checkURL(String url, AsyncOperation<Boolean> callback) {
		urlCallback = callback;
		checkVideo(url);
	}

	private void checkVideo(String url) {
		if (getYouTubeId(url) == null) {
			onUrlError();
		} else {
			onUrlOK();
		}
	}

	private void onUrlError() {
		if (urlCallback != null) {
			urlCallback.callback(Boolean.FALSE);
		}
	}

	private void onUrlOK() {
		if (urlCallback != null) {
			urlCallback.callback(Boolean.TRUE);
		}
	}

	@Override
	public void play(GeoVideo geo) {
		// TODO implement this
	}

	@Override
	public void pause(GeoVideo geo) {
		// TODO implement this
	}

	@Override
	public boolean isPlaying(GeoVideo geo) {
		// TODO implement this
		return false;
	}

	@Override
	public String getYouTubeId(String url) {
		String id = null;
		if (url.startsWith(YOUTUBE)) {
			id = url.replace(YOUTUBE, "");
		} else if (url.startsWith(YOUTUBE_SHORT)) {
			id = url.replace(YOUTUBE_SHORT, "");
		} else if (url.startsWith(YOUTUBE_EMBED)) {
			id = url.replace(YOUTUBE_EMBED, "");
		}

		if (id != null) {
			int idx = id.indexOf("&");
			if (idx != -1) {
				return id.substring(0, idx);
			}
		}
		return id;
	}

	@Override
	public void createPreview(GeoVideo geo, final AsyncOperation<MyImage> cb) {

		final Image img = new Image(geo.getPreviewUrl());
		img.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				final MyImage prev = new MyImageW(ImageElement.as(img.getElement()), false);
				cb.callback(prev);
				RootPanel.get().remove(img);
			}
		});
		RootPanel.get().add(img);
	}
}
