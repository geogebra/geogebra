/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;


/**
 *Function limited to interval [a, b]
 */
public class AlgoFunctionFreehand extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; // input    
    private GeoFunction g; // output g     
           
    /** Creates new AlgoDependentFunction */
    public AlgoFunctionFreehand(Construction cons, String label, 
    		GeoList f) 
    {
        this(cons, f);
        g.setLabel(label);
    }
    
    public AlgoFunctionFreehand(Construction cons, 
    		GeoList f) 
    {
        super(cons);
        this.inputList = f;
            
        g = new GeoFunction(cons); // output
        FunctionVariable X=new FunctionVariable(kernel);
        ExpressionNode expr=new ExpressionNode(kernel, X,ExpressionNode.SIN, null);
        Function fun = new Function(expr, X);
        g.setFunction(fun);
        g.setDefined(false);
        
        setInputOutput(); // for AlgoElement    
        compute();
    }
    
  
    public String getClassName() {
        return "AlgoFunctionFreehand";
    }   

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = inputList;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getFunction() {
        return g;
    }
    
    protected final void compute() {  
        if (!(inputList.isDefined()) || inputList.getElementType() != GeoElement.GEO_CLASS_NUMERIC || inputList.size() < 4){ 
            g.setUndefined();
            return;
        }
               
        FunctionVariable X=new FunctionVariable(kernel);
        ExpressionNode expr=new ExpressionNode(kernel, X,ExpressionNode.FREEHAND, inputList);
        Function fun = new Function(expr, X);
        g.setFunction(fun);
        g.setDefined(true);
        g.setInterval(((GeoNumeric)inputList.get(0)).getDouble(), ((GeoNumeric)inputList.get(1)).getDouble());
       
    }
    

    final public String toString() {
    	if (inputList.size() < 4 || inputList.getElementType() != GeoElement.GEO_CLASS_NUMERIC) return app.getPlain("undefined");
        return app.getPlain("FreehandFunctionOnIntervalAB",kernel.format(((GeoNumeric)inputList.get(0)).getDouble()), kernel.format(((GeoNumeric)inputList.get(1)).getDouble()));

    }

}
