package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;

/**
 * Class for representing playable audio data.
 * 
 * @author laszlo
 *
 */
public class GeoAudio extends GeoButton {
	/** URL of a test audio file */
	public static final String TEST_URL = "http://archive.geogebra.org/static/welcome_to_geogebra.mp3";
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
	}

	/**
	 * Constructs a new audio element with given content.
	 * 
	 * @param c
	 *            the construction.
	 * @param url
	 *            the audio URL.
	 * @param top
	 *            top to add audio player associated with the geo.
	 * @param left
	 *            left to add audio player associated with the geo.
	 */
	public GeoAudio(Construction c, String url, int top, int left) {
		this(c);
		setDataUrl(url);
		this.labelOffsetX = top;
		this.labelOffsetY = left;
		addScript();
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
		app.getSoundManager().loadGeoAudio(this);
	}

	private void addScript() {
		String playText = "PlaySound[\"%0\"]";
		Script playScript = app.createScript(ScriptType.GGBSCRIPT, playText, true);
		setClickScript(playScript);
	}
	@Override
	public boolean isGeoAudio() {
		return true;
	}

	/**
	 * Plays the audio.
	 */
	public void play() {
		runClickScripts(dataUrl);
	}

	public int getDuration() {
		return app.getSoundManager().getDuration(dataUrl);
	}

	public int getCurrentTime() {
		return app.getSoundManager().getCurrentTime(dataUrl);
	}
}
