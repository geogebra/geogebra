package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

public class DynamicCaption3D implements CaptionText {
	private GFont font;
	private GeoElement geo;

	public DynamicCaption3D(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public String text() {
		return dynamicCaption().getTextStringSafe();
	}

	private GeoText dynamicCaption() {
		return geo.getDynamicCaption();
	}

	@Override
	public GFont font() {
		return font;
	}

	@Override
	public int fontSize() {
		return (int) (font.getSize()
				* dynamicCaption().getFontSizeMultiplier());
	}

	@Override
	public boolean isSerifFont() {
		return dynamicCaption().isSerifFont();
	}

	@Override
	public boolean isLaTeX() {
		return dynamicCaption().isLaTeX();
	}

	@Override
	public GColor foregroundColor() {
		return dynamicCaption().getObjectColor();
	}

	@Override
	public GColor backgroundColor() {
		return dynamicCaption().getBackgroundColor();
	}

	@Override
	public void update(String text, GFont font, GColor fgColor) {
		// no need
	}

	@Override
	public String textToDraw() {
		return text();
	}

	@Override
	public GeoElement getGeoElement() {
		return dynamicCaption();
	}

	@Override
	public void createFont(GFont original) {
		font = original.deriveFont(dynamicCaption().getFontStyle(),
				original.getSize() * dynamicCaption().getFontSizeMultiplier());
	}

	@Override
	public void register(GeoElement geo) {
		dynamicCaption().registerUpdateListener(geo);
	}

	@Override
	public boolean hasChanged(String text, GFont font) {
		return true;
	}

	@Override
	public boolean isValid() {
		return geo.hasDynamicCaption();
	}
}
