package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;

public class Caption3D implements CaptionText {
	private GeoElement geo;
	private CaptionText staticCaption;
	private CaptionText dynamicCaption;
	private CaptionText caption;

	/**
	 *
	 * @param geo the caption is for
	 * @param factory the way the class constructs the static caption.
	 */
	public Caption3D(GeoElement geo, CaptionFactory factory) {
		this.geo = geo;
		this.staticCaption = factory.createStaticCaption3D(geo);
		this.dynamicCaption = new DynamicCaption3D(geo);
		update();
	}

	@Override
	public String text() {
		return caption.text();
	}

	@Override
	public GFont font() {
		return caption.font();
	}

	@Override
	public int fontSize() {
		return caption.fontSize();
	}

	@Override
	public boolean isSerifFont() {
		return caption.isSerifFont();
	}

	@Override
	public boolean isLaTeX() {
		return caption.isLaTeX();
	}

	@Override
	public GColor foregroundColor() {
		return caption.foregroundColor();
	}

	@Override
	public GColor backgroundColor() {
		return caption.backgroundColor();
	}

	@Override
	public void update(String text, GFont font, GColor fgColor) {
		caption.update(text, font, fgColor);
	}

	@Override
	public String textToDraw() {
		return caption.textToDraw();
	}

	@Override
	public GeoElement getGeoElement() {
		return caption.getGeoElement();
	}

	@Override
	public void createFont(GFont original) {
		caption.createFont(original);
	}

	@Override
	public void register(GeoElement geo) {
		caption.register(geo);
	}

	@Override
	public boolean hasChanged(String text, GFont font) {
		return caption.hasChanged(text, font);
	}

	@Override
	public boolean isValid() {
		return caption.isValid();
	}

	/**
	 * Updates caption based on static or dynamic one is needed.
	 */
	public void update() {
		caption = geo.hasDynamicCaption()
				? dynamicCaption
				: staticCaption;
	}
}
