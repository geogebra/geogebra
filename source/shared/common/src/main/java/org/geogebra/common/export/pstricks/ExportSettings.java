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
