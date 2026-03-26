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
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

public class StaticText3D implements CaptionText {
	private final GeoElement geo;
	private final EuclidianView3D view;
	private GFont font;
	private boolean serif = false;

	/**
	 * @param geo construction element
	 * @param view 3D view
	 */
	public StaticText3D(GeoElement geo, EuclidianView3D view) {
		this.geo = geo;
		this.view = view;
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
		if (!(geo instanceof GeoText text)) {
			font = original;
			return;
		}

		double newFontSize = text.getFontSize(view.getFontSize());
		int newFontStyle = text.getFontStyle();
		serif = text.isSerifFont();
		font = view.getApplication().getFontCanDisplay(
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
