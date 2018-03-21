package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for representing playable audio data.
 * 
 * @author laszlo
 *
 */
public class GeoAudio extends GeoButton {
	private static final int TIME_SLIDER_THICKNESS = 10;
	/** URL of a test audio file */
	public static final String TEST_URL = "http://archive.geogebra.org/static/welcome_to_geogebra.mp3";
	private static final int DEFAULT_PLAYER_WIDTH = 300;
	private static final int DEFAULT_PLAYER_HEIGHT = 48;
	private String dataUrl;
	private App app;

	/**
	 * Constructs a new, empty audio element.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoAudio(Construction c) {
		super(c);
		app = getKernel().getApplication();
		setWidth(DEFAULT_PLAYER_WIDTH);
		setHeight(DEFAULT_PLAYER_HEIGHT);
		setLineThickness(TIME_SLIDER_THICKNESS);
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
		setDataUrl(url);
		setLabel("audio");
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.AUDIO;
	}

	@Override
	public GeoElement copy() {
		GeoAudio ret = new GeoAudio(cons);
		ret.setDataUrl(dataUrl);
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoAudio()) {
			return;
		}
		dataUrl = ((GeoAudio) geo).getDataUrl();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	/**
	 * 
	 * @return the URL of the audio data.
	 */
	public String getDataUrl() {
		return dataUrl;
	}

	/**
	 * Sets the URL of the audio data.
	 * 
	 * @param dataUrl
	 *            to set.
	 */
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
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
	 * Plays the audio.
	 */
	public void play() {
		if (!hasSoundManager()) {
			return;
		}

		app.getSoundManager().play(this);
	}

	/**
	 * @return if audio is playing.
	 */
	public boolean isPlaying() {
		if (!hasSoundManager()) {
			return false;
		}

		return app.getSoundManager().isPlaying(this);
	}

	/**
	 * @return the duration of the audio in seconds.
	 */
	public int getDuration() {
		if (!hasSoundManager()) {
			return -1;
		}

		return app.getSoundManager().getDuration(dataUrl);
	}

	/**
	 * @return the time where audio play is at.
	 */
	public int getCurrentTime() {
		if (!hasSoundManager()) {
			return -1;
		}

		return app.getSoundManager().getCurrentTime(dataUrl);
	}

	/**
	 * Stops audio playback.
	 */
	public void pause() {
		if (!hasSoundManager()) {
			return;
		}

		app.getSoundManager().pause(this);
	}

	@Override
	public void remove() {
		pause();
		super.remove();
	}

	private boolean hasSoundManager() {
		return app.getSoundManager() != null;
	}
}
