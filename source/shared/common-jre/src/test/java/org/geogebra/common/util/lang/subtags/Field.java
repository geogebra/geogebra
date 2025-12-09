/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.common.util.lang.subtags;

/** Field of a record from the Language Subtag Registry */
public class Field {

	public final String name;
	public final String body;

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
}
