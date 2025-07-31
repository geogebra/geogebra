package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.statistics.AlgoTableToChart.ChartType;

public class CmdTableToChart extends CommandProcessor {
	public CmdTableToChart(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command command, EvalInfo info) {
		if (command.getArgumentNumber() == 4) {
			GeoElement[] args = resArgs(command, info);

			boolean[] ok = new boolean[4];
			if ((ok[0] = args[0] instanceof GeoInlineTable)
					&& (ok[1] = args[1] instanceof GeoText)
					&& (ok[2] = args[2] instanceof GeoNumeric)
					&& (ok[3] = args[3] instanceof GeoNumeric)) {

				GeoInlineTable table = (GeoInlineTable) args[0];
				ChartType chartType = ChartType.valueOf(((GeoText) args[1]).getTextString());
				int column = (int) ((GeoNumeric) args[2]).getValue();
				int embedID = (int) ((GeoNumeric) args[3]).getValue();

				AlgoTableToChart algoTableToChart =
						new AlgoTableToChart(cons, table, chartType, column, embedID);
				GeoElement chart = algoTableToChart.getOutput(0);
				chart.setLabel(command.getLabel());
				algoTableToChart.compute();
				return chart.asArray();
			}

			throw argErr(command, getBadArg(ok, args));
		}

		throw argNumErr(command);
	}
}
