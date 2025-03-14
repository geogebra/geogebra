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
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

public class AlgoTableToChart extends AlgoElement {

	public enum ChartType {
		PieChart,
		BarChart,
		LineGraph,
		Histogram
	}

	public static final int CHART_SIZE = 360;

	private final EmbedManager embedManager;

	private final GeoInlineTable table;
	private final ChartType chartType;
	private final int column;
	private final int embedID;

	private final GeoEmbed chart;

	// cache to avoid unnecessary evaluations
	private String oldChartCommand;

	/**
	 * @param cons construction
	 * @param geoInlineTable table
	 */
	public AlgoTableToChart(Construction cons, GeoInlineTable geoInlineTable,
			ChartType chartType, int column, int embedID) {
		super(cons);
		this.table = geoInlineTable;
		this.chart = new GeoEmbed(cons);
		this.chartType = chartType;
		this.column = column;
		this.embedID = embedID;
		this.embedManager = kernel.getApplication().getEmbedManager();

		chart.setAppName("classic");
		chart.attr("preloadModules", "");
		chart.attr("allowStyleBar", "true");
		chart.attr("perspective", "2");
		chart.initDefaultPosition(kernel.getApplication().getActiveEuclidianView());
		chart.setSize(CHART_SIZE, CHART_SIZE);
		chart.setEmbedId(embedID);

		setInputOutput();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{
				table,
				new GeoText(cons, chartType.toString()),
				new GeoNumeric(cons, column),
				new GeoNumeric(cons, embedID)
		};
		setOnlyOutput(chart);
		setDependencies();
	}

	/**
	 * computes and updates the chart
	 */
	public void updateChartData() {
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
			minX = min(lineData[0]) - 1;
			maxX = max(lineData[0]) + 1;
			minY = min(lineData[1]) - 1;
			maxY = max(lineData[1]) + 1;
			break;
		case BarChart:
		default:
			List<Double>[] barData = table.extractTwoColumnData(column);
			chartCommand = "chart=BarChart({"
					+ StringUtil.join(",", barData[0]) + "},{"
					+ StringUtil.join(",", barData[1]) + "}, 1)";
			minX = min(barData[0]) - 1.5;
			maxX = max(barData[0]) + 1.5;
			maxY = max(barData[1]) + 1;
			break;
		}

		if (chartCommand.equals(oldChartCommand)) {
			return;
		}

		embedManager.sendCommand(chart, chartCommand);
		oldChartCommand = chartCommand;

		if (chartType == ChartType.BarChart || chartType == ChartType.LineGraph) {
			int axisDistance = 32; // padding between the axis and the edge of the object
			App app = embedManager.getEmbedApp(chart);
			if (app != null) {
				EuclidianView ev = app.getActiveEuclidianView();
				double newXmin = (axisDistance * maxX - ev.getWidth() * minX) / (axisDistance - ev
						.getWidth());
				double newYmin = (axisDistance * maxY - ev.getHeight() * minY) / (axisDistance - ev
						.getHeight());
				app.getActiveEuclidianView().setRealWorldCoordSystem(newXmin, maxX, newYmin, maxY);
				embedManager.setGraphAxis(chart, 0, minY);
				embedManager.setGraphAxis(chart, 1, minX);
			}
		}
	}

	private double max(List<Double> column) {
		return column.isEmpty() ? Double.NaN : Collections.max(column);
	}

	private double min(List<Double> column) {
		return column.isEmpty() ? Double.NaN : Collections.min(column);
	}

	@Override
	public void compute() {
		updateChartData();
	}

	@Override
	public GetCommand getClassName() {
		return Commands.TableToChart;
	}
}
