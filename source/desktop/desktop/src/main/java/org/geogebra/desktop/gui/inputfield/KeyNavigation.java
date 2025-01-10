package org.geogebra.desktop.gui.inputfield;

public enum KeyNavigation {
	/** Arrows: nothing, Escape: nothing */
	IGNORE,
	/** Arrows: history if available, Escape: blur */
	BLUR,
	/** Arrows: history, escape: clear */
	HISTORY
}
