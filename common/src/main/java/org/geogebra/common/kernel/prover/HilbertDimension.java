package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

	private static boolean eliminationIsZero(Set<PPolynomial> polys,
			Set<PVariable> vars, HashMap<PVariable, BigInteger> substitutions) {
		Set<Set<PPolynomial>> eliminationIdeal;
		eliminationIdeal = PPolynomial.eliminate(
				polys.toArray(new PPolynomial[polys.size()]), substitutions,
				kernel, 0,
				true, false, vars);
		Iterator<Set<PPolynomial>> ndgSet;
		ndgSet = eliminationIdeal.iterator();
		while (ndgSet.hasNext()) {
			Set<PPolynomial> thisNdgSet = ndgSet.next();
			Iterator<PPolynomial> ndg = thisNdgSet.iterator();
			while (ndg.hasNext()) {
				PPolynomial poly = ndg.next();
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
		HashSet<HashSet<PVariable>> nextUseful = new HashSet<>(),
				/* lastUseful = new HashSet<>(), */ useful = new HashSet<>();
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

}
