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
public class GeoVideo extends GeoMedia implements GeoWidget {
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
	private static final String YOUTUBE_PREVIEW = "https://dev.geogebra.org/crossorigin/?url=https://img.youtube.com/vi/%ID%/0.jpg";

	private static final String TIME_PARAM_A = "&t=";
	private static final String TIME_PARAM_Q = "?t=";
	private static final String TIME_PARAM_S = "start=";
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
	private boolean background = true;

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
		setSrc(url, false);
		youtubeId = app.getVideoManager().getYouTubeId(getSrc());
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
		changed = true;
	}

	/**
	 * Refresh the video 
	 */
	public void refresh() {
		if (!changed) {
			return;
		}
		changed = true;
		app.getVideoManager().updatePlayer(this);
		changed = false;
	}

	private void initStartTime() {
		String url = getSrc();

		int startIdx = url.indexOf(TIME_PARAM_A) != -1
				? url.indexOf(TIME_PARAM_A)
				: (url.indexOf(TIME_PARAM_Q) != -1 ? url.indexOf(TIME_PARAM_Q)
						: (url.indexOf(TIME_PARAM_S) != -1
								? url.indexOf(TIME_PARAM_S) : -1));
		if (startIdx != -1) {
			String t = url.indexOf(TIME_PARAM_S) != -1
					? url.substring(startIdx + TIME_PARAM_S.length())
					: url.substring(startIdx + TIME_PARAM_A.length());

			int endIdx = t.indexOf("&") != -1 ? t.indexOf("&")
					: (t.indexOf("?") != -1 ? t.indexOf("?")
							: (t.indexOf("\"") != -1 ? t.indexOf("\"") : -1));

			String time = endIdx == -1 ? t : t.substring(0, endIdx);

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
			if (idxM == -1 && idxS == -1) {
				String seconds = time;
				startTime = Integer.parseInt(seconds);
			}
		} else {
			startTime = null;
		}
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		setReady();
		changed = true;
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		setReady();
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

		initStartTime();
		StringBuilder sb = new StringBuilder();
		sb.append(YOUTUBE_EMBED);
		sb.append(youtubeId);
		sb.append("?");
		if (startTime != null) {
			sb.append(TIME_PARAM_S);
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
		setBackground(false);
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

	/**
	 * Resets state to none.
	 */
	public void resetState() {
		state = State.NONE;
	}

	@Override
	public boolean isFixedSize() {
		return true;
	}

	/**
	 * 
	 * @return if video is in background.
	 */
	public boolean isBackground() {
		return background;
	}

	/**
	 * Sets the video to the background, thus
	 * you can draw on it.
	 * 
	 * @param background
	 *            to set;
	 */
	public void setBackground(boolean background) {
		this.background = background;
	}

	/**
	 * @return if video is online.
	 */
	public boolean isOnline() {
		if (!hasVideoManager()) {
			return false;
		}
		return app.getVideoManager().isOnline(this);
	}

	@Override
	public void remove() {
		if (!hasVideoManager()) {
			return;
		}
		app.getVideoManager().removePlayer(this);
		super.remove();
	}
}
