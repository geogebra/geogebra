/**
 * 
 */
package org.geogebra.common.kernel.locusequ;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.locusequ.arith.Equation;
import org.geogebra.common.kernel.prover.ProverBotanasMethod;
import org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.ProofResult;
import org.geogebra.common.util.Prover.ProverEngine;
import org.geogebra.common.util.debug.Log;

/**
 * @author sergio
 * Works out the equation for a given locus.
 */
public class AlgoLocusEquation extends AlgoElement implements UsesCAS {

    private GeoPoint movingPoint, locusPoint;
	private GeoImplicit geoPoly;
	private GeoElement[] efficientInput, standardInput;
	private String efficientInputFingerprint;
    private EquationSystem old_system = null; // for caching
	private GeoElement implicitLocus = null;

    
	/**
	 * @param cons
	 *            construction
	 * @param locusPoint
	 *            dependent point
	 * @param movingPoint
	 *            moving point
	 */
    public AlgoLocusEquation(Construction cons, GeoPoint locusPoint, GeoPoint movingPoint) {
        super(cons);
        
        this.movingPoint = movingPoint;
        this.locusPoint  = locusPoint;
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

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.algos.AlgoElement#setInputOutput()
	 */
	@Override
	protected void setInputOutput() {

		if (implicitLocus != null) {
			setInputOutputImplicit();
			return;
		}

		// it is inefficient to have Q and P as input
        // let's take all independent parents of Q
        // and the path as input
        TreeSet<GeoElement> inSet = new TreeSet<GeoElement>();
        inSet.add(this.movingPoint.getPath().toGeoElement());
        
        // we need all independent parents of Q PLUS
        // all parents of Q that are points on a path       
        Iterator<GeoElement> it = this.locusPoint.getAllPredecessors().iterator();
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
        
        standardInput = new GeoElement[2];
        standardInput[0] = this.locusPoint;
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
		String ret = "";
		int size = input.length;
		for (int i = 0; i < size; ++i) {
			ret += input[i].getAlgebraDescription(
					StringTemplate.defaultTemplate) + ",";
		}
		return ret;
	}

    /**
     * @return the result.
     */
	public GeoImplicit getPoly() {
		return this.geoPoly;
	}

	/* (non-Javadoc)
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
		if (implicitLocus != null) {
			computeExplicitImplicit(true);
			return;
		}

		if (implicitLocus == null
				&& movingPoint.getKernel().getApplication()
						.has(Feature.EXPLICIT_LOCUS_VIA_BOTANA)) {
			computeExplicitImplicit(false);
			return;
		}

		EquationSystem system = getOriginalIdeal();
		/* geoPoly is set to undefined until the CAS is loaded properly.
		 * On loading a GGB file geoPoly may be, however, defined, but
		 * later it will be set to undefined until the CAS is loaded.
		 * In the desktop platform the CAS is loading quickly, but in
		 * the web its loading may be slower: this is why we need
		 * to check if geoPoly is already defined or not.
		 * When geoPoly is defined, it can be the same as in a previous
		 * update when a dragging event was started. In many cases
		 * dragging will not change the equation system, hence it is
		 * unnecessary to recompute elimination in the CAS. For this
		 * purpose we simply store the previous equation system
		 * in the old_system variable.   
		 */
		if (this.geoPoly.isDefined() && system != null
				&& system.looksSame(old_system)) {
			// do nothing: the system has not been changed, thus we use the cache
			return; // avoid the heavy computation
		}
		old_system = system;

		if (system != null) {
			EquationTranslator<StringBuilder> trans = new CASTranslator(kernel);
			try{
				this.geoPoly.setCoeff(trans.eliminateSystem(system)); // eliminateSystem() is heavy
				this.geoPoly.setDefined();
				
			// Timeout or other error => set undefined	
			} catch(Exception e) {
				this.geoPoly.setUndefined();
			}
		} else {
			this.geoPoly.setUndefined();
		}
	}

	private EquationSystem getOriginalIdeal() {
		EquationScope scope = new EquationScope(locusPoint, movingPoint);
        GeoPoint[] points = EquationHelpers.getDependentPredecessorPointsForElement(locusPoint);
                
        EquationPoint pequ;
        
        EquationList restrictions = new EquationList();
        AlgoElement algo;
        
        Set<AlgoElement> visitedAlgos = new HashSet<AlgoElement>();

        // TODO some algos are done more than once.
        for(GeoPoint p : points){
            pequ = scope.getPoint(p);
            if(!pequ.isIndependent()){
                addAlgoIfNotVisited(restrictions, p.getParentAlgorithm(), scope, visitedAlgos);
                
				if (p.getParentAlgorithm() != null
						&& !p.getParentAlgorithm().isLocusEquable()) {
					Log.info("Non-algebraic or unimplemented dependent point: "
							+ p.getParentAlgorithm());
					return null;
                }
				for (Object predObj : p.getAllPredecessors()) {
					GeoElement pred = (GeoElement) predObj;
					Log.trace("Considering " + pred);
					if (pred.getParentAlgorithm() != null
							&& !pred.getParentAlgorithm().isLocusEquable()) {
						Log.info("Non-algebraic or unimplemented predecessor: "
								+ pred.getParentAlgorithm());
						return null;
					}
				}
                
                //restrictions.addAll(scope.getRestrictionsFromAlgo(p.getParentAlgorithm()));
                for(Object algoObj : p.getAlgorithmList()) {
                    algo = (AlgoElement) algoObj;
                    addAlgoIfNotVisited(restrictions, algo, scope, visitedAlgos);
                    //restrictions.addAll(scope.getRestrictionsFromAlgo(algo));
                }
            }
        }
        
		for (EquationAuxiliarSymbolicPoint p : scope
				.getAuxiliarSymbolicPoints()) {
			restrictions.addAll(p.getRestrictions());
        }
        
		return new EquationSystem(restrictions, scope);
	}
	
	/**
	 * Just static so it cannot modify any instance variables.
	 * @param restrictions
	 * @param algo
	 * @param scope
	 * @param visitedAlgos
	 */
	private static void addAlgoIfNotVisited(EquationList restrictions,
            AlgoElement algo, EquationScope scope, Set<AlgoElement> visitedAlgos) {
        if(!visitedAlgos.contains(algo)){
            visitedAlgos.add(algo);
            EquationList eqs = scope.getRestrictionsFromAlgo(algo);
			Log.debug("Visiting algo "
					+ algo.getOutput()[0]
							.toString(StringTemplate.defaultTemplate));
            for(Equation eq : eqs) {
				Log.debug(" -> " + eq.toString() + " == 0");
            }
            restrictions.addAll(eqs);
        }
    }

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.algos.AlgoElement#getClassName()
	 */
	@Override
	public Commands getClassName() {
		return Commands.LocusEquation;
	}
	
	private String getImplicitPoly(boolean implicit) throws Throwable {
		Prover p = UtilFactory.getPrototype().newProver();
		p.setProverEngine(implicit ? ProverEngine.LOCUS_IMPLICIT
				: ProverEngine.LOCUS_EXPLICIT);
		ProverBotanasMethod pbm = new ProverBotanasMethod();
		AlgebraicStatement as = pbm.new AlgebraicStatement(
				implicit ? implicitLocus : locusPoint, movingPoint, p);
		ProofResult proofresult = as.getResult();
		if (proofresult == ProofResult.PROCESSING
				|| proofresult == ProofResult.UNKNOWN) {
			/*
			 * Don't do further computations until CAS is ready or there were
			 * unimplemented algos or some other issues:
			 */
			throw new Exception();
		}
		Set<Set<Polynomial>> eliminationIdeal;

		HashMap<Variable, Long> substitutions = new HashMap<Variable, Long>();
		List<GeoElement> freePoints = ProverBotanasMethod
				.getFreePoints(implicit ? implicitLocus : locusPoint);
		if (!implicit) {
			freePoints.add(locusPoint);
		}
		if (!freePoints.contains(movingPoint)) {
			freePoints.add(movingPoint);
		}

		/* axis and fixed slope line support */
		Kernel k = movingPoint.getKernel();
		Iterator<GeoElement> geos = (implicit ? implicitLocus : locusPoint)
				.getAllPredecessors().iterator();
		while (geos.hasNext()) {
			GeoElement geo = geos.next();
			if (geo instanceof GeoLine && ((GeoLine) geo).hasFixedSlope()) {

				Variable[] vars = ((SymbolicParametersBotanaAlgo) geo)
						.getBotanaVars(geo);

				GeoLine l = (GeoLine) geo;

				/*
				 * a0/a1*x+b0/b1*y+c0/c1=0, that is:
				 * a0*b1*c1*x+a1*b0*c1*y+a1*b1*c0=0
				 */
				Coords P = l.getCoords();
				long[] a = k.doubleToRational(P.get(1));
				long[] b = k.doubleToRational(P.get(2));
				long[] c = k.doubleToRational(P.get(3));

				// Setting up two equations for the two points:
				Polynomial a0 = new Polynomial((int) a[0]);
				Polynomial a1 = new Polynomial((int) a[1]);
				Polynomial b0 = new Polynomial((int) b[0]);
				Polynomial b1 = new Polynomial((int) b[1]);
				Polynomial c0 = new Polynomial((int) c[0]);
				Polynomial c1 = new Polynomial((int) c[1]);
				Polynomial xp = new Polynomial(vars[0]);
				Polynomial yp = new Polynomial(vars[1]);
				Polynomial xq = new Polynomial(vars[2]);
				Polynomial yq = new Polynomial(vars[3]);

				Polynomial ph = a0.multiply(b1).multiply(c1).multiply(xp)
						.add(a1.multiply(b0).multiply(c1).multiply(yp))
						.add(a1.multiply(b1).multiply(c0));
				as.addPolynomial(ph);
				ph = a0.multiply(b1).multiply(c1).multiply(xq)
						.add(a1.multiply(b0).multiply(c1).multiply(yq))
						.add(a1.multiply(b1).multiply(c0));
				as.addPolynomial(ph);

				if (a[0] != 0) {
					/*
					 * This equation is not horizontal, so y can be arbitrarily
					 * chosen. Let's choose y=0 and y=1 for the 2 points.
					 */
					ph = yp;
					as.addPolynomial(ph);
					ph = yq.subtract(new Polynomial(1));
					as.addPolynomial(ph);
				} else {
					/*
					 * This equation is horizontal, so x can be arbitrarily
					 * chosen. Let's choose x=0 and x=1 for the 2 points.
					 */
					ph = xp;
					as.addPolynomial(ph);
					ph = xq.subtract(new Polynomial(1));
					as.addPolynomial(ph);
				}
				// These coordinates are no longer free.
				for (int i = 0; i < 4; i++) {
					vars[i].setFree(false);
				}
			}
			AlgoElement algo = geo.getParentAlgorithm();
			boolean condition;
			if (implicit) {
				condition = true;
			} else {
				condition = geo != locusPoint;
			}
			if (condition && algo instanceof AlgoPointOnPath) {
				/*
				 * We need to add handle all points which are on a path like
				 * free points (that is, substitution of their coordinates will
				 * be performed later), unless this point is the locus point.
				 */
				if (!freePoints.contains(geo)) {
					freePoints.add(geo);
				}
			}
		}

		Iterator<GeoElement> it = freePoints.iterator();
		String vx = "", vy = "";
		while (it.hasNext()) {
			GeoElement freePoint = it.next();
			freePoint.addToUpdateSetOnly(this);
			Variable[] vars = ((SymbolicParametersBotanaAlgo) freePoint)
					.getBotanaVars(freePoint);
			boolean condition = !movingPoint.equals(freePoint);
			if (!implicit) {
				condition &= !locusPoint.equals(freePoint);
			}
			if (condition) {
				double x = ((GeoPoint) freePoint).getInhomX();
				if ((x % 1) == 0) { // integer
					substitutions.put(vars[0], (long) x);
					vars[0].setFree(true);
				} else { // fractional
					/*
					 * Use the fraction P/Q according to the current kernel
					 * setting. We use the P/Q=x <=> P-Q*x=0 equation.
					 */
					long[] q = k.doubleToRational(x);
					vars[0].setFree(false);
					Polynomial ph = new Polynomial((int) q[0])
							.subtract(new Polynomial(vars[0])
									.multiply(new Polynomial((int) q[1])));
					as.addPolynomial(ph);
				}
				double y = ((GeoPoint) freePoint).getInhomY();
				if ((y % 1) == 0) {
					substitutions.put(vars[1], (long) y);
					vars[1].setFree(true);
				} else { // fractional
					/*
					 * Use the fraction P/Q according to the current kernel
					 * setting. We use the P/Q=x <=> P-Q*x=0 equation.
					 */
					long[] q = k.doubleToRational(y);
					vars[1].setFree(false);
					Polynomial ph = new Polynomial((int) q[0])
							.subtract(new Polynomial(vars[1])
									.multiply(new Polynomial((int) q[1])));
					as.addPolynomial(ph);
				}
			} else {
				condition = true;
				if (!implicit) {
					condition = locusPoint.equals(freePoint);
				}
				if (condition) {
					vx = vars[0].toString();
					vy = vars[1].toString();
					vars[0].setFree(true);
					vars[1].setFree(true);
				} else {
					vars[0].setFree(false);
					vars[1].setFree(false);
				}
			}
		}

		eliminationIdeal = Polynomial.eliminate(
				as.getPolynomials().toArray(
						new Polynomial[as.getPolynomials().size()]),
				substitutions, k, 0, false, true);

		// We implicitly assume that there is one equation here as result.
		Polynomial result = null;
		Iterator<Set<Polynomial>> it1 = eliminationIdeal.iterator();
		if (it1.hasNext()) {
			Set<Polynomial> results1 = it1.next();
			Iterator<Polynomial> it2 = results1.iterator();
			if (it2.hasNext()) {
				result = it2.next();
			}
		}

		if (result == null) {
			Log.info("No such implicit curve exists (0=-1)");
			return "1,1,1";
		}

		// Replacing variables to have x and y instead of vx and vy:
		String implicitCurveString = result.toString();
		if (!vx.equals("")) {
			implicitCurveString = implicitCurveString.replaceAll(vx, "x");
		}
		if (!vy.equals("")) {
			implicitCurveString = implicitCurveString.replaceAll(vy, "y");
		}
		Log.trace("Implicit locus equation: " + implicitCurveString);

		// This piece of code has been directly copied from CASgiac.java:
		StringBuilder script = new StringBuilder();
		script.append("[[aa:=")
				.append(implicitCurveString)
				.append("],")
				.append("[bb:=coeffs(factorsqrfree(aa),x)], [sx:=size(bb)], [sy:=size(coeffs(aa,y))],")
				.append("[cc:=[sx,sy]], [for ii from sx-1 to 0 by -1 do dd:=coeff(bb[ii],y);")
				.append("sd:=size(dd); for jj from sd-1 to 0 by -1 do ee:=dd[jj];")
				.append("cc:=append(cc,ee); od; for kk from sd to sy-1 do ee:=0;")
				.append("cc:=append(cc,ee); od; od],") // cc][6]");
				// Add the coefficients for the factors also to improve
				// visualization:
				.append("[ff:=factors(factorsqrfree(aa))], [ccf:=[size(ff)/2]], ")
				.append("[for ll from 0 to size(ff)-1 by 2 do aaf:=ff[ll]; bb:=coeffs(aaf,x); sx:=size(bb);")
				.append(" sy:=size(coeffs(aaf,y)); ccf:=append(ccf,sx,sy);")
				.append(" for ii from sx-1 to 0 by -1 do dd:=coeff(bb[ii],y); sd:=size(dd);")
				.append(" for jj from sd-1 to 0 by -1 do ee:=dd[jj]; ccf:=append(ccf,ee);")
				.append(" od; for kk from sd to sy-1 do ee:=0; ccf:=append(ccf,ee); od; od; od],")
				.append("[cc,ccf]][9]");

		GeoGebraCAS cas = (GeoGebraCAS) k.getGeoGebraCAS();
		try {
			String impccoeffs = cas.getCurrentCAS().evaluateRaw(
					script.toString());
			Log.trace("Output from giac: " + impccoeffs);
			return impccoeffs;
		} catch (Exception ex) {
			Log.debug("Cannot compute locus equation (yet?)");
			return null;
		}
	}

	/**
	 * Set up dependencies for the input and output objects.
	 */
	protected void setInputOutputImplicit() {

		TreeSet<GeoElement> inSet = new TreeSet<GeoElement>();
		inSet.add(this.movingPoint);
		Iterator<GeoElement> it = this.implicitLocus.getAllPredecessors()
				.iterator();
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
		standardInput[0] = this.implicitLocus;
		standardInput[1] = this.movingPoint;

		setOutputLength(1);
		setOutput(0, this.geoPoly.toGeoElement());

		setEfficientDependencies(standardInput, efficientInput);
		efficientInputFingerprint = fingerprint(efficientInput);

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
