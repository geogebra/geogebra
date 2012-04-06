package geogebra.common.kernel.prover;

import java.util.TreeMap;
import java.util.Iterator;

/**
 * A simple class for terms which are a products of potences of variables.
 * 
 * @author Simon Weitzhofer
 * 
 */
public class Term implements Comparable<Term> {
	private TreeMap<FreeVariable, Integer> variables;

	/**
	 * creates the 1 term
	 */
	public Term() {
		variables = new TreeMap<FreeVariable, Integer>();
	}

	/**
	 * Copies a term
	 * 
	 * @param t
	 *            the term to copy
	 */
	public Term(final Term t) {
		variables = new TreeMap<FreeVariable, Integer>(t.getTerm());
	}

	private Term(final TreeMap<FreeVariable, Integer> variables) {
		this.variables = variables;
	}

	/**
	 * Creates a term which consist only of one variable
	 * 
	 * @param variable
	 *            the variable
	 */
	public Term(final FreeVariable variable) {
		variables = new TreeMap<FreeVariable, Integer>();
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
	public Term(final FreeVariable variable, final int exponent) {
		variables = new TreeMap<FreeVariable, Integer>();
		variables.put(variable, exponent);
	}

	/**
	 * Calculates the product of the term and another term
	 * 
	 * @param term
	 *            the other term
	 * @return the product
	 */
	public Term times(final Term term) {
		TreeMap<FreeVariable, Integer> productTerm = new TreeMap<FreeVariable, Integer>(
				variables);

		TreeMap<FreeVariable, Integer> variables2 = term.getTerm();
		Iterator<FreeVariable> it = term.getTerm().keySet().iterator();
		while (it.hasNext()) {
			FreeVariable vp = it.next();
			if (variables.containsKey(vp)) {
				productTerm.put(vp, variables.get(vp) + variables2.get(vp));
			} else {
				productTerm.put(vp, variables2.get(vp));
			}
		}
		return new Term(productTerm);
	}

	/**
	 * Getter for the map containing the variables and the exponent
	 * 
	 * @return the map
	 */
	public TreeMap<FreeVariable, Integer> getTerm() {
		return variables;
	}

	/**
	 * Gets the variable with the highest order
	 * 
	 * @return the variable with the highest order
	 */
	public FreeVariable getHighestVariable() {
		return variables.lastKey();
	}

	public int compareTo(Term o) {
		if (o.getTerm().isEmpty()) {
			if (variables.isEmpty()) {
				return 0;
			}
			return 1;
		}
		if (variables.isEmpty()) {
			return -1;
		}
		int compare = variables.lastKey().compareTo(o.getHighestVariable());
		if (compare == 0) {
			return variables.get(variables.lastKey()).compareTo(
					o.getTerm().get(variables.lastKey()));
		}
		return compare;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Term) {
			return this.compareTo((Term) o) == 0;
		}
		return super.equals(o);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<FreeVariable> it = variables.descendingKeySet().iterator();
		while (it.hasNext()) {
			FreeVariable fv = it.next();
			sb.append(fv);
			sb.append('^');
			sb.append(variables.get(fv));
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return variables.hashCode();
	}

}
