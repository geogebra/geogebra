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

package org.geogebra.common.util;

public class MatchedString {
	public final String content;
	public final int from;
	public final int to;

	/**
	 * Note: This is necessary for Objective-C.
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Note: This is necessary for Objective-C.
	 * @return from
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * Note: This is necessary for Objective-C.
	 * @return to
	 */
	public int getTo() {
		return to;
	}

	/**
	 * @param content text content
	 * @param from index of first matched character
	 * @param to index of last matched character + 1
	 */
	public MatchedString(String content, int from, int to) {
		this.content = content;
		this.from = from;
		this.to = to;
	}

	/**
	 * @return [prefix, highlighted part, suffix]
	 */
	public String[] getParts() {
		return new String[] {content.substring(0, from),
				content.substring(from, to),
				content.substring(to)};
	}
}
