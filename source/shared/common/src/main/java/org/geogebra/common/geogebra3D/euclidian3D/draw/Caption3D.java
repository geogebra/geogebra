/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
