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
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;

import java.util.Arrays;

/**
 * Abstract class with all the label methods needed to update
 * labels of commands on functions, where the command returns
 * a varying number of GeoPoints.
 * This is to avoid a lot of duplicated label-updating code.
 * Most of the code is copied from AlgoRootsPolynomial.
 * (Where it might be eliminated later...)
 * 
 * @author Hans-Petter Ulven
 * @version 06.03.11
 * 
 */
public abstract class AlgoGeoPointsFunction extends AlgoElement{

	protected GeoFunction f;			 // For calculation of y-values 
    
    protected GeoPoint[] points;		// output in subcclass  

    private String[] labels;
    private boolean initLabels;
	protected boolean setLabels = false;
	
    //remove? double[] 		curXValues = new double[30]; // current x-values
    int numberOfXValues;

    /**
     * Computes all roots of f
     */
    public AlgoGeoPointsFunction(
            Construction cons,
            String[] labels,
            boolean setLabels,
            GeoFunction f) {
        	super(cons);
        	this.labels=labels;
        	this.setLabels=setLabels;			//In subclass: !cons.isSuppressLabelsActive();
        	this.f=f;
        	
            //  make sure root points is not null
            int number = labels == null ? 1 : Math.max(1, labels.length);
            points = new GeoPoint[0];
            initPoints(number);
            initLabels = true;  
            // setInputOutput, compute(), show at least one point: must be done in subclass.
        }//Constructor

    public AlgoGeoPointsFunction(
            Construction cons,
            GeoFunction f) {
        	super(cons);
        	this.f=f;
            
            //  make sure root points is not null
            int number = 1;
            points = new GeoPoint[0];
            initPoints(number);
            // setInputOutput, compute(), show at least one point: must be done in subclass.
        }//Constructor


    /**
     * The given labels will be used for the resulting points.   
     */
    public void setLabels(String[] labels) {
        this.labels = labels;
        setLabels = true;

        // make sure that there are at least as many
        // points as labels
        if (labels != null)
            initPoints(labels.length);

        update();
    }//setLabels(String[])

    @Override
	public abstract Algos getClassName();



    public GeoPoint[] getPoints() {
        return points;
    }//getPoints()


    // Show at least one root point in algebra view
	// Copied from AlgoRootsPolynomial...
    protected final void showOneRootInAlgebraView(){	
    	if(!points[0].isDefined() ) {
    		points[0].setCoords(0,0,1);
    		points[0].update();
    		points[0].setUndefined();
    		points[0].update();
    	}//if list not defined
    }//showOneRootInAlgebraView()
    
    protected final static void removeDuplicates(double[] tab){
    	Arrays.sort(tab);
    	int maxIndex=0;
    	double max=tab[0];
    	for(int i=1; i<tab.length;i++) {
    		if((tab[i]-max) > Kernel.MIN_PRECISION){
    			max=tab[i];
    			maxIndex++;
    			tab[maxIndex]=max;
    		}//if greater
    	}//for
    }//removeDuplicates(double[])

    // roots array and number of roots
    protected final void setPoints(double[] curXValues, int number) {
        initPoints(number);

        // now set the new values of the roots
        for (int i = 0; i < number; i++) {

                points[i].setCoords(
                    curXValues[i],
                    f.evaluate(curXValues[i]),							//yValFunction.evaluate(curXValues[i]),
                    1.0);
                
              //  Application.debug("   " + rootPoints[i]); 
            
        }//for

        // all other roots are undefined
        for (int i = number; i < points.length; i++) {
            points[i].setUndefined();
        }//

        if (setLabels)
            updateLabels(number);
        noUndefinedPointsInAlgebraView(points);			//**** experiment****
    }//setPoints(double[],n)

    // number is the number of current roots
    protected void updateLabels(int number) {  
    	if (initLabels) {
    		GeoElement.setLabels(labels, points);
    		initLabels = false;
    	} else {	    
	        for (int i = 0; i < number; i++) {
	            //  check labeling      
	            if (!points[i].isLabelSet()) {
	            	// use user specified label if we have one
	            	String newLabel = (labels != null && i < labels.length) ? labels[i] : null;	            	
	                points[i].setLabel(newLabel);	                
	            }//if
	        }//for
    	}//if
        
        // all other roots are undefined
        for (int i = number; i < points.length; i++) {
        	points[i].setUndefined();						//Points[i].setAlgebraVisible(false);
        }//for
    }//updateLabels(n)
    
    
    protected void noUndefinedPointsInAlgebraView(GeoPoint[] gpts) {
   	 for (int i=1; i < gpts.length; i++) {
   		 gpts[i].showUndefinedInAlgebraView(false);
   	 }//for
    }//noUndefinedPointsInAlgebraView(GeoPoint[])
    
    /**
     * Removes only one single output element if possible. 
     * If this is not possible the whole algorithm is removed.
     */
    @Override
	public
	void remove(GeoElement output) {
    	// only single undefined points may be removed       
        for (int i = 0; i < points.length; i++) {
        	if (points[i] == output && !points[i].isDefined()) {
        		removePoint(i);      		
        		return;
        	}//if            
        }//for
    	
        // if we get here removing output was not possible
        // so we remove the whole algorithm
        super.remove();
    }//remove(GeoElement)

    protected void initPoints(int number) {
        // make sure that there are enough points   
        if (points.length < number) {
            GeoPoint[] temp = new GeoPoint[number];
            for (int i = 0; i < points.length; i++) {
                temp[i] = points[i];
                temp[i].setCoords(0, 0, 1); // init as defined
            }
            for (int i = points.length; i < temp.length; i++) {
                temp[i] = new GeoPoint(cons);
                temp[i].setCoords(0, 0, 1); // init as defined
                temp[i].setParentAlgorithm(this);
            }
            points = temp;
            super.setOutput(points);
        }//if
    }//initPoints(n)
    
    private void removePoint(int pos) {
    	points[pos].doRemove();
    	
    	// build new rootPoints array without the removed point
    	GeoPoint[] temp = new GeoPoint[points.length - 1];
    	int i;
    	for (i=0; i < pos; i++) 
    		temp[i] = points[i];        		
    	for (i=pos+1; i < points.length; i++) 
    		temp[i-1] = points[i];
    	points = temp;
    }//removePoint(int pos)

 // * //--- SNIP (after debugging and testing) -------------------------   
    /// --- Test interface --- ///

    // Needed for script testing of children
    public AlgoGeoPointsFunction(Construction cons){
    	super(cons);
    }//Test Constructor

    
// */ //--- SNIP end ---------------------------------------    

}//class AlgoGeoPontsFunction

