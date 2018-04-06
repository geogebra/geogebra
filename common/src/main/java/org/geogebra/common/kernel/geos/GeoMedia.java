package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;

/**
 * Class for representing playable media data.
 * 
 * @author laszlo
 *
 */
public abstract class GeoMedia extends GeoButton {

	/** Source of the media, available for subclasses too */
	protected String src;

	/** Application for subclasses too. */
	protected App app;

	/**
	 * Constructs a new, empty media element.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoMedia(Construction c) {
		super(c);
		app = getKernel().getApplication();
	}

	/**
	 * Constructs a new media element with given content.
	 * 
	 * @param c
	 *            the construction.
	 * @param url
	 *            the media URL.
	 */
	public GeoMedia(Construction c, String url) {
		this(c);
		setSrc(url);
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
	 * @return the source of the media.
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * Sets the source of the media.
	 * 
	 * @param src
	 *            to set.
	 */
	public void setSrc(String src) {
		this.src = src;
		onSourceChanged();
	}

	/**
	 * Called after source has changed.
	 */
	protected abstract void onSourceChanged();

	/**
	 * Plays the media.
	 */
	public abstract void play();

	/**
	 * @return if media is playing.
	 */
	public abstract boolean isPlaying();

	/**
	 * @return the duration in seconds.
	 */
	public abstract int getDuration();

	/**
	 * @return the time where media play is at.
	 */
	public abstract int getCurrentTime();

	/**
	 * Sets the current position to a given time in seconds.
	 * 
	 * @param secs
	 *            to set.
	 */
	public abstract void setCurrentTime(int secs);

	/**
	 * Stops media play back.
	 */
	public abstract void pause();

	@Override
	public void remove() {
		pause();
		super.remove();
	}
}
