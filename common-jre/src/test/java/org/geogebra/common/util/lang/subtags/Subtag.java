package org.geogebra.common.util.lang.subtags;

/** A subtag as defined in RFC 5646 */
public class Subtag {

	private final Type type;
	private final String subtag;
	private final String description;

	/**
	 * Constructs a subtag.
	 * @param type type
	 * @param subtag subtag
	 * @param description description
	 */
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
