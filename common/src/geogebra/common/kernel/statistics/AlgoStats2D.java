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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * Mean, covariance, sum, sum of squares, etc 
 * from two lists or a list of points
 * adapted from AlgoListMin
 * to replace AlgoMean, AlgoSum
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public abstract class AlgoStats2D extends AlgoElement {

	
	private GeoList geoListx; //input
	private GeoList geoListy; //input
    public GeoNumeric result; //output	
    
    private int mode;
    
    final static int MODE_DOUBLELIST=0;
    final static int MODE_LISTOFPOINTS=1;
    
    private int stat;
    
    final static int STATS_MEANX = 0;
    final static int STATS_MEANY = 1;
    final static int STATS_COVARIANCE = 2;
    final static int STATS_SIGMAXY = 3;
    final static int STATS_SXX = 4;
    final static int STATS_SYY = 5;
    final static int STATS_SXY = 6;
    final static int STATS_PMCC = 7;
    final static int STATS_SIGMAXX = 8;
    final static int STATS_SIGMAYY = 9;
    final static int STATS_SAMPLESDX = 10;
    final static int STATS_SAMPLESDY = 11;
    final static int STATS_SDX = 12;
    final static int STATS_SDY = 13;
    
    public AlgoStats2D(Construction cons, String label, GeoList geoListx, GeoList geoListy, int stat) {
        super(cons);
    	mode=MODE_DOUBLELIST;
        this.geoListx = geoListx;
        this.geoListy = geoListy;
        this.stat=stat;
        
        result = new GeoNumeric(cons);

        setInputOutput();
        compute();
        result.setLabel(label);
    }

    public AlgoStats2D(Construction cons, String label, GeoList geoListx, int stat) {
        this(cons, geoListx, stat);
        result.setLabel(label);
    }

    public AlgoStats2D(Construction cons, GeoList geoListx, int stat) {
        super(cons);
        mode=MODE_LISTOFPOINTS;
        this.geoListx = geoListx;
        this.stat=stat;
        
        result = new GeoNumeric(cons);

        //setInputOutput();
        input = new GeoElement[1];
        input[0] = geoListx;

        setOutputLength(1);
        setOutput(0,result);
        setDependencies(); // done by AlgoElement
        compute();
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = geoListx;
        input[1] = geoListy;

        setOutputLength(1);
        setOutput(0,result);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getResult() {
        return result;
    }
    

    @Override
	final public void compute() {
    	double sumx = 0;
    	double sumy = 0;
    	double sumxx = 0;
    	double sumxy = 0;
    	double sumyy = 0;
    	double valx,valy;
    	int sizex= geoListx.size();
    	int sizey=sizex; 
    	if (mode==MODE_DOUBLELIST)
    	{
        	sizey = geoListy.size();
        	if (!geoListx.isDefined() || !geoListy.isDefined() ||  sizex == 0 || sizex!=sizey) {
        		result.setUndefined();
        		return;
        	}
        	
        	for (int i=0; i < sizex; i++) {
        		GeoElement geox = geoListx.get(i);
        		GeoElement geoy = geoListy.get(i);
        		if (geox.isNumberValue() && geoy.isNumberValue()) {
        			NumberValue numx = (NumberValue) geox;
        			NumberValue numy = (NumberValue) geoy;
        			valx=numx.getDouble();
        			valy=numy.getDouble();
        			sumx+=valx;
        			sumy+=valy;
        			sumxx+=valx*valx;
        			sumyy+=valy*valy;
        			sumxy+=valx*valy;
        		} else {
            		result.setUndefined();
        			return;
        		}    		    		
        	}   
    	}
    	else
    	{ // MODE_LISTOFPOINTS
            for (int i=0 ; i<sizex ; i++)
            {
       		 GeoElement geo = geoListx.get(i); 
     		 if (geo.isGeoPoint()) {
     			double x=((GeoPoint)geo).getX();
     			double y=((GeoPoint)geo).getY();
     			double z=((GeoPoint)geo).getZ();
     			valx=x/z;
     			valy=y/z;
    			sumx+=valx;
    			sumy+=valy;
    			sumxx+=valx*valx;
    			sumyy+=valy*valy;
    			sumxy+=valx*valy;
     		 }
     		 else
     		 {
     			result.setUndefined();	
         		return;			
     		 }
            }
    		
    	}
    	
    	double mux=sumx/sizex;
    	double muy=sumy/sizex;
    	double var;
    	
        switch (stat)
        {
        case  STATS_MEANX:
        	result.setValue(mux);
        	break;
        case  STATS_MEANY:
        	result.setValue(muy);
        	break;
        case  STATS_COVARIANCE:
        	result.setValue(sumxy/sizex-mux*muy);
        	break;
        case  STATS_SIGMAXY:
        	result.setValue(sumxy);
        	break;
        case  STATS_SIGMAXX:
        	result.setValue(sumxx);
        	break;
        case  STATS_SIGMAYY:
        	result.setValue(sumyy);
        	break;
        case  STATS_SXX :
        	result.setValue(sumxx-sumx*sumx/sizex);
        	break;
        case  STATS_SYY:
        	result.setValue(sumyy-sumy*sumy/sizex);
        	break;
        case  STATS_SXY:
        	result.setValue(sumxy-sumx*sumy/sizex);
        	break;
        case  STATS_PMCC:
        	result.setValue((sumxy*sizex-sumx*sumy)/Math.sqrt((sumxx*sizex-sumx*sumx)*(sumyy*sizex-sumy*sumy)));
        	break;
        case  STATS_SAMPLESDX:
        	var=(sumxx - sumx * sumx / sizex) / (sizex -1);
			result.setValue(Math.sqrt(var));
        	break;
        case  STATS_SAMPLESDY:
        	var=(sumyy - sumy * sumy / sizey) / (sizey -1);
			result.setValue(Math.sqrt(var));
        	break;
        case  STATS_SDX:
        	var=(sumxx - sumx * sumx / sizex) / sizex;
			result.setValue(Math.sqrt(var));
        	break;
        case  STATS_SDY:
        	var=(sumyy - sumy * sumy / sizey) / sizey;
			result.setValue(Math.sqrt(var));
        	break;
        }
    }

	// TODO Consider locusequability
    
}


