package org.geogebra.common.util.lang.subtags;

public class Subtag {

	private final Type type;
	private final String subtag;
	private final String description;

	public Subtag(Type type, String subtag, String description) {
		this.type = type;
		this.subtag = subtag;
		this.description = description;
	}

	public Type getType() {
		return type;
	}

	public String getSubtag() {
		return subtag;
	}

	public String getDescription() {

		return description;
	}
}
