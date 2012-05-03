package geogebra.common.kernel.prover;

import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedMap;
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
	private HashMap<Term, Integer> comparisons=new HashMap<Term, Integer>();

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
		if (this==o){
			return 0;
		}

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
		
		FreeVariable variablesLastKey=variables.lastKey(),
				tLastKey=t.lastKey();

		int compare = variablesLastKey.compareTo(tLastKey);

		if (compare == 0) {
			compare = variables.get(variablesLastKey).compareTo(t.get(tLastKey));
		}

		if (compare != 0) {
			return compare;
		}
		
		do {
			SortedMap<FreeVariable, Integer> variablesSub = variables.headMap(variablesLastKey);
			SortedMap<FreeVariable, Integer> oSub = t.headMap(tLastKey);
			if (variablesSub.isEmpty()) {
				if (oSub.isEmpty()) {
					return 0;
				}
				return -1;
			}
			if (oSub.isEmpty()) {
				return 1;
			}
			variablesLastKey=variablesSub.lastKey();
			tLastKey=oSub.lastKey();
			compare = variablesLastKey.compareTo(tLastKey);
			if (compare == 0) {
				compare = variablesSub.get(variablesLastKey).compareTo(
						oSub.get(tLastKey));
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
		StringBuilder sb = new StringBuilder("");
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

	/**
	 * The set of variables in this term
	 * @return the set of variables
	 */
	public HashSet<FreeVariable> getVars() {
		HashSet<FreeVariable> v = new HashSet<FreeVariable>();
		Iterator<FreeVariable> it = variables.keySet().iterator();
		while (it.hasNext()) {
			FreeVariable fv  = it.next();
			v.add(fv);
		}
		return v;
	}
	
	@Override
	public int hashCode() {
		if (variables.isEmpty()){
			return 0;
		}
		return variables.firstKey().hashCode()>>variables.lastKey().hashCode();
	}

}
