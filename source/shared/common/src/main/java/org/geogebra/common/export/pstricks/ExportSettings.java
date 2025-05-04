package org.geogebra.common.export.pstricks;

import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Export settings.
 */
public interface ExportSettings {
	int FILL_NONE = 0, FILL_OPAQUE = 1, FILL_OPACITY_PEN = 2,
			FILL_LAYER = 3;

	double getLatexHeight();

	int getFontSize();

	double getYUnit();

	boolean isGrayscale();

	double getXUnit();

	boolean getKeepDotColors();

	int getFormat();

	double textYmaxValue();

	double textYminValue();

	/**
	 * TODO move this out
	 * Callback for finished export.
	 * @param code exported code
	 */
	void write(StringBuilder code);

	boolean getAsyCompactCse5();

	boolean getAsyCompact();

	int getFillType();

	boolean getExportPointSymbol();

	double getLatexWidth();

	boolean getShowAxes();

	boolean getUsePairNames();

	boolean getGnuplot();

	GeoNumeric getcbSlidersItem();
}
