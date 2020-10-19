package org.geogebra.common.kernel.statistics;

import java.util.List;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.util.StringUtil;

public class AlgoTableToChart extends AlgoElement {
	private final GeoInlineTable table;
	private final GeoEmbed chart;
	private final EmbedManager embedManager;

	/**
	 * @param cons construction
	 * @param geoInlineTable table
	 */
	public AlgoTableToChart(Construction cons, GeoInlineTable geoInlineTable) {
		super(cons);
		this.table = geoInlineTable;
		this.chart = new GeoEmbed(cons);
		chart.setAppName("classic");
		chart.attr("allowStyleBar", "true");
		chart.initDefaultPosition(kernel.getApplication().getActiveEuclidianView());
		this.embedManager = kernel.getApplication().getEmbedManager();
		if (embedManager != null) {
			chart.setEmbedId(embedManager.nextID());
		}
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{table};
		setOnlyOutput(chart);
		setDependencies();
	}

	@Override
	public void compute() {
		List<Double> data = table.extractData();
		if (embedManager != null) {
			String cmd = "chart=BarChart({" + StringUtil.join(",", data) + "},1)";
			embedManager.sendCommand(chart, cmd);
			embedManager.sendCommand(chart, "SetPerspective(\"G\")");
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.TableToChart;
	}
}
