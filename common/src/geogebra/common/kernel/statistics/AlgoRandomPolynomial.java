package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
/**
 * Algorithm for random polynomials with given degree and coefficient range
 * @author Zbynek Konecny
 *
 */
public class AlgoRandomPolynomial extends AlgoElement {

	private NumberValue degree,min,max;
	private GeoFunction polynomial;
	private Function f;
	private FunctionVariable fv;
	/**
	 * 
	 * @param cons construction
	 * @param label label for output
	 * @param degree maximal degree
	 * @param min
	 * @param max
	 */
	public AlgoRandomPolynomial(Construction cons, String label,
			NumberValue degree, NumberValue min, NumberValue max) {
		super(cons);
		this.degree = degree;
		this.min = min;
		this.max = max;
		fv = new FunctionVariable(kernel);
		f = new Function(new ExpressionNode(kernel,fv),fv);
		polynomial = new GeoFunction(cons);
		setInputOutput();
		compute();
		polynomial.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(polynomial);
		input = new GeoElement[]{degree.toGeoElement(),min.toGeoElement(),max.toGeoElement()};
		setDependencies();
	}

	@Override
	public void compute() {
		//cover undefined cases
		if(!degree.isDefined() || !min.isDefined() || !max.isDefined() || degree.getDouble()<0){
			polynomial.setUndefined();
			return;
		}
		int lower = (int)Math.ceil(min.getDouble());
		int upper = (int)Math.floor(max.getDouble());
		if(lower>upper || (lower ==0 && upper == 0)){
			polynomial.setUndefined();
			return;
		}
		//input is sane, we can do the computation
		int deg = (int)Math.floor(degree.getDouble());
		ExpressionNode varExpr = new ExpressionNode(kernel,fv);
		ExpressionNode newExpr = randomCoef(deg !=0);
		for(int i=1;i<=deg;i++){
			newExpr = varExpr.power(new MyDouble(kernel,i)).multiply(randomCoef(i!=deg)).plus(newExpr);
		}
		f.setExpression(newExpr, fv);
		polynomial.setFunction(f);
			

	}

	private ExpressionNode randomCoef(boolean acceptZero) {
		if(acceptZero)	
		return new ExpressionNode(kernel,app.getRandomIntegerBetween(min.getDouble(),
				max.getDouble()));
		int rnd = app.getRandomIntegerBetween(min.getDouble(),
				max.getDouble()-1);
		return new ExpressionNode(kernel,rnd>=0 ? rnd +1:rnd);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoRandomPolynomial;
	}
	/**
	 * @return resulting polynomial
	 */
	public GeoFunction getResult(){
		return polynomial;
	}

	// TODO Consider locusequability

}
