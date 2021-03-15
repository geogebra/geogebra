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

package org.gwtproject.regexp.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gwtproject.regexp.shared.MatchResult;
import org.gwtproject.regexp.shared.RegExp;

public final class JavaRegExp implements RegExp {

	// In JS syntax, a \ in the replacement string has no special meaning.
	// In Java syntax, a \ in the replacement string escapes the next character,
	// so we have to translate \ to \\ before passing it to Java.
	private static final Pattern REPLACEMENT_BACKSLASH = Pattern.compile("\\\\");
	// To get \\, we have to say \\\\\\\\:
	// \\\\\\\\ --> Java string unescape --> \\\\
	// \\\\ ---> Pattern replacement unescape in replacement preprocessing --> \\
	private static final String REPLACEMENT_BACKSLASH_FOR_JAVA = "\\\\\\\\";

	// In JS syntax, a $& in the replacement string stands for the whole match.
	// In Java syntax, the equivalent is $0, so we have to translate $& to
	// $0 before passing it to Java. However, we have to watch out for $$&, which
	// is actually a Javascript $$ (see below) followed by a & with no special
	// meaning, and must not get translated.
	private static final Pattern REPLACEMENT_DOLLAR_AMPERSAND =
			Pattern.compile("((?:^|\\G|[^$])(?:\\$\\$)*)\\$&");
	private static final String REPLACEMENT_DOLLAR_AMPERSAND_FOR_JAVA = "$1\\$0";

	// In JS syntax, a $` and $' in the replacement string stand for everything
	// before the match and everything after the match.
	// In Java syntax, there is no equivalent, so we detect and reject $` and $'.
	// However, we have to watch out for $$` and $$', which are actually a JS $$
	// (see below) followed by a ` or ' with no special meaning, and must not be
	// rejected.
	private static final Pattern REPLACEMENT_DOLLAR_APOSTROPHE =
			Pattern.compile("(?:^|[^$])(?:\\$\\$)*\\$[`']");

	// In JS syntax, a $$ in the replacement string stands for a (single) dollar
	// sign, $.
	// In Java syntax, the equivalent is \$, so we have to translate $$ to \$
	// before passing it to Java.
	private static final Pattern REPLACEMENT_DOLLAR_DOLLAR = Pattern.compile("\\$\\$");
	// To get \$, we have to say \\\\\\$:
	// \\\\\\$ --> Java string unescape --> \\\$
	// \\\$ ---> Pattern replacement unescape in replacement preprocessing --> \$
	private static final String REPLACEMENT_DOLLAR_DOLLAR_FOR_JAVA = "\\\\\\$";
	private final boolean globalFlag;
	private final Pattern pattern;
	private final String source;
	private int lastIndex;

	private JavaRegExp(String source, Pattern pattern, boolean globalFlag) {
		this.source = source;
		this.pattern = pattern;
		this.globalFlag = globalFlag;
		lastIndex = 0;
	}

	/**
	 * Creates a regular expression object from a pattern with no flags.
	 *
	 * @param pattern the Javascript regular expression pattern to compile
	 * @return a new regular expression
	 * @throws RuntimeException if the pattern is invalid
	 */
	public static RegExp compile(String pattern) {
		return compile(pattern, "");
	}

	/**
	 * Creates a regular expression object from a pattern using the given flags.
	 *
	 * @param pattern the Javascript regular expression pattern to compile
	 * @param flags the flags string, containing at most one occurrence of {@code 'g'} ({@link
	 *     #getGlobal()}), {@code 'i'} ({@link #getIgnoreCase()}), or {@code 'm'} ({@link
	 *     #getMultiline()}).
	 * @return a new regular expression
	 * @throws RuntimeException if the pattern or the flags are invalid
	 */
	public static RegExp compile(String pattern, String flags) {
		// Parse flags
		boolean globalFlag = false;
		int javaPatternFlags = Pattern.UNIX_LINES;
		for (char flag : parseFlags(flags)) {
			switch (flag) {
			case 'g':
				globalFlag = true;
				break;

			case 'i':
				javaPatternFlags |= Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
				break;

			case 'm':
				javaPatternFlags |= Pattern.MULTILINE;
				break;

			default:
				throw new IllegalArgumentException("Unknown regexp flag: '" + flag + "'");
			}
		}

		Pattern javaPattern = Pattern.compile(pattern, javaPatternFlags);

		return new JavaRegExp(pattern, javaPattern, globalFlag);
	}

	/**
	 * Parses a flags string as a set of characters. Does not reject unknown flags.
	 *
	 * @param flags the flag string to parse
	 * @return a set of flags
	 * @throws IllegalArgumentException if a flag is duplicated
	 */
	private static Set<Character> parseFlags(String flags) {
		Set<Character> flagsSet = new HashSet<>(flags.length());
		for (int flagIndex = 0; flagIndex < flags.length(); flagIndex++) {
			char flag = flags.charAt(flagIndex);
			if (!flagsSet.add(flag)) {
				throw new IllegalArgumentException("Flag cannot be specified twice: '"
						+ flag + "'");
			}
		}
		return flagsSet;
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
	public static String quote(String input) {
		return Pattern.quote(input);
	}

	@Override
	public MatchResult exec(String input) {
		// Start the search at lastIndex if the global flag is true.
		int searchStartIndex = (globalFlag) ? lastIndex : 0;

		Matcher matcher;
		if (input == null || searchStartIndex < 0 || searchStartIndex > input.length()) {
			// Avoid exceptions: Javascript is more tolerant than Java
			matcher = null;
		} else {
			matcher = pattern.matcher(input);
			if (!matcher.find(searchStartIndex)) {
				matcher = null;
			}
		}

		if (matcher != null) {
			// Match: create a result

			// Retrieve the matched groups.
			int groupCount = matcher.groupCount();
			List<String> groups = new ArrayList<>(1 + groupCount);
			for (int group = 0; group <= groupCount; group++) {
				groups.add(matcher.group(group));
			}

			if (globalFlag) {
				lastIndex = matcher.end();
			}

			return new JavaMatchResult(matcher.start(), input, groups);
		} else {
			// No match
			if (globalFlag) {
				lastIndex = 0;
			}
			return null;
		}
	}

	@Override
	public boolean getGlobal() {
		return globalFlag;
	}

	@Override
	public boolean getIgnoreCase() {
		return (pattern.flags() & Pattern.CASE_INSENSITIVE) != 0;
	}

	@Override
	public int getLastIndex() {
		return lastIndex;
	}

	@Override
	public boolean getMultiline() {
		return (pattern.flags() & Pattern.MULTILINE) != 0;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String replace(String input, String replacementRaw) {
		// Replace \ in the replacement with \\ to escape it for Java replace.
		String replacement =
				REPLACEMENT_BACKSLASH.matcher(replacementRaw)
						.replaceAll(REPLACEMENT_BACKSLASH_FOR_JAVA);

		// Replace the Javascript-ese $& in the replacement with Java-ese $0, but
		// watch out for $$&, which should stay $$&, to be changed to \$& below.
		replacement =
				REPLACEMENT_DOLLAR_AMPERSAND
						.matcher(replacement)
						.replaceAll(REPLACEMENT_DOLLAR_AMPERSAND_FOR_JAVA);

		// Test for Javascript-ese $` and $', which we do not support in the pure
		// Java version.
		if (REPLACEMENT_DOLLAR_APOSTROPHE.matcher(replacement).find()) {
			throw new UnsupportedOperationException("$` and $' replacements are not supported");
		}

		// Replace the Javascript-ese $$ in the replacement with Java-ese \$.
		replacement =
				REPLACEMENT_DOLLAR_DOLLAR
						.matcher(replacement)
						.replaceAll(REPLACEMENT_DOLLAR_DOLLAR_FOR_JAVA);

		return globalFlag
				? pattern.matcher(input).replaceAll(replacement)
				: pattern.matcher(input).replaceFirst(replacement);
	}

	@Override
	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

	@Override
	public boolean test(String input) {
		return exec(input) != null;
	}
}