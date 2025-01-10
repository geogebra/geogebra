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
