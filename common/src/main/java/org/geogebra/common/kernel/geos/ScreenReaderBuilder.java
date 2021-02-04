package org.geogebra.common.kernel.geos;

import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

/**
 * String builder wrapper for screen reader; avoids double spaces and dots.
 * 
 * @author Zbynek
 */
public class ScreenReaderBuilder {
	private StringBuilder sb = new StringBuilder();
	private boolean isMobile = false;
	private TeXAtomSerializer texAtomSerializer;

	/**
	 * Default constructor
	 */
	public  ScreenReaderBuilder() {

	}

	/**
	 * Constructor
	 * @param isMobile whether the user is on a mobile device or desktop
	 */
	public  ScreenReaderBuilder(boolean isMobile) {
		this.isMobile = isMobile;
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
			texAtomSerializer = new TeXAtomSerializer(null);
		}
		return texAtomSerializer;
	}
}
