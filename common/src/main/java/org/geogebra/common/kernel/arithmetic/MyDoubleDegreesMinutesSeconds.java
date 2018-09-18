package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.DoubleUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class for angles in degrees/minutes/seconds
 *
 */
public class MyDoubleDegreesMinutesSeconds extends MyDouble {

	private StringBuilder sb = new StringBuilder();

	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param value
	 *            value
	 * @param degrees
	 *            value for degrees
	 * @param minutes
	 *            value for minutes
	 * @param seconds
	 *            value for seconds
	 */
	public MyDoubleDegreesMinutesSeconds(Kernel kernel, double value,
			String degrees, String minutes, String seconds) {
		super(kernel, value);
		if (degrees != null) {
			sb.append(degrees);
			sb.append(Unicode.DEGREE_CHAR);
		}
		if (minutes != null) {
			sb.append(minutes);
			sb.append(Unicode.MINUTES);
		}
		if (seconds != null) {
			sb.append(seconds);
			sb.append(Unicode.SECONDS);
		}
		setAngle();
	}

	/**
	 * 
	 * @param myDouble
	 *            another angles in degrees/minutes/seconds
	 */
	public MyDoubleDegreesMinutesSeconds(
			MyDoubleDegreesMinutesSeconds myDouble) {
		super(myDouble);
		sb.append(myDouble.sb);
		setAngle();
	}

	@Override
	public MyDouble deepCopy(Kernel kernel1) {
		return new MyDoubleDegreesMinutesSeconds(this);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return sb.toString();
	}

	@Override
	public void set(double val) {
		super.set(val);
		sb.setLength(0);

		double d = val * 180.0 / Math.PI;
		int dI = (int) d;
		if (dI != 0) {
			sb.append(kernel.format(dI, StringTemplate.defaultTemplate));
			sb.append(Unicode.DEGREE_CHAR);
		}

		double m = (d - dI) * 60.0;
		int mI = (int) m;
		if (mI != 0) {
			sb.append(kernel.format(mI, StringTemplate.defaultTemplate));
			sb.append(Unicode.MINUTES);
		}

		double seconds = (m - mI) * 60.0;
		if (!DoubleUtil.isZero(seconds, Kernel.MAX_PRECISION)) {
			sb.append(kernel.format(seconds, StringTemplate.defaultTemplate));
			sb.append(Unicode.SECONDS);
		}
		
		if (sb.length() == 0) {
			sb.append("0");
			sb.append(Unicode.DEGREE_CHAR);
		}
	}

	@Override
	public boolean equals(Object d) {
		return super.equals(d);
	}

	@Override
	public int hashCode() {
		return Double.hashCode(getDouble());
	}

}
