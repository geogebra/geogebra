package org.geogebra.common.kernel.prover;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.cas.giac.CASgiac.CustomFunctions;
import org.geogebra.common.kernel.Construction;
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
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
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
		String locusLib = kernel.getApplication().getSingularWS().getLocusLib();

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

		/*
		 * The equation is not yet in the form we want: the last two variables
		 * should be changed to x and y.
		 */
		String varx = as.curveVars[0].toString();
		String vary = as.curveVars[1].toString();

		// We collect the used x1,x2,... variables (their order is not
		// relevant):
		Polynomial[] allPolys = new Polynomial[as.getPolynomials().size()];
		Iterator<Polynomial> it = as.getPolynomials().iterator();
		int i = 0;
		while (it.hasNext()) {
			Polynomial poly = it.next();
			allPolys[i] = poly;
			i++;
		}

		StringBuilder vars = new StringBuilder();
		String allVars = Polynomial.getVarsAsCommaSeparatedString(allPolys,
				null, null) + ",";
		allVars = allVars.replaceAll(varx + ",", "");
		allVars = allVars.replaceAll(vary + ",", "");

		// trim closing ","
		vars.append(allVars.substring(0, allVars.length() - 1));

		// Obtaining polynomials:
		String polys = Polynomial.getPolysAsCommaSeparatedString(allPolys);

		StringBuilder script = new StringBuilder();

		// Constructing the script.
		// Single points [y-A,x-B] are returned in the form (x-B)^2+(y-A)^2.
		// Empty envelopes are drawn as 0=-1.
		// Multiple curves are drawn as products of the curves.

		String varlist = varx + "," + vary;

		if (locusLib.length() == 0) {
			/*
			 * If there is no Singular support with the Groebner cover package,
			 * then we use Giac and construct the Jacobi matrix on our own. Here
			 * we use two Giac calls, one for the Jacobian and one for the
			 * elimination. Actually, this code is faster than Singular because
			 * of local execution and faster Jacobian computation.
			 */
			script.append("[[");
			script.append("m:=[").append(polys)
					.append("]],[J:=" + CustomFunctions.JACOBI_PREPARE + "(m,["
							+ varlist + "])],[" + CustomFunctions.JACOBI_DET
							+ "(J,[" + varlist + "])]]");
			script.append("[2][0]");

			Log.trace(
					"Input to giac (compute det of Jacobi matrix): " + script);
			GeoGebraCAS cas = (GeoGebraCAS) locusPoint.getKernel()
					.getGeoGebraCAS();
			try {
				String det = cas.getCurrentCAS().evaluateRaw(script.toString());
				if ("?".equals(det)) {
					Log.debug("Cannot compute det of Jacobi matrix (yet?)");
					return null;
				}
				/* Replacing variables. */
				det = det.replaceAll(varx, "x").replaceAll(vary, "y");
				polys = polys.replaceAll(varx, "x").replaceAll(vary, "y");

				Log.trace("Output from giac (compute det of Jacobi matrix): "
						+ det);
				String script2 = cas.getCurrentCAS().createLocusEquationScript(
						polys + "," + det, vars + ",x,y", vars.toString());

				Log.trace("Input to giac: " + script2);
				String result = cas.getCurrentCAS().evaluateRaw(script2);
				return result;

			} catch (Exception ex) {
				Log.debug("Cannot compute envelope (yet?)");
				return null;
			}

		}

		/*
		 * Constructing the Singular script. This code contains a modified
		 * version of Francisco Botana's locusdgto() and envelopeto() procedures
		 * in the grobcov library. I.e. we no longer use these two commands, but
		 * locusto(), locus() and locusdg() only. We use one single Singular
		 * call instead of two (as above for Giac). Computation of the Jacobian
		 * is maybe slower here.
		 * 
		 * At the moment this code is here for backward compatibility only. It
		 * is not used in the web version and be invoked only by forcing
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
		// Temporary workaround by creating dummy factor:
		result = "{{" + result + "},{1," + result + "}}";
		// because it is not factorized.
		return result;
	}

	@Override
	public Commands getClassName() {
		return Commands.Envelope;
	}
}
