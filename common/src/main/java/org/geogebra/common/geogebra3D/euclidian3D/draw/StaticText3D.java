package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;

public class StaticText3D implements CaptionText {
	private GeoElement geo;
	private GFont font;
	private boolean serif = false;

	public StaticText3D(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public String text() {
		return asGeoText().getTextStringSafe();
	}

	@Override
	public GFont font() {
		return font;
	}

	private GeoText asGeoText() {
		return (GeoText) geo;
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
		// no need
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
		if (!(geo instanceof GeoText)) {
			font = original;
			return;
		}
		GeoText text = (GeoText) getGeoElement();

		App app = getGeoElement().getKernel().getApplication();
		int newFontSize = (int) Math.max(4,
				((EuclidianView3D) app.getEuclidianView3D()).getFontSize()
						* text.getFontSizeMultiplier());
		int newFontStyle = text.getFontStyle();
		serif = text.isSerifFont();
		font = app.getFontCanDisplay(
				text.getTextString(), serif, newFontStyle, newFontSize);
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

	protected void updateFont(GFont f) {
		font = f;
	}
}
