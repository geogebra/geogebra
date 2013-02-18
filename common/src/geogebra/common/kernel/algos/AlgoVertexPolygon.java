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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

/**
 * 
 * @author Zbynek
 */
public class AlgoVertexPolygon extends AlgoElement {

	private GeoPoly p; // input		
	private NumberValue index;
	private GeoPoint oneVertex;
	private OutputHandler<GeoElement> outputPoints;
	/**
	 * Creates new vertex algo
	 * @param cons construction
	 * @param labels labels for vertices
	 * @param p polygon or polyline
	 */

	public AlgoVertexPolygon(Construction cons, String[] labels, GeoPoly p) {

		this(cons, p);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		setLabels(labels);
		 
        update();

		// set labels dependencies: will be used with
		// Construction.resolveLabelDependency()
	}
	
	private void setLabels(String[] labels) {
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
	 * @param cons construction
	 * @param label label
	 * @param p polygon or polyline
	 * @param v vertex index
	 */
	public AlgoVertexPolygon(Construction cons, String label, GeoPoly p,
			NumberValue v) {

		this(cons, p, v);
		oneVertex.setLabel(label);
	}

	/**
	 * Creates algo for Vertex[poly] (many output points)
	 * Creates new unlabeled vertex algo
	 * @param cons construction
	 * @param p polygon or polyline
	 */
	AlgoVertexPolygon(Construction cons, GeoPoly p) {
		super(cons);
		this.p = p;
		outputPoints=createOutputPoints();
		setInputOutput(); // for AlgoElement
		compute();
	}

	/**
	 * Creates algo for Vertex[poly,n] (one output)
	 * @param cons construction or polyline
	 * @param p polygon or polyline
	 * @param v vertrex index
	 */
	AlgoVertexPolygon(Construction cons, GeoPoly p, NumberValue v) {
		super(cons);
		this.p = p;
		this.index = v;
		oneVertex = new GeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Vertex;
	}

	// for AlgoElement
	@Override
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
	public GeoPoly getPolygon() {
		return p;
	}

	@Override
	public int getOutputLength() {
		if(index!=null) return 1;
		return outputPoints.size();
	}

	@Override
	public final void compute() {				
		if(index != null){
			int  i = (int)Math.floor(index.getDouble())-1;
			if(i >= p.getPoints().length||i < 0) {
				oneVertex.setUndefined();
			} else {
				oneVertex.set((GeoElement)p.getPoint(i));
			}
			oneVertex.update();
			return;
		}
		int length = p.getPoints().length;
		App.debug(length);
		outputPoints.adjustOutputSize(length >0?length : 1);
		
		
		for (int i =0; i<length; i++){
    		GeoPointND point = (GeoPointND) outputPoints.getElement(i);
    		point.set(p.getPoint(i));    		
    	}
    	//other points are undefined
    	for(int i = length;i<outputPoints.size();i++) {
    		outputPoints.getElement(i).setUndefined();
    	}
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return loc.getPlain("VertexOfA", ((GeoElement)p).getLabel(tpl));

	}

	/**
	 * Returns list of the vertices
	 * 
	 * @return list of the vertices
	 */
	public GeoElement[] getVertex() {
		return getOutput();
	}

	@Override
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

	private OutputHandler<GeoElement> createOutputPoints(){
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint newElement() {
				GeoPoint pt=new GeoPoint(cons);
				pt.setCoords(0, 0, 1);
				pt.setParentAlgorithm(AlgoVertexPolygon.this);
				return pt;
			}
		});
	}

	// TODO Consider locusequability

}