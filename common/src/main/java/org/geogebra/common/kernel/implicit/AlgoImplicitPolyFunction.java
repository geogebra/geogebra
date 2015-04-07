package org.geogebra.common.kernel.implicit;


import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;

/**
 * Computes 
 */
public class AlgoImplicitPolyFunction extends AlgoElement {
	
	private GeoFunctionNVar function; //input
	private GeoImplicitPoly implicitPoly; //output

	/**
	 * @param c construction
	 * @param label label
	 * @param func function
	 */
	public AlgoImplicitPolyFunction(Construction c, String label,GeoFunctionNVar func) {
		super(c);
		function=func;
		implicitPoly = new GeoImplicitPoly(cons);
		setInputOutput();
		compute();
		implicitPoly.setLabel(label);
	}
	

	@Override
	public void compute() {
		implicitPoly.setDefined();
		FunctionNVar f=function.getFunction();
		FunctionVariable[] fvars=f.getFunctionVariables();
		if (fvars.length!=2){
			implicitPoly.setUndefined();
			return;
		}
		try{
			ExpressionNode en=f.getExpression().getCopy(kernel);
			/*FunctionVariable xVar=new FunctionVariable(kernel,"x");
			FunctionVariable yVar=new FunctionVariable(kernel,"y");
			en.replace(fvars[0], xVar);
			en.replace(fvars[1], yVar);*/
			Equation equ=new Equation(kernel,en,new MyDouble(kernel));	
			equ.initEquation();
			Polynomial poly =  equ.getNormalForm();
			implicitPoly.setCoeff(poly.getCoeff());
		}catch(MyError e){
			App.debug(e.getMessage());
			implicitPoly.setUndefined();
		}
	}

	@Override
	protected void setInputOutput() {
		input=new GeoElement[]{function};
		setOutputLength(1);        
        setOutput(0,implicitPoly);        
        setDependencies(); // done by AlgoElement
	}

	@Override
	public Commands getClassName() {
		return Commands.ImplicitCurve;
	}
	
	/**
	 * @return resulting polynomial
	 */
	public GeoImplicitPoly getImplicitPoly(){
		return implicitPoly;
	}

	// TODO Consider locusequability

}
