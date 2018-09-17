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

	private double degrees, minutes, seconds;
	private boolean hasDegrees, hasMinutes, hasSeconds;

	private StringBuilder sb = new StringBuilder();

	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param degrees
	 *            value for degrees
	 * @param hasDegrees
	 *            whether it has degrees in definition
	 * @param minutes
	 *            value for minutes
	 * @param hasMinutes
	 *            whether it has minutes in definition
	 * @param seconds
	 *            value for seconds
	 * @param hasSeconds
	 *            whether it has seconds in definition
	 */
	public MyDoubleDegreesMinutesSeconds(Kernel kernel, double degrees,
			boolean hasDegrees, double minutes, boolean hasMinutes,
			double seconds, boolean hasSeconds) {
		super(kernel, (degrees + (minutes + seconds / 60.0d) / 60.0d) * Math.PI
				/ 180.0d);
		this.degrees = degrees;
		this.hasDegrees = hasDegrees;
		this.minutes = minutes;
		this.hasMinutes = hasMinutes;
		this.seconds = seconds;
		this.hasSeconds = hasSeconds;
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
		this.degrees = myDouble.degrees;
		this.hasDegrees = myDouble.hasDegrees;
		this.minutes = myDouble.minutes;
		this.hasMinutes = myDouble.hasMinutes;
		this.seconds = myDouble.seconds;
		this.hasSeconds = myDouble.hasSeconds;
		setAngle();
	}

	@Override
	public MyDouble deepCopy(Kernel kernel1) {
		return new MyDoubleDegreesMinutesSeconds(this);
	}

	@Override
	public String toString(StringTemplate tpl) {
		sb.setLength(0);
		if (hasDegrees) {
			sb.append(kernel.format(degrees, tpl));
			sb.append(Unicode.DEGREE_CHAR);
		}
		if (hasMinutes) {
			sb.append(kernel.format(minutes, tpl));
			sb.append(Unicode.MINUTES);
		}
		if (hasSeconds) {
			sb.append(kernel.format(seconds, tpl));
			sb.append(Unicode.SECONDS);
		}
		return sb.toString();
	}

	@Override
	public void set(double val) {
		super.set(val);
		double d = val * 180.0 / Math.PI;
		int dI = (int) d;
		degrees = dI;
		this.hasDegrees = dI != 0;
		double m = (d - degrees) * 60.0;
		int mI = (int) m;
		minutes = mI;
		this.hasMinutes = mI != 0;
		seconds = (m - minutes) * 60.0;
		this.hasSeconds = DoubleUtil.isZero(seconds, Kernel.MAX_PRECISION);
	}

}
