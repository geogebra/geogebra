package org.geogebra.web.html5.gui;

public enum Shades {
	NEUTRAL_0("neutral-0"),
	NEUTRAL_100("neutral-100"),
	NEUTRAL_200("neutral-200"),
	NEUTRAL_300("neutral-300"),
	NEUTRAL_400("neutral-400"),
	NEUTRAL_500("neutral-500"),
	NEUTRAL_600("neutral-600"),
	NEUTRAL_700("neutral-700"),
	NEUTRAL_800("neutral-800"),
	NEUTRAL_900("neutral-900");

	public final String name;

	Shades(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getFgColName() {
		return "fg-" + getName();
	}
}