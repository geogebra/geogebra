package geogebra.common.kernel.prover;

import geogebra.common.main.AbstractApplication;

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

	public Term gcd(Term t){
		TreeMap<FreeVariable, Integer> result = new TreeMap<FreeVariable, Integer>();
		TreeMap<FreeVariable, Integer> tTM=t.getTerm();
		Iterator<FreeVariable> it = variables.keySet().iterator();
		while (it.hasNext()){
			FreeVariable var=it.next();
			if (tTM.containsKey(var)){
				result.put(var, Math.min(Math.abs(variables.get(var)),Math.abs(tTM.get(var))));
			}
		}
		return new Term(result);
	}
	public int compareTo(Term o) {
		TreeMap<FreeVariable, Integer> t=o.getTerm();
		if (t.isEmpty()) {
			if (variables.isEmpty()) {
				return 0;
			}
			return 1;
		}
		if (variables.isEmpty()) {
			return -1;
		}

		int compare = variables.lastKey().compareTo(t.lastKey());

		if (compare == 0) {
			compare = variables.get(variables.lastKey()).compareTo(t.get(t.lastKey()));
		}

		if (compare != 0) {
			return compare;
		}
		
		TreeMap<FreeVariable, Integer> variablesCopy = new TreeMap<FreeVariable, Integer>(
				variables);
		TreeMap<FreeVariable, Integer> oCopy = new TreeMap<FreeVariable, Integer>(
				t);

		do {
			variablesCopy.remove(variablesCopy.lastKey());
			oCopy.remove(oCopy.lastKey());
			if (variablesCopy.isEmpty()) {
				if (oCopy.isEmpty()) {
					return 0;
				}
				return -1;
			}
			if (oCopy.isEmpty()) {
				return 1;
			}
			compare = variablesCopy.lastKey().compareTo(oCopy.lastKey());
			if (compare == 0) {
				compare = variablesCopy.get(variablesCopy.lastKey()).compareTo(
						oCopy.get(oCopy.lastKey()));
			}
		} while (compare == 0);

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
		Iterator<FreeVariable> it = variables.keySet().iterator();
		while (it.hasNext()) {
			FreeVariable fv = it.next();
			sb.append("*" + fv);
			int power = variables.get(fv);
			if (power > 1)
				sb.append("^" + power);
		}
		return sb.substring(1); // removing first "*" character
	}
	
	@Override
	public int hashCode() {
		return variables.hashCode();
	}

}
