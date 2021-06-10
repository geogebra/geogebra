package org.geogebra.common.util;

import java.util.Arrays;
import java.util.TreeSet;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.parser.ParserInfo;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Does several replacements to simplify parsing
 * - replace || with | | where needed
 * - replace .. with ellipsis
 * - handle , as decimal or thousand separator based on locale
 */

 public class ParserPreprocessor {
	private static final String BASIC_OPERANDS = ".+-*/ ";
	private static final TreeSet<Character> SPLITTERS_BACKWARDS = new TreeSet<>(
			Arrays.asList('*', '/', '^', '=',
					Unicode.SUPERSCRIPT_0, Unicode.SUPERSCRIPT_1,
					Unicode.SUPERSCRIPT_2, Unicode.SUPERSCRIPT_3,
					Unicode.SUPERSCRIPT_4, Unicode.SUPERSCRIPT_5,
					Unicode.SUPERSCRIPT_6, Unicode.SUPERSCRIPT_7,
					Unicode.SUPERSCRIPT_8, Unicode.SUPERSCRIPT_9,
					Unicode.SUPERSCRIPT_MINUS));
	private static final TreeSet<Character> SPLITTERS_FORWARDS = new TreeSet<>(
			Arrays.asList(Unicode.SQUARE_ROOT, '+', '-',
					'*', '/', '^', '='));
	private StringBuilder sb;
	private int topLevelBars;
	private boolean forward;
	private int braketLevel;
	private int bars;
	private final ParserInfo info;

	public ParserPreprocessor(ParserInfo info) {
		this.info = info;
	}

	/**
	 *
	 * @param input
	 *            input string
	 * @return preprocessed string
	 */
	public String preprocess(String input) {
		sb = new StringBuilder();

		// first we iterate from left to right, and then backward
		topLevelBars = 0;
		scanForwards(input);
		scanBackwards(sb.reverse().toString());
		if (!info.isInputBox()) {
			replaceCommasStandard();
		}
		return sb.reverse().toString();
	}

	private void scanForwards(String input) {
		forward = true;
		scan(input, SPLITTERS_FORWARDS);
	}

	private void scanBackwards(String input) {
		forward = false;
		if (MyDouble.isOdd(topLevelBars)) {
			putLastBarToBrackets();
		}
		sb = new StringBuilder();
		scan(input, SPLITTERS_BACKWARDS);
	}

	private void putLastBarToBrackets() {
		int lPos = sb.lastIndexOf("|");
		sb.replace(lPos, lPos + 1, ")");
		sb.insert(0, "(");
	}

	private void scan(String parseString,
			TreeSet<Character> splitters) {
		// When we have <splitter> || , we know that we should separate
		// these bars (i. e., we want absolute value, not OR)
		String ignoredIndices = StringUtil.ignoreIndices(parseString);
		boolean comment = false;
		bars = 0;
		Character lastNonWhitespace = ' ';
		braketLevel = 0;

		for (int i = 0; i < ignoredIndices.length(); i++) {
			Character ch = ignoredIndices.charAt(i);
			if (isEllipsis(comment, ch)) {
				sb.setLength(sb.length() - 1);
				sb.append(Unicode.ELLIPSIS);
			} else {
				char write = parseString.charAt(i);
				sb.append(write == Unicode.MICRO ? Unicode.mu : write);
			}

			if (ch.equals(',') && info.isInputBox() && braketLevel == 0) {
				handleComma(i);
			}

			if (StringUtil.isWhitespace(ch)
					|| (comment && !ch.equals('"'))) {
				continue;
			}

			if (ch.equals('"')) {
				comment = !comment;
			} else {
				preprocessBrackets(ch);
			}

			if (ch.equals('|')) {
				preprocessBars(splitters, ignoredIndices, lastNonWhitespace, i);
			}

			lastNonWhitespace = ch;
		}
	}

	private void preprocessBars(TreeSet<Character> splitters, String ignoredIndices,
			Character lastNonWhitespace, int i) {
		// We separate bars if the previous symbol was in splitters
		// or we have ||| and there were an odd number of bars so
		// far
		int length = ignoredIndices.length();
		if (i == 0
				|| (MyDouble.isOdd(bars) && i < length - 2
				&& ignoredIndices.charAt(i + 1) == '|'
				&& ignoredIndices.charAt(i + 2) == '|')
				|| (i < length - 1
				&& ignoredIndices.charAt(i + 1) == '|'
				&& splitters.contains(lastNonWhitespace))) {
			sb.append(' ');
		}
		bars++;
		if (braketLevel == 0) {
			topLevelBars++;
		}
	}

	private void preprocessBrackets(Character ch) {
		if (isOpenBracket(ch)) {
			braketLevel++;
		} else if (isCloseBracket(ch)) {
			braketLevel--;
		}
	}

	private boolean isCloseBracket(Character ch) {
		return ch.equals('}') || ch.equals(')') || ch.equals(']');
	}

	private boolean isOpenBracket(Character ch) {
		return ch.equals('{') || ch.equals('(') || ch.equals('[');
	}

	private boolean isEllipsis(boolean comment, Character ch) {
		return !comment && forward && sb.length() > 0 && ch.equals('.')
				&& (sb.charAt(sb.length() - 1) == '.'
				|| sb.charAt(sb.length()
				- 1) == Unicode.ELLIPSIS);
	}

	private void replaceCommasStandard() {
		for (int i = 0; i < sb.length(); i++) {
				char currentChar = sb.charAt(i);
			if (currentChar == ',') {
				handleComma(i);
			} else if (nonDigitOrOperand(currentChar)) {
				break;
			}
		}
	}

	private void handleComma(int index) {
		if (info.isDecimalComma()) {
			sb.replace(index, index + 1, ".");
		} else {
			sb.delete(index, index + 1);
		}
	}

	private boolean nonDigitOrOperand(char currentChar) {
		return !Character.isDigit(currentChar)
				&& BASIC_OPERANDS.indexOf(currentChar) == -1;
	}
}
