package org.geogebra.web.html5.kernel;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.debug.Log;

/**
 * A non-threaded version of Recio's method.
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class ProverReciosMethod extends AbstractProverReciosMethod {

	@Override
	protected final ProofResult computeNd(HashSet<Variable> freeVariables,
			HashMap<Variable, BigInteger> values, int deg,
			SymbolicParameters s, AlgebraicStatement as) {
		int n = freeVariables.size();

		Variable[] variables = new Variable[n];
		Iterator<Variable> it = freeVariables.iterator();
		for (int i = 0; i < n; i++) {
			variables[i] = it.next();
		}

		int[] indices = new int[n];
		for (int i = 0; i < n; i++) {
			indices[i] = n - i;
		}

		boolean indicesChanged;
		int nrOfTests = 0, changedIndex = n - 1;
		BigInteger[][] cache = new BigInteger[n][n];

		do {

			for (int i = 0; i < n; i++) {
				BigInteger result;

				if (changedIndex == n - 1) {
					result = BigInteger.ONE;
				} else {
					result = cache[i][changedIndex + 1];
				}

				for (int j = changedIndex; j >= 0; j--) {
					result = result.multiply((BigInteger.valueOf(n)
					        .multiply(BigInteger.valueOf(indices[j])))
					        .subtract(BigInteger.valueOf(i)));
					cache[i][j] = result;
				}
				values.put(variables[i], result);
			}

			nrOfTests++;

			if (as != null) {
				// use Botana's method
				HashMap<Variable, Long> substitutions = new HashMap<Variable, Long>();
				for (Variable v : values.keySet()) {
					// FIXME: Change Long in Variable to BigInteger
					substitutions.put(v, values.get(v).longValue());
				}
				Boolean solvable = Polynomial.solvable(as.polynomials
						.toArray(new Polynomial[as.polynomials.size()]),
						substitutions, as.geoStatement.getKernel(),
						ProverSettings.transcext);
				Log.debug("Recio meets Botana #" + nrOfTests + ": "
						+ substitutions);
				if (solvable) {
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

			indicesChanged = false;

			for (int i = 0; i < n; i++) {
				if (indices[i] < (deg - i + n)) {
					indices[i]++;
					for (int j = 0; j < i; j++) {
						indices[j] = indices[i] + i - j;
					}
					changedIndex = i;
					indicesChanged = true;
					break;
				}
			}

		} while (indicesChanged);

		Log.debug(nrOfTests + " tests performed.");
		Log.debug("n: " + n);
		Log.debug("deg: " + deg);

		return ProofResult.TRUE;
	}

}
