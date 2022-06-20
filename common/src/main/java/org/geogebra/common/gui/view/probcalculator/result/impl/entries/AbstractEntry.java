package org.geogebra.common.gui.view.probcalculator.result.impl.entries;

import org.geogebra.common.gui.view.probcalculator.result.ResultEntry;

/**
 * An abstract representation of UI view.
 */
public abstract class AbstractEntry implements ResultEntry {

	private String text;

	public AbstractEntry(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return text;
	}
}
