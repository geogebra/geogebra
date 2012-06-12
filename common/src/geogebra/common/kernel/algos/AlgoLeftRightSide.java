package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;

public class AlgoLeftRightSide extends AlgoElement {

	private GeoElement equation;
	private GeoFunctionNVar side;
	private boolean left;
	private FunctionVariable[] fv;
	public AlgoLeftRightSide(Construction cons, String label,
			GeoElement equation, boolean left) {
		super(cons);
		this.equation = equation;
		this.left = left;
		
		fv = new FunctionVariable[]{new FunctionVariable(kernel,"x"),
				new FunctionVariable(kernel,"y")};
		FunctionNVar f = new FunctionNVar(new ExpressionNode(kernel,fv[0]),fv);
		side = new GeoFunctionNVar(cons,f);
		
		setInputOutput();
		compute();
		side.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(side);
		input = new GeoElement[]{equation};
		setDependencies();
	}

	@Override
	public void compute() {
		if(!equation.isDefined()){
			side.setUndefined();
			return;
		}
		ExpressionNode expr;
		if(left)
			expr = computeLeft();
		else
			expr = computeRight();
		if(expr==null){
			side.setUndefined();
			return;
		}
		FunctionNVar fun = new FunctionNVar(expr,fv);
		side.setFunction(fun);

	}

	private ExpressionNode computeLeft() {
		if(equation instanceof GeoLine){
			GeoLine line = (GeoLine)equation;
			
			switch(line.getMode()){
			case GeoLine.PARAMETRIC:
			case GeoLine.EQUATION_IMPLICIT:
				return
				new ExpressionNode(kernel,fv[0]).multiply(new MyDouble(kernel,line.getX())).plus(
				new ExpressionNode(kernel,fv[1]).multiply(new MyDouble(kernel,line.getY())));
			/** explicit equation */
			case GeoLine.EQUATION_EXPLICIT:
				if(Kernel.isZero(line.getY()))
					return new ExpressionNode(kernel,fv[0]);
				return new ExpressionNode(kernel,fv[1]);
			
			
			/** non-canonical implicit equation */
			case GeoLine.EQUATION_IMPLICIT_NON_CANONICAL:
				
			}
		}

		return null;
	}

	private ExpressionNode computeRight() {
		if(equation instanceof GeoLine){
			GeoLine line = (GeoLine)equation;
			
			switch(line.getMode()){
			case GeoLine.PARAMETRIC:
			case GeoLine.EQUATION_IMPLICIT:
				return new ExpressionNode(kernel,-line.getZ());
			/** explicit equation */
			case GeoLine.EQUATION_EXPLICIT:
				if(Kernel.isZero(line.getY()))
					return new ExpressionNode(kernel,-line.getZ()/line.getX());
				return new ExpressionNode(kernel,fv[0]).multiply(new MyDouble(kernel,-line.getX()/line.getY())).plus(
							new MyDouble(kernel,-line.getZ()/line.getY()));
			/** non-canonical implicit equation */
			case GeoLine.EQUATION_IMPLICIT_NON_CANONICAL:
				
			}
		}
		return null;
	}

	@Override
	public Algos getClassName() {
		return left ? Algos.AlgoLeftSide : Algos.AlgoRightSide;
	}

	public GeoFunctionNVar getResult() {
		return side;
	}

}
