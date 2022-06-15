package org.geogebra.common.gui.view.probcalculator.model.entry;

import javax.annotation.Nonnull;

/**
 * Represents a static text or an input field, etc.
 * In this context, entry means: a representation of a UI element.
 */
public abstract class AbstractEntry {

	public enum EntryType {
		Text,
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
