package geogebra.common.cas.mpreduce;

import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Inspecting;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;

/**
 * This enum represents the Reduce packages that are not loaded when Reduce
 * starts, but on demand. Each package remembers whether it was already loaded.
 * 
 * @author zbynek
 * 
 */
public enum ReducePackage {
	/** loads specfn package */
	SPECFN(null),
	/** loads defint (definite integral) package */
	DEFINT(SPECFN),
	/** loads odesolve package (solveode, newarbconst) */
	ODESOLVE(SPECFN),
	/** loads groebner package (groebner cmd, prover) */
	GROEBNER(null),
	/** loads taylor package */
	TAYLOR(null);
	private boolean loaded = false;
	private ReducePackage parent;

	private ReducePackage(ReducePackage parent) {
		this.parent = parent;
	}

	/**
	 * Load the package in given CAS
	 * 
	 * @param cas
	 *            cas
	 */
	public void load(CASmpreduce cas) {
		if (loaded || !cas.isInitialized())
			return;
		if (parent != null)
			parent.load(cas);
		try {
			loaded = true;
			App.debug("loading" + name());
			cas.evaluateRaw("load_package " + name().toLowerCase());
			App.debug("done");
		} catch (Throwable e) {
			App.error("Could not load " + name());
		}

	}
	/** Checks for special funcions (beta, gamma, zeta) **/
	public enum SpecFnInspecting implements Inspecting {
		/** Singleton instance*/
		INSTANCE;
		public boolean check(ExpressionValue v) {
			if (v == null || !v.isExpressionNode())
				return false;
			Operation op = ((ExpressionNode) v).getOperation();
			return op == Operation.GAMMA || op == Operation.BETA
					|| op == Operation.ZETA
					|| op == Operation.GAMMA_INCOMPLETE_REGULARIZED
					|| op == Operation.BETA_INCOMPLETE_REGULARIZED
					|| op == Operation.GAMMA_INCOMPLETE
					|| op == Operation.BETA_INCOMPLETE || op == Operation.PSI
					|| op == Operation.POLYGAMMA || op == Operation.ERF
					|| op == Operation.EI || op == Operation.CI
					|| op == Operation.SI;
		}
	}
}
