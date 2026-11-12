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

import org.jspecify.annotations.NonNull;

/**
 * Text with a matched part, e.g. a command name matching a search text.
 * @param content text content
 * @param from index of first matched character
 * @param to index of last matched character + 1
 */
public record MatchedString(@NonNull String content, int from, int to) {

	/**
	 * @return [prefix, highlighted part, suffix]
	 */
	public String[] getParts() {
		return new String[] {
			content.substring(0, from), content.substring(from, to), content.substring(to)
		};
	}
}
