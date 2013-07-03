package geogebra.common.export.pstricks;

public interface ExportSettings {

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

}
