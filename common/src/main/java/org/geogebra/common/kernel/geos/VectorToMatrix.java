package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;

public class VectorToMatrix {
	private final Kernel kernel;

	public VectorToMatrix(Kernel kernel) {
		this.kernel = kernel;
	}

	public String build(StringTemplate template, double... coords) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String separator = "";
		for (double coord: coords) {
			sb.append(separator);
			sb.append(surroundWithBrackets(coord, template));
			separator = ", ";
		}

		sb.append("}");
		return sb.toString();

	}

	private String surroundWithBrackets(double value, StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(kernel.format(value, tpl));
		sb.append("}");
		return sb.toString();
	}

}
