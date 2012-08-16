package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

public class AlgoIsPrime extends AlgoElement {
	private GeoBoolean result;
	private NumberValue number;

	public AlgoIsPrime(Construction cons, String label, NumberValue number) {
		super(cons);
		result = new GeoBoolean(cons);
		this.number = number;
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input = new GeoElement[]{number.toGeoElement()};
		setDependencies();
	}

	@Override
	public void compute() {
		double n = Math.round(number.getDouble());
    	
    	if (n == 1) {
    		result.setValue(false);
    		return;
    	}
    	
    	if (n < 2 || n > AlgoPrimeFactorization.LARGEST_INTEGER) {
    		result.setUndefined();
    		return;
    	}
    	result.setValue(true);
       	for (int i = 2; i <= n / i; i++) {
			if (n % i == 0) {
				result.setValue(false);
				return;
			}
		}
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIsPrime;
	}

	public GeoBoolean getResult() {
		return result;
	}

	// TODO Consider locusequability

}
