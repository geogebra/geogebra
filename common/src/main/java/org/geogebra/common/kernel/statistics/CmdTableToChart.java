package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;

public class CmdTableToChart extends CommandProcessor {
	public CmdTableToChart(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command command, EvalInfo info) {
		if (command.getArgumentNumber() == 1) {
			GeoElement[] args = resArgs(command);
			AlgoTableToChart algoTableToChart =
					new AlgoTableToChart(cons, (GeoInlineTable) args[0]);
			GeoElement chart = algoTableToChart.getOutput(0);
			chart.setLabel(command.getLabel());
			algoTableToChart.compute();
			return chart.asArray();
		}
		throw argNumErr(command);
	}
}
