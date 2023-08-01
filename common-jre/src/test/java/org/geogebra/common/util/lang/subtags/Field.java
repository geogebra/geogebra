package org.geogebra.common.util.lang.subtags;

/** Field of a record from the Language Subtag Registry */
public class Field {

	private final String name;
	private final String body;

	/**
	 * Constructs a field. For the format see RFC 4646.
	 * @param name name of the field
	 * @param body body of the field
	 * @see <a href="https://www.rfc-editor.org/rfc/rfc4646.txt">RFC 4646</a>
	 */
	public Field(String name, String body) {
		this.name = name;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public String getBody() {
		return body;
	}
}
