package geogebra.web.kernel.prover;

import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.prover.AbstractProverReciosMethod;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.main.App;
import geogebra.common.util.Prover.ProofResult;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ProverReciosMethod extends AbstractProverReciosMethod {

	@Override
    protected final ProofResult computeNd(HashSet<Variable> freeVariables,
            HashMap<Variable, BigInteger> values, int deg, SymbolicParameters s) {
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
	int nrOfTests = 0, changedIndex=n-1;
	BigInteger[][] cache=new BigInteger[n][n];

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
				changedIndex=i;
				indicesChanged = true;
				break;
			}
		}

	} while (indicesChanged);
	
	App.debug(nrOfTests + " tests performed.");
	App.debug("n: " + n);
	App.debug("deg: " + deg);
	

	return ProofResult.TRUE;
	}

}
