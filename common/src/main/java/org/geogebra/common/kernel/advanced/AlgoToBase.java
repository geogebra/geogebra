package org.geogebra.common.kernel.advanced;

import java.math.BigInteger;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.StringUtil;

/**
 * Allows conversion of numbers to different bases via ToBase[number, base]
 * 
 * @author zbynek
 *
 */
public class AlgoToBase extends AlgoElement {

	private NumberValue base;
	private NumberValue number;
	private GeoText result;

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            label for output
	 * @param base
	 *            base
	 * @param number
	 *            number
	 */
	public AlgoToBase(Construction c, String label, NumberValue number,
			NumberValue base) {
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
			StringBuilder sb = new StringBuilder(digits);
			sb.append(intPart);
			sb.append('.');
			for (int i = 0; i < digits - decimalPart.length(); i++) {
				sb.append('0');
			}
			sb.append(decimalPart);
			result.setTextString(sb.toString());
		}

	}

	@Override
	public Commands getClassName() {
		return Commands.ToBase;
	}

}
