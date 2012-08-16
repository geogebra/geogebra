/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Algorithm to invoke a specific macro. 
 * 
 * @author  Markus
 * @version 
 */
public class AlgoMacro extends AlgoElement 
implements AlgoMacroInterface {

	private Macro macro; 
	
	// macro construction, its input and output used by this algo	
	private GeoElement [] macroInput, macroOutput;
	
	// maps macro geos to algo geos
	private HashMap<GeoElement,GeoElement> macroToAlgoMap;
	
	// all keys of macroToAlgoMap that are not part of macroInput
	private ArrayList<GeoElement> macroOutputAndReferencedGeos;
	private ArrayList<GeoElement> algoOutputAndReferencedGeos; // for efficiency, see getMacroConstructionState()	
        
    /**
     * Creates a new algorithm that applies a macro to the
     * given input objects.        
     * @param cons
     * @param labels
     * @param macro
     * @param input
     */
    public AlgoMacro(Construction cons, String [] labels, Macro macro, GeoElement [] input) {
    	super(cons);
    	  
    	this.input = input;
        this.macro = macro;
        
        this.macroInput = macro.getMacroInput();
        this.macroOutput = macro.getMacroOutput();
                 	   
        // register algorithm with macro
        macro.registerAlgorithm(this);
        
        // create copies for the output objects
    	createOutputObjects();
    	    	
    	// initialize the mapping between macro geos and algo geos
    	initMap();    	  	
    	
    	setInputOutput();     	    	
    	compute(); 
    	
    	// check if macro construction has euclidianAlgos
    	if (macro.getMacroConstruction().hasEuclidianViewCE()) {
    		cons.registerEuclidianViewCE(this);
    	}
        
        GeoElement.setLabels(labels, getOutput());     
        
        //we hide objects that are hidden in macro construction, but
        //we want to do this only with 4.0 macros
        if(macro.isCopyCaptionsAndVisibility()){
			for(int i=0;i<macroOutput.length;i++)
				if(!macroOutput[i].isEuclidianVisible()){
					getOutput(i).setEuclidianVisible(false);
					getOutput(i).update();
				}
		}
    }         
    
    @Override
	public void remove() {
    	if(removed)
			return;
    	macro.unregisterAlgorithm(this);
    	super.remove();    	
    }           
    
    @Override
	public Algos getClassName() {
		return Algos.AlgoMacro;
	}
	
	@Override
	public String getCommandName(StringTemplate tpl) {
		return macro.getCommandName();
	}
    
	@Override
	protected void setInputOutput() {    	             
        setDependencies();
    }       
           	
    @Override
	final public void compute() {
    	try {    		
    		// set macro geos to algo geos state
    		setMacroConstructionState();

			// update all algorithms of macro-construction
			macro.getMacroConstruction().updateAllAlgorithms();      	
       
    		// set algo geos to macro geos state   
    		getMacroConstructionState();
    		
    	} catch (Exception e) {
    		App.debug("AlgoMacro compute():\n");
    		e.printStackTrace();
    		for (int i=0; i < getOutputLength(); i++) {
    			getOutput(i).setUndefined();
    		}
    	}
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {    	
        return getCommandDescription(tpl);
    }	         
    
    /**
     * Returns true when macroGeo is part of macroInput.
     */
	private boolean isMacroInputObject(GeoElement macroGeo) {
		for (int i=0; i < macroInput.length; i++) {
			if (macroGeo == macroInput[i])
				return true;
		}		
		return false;
	}		
	
	/** 
	 * Sets macro geos to the current state of algo geos.	
	 * Start points of vectors should not be copied. 
	 */
	final void setMacroConstructionState() {									
		// set input objects of macro construction		
		for (int i=0; i < macroInput.length; i++) {     			
			macroInput[i].set(input[i]);
			try{
				if(macroInput[i]instanceof GeoVector)((GeoVector)macroInput[i]).setStartPoint(null);
			}catch(Exception e){
				App.debug("Exception while handling vector input: "+e);
			}
			macroInput[i].setRealLabel(input[i].getLabelSimple());		
			//Application.debug("SET INPUT object: " + input[i] + " => " + macroInput[i]);
    	}		
	}

	
	/** 
	 * Sets algo geos to the current state of macro geos.	 
	 */
	final void getMacroConstructionState() {	
		// for efficiency: instead of lookups in macroToAlgoMap 
		// we use an array list algoOutputAndReferencedGeos with corresponding macro and algo geos
		int size = macroOutputAndReferencedGeos.size();
		for (int i=0; i < size; i++) {	
			GeoElement macroGeo = macroOutputAndReferencedGeos.get(i);
			GeoElement algoGeo = algoOutputAndReferencedGeos.get(i);
			if(macroGeo.isDefined()){
				algoGeo.set(macroGeo);	
				AlgoElement drawAlgo = macroGeo.getParentAlgorithm();
				if(drawAlgo instanceof AlgoDrawInformation){
					((GeoNumeric) algoGeo).setDrawable(true);
					algoGeo.setDrawAlgorithm(((AlgoDrawInformation)drawAlgo).copy());
				}

			}
			else algoGeo.setUndefined();		
		}		
	}
	
	
	/**
	 * Creates the output objects of this macro algorithm
	 */
	private void createOutputObjects() {		
		setOutputLength(macroOutput.length);								 						
		
		int layer = app.getMaxLayerUsed();
		for (int i=0; i < macroOutput.length; i++) {  
			// copy output object of macro and make the copy part of this construction
			setOutput(i,macroOutput[i].copyInternal(cons));
			GeoElement out = getOutput(i); 
			out.setUseVisualDefaults(false);
			out.setAdvancedVisualStyle(macroOutput[i]);	
			if(macro.isCopyCaptionsAndVisibility()){
				out.setCaption(macroOutput[i].getRawCaption());								
			}
			out.setLayer(layer);
			AlgoElement drawAlgo = macroOutput[i].getParentAlgorithm();
			if(drawAlgo instanceof AlgoDrawInformation){
				((GeoNumeric) out).setDrawable(true);
				out.setDrawAlgorithm(((AlgoDrawInformation)drawAlgo).copy());
			}
			
			out.setAlgoMacroOutput(true);
    	}
	}
	
	/**
	 * Inits the mapping of macro geos to algo geos construction.
	 * The map is used to set and get the state of the macro construction in compute()
	 * and to make sure that all output geos of the algorithm and all
	 * their references (e.g. the start point of a ray) are part of the algorithm's 
	 * construction.
	 */
	private void initMap() {	
		macroToAlgoMap = new HashMap<GeoElement,GeoElement>();
		macroOutputAndReferencedGeos = new ArrayList<GeoElement>();
		algoOutputAndReferencedGeos = new ArrayList<GeoElement>();
			
		// INPUT initing	
		// map macro input to algo input
		for (int i=0; i < macroInput.length; i++) {
			map(macroInput[i], input[i]);
		}					
						
		// OUTPUT initing	
		// map macro output to algo output
		for (int i=0; i < macroOutput.length; i++) {
			map(macroOutput[i], getOutput(i));					
    	}				
		// SPECIAL REFERENCES of output
		// make sure all algo-output objects reference objects in their own construction		
		// note: we do this in an extra loop to make sure we don't create output objects twice	
		for (int i=0; i < macroOutput.length; i++) { 
			initSpecialReferences(macroOutput[i], getOutput(i));					
    	}																		
	}
	
	/**
	 * Adds a (macroGeo, algoGeo) pair to the map. 	 		
	 */		
	private void map(GeoElement macroGeo, GeoElement algoGeo) {					
		if (macroToAlgoMap.get(macroGeo) == null) {
			// map macroGeo to algoGeo
			macroToAlgoMap.put(macroGeo, algoGeo);	
			
			if (!isMacroInputObject(macroGeo)) {						
				macroOutputAndReferencedGeos.add(macroGeo);
				// for efficiency: to avoid lookups in macroToAlgoMap
				algoOutputAndReferencedGeos.add(algoGeo); 
			}					
		}
	}
	
	/**
	 * Returns a GeoElement in this algo's construction
	 * that corresponds to the given macroGeo from the macro construction.
	 * If a macro-geo is not yet
	 * mapped to an algo-geo, a new algo-geo is created and added to 
	 * the map automatically.
	 */
	private GeoElement getAlgoGeo(GeoElement macroGeo) {
		if (macroGeo == null) return null;
		GeoElement algoGeo = macroToAlgoMap.get(macroGeo);
		
		// if we don't have a corresponding GeoElement in our map yet, 
		// create a new geo and update the map
		if (algoGeo == null) {		
			algoGeo = createAlgoCopy(macroGeo);
			map(macroGeo, algoGeo);	
		}			
		
		return algoGeo;		
	}
	
	/**
	 * Creates a new algo-geo in this construction that is copy of macroGeo from
	 * the macro construction.
	 */
	private GeoElement createAlgoCopy(GeoElement macroGeo) {
		GeoElement algoGeo = macroGeo.copyInternal(cons);	
		return algoGeo;
	}
	
		
	/**
	 * Some GeoElement types need special settings as they reference other
	 * GeoElement objects. We need to make sure that algoGeo
	 * only reference objects in its own construction.
	 */	
	private void initSpecialReferences(GeoElement macroGeo, GeoElement algoGeo) {
		
		switch (macroGeo.getGeoClassType()) {				
		case INTERVAL:
		case FUNCTION:
				initFunction(((GeoFunction) algoGeo).getFunction());
				break;
				
			case FUNCTIONCONDITIONAL:
				// done by set() in GeoFunctionConditional 
				// actually a GeoFunctionConditional consists of three GeoFunction objects,
				// so initFunction() is eventually used for them
				break;
											
			case LIST:
				initList((GeoList) macroGeo, (GeoList) algoGeo);
				break;									
										
			case LINE:						
			case LINEAR_INEQUALITY:						
				initLine((GeoLine) macroGeo, (GeoLine) algoGeo);
				break;	
				
			case POLYGON:
				initPolygon((GeoPolygon) macroGeo, (GeoPolygon) algoGeo);
				break;					
				
			case CONIC:
				initConic((GeoConic) macroGeo, (GeoConic) algoGeo);
				break;

			case TEXT:				
			case VECTOR:
			case IMAGE:
				initLocateable((Locateable) macroGeo, (Locateable) algoGeo);
				break;

			default:
			// no special treatment necessary at the moment
				// case ANGLE:								
				// case BOOLEAN:				
				// case CONICPART:
				// case LOCUS:
				// case NUMERIC:
				// case POINT:	
				// case AXIS:
				// case RAY:
				// case SEGMENT:
				// case POLYGON:
		}						
	}		
	
	/**
	 * Makes sure that the start and end point of a line are
	 * in its construction (if the line has this kind of information).
	 */			
	private void initLine(GeoLine macroLine, GeoLine line) {				
		GeoPoint startPoint = (GeoPoint) getAlgoGeo(macroLine.getStartPoint());
		GeoPoint endPoint   = (GeoPoint) getAlgoGeo(macroLine.getEndPoint());						
		line.setStartPoint(startPoint);
		line.setEndPoint(endPoint);					
	}
	
	/**
	 * Makes sure that all points on conic are
	 * in its construction.
	 */			
	private void initConic(GeoConic macroConic, GeoConic conic) {
		ArrayList<GeoPoint> macroPoints = macroConic.getPointsOnConic();
		if (macroPoints == null) return;
		
		int size = macroPoints.size();
		ArrayList<GeoPoint> points = new ArrayList<GeoPoint>(size);		
		for (int i=0; i < size; i++) {
			points.add((GeoPoint)getAlgoGeo(macroPoints.get(i)));
		}
		conic.setPointsOnConic(points);					
	}

	/**
	 * Makes sure that the start points of locateable are
	 * in its construction.
	 */	
	private void initLocateable(Locateable macroLocateable, Locateable locateable) {
		GeoPointND [] macroStartPoints = macroLocateable.getStartPoints();
		if (macroStartPoints == null) return;
		
		try {					
			for (int i=0; i < macroStartPoints.length; i++) {
				GeoPointND point = (GeoPointND) getAlgoGeo((GeoElement)macroStartPoints[i]);
				locateable.initStartPoint(point, i);
				
				//Application.debug("set start point: " + locateable + " => " + point + "(" + point.cons +")");
				
			}	
		} catch (Exception e) {
			App.debug("AlgoMacro.initLocateable:");
			e.printStackTrace();
		}
	}		
	
	/**
	 * Makes sure that the points and segments of poly are
	 * in its construction.
	 */
	private void initPolygon(GeoPolygon macroPoly, GeoPolygon poly) {								
		// points
		GeoPointND [] macroPolyPoints = macroPoly.getPoints();
		GeoPoint [] polyPoints = new GeoPoint[macroPolyPoints.length];										
		for (int i=0; i < macroPolyPoints.length; i++) {
			polyPoints[i] = (GeoPoint) getAlgoGeo( (GeoElement) macroPolyPoints[i] );	
		}
		poly.setPoints(polyPoints);
		

//		// segments
//		GeoSegment [] macroPolySegments = macroPoly.getSegments();
//		GeoSegment [] polySegments = new GeoSegment[macroPolySegments.length];										
//		for (int i=0; i < macroPolySegments.length; i++) {
//			polySegments[i] = (GeoSegment) getAlgoGeo( macroPolySegments[i] );	
//			initLine(macroPolySegments[i], polySegments[i]);
//		}
//		poly.setSegments(polySegments);
										
	} 
	
	
	/**
	 * Makes sure that all referenced GeoElements of geoList are
	 * in its construction.
	 * @param macroList GeoList of macro geos 
	 * @param geoList GeoList of construction geos
	 */			
	final public void initList(GeoList macroList, GeoList geoList) {			
		// make sure all referenced GeoElements are from the algo-construction
		
		int size = macroList.size();
		geoList.clear();
		geoList.ensureCapacity(size);
		for (int i=0; i < size; i++) {	
			geoList.add( getAlgoGeo(macroList.get(i)) );				
		}			
	} 
	
		
	/**
	 * Makes sure that all referenced GeoElements of fun are
	 * in this algorithm's construction.
	 * @param fun
	 */			
	final public void initFunction(FunctionNVar fun) {								
		// geoFun was created as a copy of macroFun, 
		// make sure all referenced GeoElements are from the algo-construction
		replaceReferencedMacroObjects(fun.getExpression());
	} 
		
	/**
	 * Replaces all references to macroGeos in expression exp by references to the corresponding
	 * algoGeos
	 */
	private void replaceReferencedMacroObjects(ExpressionNode exp) {
		ExpressionValue left = exp.getLeft();
		ExpressionValue right = exp.getRight();
		
		// left tree
		if (left.isGeoElement()) {								
			GeoElement referencedGeo = (GeoElement) left;			
			if (macro.isInMacroConstruction(referencedGeo)) {
				exp.setLeft(getAlgoGeo(referencedGeo));			
			}	
		}		
		else if (left.isExpressionNode()) {
			replaceReferencedMacroObjects((ExpressionNode) left);
		} 
		
		// right tree
		if (right == null) 
			return;
		else if (right.isGeoElement()) {
			GeoElement referencedGeo = (GeoElement) right;
			if (macro.isInMacroConstruction(referencedGeo)) {	
				exp.setRight(getAlgoGeo(referencedGeo));
			}	
		}
		else if (right.isExpressionNode()) {
			replaceReferencedMacroObjects((ExpressionNode) right);
		}		
	}

	// TODO Consider locusequability

    
}
