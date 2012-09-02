package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

public class AlgoDivision extends AlgoElement {

	private NumberValue a,b;
	private GeoNumeric num, mod;
	private GeoList result;
	public AlgoDivision(Construction cons, String label, NumberValue dividend,
			NumberValue divisor) {
		super(cons);
		a = dividend;
		b = divisor;
		num = new GeoNumeric(cons);
		mod = new GeoNumeric(cons);
		result = new GeoList(cons);
		result.add(num);
		result.add(mod);
		setInputOutput();
		compute();
		result.setLabel(label);
		
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input = new GeoElement[] {a.toGeoElement(),b.toGeoElement()};

	}

	@Override
	public void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {

    		double numerator = a.getDouble();
    		double denominator = b.getDouble();
    		
    		if (Math.abs(numerator) > MyDouble.LARGEST_INTEGER || Math.abs(denominator) > MyDouble.LARGEST_INTEGER) {
    			result.setUndefined();
    			return;
    		}
    		result.setDefined(true);
    		double m = numerator % Math.abs(denominator);
    		if (m < 0) m += Math.abs(denominator);
    		mod.setValue(m);
    		
    		num.setValue(Math.round((numerator-m)/denominator));
    	} else {
    		result.setUndefined();
    	}

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDivision;
	}

	public GeoElement getResult() {
		return result;
	}

	// TODO Consider locusequability

}
