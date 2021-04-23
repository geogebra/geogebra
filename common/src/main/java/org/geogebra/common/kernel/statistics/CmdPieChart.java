package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;

public class CmdPieChart extends CommandProcessor {
	public CmdPieChart(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) {
		if (c.getArgumentNumber() > 3 || c.getArgumentNumber() < 1) {
			throw argNumErr(c);
		}
		GeoElement[] args = resArgs(c);
		GeoNumberValue radius = null;
		GeoPoint center = null;
		if (args.length > 2) {
			radius = (GeoNumberValue) validate(args[2], args[2].isNumberValue(), c);
		}
		if (args.length > 1) {
			center = (GeoPoint) validate(args[1], args[1] instanceof GeoPoint, c);
		}
		GeoList data = (GeoList) validate(args[0], args[0].isGeoList(), c);
		AlgoPieChart algo =  new AlgoPieChart(cons, data, center, radius);
		algo.getChart().setLabel(c.getLabel());
		return new GeoElement[] {algo.getChart()};
	}

}
