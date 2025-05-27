package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;

public class LatexVectorPrinter implements Printer {

	@Override
	public String print(String xCoord, String yCoord, String zCoord,
			PrintableVector vector, StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		return printLaTeXVector(sb, xCoord, yCoord);
	}

	/**
	 * Prints vector in LaTeX, aligning on dot if possible, right otherwise.
	 * @param sb string builder
	 * @param inputs vector components
	 * @return serialized vector
	 */
	public static String printLaTeXVector(StringBuilder sb, String... inputs) {
		boolean alignOnDecimalPoint = true;
		for (String s : inputs) {
			if (s.indexOf('.') == -1) {
				alignOnDecimalPoint = false;
				break;
			}
		}

		sb.append("\\left( \\begin{align}");
		if (alignOnDecimalPoint) {
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = inputs[i].replace(".", "\\hspace{-0.2em} &.");
			}
		}
		int i = 0;
		for (String input : inputs) {
			if (i > 0) {
				sb.append(" \\\\ ");
			}
			i++;
			sb.append(input);
		}

		sb.append(" \\end{align} \\right)");
		return sb.toString();
	}

}
