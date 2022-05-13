package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;

import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

/**
 * String builder wrapper for screen reader; avoids double spaces and dots.
 * 
 * @author Zbynek
 */
public class ScreenReaderBuilder {
	public static final int MANY_PRIMES = 4;
	private final Localization loc;
	private StringBuilder sb = new StringBuilder();
	private boolean isMobile = false;
	private TeXAtomSerializer texAtomSerializer;

	/**
	 * Default constructor
	 */
	public  ScreenReaderBuilder(Localization loc) {
		this.loc = loc;
	}

	/**
	 * Constructor
	 * @param isMobile whether the user is on a mobile device or desktop
	 */
	public  ScreenReaderBuilder(Localization loc, boolean isMobile) {
		this.isMobile = isMobile;
		this.loc = loc;
	}

	/**
	 * Append string, make sure . is followed by space.
	 * 
	 * @param o
	 *            string to be appended
	 */
	public void append(String o) {
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '.') {
			sb.append(" "); // ad space after each dot
		}
		sb.append(o);
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	/**
	 * Append space, avoid double space.
	 */
	public void appendSpace() {
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
			sb.append(" ");
		}
	}

	/**
	 * End a sentence. By default this is just a space (to avoid reading
	 * "period") but subclasses may use actual "." e.g. for tests.
	 */
	public void endSentence() {
		appendSpace();
	}

	/**
	 * @return wrapped string builder
	 */
	protected StringBuilder getStringBuilder() {
		return sb;
	}
	
	/**
	 *
	 * @return whether the user is on mobile or desktop
	 */
	public boolean isMobile() {
		return isMobile;
	}

	/**
	 * @param root formula to append
	 */
	public void appendLaTeX(String root) {
		TeXFormula texFormula = new TeXFormula();
		texFormula.setLaTeX(root);
		append(getTexAtomSerializer().serialize(texFormula.root));
	}

	private TeXAtomSerializer getTexAtomSerializer() {
		if (texAtomSerializer == null) {
			texAtomSerializer = new TeXAtomSerializer(new ScreenReaderSerializationAdapter(loc));
		}
		return texAtomSerializer;
	}

	public void appendMenuDefault(String key, String fallback) {
		sb.append(loc.getMenuDefault(key, fallback));
	}

	/**
	 * Appends the label in readable form.
	 * @param label to append.
	 */
	public void appendLabel(String label) {
		if (label == null) {
			return;
		}

		if (label.endsWith("'")) {
			convertPrimes(label, loc, sb);
		} else {
			sb.append(ScreenReader.convertToReadable(label, loc));
		}
	}

	private static void convertPrimes(String label, Localization loc, StringBuilder sb) {
		int apostropheIdx = label.length() - 1;
		int count = 0;
		while (apostropheIdx > 0 && label.charAt(apostropheIdx) == '\'') {
			count++;
			apostropheIdx--;
		}
		int end = label.length() - count;
		sb.append(label, 0, end);

		if (count < MANY_PRIMES) {
			appendNamedPrime(sb, count, loc);
		} else {
			appendManyPrimes(sb, count, loc);
		}
	}

	private static void appendNamedPrime(StringBuilder sb, int count, Localization loc) {
		sb.append(" ");
		if (count == 2) {
			sb.append(loc.getMenu("double"));
			sb.append(" ");
		} else if (count == 3) {
			sb.append(loc.getMenu("triple"));
			sb.append(" ");
		}
		sb.append(getPrime(loc));
	}

	private static void appendManyPrimes(StringBuilder sb, int count, Localization loc) {
		for (int i = 0; i < count; i++) {
			sb.append(" ");
			sb.append(getPrime(loc));
		}
	}

	private static String getPrime(Localization loc) {
		return loc.getMenu("prime");
	}

	protected void appendDegreeIfNeeded(GeoElementND geo, String valueString) {
		append(degreeReplaced(geo, valueString, " "));
	}

	protected void appendLatexDegreeIfNeeded(GeoElement geo, String valueString) {
		appendLaTeX(degreeReplaced(geo, valueString, "\\ "));
		appendSpace();
	}

	private String degreeReplaced(GeoElementND geo, String valueString, String space) {
		String degreeReadable =
				geo.isSingularValue() ? ScreenReader.getDegree(loc) : ScreenReader.getDegrees(loc);

		return containsDegree(valueString)
				? valueString.replace(Unicode.DEGREE_STRING, space + degreeReadable)
				: valueString;
	}

	private boolean containsDegree(String valueString) {
		return valueString.contains(Unicode.DEGREE_STRING)
				|| valueString.contains(Unicode.DEGREE_STRING + "$");
	}
}