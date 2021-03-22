/*
 * Copyright © 2019 The GWT Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gwtproject.regexp.shared;

public interface RegExp {

	/**
	 * Creates a regular expression object from a pattern with no flags.
	 *
	 * @param pattern the Javascript regular expression pattern to compile
	 * @return a new regular expression
	 * @throws RuntimeException if the pattern is invalid
	 */
	static RegExp compile(String pattern) {
		return RegExpFactory.getPrototype().compile(pattern);
	}

	/**
	 * Creates a regular expression object from a pattern with no flags.
	 *
	 * @param pattern the Javascript regular expression pattern to compile
	 * @param flags the flags string, containing at most one occurence of {@code 'g'} ({@link
	 *     #getGlobal()}), {@code 'i'} ({@link #getIgnoreCase()} ), or {@code 'm'} ({@link
	 *     #getMultiline()}).
	 * @return a new regular expression
	 * @throws RuntimeException if the pattern or the flags are invalid
	 */
	static RegExp compile(String pattern, String flags) {
		return RegExpFactory.getPrototype().compile(pattern, flags);
	}

	/**
	 * Returns a literal pattern <code>String</code> for the specified <code>String</code>.
	 *
	 * <p>This method produces a <code>String</code> that can be used to create
	 * a <code>RegExp</code> that would match the string <code>s</code> as if it were
	 * a literal pattern. Metacharacters or escape sequences in the input sequence
	 * will be given no special meaning.
	 *
	 * @param input The string to be literalized
	 * @return A literal string replacement
	 */
	static String quote(String input) {
		return RegExpFactory.getPrototype().quote(input);
	}

	/**
	 * Applies the regular expression to the given string. This call affects the value returned by
	 * {@link #getLastIndex()} if the global flag is set.
	 *
	 * @param input the string to apply the regular expression to
	 * @return a match result if the string matches, else {@code null}
	 */
	MatchResult exec(String input);

	/**
	 * Returns whether the regular expression captures all occurences of the pattern.
	 *
	 * @return true if global, false otherwise
	 */
	boolean getGlobal();

	/**
	 * Returns whether the regular expression ignores case.
	 *
	 * @return true if ignore case, false otherwise
	 */
	boolean getIgnoreCase();

	/**
	 * Returns the zero-based position at which to start the next match. The return value is not
	 * defined if the global flag is not set. After a call to {@link #exec(String)} or {@link
	 * #test(String)}, this method returns the next position following the most recent match.
	 *
	 * @return last index
	 * @see #getGlobal()
	 */
	int getLastIndex();

	/**
	 * Sets the zero-based position at which to start the next match.
	 *
	 * @param lastIndex the zero-based position
	 */
	void setLastIndex(int lastIndex);

	/**
	 * Returns whether '$' and '^' match line returns ('\n' and '\r') in addition
	 * to the beginning or end of the string.
	 *
	 * @return true if multiline, false otherwise
	 */
	boolean getMultiline();

	/**
	 * Returns the pattern string of the regular expression.
	 *
	 * @return the source
	 */
	String getSource();

	/**
	 * Returns the input string with the part(s) matching the regular expression replaced with the
	 * replacement string. If the global flag is set, replaces all matches of the regexp.
	 * Otherwise, replaces the first match of the regular expression. As per Javascript semantics,
	 * backslashes in the replacement string get no special treatment, but the replacement can
	 * use the following special patterns:
	 *
	 * <ul>
	 *   <li>$1, $2, ... $99 - inserts the n'th group matched by the regular expression.
	 *   <li>$&amp; - inserts the entire string matched by the regular expression.
	 *   <li>$$ - inserts a $.
	 * </ul>
	 *
	 * @param input the string in which the regular expression is to be searched.
	 * @param replacement the replacement string.
	 * @return the input string with the regular expression replaced with the replacement string.
	 * @throws RuntimeException if {@code replacement} is invalid
	 */
	String replace(String input, String replacement);

	/**
	 * Determines if the regular expression matches the given string. This call affects the value
	 * returned by {@link #getLastIndex()} if the global flag is not set. Equivalent to: {@code
	 * exec(input) != null}
	 *
	 * @param input the string to apply the regular expression to
	 * @return whether the regular expression matches the given string.
	 */
	boolean test(String input);
}
