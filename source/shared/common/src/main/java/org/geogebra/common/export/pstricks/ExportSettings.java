package org.geogebra.common.export.pstricks;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Export settings.
 */
public interface ExportSettings {
	int FILL_NONE = 0, FILL_OPAQUE = 1, FILL_OPACITY_PEN = 2,
			FILL_LAYER = 3;

	@MissingDoc
	double getLatexHeight();

	@MissingDoc
	int getFontSize();

	@MissingDoc
	double getYUnit();

	@MissingDoc
	boolean isGrayscale();

	@MissingDoc
	double getXUnit();

	@MissingDoc
	boolean getKeepDotColors();

	@MissingDoc
	int getFormat();

	@MissingDoc
	double textYmaxValue();

	@MissingDoc
	double textYminValue();

	/**
	 * TODO move this out
	 * Callback for finished export.
	 * @param code exported code
	 */
	void write(StringBuilder code);

	@MissingDoc
	boolean getAsyCompactCse5();

	@MissingDoc
	boolean getAsyCompact();

	@MissingDoc
	int getFillType();

	@MissingDoc
	boolean getExportPointSymbol();

	@MissingDoc
	double getLatexWidth();

	@MissingDoc
	boolean getShowAxes();

	@MissingDoc
	boolean getUsePairNames();

	@MissingDoc
	boolean getGnuplot();

	@MissingDoc
	GeoNumeric getcbSlidersItem();
}
