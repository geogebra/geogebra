package geogebra.kernel.implicit;


import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.main.Application;
import geogebra.main.MyError;


public class AlgoImplicitPolyFunction extends AlgoElement {
	
	private GeoFunctionNVar function; //input
	private GeoImplicitPoly implicitPoly; //output

	public AlgoImplicitPolyFunction(Construction c, String label,GeoFunctionNVar func) {
		super(c);
		function=func;
		implicitPoly = new GeoImplicitPoly(cons);
		setInputOutput();
		compute();
		implicitPoly.setLabel(label);
	}
	

	@Override
	protected void compute() {
		implicitPoly.setDefined();
		FunctionNVar f=function.getFunction();
		FunctionVariable[] fvars=f.getFunctionVariables();
		if (fvars.length!=2){
			implicitPoly.setUndefined();
			return;
		}
		try{
			ExpressionNode en=f.getExpression().getCopy(kernel);
			Polynomial xVar=new Polynomial(kernel,"x");
			Polynomial yVar=new Polynomial(kernel,"y");
			en.replace(fvars[0], xVar);
			en.replace(fvars[1], yVar);
			Equation equ=new Equation(kernel,en,new MyDouble(kernel));	
			equ.initEquation();
			Polynomial poly =  equ.getNormalForm();
			implicitPoly.setCoeff(poly.getCoeff());
		}catch(MyError e){
			Application.debug(e.getMessage());
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
	public String getClassName() {
		return "AlgoImplicitPolyFunction";
	}
	
	public GeoImplicitPoly getImplicitPoly(){
		return implicitPoly;
	}
	
	public String toString(){
		return getCommandDescription();
	}

}
