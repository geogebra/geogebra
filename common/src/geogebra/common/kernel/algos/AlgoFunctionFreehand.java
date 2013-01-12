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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;


/**
 *Function limited to interval [a, b]
 */
public class AlgoFunctionFreehand extends AlgoElement {

	private GeoList inputList; // input    
    private GeoFunction g; // output g     
           
    /** Creates new AlgoDependentFunction */
    public AlgoFunctionFreehand(Construction cons, String label, 
    		GeoList f) {
        this(cons, f);
        g.setLabel(label);
    }
    
    public AlgoFunctionFreehand(Construction cons, 
    		GeoList f) {
        super(cons);
        this.inputList = f;
            
        g = new GeoFunction(cons); // output
        FunctionVariable X=new FunctionVariable(kernel);
        ExpressionNode expr=new ExpressionNode(kernel, X,Operation.SIN, null);
        Function fun = new Function(expr, X);
        g.setFunction(fun);
        g.setDefined(false);
        
        setInputOutput(); // for AlgoElement    
        compute();
    }
    
    @Override
	public Algos getClassName() {
        return Algos.Expression;
    }   

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = inputList;

        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getFunction() {
        return g;
    }
    
    @Override
	public final void compute() {  
        if (!(inputList.isDefined()) || !inputList.getElementType().equals(GeoClass.NUMERIC) || inputList.size() < 4){ 
            g.setUndefined();
            return;
        }
               
        FunctionVariable X=new FunctionVariable(kernel);
        ExpressionNode expr=new ExpressionNode(kernel, X,Operation.FREEHAND, inputList);
        Function fun = new Function(expr, X);
        g.setFunction(fun);
        g.setDefined(true);
        g.setInterval(((GeoNumeric)inputList.get(0)).getDouble(), ((GeoNumeric)inputList.get(1)).getDouble());    
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
    	if (inputList.size() < 4 || !inputList.getElementType().equals(GeoClass.NUMERIC)) return app.getPlain("Undefined");
        return app.getPlain("FreehandFunctionOnIntervalAB",kernel.format(((GeoNumeric)inputList.get(0)).getDouble(),tpl),
        		kernel.format(((GeoNumeric)inputList.get(1)).getDouble(),tpl));
    }

	public GeoList getList() {
		return inputList;	
	}

	// TODO Consider locusequability

}
