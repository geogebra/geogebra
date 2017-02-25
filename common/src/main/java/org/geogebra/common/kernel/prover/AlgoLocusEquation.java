/**
 * 
 */
package org.geogebra.common.kernel.prover;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.cas.giac.CASgiac.CustomFunctions;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.util.debug.Log;

/**
 * @author sergio Works out the equation for a given locus.
 */
public class AlgoLocusEquation extends AlgoElement implements UsesCAS {

	private GeoPoint movingPoint, locusPoint;
	private GeoImplicit geoPoly;
	private GeoElement[] efficientInput, standardInput;
	private String efficientInputFingerprint;
	private GeoElement implicitLocus = null;
	private long myPrecision = 0;

	/**
	 * @param cons
	 *            construction
	 * @param locusPoint
	 *            dependent point
	 * @param movingPoint
	 *            moving point
	 */
	public AlgoLocusEquation(Construction cons, GeoPoint locusPoint,
			GeoPoint movingPoint) {
		super(cons);

		this.movingPoint = movingPoint;
		this.locusPoint = locusPoint;
		this.implicitLocus = null;

		this.geoPoly = kernel.newImplicitPoly(cons);

		setInputOutput();
		initialCompute();
	}

	/**
	 * @param cons
	 *            construction
	 * @param implicitLocus
	 *            boolean describing the locus
	 * @param movingPoint
	 *            moving point
	 */
	public AlgoLocusEquation(Construction cons, GeoElement implicitLocus,
			GeoPoint movingPoint) {
		super(cons);

		this.implicitLocus = implicitLocus;
		this.movingPoint = movingPoint;

		this.geoPoly = kernel.newImplicitPoly(cons);

		setInputOutput();
		initialCompute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see geogebra.common.kernel.algos.AlgoElement#setInputOutput()
	 */
	@Override
	protected void setInputOutput() {

		// it is inefficient to have Q and P as input
		// let's take all independent parents of Q
		// and the path as input
		TreeSet<GeoElement> inSet = new TreeSet<GeoElement>();
		Iterator<GeoElement> it;
		standardInput = new GeoElement[2];

		if (implicitLocus != null) {
			inSet.add(this.movingPoint);
			it = this.implicitLocus.getAllPredecessors().iterator();
			standardInput[0] = this.implicitLocus;
		} else {
			inSet.add(this.movingPoint.getPath().toGeoElement());
			it = this.locusPoint.getAllPredecessors().iterator();
			standardInput[0] = this.locusPoint;
		}

		// we need all independent parents of Q PLUS
		// all parents of Q that are points on a path

		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isIndependent() || geo.isPointOnPath()) {
				inSet.add(geo);
			}
		}
		// remove P from input set!
		inSet.remove(movingPoint);

		efficientInput = new GeoElement[inSet.size()];
		efficientInput = inSet.toArray(efficientInput);
		standardInput[1] = this.movingPoint;

		setOutputLength(1);
		setOutput(0, this.geoPoly.toGeoElement());

		setEfficientDependencies(standardInput, efficientInput);
		// Removing extra algos manually:
		Construction c = movingPoint.getConstruction();
		do {
			c.removeFromAlgorithmList(this);
		} while (c.getAlgoList().contains(this));
		// Adding this again:
		c.addToAlgorithmList(this);
		// TODO: consider moving setInputOutput() out from compute()

		efficientInputFingerprint = fingerprint(efficientInput);
		myPrecision = kernel.precision();
	}

	/**
	 * Reset fingerprint to force recomputing the locus equation if the
	 * precision has dramatically changed.
	 * 
	 * @param k
	 *            kernel
	 * @param force
	 *            reset the fingerprint even if the precision has not changed
	 * 
	 * @return true if the fingerprint was reset
	 */
	public boolean resetFingerprint(Kernel k, boolean force) {
		long kernelPrecision = k.precision();
		double precisionRatio = (double) myPrecision / kernelPrecision;
		if (precisionRatio > 5 || precisionRatio < 0.2 || force) {
			Log.debug("myPrecision=" + myPrecision + " kernelPrecision="
					+ kernelPrecision + " precisionRatio=" + precisionRatio);
			efficientInputFingerprint = null;
			myPrecision = kernelPrecision;
			return true;
		}
		return false;

	}

	/*
	 * We use a very hacky way to avoid drawing locus equation when the curve is
	 * not changed. To achieve that, we create a fingerprint of the current
	 * coordinates or other important parameters of the efficient input. (It
	 * contains only those inputs which are relevant in computing the curve,
	 * hence if they are not changed, the curve will not be recomputed.) The
	 * fingerprint function should eventually be improved. Here we assume that
	 * the input objects are always in the same order (that seems sensible) and
	 * the obtained algebraic description changes iff the object does. This may
	 * not be the case if rounding/precision is not as presumed.
	 */
	private static String fingerprint(GeoElement[] input) {
		StringBuilder ret = new StringBuilder();
		int size = input.length;
		for (int i = 0; i < size; ++i) {
			ret.append(input[i]
					.getAlgebraDescription(StringTemplate.defaultTemplate));
			ret.append(",");
		}
		return ret.toString();
	}

	/**
	 * @return the result.
	 */
	public GeoImplicit getPoly() {
		return this.geoPoly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see geogebra.common.kernel.algos.AlgoElement#compute()
	 */
	@Override
	public void compute() {
		if (!kernel.getGeoGebraCAS().getCurrentCAS().isLoaded()) {
			efficientInputFingerprint = null;
			myPrecision = 0;
			return;
		}
		String efficientInputFingerprintPrev = efficientInputFingerprint;
		setInputOutput();
		if (efficientInputFingerprintPrev == null
				|| !efficientInputFingerprintPrev
						.equals(efficientInputFingerprint)) {
			Log.trace(efficientInputFingerprintPrev + " -> "
					+ efficientInputFingerprint);
			initialCompute();
		}
	}

	private void initialCompute() {
		computeExplicitImplicit(implicitLocus != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see geogebra.common.kernel.algos.AlgoElement#getClassName()
	 */
	@Override
	public Commands getClassName() {
		return Commands.LocusEquation;
	}


	private String getImplicitPoly(boolean implicit) throws Throwable {
		AlgebraicStatement as = ProverBotanasMethod.translateConstructionAlgebraically(
				implicit ? implicitLocus : locusPoint, movingPoint, implicit,
				this);
		if (as == null) {
			Log.debug("Cannot compute locus equation (yet?)");
			return null;
		}
		Set<Set<PPolynomial>> eliminationIdeal;
		Kernel k = movingPoint.getKernel();

		eliminationIdeal = PPolynomial
				.eliminate(
						as.getPolynomials()
								.toArray(new PPolynomial[as.getPolynomials()
										.size()]),
						as.substitutions, k, 0, false, true);

		// We implicitly assume that there is one equation here as result.
		PPolynomial result = null;
		Iterator<Set<PPolynomial>> it1 = eliminationIdeal.iterator();
		if (it1.hasNext()) {
			Set<PPolynomial> results1 = it1.next();
			Iterator<PPolynomial> it2 = results1.iterator();
			if (it2.hasNext()) {
				result = it2.next();
			}
		}

		if (result == null) {
			Log.info("No such implicit curve exists (0=-1)");
			return "{{1,1,1},{1,1,1,1}}";
		}

		// Replacing variables to have x and y instead of vx and vy:
		String implicitCurveString = result.toString();
		if (as.curveVars[0] != null) {
			implicitCurveString = implicitCurveString
					.replaceAll(as.curveVars[0].toString(), "x");
		}
		if (as.curveVars[1] != null) {
			implicitCurveString = implicitCurveString
					.replaceAll(as.curveVars[1].toString(), "y");
		}
		Log.trace("Implicit locus equation: " + implicitCurveString);

		// This piece of code has been directly copied from CASgiac.java:
		StringBuilder script = new StringBuilder();
		script.append("[[aa:=").append(implicitCurveString).append("],")
				.append("[bb:=coeffs(" + CustomFunctions.FACTOR_SQR_FREE
						+ "(aa),x)], [sx:=size(bb)], [sy:=size(coeffs(aa,y))],")
				.append("[cc:=[sx,sy]], [for ii from sx-1 to 0 by -1 do dd:=coeff(bb[ii],y);")
				.append("sd:=size(dd); for jj from sd-1 to 0 by -1 do ee:=dd[jj];")
				.append("cc:=append(cc,ee); od; for kk from sd to sy-1 do ee:=0;")
				.append("cc:=append(cc,ee); od; od],") // cc][6]");
				// Add the coefficients for the factors also to improve
				// visualization:
				.append("[ff:=factors(" + CustomFunctions.FACTOR_SQR_FREE
						+ "(aa))], [ccf:=[size(ff)/2]], ")
				.append("[for ll from 0 to size(ff)-1 by 2 do aaf:=ff[ll]; bb:=coeffs(aaf,x); sx:=size(bb);")
				.append(" sy:=size(coeffs(aaf,y)); ccf:=append(ccf,sx,sy);")
				.append(" for ii from sx-1 to 0 by -1 do dd:=coeff(bb[ii],y); sd:=size(dd);")
				.append(" for jj from sd-1 to 0 by -1 do ee:=dd[jj]; ccf:=append(ccf,ee);")
				.append(" od; for kk from sd to sy-1 do ee:=0; ccf:=append(ccf,ee); od; od; od],")
				.append("[cc,ccf]][9]");

		GeoGebraCAS cas = (GeoGebraCAS) k.getGeoGebraCAS();
		try {
			String impccoeffs = cas.getCurrentCAS()
					.evaluateRaw(script.toString());
			Log.trace("Output from giac: " + impccoeffs);
			return impccoeffs;
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.debug("Cannot compute locus equation (yet?)");
			return null;
		}
	}

	/**
	 * Compute the locus equation curve and put into geoPoly.
	 * 
	 * @param implicit
	 *            if the computation will be done for an implicit locus
	 */
	public void computeExplicitImplicit(boolean implicit) {
		String result = null;
		try {
			result = getImplicitPoly(implicit);
		} catch (Throwable ex) {
			ex.printStackTrace();
			Log.debug("Cannot compute implicit curve (yet?)");
		}

		if (result != null) {
			try {
				GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
				this.geoPoly.setCoeff(cas.getCurrentCAS()
						.getBivarPolyCoefficientsAll(result));
				this.geoPoly.setDefined();

				// Timeout => set undefined
			} catch (Exception e) {
				this.geoPoly.setUndefined();
			}
		} else {
			this.geoPoly.setUndefined();
		}
	}

}
