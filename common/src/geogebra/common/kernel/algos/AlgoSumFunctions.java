/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;


import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Sum of functions, may take whole list or just several first elements 
 */
public class AlgoSumFunctions  extends AlgoElement {

	@Override
	public Commands getClassName() {
		return Commands.Sum;
	}

	private GeoList geoList; //input
    private GeoNumeric truncate; //input	
    private GeoFunction resultFun;
    
   
    /**
     * Creates labeled function sum algo
     * @param cons
     * @param label
     * @param geoList
     */
    public AlgoSumFunctions(Construction cons, String label, GeoList geoList) {
        this(cons, label, geoList, null);
    }

    /**
     * Creates labeled function sum algo for truncated list 
     * (or whole list if truncate == null)
     * @param cons
     * @param label
     * @param geoList
     * @param truncate
     */
    public AlgoSumFunctions(Construction cons, String label, GeoList geoList, GeoNumeric truncate) {
    	super(cons);
        this.geoList = geoList;
        this.truncate = truncate;
        
        resultFun = new GeoFunction(cons);

        setInputOutput();
        compute();
        resultFun.setLabel(label);
    }

    @Override
	protected void setInputOutput(){
    	if (truncate == null) {
	        input = new GeoElement[1];
	        input[0] = geoList;
    	}
    	else {
    		 input = new GeoElement[2];
             input[0] = geoList;
             input[1] = truncate;
    	}

        setOutputLength(1);
        setOutput(0, resultFun);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns result
     * @return sum of functions
     */
    public GeoElement getResult() {
        return resultFun;
    }  

    @Override
	public final void compute() {
    	//Sum[{x^2,x^3}]
    	
    	int n = truncate == null ? geoList.size() : (int)truncate.getDouble();
    	
    	if (n == 0 || n > geoList.size()) {
    		resultFun.setUndefined();
    		return;
    	}
    	else if (n == 1)
    	{
    		if (!geoList.get(0).isGeoFunctionable()) {
        		resultFun.setUndefined();
        		return;
        	}
    		
           	GeoFunction fun1 = ((GeoFunctionable)geoList.get(0)).getGeoFunction();

        	FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
        	FunctionVariable x =  new FunctionVariable(kernel);

        	ExpressionNode left = fun1.getFunctionExpression().getCopy(fun1.getKernel());
        	
        	Function f = new Function(left.replace(x1,x).wrap(),x);
        	
           	resultFun.setFunction(f);
           	resultFun.setDefined(true);
    		return;
    	}

		if (!geoList.get(0).isGeoFunctionable() || !geoList.get(1).isGeoFunctionable()) {
    		resultFun.setUndefined();
    		return;
    	}		
		
	    // add first two:
	    resultFun = GeoFunction.add(resultFun,((GeoFunctionable)geoList.get(0)).getGeoFunction(), 
	    		((GeoFunctionable)geoList.get(1)).getGeoFunction());
	    	
	    if (n == 2) return;
	    	
	    for (int i = 2 ; i < n ; i++) {  	
	    		
	    	if (!geoList.get(i).isGeoFunctionable()) {
	       		resultFun.setUndefined();
	       		return;
	       	}
	    	resultFun = GeoFunction.add(resultFun,resultFun, ((GeoFunctionable)geoList.get(i)).getGeoFunction());
	    }
    }	

	// TODO Consider locusequability

}
