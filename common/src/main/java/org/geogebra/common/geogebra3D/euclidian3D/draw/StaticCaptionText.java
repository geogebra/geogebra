package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;

public class StaticCaptionText implements CaptionText {
	private GeoElement geo;
	private GFont font;
	private boolean serif = false;

	public StaticCaptionText(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public String text() {
		return geo.getLabelDescription();
	}

	@Override
	public GFont font() {
		return font;
	}

	@Override
	public int fontSize() {
		return 0;
	}

	@Override
	public boolean isSerifFont() {
		return serif;
	}

	@Override
	public boolean isLaTeX() {
		return text().startsWith("$") && text().endsWith("$");
	}

	@Override
	public GColor foregroundColor() {
		return geo.getObjectColor();
	}

	@Override
	public GColor backgroundColor() {
		return geo.getBackgroundColor();
	}

	@Override
	public void update(String text, GFont font, GColor fgColor) {
		// nothing to do
	}

	@Override
	public String textToDraw() {
		return text().substring(1, text().length() - 1);
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void createFont(GFont original) {
		int size = original.getSize();
		font = original.deriveFont(original.getStyle(),
				(float) (size * getFontScale()));
		serif = true;
		if (geo instanceof TextProperties) {
			serif = ((TextProperties) geo).isSerifFont();
		}
	}

	@Override
	public void register(GeoElement geo) {
		// nothing to do.
	}

	@Override
	public boolean hasChanged(String text, GFont font) {
		return !text().equals(text) || !font().equals(font);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	private int getFontScale() {
		return 1;
	}
}
