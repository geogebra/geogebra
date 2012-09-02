package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;

public class AlgoDivisorsSum extends AlgoElement {

	GeoNumeric result;
	private NumberValue number;
	private AlgoPrimeFactorization factors;
	private GeoList factorList;
	private boolean sum;
	public AlgoDivisorsSum(Construction c,String label,NumberValue number, boolean sum) {
		super(c);
		this.number = number;
		this.sum = sum;
		factors = new AlgoPrimeFactorization(c, number);
		factorList = factors.getResult();
		result = new GeoNumeric(cons);
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
		if(!factorList.isDefined()||!Kernel.isInteger(number.getDouble())){
			result.setUndefined();
			return;
		}
		long res = 1;
		for(int i=0;i<factorList.size();i++){
			GeoList pair = (GeoList) factorList.get(i);
			double exp = ((NumberValue)pair.get(1)).getDouble();
			if(sum){
				double prime = ((NumberValue)pair.get(0)).getDouble();
				App.debug(prime);
				res = res * Math.round((Math.pow(prime, exp+1)-1)/(prime-1.0));
			}
			else{
				res = res * Math.round(exp+1);
			}
		}
		result.setValue(res);
	}
	
	public GeoNumeric getResult(){
		return result;
	}

	@Override
	public Algos getClassName() {
		if(sum)
			return Algos.AlgoDivisorsSum;
		return Algos.AlgoDivisors;
	}

	// TODO Consider locusequability

}
