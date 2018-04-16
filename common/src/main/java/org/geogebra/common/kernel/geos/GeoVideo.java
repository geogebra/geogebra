package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 *
 * GeoElement to represent a video object.
 *
 * @author laszlo
 *
 */
public class GeoVideo extends GeoMedia {
	private static final String WMODE_TRANSPARENT = "&wmode=transparent";

	/**
	 * Indicates video state
	 */
	public enum State {
		/** Video unselected and shows preview image */
		NONE,

		/** Video is selected and ready to play (still preview is shown) */
		READY,

		/** video is playing */
		PLAYING
	}

	/**
	 * Test video URL.
	 */
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
	private HitType lastHitType;
	private State state = State.NONE;

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
	protected void onSourceChanged() {
		if (!hasVideoManager()) {
			return;
		}
		youtubeId = app.getVideoManager().getYouTubeId(getSrc());
		previewUrl = YOUTUBE_PREVIEW.replace("%ID%", youtubeId);
		app.getVideoManager().createPreview(this, new AsyncOperation<MyImage>() {

			@Override
			public void callback(MyImage obj) {
				setPreview(obj);
				setChanged(true);
				app.getActiveEuclidianView().updateAllDrawablesForView(true);
			}
		});
		app.getVideoManager().loadGeoVideo(this);
		initStartTime();
		changed = true;
	}

	private void initStartTime() {
		String url = getSrc();
		int idx = url.indexOf(TIME_PARAM); // t=
		if (idx != -1) {
			String t = url.substring(idx + TIME_PARAM.length());
			int idx2 = t.indexOf("&");
			String time = idx2 == -1 ? t : t.substring(0, idx2);

			startTime = 0;
			int idxM = time.indexOf("m"); // minutes
			int idxS = time.indexOf("s"); // seconds
			if (idxM != -1) {
				String minutes = time.substring(0, idxM);
				startTime = Integer.parseInt(minutes) * 60;
			}
			if (idxS != -1) {
				String seconds = idxM == -1 ? time.substring(0, idxS)
						: time.substring(idxM + 1, idxS);
				startTime += Integer.parseInt(seconds);
			}
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
	public boolean isGeoVideo() {
		return true;
	}

	@Override
	public boolean isPlaying() {
		return state == State.PLAYING;
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
		sb.append(WMODE_TRANSPARENT);
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

	@Override
	public void play() {
		state = processState();
		Log.debug("PLAY state: " + state);
		changed = true;
	}

	private State processState() {
		switch (state) {
		case NONE:
			return State.READY;
		case READY:
			return State.PLAYING;
		default:
			return State.NONE;
		}
	}

	@Override
	public void pause() {
		state = State.NONE;
		changed = true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		if (getEmbeddedUrl() != null) {
			sb.append("\t<video src=\"");
			sb.append(StringUtil.encodeXML(getEmbeddedUrl()));
			sb.append("\"/>\n");
		}
	}

	/**
	 *
	 * @return the YouTube ID without any arguments.
	 */
	public String getYouTubeId() {
		return youtubeId;
	}

	/**
	 * @param type
	 *            hit type
	 */
	final public void setLastHitType(HitType type) {
		lastHitType = type;
	}

	@Override
	final public HitType getLastHitType() {
		return lastHitType;
	}

	@Override
	public int getDuration() {
		// TODO Implement this
		return 0;
	}

	@Override
	public int getCurrentTime() {
		// TODO Implement this
		return 0;
	}

	@Override
	public void setCurrentTime(int secs) {
		// TODO Implement this
	}

	/**
	 * @param preview
	 *            Video preview image to set.
	 */
	public void setPreview(MyImage preview) {
		this.preview = preview;
	}

	/**
	 * 
	 * @return if player is ready to play.
	 */
	public boolean isReady() {
		return state == State.READY;
	}

	/**
	 * Sets video playable for next click.
	 */
	public void setReady() {
		state = State.READY;
	}

	@Override
	public boolean isFixedSize() {
		return true;
	}
}
