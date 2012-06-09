package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;

public class AlgoFractionalPart extends AlgoElement{

	private GeoNumeric result;
	private NumberValue num;

	public AlgoFractionalPart(Construction cons, String label, NumberValue num) {
		super(cons);
		this.num = num;
		result = new GeoNumeric(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input = new GeoElement[]{num.toGeoElement()};
		setDependencies();
	}

	@Override
	public void compute() {
		if(num.isDefined()){
			double val = num.getDouble();
			result.setValue(val-Math.round(val));
		}
		
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoFractionalPart;
	}

	public GeoNumeric getResult() {
		return result;
	}

}
