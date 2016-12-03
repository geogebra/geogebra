package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.ExtendedBoolean;
import org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.Prover.ProverEngine;
import org.geogebra.common.util.debug.Log;

/**
 * A prover which uses Tomas Recios method to prove geometric theorems.
 * 
 * @author Simon Weitzhofer
 *
 */
public abstract class AbstractProverReciosMethod {

	private static GeoElement[] fixedPoints;

	/**
	 * The prover which tries to prove the statement with the help of Tomas
	 * Recios method.
	 * 
	 * @param prover
	 *            the prover input object
	 * @return The result of the prove.
	 */
	public ProofResult prove(Prover prover) {

		SymbolicParameters s = null;
		boolean B = false; // use Botana's method or not
		if (ProverSettings.get().proverMethod.equals("groebner")) {
			B = true;
		}
		AlgebraicStatement as = null;
		GeoElement statement = prover.getStatement();
		Prover p;

		if (statement instanceof SymbolicParametersAlgo)
			s = (((SymbolicParametersAlgo) statement)
					.getSymbolicParameters());
		else if (statement.getParentAlgorithm() instanceof SymbolicParametersAlgo)
			s = (((SymbolicParametersAlgo) statement
					.getParentAlgorithm()).getSymbolicParameters());
		else {
			return ProofResult.UNKNOWN;
		}
		if (B) {
			// use Botana's method if there is no native support
			p = UtilFactory.getPrototype().newProver();
			p.setProverEngine(ProverEngine.RECIOS_PROVER);
			as = new AlgebraicStatement(statement, null, p);

			if (as.getResult() == ProofResult.PROCESSING) {
				// Don't do further computations until CAS is ready:
				return ProofResult.PROCESSING;
			}
		}

		HashSet<Variable> variables = new HashSet<Variable>();

		if (!B) {
			try {
				variables = s.getFreeVariables();
			} catch (NoSymbolicParametersException e) {
				return ProofResult.UNKNOWN;
			}
		} else {
			List<GeoElement> freePoints = ProverBotanasMethod
					.getFreePoints(statement);
			Iterator<GeoElement> it = freePoints.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				Variable[] vars = ((SymbolicParametersBotanaAlgo) geo)
						.getBotanaVars(geo);
				variables.add(vars[0]);
				variables.add(vars[1]);
				/*
				 * This is not automatically set in Botana's prover. Consider do
				 * this setting there instead of here. TODO.
				 */
				vars[0].setTwin(vars[1]);
				vars[1].setTwin(vars[0]);
				vars[0].setParent(geo);
				vars[1].setParent(geo);
				/*
				 * Consider using the same way also in Botana's prover for the
				 * other method. TODO.
				 */
			}
		}


		// setting two points fixed (the first to (0,0) and the second to (0,1))
		// all other variables are stored in freeVariables
		Iterator<Variable> it = variables.iterator();
		HashMap<Variable, BigInteger> values = new HashMap<Variable, BigInteger>();
		TreeSet<Variable> fixedVariables = new TreeSet<Variable>(
				new Comparator<Variable>() {
					public int compare(Variable v1, Variable v2) {
						String nameV1, nameV2;
						if (v1.getParent() == null
								|| (nameV1 = v1.getParent().getLabel(
										StringTemplate.defaultTemplate)) == null) {
							if (v2.getParent() == null
									|| v1.getParent().getLabel(
											StringTemplate.defaultTemplate) == null) {
								return v1.compareTo(v2);
							}
							return -1;
						}
						if (v2.getParent() == null
								|| (nameV2 = v2.getParent().getLabel(
										StringTemplate.defaultTemplate)) == null) {
							return 1;
						}
						int compareNames = nameV1.compareTo(nameV2);
						if (compareNames == 0) {
							return v1.compareTo(v2);
						}
						return compareNames;
					}
				});
		HashSet<Variable> freeVariables = new HashSet<Variable>();
		while (it.hasNext()) {
			Variable fv = it.next();
			if (fv.getTwin() == null || !variables.contains(fv.getTwin())) {
				freeVariables.add(fv);
				continue;
			}
			fixedVariables.add(fv);
		}

		it = fixedVariables.iterator();
		int nrOfFixedPoints = 0;
		GeoElement fixedElement1 = null, fixedElement2 = null;
		while (it.hasNext()) {
			Variable var;
			if (nrOfFixedPoints == 0) {
				var = it.next();
				values.put(var, BigInteger.ZERO);
				values.put(it.next(), BigInteger.ZERO);
				fixedElement1 = var.getParent();
				nrOfFixedPoints = 1;
			} else if (nrOfFixedPoints == 1) {
				var = it.next();
				values.put(var, BigInteger.ZERO);
				values.put(it.next(), BigInteger.ONE);
				fixedElement2 = var.getParent();
				nrOfFixedPoints = 2;
			} else {
				freeVariables.add(it.next());
			}
		}

		if (nrOfFixedPoints == 1) {
			fixedPoints = new GeoElement[1];
			fixedPoints[0] = fixedElement1;
		} else if (nrOfFixedPoints == 2) {
			fixedPoints = new GeoElement[2];
			fixedPoints[0] = fixedElement1;
			fixedPoints[1] = fixedElement2;
		}

		int nrFreeVariables = freeVariables.size();
		if (nrFreeVariables > 5) {
			// It would take too much time, it's better to find another method.
			// TODO: This is not a problem in the method, it is in the
			// implementation.
			// FIXME: Make the implementation faster.
			Log.debug("Recio's method is currently disabled when # of free variables > 5");
			return ProofResult.UNKNOWN;
		}

		int[] degs;
		try {
			degs = s.getDegrees();
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
		int deg = 0;
		for (int i : degs) {
			deg = Math.max(deg, i);
		}

		switch (nrFreeVariables) {
		case 0:
			return compute0d(values, s, as);
		case 1:
			return compute1d(freeVariables, values, deg, s, as);
		case 2:
			return compute2d(freeVariables, values, deg, s, as);
		default:
			return computeNd(freeVariables, values, deg, s, as);
		}

	}

	private static ProofResult compute0d(HashMap<Variable, BigInteger> values,
			SymbolicParameters s, AlgebraicStatement as) {
		if (as != null) {
			// use Botana's method
			HashMap<Variable, Long> substitutions = new HashMap<Variable, Long>();
			for (Variable v : values.keySet()) {
				// FIXME: Change Long in Variable to BigInteger
				substitutions.put(v, values.get(v).longValue());
			}
			ProverSettings proverSettings = ProverSettings.get();
			ExtendedBoolean solvable = Polynomial.solvable(as.polynomials
					.toArray(new Polynomial[as.polynomials.size()]),
					substitutions, as.geoStatement.getKernel(),
					proverSettings.transcext);
			Log.debug("Recio meets Botana:" + substitutions);
			if (solvable.boolVal()) {
				return ProofResult.FALSE;
			}
		} else
		try {
			BigInteger[] exactCoordinates = s.getExactCoordinates(values);
			for (BigInteger result : exactCoordinates) {
				if (!result.equals(BigInteger.ZERO)) {
					return ProofResult.FALSE;
				}
			}
		} catch (NoSymbolicParametersException e) {
			return ProofResult.UNKNOWN;
		}
		return ProofResult.TRUE;
	}

	private static ProofResult compute1d(final HashSet<Variable> freeVariables,
			final HashMap<Variable, BigInteger> values, final int deg,
			final SymbolicParameters s, AlgebraicStatement as) {
		Variable variable = freeVariables.iterator().next();
		for (int i = 1; i <= deg + 2; i++) {
			values.put(variable, BigInteger.valueOf(i));
			if (as != null) {
				// use Botana's method
				HashMap<Variable, Long> substitutions = new HashMap<Variable, Long>();
				for (Variable v : values.keySet()) {
					// FIXME: Change Long in Variable to BigInteger
					substitutions.put(v, values.get(v).longValue());
				}
				ProverSettings proverSettings = ProverSettings.get();
				ExtendedBoolean solvable = Polynomial.solvable(as.polynomials
						.toArray(new Polynomial[as.polynomials.size()]),
						substitutions, as.geoStatement.getKernel(),
						proverSettings.transcext);
				Log.debug("Recio meets Botana: #" + i + " " + substitutions);
				if (solvable.boolVal()) {
					return ProofResult.FALSE;
				}
			} else
				try {
				BigInteger[] exactCoordinates = s.getExactCoordinates(values);
				for (BigInteger result : exactCoordinates) {
					if (!result.equals(BigInteger.ZERO)) {
						return ProofResult.FALSE;
					}
				}
			} catch (NoSymbolicParametersException e) {
				return ProofResult.UNKNOWN;
			}
		}
		return ProofResult.TRUE;
	}

	private static ProofResult compute2d(final HashSet<Variable> freeVariables,
			final HashMap<Variable, BigInteger> values, final int deg,
			final SymbolicParameters s, AlgebraicStatement as) {
		Variable[] variables = new Variable[freeVariables.size()];
		Iterator<Variable> it = freeVariables.iterator();
		for (int i = 0; i < variables.length; i++) {
			variables[i] = it.next();
		}

		int nrOfTests = ((deg + 2) * (deg + 1)) / 2;
		Log.debug("nr of tests: " + nrOfTests);
		int caseno = 0;
		for (int i = 1; i < /* = */deg + 2; i++) {
			for (int j = 1; j <= i; j++) {
				caseno++;
				values.put(variables[0],
						BigInteger.valueOf((deg + 2 - i) * (deg + 2 - j)));
				values.put(variables[1], BigInteger.valueOf(i * j));

				if (as != null) {
					// use Botana's method
					HashMap<Variable, Long> substitutions = new HashMap<Variable, Long>();
					for (Variable v : values.keySet()) {
						// FIXME: Change Long in Variable to BigInteger
						substitutions.put(v, values.get(v).longValue());
					}
					ExtendedBoolean solvable = Polynomial
							.solvable(as.polynomials
							.toArray(new Polynomial[as.polynomials.size()]),
							substitutions, as.geoStatement.getKernel(),
							ProverSettings.get().transcext);
					Log.debug("Recio meets Botana: #" + caseno + " "
							+ substitutions);
					if (solvable.boolVal()) {
						return ProofResult.FALSE;
					}
				} else
					try {
					BigInteger[] exactCoordinates = s
							.getExactCoordinates(values);
					for (BigInteger result : exactCoordinates) {
						if (!result.equals(BigInteger.ZERO)) {
							return ProofResult.FALSE;
						}
					}
				} catch (NoSymbolicParametersException e) {
					return ProofResult.UNKNOWN;
				}
			}
		}
		return ProofResult.TRUE;
	}

	/**
	 * More complicated calculations are done by multiple threads in desktop
	 * 
	 * @param freeVariables
	 *            The free variables ruling the construction
	 * @param values
	 *            The values for the fixed variables (If e.g. one point gets
	 *            fixed coordinates (0,0) and another (0,1)
	 * @param deg
	 *            The bound for the degree of the statement
	 * @param s
	 *            The Symbolic parameters class that is used to test the
	 *            statement for a fixed point
	 * @param as
	 *            The algebraic translation of the statement, if null, use
	 *            native computations (by Weitzhofer), otherwise use the Botana
	 *            equations (by Kovacs/Solyom-Gecse)
	 * @return the result of the proof
	 */

	protected abstract ProofResult computeNd(
			final HashSet<Variable> freeVariables,
			final HashMap<Variable, BigInteger> values, final int deg,
			final SymbolicParameters s, AlgebraicStatement as);

	/**
	 * Returns the elements which are fixed by Recio's method prover
	 * 
	 * @return the fixed elements
	 */
	public static GeoElement[] getFixedPoints() {
		return fixedPoints;
	}

}
