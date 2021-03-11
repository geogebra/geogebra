package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * Class for representing playable audio data.
 */
public class GeoAudio extends GeoMedia {

	public static final int DEFAULT_PLAYER_WIDTH = 300;
	public static final int DEFAULT_PLAYER_HEIGHT = 48;

	private static final double DEFAULT_STEP = 0.5;

	/**
	 * Constructs a new, empty audio element.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoAudio(Construction c) {
		super(c);
		setSize(DEFAULT_PLAYER_WIDTH, DEFAULT_PLAYER_HEIGHT);
		app = getKernel().getApplication();
		setAnimationStep(DEFAULT_STEP);
	}

	@Override
	public double getMinWidth() {
		return DEFAULT_PLAYER_WIDTH;
	}

	@Override
	public double getMinHeight() {
		return DEFAULT_PLAYER_HEIGHT;
	}

	/**
	 * Constructs a new audio element with given content.
	 * 
	 * @param c
	 *            the construction.
	 * @param url
	 *            the audio URL.
	 */
	public GeoAudio(Construction c, String url) {
		this(c);
		setSrc(url, MediaFormat.AUDIO_HTML5);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.AUDIO;
	}

	@Override
	public GeoElement copy() {
		GeoAudio ret = new GeoAudio(cons);
		ret.setSrc(src, getFormat());
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoAudio()) {
			return;
		}
		src = ((GeoAudio) geo).getSrc();
	}

	@Override
	public void remove() {
		pause();
		super.remove();
	}

	@Override
	protected void onSourceChanged() {
		if (!hasSoundManager()) {
			return;
		}
		app.getSoundManager().loadGeoAudio(this);
	}

	@Override
	public boolean isGeoAudio() {
		return true;
	}

	/**
	 * Play the audio
	 */
	public void play() {
		if (!hasSoundManager()) {
			return;
		}
		app.getSoundManager().play(this);
	}

	/**
	 * @return Whether this audio is playing
	 */
	public boolean isPlaying() {
		if (!hasSoundManager()) {
			return false;
		}
		return app.getSoundManager().isPlaying(this);
	}

	@Override
	public int getDuration() {
		if (!hasSoundManager()) {
			return -1;
		}
		return app.getSoundManager().getDuration(this);
	}

	@Override
	public int getCurrentTime() {
		if (!hasSoundManager()) {
			return -1;
		}
		return app.getSoundManager().getCurrentTime(this);
	}

	@Override
	public void setCurrentTime(int secs) {
		if (!hasSoundManager()) {
			return;
		}
		app.getSoundManager().setCurrentTime(this, secs);
	}

	/**
	 * Pause the audio
	 */
	public void pause() {
		if (!hasSoundManager()) {
			return;
		}
		app.getSoundManager().pause(this);
	}

	private boolean hasSoundManager() {
		return app.getSoundManager() != null;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		if (src != null) {
			sb.append("\t<audio src=\"");
			sb.append(StringUtil.encodeXML(src));
			sb.append("\"/>\n");
		}
	}

	@Override
	public MediaFormat getFormat() {
		return MediaFormat.AUDIO_HTML5;
	}

	/**
	 * 
	 * @param src
	 *            the audio source URL to set.
	 */
	public void setSrc(String src) {
		setSrc(src, MediaFormat.AUDIO_HTML5);
	}
}
