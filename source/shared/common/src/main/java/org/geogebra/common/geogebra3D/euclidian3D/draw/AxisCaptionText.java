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
		return text != null && text.length() > 1 && (text.charAt(0) == '$')
				&& text.endsWith("$");
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
