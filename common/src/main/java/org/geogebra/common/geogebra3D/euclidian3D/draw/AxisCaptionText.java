package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.EuclidianSettings;

public class AxisCaptionText implements CaptionText {
	private String text;
	private GFont font;
	private GColor objectColor;
	private final EuclidianSettings settings;

	public AxisCaptionText(EuclidianSettings settings) {
		this.settings = settings;
	}

	@Override
	public String text() {
		return text;
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
		return settings.getAxesLabelsSerif();
	}

	@Override
	public boolean isLaTeX() {
		return false;
	}

	@Override
	public GColor foregroundColor() {
		return objectColor;
	}

	@Override
	public GColor backgroundColor() {
		return null;
	}

	@Override
	public void update(String text, GFont font, GColor fgColor) {
		this.text = text;
		this.font = font;
		this.objectColor = fgColor;
	}

	@Override
	public String textToDraw() {
		return text;
	}

	@Override
	public GeoElement getGeoElement() {
		return null;
	}

	@Override
	public void createFont(GFont original) {
		// nothing to do
	}

	@Override
	public void register(GeoElement geo) {
		// nothing to do
	}

	@Override
	public boolean hasChanged(String text, GFont font) {
		return true;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
