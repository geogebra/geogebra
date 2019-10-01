package org.geogebra.common.kernel.geos;

/**
 * String builder wrapper for screen reader; avoids double spaces and dots.
 * 
 * @author Zbynek
 */
public class ScreenReaderBuilder {
	private StringBuilder sb = new StringBuilder();
    private boolean isMobile = false;

    /**
     * Default constructor
     */
    public ScreenReaderBuilder() {

    }

    /**
     * Constructor
     *
     * @param isMobile whether the user is on a mobile device or desktop
     */
    public ScreenReaderBuilder(boolean isMobile) {
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
		if (sb.length() > 1 && sb.charAt(sb.length() - 1) != ' ') {
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
     * @return whether the user is on mobile or desktop
     */
    public boolean isMobile() {
        return isMobile;
    }
}
