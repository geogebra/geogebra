package org.geogebra.common.export.pstricks;

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

	public double getLatexHeight() {
		return latexHeight;
	}

	public int getFontSize() {
		return fontSize;
	}

	public double getYUnit() {
		return yUnit;
	}

	public boolean isGrayscale() {
		return grayscale;
	}

	public double getXUnit() {
		return xUnit;
	}

	public boolean getKeepDotColors() {
		return keepDotColors;
	}

	public int getFormat() {
		return format;
	}

	public int textYmaxValue() {
		return yMax;
	}

	public int textYminValue() {
		return yMin;
	}

	public void write(StringBuilder code) {
		this.code = code;
	}

	public String getCode() {
		return code.toString();
	}

	public boolean getAsyCompactCse5() {
		return false;
	}

	public boolean getAsyCompact() {
		return false;
	}

	public int getFillType() {
		return fillType;
	}

	public boolean getExportPointSymbol() {
		return exportPointSymbol;
	}

	public double getLatexWidth() {
		return latexWidth;
	}

	public boolean getShowAxes() {
		return showAxes;
	}

	public boolean getUsePairNames() {
		return usePairName;
	}

	public boolean getGnuplot() {
		return gnuPlot;
	}

}
