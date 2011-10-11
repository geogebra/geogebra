/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 * Creates all angles of a polygon.
 */

public class AlgoAnglePolygon extends AlgoElement {

    /**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	private GeoPolygon poly; // input          
    protected OutputHandler<GeoElement> outputAngles;
    private AlgoAnglePoints algoAngle;

    AlgoAnglePolygon(Construction cons, String[] labels, GeoPolygon poly) {        
        this(cons, poly);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		setLabels(labels);
		 
        update();
    }
    
    AlgoAnglePolygon(Construction cons, GeoPolygon p) {
		super(cons);
		this.poly = p;
		outputAngles=createOutputPoints();
		setInputOutput(); // for AlgoElement
		compute();
	}
    
    protected void setLabels(String[] labels) {
        //if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
        if (labels!=null &&
        		labels.length==1 &&
        		//outputPoints.size() > 1 &&
        		labels[0]!=null &&
        		!labels[0].equals("")) {
        	outputAngles.setIndexLabels(labels[0]);
        } else {
        	
        	outputAngles.setLabels(labels);
        	outputAngles.setIndexLabels(outputAngles.getElement(0).getLabel());
        }	
    }

    public String getClassName() {
        return "AlgoAnglePolygon";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGLE;
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = poly;
        
        setDependencies();        
    }

    public GeoElement[] getAngles() {
        return getOutput();
    }
    public GeoPolygon getPolygon() {
        return poly;
    }

    
     protected final void compute() {
    	int length = poly.getPoints().length;		
		outputAngles.adjustOutputSize(length >0?length : 1);
		
		
		for (int i =0; i<length; i++){
			algoAngle = new AlgoAnglePoints(cons,poly.getPoint((i+1)%length), poly.getPoint(i),
					poly.getPoint((i+length-1)%length));
			
    		GeoAngle angle = (GeoAngle) outputAngles.getElement(i);
    		angle.set(algoAngle.getAngle());   
    		angle.setDrawAlgorithm(algoAngle.copy());
    		cons.removeFromConstructionList(algoAngle);
    	}
    	//other points are undefined
    	for(int i = length;i<outputAngles.size();i++)
    		outputAngles.getElement(i).setUndefined();
    }

   final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("AngleOfA",poly.getLabel());
    }
    
    protected OutputHandler<GeoElement> createOutputPoints(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoAngle newElement() {
				GeoAngle p=new GeoAngle(cons);
				p.setValue(0);
				p.setParentAlgorithm(AlgoAnglePolygon.this);
				return p;
			}
		});
    }
}
