package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for representing playable audio data.
 * 
 * @author laszlo
 *
 */
public class GeoAudio extends GeoElement {

	private String dataUrl;

	/**
	 * Constructs a new, empty audio element.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoAudio(Construction c) {
		super(c);
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
		super(c);
		setDataUrl(url);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
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
	public boolean isDefined() {
		return dataUrl != null;
	}

	@Override
	public void setUndefined() {
		dataUrl = null;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isEqual(GeoElementND geo) {
		if (!geo.isGeoAudio()) {
			return false;
		}
		return dataUrl.equals(((GeoAudio) geo).getDataUrl());
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
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
	}

	@Override
	public boolean isGeoAudio() {
		return true;
	}

}
