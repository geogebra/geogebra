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
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;



public class AlgoPointOnPath extends AlgoElement implements PathAlgo {

	private static final long serialVersionUID = 1L;
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

	public String getClassName() {
        return "AlgoPointOnPath";
    }

	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POINT;
    }
    
	
    // for AlgoElement
    protected void setInputOutput() {
    	if(param == null){
    		input = new GeoElement[1];
    		input[0] = path.toGeoElement();
    	}else {
    		input = new GeoElement[2];
    		input[0] = path.toGeoElement();
    		input[1] = param.toGeoElement();    		
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
      
    protected final void compute() {
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

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation        
        return app.getPlain("PointOnA", input[0].getLabel());
    }
    
	public boolean isChangeable() {
		return param == null;
	}
}
