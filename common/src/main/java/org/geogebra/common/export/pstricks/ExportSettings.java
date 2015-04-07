package org.geogebra.common.export.pstricks;

public interface ExportSettings {
	static final int FILL_NONE = 0, FILL_OPAQUE = 1, FILL_OPACITY_PEN = 2,
			FILL_LAYER = 3;

	double getLatexHeight();

	int getFontSize();

	double getYUnit();

	boolean isGrayscale();

	double getXUnit();

	boolean getKeepDotColors();

	int getFormat();

	int textYmaxValue();

	int textYminValue();

	void write(StringBuilder code);

	boolean getAsyCompactCse5();

	boolean getAsyCompact();

	int getFillType();

	boolean getExportPointSymbol();

	double getLatexWidth();

	boolean getShowAxes();

	boolean getUsePairNames();

	boolean getGnuplot();

}
