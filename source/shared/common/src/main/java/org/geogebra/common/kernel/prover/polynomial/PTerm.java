package org.geogebra.common.kernel.prover.polynomial;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A simple class for terms which are a products of potences of variables.
 * 
 * @author Simon Weitzhofer
 * 
 */
public class PTerm implements Comparable<PTerm> {
	private TreeMap<PVariable, Integer> variables;

	/**
	 * creates the 1 term
	 */
	public PTerm() {
		variables = new TreeMap<>();
	}

	/**
	 * Copies a term
	 * 
	 * @param t
	 *            the term to copy
	 */
	public PTerm(final PTerm t) {
		variables = new TreeMap<>(t.getTerm());
	}

	/**
	 * Creates a Term out of a map from variables to integers. The term is the
	 * product of the variables raised by the corresponding integers.
	 * 
	 * @param variables
	 *            The map
	 */
	PTerm(final TreeMap<PVariable, Integer> variables) {
		this.variables = variables;
	}

	/**
	 * Creates a term which consist only of one variable
	 * 
	 * @param variable
	 *            the variable
	 */
	public PTerm(final PVariable variable) {
		variables = new TreeMap<>();
		variables.put(variable, 1);
	}

	/**
	 * Creates a term variable^exponent
	 * 
	 * @param variable
	 *            the variable
	 * @param exponent
	 *            the exponent
	 */
	public PTerm(final PVariable variable, final int exponent) {
		variables = new TreeMap<>();
		variables.put(variable, exponent);
	}

	/**
	 * Calculates the product of the term and another term
	 * 
	 * @param term
	 *            the other term
	 * @return the product
	 */
	public PTerm times(final PTerm term) {
		TreeMap<PVariable, Integer> productTerm = new TreeMap<>(
				variables);

		TreeMap<PVariable, Integer> variables2 = term.getTerm();
		Iterator<PVariable> it = term.getTerm().keySet().iterator();
		while (it.hasNext()) {
			PVariable vp = it.next();
			if (variables.containsKey(vp)) {
				productTerm.put(vp, variables.get(vp) + variables2.get(vp));
			} else {
				productTerm.put(vp, variables2.get(vp));
			}
		}
		return new PTerm(productTerm);
	}

	/**
	 * Getter for the map containing the variables and the exponent
	 * 
	 * @return the map
	 */
	public TreeMap<PVariable, Integer> getTerm() {
		return variables;
	}

	/**
	 * Gets the variable with the highest order
	 * 
	 * @return the variable with the highest order
	 */
	public PVariable getHighestVariable() {
		return variables.lastKey();
	}

	@Override
	public int compareTo(PTerm o) {
		if (this == o) {
			return 0;
		}

		TreeMap<PVariable, Integer> t = o.getTerm();
		if (t.isEmpty()) {
			if (variables.isEmpty()) {
				return 0;
			}
			return 1;
		}
		if (variables.isEmpty()) {
			return -1;
		}

		PVariable variablesLastKey = variables.lastKey(), tLastKey = t.lastKey();

		int compare = variablesLastKey.compareTo(tLastKey);

		if (compare == 0) {
			compare = variables.get(variablesLastKey)
					.compareTo(t.get(tLastKey));
		}

		if (compare != 0) {
			return compare;
		}

		do {
			SortedMap<PVariable, Integer> variablesSub = variables
					.headMap(variablesLastKey);
			SortedMap<PVariable, Integer> oSub = t.headMap(tLastKey);
			if (variablesSub.isEmpty()) {
				if (oSub.isEmpty()) {
					return 0;
				}
				return -1;
			}
			if (oSub.isEmpty()) {
				return 1;
			}
			variablesLastKey = variablesSub.lastKey();
			tLastKey = oSub.lastKey();
			compare = variablesLastKey.compareTo(tLastKey);
			if (compare == 0) {
				compare = variablesSub.get(variablesLastKey)
						.compareTo(oSub.get(tLastKey));
			}
		} while (compare == 0);

		return compare;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PTerm) {
			return this.compareTo((PTerm) o) == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<PVariable, Integer>> it = variables.entrySet().iterator();
		while (it.hasNext()) {
			Entry<PVariable, Integer> entry = it.next();
			PVariable fv = entry.getKey();
			sb.append("*");
			sb.append(fv);
			int power = entry.getValue();
			if (power > 1) {
				sb.append("^");
				sb.append(power);
			}
		}
		if (sb.length() > 0) {
			return sb.substring(1); // removing first "*" character
		}
		return "";
	}

	/**
	 * Exports the term into LaTeX
	 * 
	 * @return LaTeX formatted polynomial
	 */
	public String toTeX() {
		StringBuilder sb = new StringBuilder("");
		Iterator<Entry<PVariable, Integer>> it = variables.entrySet().iterator();
		while (it.hasNext()) {
			Entry<PVariable, Integer> entry = it.next();
			PVariable fv = entry.getKey();
			sb.append(fv.toTeX());
			int power = entry.getValue();
			if (power > 1) {
				sb.append("^{" + power + "}");
			}
		}
		return sb.toString();
	}

	/**
	 * The set of variables in this term
	 * 
	 * @return the set of variables
	 */
	public HashSet<PVariable> getVars() {
		HashSet<PVariable> v = new HashSet<>();
		Iterator<PVariable> it = variables.keySet().iterator();
		while (it.hasNext()) {
			PVariable fv = it.next();
			v.add(fv);
		}
		return v;
	}

	@Override
	public int hashCode() {
		if (variables.isEmpty()) {
			return 0;
		}
		return variables.firstKey().hashCode() >> variables.lastKey()
				.hashCode();
	}

	/**
	 * Test whether the term f is a multiple of term g
	 * 
	 * @param f
	 *            the dividend
	 * @param g
	 *            the divisor
	 * @return true if g divides f and false otherwise
	 */
	public static boolean divides(final PTerm f, final PTerm g) {
		TreeMap<PVariable, Integer> termG = g.getTerm();
		Iterator<Entry<PVariable, Integer>> itG = termG.entrySet().iterator();
		while (itG.hasNext()) {
			Entry<PVariable, Integer> entry = itG.next();
			PVariable var = entry.getKey();
			Integer powF = f.getTerm().get(var);
			if (powF == null || powF.intValue() < entry.getValue().intValue()) {
				return false;
			}
		}
		return true;
	}

}
