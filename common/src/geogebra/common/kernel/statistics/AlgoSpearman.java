/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;

import org.apache.commons.math.stat.correlation.SpearmansCorrelation;

/**
 * Finds Spearman's correlation coefficient from a list of points or two lists of
 * numbers.
 * 
 * @author G. Sturr
 */
public class AlgoSpearman extends AlgoElement {

	
	private GeoList geoListPts, geoListX, geoListY; //input
	private GeoNumeric  result;     // output   
	private SpearmansCorrelation sp;
	private double[] valX;
	private double[] valY;

	public AlgoSpearman(Construction cons, String label, GeoList geoListX, GeoList geoListY) {
		super(cons);
		this.geoListX = geoListX;
		this.geoListY = geoListY;
		this.geoListPts = null;
		result = new GeoNumeric(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}

	public AlgoSpearman(Construction cons, String label, GeoList geoList) {
		this(cons, geoList);      
		result.setLabel(label);
	}

	public AlgoSpearman(Construction cons, GeoList geoList) {
		super(cons);
		this.geoListX = null;
		this.geoListY = null;
		this.geoListPts = geoList;

		result = new GeoNumeric(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
	}

	@Override
	public Commands getClassName() {
		return Commands.Spearman;
	}

	@Override
	protected void setInputOutput(){

		if(geoListPts != null){
			input = new GeoElement[1];
			input[0] = geoListPts;
		}else{
			input = new GeoElement[2];
			input[0] = geoListX;
			input[1] = geoListY;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {

		if(input.length == 1){
			// input is single list of points
			int size= geoListPts.size();
			if(!geoListPts.isDefined() || size < 2){
				result.setUndefined();	
				return;			
			}

			valX = new double[size];
			valY = new double[size];

			for (int i = 0 ; i < size ; i++)
			{
				GeoElement geo = geoListPts.get(i); 
				if (geo.isGeoPoint()) {
					double x=((GeoPoint)geo).getX();
					double y=((GeoPoint)geo).getY();
					double z=((GeoPoint)geo).getZ();
					valX[i] = x/z;
					valY[i] = y/z;
				}else{
					result.setUndefined();	
					return;			
				}
			}

		}else{
			// input is two lists
			int sizeX = geoListX.size();
        	int sizeY = geoListY.size();
        	if (!geoListX.isDefined() || !geoListY.isDefined() ||  sizeX < 2 || sizeX != sizeY) {
        		result.setUndefined();
        		return;
        	}
        	
        	valX = new double[sizeX];
			valY = new double[sizeX];
        	
			for (int i=0; i < sizeX; i++) {
        		GeoElement geox = geoListX.get(i);
        		GeoElement geoy = geoListY.get(i);
        		if (geox.isNumberValue() && geoy.isNumberValue()) {
        			NumberValue numx = (NumberValue) geox;
        			NumberValue numy = (NumberValue) geoy;
        			valX[i] = numx.getDouble();
        			valY[i] = numy.getDouble();
        			
        		} else {
            		result.setUndefined();
        			return;
        		}    		    		
        	}   
    	}
			
		
		if(sp == null)
			sp = new SpearmansCorrelation();
		
		result.setValue(sp.correlation(valX, valY));

	}

	// TODO Consider locusequability

}
