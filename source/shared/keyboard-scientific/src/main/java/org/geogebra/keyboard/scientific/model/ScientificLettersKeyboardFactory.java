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

package org.geogebra.keyboard.scientific.model;

import java.util.Map;

import org.geogebra.keyboard.base.model.impl.factory.LetterKeyboardFactory;

public class ScientificLettersKeyboardFactory extends LetterKeyboardFactory {

	private static final String DEFAULT_CONTROL_ROW = "=,'";
	private ScientificLetterRowsBuilder rowsBuilder;

	public ScientificLettersKeyboardFactory() {
		rowsBuilder = new ScientificLetterRowsBuilder();
	}

	@Override
	public void setKeyboardDefinition(String topRow, String middleRow, String bottomRow, String
			controlRow, Integer bottomActionLeft, Integer controlActionLeft) {
		String[] rows = rowsBuilder.rowsFrom(topRow, middleRow, bottomRow);
		super.setKeyboardDefinition(rows[0], rows[1], rows[2], DEFAULT_CONTROL_ROW,
				null, ACTION_SHIFT);
	}

	@Override
	public void setUpperKeys(Map<String, String> upperKeys) {
		rowsBuilder.setUpperKeys(upperKeys);
	}
}
