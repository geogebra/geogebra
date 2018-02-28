package org.geogebra.common.kernel.stepbystep.solution;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SolutionTable extends SolutionStep {

    private StepExpression[] header;
    private List<TableElement[]> rows;

    public SolutionTable(StepExpression... header) {
        this.header = header;
        rows = new ArrayList<>();
    }

    public void addRow(TableElement... row) {
        rows.add(row);
    }

    public static SolutionTable createSignTable(StepVariable variable, List<StepExpression> roots,
                                                Set<StepExpression> expressions) {
        StepExpression[] header = new StepExpression[1 + roots.size()];
        header[0] = variable;
        for (int i = 0; i < roots.size(); i++) {
            header[i + 1] = roots.get(i);
        }

        SolutionTable table = new SolutionTable(header);

        for (StepExpression expression : expressions) {
            List<TableElement> row = new ArrayList<>();
            row.add(expression);
            for (int i = 0; i < roots.size(); i++) {
                double value = expression.getValueAt(variable, roots.get(i).getValue());
                if (StepNode.isEqual(value, 0)) {
                    row.add(TableElementType.ZERO);
                } else if (value < 0) {
                    row.add(TableElementType.NEGATIVE);
                } else {
                    row.add(TableElementType.POSITIVE);
                }

                if (i == roots.size() - 1) {
                    break;
                }

                if (StepHelper.isNegative(expression, roots.get(i), roots.get(i + 1), variable)) {
                    row.add(TableElementType.NEGATIVE);
                } else {
                    row.add(TableElementType.POSITIVE);
                }
            }
            table.addRow(row.toArray(new TableElement[0]));
        }

        return table;
    }

    @Override
    public String getDefault(Localization loc) {
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

    @Override
    public String getDetailed(Localization loc) {
        return getDefault(loc);
    }
}
