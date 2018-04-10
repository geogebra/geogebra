package org.geogebra.common.main;

public class ExamLogBuilder {
	private StringBuilder sb = new StringBuilder();

	public void addHR() {
		sb.append("<hr>");
	}

	public void addLine(StringBuilder line) {
		sb.append(line);
		sb.append('\n');
	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
