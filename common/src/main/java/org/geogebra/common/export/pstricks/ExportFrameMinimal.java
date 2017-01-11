package org.geogebra.common.export.pstricks;

import org.geogebra.common.kernel.geos.GeoNumeric;

public class ExportFrameMinimal implements ExportSettings {

	private StringBuilder code;
	private boolean gnuPlot = false;
	private boolean usePairName;
	private double latexWidth;
	private boolean showAxes;
	private boolean exportPointSymbol = true;
	private int fillType;
	private int fontSize;
	private double xUnit = 1;
	private double yUnit = 1;
	private boolean grayscale = false;
	private boolean keepDotColors;
	private double latexHeight;
	private int format = 0;
	private int yMax;
	private int yMin;
	private GeoNumeric cbitem;

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
	public int textYmaxValue() {
		return yMax;
	}

	@Override
	public int textYminValue() {
		return yMin;
	}

	@Override
	public void write(StringBuilder code) {
		this.code = code;
	}

	public String getCode() {
		return code.toString();
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
}
