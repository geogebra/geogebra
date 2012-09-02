package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.advanced.AlgoPrimeFactorization;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;

public class AlgoNextPreviousPrime extends AlgoElement {

	private NumberValue init;
	private GeoNumeric result;
	private boolean next;
	public AlgoNextPreviousPrime(Construction c,String label,NumberValue init,boolean next) {
		super(c);
		this.init = init;
		this.next=next;
		result = new GeoNumeric(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input = new GeoElement[]{init.toGeoElement()};
		setDependencies();
	}
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	@Override
	public void compute() {
		if (!init.isDefined() || init.getDouble()>AlgoPrimeFactorization.LARGEST_INTEGER) {
			result.setUndefined();
			return;
		}
		StringBuilder sb = new StringBuilder(30);
		sb.append(next?"NextPrime(":"PreviousPrime(");
		sb.append(init.toValueString(StringTemplate.maxPrecision));
		sb.append(")");
		try{
		String functionOut = kernel
				.evaluateCachedGeoGebraCAS(sb.toString(),arbconst);
		if (functionOut == null || functionOut.length() == 0) {
			result.setUndefined();
		} else {
			// read result back into function
			result.setValue(Double.parseDouble(functionOut));
		}
		}catch(Throwable e){
			result.setUndefined();
		}
		
	}

	@Override
	public Algos getClassName() {
		return next ? Algos.AlgoNextPrime : Algos.AlgoPreviousPrime;
	}

	public GeoNumeric getResult() {
		return result;
	}

	// TODO Consider locusequability

}
