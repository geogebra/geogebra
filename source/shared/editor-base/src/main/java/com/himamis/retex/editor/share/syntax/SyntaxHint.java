package com.himamis.retex.editor.share.syntax;

public interface SyntaxHint {

	/**
	 * @return parts before the active placeholder
	 */
	String getPrefix();

	/**
	 * @return active placeholder
	 */
	String getActivePlaceholder();

	/**
	 * @return parts after the active placeholder
	 */
	String getSuffix();

	/**
	 * Tests if the syntax hint is empty.
	 * The prefix, placeholder and suffix will return empty string in this case.
	 * @return true if there is no hint.
	 */
	boolean isEmpty();
}
