package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
/**
 * Algorithm for NextPrime and PreviousPrime
 */
public class AlgoNextPreviousPrime extends AlgoElement {

	private NumberValue init;
	private GeoNumeric result;
	private boolean next;
	/**
	 * @param cons construction
	 * @param label label for output
	 * @param init where to start seeking from primes
	 * @param next true for NextPrime, false for PreviousPrime
	 */
	public AlgoNextPreviousPrime(Construction cons,String label,NumberValue init,boolean next) {
		super(cons);
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
	public Commands getClassName() {
    	return next ? Commands.NextPrime : Commands.PreviousPrime;
    } 

	/**
	 * @return resulting prime
	 */
	public GeoNumeric getResult() {
		return result;
	}
	
	//Locus equability makes no sense here

}
