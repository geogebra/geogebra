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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.draw.DrawStickyNote;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.common.plugin.GeoClass;

public class GeoStickyNote extends GeoInline
		implements HasVerticalAlignment, HasTextFormatter, TextStyle {
	public static final int DEFAULT_WIDTH = 160;
	public static final int DEFAULT_HEIGHT = 160;
	public static final int MIN_HEIGHT = 36;
	private String content;
	private VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;
	private double minHeight;

	/**
	 * Creates a sticky note geo.
	 * @param c {@link Construction}
	 * @param location x,y location of the sticky note
	 */
	public GeoStickyNote(Construction c, GPoint2D location) {
		super(c);
		setLocation(location);
		setBackgroundColor(GColor.STICKY_NOTE_YELLOW);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setContentWidth(DEFAULT_WIDTH);
		setContentHeight(DEFAULT_HEIGHT);
		setContent("[{\"text\":\"\\n\",\"align\":\"center\"}]");
	}

	@Override
	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.STICKY_NOTE;
	}

	@Override
	public GeoElement copy() {
		return new GeoStickyNote(
				cons, new GPoint2D(getLocation().getX(), getLocation().getY()));
	}

	@Override
	public void set(GeoElementND geo) {
		cons = geo.getConstruction();
		if (geo instanceof GeoStickyNote stickyNote) {
			setLocation(stickyNote.getLocation());
			setSize(stickyNote.getWidth(), stickyNote.getHeight());
		}
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setUndefined() {
		// unimplemented
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return null;
	}

	@Override
	public double getMinWidth() {
		return GeoInlineText.MIN_WIDTH;
	}

	@Override
	public double getMinHeight() {
		return Math.max(minHeight, MIN_HEIGHT);
	}

	@Override
	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	@Override
	public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}

	@Override
	public int getFontStyle() {
		return GeoInlineText.getFontStyle(getFormatter());
	}

	@Override
	public double getFontSizeMultiplier() {
		return GeoText.getRelativeFontSize(GeoText.FONTSIZE_SMALL);
	}

	@Override
	public int defaultFontSize() {
		return FontSettings.DEFAULT_STICKY_NOTE_FONT_SIZE;
	}

	@Override
	public int defaultPadding() {
		return DrawStickyNote.PADDING;
	}

	@Override
	public boolean hasProperties() {
		return false;
	}
}
