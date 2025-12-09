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

package org.geogebra.editor.share.syntax;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SyntaxHintImpl implements SyntaxHint {

	private String command = null;
	private List<String> placeholders;
	private int index;

	/**
	 * @param command command name
	 * @param placeholders argument placeholders
	 * @param index index of active placeholder; must be 0 &lt;= index &lt; placeholders.length
	 */
	public void update(String command, List<String> placeholders, int index) {
		this.command = command;
		this.placeholders = placeholders;
		this.index = index;
	}

	@Override
	public String getPrefix() {
		if (placeholders.isEmpty()) {
			return "";
		}

		return command + "(" + placeholders.stream().limit(index)
				.collect(Collectors.joining(", ")) + (index > 0 ? ", " : "");
	}

	@Override
	public String getSuffix() {
		if (placeholders.isEmpty()) {
			return "";
		}

		return (index < placeholders.size() - 1 ? ", " : "")
				+ placeholders.stream().skip(index + 1).collect(Collectors.joining(", ")) + ")";
	}

	@Override
	public String getActivePlaceholder() {
		return placeholders.isEmpty() ? "" : placeholders.get(index);
	}

	/**
	 * clear hint.
	 */
	public void clear() {
		command = "";
		placeholders = Collections.emptyList();
		index = -1;
	}

	@Override
	public boolean isEmpty() {
		return "".equals(command) && Collections.emptyList().equals(placeholders);
	}

	@Override
	public String toString() {
		return "SyntaxHint{"
				+ "command='" + command + '\''
				+ ", placeholders=" + placeholders
				+ ", index="
				+ index
				+ '}';
	}

	/**
	 * Invalidate the placeholders.
	 */
	public void invalidate() {
		placeholders = Collections.emptyList();
	}
}
