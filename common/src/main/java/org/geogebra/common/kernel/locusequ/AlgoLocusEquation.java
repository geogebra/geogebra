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
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.locusequ.arith.Equation;
import org.geogebra.common.kernel.prover.ProverBotanasMethod;
import org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.ProverEngine;
import org.geogebra.common.util.debug.Log;

/**
 * @author sergio
 * Works out the equation for a given locus.
 */
public class AlgoLocusEquation extends AlgoElement {

    private GeoPoint movingPoint, locusPoint;
    public static final String CLASS_NAME = "AlgoLocusEqu";
	private GeoImplicit geoPoly;
    private GeoElement[] efficientInput, standardInput;
    private EquationSystem old_system = null; // for caching
	private GeoElement implicitLocus = null;
    
	public AlgoLocusEquation(Construction cons, String label, GeoPoint locusPoint, GeoPoint movingPoint) {
		this(cons, locusPoint, movingPoint);
        this.geoPoly.setLabel(label);
	}
    
    public AlgoLocusEquation(Construction cons, GeoPoint locusPoint, GeoPoint movingPoint) {
        super(cons);
        
        this.movingPoint = movingPoint;
        this.locusPoint  = locusPoint;
		this.implicitLocus = null;
        
		this.geoPoly = kernel.newImplicitPoly(cons);
        
        setInputOutput();
        compute();
    }

	public AlgoLocusEquation(Construction cons, String label,
			GeoElement implicitLocus, GeoPoint movingPoint) {
		this(cons, implicitLocus, movingPoint);
		this.geoPoly.setLabel(label);
	}

	public AlgoLocusEquation(Construction cons, GeoElement implicitLocus,
			GeoPoint movingPoint) {
		super(cons);

		this.implicitLocus = implicitLocus;
		this.movingPoint = movingPoint;

		this.geoPoly = kernel.newImplicitPoly(cons);

		setInputOutput();
		compute();
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

		if (implicitLocus != null) {
			computeImplicit();
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
		if (this.geoPoly != null && this.geoPoly.isDefined() && system != null && system.looksSame(old_system)) {
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
					App.debug("[LocusEquation] Non-algebraic or unimplemented dependent point: "
							+ p.getParentAlgorithm());
					return null;
                }
				for (Object predObj : p.getAllPredecessors()) {
					GeoElement pred = (GeoElement) predObj;
					// App.debug("[LocusEquation] Considering " + pred);
					if (pred.getParentAlgorithm() != null
							&& !pred.getParentAlgorithm().isLocusEquable()) {
						App.debug("[LocusEquation] Non-algebraic or unimplemented predecessor: "
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
			App.debug("[LocusEquation] Visiting algo "
					+ algo.getOutput()[0]
							.toString(StringTemplate.defaultTemplate));
            for(Equation eq : eqs) {
				App.debug("[LocusEquation] -> " + eq.toString() + " == 0");
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
	
	private long[] doubleToRational(double x) {
		double y;
		long[] ret = new long[2];
		ret[1] = 1;
		int rounding = kernel.getPrintDecimals();
		y = x; // Kernel.roundToScale(x, rounding);
		while (rounding > 0) {
			ret[1] *= 10;
			y *= 10;
			rounding--;
		}
		ret[0] = (int) Math.floor(y);
		long gcd = Kernel.gcd(ret[0], ret[1]);
		ret[0] /= gcd;
		ret[1] /= gcd;
		return ret;
	}

	private String getImplicitPoly() throws Throwable {
		Prover p = UtilFactory.prototype.newProver();
		p.setProverEngine(ProverEngine.LOCUS_IMPLICIT);
		ProverBotanasMethod pbm = new ProverBotanasMethod();
		AlgebraicStatement as = pbm.new AlgebraicStatement(implicitLocus, p);
		Set<Set<Polynomial>> eliminationIdeal;

		HashMap<Variable, Long> substitutions = new HashMap<Variable, Long>();
		List<GeoElement> freePoints = ProverBotanasMethod
				.getFreePoints(implicitLocus);
		Iterator<GeoElement> it = freePoints.iterator();
		String vx = "", vy = "";
		while (it.hasNext()) {
			GeoElement freePoint = it.next();
			Variable[] vars = ((SymbolicParametersBotanaAlgo) freePoint)
					.getBotanaVars(freePoint);
			if (!movingPoint.equals(freePoint)) {
				double x = ((GeoPoint) freePoint).getInhomX();
				if ((x % 1) == 0) { // integer
					substitutions.put(vars[0], (long) x);
					vars[0].setFree(true);
				} else { // fractional
					/*
					 * Use the fraction P/Q according to the current kernel
					 * setting. We use the P/Q=x <=> P-Q*x=0 equation.
					 */
					long[] q = doubleToRational(x);
					vars[0].setFree(false);
					Polynomial ph = new Polynomial((int) q[0])
							.subtract(new Polynomial(
							vars[0]).multiply(new Polynomial((int) q[1])));
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
					long[] q = doubleToRational(y);
					vars[1].setFree(false);
					Polynomial ph = new Polynomial((int) q[0])
							.subtract(new Polynomial(
							vars[1]).multiply(new Polynomial((int) q[1])));
					as.addPolynomial(ph);
				}
			} else {
				vx = vars[0].toString();
				vy = vars[1].toString();
				vars[0].setFree(true);
				vars[1].setFree(true);
			}
		}

		eliminationIdeal = Polynomial.eliminate(
				as.getPolynomials().toArray(
						new Polynomial[as.getPolynomials().size()]),
				substitutions, kernel, 0, false);

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
			Log.warn("No implicit locus equation found");
			return null;
		}

		String implicitCurveString = result.toString().replaceAll(vx, "x")
				.replaceAll(vy, "y");
		Log.debug("Implicit locus equation: " + implicitCurveString);

		// This piece of code has been directly copied from CASgiac.java:
		StringBuilder script = new StringBuilder();
		script.append("[[aa:=")
				.append(implicitCurveString)
				.append("],")
				.append("[bb:=coeffs(factorsqrfree(aa),x)], [sx:=size(bb)], [sy:=size(coeffs(aa,y))],")
				.append("[cc:=[sx,sy]], [for ii from sx-1 to 0 by -1 do dd:=coeff(bb[ii],y);")
				.append("sd:=size(dd); for jj from sd-1 to 0 by -1 do ee:=dd[jj];")
				.append("cc:=append(cc,ee); od; for kk from sd to sy-1 do ee:=0;")
				.append("cc:=append(cc,ee); od; od],cc][6]");

		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		try {
			String impccoeffs = cas.getCurrentCAS().evaluateRaw(
					script.toString());
			Log.debug("Output from giac: " + impccoeffs);
			return impccoeffs.substring(1, impccoeffs.length() - 1);
		} catch (Exception ex) {
			Log.warn("Error computing locus equation");
			return null;
		}
	}

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

	}

	public void computeImplicit() {
		String result = null;
		try {
			result = getImplicitPoly();
		} catch (Throwable ex) {
			Log.warn("Error computing implicit curve");
		}

		if (result != null) {
			try {
				this.geoPoly.setCoeff(CASTranslator
						.getBivarPolyCoefficientsSingular(result));
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
