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
