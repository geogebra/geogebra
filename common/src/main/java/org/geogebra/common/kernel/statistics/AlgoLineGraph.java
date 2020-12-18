package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.Operation;

public class AlgoLineGraph extends AlgoElement {
	private final GeoList xValues;
	private final GeoList yValues;
	private final GeoFunction graph;
	private final MyList xValuesCopy;
	private final MyList yValuesCopy;

	/**
	 * @param c construction
	 * @param xValues x values
	 * @param yValues y values
	 */
	public AlgoLineGraph(Construction c, GeoList xValues, GeoList yValues) {
		super(c);
		this.xValues = xValues;
		this.yValues = yValues;
		this.xValuesCopy = new MyList(c.getKernel());
		this.yValuesCopy = new MyList(c.getKernel());
		FunctionVariable var = new FunctionVariable(kernel);
		ExpressionNode graphExpr = new ExpressionNode(kernel, var, Operation.DATA,
				new MyNumberPair(kernel, xValuesCopy, yValuesCopy));
		this.graph = new GeoFunction(c, new Function(kernel, graphExpr));
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{xValues, yValues};
		setOnlyOutput(graph);
		setDependencies();
	}

	@Override
	public void compute() {
		if (xValues.size() < 2
				|| xValues.size() != yValues.size()
				|| !areXValuesFiniteAndSorted()
				|| !areYValuesFinite()) {
			graph.setUndefined();
			return;
		}
		xValuesCopy.clear();
		yValuesCopy.clear();
		xValues.copyListElements(xValuesCopy);
		yValues.copyListElements(yValuesCopy);
		graph.setDefined(true);
		graph.setInterval(xValues.get(0).evaluateDouble(),
				xValues.get(xValues.size() - 1).evaluateDouble());
	}

	private boolean areXValuesFiniteAndSorted() {
		double last = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < xValues.size(); i++) {
			double value = xValues.get(i).evaluateDouble();
			if (!MyDouble.isFinite(value) || value <= last) {
				return false;
			}
			last = value;
		}
		return true;
	}

	private boolean areYValuesFinite() {
		for (int i = 0; i < yValues.size(); i++) {
			if (!MyDouble.isFinite(yValues.get(i).evaluateDouble())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.LineGraph;
	}
}
