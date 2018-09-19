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
	private Value vDMS;

	public static class Value {
		public int degrees;
		public int minutes;
		public double seconds;
		public boolean needsMinus;

		public void set(double val, double precision) {
			needsMinus = DoubleUtil.isGreater(0, val, precision);
			double d = Math.abs(val * 180.0 / Math.PI);
			degrees = (int) d;
			double m = (d - degrees) * 60.0;
			minutes = (int) m;
			seconds = (m - minutes) * 60.0;
		}

		public void checkMinutesOrSecondsEqual60(double precision) {
			if (DoubleUtil.isEqual(seconds, 60, precision)) {
				minutes++;
				seconds = 0;
			}
			if (minutes >= 60) {
				minutes -= 60;
				degrees++;
			}
		}

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
					sbFormatAngle.append(Unicode.SECONDS);
					sbFormatAngle.append(kernel.format(seconds, tpl));
					sbFormatAngle.append(Unicode.MINUTES);
					sbFormatAngle.append(kernel.format(minutes, tpl));
					sbFormatAngle.append(Unicode.DEGREE_CHAR);
					sbFormatAngle.append(kernel.format(degrees, tpl));
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
					sbFormatAngle.append(kernel.format(degrees, tpl));
					sbFormatAngle.append(Unicode.DEGREE_CHAR);
					sbFormatAngle.append(kernel.format(minutes, tpl));
					sbFormatAngle.append(Unicode.MINUTES);
					sbFormatAngle.append(kernel.format(seconds, tpl));
					sbFormatAngle.append(Unicode.SECONDS);
				}
			}
		}

	}

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

		if (vDMS == null) {
			vDMS = new Value();
		}
		vDMS.set(val, Kernel.MAX_PRECISION);
		vDMS.checkMinutesOrSecondsEqual60(Kernel.MAX_PRECISION);

		if (vDMS.needsMinus) {
			sb.append(Unicode.MINUS);
		}
		if (vDMS.degrees != 0) {
			sb.append(kernel.format(vDMS.degrees, StringTemplate.defaultTemplate));
			sb.append(Unicode.DEGREE_CHAR);
		}
		if (vDMS.minutes != 0) {
			sb.append(kernel.format(vDMS.minutes, StringTemplate.defaultTemplate));
			sb.append(Unicode.MINUTES);
		}
		if (!DoubleUtil.isZero(vDMS.seconds, Kernel.MAX_PRECISION)) {
			sb.append(kernel.format(vDMS.seconds, StringTemplate.defaultTemplate));
			sb.append(Unicode.SECONDS);
		}
		
		if (sb.length() == 0 || (vDMS.needsMinus && sb.length() == 1)) {
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
