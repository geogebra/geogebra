package org.geogebra.common.kernel.geos;

public class ScreenReaderBuilderDot extends ScreenReaderBuilder {

	@Override
	public void endSentence() {
		StringBuilder sb = getStringBuilder();
		if (sb.length() > 0) {
			int idx = sb.length() - 1;
			if (sb.charAt(idx) == ' ') {
				sb.setLength(idx--);
			}
			if (idx > 0 && sb.charAt(idx) != '.') {
				sb.append(".");
			}
		}
	}
}
