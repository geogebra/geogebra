/**
 * 
 */
package org.geogebra.common.kernel.prover;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.cas.giac.CASgiac.CustomFunctions;
import org.geogebra.common.cas.singularws.SingularWebService;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoLocusND;
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
		TreeSet<GeoElement> inSet = new TreeSet<>();
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
			Log.debug("resetFingerprint: myPrecision=" + myPrecision + " kernelPrecision="
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
			Log.debug("CAS is not yet loaded => fingerprint set to null");
			efficientInputFingerprint = null;
			myPrecision = 0;
			return;
		}
		String efficientInputFingerprintPrev = efficientInputFingerprint;
		setInputOutput();
		if (efficientInputFingerprintPrev == null
				|| !efficientInputFingerprintPrev
						.equals(efficientInputFingerprint)) {
			Log.debug(efficientInputFingerprintPrev + " -> "
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
			resetFingerprint(kernel, true);
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
		if (!implicit) {
			if (!AlgoLocusND.validLocus(locusPoint, movingPoint)) {
				this.geoPoly.setUndefined();
				return;
			}
		}
		double startTime = UtilFactory.getPrototype().getMillisecondTime();
		String result = null;
		try {
			result = getImplicitPoly(implicit);
		} catch (Throwable ex) {
			Log.debug(ex);
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

		int elapsedTime = (int) (UtilFactory.getPrototype().getMillisecondTime()
				- startTime);
		/*
		 * Don't remove this. It is needed for automated testing. (String match
		 * is assumed.)
		 */
		Log.debug("Benchmarking: " + elapsedTime + " ms");
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

			String subindex;
			String locusLib = singularWS.getLocusLib();
			String locusStatement;
			if (singularWS.getLocusLib()
					.equals("/home/singularws/grobcov-20170620")) {
				locusStatement = "list l=locus(I);\n";
				subindex = "[2]";
			} else {
				locusStatement = "def Gp=grobcov(I);list l="
						+ singularWS.getLocusCommand() + "(Gp);\n";
				subindex = "";
			}
			
			// This is a very painful way for maintaining the code.
			// Consider implementing loadTextFile in App instead. TODO
			String functions = ""
					+ "// Functions used in Singular                                                                                           \n"
					+ "// Author: Zoltan Kovacs <zoltan@geogebra.org>                                                                          \n"
					+ "                                                                                                                        \n"
					+ "// Farey algorithm to find best rational approximation for 0<x<=1 with at most denominator N.                           \n"
					+ "// The result is the list of the numerator and the denomimnator.                                                        \n"
					+ "// https://www.johndcook.com/blog/2010/10/20/best-rational-approximation/                                               \n"
					+ "proc fareyAlgo(number x, int N) {                                                                                       \n"
					+ "  number a,b,c,d,mediant;                                                                                               \n"
					+ "  a=0; b=1; c=1; d=1;                                                                                                   \n"
					+ "  while (b <= N and d <= N) {                                                                                           \n"
					+ "    if (x==1) { return (list(1,1)); }                                                                                   \n"
					+ "    mediant=(a+c)/(b+d);                                                                                                \n"
					+ "    if (x==mediant) {                                                                                                   \n"
					+ "      if (b + d <= N) { return (list(a+c,b+d)); }                                                                       \n"
					+ "      else {                                                                                                            \n"
					+ "        if (d>b) { return (list(c,d)); }                                                                                \n"
					+ "        else { return (list(a,b)); }                                                                                    \n"
					+ "        }                                                                                                               \n"
					+ "      }                                                                                                                 \n"
					+ "    else {                                                                                                              \n"
					+ "      if (x>mediant) { a=a+c; b=b+d; }                                                                                  \n"
					+ "      else { c=a+c; d=b+d; }                                                                                            \n"
					+ "      }                                                                                                                 \n"
					+ "    }                                                                                                                   \n"
					+ "  if (b>N) { return (list(c,d)); }                                                                                      \n"
					+ "  else { return (list(a,b)); }                                                                                          \n"
					+ "  }                                                                                                                     \n"
					+ "                                                                                                                        \n"
					+ "// Sign of number x                                                                                                     \n"
					+ "proc sgn(number x) {                                                                                                    \n"
					+ "  if (x<0) { return (-1); }                                                                                             \n"
					+ "  if (x>0) { return (1); }                                                                                              \n"
					+ "  return (0);                                                                                                           \n"
					+ "  }                                                                                                                     \n"
					+ "                                                                                                                        \n"
					+ "// Compute a good rational approximation for n with precision prec (which is a big integer).                            \n"
					+ "// The result is a rational as string.                                                                                  \n"
					+ "proc number2rational(number n, int prec) {                                                                              \n"
					+ "  if (n==0) { return (\"0\"); }                                                                                         \n"
					+ "  int num=sgn(n);                                                                                                       \n"
					+ "  while (absValue(n)>1) { num = num * 2; n = n / 2; }                                                                   \n"
					+ "  list f=fareyAlgo(absValue(n),prec);                                                                                   \n"
					+ "  int g=gcd(num*int(f[1]),int(f[2]));                                                                                   \n"
					+ "  return (string(num*f[1]/g)+\"/\"+string(f[2]/g));                                                                     \n"
					+ "  }                                                                                                                     \n"
					+ "                                                                                                                        \n"
					+ "// Create a polynomial to desribe a 0 dimensional ideal geometrically.                                                  \n"
					+ "// It constructs a ring R of complex numbers by using solve.lib and                                                     \n"
					+ "// converts the real numbers to rationals. Also another ring rrr is constructed                                         \n"
					+ "// over the integers and the parameter variables (they are for the final locus).                                        \n"
					+ "// The result is a list of \"poly as string\".                                                                          \n"
					+ "proc point_to_0circle(ideal l, int prec) {                                                                              \n"
					+ "  if (size(l)==1) {return(string(list(l[1])));}                                                                                 \n"
					+ "  if (size(l)>1) {                                                                                                      \n"
					+ "    ring r=basering;                                                                                                    \n"
					+ "    string s=\"def R=solve([\"+string(l[1]);                                                                            \n"
					+ "    int ii;                                                                                                             \n"
					+ "    for (ii=2; ii<=size(l); ii++) { s=s+\",\" + string(l[ii]); }                                                        \n"
					+ "    s=s+\"],\\\"nodisplay\\\");\";                                                                                      \n"
					+ "    string ringdef=\"ring rrr=0,(\"+parstr(1)+\",\"+parstr(2)+\"),dp\";                                                 \n"
					+ "    execute(ringdef);                                                                                                   \n"
					+ "    execute(s);                                                                                                         \n"
					+ "    setring R;                                                                                                          \n"
					+ "    string ps=\"list pp=\";                                                                                             \n"
					+ "    for (ii=1; ii<=size(SOL); ii++) {                                                                                   \n"
					+ "      ps = ps + \"cleardenom((\" + string(var(1)) + \"-(\" + number2rational(SOL[ii][1],prec) + \"))^2+(\"              \n"
					+ "              + string(var(2)) + \"-(\" + number2rational(SOL[ii][2],prec) + \"))^2)\";                                 \n"
					+ "      if (ii<size(SOL)) { ps = ps + \",\"; }                                                                            \n"
					+ "      }                                                                                                                 \n"
					+ "    setring rrr;                                                                                                        \n"
					+ "    execute(ps)                                                                                                         \n"
					+ "    return(string(pp));                                                                                                 \n"
					+ "    }                                                                                                                   \n"
					+ "  return list(1);                                                                                                       \n"
					+ "}                                                                                                                       \n"
					+ "                                                                                                                        \n"
					+ "proc impossible() { return (\"{{1,1,1},{1,1,1,1}}\"); }                                                                 \n"
					+ "                                                                                                                        \n"
					+ "proc flattenCoeffs(poly p, string vv1, string vv2) {                                                                    \n"
					+ "  string outx=\"string out=sprintf(\\\"%s,%s,%s\\\",size(coeffs(p,\"+vv1+\")),size(coeffs(p,\"+vv2+\")),\"              \n"
					+ "    + \"coeffs(coeffs(p,\"+vv1+\"),\"+vv2+\"))\";                                                                       \n"
					+ "  execute(outx);                                                                                                        \n"
					+ "  return (out);                                                                                                         \n"
					+ "  }                                                                                                                     \n"
					+ "                                                                                                                        \n"
					+ "proc locusequ(ideal I, int prec) {                                                                                      \n"
					+ "  short=0;                                                                                                              \n"
					// + " list l=locus(I); \n"
					+ locusStatement
					+ "  if (size(l)==0) {print (impossible()); return; }                                                                      \n"
					+ "  poly pp;                                                                                                              \n"
					+ "  ideal ii;                                                                                                             \n"
					+ "  int i, j, jj;                                                                                                         \n"
					+ "  j=1;                                                                                                                  \n"
					+ "  pp=1;                                                                                                                 \n"
					+ "  list c;                                                                                                               \n"
					+ "  string cx;                                                                                                            \n"
					+ "  for (i=1; i<=size(l); i++) {                                                                                          \n"

					+ "    if ((string(l[i][3]" + subindex
					+ ")==\"Normal\") || (string(l[i][3]" + subindex
					+ ")==\"Accumulation\")) {         \n"

					+ "      cx=\"c=\"+point_to_0circle(l[i][1],prec);                                                                         \n"
					+ "      execute(cx);                                                                                                      \n"
					+ "      for (jj=1; jj<=size(c); jj++) {                                                                                   \n"
					+ "        pp=pp*c[jj];                                                                                                    \n"
					+ "        ii[j]=c[jj];                                                                                                    \n"
					+ "        j++;                                                                                                            \n"
					+ "        }                                                                                                               \n"
					+ "      }                                                                                                                 \n"
					+ "    }                                                                                                                   \n"
					+ "  string s=string(pp);                                                                                                  \n"
					+ "  string si=\"ideal iii=\"+string(ii);                                                                                  \n"
					+ "  int sl=size(s);                                                                                                       \n"
					+ "  if (sl==1) { print (impossible()); return; }                                                                          \n"
					+ "  string pg=\"poly p=\"+s[2,sl-2];                                                                                      \n"
					+ "  string vv1=parstr(1);                                                                                                 \n"
					+ "  string vv2=parstr(2);                                                                                                 \n"
					+ "  string rrx=\"ring rr=0,(\"+vv1+\",\"+vv2+\"),dp\";                                                                    \n"
					+ "  execute(rrx);                                                                                                         \n"
					+ "  execute(pg);                                                                                                          \n"
					+ "  execute(si);                                                                                                          \n"
					+ "  string out=sprintf(\"{{%s},{\",flattenCoeffs(p,vv1,vv2));                                                             \n"
					+ "  int iiis=size(iii);                                                                                                   \n"
					+ "  out=out+sprintf(\"%s,\",iiis);                                                                                        \n"
					+ "  for (i=1; i<=iiis; i++) {                                                                                             \n"
					+ "    out=out+flattenCoeffs(iii[i],vv1,vv2);                                                                              \n"
					+ "    if (i<iiis) { out=out+\",\"; }                                                                                      \n"
					+ "    }                                                                                                                   \n"
					+ "  sprintf(\"%s}}\", out);                                                                                               \n"
					+ "  }                                                                                                                     \n"
					+ "                                                                                                                        \n"
					+ "LIB \"solve.lib\";                                                                                                      \n"
					+ "// LIB \"/home/singularws/grobcov-20170620.lib\";                                                                       \n";

			/*
			 * Calling the above functions at entry point locusequ():
			 */
			sb.append(functions).append("LIB \"").append(locusLib)
					.append(".lib\";\n");
			sb.append("ring r=(0,")
					.append(vars)
					.append("),(")
					.append(elimVars)
					.append("),dp;\n");
			sb.append("ideal I=").append(polys).append(";\n");
			sb.append("locusequ(I,").append(PRECISION).append(");\n");

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
