/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoRotatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.RotateableND;


/**
 *
 * @author  mathieu
 */
public abstract class AlgoRotate3D extends AlgoTransformation {

	protected GeoElement inGeo, outGeo;
    protected RotateableND out;    
    protected NumberValue angle; 
    
 
    
    /**
     * Creates new unlabeled point rotation algo
     */
    public AlgoRotate3D(Construction cons, 
    		GeoElement in,
    		NumberValue angle) {
    	
        super(cons);   
        this.inGeo = in;
        this.angle = angle;

        // create output object
        outGeo = getResultTemplate(inGeo);
        if(outGeo instanceof RotateableND)
        	out = (RotateableND) outGeo;

         
    }

  
 	/**
 	 * 
 	 */
 	protected void setOutput() { 
  	

        setOutputLength(1);
        setOutput(0, outGeo);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the rotated point
     * @return rotated point
     */
    @Override
	public
	GeoElement getResult() {
        return outGeo;
    }

  
       
    @Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if(!(outGeo instanceof GeoList))
			out = (RotateableND)outGeo;
		
	}
    
    
    @Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if(geo instanceof GeoFunction)
			return new GeoCurveCartesian3D(cons);

		return super.getResultTemplate(geo);
	}
	
	@Override
	protected GeoElement copy(GeoElement geo) {
		return ((Kernel3D) kernel).copy3D(geo);
	}

	@Override
	protected GeoElement copyInternal(Construction cons, GeoElement geo){
		return ((Kernel3D) kernel).copyInternal3D(cons,geo);
	}
	
    
    protected void toGeoCurveCartesian(GeoFunction geoFun, GeoCurveCartesian3D curve){
    	FunctionVariable t = new FunctionVariable(kernel, "t");
		FunctionVariable x = geoFun.getFunction().getFunctionVariable();
		ExpressionNode yExp = (ExpressionNode) ((ExpressionNode) geoFun.getFunction()
				.getExpression().deepCopy(kernel)).replace(x, t);
		Function[] fun = new Function[3];
		fun[0] = new Function(new ExpressionNode(kernel, t), t);
		fun[1] = new Function(yExp, t);
		fun[2] = new Function(new ExpressionNode(kernel, 0), t);
		curve.setFun(fun);
		if (geoFun.hasInterval()) {
			curve.setInterval(geoFun.getIntervalMin(), geoFun.getIntervalMax());
		} else {
			double min = kernel.getXminForFunctions();
			double max = kernel.getXmaxForFunctions();
			curve.setInterval(min, max);
			//curve.setHideRangeInFormula(true);
		}
    }

    
}
