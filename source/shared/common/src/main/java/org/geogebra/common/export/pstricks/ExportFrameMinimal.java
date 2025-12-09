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

package org.geogebra.common.export.pstricks;

import org.geogebra.common.kernel.geos.GeoNumeric;

public class ExportFrameMinimal implements ExportSettings {

	private StringBuilder code;
	private boolean gnuPlot = false;
	private boolean usePairName;
	private double latexWidth;
	private boolean showAxes = true;
	private boolean exportPointSymbol = true;
	private int fillType;
	private int fontSize = 10;
	private double xUnit = 1;
	private double yUnit = 1;
	private boolean grayscale = false;
	private boolean keepDotColors;
	private double latexHeight;
	private int format = 0;
	private double yMax;
	private double yMin;
	private GeoNumeric cbitem;

	/**
	 * @param ymin2
	 *            y-min
	 * @param ymax2
	 *            y-max
	 */
	public ExportFrameMinimal(double ymin2, double ymax2) {
		yMin = ymin2;
		yMax = ymax2;
	}

	@Override
	public double getLatexHeight() {
		return latexHeight;
	}

	@Override
	public int getFontSize() {
		return fontSize;
	}

	@Override
	public double getYUnit() {
		return yUnit;
	}

	@Override
	public boolean isGrayscale() {
		return grayscale;
	}

	@Override
	public double getXUnit() {
		return xUnit;
	}

	@Override
	public boolean getKeepDotColors() {
		return keepDotColors;
	}

	@Override
	public int getFormat() {
		return format;
	}

	@Override
	public double textYmaxValue() {
		return yMax;
	}

	@Override
	public double textYminValue() {
		return yMin;
	}

	@Override
	public void write(StringBuilder code1) {
		this.code = code1;
	}

	public String getCode() {
		return code.toString().replaceAll("\n", "\r\n");
	}

	@Override
	public boolean getAsyCompactCse5() {
		return false;
	}

	@Override
	public boolean getAsyCompact() {
		return false;
	}

	@Override
	public int getFillType() {
		return fillType;
	}

	@Override
	public boolean getExportPointSymbol() {
		return exportPointSymbol;
	}

	@Override
	public double getLatexWidth() {
		return latexWidth;
	}

	@Override
	public boolean getShowAxes() {
		return showAxes;
	}

	@Override
	public boolean getUsePairNames() {
		return usePairName;
	}

	@Override
	public boolean getGnuplot() {
		return gnuPlot;
	}

	@Override
	public GeoNumeric getcbSlidersItem() {
		return cbitem;
	}

	public void setSlider(GeoNumeric slider) {
		cbitem = slider;
	}

	/**
	 * Set the keep color flag.
	 */
	public void setKeepColor() {
		this.keepDotColors = true;
	}
}
