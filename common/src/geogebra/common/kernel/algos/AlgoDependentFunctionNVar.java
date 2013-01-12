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
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.main.App;

/**
 * This class is needed to handle dependent multivariate functions like
 * e.g. f(x,y) = a x^2 + b y that depends on a and b.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentFunctionNVar extends AlgoElement implements DependentAlgo {

	private FunctionNVar fun;
    private GeoFunctionNVar f; // output         
    private ExpressionNode expression;
    private FunctionNVar expandedFun;
    private boolean expContainsFunctions;
    /**
     * @param cons construction
     * @param label label for output
     * @param fun input function
     */
    public AlgoDependentFunctionNVar(Construction cons, String label, FunctionNVar fun) {
        this(cons, fun);
       	f.setLabel(label);
    }
    
    /**
     * @param cons construction
     * @param fun input function
     */
    AlgoDependentFunctionNVar(Construction cons, FunctionNVar fun) {
        super(cons);
        this.fun = fun;
        f = new GeoFunctionNVar(cons);
        f.setFunction(fun);
        expression = fun.getExpression();
		expContainsFunctions = AlgoDependentFunction.containsFunctions(expression);
		if (expContainsFunctions) {
			expandedFun = new FunctionNVar(fun, kernel);
		}
        setInputOutput(); // for AlgoElement
        
        compute();
        f.setConstructionDefaults();
    }
    
    /**
     * @param cons construction
     */
    public AlgoDependentFunctionNVar(Construction cons) {
		super(cons);
	}

    @Override
	public Algos getClassName() {
		return Algos.Expression;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = fun.getGeoElementVariables();

        setOutputLength(1);
        setOutput(0,f);
        setDependencies(); // done by AlgoElement
    }

    /**
     * @return resulting function
     */
    public GeoFunctionNVar getFunction() {
        return f;
    }

    @Override
	public final void compute() {
        // evaluation of function will be done in view (see geogebra.euclidian.DrawFunction)
        
        // check if function is defined
        boolean isDefined = true;
        for (int i=0; i < input.length; i++) {
            if (!input[i].isDefined()) {
                isDefined = false;
                break;
            }
        }
        if (isDefined && expContainsFunctions) {
			// expand the functions and derivatives in expression tree
			ExpressionValue ev = null;

			try { // needed for eg f(x)=floor(x) f'(x)
				ev = AlgoDependentFunction.expandFunctionDerivativeNodes(expression.deepCopy(kernel));
			} catch (Exception e) {
				e.printStackTrace();
				App.debug("derivative failed");
			}

			if (ev == null) {
				f.setUndefined();
				return;
			}

			ExpressionNode node;
			if (ev.isExpressionNode())
				node = (ExpressionNode) ev;
			else
				node = new ExpressionNode(kernel, ev);

			expandedFun.setExpression(node);
			f.setFunction(expandedFun);
			//we need this to update the borders, see #2880
			if (f.isBooleanFunction() && f.isLabelSet())
				f.resetIneqs();
		}
        
        
        f.setDefined(isDefined);
    }
    
    private StringBuilder sb;
    @Override
	public String toString(StringTemplate tpl) {    	
        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
        if (f.isLabelSet() && !f.isBooleanFunction()) {
            sb.append(f.getLabel(tpl));
            sb.append("(");
			sb.append(f.getVarString(tpl));
			sb.append(") = ");
        }  
        sb.append(fun.toString(tpl));
        return sb.toString();
    }

	// TODO Consider locusequability
    
}
