package geogebra.common.kernel.algos;

import java.math.BigInteger;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.StringUtil;

public class AlgoToBase extends AlgoElement {

	private NumberValue base;
	private NumberValue number;
	private GeoText result;
	public AlgoToBase(Construction c,String label,NumberValue base,NumberValue number) {
		super(c);
		this.base=base;
		this.number=number;
		result = new GeoText(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]
				{number.toGeoElement(),base.toGeoElement()};
		setOnlyOutput(result);
		setDependencies();
	}
	
	/**
	 * @return result as text
	 */
	public GeoText getResult(){
		return result;
	}

	@Override
	public void compute() {
		if(!number.isDefined()||!base.isDefined()){
			result.setUndefined();
			return;
		}
		int b = (int)base.getDouble();
		if(b<2 || b>36 || !Kernel.isInteger(number.getDouble())){
			result.setUndefined();
			return; 
		}
		BigInteger bi = BigInteger.valueOf((long)number.getDouble());
		result.setTextString(StringUtil.toUpperCase(bi.toString(b)));

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoToBase;
	}

}
