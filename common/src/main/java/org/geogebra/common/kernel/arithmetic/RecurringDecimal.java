package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class for recurring decimals e.g. 1.23\u03054\u0305
 */
public class RecurringDecimal extends MyDouble {

	private RecurringDecimalProperties properties;


	/**
	 * @param kernel Kernel
	 * @param properties of the recurring decimal
	 */
	public RecurringDecimal(Kernel kernel, RecurringDecimalProperties properties) {
		super(kernel, toDouble(properties));
		this.properties = properties;
	}

	private static double toDouble(RecurringDecimalProperties properties) {
		double value = numerator(properties) / (denominator(properties) + 0.0);
		return properties.isPercent() ? 0.01 * value : value;
	}

	public double toDouble() {
		return toDouble(properties);
	}


	private static int denominator(RecurringDecimalProperties properties) {
		return denominator(properties.recurringLength,
				properties.nonRecurringLength);
	}

	private static int numerator(RecurringDecimalProperties properties) {
		return numerator(properties.integerPart, properties.nonRecurringPart,
				properties.recurringPart);
	}


	/**
	 * Copy constructor
	 * @param rd RecurringDecimal
	 */
	public RecurringDecimal(RecurringDecimal rd) {
		super(rd);
		this.properties = rd.properties;
	}

	public static String toFraction(ExpressionNode expression, Kernel kernel, StringTemplate tpl) {
		return Fractions.getResolution(expression, kernel, false).toValueString(tpl);
	}


	public static void asFraction(ExpressionValue[] parts, ExpressionNode expr) {
		Kernel kernel = expr.getKernel();
		RecurringDecimal rd = (RecurringDecimal) expr.unwrap();
		parts[0] = new MyDouble(kernel, numerator(rd.properties));
		parts[1] = new MyDouble(kernel, denominator(rd.properties));
	}

	@Override
	public MyDouble getNumber() {
		return new RecurringDecimal(this);
	}

	@Override
	public RecurringDecimal deepCopy(Kernel kernel) {
		RecurringDecimal ret = new RecurringDecimal(this);
		ret.kernel = kernel;
		return ret;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RecurringDecimal) {
			return super.equals(o)
					&& ((RecurringDecimal) o).properties.equals(this.properties);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * extension of StringUtil.parseDouble() to cope with unicode digits e.g. Arabic <br>
	 * Enables parsing of recurring decimals
	 * @param str string to be parsed
	 * @param app application for showing errors
	 * @return value
	 */
	public static RecurringDecimal parse(Kernel kernel, Localization loc, String str, boolean percent) {
		return new RecurringDecimal(kernel, parseProperties(loc, str, percent));
	}

	private static RecurringDecimalProperties parseProperties(Localization loc, String str,
			boolean percent) {
		StringBuilder sb = serializeDigits(str, true);
		try {
			return RecurringDecimalProperties.parse(sb.toString(), percent);
		} catch (NumberFormatException e) {
			throw new MyError(loc, MyError.Errors.InvalidInput, str);
		}
	}

	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append(properties.integerPart);
		sb.append(".");
		if (properties.nonRecurringPart != null) {
			sb.append(properties.nonRecurringPart);
		}
		if (tpl.isLatex()) {
			sb.append("\\overline{");
			sb.append(properties.recurringPart);
			sb.append("}");
		} else {
			String recurringString = String.valueOf(properties.recurringPart);

			for (int i = 0; i < recurringString.length(); i++) {
				sb.append(recurringString.charAt(i));
				sb.append(Unicode.OVERLINE);

			}
		}
		return sb.toString();
	}

	@Override
	public boolean isRecurringDecimal() {
		return true;
	}

	public String toFractionSting() {
		return numerator(properties)
				+ "/"
				+ denominator(properties);
	}

	static int numerator(int i, Integer a, int p) {
		int pL = lengthOf(p);
		int aL = a != null ? lengthOf(a): 0;
		int A = a != null ? a: 0;
		int iap = (int) (p + A * Math.pow(10, pL) + i * Math.pow(10, pL + aL));
		int ia = (int) (A + i * Math.pow(10, aL));
		return iap - ia;
	}

	static int denominator(int nines, int zeros) {
		int nins = nines == 0 ? 1 : (int) (Math.pow(10, nines) - 1);
		int tens = zeros == 0 ? 1 : (int) (Math.pow(10, zeros));
		return nins * tens;
	}

	private static int lengthOf(int number) {
		if (number < 100000) {
			if (number < 100) {
				if (number < 10) {
					return 1;
				} else {
					return 2;
				}
			} else {
				if (number < 1000) {
					return 3;
				} else {
					if (number < 10000) {
						return 4;
					} else {
						return 5;
					}
				}
			}
		} else {
			if (number < 10000000) {
				if (number < 1000000) {
					return 6;
				} else {
					return 7;
				}
			} else {
				if (number < 100000000) {
					return 8;
				} else {
					if (number < 1000000000) {
						return 9;
					} else {
						return 10;
					}
				}
			}
		}
	}
}