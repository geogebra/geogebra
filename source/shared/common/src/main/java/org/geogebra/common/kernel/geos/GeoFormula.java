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

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.serializer.TeXSerializer;

public class GeoFormula extends GeoInline {

	public static final int DEFAULT_WIDTH = 250;
	public static final int DEFAULT_HEIGHT = 48;

	private static final Parser parser = new Parser(new TemplateCatalog());
	private static final TeXSerializer texSerializer = new TeXSerializer();

	static {
		texSerializer.setPlaceholderEnabled(false);
	}

	private boolean defined = true;
	private String formula;

	private double minWidth;
	private double minHeight;

	private String latex = "";

	/**
	 * Creates new GeoElement for given construction
	 *
	 * @param c Construction
	 * @param location initial location in RW coordinates
	 */
	public GeoFormula(Construction c, GPoint2D location) {
		super(c);
		setLocation(location);
		setContentWidth(DEFAULT_WIDTH);
		setContentHeight(DEFAULT_HEIGHT);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.FORMULA;
	}

	@Override
	public GeoElement copy() {
		GeoFormula copy = new GeoFormula(cons, getLocation());
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoFormula) {
			this.formula = ((GeoFormula) geo).formula;
			this.defined = geo.isDefined();
		} else {
			setUndefined();
		}
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		defined = false;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return latex;
	}

	@Override
	public void setContent(String content) {
		formula = content;
		try {
			latex = texSerializer.serialize(parser.parse(formula));
		} catch (ParseException e) {
			Log.debug(e);
		}
	}

	@Override
	public String getContent() {
		return formula;
	}

	@Override
	public double getMinWidth() {
		return Math.max(minWidth, DEFAULT_WIDTH);
	}

	@Override
	public double getMinHeight() {
		return Math.max(minHeight, DEFAULT_HEIGHT);
	}

	public void setMinWidth(double minWidth) {
		this.minWidth = minWidth;
	}

	@Override
	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * Make sure width and height are bigger than their respective lower bounds.
	 */
	public void ensureMinSize() {
		setSize(Math.max(getWidth(), minWidth), Math.max(getHeight(), minHeight));
	}
}
