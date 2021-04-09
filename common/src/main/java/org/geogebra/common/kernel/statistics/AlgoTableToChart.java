package org.geogebra.common.kernel.statistics;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
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
		LineGraph
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
			case LineGraph:
				embedManager.setGrid(chart, EuclidianView.GRID_CARTESIAN);
			case BarChart:
				embedManager.sendCommand(chart, "ShowAxes(true)");
				break;
			default:
				break;
			}
		});
	}

	@Override
	public void compute() {
		if (embedManager == null) {
			return;
		}

		String chartCommand;
		double minX = 0, minY = 0, maxX = 0, maxY = 0;

		switch (chartType) {
		case PieChart:
			List<Double> pieData = table.extractData(column);
			chartCommand = "chart=PieChart({" + StringUtil.join(",", pieData) + "})";
			break;
		case LineGraph:
			List<Double>[] lineData = table.extractTwoColumnData(column);
			chartCommand = "chart=LineGraph({"
					+ StringUtil.join(",", lineData[0]) + "},{"
					+ StringUtil.join(",", lineData[1]) + "})";
			minX = Collections.min(lineData[0]) - 1;
			maxX = Collections.max(lineData[0]) + 1;
			minY = Collections.min(lineData[1]) - 1;
			maxY = Collections.max(lineData[1]) + 1;
			break;
		default:
		case BarChart:
			List<Double>[] barData = table.extractTwoColumnData(column);
			chartCommand = "chart=BarChart({"
					+ StringUtil.join(",", barData[0]) + "},{"
					+ StringUtil.join(",", barData[1]) + "}, 1)";
			minX = Collections.min(barData[0]) - 1.5;
			maxX = Collections.max(barData[0]) + 1.5;
			minY = 0;
			maxY = Collections.max(barData[1]) + 1;
			break;
		}

		if (!chartCommand.equals(oldChartCommand)) {
			embedManager.sendCommand(chart, chartCommand);
			oldChartCommand = chartCommand;

			if (chartType == ChartType.BarChart || chartType == ChartType.LineGraph) {
				int axisDistance = 32; // padding between the axis and the edge of the object
				String newMinX = "(" + axisDistance + " * " + maxX + " - x(Corner(5)) * "
						+ minX + ") / (" + axisDistance + " - x(Corner(5)))";
				String newMinY = "(" + axisDistance + " * " + maxY + " - y(Corner(5)) * "
						+ minY + ") / (" + axisDistance + " - y(Corner(5)))";

				embedManager.sendCommand(chart, "ShowLabel(chart, false)");
				embedManager.sendCommand(chart, "ZoomIn(" + newMinX + ", " + newMinY
						+ ", " + maxX + ", " + maxY + ")");
				embedManager.setGraphAxis(chart, 0, minY);
				embedManager.setGraphAxis(chart, 1, minX);
			}

			switch (chartType) {
			case BarChart:
				if (kernel.getApplication().isMebis()) {
					embedManager.sendCommand(chart, "SetColor(chart, \"#B500A8D5\")");
				} else {
					embedManager.sendCommand(chart, "SetColor(chart, \"#B56557D2\")");
				}
				break;
			case LineGraph:
				if (kernel.getApplication().isMebis()) {
					embedManager.sendCommand(chart, "SetColor(chart, \"#00A8D5\")");
				} else {
					embedManager.sendCommand(chart, "SetColor(chart, \"#6557D2\")");
				}
				embedManager.sendCommand(chart, "SetLineThickness(chart, 8)");
				break;
			default:
				break;
			}
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.TableToChart;
	}
}
