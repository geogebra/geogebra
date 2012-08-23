package geogebra.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A default implementation of the autocomplete dictionary. This implementation
 * is based upon the TreeSet collection class to provide quick lookups and
 * default sorting. All lookups are case insensitive!
 */
public class LowerCaseDictionary extends HashMap<String, String> implements
		AutoCompleteDictionary {

	private static final long serialVersionUID = 1L;

	private TreeSet<String> treeSet = new TreeSet<String>();

	private NormalizerMinimal normalizer;

	/**
	 * constructor
	 * 
	 * @param normalizer
	 *            normalizer used for dict entries
	 */
	public LowerCaseDictionary(NormalizerMinimal normalizer) {
		this.normalizer = normalizer;
	}

	/**
	 * Adds an entry to the dictionary.
	 * 
	 * @param s
	 *            The string to add to the dictionary.
	 */
	public void addEntry(final String s) {
		String lowerCase = normalizer.transform(s);
		put(lowerCase, s);

		// store lowerCase in tree
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
	public boolean removeEntry(String s) {
		String lowerCase = s.toLowerCase();
		remove(lowerCase);
		return treeSet.remove(lowerCase);
	}

	public Iterator<String> getIterator() {
		return treeSet.iterator();
	}

	/**
	 * Perform a lookup. This routine returns the closest matching string that
	 * completely starts with the given string, or null if none is found. Note:
	 * the lookup is NOT case sensitive.
	 * 
	 * @param curr
	 *            The string to use as the base for the lookup.
	 * @return curr The closest matching string that completely contains the
	 *         given string.
	 */
	public String lookup(final String curr) {
		if (curr == null || "".equals(curr)) {
			return null;
		}

		String currLowerCase = curr.toLowerCase();
		try {
			SortedSet<String> tailSet = treeSet.tailSet(currLowerCase);
			if (tailSet != null) {
				String firstObj = tailSet.first();
				if (firstObj != null) {
					String first = firstObj;
					if (first.startsWith(currLowerCase)) {
						String ret = get(first);
						return ret;
					}
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
	public List<String> getCompletions(final String curr) {
		if (curr == null || "".equals(curr))
			return null;

		String currLowerCase = normalizer.transform(curr);
		try {
			SortedSet<String> tailSet = treeSet.tailSet(currLowerCase);
			ArrayList<String> completions = new ArrayList<String>();
			Iterator<String> compIter = tailSet.iterator();
			while (compIter.hasNext()) {
				String comp = compIter.next();
				if (!comp.startsWith(currLowerCase)) {
					break;
				}
				completions.add(get(comp));
			}
			if (completions.isEmpty()) {
				return null;
			}
			return completions;
		} catch (Exception e) {
			return null;
		}
	}

	public List<String> getCompletionsKorean(final String curr) {
		if (curr == null || "".equals(curr)) {
			return null;
		}

		ArrayList<String> completions = new ArrayList<String>();
		String koreanCurr = Korean.flattenKorean(curr);
		Iterator<String> it = getIterator();
		while (it.hasNext()) {
			String str = it.next();

			if (Korean.flattenKorean(str).startsWith(koreanCurr)) {
				completions.add(str);
			}
		}

		return completions.isEmpty() ? null : completions;
	}
}