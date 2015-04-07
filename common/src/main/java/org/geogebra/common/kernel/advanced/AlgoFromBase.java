package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.StringUtil;

public class AlgoFromBase extends AlgoElement {

	private GeoNumberValue base;
	private GeoText number;
	private GeoNumeric result;

	public AlgoFromBase(Construction c, String label, GeoText number,
			GeoNumberValue base) {
		super(c);
		this.base = base;
		this.number = number;
		result = new GeoNumeric(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { number, base.toGeoElement() };
		setOnlyOutput(result);
		setDependencies();
	}

	/**
	 * @return result as text
	 */
	public GeoNumeric getResult() {
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
		double val = 0;

		String in = number.getTextString();
		int pos = in.indexOf('.');
		String s = pos > -1 ? StringUtil.toLowerCase(in.substring(0, pos))
				: StringUtil.toLowerCase(in);
		for (int i = 0; i < s.length(); i++) {
			int last = s.charAt(i) - 0x30;
			if (last > 9)
				last -= 0x30 - 9;
			if (last >= b || last < 0) {
				result.setUndefined();
				return;
			}
			val = val * b + last;
		}
		if (pos > -1) {
			s = StringUtil.toLowerCase(in.substring(pos + 1));
			double power = 1;
			for (int i = 0; i < s.length(); i++) {
				int last = s.charAt(i) - 0x30;
				if (last > 9)
					last -= 0x30 - 9;
				if (last >= b || last < 0) {
					result.setUndefined();
					return;
				}
				power /= b;
				val += power * last;
			}
		}
		result.setValue(val);

	}

	@Override
	public Commands getClassName() {
		return Commands.FromBase;
	}

	// TODO Consider locusequability

}
