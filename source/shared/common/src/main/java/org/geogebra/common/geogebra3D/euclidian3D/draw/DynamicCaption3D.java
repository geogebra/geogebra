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
