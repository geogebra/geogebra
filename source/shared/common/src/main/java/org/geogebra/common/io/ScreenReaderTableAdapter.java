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

package org.geogebra.common.io;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OrdinalConverter;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.util.lang.Language;

import com.himamis.retex.renderer.share.serialize.TableAdapter;

public class ScreenReaderTableAdapter extends TableAdapter {
	private boolean transpose;
	private String type = "table";
	private final Localization loc;

	public ScreenReaderTableAdapter(Localization loc) {
		this.loc = loc;
	}

	@Override
	public String matrixStart(int rows, int cols) {
		if (transpose) {
			return loc.getPlainDefault("ScreenReader.MatrixAWithBColumnsAndCRows",
					"%0 with %1 columns and %2 rows",
					type, String.valueOf(cols), String.valueOf(rows)) + " ";
		}
		return loc.getPlainDefault("ScreenReader.MatrixAWithBRowsAndCColumns",
				"%0 with %1 rows and %2 columns",
				type, String.valueOf(rows), String.valueOf(cols)) + " ";
	}

	@Override
	public String matrixEnd() {
		return " end of " + type;
	}

	@Override
	public String matrixRowOrColumnEnd() {
		return "";
	}

	@Override
	public String matrixRowStart(int row) {
		return "The " + OrdinalConverter.getOrdinalNumber(Language.English_UK, row + 1)
				+ (transpose ? " column is " : " row is ");
	}

	@Override
	public boolean shouldTransposeMatrices() {
		return transpose;
	}

	@Override
	public void setShouldTransposeMatrices(boolean transpose) {
		this.transpose = transpose;
	}

	@Override
	public String getSeparator() {
		return " ";
	}

	@Override
	public void setMatrixType(String left, String right) {
		if ("|".equals(left) && "|".equals(right)) {
			type = "determinant";
		} else if (ScreenReader.getOpenParenthesis(loc).equals(left)
				&& ScreenReader.getCloseParenthesis(loc).equals(right)
			|| "(".equals(left) && ")".equals(right)) {
			type = "matrix";
		} else {
			type = "table";
		}
	}
}
