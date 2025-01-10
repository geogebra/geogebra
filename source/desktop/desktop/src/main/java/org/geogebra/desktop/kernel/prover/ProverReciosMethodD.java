package org.geogebra.desktop.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.debug.Log;

/**
 * This class can prove a statement by a bounded number of checks. In this
 * desktop version this is done by multiple threads, if the CPU has multiple
 * threads.
 * 
 * @author Simon
 * 
 */
public class ProverReciosMethodD extends AbstractProverReciosMethod {

	private enum TestPointResult {

		/**
		 * The statement is true in the point
		 */
		PASSED,
		/**
		 * The statement is false in the point
		 */
		FALSE,
		/**
		 * An error occurred
		 */
		ERROR
	}

	private PointTester[] pointTesters;
	/**
	 * The queue which contains the coordinates of the points to test
	 */
	final LinkedBlockingQueue<BigInteger[]> coordinatesQueue = new LinkedBlockingQueue<>();
	private AtomicInteger verifiedPoints;
	private boolean stop;
	private boolean errorOccurred;
	private Thread[] threads;

	// stops all working threads
	private void interruptThreads() {
		for (Thread t : threads) {
			t.interrupt();
		}
	}

	/**
	 * Takes the result back from the threads.
	 * 
	 * @param result
	 *            the result of the test point.
	 */
	protected void writeResult(TestPointResult result) {
		switch (result) {
		case PASSED:
			verifiedPoints.incrementAndGet();
			break;
		case ERROR:
			errorOccurred = true;
			//$FALL-THROUGH$
		case FALSE:
			stop = true;
			coordinatesQueue.clear();
		}
	}

	private boolean getErrorOccurred() {
		return errorOccurred;
	}

	@Override
	protected final ProofResult computeNd(HashSet<PVariable> freeVariables,
			HashMap<PVariable, BigInteger> values, int deg, SymbolicParameters s,
			AlgebraicStatement as) {
		int n = freeVariables.size();
		PVariable[] variables = new PVariable[n];
		Iterator<PVariable> it = freeVariables.iterator();
		for (int i = 0; i < n; i++) {
			variables[i] = it.next();
		}

		coordinatesQueue.clear();
		verifiedPoints = new AtomicInteger(0);
		stop = false;
		errorOccurred = false;

		int[] indices = new int[n];
		for (int i = 0; i < n; i++) {
			indices[i] = n - i;
		}

		boolean indicesChanged;
		int nrOfTests = 0, changedIndex = n - 1;
		BigInteger[][] cache = new BigInteger[n][n];
		BigInteger[] coordinates = new BigInteger[n];

		Runtime runtime = Runtime.getRuntime();
		int useProcessors = runtime.availableProcessors() - 1;
		useProcessors = 0; // do not use threads until #3399 is fixed

		pointTesters = new PointTester[useProcessors];
		threads = new Thread[useProcessors];

		for (int i = 0; i < useProcessors; i++) {
			pointTesters[i] = new PointTester(this, values, variables, s);
			threads[i] = new Thread(pointTesters[i],
					"ProverReciosMethod_TestPoints" + i);
			threads[i].start();
		}

		do {
			if (Thread.interrupted()) {
				interruptThreads();
				return ProofResult.UNKNOWN;
			}

			// calculation of the coordinates
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
				coordinates[i] = result;
			}

			nrOfTests++;

			try {
				coordinatesQueue.put(coordinates);
			} catch (InterruptedException e) {
				return ProofResult.UNKNOWN;
			}

			// the following is the loop header
			// the created indices sequence is:
			// [n n-1 n-2 ... 1]
			// [n+1 n-1 n-2 ... 1]
			// ...
			// [n+d n-1 n-2 ... 1]
			// [n+1 n n-2 ... 1]
			// [n+2 n n-2 ... 1]
			// ...
			// [n+d n+d-1 ... d]

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

		} while (indicesChanged && !stop);

		if (stop) {
			interruptThreads();
			if (getErrorOccurred()) {
				return ProofResult.UNKNOWN;
			}
			return ProofResult.FALSE;
		}

		int nrOfChecks = 0;
		boolean wrong = false;

		// if the tests are not finished by the threads
		// we help the threads testing the points.
		while (!stop && verifiedPoints.get() < nrOfTests) {
			if (Thread.interrupted()) {
				interruptThreads();
				return ProofResult.UNKNOWN;
			}

			coordinates = coordinatesQueue.poll();
			if (coordinates == null) {
				continue;
			}
			for (int i = 0; i < coordinates.length; i++) {
				values.put(variables[i], coordinates[i]);
			}

			if (as != null) {
				// use Botana's method
				HashMap<PVariable, BigInteger> substitutions = new HashMap<>();
				for (Entry<PVariable, BigInteger> entry : values.entrySet()) {

					PVariable v = entry.getKey();
					// FIXME: Change Long in Variable to BigInteger
					substitutions.put(v, entry.getValue());
				}
				ExtendedBoolean solvable = PPolynomial.solvable(
						as.getPolynomials()
								.toArray(new PPolynomial[as.getPolynomials().size()]),
						substitutions, as.geoStatement.getKernel(),
						ProverSettings.get().transcext, as.getFreeVariables());
				Log.debug("Recio meets Botana (threaded): " + substitutions);
				if (solvable.boolVal()) {
					wrong = true;
					break;
				}
			} else {
				try {
					BigInteger[] exactCoordinates = s
							.getExactCoordinates(values);

					wrong = false;
					for (BigInteger result : exactCoordinates) {
						nrOfChecks++;
						if (!result.equals(BigInteger.ZERO)) {
							wrong = true;
							break;
						}
					}
				} catch (NoSymbolicParametersException e) {
					writeResult(TestPointResult.ERROR);
					continue;
				}
			}
			if (wrong) {
				writeResult(TestPointResult.FALSE);
			} else {
				writeResult(TestPointResult.PASSED);
			}
		}

		if (stop) {
			// the theorem could not be verified in one point
			if (getErrorOccurred()) {
				return ProofResult.UNKNOWN;
			}
			return ProofResult.FALSE;
		}

		// all points are tested now

		interruptThreads();

		for (int i = 0; i < pointTesters.length; i++) {
			Log.debug(pointTesters[i].nrOfTests + " tests done by thread " + i);
		}
		Log.debug(nrOfChecks + " tests done by main thread");

		return ProofResult.TRUE;

	}

	private final static class PointTester implements Runnable {
		HashMap<PVariable, BigInteger> values;
		PVariable[] variables;
		ProverReciosMethodD prover;
		SymbolicParameters s;
		public int nrOfTests;

		public PointTester(final ProverReciosMethodD prover,
				final HashMap<PVariable, BigInteger> values,
				final PVariable[] variables, final SymbolicParameters s) {
			this.prover = prover;
			this.variables = variables;
			this.values = (HashMap<PVariable, BigInteger>) values.clone();
			this.s = s;
		}

		@Override
		public void run() {
			BigInteger[] coordinates;
			boolean wrong;
			nrOfTests = 0;
			while (!Thread.interrupted()) {

				try {
					coordinates = prover.coordinatesQueue.take();
				} catch (InterruptedException e) {
					return;
				}

				for (int i = 0; i < coordinates.length; i++) {
					this.values.put(variables[i], coordinates[i]);
				}
				try {
					BigInteger[] exactCoordinates = s
							.getExactCoordinates(values);
					nrOfTests++;
					wrong = false;
					for (BigInteger result : exactCoordinates) {
						if (!result.equals(BigInteger.ZERO)) {
							wrong = true;
							break;
						}
					}
				} catch (NoSymbolicParametersException e) {
					prover.writeResult(TestPointResult.ERROR);
					continue;
				}
				if (wrong) {
					prover.writeResult(TestPointResult.FALSE);
				} else {
					prover.writeResult(TestPointResult.PASSED);
				}
			}
		}

	}

}