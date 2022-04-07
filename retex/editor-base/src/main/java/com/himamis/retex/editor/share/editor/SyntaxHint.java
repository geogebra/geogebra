package com.himamis.retex.editor.share.editor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SyntaxHint {

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

	/**
	 * @return parts before the active placeholder
	 */
	public String getPrefix() {
		if (placeholders.isEmpty()) {
			return "";
		}

		return command + "(" + placeholders.stream().limit(index)
				.collect(Collectors.joining(", ")) + (index > 0 ? ", " : "");
	}

	/**
	 * @return parts after the active placeholder
	 */
	public String getSuffix() {
		if (placeholders.isEmpty()) {
			return "";
		}

		return (index < placeholders.size() - 1 ? ", " : "")
				+ placeholders.stream().skip(index + 1).collect(Collectors.joining(",")) + ")";
	}

	/**
	 * @return active placeholder
	 */
	public String getActivePlacehorder() {
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

	/**
	 *
	 * @return if hint is empty.
	 */
	public boolean isEmpty() {
		return "".equals(command) && Collections.emptyList().equals(placeholders)
				;
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

	public void invalidate() {
		placeholders = Collections.emptyList();
	}
}
