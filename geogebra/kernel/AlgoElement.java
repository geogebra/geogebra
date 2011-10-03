/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoElement.java
 *
 * Created on 30. August 2001, 21:36
 */

package geogebra.kernel;

import geogebra.gui.view.algebra.AlgebraView;
import geogebra.main.Application;
import geogebra.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeSet;

/**
 * AlgoElement is the superclass of all algorithms.
 * 
 * @author  Markus
 * @version 
 */
public abstract class AlgoElement extends ConstructionElement implements EuclidianViewCE {
	 
    private static ResourceBundle rbalgo2command;
	// Added for Intergeo File Format (Yves Kreis) -->
	private static ResourceBundle rbalgo2intergeo;
	// <-- Added for Intergeo File Format (Yves Kreis)
    
    protected GeoElement[] input;
    /** list of output
     * @deprecated (matthieu) use setOutputLength(), setOutput(), getOutputLength(), getOutput() instead
     */
    protected GeoElement[] output;
    private GeoElement [] efficientInput;
    private GeoNumeric [] randomUnlabeledInput;
     
    private boolean isPrintedInXML = true;
    private boolean stopUpdateCascade = false;
    
    public AlgoElement(Construction c) {
        this(c, true);               
    }

    protected AlgoElement(Construction c, boolean addToConstructionList) {
        super(c);     
        
        if (addToConstructionList)
        	c.addToConstructionList(this, false);                 
    }
    
    
    	
    
    

    /**
     * initialize output list
     * @param n Output length 
     * 
     */
    protected void setOutputLength(int n){
    	output = new GeoElement[n];
    }
    
    
    /**
     * set output number i
     * @param i
     * @param geo 
     */
    protected void setOutput(int i, GeoElement geo){
    	output[i] = geo;
    }
    
    /**
     * @param i
     * @return output i
     */
    protected GeoElement getOutput(int i){
    	return output[i];
    }
    
    /**
     * 
     * @return number of outputs
     */
    protected int getOutputLength(){
    	if (output==null)
    		return 0;
    	
    	return output.length;
    }
    
    /**
     * list of registered outputHandler of this AlgoElement
     */
    private List<OutputHandler<?>> outputHandler;
    
    /**
     * One OutputHandler has been changed, we put together the new output.
     */
    private void refreshOutput(){
    	Iterator<OutputHandler<?>> it=outputHandler.iterator();
    	int n=0;
    	while(it.hasNext()){
    		n+=it.next().size();
    	}
    	output=new GeoElement[n];
    	it=outputHandler.iterator();
    	int i=0;
    	while(it.hasNext()){
    		OutputHandler<?> handler=it.next();
    		for (int k=0;k<handler.size();k++)
    			output[i++]=handler.getElement(k);
    	}
    }
    
    
    /**
     * OutputHandler can manage several different output types, each with increasing length.
     * For each occurring type, you need one OutputHandler in the Subclass 
     * (or OutputHandler<GeoElement> if the type doesn't matter).<br />
     * Don't use this if you are accessing output directly, or use setOutput or setOutputLength, because
     * the OutputHandler changes output on it's own and will 'overwrite' any direct changes.
     * @param <T> extends GeoElement: type of the OutputHandler 
     */
    public class OutputHandler<T extends GeoElement>{
    	private elementFactory<T> fac;
    	private ArrayList<T> outputList;
    	private String[] labels;
    	private String indexLabel;
    	/**
    	 * use Labels for this outputs
    	 */
    	public boolean setLabels;
    	
    	/** number of labels already set */
    	private int labelsSetLength = 0;
    	

		/**
		 * @param fac elementFactory to create new Elements of type T
		 */
		public OutputHandler(elementFactory<T> fac) {
			this.fac = fac;
			outputList=new ArrayList<T>();
			if (outputHandler==null)
				outputHandler=new ArrayList<OutputHandler<?>>();
			outputHandler.add(this);
		}
		
		/**
		 * @param fac elementFactory to create new Elements of type T
		 * @param labels array of labels to use for Outputelements.
		 */
		public OutputHandler(elementFactory<T> fac,String[] labels){
			this(fac);
			this.labels=labels;
			if (labels!=null)
				adjustOutputSize(labels.length);
		}
		
		/**
		 * @param size makes room in this OutputHandler for size Objects.<br />
		 * if there are currently more objects than size, they become undefined.
		 */
		public void adjustOutputSize(int size){
			if (outputList.size()<size){
				outputList.ensureCapacity(size);
				for (int i=outputList.size();i<size;i++){
					T newGeo = fac.newElement();
					outputList.add(newGeo);
					setOutputDependencies(newGeo);
				}
				refreshOutput();
			}else{
				for (int i=size;i<outputList.size();i++){
					outputList.get(i).setUndefined();
				}
			}
			if (setLabels){
				updateLabels();
			}
		}
		
		public void augmentOutputSize(int size){
			size+=outputList.size();
			outputList.ensureCapacity(size);
			for (int i=outputList.size();i<size;i++){
				T newGeo = fac.newElement();
				outputList.add(newGeo);
				setOutputDependencies(newGeo);
			}
			refreshOutput();
			
			if (setLabels){
				updateLabels();
			}
		}
		
		/**
		 * add the geos list to the output
		 * @param geos
		 * @param setDependencies says if the dependencies have to be set for this output
		 * @param refresh if true, output array is recomputed using outputhandler
		 */
		public void addOutput(T[] geos, boolean setDependencies, boolean refresh){
			for (int i=0; i<geos.length; i++){
				outputList.add(geos[i]);
				if (setDependencies)
					setOutputDependencies(geos[i]);
			}
			if (refresh)
				refreshOutput();
		}
		
		/**
		 * set setLabels to true
		 * @param labels use this Strings as labels. If labels == null, default labels are used
		 */
		public void setLabels(String[] labels){
			this.labels=labels;
			//setLabels=true;
			setLabels = !cons.isSuppressLabelsActive();
			if (labels != null){
				if (labels.length==1)
					setIndexLabels(labels[0]);
				adjustOutputSize(labels.length);
			}
			else
				updateLabels();
		}
		
		/**
		 * set setLabels to true
		 * @param label use this String as indexed labels. 
		 */
		public void setIndexLabels(String label){
			this.indexLabel=label;
			//setLabels=true;
			setLabels = !cons.isSuppressLabelsActive();
			updateLabels();
		}	
		
		
		/**
		 * assigns Labels to unlabeled elements
		 */
		public void updateLabels(){
			for (int i=0;i<outputList.size();i++){
				if (!outputList.get(i).isLabelSet()){
					if (indexLabel!=null){ //use indexed label
						outputList.get(i).setLabel(outputList.get(i).getIndexLabel(indexLabel));
					}else if (labels!=null&&i<labels.length)
						outputList.get(i).setLabel(labels[i]);
					else
						outputList.get(i).setLabel(null);
				}
			}
		}
		
		
		/**
		 * set the label to the next geo with no label (or create new one)
		 * @param label
		 * @return corresponding geo
		 */
		public T addLabel(String label){
			T geo;
			if (labelsSetLength<outputList.size()){
				geo = getElement(labelsSetLength);
				//Application.debug(label+", geo="+geo);
			}else{
				geo = fac.newElement();
				outputList.add(geo);
				setOutputDependencies(geo);
				refreshOutput();
			}
			
			labelsSetLength++;
			geo.setLabel(label);
			
			return geo;
			
		}
		
		/**
		 * @param i
		 * @return get the i<sup>th</sup> Element of this OutputHandler
		 */
		public T getElement(int i){
			return outputList.get(i);
		}
		
		/**
		 * @param a type of the Output
		 * @return content of this OutputHandler as array
		 */
		public T[] getOutput(T[] a){
//			Application.debug("getOutput: "+Arrays.deepToString(outputList.toArray())+" length:"+outputList.toArray().length);
			return (T[])outputList.toArray(a);
		}
		
		/**
		 * @return size of the content of this OutputHandler
		 */
		public int size(){
			return outputList.size();
		}
    	
    }
    
    /**
     * 
     *
     * @param <S>
     */
    public interface elementFactory<S extends GeoElement>{
    	
    	/**
    	 * this is called by the OutputHandler every Time a new Element is needed.
    	 * @return a new Element of type S. (e.g. new GeoPoint(cons))
    	 */
    	public S newElement();
    }
    
    
    
    /*
     * needed so that JavaScript commands work:
     * ggbApplet.getCommandString(objName);
	 * ggbApplet.getValueString(objName);
     */
    final public static void initAlgo2CommandBundle(Application app) {
        if (rbalgo2command == null) {
        	rbalgo2command = app.initAlgo2CommandBundle();
        }
    	
    }
    
    final String getCommandString(String classname) {
        // init rbalgo2command if needed
        // for translation of Algo-classname to command name
        if (rbalgo2command == null) {
        	rbalgo2command = app.initAlgo2CommandBundle();
        }
            	
    	// translate algorithm class name to internal command name
    	return rbalgo2command.getString(classname);
    }
    
	// Added for Intergeo File Format (Yves Kreis) -->
    private String getIntergeoString(String classname) {
        // init rbalgo2intergeo if needed
        // for translation of Algo-classname to Intergeo name
        if (rbalgo2intergeo == null) {
        	rbalgo2intergeo = app.initAlgo2IntergeoBundle();
        }
            	
    	// translate algorithm class name to Intergeo name
        try {
        	return rbalgo2intergeo.getString(classname);
        } catch (MissingResourceException e) {
        	return classname;
        }
    }
	// <-- Added for Intergeo File Format (Yves Kreis)
    
    // in setInputOutput() the member vars input and output are set
    abstract protected  void setInputOutput();

    // in compute() the output ist derived from the input
    abstract protected void compute();       

    /**
     * Inits this algorithm for the near-to-relationship. This
     * is important to init the intersection algorithms  when
     * loading a file, so that they have a look at the current
     * location of their output points. 
     */
    protected void initForNearToRelationship() {}
    
    public boolean isNearToAlgorithm() {
    	return false;
    }
    
//    public static double startTime, endTime;
//    public static double computeTime, updateTime;
//    public static double counter;
    
    public void update() {
    	if (stopUpdateCascade) return;
    	 
    	// update input random numbers without label
    	if (randomUnlabeledInput != null) {
    		for (int i=0; i < randomUnlabeledInput.length; i++) {
    			randomUnlabeledInput[i].updateRandomGeo();
    		}
    	}
    	
//    	counter++;
//    	startTime = System.currentTimeMillis(); 

        // compute output from input
        compute();
        
//        endTime = System.currentTimeMillis(); 
//        computeTime += (endTime - startTime);
    	//startTime = System.currentTimeMillis(); 
        
        // update dependent objects 
        for (int i = 0; i < getOutputLength(); i++) {           
                getOutput(i).update();
        }           
        
//        endTime = System.currentTimeMillis(); 
//        updateTime += (endTime - startTime );           
    }              
    
    /**
	 * Updates all AlgoElements in the given ArrayList.
	 * Note: this method is more efficient than calling 
	 * updateCascade() for all individual AlgoElements.
     * @param algos 
	 */
	public static void updateCascadeAlgos(ArrayList<AlgoElement> algos) {
		if (algos == null) return;		
		int size = algos.size();
		if (size == 0) return;
		
		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		for (int i=0; i < size; i++) {
			AlgoElement algo = algos.get(i);
			algo.compute();
			for (int j=0; j < algo.getOutputLength(); j++) {
				algo.getOutput(j).update();
				geos.add(algo.getOutput(j));
			}
		}
		
		// update all geos
		GeoElement.updateCascade(geos, getTempSet(), true);
	}
	
	private static TreeSet<AlgoElement> tempSet;	
	private static TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>();
		}
		return tempSet;
	}   

    // public part    
	/** 
	 * @return list of output
	 */
    public GeoElement[] getOutput() {
        return output;
    }
    final public GeoElement[] getInput() {
        return input;
    }
    
    GeoElement[] getInputForUpdateSetPropagation() {
        return input;
    }

    /**
     *  DEPENDENCY handling 
     *  the dependencies are treated here
     *  by using input and output which must be set by
     *  every algorithm. Note: setDependencies() is
     *  called by every algorithm in topological order
     *  (i.e. possible helper algos call this method before
     *  the using algo does).
     * @see #setInputOutput()
     */
    final protected void setDependencies() {  
        // dependents on input
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);            
        }    
         
        doSetDependencies();   
    }
    
    protected void doSetDependencies() {
     	setRandomUnlabeledInput();
        setOutputDependencies();           
        cons.addToAlgorithmList(this);  
    }
    
    /**
     * Builds a list of all random input numbers that are unlabeled.
     * These numbers need to be updated in update() before compute()
     * is called.
     */
    private void setRandomUnlabeledInput() {
    	ArrayList<GeoNumeric> tempList = null;
        for (int i = 0; i < input.length; i++) {  
       	 if (input[i].isGeoNumeric() && !input[i].isLabelSet()) {
       		 GeoNumeric num = (GeoNumeric) input[i];
       		 if (num.isRandomGeo()) {
       			 if (tempList == null) tempList = new ArrayList<GeoNumeric>();
       			 tempList.add(num);
       		 }
       	 }
        } 
        
        if (tempList != null) {
        	randomUnlabeledInput = new GeoNumeric[tempList.size()];
        	for (int i=0; i < randomUnlabeledInput.length; i++) {
        		randomUnlabeledInput[i] = (GeoNumeric) tempList.get(i);
        	}
        }
    }
    
    
    protected final void setEfficientDependencies(GeoElement [] standardInput, GeoElement [] efficientInput) {   	
    	// dependens on standardInput
        for (int i = 0; i < standardInput.length; i++) {
        	standardInput[i].addToAlgorithmListOnly(this);            
        }
    	
    	// we use efficientInput for updating
        for (int i = 0; i < efficientInput.length; i++) {
        	efficientInput[i].addToUpdateSetOnly(this);            
        }
        
        // input is standardInput
        input = standardInput;
        this.efficientInput = efficientInput;
        
        doSetDependencies(); 
    }  
    
    private void setOutputDependencies() {
    	
    	
   	 // parent algorithm of output
       for (int i = 0; i < getOutputLength(); i++) {
    	   
    	   setOutputDependencies(getOutput(i));
        
       }   
   }
    

    /**
     * sets the given geo to be child of this algo
     * @param output
     */
    protected void setOutputDependencies(GeoElement output) {
    	// parent algorithm of output
    	output.setParentAlgorithm(this);

    	// every algorithm with an image as output
    	// should be notified about view changes
    	if (output.isGeoImage())
    		cons.registerEuclidianViewCE(this);

    	//  make sure that every output has same construction as this algorithm
    	// this is important for macro constructions that have input geos from
    	// outside the macro: the output should be part of the macro construction!
    	if (cons != output.cons)
    		output.setConstruction(cons);             

    }
    
    
    
    
       
    public boolean euclidianViewUpdate() {
    	update();
    	return false;
    }
       
    /**
     * Removes algorithm and all dependent objects from construction.
     */
    public void remove() {      	
        cons.removeFromConstructionList(this);                
        cons.removeFromAlgorithmList(this);        
        
        // delete dependent objects          
        for (int i = 0; i < getOutputLength(); i++) {
            getOutput(i).doRemove();
        }
                              
        // delete from algorithm lists of input                
        for (int i = 0; i < input.length; i++) {
            input[i].removeAlgorithm(this);                                      
        }    
        
    	// delete from algorithm lists of efficient input 
        if (efficientInput != null) {        	
            for (int i = 0; i < efficientInput.length; i++) {
            	efficientInput[i].removeAlgorithm(this);            	
            }  
        }                   
    }
    
//    /**
//     * Removes algorithm from all updateSets and algorithm lists of
//     * input objects and their predecessors. The algorithm itself
//     * and its output are kept in the construction.
//     */
//    final public void removeInputDependencies() {
//    	if (input == null) return;
//    	
//    	// we keep the output, so we need to remove
//    	// the algorithm from all update sets of input predecessors     	 
//    	TreeSet<GeoElement> inputPred = new TreeSet<GeoElement>();
//
//    	// delete from algorithm lists of input  
//    	// collect all input predecessors 
//    	for (int i = 0; i < input.length; i++) {
//    		input[i].removeAlgorithm(this);    			
//    		input[i].addPredecessorsToSet(inputPred, false);
//    		inputPred.add(input[i]);    		                       
//    	}    
//
//    	// delete from algorithm lists of efficient input 
//    	// collect all input predecessors 
//    	if (efficientInput != null) {        	
//    		for (int i = 0; i < efficientInput.length; i++) {
//    			efficientInput[i].removeAlgorithm(this);
//    			efficientInput[i].addPredecessorsToSet(inputPred, false);
//    			inputPred.add(efficientInput[i]);    			  
//    		}  
//    	}
//
//		// make sure that unreachable algos are removed from update sets
//		for (GeoElement predGeo : inputPred) {
//			predGeo.removeUnreachableAlgorithmsFromUpdateSet();
//		}	         
//    }
    
    /**
     * Tells this algorithm to react on the deletion
     * of one of its outputs. 
     * @param output 
     */
    void remove(GeoElement output) {
    	remove();
    }
    
    /**
     * Calls doRemove() for all output objects of this
     * algorithm except for keepGeo.
     * @param keepGeo 
     */
    void removeOutputExcept(GeoElement keepGeo) {
    	
    	for (int i=0; i < getOutputLength(); i++) {
            GeoElement geo = getOutput(i);
            if (geo != keepGeo) 
            	geo.doRemove();
        }
    }

    /**
     * Tells all views to add all output GeoElements of this algorithm. 
     */
    final public void notifyAdd() {
        for (int i = 0; i < getOutputLength(); ++i) {
            getOutput(i).notifyAdd();
        }
    }

    /**
     * Tells all views to remove all output GeoElements of this algorithm. 
     */
    final public void notifyRemove() {
        for (int i = 0; i < getOutputLength(); ++i) {
            getOutput(i).notifyRemove();
        }
    }

    final public GeoElement[] getGeoElements() {
        return getOutput();
    }
    
    /** 
     * Returns whether all output objects have
     * the same type.     
     * @return whether all outputs have the same type
     */
    final public boolean hasSingleOutputType() {
    	int type = getOutput(0).getGeoClassType();
    	
    	 for (int i = 1; i < getOutputLength(); ++i) {
            if (getOutput(i).getGeoClassType() != type)
            	return false;
         }    	
    	 return true;
    }
    
    final public boolean isAlgoElement() {
        return true;
    }

    final public boolean isGeoElement() {
        return false;
    }      

	/**
	 * Returns true iff one of the output geos is shown
	 * in the construction protocol	 
	 */
	final public boolean isConsProtocolBreakpoint() {
		for (int i=0; i < getOutputLength(); i++) {
			if (getOutput(i).isConsProtocolBreakpoint())
				return true;
		}
		return false;
	}
	
	/**
	 * Compares using getConstructionIndex() to order algos in update order.
	 * Note: 0 is only returned for this == obj.
	 * @overwrite ConstructionElement.compareTo()
	 */
    public int compareTo(ConstructionElement obj) {
    	if (this == obj) return 0;
    	
    	ConstructionElement ce = (ConstructionElement) obj;   
    	if (getConstructionIndex() < ce.getConstructionIndex())
    		return -1;
    	else
    		return 1;
    }
    
    /**
     * Returns construction index in current construction.
     * For an algorithm that is not in the construction list, the largest construction
     * index of its inputs is returned.
     */
    public int getConstructionIndex() {
        int index =  super.getConstructionIndex();
        // algorithm is in construction list
        if (index >= 0) return index;
        
        // algorithm is not in construction list
        for (int i=0; i < input.length; i++) {
            int temp = input[i].getConstructionIndex();
            if (temp > index) index = temp;
        }
        return index;
    }    
    
    /**
     * Returns the smallest possible construction index for this object in its construction.
     */
    public int getMinConstructionIndex() {    	
        // index must be greater than every input's index
    	int max = 0;
        for (int i=0; i < input.length; ++i) {
            int index = input[i].getConstructionIndex();
            if (index > max) max = index;
        }
        return max+1;
    }
    
    /**
     * Returns the largest possible construction index for this object in its construction.
     */ 
    public int getMaxConstructionIndex() {            	
         // index is less than minimum of all dependent algorithm's index of all output
         ArrayList<AlgoElement> algoList;
         int size, index;
         int min = cons.steps();                        
         for (int k=0; k < getOutputLength(); ++k) {
            algoList = getOutput(k).getAlgorithmList();
            size = algoList.size();                                  
            for (int i=0; i < size; ++i) {                          
                 index = ((AlgoElement)algoList.get(i)).getConstructionIndex();
                 if (index < min) min = index;
            }
         }       
         return min-1;              
    }
    
    /**
     * Returns all independent predecessors (of type GeoElement) that this algo depends on.
     * The predecessors are sorted topologically.
     */
    final public TreeSet<GeoElement> getAllIndependentPredecessors() {
        //  return predecessors of any output, i.e. the inputs of this algo
        TreeSet<GeoElement> set = new TreeSet<GeoElement>();
        addPredecessorsToSet(set, true);
        return set;
    }
    
    //  adds all predecessors of this object to the given list
    // the set is kept topologically sorted 
    // @param onlyIndependent: whether only indpendent geos should be added
    final void addPredecessorsToSet(TreeSet<GeoElement> set, boolean onlyIndependent) {
        for (int i = 0; i < input.length; i++) {
            GeoElement parent = input[i];

    		if (!set.contains(parent)) {  		
				if (!onlyIndependent) {
					set.add(parent);
				}
	            parent.addPredecessorsToSet(set, onlyIndependent);
    		}
        }
    }
    
    final void addRandomizablePredecessorsToSet(TreeSet<GeoElement> set) {
        for (int i = 0; i < input.length; i++) {
            GeoElement parent = input[i];

    		if (!set.contains(parent)) {  		
	            parent.addRandomizablePredecessorsToSet(set);
    		}
        }
    }
    
    
    /**
	 * Returns all moveable input points of this algorithm.	 
     * @return list of moveable input points
	 */   
    public ArrayList<GeoPoint> getFreeInputPoints() {
		if (freeInputPoints == null) {				
			freeInputPoints = new ArrayList<GeoPoint>(input.length);
			
			// don't use free points from dependent algos with expression trees 			
			if (!getClassName().startsWith("AlgoDependent")) {							
				for (int i=0; i < input.length; i++) {				
					if (input[i].isGeoPoint() && input[i].isIndependent())
						freeInputPoints.add((GeoPoint)input[i]);	
				}				
			}
		}
	
		return freeInputPoints;
    }
    private ArrayList<GeoPoint> freeInputPoints;
    
    /**
	 * Returns all input points of this algorithm.	 
     * @return list of input points
	 */
    public ArrayList<GeoPoint> getInputPoints() {	
    	if (inputPoints == null) {
			inputPoints = new ArrayList<GeoPoint>(input.length);
			for (int i=0; i < input.length; i++) {			
				if (input[i].isGeoPoint() )
					inputPoints.add((GeoPoint) input[i]);			
			}	
    	}
		
		return inputPoints;
    }
    private ArrayList<GeoPoint> inputPoints;

    final public boolean isIndependent() {
        return false;
    }

    protected StringBuilder sbAE = new StringBuilder();

    public String getNameDescription() {
        sbAE.setLength(0);
        if (getOutput(0).isLabelSet()) sbAE.append(getOutput(0).getNameDescription());
        for (int i = 1; i < getOutputLength(); ++i) {
            if (getOutput(i).isLabelSet()) {
                sbAE.append("\n");
                sbAE.append(getOutput(i).getNameDescription());              
            }                           
        }
        return sbAE.toString();
    }
    

  
    public String getAlgebraDescription() {
    	 sbAE.setLength(0);
        
        if (getOutput(0).isLabelSet()) sbAE.append(getOutput(0).getAlgebraDescription());       
        for (int i = 1; i < getOutputLength(); ++i) {
            if (getOutput(i).isLabelSet()) {
                sbAE.append("\n");
                sbAE.append(getOutput(i).getAlgebraDescription());               
            }           
        }
        return sbAE.toString();
    }

    public String getAlgebraDescriptionRegrOut() {
   	 sbAE.setLength(0);
       
       if (getOutput(0).isLabelSet()) sbAE.append(getOutput(0).getAlgebraDescriptionRegrOut());       
       for (int i = 1; i < getOutputLength(); ++i) {
           if (getOutput(i).isLabelSet()) {
               sbAE.append("\n");
               sbAE.append(getOutput(i).getAlgebraDescriptionRegrOut());               
           }           
       }
       return sbAE.toString();
   }


    
    public String getDefinitionDescription() {
        return toString();
    }    
        
    public String getCommandDescription() {
    	return  getCommandDescription(false); 
    }
    public String getCommandDescription(boolean real) {
        String cmdname = getCommandName();          
        
        //      command name
        if (cmdname.equals("Expression"))
			return real ? toRealString():toString();
		else {
			 sbAE.setLength(0);
            if (kernel.isPrintLocalizedCommandNames()) {
                sbAE.append(app.getCommand(cmdname));        
            } else {
                sbAE.append(cmdname);
            } 
            
            int length = input.length;
                  
            sbAE.append("[");
            // input
            if (length>0) sbAE.append(real?input[0].getRealLabel():input[0].getLabel()); // Michael Borcherds 2008-05-15 added input.length>0 for Step[]
            for (int i = 1; i < length; ++i) {
                sbAE.append(", ");
                appendCheckVector(sbAE, input[i], real);
            }
            sbAE.append("]");
            return sbAE.toString();           
        }       
    }
    
    /*
     * see #1377
     * g:X = (-5, 5) + t (4, -3) 
     */
    private void appendCheckVector(StringBuilder sb, GeoElement geo, boolean real) {
        String cmd = real? geo.getRealLabel():geo.getLabel();
        if (geo.isGeoVector()) {
        	String vectorCommand = "Vector["; // in XML, so don't want this translated
        	boolean needsWrapping = !geo.isLabelSet() && !cmd.startsWith(vectorCommand);
        	
        	if (needsWrapping) sb.append(vectorCommand);
        	sb.append(cmd);
        	if (needsWrapping) sb.append(']');
        } else {
        	sb.append(cmd);
        }
    	
    }
    
    
   

    public String toRealString() {
		return toString();
	}

	/**
     * translate class name to internal command name
     * GeoGebra File Format
	 * @return internal command name
     */
    public String getCommandName() {
        String cmdname, classname;
        // get class name
        classname = getClassName();
        // dependent algorithm is an "Expression"
        if (classname.startsWith("AlgoDependent")) {
            cmdname = "Expression";
        } else {
            // translate algorithm class name to internal command name
            cmdname = getCommandString(classname);
            
            if (kernel.isUseTempVariablePrefix()) {
	    		// protect GeoGebra commands when sent to CAS
	    		// e.g. Element[list, 1] becomes ggbtmpvarElement[list, 1] to
	    		// make sure that the CAS does not evaluate this command, see #1447
	    		cmdname = kernel.printVariableName(cmdname);
	    	}
        }
        return cmdname;
    }   

    /**
     * translate class name to Intergeo name
     * Intergeo File Format (Yves Kreis)
     * @return intergeo command name
     */
    String getIntergeoName() {
        String cmdname, classname;
        // get class name
        //classname = this.getClass().toString();
        //classname = classname.substring(classname.lastIndexOf('.') + 1);
        classname = getClassName();
        // dependent algorithm is an "Expression"
        if (classname.startsWith("AlgoDependent")) {
            cmdname = "Expression";
        } else if (classname.equals("AlgoPointOnPath")) {
        	AlgoPointOnPath algo = (AlgoPointOnPath) this;
        	cmdname = getIntergeoString(classname + "+" + algo.getPath().toGeoElement().getClassName());
        } else {
            // translate algorithm class name to Intergeo name
            cmdname = getIntergeoString(classname);
        }
        return cmdname;
    }   

    /**
     * Returns this algorithm and it's output objects (GeoElement) in XML format.
     * GeoGebra File Format.
     */
	public void getXML(StringBuilder sb) {
    	getXML(sb, true);
    }
    	
	public String getXML() {
		StringBuilder sb = new StringBuilder();
    	getXML(sb, true);
    	return sb.toString();
    }
    	
    final void getXML(StringBuilder sb, boolean includeOutputGeos) {  
        // this is needed for helper commands like 
        // intersect for single intersection points
        if (!isPrintedInXML) return; 
        
        // turn off eg Arabic digits
        boolean oldDigitsSetting = kernel.internationalizeDigits;
        kernel.internationalizeDigits = false;
        
        // USE INTERNAL COMMAND NAMES IN EXPRESSION        
        boolean oldValue = kernel.isPrintLocalizedCommandNames();
        kernel.setPrintLocalizedCommandNames(false);                           
        
        try {
	        // command
	        String cmdname = getCommandName();
	        if (cmdname.equals("Expression"))
	            sb.append(getExpXML());
	        else
	            sb.append(getCmdXML(cmdname));
	        
	        if (includeOutputGeos){// && output != null) {	       
		        getOutputXML(sb);
	        }            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        kernel.setPrintLocalizedCommandNames(oldValue);      
        
        kernel.internationalizeDigits = oldDigitsSetting;

    }
    
    /**
     * concatenate output XML to sb
     * @param sb
     */
    protected void getOutputXML(StringBuilder sb){
    	// output               
        GeoElement geo;                   
        for (int i = 0; i < getOutputLength(); i++) {
            geo = getOutput(i);
            // save only GeoElements that have a valid label
            //Application.debug(geo.toString()+"--"+geo.isLabelSet());
            if (geo.isLabelSet()) {
                geo.getXML(sb);
            }
        }
    }

    /**
     * Returns this algorithm or it's output objects (GeoElement) in I2G format.
     * Intergeo File Format. (Yves Kreis)
     */
    public void getI2G(StringBuilder sb, int mode) {
        // this is needed for helper commands like 
        // intersect for single intersection points
        if (!isPrintedInXML) return; 
        
        // USE INTERNAL COMMAND NAMES IN EXPRESSION        
        boolean oldValue = kernel.isPrintLocalizedCommandNames();
        kernel.setPrintLocalizedCommandNames(false);             
        
        try {
        	if (mode == CONSTRAINTS) {
    	        // command
    	        String cmdname = getIntergeoName();
    	        if (!cmdname.equals("Expression"))
    	            sb.append(getCmdI2G(cmdname));
        	} else {// if (output != null){
    	        // output               
    	        GeoElement geo;                   
    	        for (int i = 0; i < getOutputLength(); i++) {
    	            geo = getOutput(i);
    	            // save only GeoElements that have a valid label
    	            if (geo.isLabelSet()) {
    	                geo.getI2G(sb, mode);
    	            }
    	        }
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        kernel.setPrintLocalizedCommandNames(oldValue);        
    }

    // Expressions should be shown as out = expression
    // e.g. <expression label="u" exp="a + 7 b"/>
    protected String getExpXML() {                
        StringBuilder sb = new StringBuilder();        
        sb.append("<expression");
        // add label
        String labelStr = "";
        if (/*output != null &&*/ getOutputLength() == 1) {
            if (getOutput(0).isLabelSet()) {
                sb.append(" label=\"");
                labelStr = Util.encodeXML(getOutput(0).getLabel());
                sb.append(labelStr);
                sb.append("\"");
            }
        } 
        // add expression       
        String expStr = Util.encodeXML(toExpString());
        sb.append(" exp=\"");                    
        sb.append(expStr);
        sb.append("\"");

        // make sure that a vector remains a vector and a point remains a point
        if (getOutputLength()>0)//(output != null)
        {
        	if (getOutput(0).isGeoPoint()) {
        		sb.append(" type=\"point\"");
        	} else if (getOutput(0).isGeoVector()) {
        		sb.append(" type=\"vector\"");
        	} else if (getOutput(0).isGeoLine()) {
        		sb.append(" type=\"line\"");
        	} else if (getOutput(0).isGeoPlane()) {
        		sb.append(" type=\"plane\"");
        	} else if (getOutput(0).isGeoConic()){
        		sb.append(" type=\"conic\"");
        	}  else if (getOutput(0).isGeoImplicitPoly()){
        		sb.append(" type=\"implicitPoly\"");
        	}

        }

        // expression   
        sb.append(" />\n");
        
        return sb.toString();
    }

    // standard command has cmdname, output, input
    private String getCmdXML(String cmdname) {      
        StringBuilder sb = new StringBuilder();
        sb.append("<command name=\"");
            sb.append(cmdname);
            sb.append("\"");    
                sb.append(">\n");               
                            
        // add input information
        if (input != null) {
            sb.append("\t<input");
            for (int i = 0; i < input.length; i++) {
                sb.append(" a");
                sb.append(i);
                // attribute name is input No. 
                sb.append("=\"");
                
                String cmd = Util.encodeXML(input[i].getLabel());
                
                // ensure a vector stays a vector!  
                // eg g:X = (-5, 5) + t (4, -3) 
                if (input[i].isGeoVector() && !input[i].isLabelSet() && !cmd.startsWith("Vector[")) {
                    // add Vector[ ] command around argument
                    // to make sure that this really becomes a vector again
                	// eg g:X = (-5, 5) + t (4, -3) 
                    sb.append("Vector[");
                    sb.append(cmd);
                    sb.append("]");
                } else {
                	// standard case
                    sb.append(cmd);                 
                }                       
                        
                sb.append("\"");
            }
            sb.append("/>\n");
        } 
        
        // add output information    
        if (getOutputLength()>0) { //(output != null) {
            sb.append("\t<output");
            for (int i = 0; i < getOutputLength(); i++) {
                sb.append(" a");
                sb.append(i);
                // attribute name is output No. 
                sb.append("=\"");     
                if (getOutput(i).isLabelSet()) 
                	sb.append(Util.encodeXML(getOutput(i).getLabel()));
               	sb.append("\"");            
            }
            
            sb.append("/>\n");
        }
        
        sb.append("</command>\n");      
        return sb.toString();
    }

    // standard command has cmdname, output, input
	// Added for Intergeo File Format (Yves Kreis) -->
    private String getCmdI2G(String cmdname) {      
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t<" + cmdname + ">\n");

        String type;

        // add output information    
        //if (output != null) {
            for (int i = 0; i < getOutputLength(); i++) {
            	type = getXMLtypeString(getOutput(i));
                sb.append("\t\t\t<" + type + " out=\"true\">");
                sb.append(Util.encodeXML(getOutput(i).getLabel()));
                sb.append("</" + type + ">\n");
            }
        //}
        
        // add input information
        if (input != null) {
        	if (cmdname.equals("line_parallel_to_line_through_point") ||
            	cmdname.equals("line_perpendicular_to_line_through_point")) {
        		type = getXMLtypeString(input[1]);
        		sb.append("\t\t\t<" + type + ">");
        		sb.append(Util.encodeXML(input[1].getLabel()));                 
        		sb.append("</" + type + ">\n");
        		type = getXMLtypeString(input[0]);
        		sb.append("\t\t\t<" + type + ">");
        		sb.append(Util.encodeXML(input[0].getLabel()));                 
        		sb.append("</" + type + ">\n");
        	} else {
        		for (int i = 0; i < input.length; i++) {
            		type = getXMLtypeString(input[i]);
            		sb.append("\t\t\t<" + type + ">");
            		sb.append(Util.encodeXML(input[i].getLabel()));                 
            		sb.append("</" + type + ">\n");
            	}
            }
        } 
        
        sb.append("\t\t</" + cmdname + ">\n");
        return sb.toString();
    }

	final public String getXMLtypeString(GeoElement geo) {		
		return geo.getClassName().substring(3).toLowerCase(Locale.US);
	}
	// <-- Added for Intergeo File Format (Yves Kreis)

    /**
     * Sets whether the output of this command should
     * be labeled. This setting is used for getXML().
     * @param flag 
     */
    public void setPrintedInXML(boolean flag) {
        isPrintedInXML = flag;
        if (flag)
            cons.addToConstructionList(this, true);
        else 
            cons.removeFromConstructionList(this);
    }
    
    protected boolean isPrintedInXML() {
        return isPrintedInXML;
    }
    
    public String toString() {
    	return getCommandDescription();
    }
    
    protected String toExpString(){
    	return toString();
    }

	final boolean doStopUpdateCascade() {
		return stopUpdateCascade;
	}

	final void setStopUpdateCascade(boolean stopUpdateCascade) {
		this.stopUpdateCascade = stopUpdateCascade;
	}
	
    public boolean wantsConstructionProtocolUpdate() {
    	return false;
    }
    
    
	/**
	 * Makes sure that this algorithm will be updated after the given parentAlgorithm. 
	 * @param updateAfterAlgo 
	 * @see #getUpdateAfterAlgo()
	 */
	final public void setUpdateAfterAlgo(AlgoElement updateAfterAlgo) {
		this.updateAfterAlgo  = updateAfterAlgo;
	}
	private AlgoElement updateAfterAlgo;
	
	/**
	 * Returns the algorithm the should be updated right before this algorithm.
	 * This is being used in AlgorithmSet to sort algorithms by updating order.
	 * @return null when there is no special algorithm that needs to be updated first 
	 * @see #getUpdateAfterAlgo()
	 */
	final public AlgoElement getUpdateAfterAlgo() {
		return updateAfterAlgo;
	}	
	
		
	///////////////////////////////////////////
	// USED FOR PREVIEWABLES
	///////////////////////////////////////////

	
	/**
	 * remove all outputs from algebra view
	 */
	public void removeOutputFromAlgebraView(){
		AlgebraView av = app.getAlgebraView();
		if (av != null)
			for (int i=0; i<getOutputLength();i++)
				av.remove(getOutput(i));
	}	
	
	/**
	 * remove all outputs from picking
	 */
	public void removeOutputFromPicking(){
		for (int i=0; i<getOutputLength();i++)
			getOutput(i).setIsPickable(false);
	}
}
