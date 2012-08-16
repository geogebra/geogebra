/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

import java.util.TreeMap;


/**
 * Algo for intersection of a line with a polyLine
 * @author Tam
 * @version 
 */
public class AlgoIntersectLinePolyLine extends AlgoElement{

    protected GeoLineND g; // input
	protected GeoPolyLine p; //input
	protected GeoPoly pi;
	protected OutputHandler<GeoElement> outputPoints; // output
	//protected OutputHandler<GeoElement> outputSegments; // output 
    
    
    private TreeMap<Double, Coords> newCoords;
    //private TreeMap<Double, Coords[]> newSegmentCoords;

	//protected boolean pAsBoundary;
    
    /** 
     * common constructor
     * @param c 
     * @param labels
     * @param g
     * @param p
     */
    public AlgoIntersectLinePolyLine(Construction c, String[] labels, GeoLineND g, GeoPoly p) {

    	super(c);
        
		outputPoints=createOutputPoints();

        this.g = g;
        this.pi = p;
        
        newCoords = new TreeMap<Double, Coords>(Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));
    
        compute();
        
        setInputOutput(); // for AlgoElement
        
        setLabels(labels);
 
        update();    
	}

	 /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
    	//TODO: should be called dynamically

	}

    
    protected void setLabels(String[] labels) {
        //if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
        if (labels!=null &&
        		labels.length==1 &&
        		//outputPoints.size() > 1 &&
        		labels[0]!=null &&
        		!labels[0].equals("")) {
        	outputPoints.setIndexLabels(labels[0]);
        } else {
        	
        	outputPoints.setLabels(labels);
        	outputPoints.setIndexLabels(outputPoints.getElement(0).getLabel(StringTemplate.defaultTemplate));
        }	
    }
    
	/**
     * 
     * @return handler for output points
     */
    protected OutputHandler<GeoElement> createOutputPoints(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint newElement() {
				GeoPoint p=new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLinePolyLine.this);
				return p;
			}
		});
    }
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoIntersectLinePolyLine;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) g;
        input[1] = (GeoElement) pi;
        
        setDependencies(); // done by AlgoElement
    }

    protected void intersectionsCoords(GeoLineND g, GeoPolyLine p, TreeMap<Double, Coords> newCoords){
    	
    	double min = g.getMinParameter();
    	double max = g.getMaxParameter();
    	Coords gCoords = ((GeoVec3D)g).getCoords();

    	for(int i=0; i<p.getNumPoints()-1; i++){
    		
    		Coords segStart = p.getPoint(i).getCoords();
    		Coords segEnd = p.getPoint(i+1).getCoords();
    		
    		Coords coords = segStart.crossProduct(segEnd).crossProduct(gCoords);
    		
    		if (Kernel.isZero(coords.getLast())){
    			if (((GeoLine) g).isOnPath(segStart, Kernel.EPSILON) &&
    					((GeoLine) g).isOnPath(segEnd, Kernel.EPSILON)	) {
    				newCoords.put(((GeoLine) g).getPossibleParameter(segStart), segStart);
    				newCoords.put(((GeoLine) g).getPossibleParameter(segEnd), segEnd);
    			}
    		} else if ( GeoSegment.checkOnPath(segStart,segEnd,coords,false,Kernel.EPSILON) ) {
       			double t = ((GeoLine) g).getPossibleParameter(coords);
    			//Application.debug("parameter("+i+") : "+t);
       			if (t>=min && t<=max)
       				newCoords.put(t, coords);
    		}
        }
    }
        
    @Override
	public void compute() { 	
    	//clear the points map
    	newCoords.clear();
    	
    	//fill a new points map
    	this.p = (GeoPolyLine)pi.getBoundary();
    	intersectionsCoords(g, p, newCoords);
    	
    	//update and/or create points
    	outputPoints.adjustOutputSize(newCoords.size() > 0 ? newCoords.size() : 1);
    	
    	//affect new computed points
    	int index = 0;
    	for (Coords coords : newCoords.values()){
    		GeoPointND point = (GeoPointND) outputPoints.getElement(index);
    		point.setCoords(coords,false);
    		point.updateCoords();
    		index++;
    	}
    	//other points are undefined
    	for(; index<outputPoints.size(); index++) {
    		outputPoints.getElement(index).setUndefined();
    	}
    }

	@Override
	final public String toString(StringTemplate tpl) {
        return app.getPlain("IntersectionPointOfAB",((GeoElement) g).getLabel(tpl),
        		((GeoElement)pi).getLabel(tpl));
    }  

	// TODO Consider locusequability
    
}
