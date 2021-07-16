package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 *
 * GeoElement to represent a video object.
 *
 * @author laszlo
 *
 */
public class GeoVideo extends GeoMedia {

	private static final int VIDEO_WIDTH = 420;
	private static final int VIDEO_HEIGHT = 345;

	public final static int VIDEO_SIZE_THRESHOLD = 100;

	private static final String WMODE_TRANSPARENT = "&wmode=transparent";

	/**
	 * Test video URL.
	 */
	private static final String YOUTUBE_EMBED = "https://www.youtube.com/embed/";
	private static final String YOUTUBE_PREVIEW = "https://beta.geogebra.org/apps/crossorigin/?url=https://img.youtube.com/vi/%ID%/0.jpg";
	private static final String MEBIS_PREVIEW = "https://beta.geogebra.org/apps/crossorigin/?url=https://sodis.de/mediathek/thumbsCache_16_9/%ID%___.jpg";

	private static final String TIME_PARAM_A = "&t=";
	private static final String TIME_PARAM_Q = "?t=";
	private static final String TIME_PARAM_S = "start=";
	private static final String JAVASCRIPT_API = "enablejsapi=1";

	private String youtubeId = null;
	private String previewUrl = null;
	private Integer startTime = null;
	private MyImage preview;
	private HitType lastHitType;
	private boolean background = true;

	private Runnable sizeSetCallback;

	/**
	 * Constructor.
	 *
	 * @param c
	 *            the construction.
	 */
	public GeoVideo(Construction c) {
		super(c);
		setSize(VIDEO_WIDTH, VIDEO_HEIGHT);
	}

	/**
	 * Constructor.
	 *
	 * @param c
	 *            the construction.
	 * @param url
	 *            the URL of the video.
	 * @param format
	 *            {@link MediaFormat}
	 */
	public GeoVideo(Construction c, String url, MediaFormat format) {
		super(c, url, format);
		setLabel("video");
		setSize(format == MediaFormat.VIDEO_YOUTUBE ? VIDEO_WIDTH : -1,
				format == MediaFormat.VIDEO_YOUTUBE ? VIDEO_HEIGHT : -1);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.VIDEO;
	}

	@Override
	public GeoElement copy() {
		GeoVideo ret = new GeoVideo(cons);
		ret.setSrc(getSrc(), getFormat());
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoVideo()) {
			return;
		}
		GeoVideo video = (GeoVideo) geo;
		setSrc(video.getSrc(), video.getFormat());
	}

	@Override
	protected void onSourceChanged() {
		if (!hasVideoManager()) {
			return;
		}
		constructIds();
		createPreview();
	}

	/**
	 * Define the identifiers and/or any URLs of the video here.
	 */
	private void constructIds() {
		if (getFormat() == MediaFormat.VIDEO_YOUTUBE) {
			youtubeId = app.getVideoManager().getYouTubeId(getSrc());
			previewUrl = YOUTUBE_PREVIEW.replace("%ID%", youtubeId);
		} else if (getFormat() == MediaFormat.VIDEO_MEBIS) {
			String id = app.getVideoManager().getMebisId(getSrc());
			previewUrl = MEBIS_PREVIEW.replace("%ID%", id);
		}
	}

	/**
	 * Creates the preview image for the video.
	 */
	private void createPreview() {
		if (getFormat() != MediaFormat.VIDEO_YOUTUBE && getFormat() != MediaFormat.VIDEO_MEBIS) {
			return;
		}
		app.getVideoManager().createPreview(this);
	}

	private void initStartTime() {
		String url = getSrc();

		int startIdx = url.contains(TIME_PARAM_A) ? url.indexOf(TIME_PARAM_A)
				: (url.contains(TIME_PARAM_Q) ? url.indexOf(TIME_PARAM_Q)
				: url.indexOf(TIME_PARAM_S));
		if (startIdx != -1) {
			String t = url.contains(TIME_PARAM_S)
					? url.substring(startIdx + TIME_PARAM_S.length())
					: url.substring(startIdx + TIME_PARAM_A.length());

			int endIdx = t.contains("&") ? t.indexOf("&")
					: (t.contains("?") ? t.indexOf("?")
					: t.indexOf("\""));

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
				startTime = Integer.parseInt(time);
			}
		} else {
			startTime = null;
		}
	}

	@Override
	public double getMinWidth() {
		return VIDEO_SIZE_THRESHOLD;
	}

	@Override
	public double getMinHeight() {
		return VIDEO_SIZE_THRESHOLD;
	}

	@Override
	public void setSize(double width, double height) {
		super.setSize(width, height);
		runSizeCallbackIfReady();
	}

	@Override
	public boolean isGeoVideo() {
		return true;
	}

	private boolean hasVideoManager() {
		return app.getVideoManager() != null;
	}

	/**
	 *
	 * @return the embedded link of the geo.
	 */
	public String getEmbeddedUrl() {
		if (getFormat() != MediaFormat.VIDEO_YOUTUBE) {
			return null;
		}
		if (youtubeId == null) {
			youtubeId = app.getVideoManager().getYouTubeId(getSrc());
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
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<video src=\"");
		if (getFormat() == MediaFormat.VIDEO_YOUTUBE) {
			sb.append(StringUtil.encodeXML(getEmbeddedUrl()));
		} else if (getSrc() != null) {
			sb.append(StringUtil.encodeXML(getSrc()));
		}
		sb.append("\"");
		if (getFormat() != null) {
			sb.append(" type=\"");
			sb.append(getFormat());
			sb.append("\"");
		}
		sb.append("/>\n");
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
	 * @return if video size is set or not.
	 */
	public boolean hasSize() {
		return getWidth() != -1 && getHeight() != -1;
	}

	/**
	 * Runs callback once after size is set
	 * 
	 * @param sizeCallback
	 *            size callback
	 */
	public void afterSizeSet(Runnable sizeCallback) {
		sizeSetCallback = sizeCallback;
		runSizeCallbackIfReady();
	}

	private void runSizeCallbackIfReady() {
		if (sizeSetCallback != null && getWidth() > 0 && getHeight() > 0) {
			sizeSetCallback.run();
			sizeSetCallback = null;
		}
	}
}
