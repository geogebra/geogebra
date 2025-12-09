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
 
package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;

public class TestCellRenderableFactory implements CellRenderableFactory {
	@Override
	public SelfRenderable getRenderable(Object data, SpreadsheetStyling style,
			int row, int column) {
		return data == null ? null : new SelfRenderable(new StringRenderer(),
				GFont.PLAIN, CellFormat.ALIGN_LEFT, data);
	}
}