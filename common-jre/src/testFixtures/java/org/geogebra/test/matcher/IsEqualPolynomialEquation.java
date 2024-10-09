package org.geogebra.test.matcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Used for comparing multivariate polynomial equations which have to be in the
 * syntax of the CAS output (for example "(a * x^(2) + b * x + c) / a = 0")
 * 
 * @author Johannes Renner
 */
public class IsEqualPolynomialEquation extends TypeSafeMatcher<String> {

	private final String expected;

	// Key of the outer HashMap is the variable name + the exponent (for example
	// "x^(2)")
	// Value of the HashMap is the factor of the variable ordered
	// alphabetically (for example: +1*x*y, -14*a*b)
	// the array index indicates the part of the equation (a new part begins
	// after every '/' character)
	private static HashMap<String, String>[] expectedTermsLeft;
	private static HashMap<String, String>[] expectedTermsRight;
	private static HashMap<String, String>[] testResultTermsLeft;
	private static HashMap<String, String>[] testResultTermsRight;

	/**
	 * @param expected
	 *            the expected equation
	 */
	public IsEqualPolynomialEquation(String expected) {
		this.expected = expected;
	}

	@Override
	public boolean matchesSafely(String testResult) {
		extractTerms(expected, true);
		extractTerms(testResult, false);

		return compare();
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}

	/**
	 * Tests if a polynomial equation is equal to another one ignoring the
	 * ordering of the terms on each side of the '=' character.
	 * 
	 * @param expected
	 *            the expected polynomial equation
	 * @return <b>true</b> if the equations are same (ignoring the term ordering
	 *         on each side, <b>false</b> otherwise
	 */
	@Factory
	public static Matcher<String> equalToPolynomialEquation(String expected) {
		return new IsEqualPolynomialEquation(expected);
	}

	/**
	 * Extracts the terms out of the equation
	 * 
	 * @param equation
	 *            the equation to handle
	 * @param isExpectedResult
	 *            if true the terms will be written to the class arguments
	 *            <code>expectedTermsLeft</code> and
	 *            <code>expectedTermsRight</code><br/>
	 *            if false they will be written to
	 *            <code>testResultTermsLeft</code> and
	 *            <code>testResultTermsRight</code>
	 */
	@SuppressWarnings("unchecked")
	private static void extractTerms(String equation, boolean isExpectedResult) {
		// remove all blanks
		String equationWithoutBlanks = equation.replaceAll(" ", "");
		// split the equation in the left and the right side
		String[] split = equationWithoutBlanks.split("=");
		// split every side in their parts
		String[] leftParts = split[0].split("/");
		String[] rightParts = split[1].split("/");

		if (isExpectedResult) {
			expectedTermsLeft = new HashMap[leftParts.length];
			expectedTermsRight = new HashMap[rightParts.length];

			extractTermsOfParts(leftParts, expectedTermsLeft);
			extractTermsOfParts(rightParts, expectedTermsRight);
		} else {
			testResultTermsLeft = new HashMap[leftParts.length];
			testResultTermsRight = new HashMap[rightParts.length];

			extractTermsOfParts(leftParts, testResultTermsLeft);
			extractTermsOfParts(rightParts, testResultTermsRight);
		}
	}

	/**
	 * Extracts all terms of the given parts of the equation
	 * 
	 * @param parts
	 *            the parts to handle
	 * @param terms
	 *            the array of {@link HashMap}s which contain all terms of the
	 *            parts of the equation
	 */
	private static void extractTermsOfParts(String[] parts,
			HashMap<String, String>[] terms) {
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];

			// remove parentheses if there are some around the whole part
			if (part.charAt(0) == '(') {
				part = part.substring(1, part.length() - 1);
			}

			int termNumber = 0;
			String signs = "";
			for (int c = 0; c < part.length(); c++) {
				char toCheck = part.charAt(c);
				if (toCheck == '+' || toCheck == '-') {
					termNumber++;
					signs += toCheck;
				} else if (termNumber == 0) {
					// first factor has no sign so we add one
					termNumber = 1;
					signs += "+";
				}
			}

			// if the first character of the part is a sign ('+' or '-') we have
			// to remove that because otherwise we would get an empty string
			// after splitting it up
			if (part.charAt(0) == '+' || part.charAt(0) == '-') {
				part = part.substring(1);
			}

			String[] splitPart = part.split("[\\+\\-]");

			terms[i] = new HashMap<>();

			for (int p = 0; p < splitPart.length; p++) {
				handleTerm(splitPart[p], signs.charAt(p), terms[i]);
			}
		}
	}

	/**
	 * Handles one term of the equation
	 * 
	 * @param term
	 *            the term to handle
	 * @param sign
	 *            the sign of the given term
	 * @param terms
	 *            the {@link HashMap} which contains all terms of this part of
	 *            the equation
	 */
	private static void handleTerm(String term, char sign,
			HashMap<String, String> terms) {
		String numericalFactor = "1";

		String[] splitTerm = term.split("\\*");
		LinkedList<String> variables = new LinkedList<>();
		for (int i = 0; i < splitTerm.length; i++) {
			// handle the number of the factor or one variable
			String s = splitTerm[i];
			if (Character.isDigit(s.charAt(0))) {
				numericalFactor = s;
			} else {
				addAlphabetically(s, variables);
			}
		}

		for (int i = 0; i < variables.size(); i++) {
			String variable = variables.get(i);
			String factor = sign + numericalFactor;
			for (int ii = 0; ii < variables.size(); ii++) {
				if (i != ii) {
					// add every variable which is in the factor to the
					// factor-String
					factor += "*" + variables.get(ii);
				}
			}
			terms.put(variable, factor);
		}
	}

	/**
	 * Adds one variable to the given list, so that the list is alphabetically
	 * ordered
	 * 
	 * @param variable
	 *            the variable to add to the list
	 * @param list
	 *            the list where the given variable will be added
	 */
	private static void addAlphabetically(String variable,
			LinkedList<String> list) {
		if (list.isEmpty()) {
			list.add(variable);
			return;
		}

		Iterator<String> it = list.iterator();
		int position = 0;
		while (it.hasNext()) {
			String current = it.next();
			if (current.compareTo(variable) < 0) {
				position++;
			} else {
				break;
			}
		}
		list.add(position, variable);
	}

	/**
	 * Compares the expected result and the test result and returns if they are
	 * equal or not
	 * 
	 * @return true if the equations are equal (except their term ordering),
	 *         false otherwise
	 */
	private static boolean compare() {
		if (expectedTermsLeft.length != testResultTermsLeft.length
				|| expectedTermsRight.length != testResultTermsRight.length) {
			return false;
		}

		return compareSide(expectedTermsLeft, testResultTermsLeft)
				&& compareSide(expectedTermsRight, testResultTermsRight);
	}

	/**
	 * Compares one side of the equation
	 * 
	 * @param expectedTerms
	 *            the array of the expected terms of all parts of one side of
	 *            the equation
	 * @param testResultTerms
	 *            the array of the test result terms of all parts of one side of
	 *            the equation
	 * @return true if the compared side of the expected result and the test
	 *         result are equal, false otherwise
	 */
	private static boolean compareSide(HashMap<String, String>[] expectedTerms,
			HashMap<String, String>[] testResultTerms) {
		for (int i = 0; i < expectedTerms.length; i++) {
			Set<String> keys = expectedTerms[i].keySet();
			Iterator<String> it = keys.iterator();
			// compare the factors of every variable
			while (it.hasNext()) {
				String key = it.next();
				if (!expectedTerms[i].get(key).equals(
						testResultTerms[i].get(key))) {
					return false;
				}
			}
		}
		return true;
	}
}
