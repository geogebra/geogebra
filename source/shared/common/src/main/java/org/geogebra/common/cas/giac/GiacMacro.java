package org.geogebra.common.cas.giac;

public class GiacMacro {
	public static String when(String condition, String whenTrue, String whenFalse) {
		return "when(" + condition + "," + whenTrue + "," + whenFalse + ")";
	}

	public static String last(String... commands) {
		StringBuilder sb = new StringBuilder("[");
		for (String command: commands) {
			sb.append(command);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append("][-1]");
		return sb.toString();
	}
}
