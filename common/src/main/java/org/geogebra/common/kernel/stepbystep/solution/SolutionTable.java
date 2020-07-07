package org.geogebra.common.kernel.stepbystep.solution;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.Localization;

public class SolutionTable extends SolutionStep {

	public final HasLaTeX[] header;
	public final List<List<HasLaTeX>> rows;

	public SolutionTable(HasLaTeX... header) {
		this.header = header;
		rows = new ArrayList<>();
	}

	@Override
	public SolutionStepType getType() {
		return SolutionStepType.TABLE;
	}

	@Override
	public List<TextElement> getDefault(Localization loc) {
		StringBuilder sb = new StringBuilder();

		sb.append("$\\begin{tabular}{r | *{");
		sb.append(header.length * 2 - 3);
		sb.append("}{c}}");

		for (int i = 0; i < header.length; i++) {
			if (i > 0) {
				sb.append(" & ");
			}
			if (i > 1) {
				sb.append(" \\; & ");
			}
			sb.append(header[i].toLaTeXString(loc));
		}
		sb.append(" \\\\ ");
		sb.append(" \\hline ");

		for (List<HasLaTeX> row : rows) {
			for (int i = 0; i < row.size(); i++) {
				if (i != 0) {
					sb.append(" & ");
				}
				sb.append(row.get(i).toLaTeXString(loc));
			}
			sb.append(" \\\\ ");
		}

		sb.append("\\end{tabular}$");

		List<TextElement> result = new ArrayList<>();
		result.add(new TextElement(sb.toString(), sb.toString()));
		return result;
	}

	@Override
	public List<TextElement> getDetailed(Localization loc) {
		return getDefault(loc);
	}
}
