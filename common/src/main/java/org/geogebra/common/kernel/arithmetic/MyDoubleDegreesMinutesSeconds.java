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
	private Value vDMS = new Value();

	/**
	 * 
	 * Class for degrees/minutes/seconds
	 *
	 */
	public static class Value {
		private double degrees;
		private double minutes;
		private double seconds;
		private boolean needsMinus;
		private boolean showDegrees;
		private boolean showMinutes;
		private boolean showSeconds;

		/**
		 * set values
		 * 
		 * @param degrees
		 *            degrees
		 * @param showDegrees
		 *            if show degrees when turned into string
		 * @param minutes
		 *            minutes
		 * @param showMinutes
		 *            if show minutes when turned into string
		 * @param seconds
		 *            seconds
		 * @param showSeconds
		 *            if show seconds when turned into string
		 */
		public void set(double degrees, boolean showDegrees, double minutes,
				boolean showMinutes, double seconds, boolean showSeconds) {
			needsMinus = degrees < 0;
			this.degrees = Math.abs(degrees);
			this.minutes = minutes;
			this.seconds = seconds;
			// at least one of degrees/minutes/seconds needs to be shown
			this.showDegrees = showDegrees || (!showMinutes && !showSeconds);
			this.showMinutes = showMinutes;
			this.showSeconds = showSeconds;
		}

		/**
		 * 
		 * @param value
		 *            value
		 */
		public void set(Value value) {
			needsMinus = value.needsMinus;
			this.degrees = value.degrees;
			this.minutes = value.minutes;
			this.seconds = value.seconds;
			this.showDegrees = value.showDegrees;
			this.showMinutes = value.showMinutes;
			this.showSeconds = value.showSeconds;
		}

		/**
		 * set value
		 * 
		 * @param val
		 *            value
		 * @param precision
		 *            precision for rounding
		 * @param unbounded
		 *            if needs to be bounded
		 */
		public void set(double val, double precision, boolean unbounded) {
			showDegrees = true;
			showMinutes = true;
			showSeconds = true;

			double phi = val;
			if (!unbounded) {
				phi = phi % (2 * Math.PI);
				if (phi < 0) {
					phi += 2 * Math.PI;
				}
			}

			needsMinus = DoubleUtil.isGreater(0, phi, precision);
			double d = Math.abs(phi * 180.0 / Math.PI);
			degrees = (int) d;
			double m = (d - degrees) * 60.0;
			int mI = (int) m;
			seconds = (m - mI) * 60.0;

			if (!unbounded) {
				degrees = degrees % 360;
			}

			seconds = DoubleUtil.checkInteger(seconds);

			if (DoubleUtil.isEqual(seconds, 60, precision)) {
				mI++;
				seconds = 0;
			}
			if (mI >= 60) {
				mI -= 60;
				degrees++;
			}
			minutes = mI;
		}

		/**
		 * 
		 * @param sbFormatAngle
		 *            string
		 * @param tpl
		 *            string template
		 * @param kernel
		 *            kernel
		 */
		public void format(StringBuilder sbFormatAngle, StringTemplate tpl,
				Kernel kernel) {
			if (kernel.getLocalization().isRightToLeftDigits(tpl)) {
				if (tpl.hasCASType()) {
					if (needsMinus) {
						sbFormatAngle.append(Unicode.MINUS);
					}
					sbFormatAngle.append("pi/180*(");
					sbFormatAngle.append(kernel.format(seconds, tpl));
					sbFormatAngle.append("/3600+");
					sbFormatAngle.append(kernel.format(minutes, tpl));
					sbFormatAngle.append("/60+");
					sbFormatAngle.append(kernel.format(degrees, tpl));
					sbFormatAngle.append(")");
				} else {
					if (showSeconds) {
						sbFormatAngle.append(Unicode.SECONDS);
						sbFormatAngle.append(kernel.format(seconds, tpl));
					}
					if (showMinutes) {
						sbFormatAngle.append(Unicode.MINUTES);
						sbFormatAngle.append(kernel.format(minutes, tpl));
					}
					if (showDegrees) {
						sbFormatAngle.append(Unicode.DEGREE_CHAR);
						sbFormatAngle.append(kernel.format(degrees, tpl));
					}
					if (needsMinus) {
						sbFormatAngle.append(Unicode.MINUS);
					}
				}
			} else {
				if (tpl.hasCASType()) {
					if (needsMinus) {
						sbFormatAngle.append(Unicode.MINUS);
					}
					sbFormatAngle.append("(");
					sbFormatAngle.append(kernel.format(degrees, tpl));
					sbFormatAngle.append("+");
					sbFormatAngle.append(kernel.format(minutes, tpl));
					sbFormatAngle.append("/60+");
					sbFormatAngle.append(kernel.format(seconds, tpl));
					sbFormatAngle.append("/3600)*pi/180");
				} else {
					if (needsMinus) {
						sbFormatAngle.append(Unicode.MINUS);
					}
					if (showDegrees) {
						sbFormatAngle.append(kernel.format(degrees, tpl));
						sbFormatAngle.append(Unicode.DEGREE_CHAR);
					}
					if (showMinutes) {
						sbFormatAngle.append(kernel.format(minutes, tpl));
						sbFormatAngle.append(Unicode.MINUTES);
					}
					if (showSeconds) {
						sbFormatAngle.append(kernel.format(seconds, tpl));
						sbFormatAngle.append(Unicode.SECONDS);
					}
				}
			}
		}
	}

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
		vDMS.set(degrees, hasDegrees, minutes, hasMinutes, seconds, hasSeconds);
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
		vDMS.set(myDouble.vDMS);
		setAngle();
	}

	@Override
	public MyDouble deepCopy(Kernel kernel1) {
		return new MyDoubleDegreesMinutesSeconds(this);
	}

	@Override
	public String toString(StringTemplate tpl) {
		sb.setLength(0);
		vDMS.format(sb, tpl, kernel);
		return sb.toString();
	}

	@Override
	public void set(double val) {
		super.set(val);
		sb.setLength(0);
		vDMS.set(val, Kernel.MAX_PRECISION, true);
	}

	@Override
	public boolean equals(Object d) {
		return super.equals(d);
	}

	@Override
	public int hashCode() {
		return DoubleUtil.hashCode(getDouble());
	}

}
