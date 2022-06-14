package org.geogebra.common.gui.view.probcalculator.model.entry;

public class StaticTextEntry extends AbstractEntry {

	public StaticTextEntry() {
	}

	public StaticTextEntry(String text) {
		this.text = text;
	}

	@Override
	public EntryType getType() {
		return EntryType.PlaceHolder;
	}
}
