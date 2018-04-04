package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;

/**
 * 
 * GeoElement to represent a video object.
 * 
 * @author laszlo
 *
 */
public class GeoVideo extends GeoAudio {
	/**
	 * Test video URL.
	 */
	public static final String TEST_VIDEO_URL = "https://youtu.be/8MPbR6Cbwi4";
	private static final String YOUTUBE_EMBED = "https://www.youtube.com/embed/";
	private static final String YOUTUBE_PREVIEW = "https://img.youtube.com/vi/%ID%/0.jpg";
	private static final String TIME_PARAM = "t=";
	private static final String EMBED_TIME_PARAM = "start=";
	private static final int VIDEO_WIDTH = 420;
	private static final int VIDEO_HEIGHT = 345;
	private static final String JAVASCRIPT_API = "enablejsapi=1";
	private boolean changed = false;
	private String youtubeId = null;
	private String previewUrl = null;
	private Integer startTime = null;
	private MyImage preview;
	private boolean playing = false;

	/**
	 * Constructor.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoVideo(Construction c) {
		super(c);
		setWidth(VIDEO_WIDTH);
		setHeight(VIDEO_HEIGHT);
	}

	/**
	 * Constructor.
	 * 
	 * @param c
	 *            the construction.
	 * @param url
	 *            the URL of the video.
	 */
	public GeoVideo(Construction c, String url) {
		super(c, url);
		setSrc(url);
		setLabel("video");
		setWidth(VIDEO_WIDTH);
		setHeight(VIDEO_HEIGHT);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.VIDEO;
	}

	@Override
	public GeoElement copy() {
		GeoVideo ret = new GeoVideo(cons);
		ret.setSrc(getSrc());
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoVideo()) {
			return;
		}
		setSrc(((GeoVideo) geo).getSrc());
		changed = true;
	}

	@Override
	public void setSrc(String url) {
		super.setSrc(url);
		onSetSrc();
	}

	private void onSetSrc() {
		if (!hasVideoManager()) {
			return;
		}
		youtubeId = app.getVideoManager().getYouTubeId(getSrc());
		previewUrl = YOUTUBE_PREVIEW.replace("%ID%", youtubeId);
		app.getVideoManager().createPreview(this, new AsyncOperation<MyImage>() {

			@Override
			public void callback(MyImage obj) {
				preview = obj;
			}
		});
		initStartTime();
		changed = true;
	}

	private void initStartTime() {
		String url = getSrc();
		int idx = url.indexOf(TIME_PARAM);
		if (idx != -1) {
			String t = url.substring(idx + TIME_PARAM.length());
			int idx2 = t.indexOf("&");
			String time = idx2 == -1 ? t : t.substring(0, idx2);
			startTime = Integer.valueOf(time);
		} else {
			startTime = null;
		}
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		changed = true;
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		changed = true;
	}

	@Override
	public boolean isGeoAudio() {
		return false;
	}

	@Override
	public boolean isGeoVideo() {
		return true;
	}

	@Override
	public boolean isPlaying() {
		if (!hasVideoManager()) {
			return false;
		}

		return playing;
	}

	private boolean hasVideoManager() {
		return app.getVideoManager() != null;
	}

	/**
	 * 
	 * @return if any relevant property has changed.
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * 
	 * @param changed
	 *            to set
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * 
	 * @return the embedded link of the geo.
	 */
	public String getEmbeddedUrl() {
		if (youtubeId == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(YOUTUBE_EMBED);
		sb.append(youtubeId);
		sb.append("?");
		if (startTime != null) {
			sb.append(EMBED_TIME_PARAM);
			sb.append(startTime);
			sb.append("&");
		}
		sb.append(JAVASCRIPT_API);

		return sb.toString();

	}

	/**
	 * 
	 * @return the preview link of the geo.
	 */
	public String getPreviewUrl() {
		return previewUrl;

	}

	/**
	 * 
	 * @return the preview image.
	 */
	public MyImage getPreview() {
		return preview;
	}

	/**
	 * @param playing
	 *            to set.
	 */
	public void setPlaying(boolean playing) {
		this.playing = playing;
		changed = true;
	}

	@Override
	public void play() {
		setPlaying(true);
	}

	@Override
	public void pause() {
		setPlaying(false);
	}

	/**
	 * 
	 * @return the YouTube ID without any arguments.
	 */
	public String getYouTubeId() {
		return youtubeId;
	}
}
