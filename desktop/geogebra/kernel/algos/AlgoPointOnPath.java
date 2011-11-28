/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.kernel.Construction;
import geogebra.kernel.PathAlgo;
import geogebra.kernel.PathNormalizer;
import geogebra.kernel.PathParameter;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.Path;


public class AlgoPointOnPath extends AlgoElement implements PathAlgo {

	private Path path; // input
    private GeoPoint P; // output      
    private NumberValue param;

    public AlgoPointOnPath(
        Construction cons,
        String label,
        Path path,
        double x,
        double y) {
    	
    	this(cons, path, x, y);
       
        P.setLabel(label);
    }

    private void setIncidence() {
    	P.addIncidence((GeoElement) path);
		
	}

	public AlgoPointOnPath(Construction cons, String label, Path path, double x,
			double y, NumberValue param) {
    	this(cons,path,x,y,param);
		P.setLabel(label);
	}
    
    public AlgoPointOnPath(Construction cons,  Path path, double x,
			double y, NumberValue param) {
    	super(cons);
        this.path = path;
        // create point on path and compute current location
        P = new GeoPoint(cons);
        P.setPath(path);
        P.setCoords(x, y, 1.0);
		this.param = param;
		setInputOutput(); // for AlgoElement	       	        
		compute();		
	}

	public AlgoPointOnPath(Construction cons, Path path, double x, double y) {
        super(cons);
        this.path = path;
        
        // create point on path and compute current location
        P = new GeoPoint(cons);
        P.setPath(path);
        setIncidence();
        
        P.setCoords(x, y, 1.0);                   
        
        setInputOutput(); // for AlgoElement
	}

	@Override
	public String getClassName() {
        return "AlgoPointOnPath";
    }

	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POINT;
    }
    
	
    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	if(param == null){
    		input = new GeoElement[1];
    		input[0] = path.toGeoElement();
    	}else {
    		input = new GeoElement[2];
    		input[0] = path.toGeoElement();
    		input[1] = (GeoElement)param.toGeoElement();    		
    	}
        setOutputLength(1);
        setOutput(0, P);
        setDependencies(); // done by AlgoElement
    }

    public GeoPoint getP() {
        return P;
    }
    public Path getPath() {
        return path;
    }
      
    @Override
	public final void compute() {
    	if(param != null){
    		PathParameter pp = P.getPathParameter();
    		//Application.debug(param.getDouble()+" "+path.getMinParameter()+" "+path.getMaxParameter());
    		pp.setT(PathNormalizer.toParentPathParameter(param.getDouble(), path.getMinParameter(), path.getMaxParameter()));
    		//Application.debug(pp.t);
    	}
    	if (input[0].isDefined()) {	    	
	        path.pathChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation        
        return app.getPlain("PointOnA", input[0].getLabel());
    }
    
	public boolean isChangeable() {
		return param == null;
	}
}
