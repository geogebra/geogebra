package org.geogebra.common.kernel.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.MyMath2;

public class AlgoNpR extends AlgoElement {
	private GeoNumeric result;
	private GeoNumberValue num1, num2;

	public AlgoNpR(Construction cons, String label, GeoNumberValue num1,
			GeoNumberValue num2) {
		super(cons);
		this.num1 = num1;
		this.num2 = num2;
		result = new GeoNumeric(cons);
		setInputOutput();
		compute();
		result.setLabel(label);

	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input = new GeoElement[] { num1.toGeoElement(), num2.toGeoElement() };
		setDependencies();

	}

	@Override
	public void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			double nCr = NpR(num1.getDouble(), num2.getDouble());
			result.setValue(nCr);
		} else
			result.setUndefined();
	}

	@Override
	public Commands getClassName() {
		return Commands.nPr;
	}

	public GeoNumeric getResult() {
		return result;
	}

	private static double NpR(double n, double r) {
		double INFINITY = Double.POSITIVE_INFINITY;
		try {
			if (n == 0d && r == 0d)
				return 1d;
			if (n < 1d || r < 0d || n < r)
				return 0d;
			if (Math.floor(n) != n || Math.floor(r) != r)
				return 0d;

			double ncr = NpRLog(n, r);
			if (ncr == INFINITY)
				return INFINITY; // check to stop needless slow calculations

			// NpRLog is not exact for some values
			// (determined by trial and error) eg 17P16
			if (n <= 16)
				return ncr;
			// if (r<2.8+Math.exp((250-n)/100) && n<59000) return ncr;

			// NpRBig is more accurate but slower
			// (but cannot be exact if the answer has more than about 16
			// significant digits)
			return NpRBig(n, r);
		} catch (Exception e) {
			return INFINITY;
		}
	}

	private static double NpRBig(double n, double r) {
		BigInteger ncr = BigInteger.ONE, nn, nr;
		// nn=BigInteger.valueOf((long)n);
		// rr=BigInteger.valueOf((long)r);

		// need a long-winded conversion in case n>10^18
		Double nnn = new Double(n);
		Double rrr = new Double(n - r);
		nn = (new BigDecimal(nnn.toString())).toBigInteger();
		nr = (new BigDecimal(rrr.toString())).toBigInteger();
		nr = nr.add(BigInteger.ONE);

		while (nr.compareTo(nn) <= 0) {
			ncr = ncr.multiply(nr);

			nr = nr.add(BigInteger.ONE);

		}
		return ncr.doubleValue();
	}

	private static double NpRLog(double n, double r) {
		// exact for n<=37
		// also if r<2.8+Math.exp((250-n)/100) && n<59000
		// eg NpR2(38,19) is wrong

		return Math.floor(0.5 + Math.exp(MyMath2.logGamma(n + 1d)
				- MyMath2.logGamma(n - r + 1)));

	}

	

}
