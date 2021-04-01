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
package org.gwtproject.regexp.client;

import static java.util.Objects.isNull;

import org.gwtproject.regexp.shared.MatchResult;
import org.gwtproject.regexp.shared.RegExp;

import elemental2.core.JsRegExp;
import elemental2.core.JsString;
import jsinterop.base.Js;

/**
 * A class for regular expressions with features like Javascript's RegExp, plus Javascript String's
 * replace and split methods (which can take a RegExp parameter). The pure Java implementation (for
 * server-side use) uses Java's Pattern class, unavailable under GWT. The super-sourced GWT
 * implementation simply calls on to the native Javascript classes.
 *
 * <p>There are a few small incompatibilities between the two implementations. Java-specific
 * constructs in the regular expression syntax (e.g. [a-z&amp;&amp;[^bc]], (?&lt;=foo), \A, \Q) work
 * only in the pure Java implementation, not the GWT implementation, and are not rejected by either.
 * Also, the Javascript-specific constructs $` and $' in the replacement expression work only in the
 * GWT implementation, not the pure Java implementation, which rejects them.
 */
public class NativeRegExp implements RegExp {

	private JsRegExp jsRegExp;

	private NativeRegExp(String pattern) {
		this();
		jsRegExp.compile(pattern);
	}

	private NativeRegExp() {
		jsRegExp = new JsRegExp();
	}

	private NativeRegExp(String pattern, String flags) {
		this();
		jsRegExp.compile(pattern, flags);
	}

	/**
	 * Creates a regular expression object from a pattern with no flags.
	 *
	 * @param pattern the Javascript regular expression pattern to compile
	 * @return a new regular expression
	 * @throws RuntimeException if the pattern is invalid
	 */
	public static RegExp compile(String pattern) {
		return new NativeRegExp(pattern);
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
	public static RegExp compile(String pattern, String flags) {
		return new NativeRegExp(pattern, flags);
	}

	/**
	 * Returns a literal pattern <code>String</code> for the specified <code>String</code>.
	 *
	 * <p>This method produces a <code>String</code> that can be used to create a <code>RegExp</code>
	 * that would match the string <code>s</code> as if it were a literal pattern. Metacharacters or
	 * escape sequences in the input sequence will be given no special meaning.
	 *
	 * @param input The string to be literalized
	 * @return A literal string replacement
	 */
	public static String quote(String input) {
		return new JsString(input).replace(new JsRegExp("([.?*+^$[\\]\\\\(){}|-])", "g"), "\\$1");
	}

	@Override
	public MatchResult exec(String input) {
		String[] result = Js.uncheckedCast(jsRegExp.exec(input));
		return isNull(result) ? null : new NativeMatchResult(Js.cast(result));
	}

	@Override
	public boolean getGlobal() {
		return jsRegExp.global;
	}

	@Override
	public boolean getIgnoreCase() {
		return jsRegExp.ignoreCase;
	}

	@Override
	public int getLastIndex() {
		return jsRegExp.lastIndex;
	}

	@Override
	public boolean getMultiline() {
		return jsRegExp.multiline;
	}

	@Override
	public String getSource() {
		return jsRegExp.source;
	}

	@Override
	public String replace(String input, String replacement) {
		return new JsString(input).replace(jsRegExp, replacement);
	}

	@Override
	public void setLastIndex(int lastIndex) {
		jsRegExp.lastIndex = lastIndex;
	}

	@Override
	public boolean test(String input) {
		return jsRegExp.test(input);
	}
}