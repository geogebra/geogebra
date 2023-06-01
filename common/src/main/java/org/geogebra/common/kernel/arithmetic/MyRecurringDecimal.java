package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class for recurring decimals e.g. 1.23\u03054\u0305
 */
public class MyRecurringDecimal extends MyDouble {

	private final String representation;

	/**
	 * @param kernel Kernel
	 * @param val Value
	 * @param representation Representation of the recurring decimal
	 */
	public MyRecurringDecimal(Kernel kernel, double val, String representation) {
		super(kernel, val);
		this.representation = representation;
	}

	/**
	 * Copy constructor
	 * @param rd MyRecurringDecimal
	 */
	public MyRecurringDecimal(MyRecurringDecimal rd) {
		super(rd);
		this.representation = rd.representation;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return this.representation;
	}

	@Override
	public MyDouble getNumber() {
		return new MyRecurringDecimal(this);
	}

	@Override
	public MyRecurringDecimal deepCopy(Kernel kernel) {
		MyRecurringDecimal ret = new MyRecurringDecimal(this);
		ret.kernel = kernel;
		return ret;
	}

	/**
	 * extension of StringUtil.parseDouble() to cope with unicode digits e.g. Arabic <br>
	 * Enables parsing of recurring decimals
	 * @param str string to be parsed
	 * @param app application for showing errors
	 * @return value
	 */
	public static double parseDouble(Localization app, String str) {
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		for (int i = 0; i < str.length(); i++) {
			int ch = str.charAt(i);
			if (ch <= 0x30 || ch == Unicode.OVERLINE) {
				sb.append(str.charAt(i)); // eg . or \u0305 (overline)
				continue;
			}

			// check roman first (most common)
			else if (ch <= 0x39) {
				ch -= 0x30; // Roman (normal)
			} else if (ch <= 0x100) {
				sb.append(str.charAt(i)); // eg E
				continue;
			} else if (ch <= 0x669) {
				ch -= 0x660; // Arabic-Indic
			} else if (ch == 0x66b) { // Arabic decimal point
				sb.append(".");
				continue;
			} else if (ch <= 0x6f9) {
				ch -= 0x6f0;
			} else if (ch <= 0x96f) {
				ch -= 0x966;
			} else if (ch <= 0x9ef) {
				ch -= 0x9e6;
			} else if (ch <= 0xa6f) {
				ch -= 0xa66;
			} else if (ch <= 0xaef) {
				ch -= 0xae6;
			} else if (ch <= 0xb6f) {
				ch -= 0xb66;
			} else if (ch <= 0xbef) {
				ch -= 0xbe6; // Tamil
			} else if (ch <= 0xc6f) {
				ch -= 0xc66;
			} else if (ch <= 0xcef) {
				ch -= 0xce6;
			} else if (ch <= 0xd6f) {
				ch -= 0xd66;
			} else if (ch <= 0xe59) {
				ch -= 0xe50; // Thai
			} else if (ch <= 0xed9) {
				ch -= 0xed0;
			} else if (ch <= 0xf29) {
				ch -= 0xf20; // Tibetan
			} else if (ch <= 0x1049) {
				ch -= 0x1040; // Mayanmar (Burmese)
			} else if (ch <= 0x17e9) {
				ch -= 0x17e0; // Khmer
			} else if (ch <= 0x1819) {
				ch -= 0x1810; // Mongolian
			} else if (ch <= 0x1b59) {
				ch -= 0x1b50; // Balinese
			} else if (ch <= 0x1bb9) {
				ch -= 0x1bb0; // Sudanese
			} else if (ch <= 0x1c49) {
				ch -= 0x1c40; // Lepcha
			} else if (ch <= 0x1c59) {
				ch -= 0x1c50; // Ol Chiki
			} else if (ch <= 0xa8d9) {
				ch -= 0xa8d0; // Saurashtra
			} else {
				sb.append(str.charAt(i)); // eg -
				continue;
			}
			sb.append(ch);
		}
		try {
			return getValue(sb);
		} catch (NumberFormatException e) {
			// eg try to parse "1.2.3", "1..2"
			throw new MyError(app, MyError.Errors.InvalidInput, str);
		}
	}

	/**
	 * @param sb String to be parsed
	 * @return Value of the recurring decimal as a fraction e.g. 1.3\u0305 -> 12/9 = 4/3
	 * @throws NumberFormatException When trying to parse an invalid double e.g. 1.3.2\u0305
	 */
	private static double getValue(StringBuilder sb) throws NumberFormatException {
		int repeatingDigits = (int) sb.chars().filter(ch -> ch == Unicode.OVERLINE).count();
		int nonRepeatingDigits = sb.substring(sb.indexOf("."), sb.indexOf("\u0305")).length() - 2;
		// Might throw a NumberFormatException (e.g. 1.2.3\u0305)
		double decimalValue = StringUtil.parseDouble(sb.toString().replaceAll("\u0305", ""));

		if (nonRepeatingDigits == 0) {
			return (decimalValue * Math.pow(10, repeatingDigits) - (int) decimalValue)
					/ (Math.pow(10, repeatingDigits) - 1);
		}

		int equation1 = (int) (decimalValue * Math.pow(10, nonRepeatingDigits));
		double equation2 = decimalValue * Math.pow(10, repeatingDigits + nonRepeatingDigits);

		return (equation2 - equation1) / (Math.pow(10, repeatingDigits + nonRepeatingDigits)
				- Math.pow(10, nonRepeatingDigits));
	}
}
