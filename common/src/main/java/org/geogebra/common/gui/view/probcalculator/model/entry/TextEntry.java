package org.geogebra.common.gui.view.probcalculator.model.entry;

/**
 * Represents a text view.
 */
public class TextEntry extends AbstractEntry {

	public TextEntry() {
	}

	public TextEntry(String text) {
		this.text = text;
	}

	@Override
	public EntryType getType() {
		return EntryType.Text;
	}
}
