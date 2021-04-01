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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.StringUtil;

public class AlgoTableToChart extends AlgoElement {

	public enum ChartType {
		PieChart,
		BarChart,
		LineChart
	}

	public static final int CHART_SIZE = 360;

	private final EmbedManager embedManager;

	private final GeoInlineTable table;
	private final ChartType chartType;
	private final int column;

	private final GeoEmbed chart;

	// cache to avoid unnecessary evaluations
	private String oldChartCommand;

	/**
	 * @param cons construction
	 * @param geoInlineTable table
	 */
	public AlgoTableToChart(Construction cons, GeoInlineTable geoInlineTable,
			ChartType chartType, int column) {
		super(cons);
		this.table = geoInlineTable;
		this.chart = new GeoEmbed(cons);
		this.chartType = chartType;
		this.column = column;
		this.embedManager = kernel.getApplication().getEmbedManager();

		setInputOutput();

		initChart();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{
				table,
				new GeoText(cons, chartType.toString()),
				new GeoNumeric(cons, column)
		};
		setOnlyOutput(chart);
		setDependencies();
	}

	private void initChart() {
		if (embedManager == null) {
			return;
		}

		chart.setAppName("classic");
		chart.attr("allowStyleBar", "true");
		chart.attr("perspective", "2");
		chart.initDefaultPosition(kernel.getApplication().getActiveEuclidianView());
		chart.setSize(CHART_SIZE, CHART_SIZE);
		chart.setEmbedId(embedManager.nextID());

		cons.getApplication().invokeLater(() -> {
			switch (chartType) {
			case PieChart:
				embedManager.sendCommand(chart, "ShowAxes(false)");
				embedManager.sendCommand(chart, "ZoomIn(-4, -4, 4, 4)");
				break;
			case LineChart:
				// todo
				break;
			case BarChart:
				// todo
				break;
			}
		});
	}

	@Override
	public void compute() {
		if (embedManager == null) {
			return;
		}

		List<Double> data = table.extractData(column);

		String chartCommand;

		switch (chartType) {
		case PieChart:
			chartCommand = "chart=PieChart({" + StringUtil.join(",", data) + "})";
			break;
		case LineChart:
			chartCommand = "chart=LineChart({" + StringUtil.join(",", data) + "},1)";
			break;
		default:
		case BarChart:
			chartCommand = "chart=BarChart({" + StringUtil.join(",", data) + "},1)";
			break;
		}

		if (!chartCommand.equals(oldChartCommand)) {
			embedManager.sendCommand(chart, chartCommand);
			oldChartCommand = chartCommand;
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.TableToChart;
	}
}
