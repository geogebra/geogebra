package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.util.StringUtil;

import java.math.BigInteger;
/**
 * Allows conversion of numbers to different bases via ToBase[number, base]
 * @author zbynek
 *
 */
public class AlgoToBase extends AlgoElement {

	private NumberValue base;
	private NumberValue number;
	private GeoText result;

	/**
	 * @param c construction
	 * @param label label for output
	 * @param base base
	 * @param number number
	 */
	public AlgoToBase(Construction c, String label, NumberValue number, NumberValue base
			) {
		super(c);
		this.base = base;
		this.number = number;
		result = new GeoText(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { number.toGeoElement(), base.toGeoElement() };
		setOnlyOutput(result);
		setDependencies();
	}

	/**
	 * @return result as text
	 */
	public GeoText getResult() {
		return result;
	}

	@Override
	public void compute() {
		if (!number.isDefined() || !base.isDefined()) {
			result.setUndefined();
			return;
		}
		int b = (int) base.getDouble();
		if (b < 2 || b > 36) {
			result.setUndefined();
			return;
		}
		int digits = kernel.format(1.0 / 9.0, result.getStringTemplate())
				.length() - 2;
		double power = Math.round(Math.pow(b, digits));
		double in = number.getDouble();
		in = in + 1 / power > Math.ceil(in) ? Math.ceil(in) : in;
		BigInteger bi = BigInteger.valueOf((long) in);
		String intPart = StringUtil.toUpperCase(bi.toString(b));
		if (Kernel.isInteger(in)) {
			result.setTextString(intPart);
		} else {

			double decimal = Math.round(power
					* (number.getDouble() - Math.floor(number.getDouble())));
			bi = BigInteger.valueOf((long) decimal);
			String decimalPart = StringUtil.toUpperCase(bi.toString(b));
			result.setTextString(intPart + "." + decimalPart);
		}

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoToBase;
	}

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}

}
