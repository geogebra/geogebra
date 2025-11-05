/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
