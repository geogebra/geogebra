package org.geogebra.common.kernel.locusequ;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.cas.singularws.SingularWebService;
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
import org.geogebra.common.kernel.locusequ.arith.Equation;
import org.geogebra.common.kernel.locusequ.arith.EquationSymbolicValue;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * The Singular computations are provided by Francisco Botana
 * and the grobcov library by Antonio Montes & al.
 * Based on Sergio's LocusEquation.
 * Works out the equation for a given envelope.
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
        
		this.geoPoly = kernel.newImplicitPoly(cons);
        
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
		String result = null;
		try {
			result = getImplicitPoly();
		} catch (Throwable ex) {
			Log.debug("Cannot compute implicit curve (yet?)");
		}

		if (result != null) {
			try{
				GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
				this.geoPoly.setCoeff(cas.getCurrentCAS()
						.getBivarPolyCoefficientsAll(result));
				this.geoPoly.setDefined();
				// Timeout => set undefined
			} catch(Exception e) {
				this.geoPoly.setUndefined();
			}
		} else {
			this.geoPoly.setUndefined();
		}
	}

	private String getImplicitPoly() throws Throwable {

        String locusLib = SingularWebService.getLocusLib();
        
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
		int varsN = scopeVars.size();
		int[] scopeVarsI = new int[varsN];
		
		Iterator<?> it = scopeVars.iterator();
		int i = 0;
		while (it.hasNext()) {
			scopeVarsI[i++] = ((EquationSymbolicValue) it.next()).getId();
		}
		Arrays.sort(scopeVarsI);
		String varx = "x" + scopeVarsI[varsN - 2];
		String vary = "x" + scopeVarsI[varsN - 1];
		
		// Now we do the replacement for the last equation (obtained for the path):
		String[] lastS = new String[lastT.size()];
		it = lastT.iterator();
		i = 0;	
		while (it.hasNext()) {
			 String eq = it.next().toString();
			 eq = eq.replaceAll(varx, "x");
			 eq = eq.replaceAll(vary, "y");
			 eq = CASTranslator.convertFloatsToRationals(eq);
			 lastS[i++] = eq;
		}
		// We collect the used x1,x2,... variables (their order is not relevant):
		String vars = "";
		for (i=0; i < varsN - 2; ++i) {
			vars += "x" + scopeVarsI[i];
			if (i < varsN - 3) {
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

        StringBuilder script = new StringBuilder();

        // Constructing the script.
        // Single points [y-A,x-B] are returned in the form (x-B)^2+(y-A)^2.
        // Empty envelopes are drawn as 0=-1.
        // Multiple curves are drawn as products of the curves.

		if (locusLib.length() == 0) {
			// If there is no Singular support with the Groebner cover package, then we use Giac
			// and construct the Jacobi matrix on our own. Here we use two Giac calls, one for
			// the Jacobian and one for the elimination.
			script.append("[[");
			script.append("m:=[").append(polys).append("]],[J:=det_minor(");
			for (i=0; i<varsN-2; ++i) {
				script.append("[");
				for (int j=0; j<varsN-2; ++j) {
					script.append("diff(m[" + i + "],x" + scopeVarsI[j] + ")");
					if (j!=varsN-3) {
						script.append(",");
					}
				}
				script.append("]");
				if (i!=varsN-3) {
					script.append(",");
				}
			}
			script.append(")]][1][0]");

			Log.trace(
					"Input to giac (compute det of Jacobi matrix): "
							+ script);
			GeoGebraCAS cas = (GeoGebraCAS) locusPoint.getKernel().getGeoGebraCAS();
			try {
				String det = cas.getCurrentCAS().evaluateRaw(script.toString());
				if (det.equals("?")) {
					Log.debug("Cannot compute det of Jacobi matrix (yet?)");
					return null;
				}
				Log.trace(
						"Output from giac (compute det of Jacobi matrix): "
								+ det);
				String script2 = cas.getCurrentCAS().createLocusEquationScript(polys + "," + det, vars + ",x,y", vars);

				Log.trace("Input to giac: " + script2);
				String result = cas.getCurrentCAS().evaluateRaw(script2);
				return result;

			} catch (Exception ex) {
				Log.debug("Cannot compute envelope (yet?)");
				return null;
			}
        	
        }       	
		
        // Constructing the Singular script. This code contains a modified version
		// of Francisco Botana's locusdgto() and envelopeto() procedures in the grobcov library.
		// I.e. we no longer use these two commands, but locusto(), locus() and locusdg() only.
		// We use one single Singular call instead of two (as above for Giac).
        script.append("proc mylocusdgto(list L) {" +
        		"poly p=1;" +
        		"int i; int j; int k;" +
        		"for(i=1;i<=size(L);i++) { if(L[i][3]<>\"Degenerate\")" +
        		" { if(size(L[i][1])>1) {p=p*((L[i][1][1])^2+(L[i][1][2])^2);}" +
        			"else {p=p*L[i][1][1];}" +
    			"} } return(p); }");
        script.append("proc myenvelopeto (list GG) {" +
        		"list GGG;" +
        		"if (GG[1][2][1]<>1) { GGG=delete(GG,1); }" +
        		"else { GGG=GG; };" +
        		"string SLo=locusto(locus(GGG));" +
        		"if (find(SLo,\"Normal\") == 0 and find(SLo,\"Accumulation\") == 0 and find(SLo,\"Special\") == 0)" +
        			"{ return(1); }" +
        		"else { return(mylocusdgto(locus(GGG))); } }");
  		script.append("LIB \"" + locusLib + ".lib\";ring r=(0,x,y),(" + vars).
			append("),dp;short=0;ideal m=");
		script.append(polys);
		script.append(";poly D=det(jacob(m));ideal S=" + polys + ",D;list e=myenvelopeto(grobcov(S));");
		// This trick is required to push the result polynomial to the new ring world:
		script.append("string ex=\"poly p=\" + string(e[1]);");
		script.append("ring rr=0,(x,y),dp;");
		script.append("execute(ex);");
		// Now we obtain the coefficients (see exactly the same code for locus equation):
		script.append("sprintf(\"%s,%s,%s\",size(coeffs(p,x)),size(coeffs(p,y)),").
			append("coeffs(coeffs(p,x),y));");
		Log.trace("Input to singular: " + script);
		String result = App.getSingularWS().directCommand(script.toString());
		Log.trace("Output from singular: " + result);
		// Temporary workaround by creating dummy factor:
		result = "{{" + result + "},{1," + result + "}}";
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
			Log.debug("Restriction " + algo.getOutput()[0]
					.toString(StringTemplate.defaultTemplate));
            for(Equation eq : eqs) {
            	Log.debug(eq.toString());
            }
            restrictions.addAll(eqs);
        }
    }

	@Override
	public Commands getClassName() {
		return Commands.Envelope;
	}
}
