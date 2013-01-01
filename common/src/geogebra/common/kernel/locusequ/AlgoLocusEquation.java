/**
 * 
 */
package geogebra.common.kernel.locusequ;



import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.locusequ.arith.Equation;
import geogebra.common.main.App;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author sergio
 * Works out the equation for a given locus.
 */
public class AlgoLocusEquation extends AlgoElement {

    private GeoPoint movingPoint, locusPoint;
    public static final String CLASS_NAME = "AlgoLocusEqu";
    private GeoImplicitPoly geoPoly;
    private GeoElement[] efficientInput, standardInput;
    
	public AlgoLocusEquation(Construction cons, String label, GeoPoint locusPoint, GeoPoint movingPoint) {
		this(cons, locusPoint, movingPoint);
        this.geoPoly.setLabel(label);
	}
    
    public AlgoLocusEquation(Construction cons, GeoPoint locusPoint, GeoPoint movingPoint) {
        super(cons);
        
        this.movingPoint = movingPoint;
        this.locusPoint  = locusPoint;
        
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
		EquationSystem system = getOriginalIdeal();

		if(system != null) {
			EquationTranslator trans = new CASTranslator(cons.getKernel());
			try{
				this.geoPoly.setCoeff(trans.eliminateSystem(system));
			//Timeout => set undefined	
			}catch(Exception e){
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
                
                //restrictions.addAll(scope.getRestrictionsFromAlgo(p.getParentAlgorithm()));
                for(Object algoObj : p.getAlgorithmList()) {
                    algo = (AlgoElement) algoObj;
                    addAlgoIfNotVisited(restrictions, algo, scope, visitedAlgos);
                    //restrictions.addAll(scope.getRestrictionsFromAlgo(algo));
                }
            }
        }
        
        if(constructionIsFeasible) {
        	for(EquationAuxiliarSymbolicPoint p : scope.getAuxiliarSymbolicPoints()) {
        		restrictions.addAll(p.getRestrictions());
        	}
        
        	return new EquationSystem(restrictions,scope);
        }
        
        return null;
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
            App.debug("[LocusEqu] Restrictions init");
            App.debug("[LocusEqu] Construction " + algo.getOutput()[0].toString());
            for(Equation eq : eqs) {
            	App.debug(eq.toString());
            }
            App.debug("[LocusEqu] Restrictions end");
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

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.algos.AlgoElement#buildEquationElementForGeo(geogebra.common.kernel.geos.GeoElement, geogebra.common.kernel.locusequ.EquationScope)
	 */
	

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.algos.ConstructionElement#isLocusEquable()
	 */
	

}
