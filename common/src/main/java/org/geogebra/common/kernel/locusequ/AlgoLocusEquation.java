/**
 * 
 */
package org.geogebra.common.kernel.locusequ;



import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.kernel.locusequ.arith.Equation;
import org.geogebra.common.main.App;

/**
 * @author sergio
 * Works out the equation for a given locus.
 */
public class AlgoLocusEquation extends AlgoElement {

    private GeoPoint movingPoint, locusPoint;
    public static final String CLASS_NAME = "AlgoLocusEqu";
    private GeoImplicitPoly geoPoly;
    private GeoElement[] efficientInput, standardInput;
    private EquationSystem old_system = null; // for caching
    
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
			EquationTranslator<StringBuilder> trans = new CASTranslator(cons.getKernel());
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
					App.debug("[LocusEquation] Infeasible dependent point: "
							+ p.getParentAlgorithm());
					return null;
                }
				for (Object predObj : p.getAllPredecessors()) {
					GeoElement pred = (GeoElement) predObj;
					// App.debug("[LocusEquation] Considering " + pred);
					if (pred.getParentAlgorithm() != null
							&& !pred.getParentAlgorithm().isLocusEquable()) {
						App.debug("[LocusEquation] Infeasible predecessor: "
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

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.algos.AlgoElement#buildEquationElementForGeo(geogebra.common.kernel.geos.GeoElement, geogebra.common.kernel.locusequ.EquationScope)
	 */
	

	/* (non-Javadoc)
	 * @see geogebra.common.kernel.algos.ConstructionElement#isLocusEquable()
	 */
	

}
