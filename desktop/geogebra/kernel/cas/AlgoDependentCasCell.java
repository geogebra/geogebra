package geogebra.kernel.cas;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoCasCell;
import geogebra.kernel.GeoElement;

import java.util.TreeSet;

/**
 * Algorithm to take care of GeoCasCells and possibly 
 * other GeoElements in the construction. This algorithm updates 
 * a given output GeoCasCell (e.g. m := c + 3) and possibly a 
 * twin GeoElement object (e.g. GeoNumeric m = c + 3 when c is defined). 
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentCasCell extends AlgoElement {
	
	// output CAS cell of this algorithm
	private GeoCasCell casCell;

	/**
	 * Creates a new algorithm to handle updates of the given cell.
	 * 
	 * @param outputCasCell the output cell that this algorithm should update.
	 */
	public AlgoDependentCasCell(GeoCasCell casCell) {
		super(casCell.getConstruction());		
		this.casCell = casCell;
		
		// make sure all input geos' values are present in the CAS
		initInput();
        
        // compute output of outputCasCell and
        // create twinGeoElement if necessary
		if (casCell.isOutputEmpty()) {
			// output empty: newly created casCell
			// compute output
			compute();
		} else {
			// output set: casCell was loaded from XML
			// only create twinGeo
			casCell.updateTwinGeo();
		}
             
        // initialize algorithm dependencies
        setInputOutput();                
        
        // tell construction that order of CAS cells may have changed
        cons.updateCasCellRows();
        
        // setLabel of twinGeo if we got one
        casCell.setLabelOfTwinGeo();        
	}
	
	@Override
	public String getClassName() {
		return "AlgoDependentCasCell";
	}

	private void initInput() {
		// input
		// m := c + 3 has input variable c
		TreeSet<GeoElement> geoVars = casCell.getGeoElementVariables();
		GeoElement [] geos = new GeoElement[geoVars.size()];
		input = geoVars.toArray(geos);	
		
		// tell construction that input geos are used by CAS algorithm
		for (GeoElement geo : input) {
			boolean firstUser = !geo.isSendingUpdatesToCAS();
			geo.addCasAlgoUser();
			if (firstUser) {
				// make sure geo's value is set in CAS
				geo.sendValueToCAS();
			}
		}
	}	
	
	public void remove() {
		// tell construction that input geos are no longer used by this CAS algorithm
		for (GeoElement geo : input) {
			geo.removeCasAlgoUser();			
		}
		
		super.remove();
	}
	
	/**
	 * Initializes input and output dependencies of this algorithm.
	 * This method can be called again after the GeoCasCell's input expression
	 * has changed.
	 */
	protected void setInputOutput() {   
		// init output
		// twin geo that may be created as a side effect
		// e.g. the CAS cell m := c + 3 will create a GeoNumeric m
		GeoElement twinGeo = casCell.getTwinGeo();			   			       
		
	    setOutputLength(twinGeo == null ? 1 : 2);        
	    setOutput(0, casCell);     
	    if (twinGeo != null)
	    	setOutput(1, twinGeo);
	    
	    // set input and output dependencies
        setDependencies();
	}
	
	public GeoCasCell getCasCell() {
		return casCell;
	}
		
	@Override
	protected void compute() {
		// check if all input variables are defined
		boolean inputDefined = true;
		for (GeoElement geo : input) {
			if (!geo.isDefined()) {
				inputDefined = false;
				break;
			}
		}
		
		if (inputDefined) {
			// compute output of CAS cell and update twin GeoElement
			casCell.computeOutput();
		} else {
			casCell.setUndefined();
		}
	}
	
	
	
    final public String toString() {
        // return input string, e.g. "m := c + 3"
        return casCell.getInput();
    }	
    
    /**
     * Returns <cellPair> tag instead of <expression> XML
     */
    protected String getExpXML() {   
    	return casCell.getXML();
    }
    

//	/**
//	 * Initializes input and output dependencies of this algorithm.
//	 * This method can be called again after the GeoCasCell's input expression
//	 * has changed.
//	 */
//	private void initAlgorithm() {
//		// old and new predecessors of casCell
//		TreeSet<GeoElement> oldPred = null;
//		
//		if (input != null) {
//			// remember old predecessors of casCell 
//			// for updateConstructionOrder below
//			oldPred = casCell.getAllPredecessors();
//		}			
//		
//		// input
//		// m := c + 3 has input variable c
//		TreeSet<GeoElement> geoVars = casCell.getGeoElementVariables();
//		GeoElement [] geos = new GeoElement[geoVars.size()];
//		input = geoVars.toArray(geos);										
//													
//		                 
//        
//        // init output
//        // twin geo that may be created as a side effect
//		// e.g. the CAS cell m := c + 3 will create a GeoNumeric m
//		GeoElement twinGeo = casCell.getTwinGeo();			   			       
//		
//	    setOutputLength(twinGeo == null ? 1 : 2);        
//	    setOutput(0, casCell);     
//	    if (twinGeo != null)
//	    	setOutput(1, twinGeo);   
//        
//        // set dependencies
//		setInputOutput();
//		
//		// Make sure that geoCasCell comes after all its predecessors 
//		// in  the construction list.
//		updateConstructionOrder(oldPred);
//	}
    
	/*
	 * added for minimal applets
	 */
    @Override
	public boolean isAlgoDependentCasCell() {
		return true;
	}   

}
