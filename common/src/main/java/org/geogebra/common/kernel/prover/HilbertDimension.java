package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.debug.Log;

/**
 * Compute the Hilbert dimension of the hypothesis ideal appearing in an
 * algebraic geometry proof.
 * 
 * @author kovzol
 *
 */
public class HilbertDimension {

	private static Kernel kernel;

	private static Set<PVariable> aMaximalSet;

	/**
	 * Get a maximum size independent set that contains the same amount element
	 * as the Hilbert dimension of the ideal.
	 * 
	 * @return a maximum size independent set
	 */
	/* Using static here is dangerous. FIXME */
	public static Set<PVariable> getAMaximalSet() {
		return aMaximalSet;
	}

	private static boolean eliminationIsZero(Set<PPolynomial> polys,
			Set<PVariable> vars, HashMap<PVariable, BigInteger> substitutions) {
		Set<Set<PPolynomial>> eliminationIdeal = PPolynomial.eliminate(
				polys.toArray(new PPolynomial[polys.size()]), substitutions,
				kernel, 0,
				true, false, vars);
		Iterator<Set<PPolynomial>> ndgSet;
		ndgSet = eliminationIdeal.iterator();
		while (ndgSet.hasNext()) {
			Set<PPolynomial> thisNdgSet = ndgSet.next();
			for (PPolynomial poly : thisNdgSet) {
				if (poly.isZero()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isDimGreaterThan(AlgebraicStatement as,
			HashMap<PVariable, BigInteger> substitutions, int minDim) {

		int dim = 0;

		kernel = as.geoStatement.getKernel();
		HashSet<HashSet<PVariable>> nextUseful, useful = new HashSet<>();
		HashSet<PVariable> allVars = PPolynomial.getVars(as.getPolynomials());
		// Remove substituted vars:
		for (PVariable var : substitutions.keySet()) {
			allVars.remove(var);
		}

		// Create the useful set of variable sets, each containing one single
		// variable first:
		for (PVariable var : allVars) {
			HashSet<PVariable> singleSet = new HashSet<>();
			singleSet.add(var);
			useful.add(singleSet);
		}

		boolean loop = true;
		while (loop) {
			dim++;
			Log.debug(useful.size() + " useful sets to be checked for " + dim
					+ " dimensions");
			/* lastUseful = nextUseful; */
			nextUseful = new HashSet<>();
			// Check the useful set if they are useful in the future:
			for (HashSet<PVariable> set : useful) {
				if (eliminationIsZero(as.getPolynomials(), set,
						substitutions)) {
					nextUseful.add(set);
					if (dim > minDim) {
						Log.debug(
								"Found a useful set " + set + " with dimension "
										+ dim + ": Hilbert dimension > "
										+ minDim);
						return true;
					}
				}
			}

			// Create next useful set:
			useful = new HashSet<>();
			for (HashSet<PVariable> set1 : nextUseful) {
				for (HashSet<PVariable> set2 : nextUseful) {
					HashSet<PVariable> union = new HashSet<>(set1);
					union.addAll(set2);
					if (union.size() == dim + 1) {
						useful.add(union);
					}
				}
			}

			Log.debug(
					"There are " + useful.size() + " useful sets = " + useful);
			if (useful.isEmpty()) {
				loop = false;
			}
		}
		Log.debug("No useful sets found with " + dim
				+ " dimensions: Hilbert dimension = " + (dim - 1));
		return false;
	}

	/**
	 * Compute Hilbert dimension of the ideal described by the polynomials.
	 * Before calling this, ensure that the input does not contain the thesis.
	 * 
	 * TODO: This algorithm is very slow when there are more variables, find a
	 * faster method. Using substitutions may speed up computations
	 * dramatically.
	 * 
	 * @param as
	 *            the algebraic statement
	 * @param substitutions
	 *            variables and their BigInteger substitutions
	 * @return the Hilbert dimension
	 */
	public static int compute(AlgebraicStatement as,
			HashMap<PVariable, BigInteger> substitutions) {
		int dim = 0;

		kernel = as.geoStatement.getKernel();
		HashSet<HashSet<PVariable>> nextUseful = new HashSet<>(),
				lastUseful = new HashSet<>(),
				useful = new HashSet<>();
		HashSet<PVariable> allVars = PPolynomial.getVars(as.getPolynomials());
		// Remove substituted vars:
		for (PVariable var : substitutions.keySet()) {
			allVars.remove(var);
		}

		// Create the useful set of variable sets, each containing one single
		// variable first:
		for (PVariable var : allVars) {
			HashSet<PVariable> singleSet = new HashSet<>();
			singleSet.add(var);
			useful.add(singleSet);
		}

		while (!useful.isEmpty()) {
			dim++;
			Log.debug(
					useful.size() + " useful sets to be checked for " + dim
							+ " dimensions");
			lastUseful = nextUseful;
			nextUseful = new HashSet<>();
			// Check the useful set if they are useful in the future:
			for (HashSet<PVariable> set : useful) {
				if (eliminationIsZero(as.getPolynomials(), set,
						substitutions)) {
					nextUseful.add(set);
				}
			}

			// Create next useful set:
			useful = new HashSet<>();
			for (HashSet<PVariable> set1 : nextUseful) {
				for (HashSet<PVariable> set2 : nextUseful) {
					HashSet<PVariable> union = new HashSet<>(set1);
					union.addAll(set2);
					if (union.size() == dim + 1) {
						useful.add(union);
					}
				}
			}
		}
		Log.debug(
				"Sets with full dimension (" + (dim - 1) + ") = " + lastUseful);
		return dim - 1;
	}

	public static boolean isDimGreaterThan2(AlgebraicStatement as,
			HashMap<PVariable, BigInteger> substitutions, int minDim) {

		kernel = as.geoStatement.getKernel();
		HashSet<PVariable> allVars = PPolynomial.getVars(as.getPolynomials());
		// Remove substituted vars:
		for (PVariable var : substitutions.keySet()) {
			allVars.remove(var);
		}
		HashSet<PVariable> dependentVars = new HashSet<>();
		dependentVars.addAll(allVars);
		dependentVars.removeAll(as.getFreeVariables());
		dependentVars.removeAll(substitutions.keySet());
		StringBuilder depVars = new StringBuilder();
		for (PVariable var : dependentVars) {
			if (depVars.length() > 0) {
				depVars.append(",");
			}
			depVars.append(var);
		}
		HashSet<PVariable> freeVariables = new HashSet<>();
		freeVariables.addAll(as.getFreeVariables());
		freeVariables.removeAll(substitutions.keySet());
		StringBuilder freeVars = new StringBuilder();
		for (PVariable var : freeVariables) {
			if (freeVars.length() > 0) {
				freeVars.append(",");
			}
			freeVars.append(var);
		}

		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();

		as.computeStrings();
		String gbasisProgram = cas.getCurrentCAS().createGroebnerInitialsScript(
				substitutions, as.getPolys(), freeVars.toString(),
				depVars.toString());
		String gbasisResult = cas.evaluate(gbasisProgram);

		// parse the result
		// https://stackoverflow.com/a/8910767
		int gbasisSize = gbasisResult.length()
				- gbasisResult.replace("{", "").length() - 1;
		HashSet<HashSet<PVariable>> initials = new HashSet<>();
		int pos = 1;
		for (int i = 0; i < gbasisSize; ++i) {
			HashSet<PVariable> initial = new HashSet<>();
			while (!gbasisResult.substring(pos, pos + 1).equals("}")) {
				pos++;
				// the current position must be a v
				pos++;
				int oldpos = pos;
				String thischar;
				while (!","
						.equals(thischar = gbasisResult.substring(pos, pos + 1))
						&& !"}".equals(thischar)) {
					pos++;
				}
				String var = gbasisResult.substring(oldpos, pos);
				// lookup var
				boolean found = false;
				Iterator<PVariable> allvarsIterator = allVars.iterator();
				while (!found) {
					PVariable pvar = allvarsIterator.next();
					if ((pvar.getId() + "").equals(var)) {
						initial.add(pvar);
						found = true;
					}
				} // found a var
			} // found an initial
			initials.add(initial);
			pos += 2;
		} // found all initials

		// We know that the dimension is at least minDim (the naive dimension),
		// the question is if it is greater than minDim. So we check for
		// independent
		// sets having exactly minDim+1 elements. If we find an independent set
		// among them, then the Hilbert dimension is minDim+1, otherwise it is
		// minDim. See
		// https://www.risc.jku.at/projects/science/school/fifth/materials/GB.pdf
		// algorithm DIMENSION_2 for details.

		// It is possible that the naive dimension is the Hilbert dimension.
		// In this case we will use the geometrically free variables.
		aMaximalSet = new HashSet<>();
		aMaximalSet.addAll(freeVariables);
		Log.debug("The geometrically free variables should be independent: "
				+ aMaximalSet);

		int dim = minDim + 1;

		while (true) {

			Combinations<PVariable> allSubsets = new Combinations<>(allVars,
					dim);
			boolean independentFound = false;

			while (allSubsets.hasNext() && !independentFound) {
				Set<PVariable> X = allSubsets.next();
				boolean independent = true;
				// Log.debug(X);
				// in(g) \not\in K[X] means in(g) is not completely in X
				Iterator<HashSet<PVariable>> initialIterator = initials
						.iterator();
				while (initialIterator.hasNext() && independent) {
					HashSet<PVariable> initial = initialIterator.next();
					if (X.containsAll(initial)) {
						// we found an in(g) which is completely in X
						// therefore X is not independent
						independent = false;
					}
				}
				if (independent) {
					aMaximalSet = X;
					independentFound = true;
					Log.debug("An independent set found: " + aMaximalSet);
				}
			}

			if (!independentFound) {
				Log.debug("No independent set found with dimension " + dim);
				// We did not find an independent set, so we use the last
				// maximal set being found.
				return dim > minDim + 1;
			}
			dim++;
		}
	}

}
