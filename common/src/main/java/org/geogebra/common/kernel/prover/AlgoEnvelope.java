package org.geogebra.common.kernel.prover;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.cas.giac.CASgiac.CustomFunctions;
import org.geogebra.common.cas.singularws.SingularWebService;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;
import org.geogebra.common.util.debug.Log;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> The Singular computations are
 *         provided by Francisco Botana and the grobcov library by Antonio
 *         Montes & al. Based on Sergio's LocusEquation. Works out the equation
 *         for a given envelope.
 */
public class AlgoEnvelope extends AlgoElement implements UsesCAS {

	private GeoPoint movingPoint;
	private GeoElement path;
	/**
	 * class name
	 */
	public static final String CLASS_NAME = "AlgoEnvelope";
	private GeoImplicit geoPoly;
	private GeoElement[] efficientInput, standardInput;
	private String efficientInputFingerprint;

	/**
	 * Constructor.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param path
	 *            path
	 * @param movingPoint
	 *            moving point
	 */
	public AlgoEnvelope(Construction cons, String label, GeoElement path,
			GeoPoint movingPoint) {
		this(cons, path, movingPoint);
		this.geoPoly.setLabel(label);
	}

	/**
	 * Constructor.
	 * 
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param movingPoint
	 *            moving point
	 */
	public AlgoEnvelope(Construction cons, GeoElement path,
			GeoPoint movingPoint) {
		super(cons);

		this.movingPoint = movingPoint;
		this.path = path;

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
		inSet.add(this.movingPoint.getPath().toGeoElement());

		// we need all independent parents of Q PLUS
		// all parents of Q that are points on a path

		Iterator<GeoElement> it = this.path.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isIndependent() || geo.isPointOnPath()) {
				inSet.add(geo);
			}
		}
		// remove P from input set!
		inSet.remove(movingPoint);

		/*
		 * We need to create a new object from "linear". E.g., if it is a
		 * circle, we have to define an equation for the circle by putting x and
		 * y the free variables.
		 */

		efficientInput = new GeoElement[inSet.size()];
		efficientInput = inSet.toArray(efficientInput);

		standardInput = new GeoElement[2];
		standardInput[0] = this.path;
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
	}

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
		computeEnvelope();
	}

	/**
	 * Compute the locus equation curve and put into geoPoly.
	 * 
	 */
	public void computeEnvelope() {
		String result = null;
		try {
			result = getImplicitPoly();
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
	 * Set up dependencies for the input and output objects.
	 */
	protected void setInputOutputEnvelope() {

		TreeSet<GeoElement> inSet = new TreeSet<GeoElement>();
		inSet.add(this.movingPoint);
		Iterator<GeoElement> it = this.path.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isIndependent() || geo.isPointOnPath()) {
				inSet.add(geo);
			}
		}
		inSet.remove(movingPoint);

		efficientInput = new GeoElement[inSet.size()];
		efficientInput = inSet.toArray(efficientInput);

		standardInput = new GeoElement[2];
		standardInput[0] = this.path;
		standardInput[1] = this.movingPoint;

		setOutputLength(1);
		setOutput(0, this.geoPoly.toGeoElement());

		setEfficientDependencies(standardInput, efficientInput);
		efficientInputFingerprint = fingerprint(efficientInput);

	}

	private String getImplicitPoly() throws Throwable {
		SingularWebService sws = kernel.getApplication().getSingularWS();
		String locusLib = sws == null ? "" : sws.getLocusLib();

		/*
		 * First we create a virtual locus point on the path object. This is
		 * done with AlgoPointOnPath. Then we retrieve the corresponding
		 * equation to this virtual locus point.
		 */

		GeoPoint locusPoint = new GeoPoint(cons);
		AlgoPointOnPath apop = new AlgoPointOnPath(cons, (Path) path, 1, 1);
		locusPoint.setParentAlgorithm(apop);
		/*
		 * Now we collect all the restriction equations except for the linear
		 * itself. This is exactly the same as in AlgoLocusEquation.
		 */
		AlgebraicStatement as = ProverBotanasMethod
				.translateConstructionAlgebraically(locusPoint,
				movingPoint, false, this);
		// It is safe to remove the virtual locus point here.
		locusPoint.remove();

		if (as == null) {
			Log.debug("Cannot compute locus equation (yet?)");
			return null;
		}
		Kernel k = movingPoint.getKernel();

		// TODO: Implement Singular computation also.
		GeoGebraCAS cas = (GeoGebraCAS) k.getGeoGebraCAS();

		if (true) {
		try {
			String impccoeffs = cas.getCurrentCAS().evaluateRaw(
						envelopeEqu(as).toString());
			Log.trace("Output from giac: " + impccoeffs);
			return impccoeffs;
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.debug("Cannot compute locus equation (yet?)");
			return null;
		}
		}

		StringBuilder script = new StringBuilder();
		String varlist = "", vars = "", polys = "", varx = "", vary = "";

		/*
		 * Constructing the Singular script. This code contains a modified
		 * version of Francisco Botana's locusdgto() and envelopeto() procedures
		 * in the grobcov library. I.e. we no longer use these two commands, but
		 * locusto(), locus() and locusdg() only. We use one single Singular
		 * call instead of two (as above for Giac). Computation of the Jacobian
		 * is maybe slower here.
		 * 
		 * At the moment this code is here for backward compatibility only. It
		 * is not used in the web version and can be invoked only by forcing
		 * SingularWS on startup. TODO: Consider implementing Singular's grobcov
		 * library in Giac---it may produce better envelopes.
		 */
		script.append("proc mylocusdgto(list L) {" + "poly p=1;"
				+ "int i; int j; int k;"
				+ "for(i=1;i<=size(L);i++) { if(L[i][3]<>\"Degenerate\")"
				+ " { if(size(L[i][1])>1) {p=p*((L[i][1][1])^2+(L[i][1][2])^2);}"
				+ "else {p=p*L[i][1][1];}" + "} } return(p); }");
		script.append("proc myenvelopeto (list GG) {" + "list GGG;"
				+ "if (GG[1][2][1]<>1) { GGG=delete(GG,1); }"
				+ "else { GGG=GG; };" + "string SLo=locusto(locus(GGG));"
				+ "if (find(SLo,\"Normal\") == 0 and find(SLo,\"Accumulation\") == 0 and find(SLo,\"Special\") == 0)"
				+ "{ return(1); }"
				+ "else { return(mylocusdgto(locus(GGG))); } }");
		script.append("LIB \"" + locusLib + ".lib\";ring r=(0," + varlist
				+ "),(" + vars)
				.append("),dp;short=0;ideal m=");
		script.append(polys);
		script.append(";poly D=det(jacob(m));ideal S=" + polys
				+ ",D;list e=myenvelopeto(grobcov(S));");
		/*
		 * This trick is required to push the result polynomial to the new ring
		 * world:
		 */
		script.append("string ex=\"poly p=\" + string(e[1]);");
		script.append("ring rr=0,(" + varlist + "),dp;");
		script.append("execute(ex);");
		/*
		 * Now we obtain the coefficients (see exactly the same code for locus
		 * equation):
		 */
		script.append(
				"sprintf(\"%s,%s,%s\",size(coeffs(p," + varx
						+ ")),size(coeffs(p," + vary + ")),")
				.append("coeffs(coeffs(p," + varx + ")," + vary + "));");
		Log.trace("Input to singular: " + script);
		String result = kernel.getApplication().getSingularWS()
				.directCommand(script.toString());
		Log.trace("Output from singular: " + result);
		/* Temporary workaround by creating dummy factor: */
		result = "{{" + result + "},{1," + result + "}}";
		/*
		 * because the output is not factorized (that is, it may not produce
		 * nice plots in some cases.
		 */
		return result;
	}

	@Override
	public Commands getClassName() {
		return Commands.Envelope;
	}

	/**
	 * Compute the coefficients of the implicit curve for the envelope equation.
	 * 
	 * @param as
	 *            the algebraic statement structure
	 * @return the implicit curve as a string
	 */
	public StringBuilder envelopeEqu(AlgebraicStatement as) {
		StringBuilder sb = new StringBuilder();
		boolean useSingular = false;

		/* Use Singular if it is enabled. TODO: Implement this. */
		SingularWebService singularWS = kernel.getApplication().getSingularWS();
		if (singularWS != null && singularWS.isAvailable()) {
			useSingular = true;
		}
		/* Otherwise use Giac. */

		String polys = as.getPolys();
		String elimVars = as.getElimVars();

		String PRECISION = Long.toString(kernel.precision());
		Log.debug("PRECISION = " + PRECISION);

		sb.append(CustomFunctions.ENVELOPE_EQU).append("([").append(polys)
				.append("],[").append(elimVars).append("],").append(PRECISION)
				.append(",").append(as.curveVars[0]).append(",")
				.append(as.curveVars[1]).append(")");
		return sb;
	}

}
