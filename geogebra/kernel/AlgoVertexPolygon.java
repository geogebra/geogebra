/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoVertex.java
 *
 * Created on 11. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

/**
 * 
 * @author Zbynek
 * @version
 */
public class AlgoVertexPolygon extends AlgoElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPolyLineInterface p; // input		
	private NumberValue index;
	private GeoPoint oneVertex;
	protected OutputHandler<GeoElement> outputPoints;
	/**
	 * Creates new vertex algo
	 * 
	 * @param cons
	 * @param labels
	 * @param p
	 */

	AlgoVertexPolygon(Construction cons, String[] labels, GeoPolyLineInterface p) {

		this(cons, p);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		setLabels(labels);
		 
        update();

		// set labels dependencies: will be used with
		// Construction.resolveLabelDependency()

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
        	outputPoints.setIndexLabels(outputPoints.getElement(0).getLabel());
        }	
    }

	/**
	 * @param cons
	 * @param label
	 * @param p
	 * @param v
	 */
	AlgoVertexPolygon(Construction cons, String label, GeoPolyLineInterface p,
			NumberValue v) {

		this(cons, p, v);
		oneVertex.setLabel(label);
	}

	/**
	 * Creates new unlabeled vertex algo
	 * 
	 * @param cons
	 * @param p
	 */

	AlgoVertexPolygon(Construction cons, GeoPolyLineInterface p) {
		super(cons);
		this.p = p;
		outputPoints=createOutputPoints();
		setInputOutput(); // for AlgoElement
		compute();
	}

	/**
	 * @param cons
	 * @param p
	 * @param v
	 */
	AlgoVertexPolygon(Construction cons, GeoPolyLineInterface p, NumberValue v) {
		super(cons);
		this.p = p;
		this.index = v;
		oneVertex = new GeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	public String getClassName() {
		return "AlgoVertex";
	}

	// for AlgoElement
	public void setInputOutput() {
		if(index!=null){
			input = new GeoElement[2];
			input[1] = index.toGeoElement();			
			setOutputLength(1);
			setOutput(0,oneVertex);
		}else{
			input = new GeoElement[1];			
		}
		input[0] = (GeoElement)p;
		
		
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the polygon
	 * 
	 * @return input polygon
	 */
	public GeoPolyLineInterface getPolygon() {
		return p;
	}



	public int getOutputLength() {
		if(index!=null) return 1;
		return outputPoints.size();
	}

	protected final void compute() {				
		if(index != null){
			int  i = (int)Math.floor(index.getDouble())-1;
			if(i >= p.getPoints().length||i < 0)
				oneVertex.setUndefined();
			else 
				oneVertex.set((GeoElement)p.getPoint(i));
			oneVertex.update();
			return;
		}
		int length = p.getPoints().length;
		Application.debug(length);
		outputPoints.adjustOutputSize(length >0?length : 1);
		
		
		for (int i =0; i<length; i++){
    		GeoPointND point = (GeoPointND) outputPoints.getElement(i);
    		point.set(p.getPoint(i));    		
    	}
    	//other points are undefined
    	for(int i = length;i<outputPoints.size();i++)
    		outputPoints.getElement(i).setUndefined();
	}

	public final String toString() {
		return app.getPlain("VertexOfA", ((GeoElement)p).getLabel());

	}

	/**
	 * Returns list of the vertices
	 * 
	 * @return list of the vertices
	 */
	public GeoElement[] getVertex() {
		return getOutput();
	}

	public GeoElement getOutput(int i) {
		if(index!=null)return oneVertex;
		return outputPoints.getElement(i);
	}
	
	/**
	 * @return the vertex when called as Vertex[poly,number]
	 */
	public GeoPoint getOneVertex(){
		return oneVertex;
	}
	
	
	 protected OutputHandler<GeoElement> createOutputPoints(){
	    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
				public GeoPoint newElement() {
					GeoPoint p=new GeoPoint(cons);
					p.setCoords(0, 0, 1);
					p.setParentAlgorithm(AlgoVertexPolygon.this);
					return p;
				}
			});
	    }

}