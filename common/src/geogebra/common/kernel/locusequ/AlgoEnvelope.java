package geogebra.common.kernel.locusequ;

import geogebra.common.cas.singularws.SingularWebService;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoPointOnPath;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.locusequ.arith.Equation;
import geogebra.common.kernel.locusequ.arith.EquationSymbolicValue;
import geogebra.common.main.App;
import geogebra.common.util.debug.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * The Singular computations are provided by Francisco Botana
 * and the grobcov library by Antonio Montes & al.
 * Based on Sergio's LocusEquation.
 * Works out the equation for a given envelope.
 */
public class AlgoEnvelope extends AlgoElement {

    private GeoPoint movingPoint;
    private GeoElement path;
    /**
     * class name
     */
    public static final String CLASS_NAME = "AlgoEnvelope";
    private GeoImplicitPoly geoPoly;
    private GeoElement[] efficientInput, standardInput;
    
	/**
	 * Constructor.
	 * @param cons construction
	 * @param label label
	 * @param path path
	 * @param movingPoint moving point
	 */
	public AlgoEnvelope(Construction cons, String label, GeoElement path, GeoPoint movingPoint) {
		this(cons, path, movingPoint);
        this.geoPoly.setLabel(label);
	}
    	
    /**
     * Constructor. 
     * @param cons construction
     * @param path path
     * @param movingPoint moving point
     */
    public AlgoEnvelope(Construction cons, GeoElement path, GeoPoint movingPoint) {
        super(cons);
        
        this.movingPoint = movingPoint;
        this.path  = path;
        
        this.geoPoly = new GeoImplicitPoly(cons);
        
        setInputOutput();
        compute();
    }

	/* (non-Javadoc)
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
         * We need to create a new object from "linear". E.g., if it is a circle,
         * we have to define an equation for the circle by putting x and y the
         * free variables. 
         */
        
        
        
        efficientInput = new GeoElement[inSet.size()];
        efficientInput = inSet.toArray(efficientInput);
        
        standardInput = new GeoElement[2];
        standardInput[0] = this.path;
        standardInput[1] = this.movingPoint;
        
        setOutputLength(1);
        setOutput(0, this.geoPoly);
        
        setEfficientDependencies(standardInput, efficientInput);
	}
    
    /**
     * @return the result.
     */
    public GeoImplicitPoly getPoly() { return this.geoPoly; }

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.algos.AlgoElement#compute()
	 */
	@Override
	public void compute() {
		
		String result = getImplicitPoly();

		if (result != null) {
			try{
				this.geoPoly.setCoeff(CASTranslator.getBivarPolyCoefficientsSingular(result));
				this.geoPoly.setDefined();
				
			//Timeout => set undefined	
			} catch(Exception e) {
				this.geoPoly.setUndefined();
			}
		} else {
			this.geoPoly.setUndefined();
		}
	}

	private String getImplicitPoly() {

        String locusLib = SingularWebService.getLocusLib();
        
        if (locusLib.length() == 0) 
        	return null; // No Envelope support.
        		
		/* First we collect all the restriction equations except for the linear itself.
		 * This is exactly the same as in AlgoLocusEquation.
		 */
		EquationScope scope = new EquationScope(path, movingPoint);
        GeoPoint[] points = EquationHelpers.getDependentPredecessorPointsForElement(path);
                
        EquationPoint pequ;
        
        EquationList restrictions = new EquationList();
        AlgoElement algo;
        
        Set<AlgoElement> visitedAlgos = new HashSet<AlgoElement>();
        
        boolean constructionIsFeasible = true;
        
        // TODO some algos are done more than once.
        for(GeoPoint p : points){
            pequ = scope.getPoint(p);
            if(!pequ.isIndependent()){
                addAlgoIfNotVisited(restrictions, p.getParentAlgorithm(), scope, visitedAlgos);
                
                if(p.getParentAlgorithm() != null && !p.getParentAlgorithm().isLocusEquable()) {
                	constructionIsFeasible = false;
                	break;
                }
                
                for(Object algoObj : p.getAlgorithmList()) {
                    algo = (AlgoElement) algoObj;
                    addAlgoIfNotVisited(restrictions, algo, scope, visitedAlgos);
                }
            }
        }

        for(EquationAuxiliarSymbolicPoint p : scope.getAuxiliarSymbolicPoints()) {
        	restrictions.addAll(p.getRestrictions());
        }
        
        /* Now we create a virtual locus point on the path object.
         * This is done with AlgoPointOnPath. Then we retrieve the corresponding
         * equation to this virtual locus point. 
         */

		GeoPoint locusPoint = new GeoPoint(cons);
		AlgoPointOnPath apop = new AlgoPointOnPath(cons, (Path) path, 1, 1);
		locusPoint.setParentAlgorithm(apop);
		EquationList last = new EquationList();
		addAlgoIfNotVisited(last, apop, scope, visitedAlgos);
		// It is safe to remove the virtual locus point here. 
        locusPoint.remove();

        if (!constructionIsFeasible) {
        	return null;
        }
        
		// Changing "2.0" to "2", "x1*-x2" to "x1*(-x2)" and other formatting to satisfy Singular:
        CASTranslator ct = new CASTranslator(kernel);
        EquationSystem es = new EquationSystem(restrictions, scope);
        Collection<StringBuilder> restrictionsT = ct.translate(es);
        es = new EquationSystem(last, scope);
        Collection<StringBuilder> lastT = ct.translate(es);
        
		/* The equation is not yet in the form we want: the last two variables
		 * should be changed to x and y. So we collect all variables and
		 * change them by string replacing. This is quite ugly: if there would
		 * be a way to know the last two variables by a standard technique,
		 * it should be used here instead.
		 */
		
		Collection<? extends EquationSymbolicValue> scopeVars = scope.getAllVariables();
		String[] scopeVarsS = new String[scopeVars.size()];
		Iterator<?> it = scopeVars.iterator();
		int i = 0;
		while (it.hasNext()) {
			scopeVarsS[i++] = it.next().toString();
		}
		Arrays.sort(scopeVarsS, Collections.reverseOrder());
		// Variables to replace are in first two elements of scopeVarsS.
		
		// Now we do the replacement for the last equation (obtained for the path):
		String[] lastS = new String[lastT.size()];
		it = lastT.iterator();
		i = 0;	
		while (it.hasNext()) {
			 String eq = it.next().toString();
			 eq = eq.replaceAll(scopeVarsS[1], "x");
			 eq = eq.replaceAll(scopeVarsS[0], "y");
			 eq = CASTranslator.convertFloatsToRationals(eq);
			 lastS[i++] = eq;
		}
		// We collect the used x1,x2,... variables (their order is not relevant):
		String vars = "";
		int varsN = scopeVarsS.length;
		for (i=2; i < varsN; ++i) {
			vars += scopeVarsS[i];
			if (i < varsN - 1) {
				vars += ",";
			}
		}
		        
        // Obtaining polynomials:
		String polys = "";
		it = restrictionsT.iterator();
		while (it.hasNext()) {
			polys += CASTranslator.convertFloatsToRationals(it.next().toString()) + ",";
		}
		it = lastT.iterator();
		for (i=0; i<lastS.length; ++i) {
			polys += CASTranslator.convertFloatsToRationals(lastS[i]);
			if (i<lastS.length-1) {
				polys += ",";
			}
		}
       	
        // Constructing the Singular script.
        StringBuilder script = new StringBuilder();
        script.append("");
		script.append("LIB \"" + locusLib + ".lib\";ring r=(0,x,y),(" + vars).
			append("),dp;short=0;ideal m=");
		script.append(polys);
		script.append(";poly D=det(jacob(m));ideal S=" + polys + ",D;list e=envelopeto(grobcov(S));");
		script.append("ring rr=0,(x,y),dp;");
		// The output envelope can be a curve either in [[[x^2+y^2-3*x+...],[[...]]] form,
		// or a single point in [[[(y-2),(x-2)],[[..]]] form. In this latter case we rewrite it to
		// (y-2)^2+(x-2)^2 since it can be handled in GeoGebra as an implicit curve having
		// exactly one point.
		script.append("string w=e[1][4,find(e[1],\"]\")-4];int f=find(w,\",\");");
		script.append("if (f>0) {string ex=\"poly p=(\" + w[1,f-1] + \")^2+(\" + w[f+1,size(w)] + \")^2\";};");
		script.append("if (f<1) {string ex=\"poly p=\" + w;};"); // here an "else" would be more elegant, FIXME
		script.append("execute(ex);");
		// Now we obtain the coefficients (see exactly the same code for locus equation):
		script.append("printf(\"%s,%s,%s\",size(coeffs(p,x)),size(coeffs(p,y)),").
			append("coeffs(coeffs(p,x),y));");
		Log.info("[Envelope] input to singular: "+script);
		String result = App.singularWS.directCommand(script.toString());
		Log.info("[Envelope] output from singular: "+result);

		return result;
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
            App.debug("[Envelope] Restrictions init");
            App.debug("[Envelope] Construction " + algo.getOutput()[0].toString());
            for(Equation eq : eqs) {
            	App.debug(eq.toString());
            }
            App.debug("[Envelope] Restrictions end");
            restrictions.addAll(eqs);
        }
    }

	@Override
	public Commands getClassName() {
		return Commands.Envelope;
	}
}
