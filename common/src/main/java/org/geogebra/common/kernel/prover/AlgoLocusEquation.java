/**
 * 
 */
package org.geogebra.common.kernel.prover;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.cas.giac.CASgiac.CustomFunctions;
import org.geogebra.common.cas.singularws.SingularWebService;
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
		AlgebraicStatement as = ProverBotanasMethod
				.translateConstructionAlgebraically(
						implicit ? implicitLocus : locusPoint, movingPoint,
						implicit, this);
		if (as == null) {
			Log.debug("Cannot compute locus equation (yet?)");
			return null;
		}

		return locusEqu(as);
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

	/**
	 * Compute the coefficients of the implicit curve for the envelope equation.
	 * 
	 * @param as
	 *            the algebraic statement structure
	 * @return the implicit curve as a string
	 */
	public String locusEqu(AlgebraicStatement as) {
		StringBuilder sb = new StringBuilder();

		String polys = as.getPolys();
		String elimVars = as.getElimVars();
		String varx = as.curveVars[0].toString();
		String vary = as.curveVars[1].toString();
		String vars = varx + "," + vary;

		String PRECISION = Long.toString(kernel.precision());
		Log.debug("PRECISION = " + PRECISION);

		/* Use Singular if it is enabled. TODO: Implement this. */
		SingularWebService singularWS = kernel.getApplication().getSingularWS();
		if (singularWS != null && singularWS.isAvailable()) {

			String locusLib = singularWS.getLocusLib();

			/*
			 * Constructing the Singular script. This code contains a modified
			 * version of Francisco Botana's locusdgto() and envelopeto()
			 * procedures in the grobcov library. I.e. we no longer use these
			 * two commands, but locusto(), locus() and locusdg() only. We use
			 * one single Singular call instead of two (as above for Giac).
			 * Computation of the Jacobian is maybe slower here.
			 * 
			 * At the moment this code is here for backward compatibility only.
			 * It is not used in the web version and can be invoked only by
			 * forcing SingularWS on startup. TODO: Consider implementing
			 * Singular's grobcov library in Giac---it may produce better
			 * envelopes.
			 */

			/*
			 * Convert v1-a,v2-b type ideals to (v1-a)^2+(v2-b)^2. This works
			 * also in general, not only for linear polys.
			 */
			sb.append(
					"proc point_to_0circle(ideal l) { if (size(l)==1) {return(l[1]);} if (size(l)==2) {return((l[1])^2+(l[2])^2));} return 1; }; ");
			sb.append(
					"LIB \"" + locusLib + ".lib\";ring r=(0,").append(vars)
					.append("),(" + elimVars)
					.append("),dp;").append("short=0;ideal I=" + polys)
					.append(";def Gp=grobcov(I);list l="
							+ singularWS.getLocusCommand() + "(Gp);");
			/*
			 * If Gp is an empty list, then there is no locus, so that we return
			 * 0=-1.
			 */
			sb.append("if(size(l)==0){print(\"{{1,1,1},{1,1,1,1}}\");exit;}")
					.append("poly pp=1; int i; for (i=1; i<=size(l); i++)")
					.append("{ if ((string(l[i][3])==\"Normal\") || (string(l[i][3])==\"Accumulation\")) { pp=pp*point_to_0circle(l[i][1]); } }")
					.append("string s=string(pp);int sl=size(s);string pg=\"poly p=\"+s[2,sl-2];")
					.append("ring rr=0,(").append(vars)
					.append("),dp;execute(pg);")
					.append("string out=sprintf(\"%s,%s,%s\",size(coeffs(p,")
					.append(varx).append(")),size(coeffs(p,").append(vary)
					.append(")),").append("coeffs(coeffs(p,").append(varx)
					.append("),").append(vary).append("));");

			/*
			 * Temporary workaround by creating dummy factor, because the output
			 * is not factorized (that is, it may not produce nice plots in some
			 * cases:
			 */
			sb.append("sprintf(\"{{%s},{1,%s}}\",out,out);").toString();

			Log.trace("Input to singular: " + sb);
			String result;
			try {
				result = kernel.getApplication().getSingularWS()
						.directCommand(sb.toString());
			} catch (Throwable e) {
				Log.error("Error on running Singular code");
				return null;
			}
			Log.trace("Output from singular: " + result);

			return result;
		}

		/* Otherwise use Giac. */
		sb.append(CustomFunctions.LOCUS_EQU).append("([").append(polys)
				.append("],[").append(elimVars).append("],").append(PRECISION)
				.append(",").append(",").append(as.curveVars[0]).append(",")
				.append(as.curveVars[1]).append(")");

		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		try {
			String result = cas.getCurrentCAS().evaluateRaw(sb.toString());
			Log.trace("Output from giac: " + result);
			return result;
		} catch (Throwable ex) {
			Log.error("Error on running Giac code");
			return null;
		}
	}

}
