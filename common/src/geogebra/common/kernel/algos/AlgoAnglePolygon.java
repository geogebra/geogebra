/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;


/**
 * Creates all angles of a polygon.
 */

public class AlgoAnglePolygon extends AlgoAngle {

    /**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	private GeoPolygon poly; // input          
    protected OutputHandler<GeoElement> outputAngles;
    private AlgoAnglePoints algoAngle;

    public AlgoAnglePolygon(Construction cons, String[] labels, GeoPolygon poly) {        
        this(cons, poly);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		setLabels(labels);
		 
        update();
    }
    
    AlgoAnglePolygon(Construction cons, GeoPolygon p) {
		super(cons);
		algoAngle = newAlgoAnglePoints(cons);
		this.poly = p;
		outputAngles=createOutputPoints();
		setInputOutput(); // for AlgoElement
		compute();
	}
    
    /**
     * 
     * @param cons
     * @return helper algo
     */
    protected AlgoAnglePoints newAlgoAnglePoints(Construction cons){
    	return new AlgoAnglePoints(cons);
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
        	outputAngles.setIndexLabels(outputAngles.getElement(0).getLabel(StringTemplate.defaultTemplate));
        }	
    }

    @Override
	public Commands getClassName() {
        return Commands.Angle;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGLE;
    }

    // for AlgoElement
    @Override
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

    
     @Override
	public final void compute() {
    	int length = poly.getPoints().length;		
		outputAngles.adjustOutputSize(length >0?length : 1);
		
		
		for (int i =0; i<length; i++){
			algoAngle.setABC(poly.getPointND((i+1)%length), poly.getPointND(i),
					poly.getPointND((i+length-1)%length));
			algoAngle.compute();
			
    		GeoAngle angle = (GeoAngle) outputAngles.getElement(i);
    		angle.set(algoAngle.getAngle());
    		if(!angle.isDrawable){
    			angle.setDrawable(true);
    			if (angle.isLabelVisible()) 
					angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
				else{ 
					angle.setLabelMode(GeoElement.LABEL_VALUE);
					angle.setLabelVisible(true);					
				}
    		}
    		angle.setDrawAlgorithm(algoAngle.copy());
    		cons.removeFromConstructionList(algoAngle);
    	}
    	//other points are undefined
    	for(int i = length;i<outputAngles.size();i++)
    		outputAngles.getElement(i).setUndefined();
    }

   @Override
final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return loc.getPlain("AngleOfA",poly.getLabel(tpl));
    }
    
    protected OutputHandler<GeoElement> createOutputPoints(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			@Override
			public GeoAngle newElement() {
				GeoAngle p = newGeoAngle(cons);
				p.setValue(0);
				p.setParentAlgorithm(AlgoAnglePolygon.this);
				return p;
			}
		});
    }

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec, DrawAngle drawable) {
		// nothing to do here
		return true;
	}
	
	@Override
	public boolean getCoordsInD3(Coords[] drawCoords){
		// nothing to do here
		return true;
	}

	// TODO Consider locusequability
}
