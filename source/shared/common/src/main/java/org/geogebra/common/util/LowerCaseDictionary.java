package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.himamis.retex.editor.share.model.Korean;

/**
 * A default implementation of the autocomplete dictionary. This implementation
 * is based upon the TreeSet collection class to provide quick lookups and
 * default sorting. All lookups are case-insensitive!
 */
public class LowerCaseDictionary extends HashMap<String, String>
		implements AutoCompleteDictionary {

	private static final long serialVersionUID = 1L;

	private final TreeSet<String> treeSet;

	/**
	 * constructor
	 */
	public LowerCaseDictionary() {
		treeSet = new TreeSet<>();
	}

	/**
	 * copy constructor
	 *
	 * @param dict the dictionary to copy from.
	 */
	public LowerCaseDictionary(LowerCaseDictionary dict) {
		this.treeSet = new TreeSet<>(dict.treeSet);
		putAll(dict);
	}

	private static @CheckForNull String greatestCommonPrefix(String possiblyNull,
			String notNull) {
		if (possiblyNull == null) {
			return null;
		}
		int minLength = Math.min(possiblyNull.length(), notNull.length());
		for (int i = 0; i < minLength; i++) {
			if (possiblyNull.charAt(i) != notNull.charAt(i)) {
				return possiblyNull.substring(0, i);
			}
		}
		return possiblyNull.substring(0, minLength);
	}

	/**
	 * Adds an entry to the dictionary.
	 *
	 * @param s
	 *            The string to add to the dictionary.
	 */
	@Override
	public void addEntry(final String s) {
		String lowerCase = StringUtil.removeAccents(s);
		put(lowerCase, s);

		treeSet.add(lowerCase);
	}

	/**
	 * Removes an entry from the dictionary.
	 *
	 * @param s
	 *            The string to remove to the dictionary.
	 * @return True if successful, false if the string is not contained or
	 *         cannot be removed.
	 */
	@Override
	public boolean removeEntry(String s) {
		String lowerCase = s.toLowerCase(Locale.ROOT);
		remove(lowerCase);
		return treeSet.remove(lowerCase);
	}

	@Override
	public @Nonnull Iterator<String> iterator() {
		return treeSet.iterator();
	}

	/**
	 * Perform a lookup. This routine returns the closest matching string that
	 * completely starts with the given string, or null if none is found. Note:
	 * the lookup is NOT case-sensitive.
	 *
	 * @param curr
	 *            The string to use as the base for the lookup.
	 * @return curr The closest matching string that completely contains the
	 *         given string.
	 */
	@Override
	public String lookup(final String curr) {
		if (StringUtil.empty(curr)) {
			return null;
		}

		String currLowerCase = curr.toLowerCase(Locale.ROOT);
		try {
			SortedSet<String> tailSet = treeSet.tailSet(currLowerCase);
			String firstObj = tailSet.first();
			if (firstObj != null) {
				if (firstObj.startsWith(currLowerCase)) {
					return get(firstObj);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * Find all possible completions for the string curr; return null if none
	 * exists
	 *
	 * @param curr
	 *            The string to use as the base for the lookup
	 * @return a list of strings containing all completions or null if none
	 *         exists
	 */
	@Override
	public List<MatchedString> getCompletions(final String curr) {
		if (StringUtil.empty(curr)) {
			return null;
		}

		String currLowerCase = StringUtil.removeAccents(curr);
		try {
			ArrayList<MatchedString> completions = new ArrayList<>();
			int initialMatches = 0;
			for (String cmd: treeSet) {
				int index = cmd.indexOf(currLowerCase);
				if (index > -1) {
					String entry = get(cmd);
					int matchTo = getOriginalLength(entry, index + curr.length());
					if (index == 0) {
						completions.add(initialMatches++, new MatchedString(entry, index, matchTo));
					} else {
						if (cmd.length() != entry.length()) {
							index = getOriginalLength(entry, index);
						}
						completions.add(new MatchedString(entry, index, matchTo));
					}
				}
			}
			if (completions.isEmpty()) {
				return null;
			}
			return completions;
		} catch (Exception e) {
			return null;
		}
	}

	private int getOriginalLength(String original, int normalizedLength) {
		int originalLength = 0;
		int normalizedPrefix = 0;
		while (normalizedPrefix < normalizedLength) {
			normalizedPrefix +=  StringUtil.removeAccents(
					String.valueOf(original.charAt(originalLength))).length();
			originalLength++;
		}
		return originalLength;
	}

	/**
	 * Calculate the greatest prefix common to curr. Then return list of all
	 * elements matching this prefix. Return null if none exists
	 *
	 * @param curr
	 *            The string to use as the base for the lookup
	 * @param completions
	 *            output array
	 * @return the greatest common prefix
	 */
	public String setMatchingGreatestPrefix(final String curr,
			ArrayList<String> completions) {
		if (StringUtil.empty(curr)) {
			return "";
		}

		String prefixLowerCase = getGreatestCommonPrefix(
				StringUtil.removeAccents(curr));
		if (StringUtil.empty(prefixLowerCase)) {
			return ""; // no common prefix
		}

		try {
			SortedSet<String> tailSet = treeSet.tailSet(prefixLowerCase);
			for (String comp : tailSet) {
				if (!comp.startsWith(prefixLowerCase)) {
					break;
				}
				completions.add(get(comp));
			}
			return prefixLowerCase;
		} catch (Exception e) {
			return "";
		}
	}

	private @CheckForNull String getGreatestCommonPrefix(final String curr) {

		String prefixBefore = greatestCommonPrefix(treeSet.floor(curr), curr);
		String prefixAfter = greatestCommonPrefix(treeSet.ceiling(curr), curr);

		if (prefixBefore == null) {
			return prefixAfter;
		}

		if (prefixAfter == null) {
			return prefixBefore;
		}

		if (prefixBefore.length() > prefixAfter.length()) {
			return prefixBefore;
		}

		return prefixAfter;
	}

	@Override
	public List<MatchedString> getCompletionsKorean(final String curr) {
		if (StringUtil.empty(curr)) {
			return null;
		}

		ArrayList<MatchedString> completions = new ArrayList<>();
		String koreanCurr = Korean.flattenKorean(curr);
		for (String str : treeSet) {
			if (Korean.flattenKorean(str).startsWith(koreanCurr)) {
				completions.add(new MatchedString(Korean.unflattenKorean(str),
						0, curr.length()));
			}
		}

		return completions.isEmpty() ? null : completions;
	}

	@Override
	public void clear() {
		super.clear();
		this.treeSet.clear();
	}

	/**
	 * @return all commands in the dictionary
	 */
	public ArrayList<String> getAllCommands() {
		ArrayList<String> ret = new ArrayList<>();
		for (String key : treeSet) {
			ret.add(get(key));
		}

		if (ret.isEmpty()) {
			return null;
		}

		return ret;
	}
}