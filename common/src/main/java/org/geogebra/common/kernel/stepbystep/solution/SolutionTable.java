package org.geogebra.common.kernel.stepbystep.solution;

import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.List;

public class SolutionTable {

    private StepExpression[] header;
    private List<TableElement[]> rows;

    public SolutionTable(StepExpression... header) {
        this.header = header;
        rows = new ArrayList<>();
    }

    public void addRow(TableElement... row) {
        rows.add(row);
    }

    public String toLaTeXString(Localization loc) {
        StringBuilder sb = new StringBuilder();

        sb.append("\\begin{tabular}{r | *{");
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

        for (TableElement[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (i != 0) {
                    sb.append(" & ");
                }
                sb.append(row[i].toLaTeXString(loc));
            }
            sb.append(" \\\\ ");
        }

        sb.append("\\end{tabular}");
        return sb.toString();
    }

}
