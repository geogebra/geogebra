package org.geogebra.common.kernel.prover.polynomial;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.debug.Log;

/**
 * This is a simple polynomial class for polynomials with arbitrary many
 * variables.
 * 
 * @author Simon Weitzhofer
 * 
 */
public class PPolynomial implements Comparable<PPolynomial> {
	private TreeMap<PTerm, BigInteger> terms;

	/**
	 * Creates the 0 polynomial
	 */
	public PPolynomial() {
		terms = new TreeMap<>();
	}

	/**
	 * Copies a polynomial
	 * 
	 * @param poly
	 *            the polynomial to copy
	 */
	public PPolynomial(final PPolynomial poly) {
		terms = new TreeMap<>(poly.getTerms());
	}

	private PPolynomial(final TreeMap<PTerm, BigInteger> terms) {
		this.terms = terms;
	}

	/**
	 * Getter for the map which contains the terms and the according
	 * coefficients.
	 * 
	 * @return the map
	 */
	public TreeMap<PTerm, BigInteger> getTerms() {
		return terms;
	}

	/**
	 * Creates a constant polynomial.
	 * 
	 * @param coeff
	 *            the constant
	 */
	public PPolynomial(final BigInteger coeff) {
		this(coeff, new PTerm());
	}

	/**
	 * Creates a constant polynomial.
	 * 
	 * @param coeff
	 *            the constant
	 */
	public PPolynomial(final long coeff) {
		this(new BigInteger(Long.toString(coeff)), new PTerm());
	}
	
	/**
	 * Creates a polynomial which contains only one variable
	 * 
	 * @param fv
	 *            the variable
	 */
	public PPolynomial(final PVariable fv) {
		this();
		terms.put(new PTerm(fv), BigInteger.ONE);
	}

	/**
	 * Creates the polynomial coeff*variable
	 * 
	 * @param coeff
	 *            the coefficient
	 * @param variable
	 *            the variable
	 */
	public PPolynomial(final BigInteger coeff, final PVariable variable) {
		this();
		if (coeff != BigInteger.ZERO)
			terms.put(new PTerm(variable), coeff);
	}

	/**
	 * Creates the polynomial coeff*(variable^power)
	 * 
	 * @param coeff
	 *            The coefficient
	 * @param variable
	 *            the variable
	 * @param power
	 *            the exponent
	 */
	public PPolynomial(final BigInteger coeff, final PVariable variable,
			final int power) {
		this();
		if (coeff != BigInteger.ZERO)
			terms.put(new PTerm(variable, power), coeff);
	}

	/**
	 * Creates the polynomial which contains only one term
	 * 
	 * @param t
	 *            the term
	 */
	public PPolynomial(final PTerm t) {
		this();
		terms.put(t, BigInteger.ONE);
	}

	/**
	 * Creates the polynomial coeff*t
	 * 
	 * @param coeff
	 *            the coefficient
	 * @param t
	 *            the term
	 */
	public PPolynomial(final BigInteger coeff, final PTerm t) {
		this();
		if (coeff != BigInteger.ZERO)
			terms.put(t, coeff);
	}

	/**
	 * Returns the sum of the polynomial plus another polynomial.
	 * 
	 * @param poly
	 *            the polynomial to add
	 * @return the sum
	 */
	public PPolynomial add(final PPolynomial poly) {
		TreeMap<PTerm, BigInteger> result = new TreeMap<>(terms);
		TreeMap<PTerm, BigInteger> terms2 = poly.getTerms();
		Iterator<Entry<PTerm, BigInteger>> it = terms2.entrySet().iterator();
		while (it.hasNext()) {
			Entry<PTerm, BigInteger> entry = it.next();
			PTerm t = entry.getKey();
			if (terms.containsKey(t)) {
				BigInteger coefficient = terms.get(t).add(terms2.get(t));
				if (coefficient == BigInteger.ZERO) {
					result.remove(t);
				} else {
					result.put(t, terms.get(t).add(entry.getValue()));
				}
			} else {
				result.put(t, entry.getValue());
			}
		}
		return new PPolynomial(result);
	}

	/**
	 * Calculates the additive inverse of the polynomial
	 * 
	 * @return the negation of the polynomial
	 */
	public PPolynomial negate() {
		TreeMap<PTerm, BigInteger> result = new TreeMap<>();
		Iterator<Entry<PTerm, BigInteger>> it = terms.entrySet().iterator();
		while (it.hasNext()) {
			Entry<PTerm, BigInteger> entry = it.next();
			PTerm t = entry.getKey();
			result.put(t, BigInteger.ZERO.subtract(entry.getValue()));
		}
		return new PPolynomial(result);
	}

	/**
	 * Subtracts another polynomial
	 * 
	 * @param poly
	 *            the polynomial which is subtracted
	 * @return the difference
	 */
	public PPolynomial subtract(final PPolynomial poly) {
		return add(poly.negate());
	}

	/**
	 * Multiplies the polynomial with another polynomial
	 * 
	 * @param poly
	 *            the polynomial which is multiplied
	 * @return the product
	 */
	public PPolynomial multiply(final PPolynomial poly) {
		TreeMap<PTerm, BigInteger> result = new TreeMap<>();
		TreeMap<PTerm, BigInteger> terms2 = poly.getTerms();
		Iterator<Entry<PTerm, BigInteger>> it1 = terms.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<PTerm, BigInteger> entry1 = it1.next();
			PTerm t1 = entry1.getKey();
			Iterator<Entry<PTerm, BigInteger>> it2 = terms2.entrySet()
					.iterator();
			while (it2.hasNext()) {
				Entry<PTerm, BigInteger> entry2 = it2.next();
				PTerm t2 = entry2.getKey();
				PTerm product = t1.times(t2);
				BigInteger productCoefficient = entry1.getValue()
						.multiply(entry2.getValue());
				if (result.containsKey(product)) {
					BigInteger sum = result.get(product).add(productCoefficient);
					if (sum == BigInteger.ZERO) {
						result.remove(product);
					} else {
						result.put(product, result.get(product).add(productCoefficient));
					}
				} else {
					result.put(product, productCoefficient);
				}
			}

		}
		return new PPolynomial(result);
	}

	@Override
	public int compareTo(PPolynomial poly) {
		if (this==poly){
			return 0;
		}

		TreeMap<PTerm, BigInteger> polyVars = poly.getTerms();
		if (polyVars.isEmpty()) {
			if (terms.isEmpty()) {
				return 0;
			}
			return 1;
		}
		if (terms.isEmpty()) {
			return -1;
		}
		
		PTerm termsLastKey=terms.lastKey(),
				polyVarsLastKey=polyVars.lastKey();

		int compare = termsLastKey.compareTo(polyVarsLastKey);

		if (compare == 0) {
			compare = terms.get(termsLastKey).compareTo(polyVars.get(polyVarsLastKey));
		}

		if (compare != 0) {
			return compare;
		}
		
		do {
			SortedMap<PTerm, BigInteger> termsSub = terms.headMap(termsLastKey);
			SortedMap<PTerm, BigInteger> oSub = polyVars.headMap(polyVarsLastKey);
			if (termsSub.isEmpty()) {
				if (oSub.isEmpty()) {
					return 0;
				}
				return -1;
			}
			if (oSub.isEmpty()) {
				return 1;
			}
			termsLastKey=termsSub.lastKey();
			polyVarsLastKey=oSub.lastKey();
			compare = termsLastKey.compareTo(polyVarsLastKey);
			if (compare == 0) {
				compare = termsSub.get(termsLastKey).compareTo(
						oSub.get(polyVarsLastKey));
			}
		} while (compare == 0);

		return compare;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<PTerm, BigInteger>> it = terms.entrySet().iterator();
		if (!it.hasNext()) {
			return "0";
		}
		while (it.hasNext()) {
			Entry<PTerm, BigInteger> entry = it.next();
			PTerm t = entry.getKey();
			BigInteger c = entry.getValue();
			if (!t.getTerm().isEmpty()) {
				if (c != BigInteger.ONE)
					sb.append(c + "*");
				sb.append(t);
			}
			else
				sb.append(c);
			sb.append('+');
		}
		String ret = sb.substring(0, sb.length() - 1); // removing closing "+"
		ret = ret.replaceAll("\\+-", "-").replaceAll("-1\\*", "-")
				.replaceAll("\\+1\\*", "+").replaceAll("^1\\*", "");
		return ret;
	}

	/**
	 * Exports the polynomial into LaTeX
	 * @return the LaTeX formatted polynomial
	 */
	public String toTeX() {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<PTerm, BigInteger>> it = terms.entrySet().iterator();
		if (!it.hasNext()) {
			return "0";
		}
		while (it.hasNext()) {
			Entry<PTerm, BigInteger> entry = it.next();
			PTerm t = entry.getKey();
			BigInteger c = entry.getValue();
			if (!t.getTerm().isEmpty()) {
				if (!c.equals(BigInteger.ONE)) {
					// c != -1
					if (!(c.add(BigInteger.ONE)).equals(BigInteger.ZERO)) {
						// c < -1
						if (c.add(BigInteger.ONE).compareTo(BigInteger.ZERO) < 0) {
							if (sb.length() > 0) {
								sb.deleteCharAt(sb.length()-1); // removing last "+"
							}
						} 
						sb.append(c);
					} else {
						// -1
						if (sb.length() > 0) {
							sb.deleteCharAt(sb.length()-1); // removing last "+"
						}
						sb.append('-');
					}
				}
				sb.append(t.toTeX());
			}
			else
				sb.append(c);
			sb.append('+');
		}
		return sb.substring(0, sb.length() - 1); // removing closing "+"
	}

	
	/**
	 * The set of the variables in this polynomial
	 * @return the set of variables
	 */
	public HashSet<PVariable> getVars() {
		HashSet<PVariable> v = new HashSet<>();
		Iterator<PTerm> it = terms.keySet().iterator();
		while (it.hasNext()) {
			PTerm t = it.next();
			v.addAll(t.getVars());
		}
		return v;
	}

	/**
	 * The set of the variables in the given polynomials
	 * @param polys the polynomials
	 * @return the set of variables
	 */
	public static HashSet<PVariable> getVars(PPolynomial[] polys) {
		HashSet<PVariable> v = new HashSet<>();
		int polysLength = 0;
		if (polys != null)
			polysLength = polys.length;
		for (int i=0; i<polysLength; ++i) {
			HashSet<PVariable> vars = polys[i].getVars();
			if (vars != null)
				v.addAll(vars);
		}
		return v;
	}

	/**
	 * The set of the variables in the given polynomials
	 * 
	 * @param polys
	 *            the polynomials
	 * @return the set of variables
	 */
	public static HashSet<PVariable> getVars(Set<PPolynomial> polys) {
		HashSet<PVariable> v = new HashSet<>();
		for (PPolynomial poly : polys) {
			HashSet<PVariable> vars = poly.getVars();
			if (vars != null)
				v.addAll(vars);
		}
		return v;
	}
	
	/**
	 * Creates a comma separated list of the variables in the given polynomials
	 * 
	 * @param polys
	 *            the polynomials
	 * @param extraVars
	 *            (maybe) extra variables (typically substituted variables)
	 * @param free
	 *            filter the query if the variables are free or dependant (or
	 *            any if null)
	 * @param freeVariables
	 *            set of free variables
	 * @return the comma separated list
	 */
	public static String getVarsAsCommaSeparatedString(PPolynomial[] polys,
			HashSet<PVariable> extraVars, Boolean free,
			Set<PVariable> freeVariables) {
		StringBuilder sb = new StringBuilder();
		HashSet<PVariable> vars = getVars(polys);
		if (extraVars != null)
			vars.addAll(extraVars);
		Iterator<PVariable> it = vars.iterator();
		while (it.hasNext()) {
			PVariable fv = it.next();
			if ((free == null) || (free && freeVariables.contains(fv))
					|| (!free && !(freeVariables.contains(fv))))
				sb.append("," + fv);
		}
		if (sb.length()>0)
			return sb.substring(1); // removing first "," character
		return "";
	}
	
	/**
	 * Creates a comma separated list of the given polynomials
	 * @param polys the polynomials
	 * @return the comma separated list
	 */
	public static String getPolysAsCommaSeparatedString(PPolynomial[] polys) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < polys.length; ++i) {
			if (!polys[i].isZero()) { // avoid sending 0 to Giac's eliminate
				sb.append("," + polys[i].toString());
			}
		}
		if (sb.length()>0)
			return sb.substring(1); // removing first "," character
		return "";
	}
	

	/**
	 * Creates a Singular program for creating a ring to work with two
	 * polynomials, and multiply them; adds a closing ";" 
	 * @param ringVariable variable name for the ring in Singular
	 * @param p1 first polynomial
	 * @param p2 second polynomial
	 * @return the Singular program code
	 */
	public String getSingularMultiplication(String ringVariable, PPolynomial p1, PPolynomial p2) {
		String vars = getVarsAsCommaSeparatedString(
				new PPolynomial[] { p1, p2 }, null, null, null);
		if (!"".equals(vars))
			return "ring " + ringVariable + "=0,(" 
				+ vars
				+ "),dp;" // ring definition in Singular
				
				+ "short=0;" // switching off short output
				
				+ "(" + p1.toString() + ")"
				+ "*"
				+ "(" + p2.toString() + ");"; // the multiplication command
		return p1.toString() + "*" + p2.toString() + ";";
	}
	
	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * lying on the same line. 
	 * @param fv1 x-coordinate of the first point
	 * @param fv2 y-coordinate of the first point
	 * @param fv3 x-coordinate of the second point
	 * @param fv4 y-coordinate of the second point
	 * @param fv5 x-coordinate of the third point
	 * @param fv6 y-coordinate of the third point
	 * @return the polynomial
	 */
	public static PPolynomial collinear(PVariable fv1, PVariable fv2, PVariable fv3, 
			PVariable fv4, PVariable fv5, PVariable fv6) {
		Log.trace("Setting up equation for collinear points " +
			"(" + fv1 + "," + fv2 + "), " +
			"(" + fv3 + "," + fv4 + ") and " +
			"(" + fv5 + "," + fv6 + ")");
		// a*d-b*c:
		PPolynomial a = new PPolynomial(fv1);
		PPolynomial b = new PPolynomial(fv2);
		PPolynomial c = new PPolynomial(fv3);
		PPolynomial d = new PPolynomial(fv4);
		PPolynomial e = new PPolynomial(fv5);
		PPolynomial f = new PPolynomial(fv6);
		
		PPolynomial ret = a.multiply(d).subtract(b.multiply(c))
				// + e*(b-d)
				.add(e.multiply(b.subtract(d)))
				// - f*(a-c)
				.subtract(f.multiply(a.subtract(c)));
		return ret;
	}
	
	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * are perpendicular, i.e. AB is perpendicular to CD. 
	 * @param v1 x-coordinate of the first point (A)
	 * @param v2 y-coordinate of the first point (A)
	 * @param v3 x-coordinate of the second point (B)
	 * @param v4 y-coordinate of the second point (B)
	 * @param v5 x-coordinate of the third point (C)
	 * @param v6 y-coordinate of the third point (C)
	 * @param v7 x-coordinate of the fourth point (D)
	 * @param v8 y-coordinate of the fourth point (D)
	 * @return the polynomial
	 */
	public static PPolynomial perpendicular(PVariable v1, PVariable v2, PVariable v3, 
			PVariable v4, PVariable v5, PVariable v6, PVariable v7, PVariable v8) {

		Log.trace("Setting up equation for perpendicular lines " +
				"(" + v1 + "," + v2 + ")-" +
				"(" + v3 + "," + v4 + ") and " +
				"(" + v5 + "," + v6 + ")-" +
				"(" + v7 + "," + v8 + ")");
		
		PPolynomial a1 = new PPolynomial(v1);
		PPolynomial a2 = new PPolynomial(v2);
		PPolynomial b1 = new PPolynomial(v3);
		PPolynomial b2 = new PPolynomial(v4);
		PPolynomial c1 = new PPolynomial(v5);
		PPolynomial c2 = new PPolynomial(v6);
		PPolynomial d1 = new PPolynomial(v7);
		PPolynomial d2 = new PPolynomial(v8);
		
		// (a1-b1)*(c1-d1)+(a2-b2)*(c2-d2)
		PPolynomial ret = ((a1.subtract(b1)).multiply(c1.subtract(d1)))
				.add((a2.subtract(b2)).multiply(c2.subtract(d2)));
		return ret;
	}

	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * are parallel, i.e. AB is parallel to CD. 
	 * @param v1 x-coordinate of the first point (A)
	 * @param v2 y-coordinate of the first point (A)
	 * @param v3 x-coordinate of the second point (B)
	 * @param v4 y-coordinate of the second point (B)
	 * @param v5 x-coordinate of the third point (C)
	 * @param v6 y-coordinate of the third point (C)
	 * @param v7 x-coordinate of the fourth point (D)
	 * @param v8 y-coordinate of the fourth point (D)
	 * @return the polynomial
	 */
	public static PPolynomial parallel(PVariable v1, PVariable v2, PVariable v3, 
			PVariable v4, PVariable v5, PVariable v6, PVariable v7, PVariable v8) {

		Log.trace("Setting up equation for parallel lines " +
				"(" + v1 + "," + v2 + ")-" +
				"(" + v3 + "," + v4 + ") and " +
				"(" + v5 + "," + v6 + ")-" +
				"(" + v7 + "," + v8 + ")");
		
		PPolynomial a1 = new PPolynomial(v1);
		PPolynomial a2 = new PPolynomial(v2);
		PPolynomial b1 = new PPolynomial(v3);
		PPolynomial b2 = new PPolynomial(v4);
		PPolynomial c1 = new PPolynomial(v5);
		PPolynomial c2 = new PPolynomial(v6);
		PPolynomial d1 = new PPolynomial(v7);
		PPolynomial d2 = new PPolynomial(v8);

		// (a1-b1)*(c2-d2)-(a2-b2)*(c1-d1)
		PPolynomial ret = ((a1.subtract(b1)).multiply(c2.subtract(d2)))
				.subtract((a2.subtract(b2)).multiply(c1.subtract(d1)));
		return ret;
	}

	/**
	 * Creates a polynomial which describes the area of a triangle, i.e. area of
	 * triangle ABC.
	 * 
	 * @param v1
	 *            x-coordinate of the first point (A)
	 * @param v2
	 *            y-coordinate of the first point (A)
	 * @param v3
	 *            x-coordinate of the second point (B)
	 * @param v4
	 *            y-coordinate of the second point (B)
	 * @param v5
	 *            x-coordinate of the third point (C)
	 * @param v6
	 *            y-coordinate of the third point (C)
	 * @return the polynomial
	 */
	public static PPolynomial area(PVariable v1, PVariable v2, PVariable v3,
			PVariable v4, PVariable v5, PVariable v6) {
		PPolynomial a1 = new PPolynomial(v1);
		PPolynomial a2 = new PPolynomial(v2);
		PPolynomial b1 = new PPolynomial(v3);
		PPolynomial b2 = new PPolynomial(v4);
		PPolynomial c1 = new PPolynomial(v5);
		PPolynomial c2 = new PPolynomial(v6);

		PPolynomial ret = a1.multiply(b2).add(b1.multiply(c2))
				.add(c1.multiply(a2)).subtract(c1.multiply(b2))
				.subtract(a1.multiply(c2)).subtract(a2.multiply(b1));
		return ret;
	}
		
	/**
	 * Calculates the determinant of a 4 times 4 matrix
	 * @param matrix matrix
	 * @return the determinant
	 */
	public static PPolynomial det4(final PPolynomial[][] matrix){
		return matrix[0][3].multiply(matrix[1][2].multiply(matrix[2][1].multiply(matrix[3][0]))).subtract(
				matrix[0][2].multiply(matrix[1][3]).multiply(matrix[2][1]).multiply(matrix[3][0])).subtract(
				matrix[0][3].multiply(matrix[1][1]).multiply(matrix[2][2]).multiply(matrix[3][0])).add(
				matrix[0][1].multiply(matrix[1][3]).multiply(matrix[2][2]).multiply(matrix[3][0])).add(
				matrix[0][2].multiply(matrix[1][1]).multiply(matrix[2][3]).multiply(matrix[3][0])).subtract(
				matrix[0][1].multiply(matrix[1][2]).multiply(matrix[2][3]).multiply(matrix[3][0])).subtract(
				matrix[0][3].multiply(matrix[1][2]).multiply(matrix[2][0]).multiply(matrix[3][1])).add(
				matrix[0][2].multiply(matrix[1][3]).multiply(matrix[2][0]).multiply(matrix[3][1])).add(
				matrix[0][3].multiply(matrix[1][0]).multiply(matrix[2][2]).multiply(matrix[3][1])).subtract(
				matrix[0][0].multiply(matrix[1][3]).multiply(matrix[2][2]).multiply(matrix[3][1])).subtract(
				matrix[0][2].multiply(matrix[1][0]).multiply(matrix[2][3]).multiply(matrix[3][1])).add(
				matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][3]).multiply(matrix[3][1])).add(
				matrix[0][3].multiply(matrix[1][1]).multiply(matrix[2][0]).multiply(matrix[3][2])).subtract(
				matrix[0][1].multiply(matrix[1][3]).multiply(matrix[2][0]).multiply(matrix[3][2])).subtract(
				matrix[0][3].multiply(matrix[1][0]).multiply(matrix[2][1]).multiply(matrix[3][2])).add(
				matrix[0][0].multiply(matrix[1][3]).multiply(matrix[2][1]).multiply(matrix[3][2])).add(
				matrix[0][1].multiply(matrix[1][0]).multiply(matrix[2][3]).multiply(matrix[3][2])).subtract(
				matrix[0][0].multiply(matrix[1][1]).multiply(matrix[2][3]).multiply(matrix[3][2])).subtract(
				matrix[0][2].multiply(matrix[1][1]).multiply(matrix[2][0]).multiply(matrix[3][3])).add(
				matrix[0][1].multiply(matrix[1][2]).multiply(matrix[2][0]).multiply(matrix[3][3])).add(
				matrix[0][2].multiply(matrix[1][0]).multiply(matrix[2][1]).multiply(matrix[3][3])).subtract(
				matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][1]).multiply(matrix[3][3])).subtract(
				matrix[0][1].multiply(matrix[1][0]).multiply(matrix[2][2]).multiply(matrix[3][3])).add(
				matrix[0][0].multiply(matrix[1][1]).multiply(matrix[2][2]).multiply(matrix[3][3]));
	}
	
	/** 
	 * Calculates the cross product of two vectors of dimension three.
	 * @param a the first vector
	 * @param b the second vector
	 * @return the cross product of the two vectors
	 */
	public static PPolynomial[] crossProduct(PPolynomial[] a,
			PPolynomial[] b) {
		PPolynomial[] result=new PPolynomial[3];
		result[0]=(a[1].multiply(b[2])).subtract(a[2].multiply(b[1]));
		result[1]=(a[2].multiply(b[0])).subtract(a[0].multiply(b[2]));
		result[2]=(a[0].multiply(b[1])).subtract(a[1].multiply(b[0]));
		return result;
	}
	
	/**
	 * Substitutes variables in the polynomial by integer values
	 * 
	 * @param substitutions
	 *            A map of the substitutions
	 * @return a new polynomial with the variables substituted.
	 */
	public PPolynomial substitute(Map<PVariable, BigInteger> substitutions) {
		
		if (substitutions == null)
			return this;
			
		TreeMap<PTerm, BigInteger> result = new TreeMap<>();

		Iterator<Entry<PTerm, BigInteger>> it = terms.entrySet().iterator();
		while (it.hasNext()) {
			Entry<PTerm, BigInteger> entry = it.next();
			PTerm t1 = entry.getKey();
			TreeMap<PVariable, Integer> term = new TreeMap<>(t1.getTerm());
			BigInteger product = BigInteger.ONE;
			Iterator<Entry<PVariable, BigInteger>> itSubst = substitutions
					.entrySet()
					.iterator();
			while (itSubst.hasNext()) {
				Entry<PVariable, BigInteger> entrySubst = itSubst.next();
				PVariable variable = entrySubst.getKey();
				Integer exponent = term.get(variable);
				if (exponent != null) {
					product = product
							.multiply(entrySubst.getValue().pow(exponent));
					term.remove(variable);
				}
			}
			product = product.multiply(entry.getValue());
			PTerm t = new PTerm(term);
			if (result.containsKey(t)) {
				BigInteger sum = result.get(t).add(product);
				if (sum.longValue() == 0) {
					result.remove(t);
				} else {
					result.put(t, sum);
				}
			} else if (product.intValue() != 0){
				result.put(t, product);
			}
		}
		return new PPolynomial(result);
	}
	/**
	 * Substitutes a variable in the polynomial by another variable.
	 * 
	 * @param oldVar
	 *            old variable
	 * @param newVar
	 *            new variable
	 *
	 * @return a new polynomial with the variable substituted.
	 */
	public PPolynomial substitute(PVariable oldVar, PVariable newVar) {

		TreeMap<PTerm, BigInteger> result = new TreeMap<>();
		Iterator<Entry<PTerm, BigInteger>> it = terms.entrySet().iterator();
		while (it.hasNext()) {
			Entry<PTerm, BigInteger> entry = it.next();
			PTerm t1 = entry.getKey();
			TreeMap<PVariable, Integer> term = new TreeMap<>(
					t1.getTerm());
			Integer oldExponent = term.get(oldVar);
			if (oldExponent != null) {
				Integer newExponent = term.get(newVar);
				if (newExponent == null) {
					newExponent = 0;
				} else {
					term.remove(newVar);
				}
				term.remove(oldVar);
				term.put(newVar, oldExponent + newExponent);
			}
			BigInteger coeff = entry.getValue();
			PTerm t = new PTerm(term);
			result.put(t, coeff);
		}
		return new PPolynomial(result);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PPolynomial) {
			return this.compareTo((PPolynomial) o) == 0;
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return terms.hashCode();
	}

	/**
	 * Tests if the Polynomial is the zero polynomial
	 * @return true if the polynomial is zero false otherwise
	 */
	public boolean isZero() {
		return terms.isEmpty();
	}
	
	/**
	 * Tests if the polynomial is a constant.
	 * @return if input is a constant
	 */
	public boolean isConstant() {
		if (terms.size() > 1) {
			return false;
		}
		if (terms.firstKey().equals(new PTerm())) {
			return true;
		}
		return false;
		}
	
	/**
	 * @return Integer value of Polynomial if it is constant
	 */
	public BigInteger getConstant() {
		if (terms.size() > 1) {
			return null;
		}
		return terms.firstEntry().getValue();
	}

	/**
	 * Tests if two polynomials are associates by a +/-1 multiplier
	 * @param p1 First polynomial
	 * @param p2 Second polynomial
	 * @return if the polynomials are associates
	 */
	public static boolean areAssociates1(PPolynomial p1, PPolynomial p2) {
		return p1.equals(p2) || p1.add(p2).isZero();
	}
	
	
	/**
	 * Tests if the Polynomial is the constant one polynomial
	 * @return true if the polynomial is zero false otherwise
	 */
	public boolean isOne() {
		return equals(new PPolynomial(BigInteger.ONE));
	}
	

	/**
	 * Converts substitutions to Singular strings
	 * 
	 * @param substitutions
	 *            input as a HashMap
	 * @return the parameters for Singular (e.g. "v1,0,v2,0,v3,0,v4,1")
	 */
	static String substitutionsString(
			HashMap<PVariable, BigInteger> substitutions) {
		StringBuilder ret = new StringBuilder();
		Iterator<Entry<PVariable, BigInteger>> it = substitutions.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<PVariable, BigInteger> entry = it.next();
			PVariable v = entry.getKey();
			ret.append("," + v.toString() + "," + entry.getValue());
		}
		if (ret.length()>0)
			return ret.substring(1);
		return "";
	}
		
	/**
	 * Adds a leading comma to the input string if it is not empty
	 * @param in input
	 * @return output string
	 */
	public static String addLeadingComma (String in) {
		if (in == null || in.length() == 0)
			return "";
		return "," + in;
	}
	
	/**
	 * Returns in1 if it is not empty, in2 otherwise
	 * @param in1 input1
	 * @param in2 input2
	 * @return the first non-empty input
	 */
	public static String coalesce (String in1, String in2) {
		if (in1 == null || in1.length() == 0)
			return in2;
		return in1;
	}
	
	/**
	 * Creates a Singular program for creating a ring to work with several
	 * polynomials, and returns if the equation system has a solution. Uses
	 * the Groebner basis w.r.t. the revgradlex order.
	 * @param substitutions HashMap with variables and values, e.g. {v1->0},{v2->1}
	 * @param polys polynomials, e.g. "v1+v2-3*v4-10"
	 * @param fieldVars field variables (comma separated) 
	 * @param ringVars ring variables (comma separated)
	 * @param transcext use coefficients from a transcendental extension
	 * @return the Singular program code
	 */
	public static String createGroebnerSolvableScript(
			HashMap<PVariable, BigInteger> substitutions, String polys,
			String fieldVars, String ringVars, boolean transcext) {
		
		String ringVariable = "r";
		String idealVariable = "i";
		String dummyVar = "d";
		
		String vars = ringVars + addLeadingComma(fieldVars); 
		
		String substCommand = "";
		if (substitutions != null) {
			String substParams = substitutionsString(substitutions);
			substCommand = idealVariable + "=subst(" + idealVariable + "," + substParams + ");";
		}
		String ret = "ring " + ringVariable + "=";
		
		if (transcext) {
			ret += "(0" + addLeadingComma(fieldVars)
				+ "),(" + coalesce(ringVars, dummyVar);
		}
		else {
			ret += "0,(" + coalesce(vars, dummyVar);
		}
		
		ret += "),dp;" // ring definition in Singular, using revgradlex
			+ "ideal " + idealVariable + "="
		 	+ polys + ";"; // ideal definition in Singular

		ret += substCommand;

		ret += "groebner(" + idealVariable + ")!=1;"; // the Groebner basis calculation command
		return ret;
	}
	
	
	/**
	 * Creates a Singular program for the elimination ideal given by
	 * a set of generating polynomials. We get the result in factorized form.
	 * @param polys set of polynomials generating the ideal
	 * @param pVariables the variables of the polynomials
	 * @param dependentVariables the variables that should be eliminated
	 * @return the Singular program code
	 */
	
	/*
	 * Example program code:
	 * ring r=0,(v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15),dp;
	 * ideal i=-1*v5+-1*v3+2*v1,-1*v6+-1*v4+2*v2,-1*v9+2*v7+-1*v5,-1*v10+2*v8+-1*v6,2*v11+-1*v7+-1*v1,2*v12+-1*v8+-1*v2,
	 * -1*v13*v12+v14*v11+v13*v6+-1*v11*v6+-1*v14*v5+v12*v5,
	 * -1*v13*v10+v14*v9+v13*v4+-1*v9*v4+-1*v14*v3+v10*v3,
	 * -1+2*v15*v14*v10+-1*v15*v10^2+2*v15*v13*v9+-1*v15*v9^2+-2*v15*v14*v4+v15*v4^2+-2*v15*v13*v3+v15*v3^2;
	 * ideal e=eliminate(i,v1*v2*v7*v8*v11*v12*v13*v14*v15);
	 * list o;int s=size(e);int j;for(j=1;j<=s;j=j+1){o[j]=factorize(e[j]);}o;
	 * 
	 * Example output from Singular:
	 * [1]:
          [1]:
             _[1]=1
             _[2]=v4*v5-v3*v6-v4*v9+v6*v9+v3*v10-v5*v10
          [2]:
             1,1
	 */
	
	public static String createEliminateFactorizedScript( 
			PPolynomial[] polys, PVariable[] pVariables, Set<PVariable> dependentVariables) {

		String ringVariable = "r";
		String idealVariable = "i";
		String loopVariable = "j";
		String sizeVariable = "s";
		String eliminationVariable = "e";
		String outputVariable = "o";
		String dummyVar = "d";
		
		StringBuilder ret = new StringBuilder("ring ");
		ret.append(ringVariable);
		ret.append("=0,(");
		StringBuilder vars = new StringBuilder();
		for (PVariable v : pVariables) {
			vars.append(v + ",");
		}
		if (vars.length() > 0) {
			ret.append(vars.substring(0, vars.length() - 1));
			if (dependentVariables.isEmpty()) {
				ret.append(",").append(dummyVar);
			}
		}
		else
			ret.append(dummyVar);
		
		ret.append("),dp;");
		
		ret.append("ideal ");
		ret.append(idealVariable);
		ret.append("=");
		ret.append(getPolysAsCommaSeparatedString(polys));
		ret.append(";");
		
		ret.append("ideal ");
		ret.append(eliminationVariable);
		ret.append("=");
		ret.append("eliminate(");
		ret.append(idealVariable);
		ret.append(",");

		vars = new StringBuilder();
		Iterator<PVariable> dependentVariablesIterator = dependentVariables.iterator();
		while (dependentVariablesIterator.hasNext()){
			vars.append(dependentVariablesIterator.next());
			if (dependentVariablesIterator.hasNext()){
				vars.append("*");
			}
		}
		if (vars.length() > 0) {
			ret.append(vars);
		} else {
			ret.append(dummyVar);
		}
		
		ret.append(");");

		// list o;int s=size(e);int j;for(j=1;j<=s;j=j+1){o[j]=factorize(e[j]);}o;
		ret.append("list " + outputVariable + ";int " + sizeVariable + "=size(" + eliminationVariable + ");");
		ret.append("int " + loopVariable + ";for(" + loopVariable + "=1;" + loopVariable + "<=" + sizeVariable
				+ ";" + loopVariable + "=" + loopVariable + "+1)");
		ret.append("{" + outputVariable + "[" + loopVariable + "]=factorize(" + eliminationVariable
				+ "[" + loopVariable + "]);}o;");
		
		return ret.toString();
	}

	/**
	 * Decides if an array of polynomials (as a set) gives a solvable equation
	 * system on the field of the complex numbers.
	 * 
	 * @param polys
	 *            the array of polynomials
	 * @param substitutions
	 *            some variables which are to be evaluated with exact numbers
	 * @param kernel
	 *            kernel for the prover
	 * @param transcext
	 *            use coefficients from transcendent extension if possible
	 * @param freeVariables
	 *            set of free variables
	 * @return yes if solvable, no if no solutions, or null (if cannot decide)
	 */
	public static ExtendedBoolean solvable(PPolynomial[] polys,
			HashMap<PVariable, BigInteger> substitutions, Kernel kernel,
			boolean transcext, Set<PVariable> freeVariables) {
		
		HashSet<PVariable> substVars = null;
		String polysAsCommaSeparatedString = getPolysAsCommaSeparatedString(polys);
		substVars = new HashSet<>(substitutions.keySet());

		String freeVars = getVarsAsCommaSeparatedString(polys, substVars, true,
				freeVariables);
		String dependantVars = getVarsAsCommaSeparatedString(polys, substVars,
				false, freeVariables);
		String solvableResult, solvableProgram;

		// If SingularWS is not applicable, then we try to use the internal CAS:
		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		
		solvableProgram = cas.getCurrentCAS().createGroebnerSolvableScript(substitutions, polysAsCommaSeparatedString,
				freeVars, dependantVars, transcext);
		if (solvableProgram == null) {
			Log.info("Not implemented (yet)");
			return ExtendedBoolean.UNKNOWN; // cannot decide
		}
		solvableResult = cas.evaluate(solvableProgram);
		if ("0".equals(solvableResult) || "false".equals(solvableResult)) {
			return ExtendedBoolean.FALSE; // no solution
		}
		if ("1".equals(solvableResult) || "true".equals(solvableResult)) {
			return ExtendedBoolean.TRUE; // at least one solution exists
		}
		return ExtendedBoolean.UNKNOWN; // cannot decide
	}
	
	/** Returns the square of the input polynomial
	 * @param p input polynomial
	 * @return the square (p*p)
	 */
	public static PPolynomial sqr(PPolynomial p) {
		return p.multiply(p);
	}
	
	/**
	 * Returns the square of the distance of two points
	 * @param a1 first coordinate of A
	 * @param a2 second coordinate of A
	 * @param b1 first coordinate of B
	 * @param b2 second coordinate of B
	 * @return the square of the distance
	 */
	public static PPolynomial sqrDistance(PVariable a1, PVariable a2, PVariable b1, PVariable b2) {
		return sqr(new PPolynomial(a1).subtract(new PPolynomial(b1)))
				.add(sqr(new PPolynomial(a2).subtract(new PPolynomial(b2))));
	}
	
	/**
	 * Returns if AO=OB, i.e. whether the AOB triangle is isosceles
	 * @param a1 first coordinate of A
	 * @param a2 second coordinate of A
	 * @param o1 first coordinate of O
	 * @param o2 second coordinate of O
	 * @param b1 first coordinate of B
	 * @param b2 second coordinate of B
	 * @return the 0 polynomial if AO=OB
	 */
	public static PPolynomial equidistant(PVariable a1, PVariable a2,
			PVariable o1, PVariable o2, PVariable b1,
			PVariable b2) {
		return sqrDistance(a1,a2,o1,o2).subtract(sqrDistance(o1,o2,b1,b2));  
	}
	
	/**
	 * Returns the elimination ideal for the given equation system, assuming
	 * given substitutions. Only the dependent variables will be eliminated.
	 * 
	 * @param eqSystem
	 *            the equation system
	 * @param substitutions
	 *            fixed values for certain variables
	 * @param kernel
	 *            GeoGebra kernel
	 * @param permutation
	 *            use this permutation number for changing the order of the
	 *            first free variables
	 * @param factorized
	 *            compute output ideal in factorized form
	 * @param oneCurve
	 *            prefer getting one algebraic curve than an ideal with more
	 *            elements
	 * @param freeVariablesInput
	 *            input set of free variables
	 * @return elements of the elimination ideal or null if computation failed
	 */
	public static Set<Set<PPolynomial>> eliminate(PPolynomial[] eqSystem,
			HashMap<PVariable, BigInteger> substitutions, Kernel kernel,
			int permutation, boolean factorized, boolean oneCurve,
			Set<PVariable> freeVariablesInput) {

		TreeSet<PVariable> dependentVariables = new TreeSet<>();
		TreeSet<PVariable> variables = new TreeSet<>(getVars(eqSystem));
		Iterator<PVariable> variablesIterator = variables.iterator();
		while (variablesIterator.hasNext()) {
			PVariable variable = variablesIterator.next();
			if (substitutions == null || !substitutions.containsKey(variable)) {
				if (!freeVariablesInput.contains(variable)) {
					dependentVariables.add(variable);
				}
			}
		}
		PPolynomial[] eqSystemSubstituted;
		if (substitutions != null) {
			eqSystemSubstituted = new PPolynomial[eqSystem.length];
			for (int i = 0; i < eqSystem.length; i++) {
				eqSystemSubstituted[i] = eqSystem[i]
						.substitute(substitutions);
			}
			variables.removeAll(substitutions.keySet());
		} else {
			eqSystemSubstituted = eqSystem;
		}
		
		String elimResult, elimProgram;
		Log.debug("Eliminating system in " + variables.size() + " variables (" + dependentVariables.size() + " dependent)");

		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();

		String polys = getPolysAsCommaSeparatedString(eqSystemSubstituted);
		String elimVars = getVarsAsCommaSeparatedString(eqSystemSubstituted,
				null, false, freeVariablesInput);
		String freeVars = getVarsAsCommaSeparatedString(eqSystemSubstituted,
				null, true, freeVariablesInput);
		Log.trace("gbt polys = " + polys);
		Log.trace("gbt vars = " + elimVars + "," + freeVars);
		// Consider uncomment this if Giac cannot find a readable NDG:
		// elimVars = dependentVariables.toString().replaceAll(" ", "");
		// elimVars = elimVars.substring(1, elimVars.length()-1);

		if (factorized) {
			elimProgram = cas.getCurrentCAS()
					.createEliminateFactorizedScript(polys, elimVars);
		} else {
			elimProgram = cas.getCurrentCAS().createEliminateScript(polys,
					elimVars, oneCurve, kernel.precision());
		}
		if (elimProgram == null) {
			Log.info("Not implemented (yet)");
			return null; // cannot decide
		}

		elimResult = cas.evaluate(elimProgram).replace("unicode95u", "_")
				.replace("unicode91u", "[");

		if (!factorized) {

			elimResult = elimResult.replace(".0", "");
			elimResult = elimResult.substring(1, elimResult.length() - 1);
			elimResult = "[1]: [1]: _[1]=1 _[2]=" + elimResult
					+ " [2]: 1,1";
			Log.trace("Rewritten: " + elimResult);
		}

		// Singular returns "empty list", Giac "{0}" when the statement is
		// false:
		if ("empty list".equals(elimResult) || "{0}".equals(elimResult)) {
			// If we get an empty list from Singular, it means
			// the answer is false, so we artificially create the {{0}} answer.
			Set<Set<PPolynomial>> ret = new HashSet<>();
			HashSet<PPolynomial> polysSet = new HashSet<>();
			polysSet.add(new PPolynomial(BigInteger.ZERO)); // this might be Polynomial() as well
			ret.add(polysSet);
			return ret;
		}
		/*
		 * Singular may return "halt 1" or something similar. We should handle
		 * this in general but for some strange reason we cannot catch the
		 * exception later from PolynomialParser. TODO: find a better way than
		 * this hack.
		 */
		if (elimResult.contains("halt")) {
			return null; // too difficult problem for Singular
		}

		// Giac returns ? or empty string if there was a timeout:
		if ("?".equals(elimResult) || "".equals(elimResult)) {
			return null; // cannot decide
		}

		try {
			return PolynomialParser.parseFactoredPolynomialSet(
					elimResult, variables);
		} catch (ParseException e) {
			Log.debug("Cannot parse: " + elimResult);
			Log.debug(e);
		}

		return null; // cannot decide
	}

}
