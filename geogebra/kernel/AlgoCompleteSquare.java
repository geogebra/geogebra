package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.cas.AlgoCoefficients;
import geogebra.main.Application;

public class AlgoCompleteSquare extends AlgoElement {
	
	private GeoFunction f,square;
	private FunctionVariable fv;
	private MyDouble a,h,k; //a(x-h)^2+k
	private int lastDeg;
	private AlgoCoefficients algoCoef;
	public AlgoCompleteSquare(Construction cons,String label,GeoFunction f){
		super(cons);
		this.f=f;
		a = new MyDouble(kernel);
		h = new MyDouble(kernel);
		k = new MyDouble(kernel);
		
		fv = new FunctionVariable(kernel);	
		ExpressionNode squareE = new ExpressionNode(kernel,fv,ExpressionNode.MINUS,h)
					.power(new MyDouble(kernel,2)).multiply(a).plus(k);
		Function squareF = new Function(squareE,fv);
		squareF.initFunction();
		square = new GeoFunction(cons);		
		setInputOutput();
		square.setFunction(squareF);		
		compute();		
		lastDeg = 0;
		square.setLabel(label);
		
	}
	@Override
	protected void compute() {
		int degInt;
		GeoList coefs = null;
		fv.setVarString(f.getVarString());
		//px^2+qx+r; p+q+r=s;
		double r = f.evaluate(0);
		double s = f.evaluate(1);		
		double p = 0.5*(s+f.evaluate(-1))-r;
		double q = s-p-r;
		boolean isQuadratic = !f.isGeoFunctionConditional();
		double[] checkpoints = {1000,-1000,Math.PI,Math.E};
		for(int i=0;i<checkpoints.length;i++){
			double x=checkpoints[i];
			if(!Kernel.isZero(p*x*x+q*x+r- f.evaluate(x))){
				Application.debug(p+","+q+","+r+","+(p*x*x+q*x+r- f.evaluate(x)));
				isQuadratic = false;
			}
		}
		if(!isQuadratic){
			if(algoCoef==null) {
				algoCoef = new AlgoCoefficients(cons,f);
				cons.removeFromConstructionList(algoCoef);
			}
			coefs = algoCoef.getResult();

			degInt = coefs.size()-1;
			isQuadratic = coefs.isDefined() && coefs.get(0).isDefined();
			for(int i=1;i<degInt;i++){
				if(2*i != degInt && !Kernel.isZero(((GeoNumeric)coefs.get(i)).getDouble())){
					isQuadratic = false;
				}
			r = ((GeoNumeric)coefs.get(0)).getDouble();
			q = ((GeoNumeric)coefs.get(degInt/2)).getDouble();
			p = ((GeoNumeric)coefs.get(degInt)).getDouble();	
			}
		}else{
			degInt = 2;
		}		
		
		if(degInt %2 == 1 || degInt < 2 || !isQuadratic){
			square.setUndefined();
			return;
		}
		
		if(lastDeg != degInt){
			ExpressionNode squareE;
			if(degInt == 2)
			 squareE = new ExpressionNode(kernel,fv,ExpressionNode.MINUS,h)
			.power(new MyDouble(kernel,2)).multiply(a).plus(k);
			else
			squareE = new ExpressionNode(kernel,
						new ExpressionNode(kernel,fv,ExpressionNode.POWER,new MyDouble(kernel,degInt/2))
					,ExpressionNode.MINUS,h)
					.power(new MyDouble(kernel,2)).multiply(a).plus(k);
			square.getFunction().setExpression(squareE);
		}
		lastDeg = degInt;
		fv.setVarString(f.getVarString());				
		
		//if one is undefined, others are as well
		square.setDefined(!Double.isNaN(r));
		a.set(p);
		h.set(-q/(2*p));
		k.set(r-q*q/(p*4));		
				
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0]=f;
		setOutputLength(1);
		setOutput(0,square);
		setDependencies();

	}
	public GeoFunction getResult(){
		return square;
	}

	@Override
	public String getClassName() {
		return "AlgoCompleteSquare";
	}

}
