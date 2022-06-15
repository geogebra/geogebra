package org.geogebra.common.gui.view.probcalculator.model.entry;

import javax.annotation.Nonnull;

public abstract class AbstractEntry {

	public enum EntryType {
		PlaceHolder,
		Input
	}

	String text = "";

	public abstract EntryType getType();

	public void setText(@Nonnull String text) {
		this.text = text;
	}

	public @Nonnull String getText() {
		return text;
	}

	@Override
	public String toString() {
		return text;
	}
}
