/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.distribution.HypergeometricDistributionImpl;
import org.apache.commons.math.distribution.IntegerDistribution;
import org.apache.commons.math.distribution.PascalDistributionImpl;
import org.apache.commons.math.distribution.PoissonDistributionImpl;
import org.apache.commons.math.distribution.ZipfDistributionImpl;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.DrawBarGraph;
import org.geogebra.common.euclidian.draw.DrawBarGraph.DrawType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.statistics.AlgoUsingUniqueAndFrequency;
import org.geogebra.common.util.Cloner;
import org.geogebra.common.util.debug.Log;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Bar chart algorithm.
 * 
 * @author G. Sturr
 * 
 */
public class AlgoBarChart extends AlgoUsingUniqueAndFrequency implements
		DrawInformationAlgo {

	private Map<Integer, HashMap<Integer, Object>> tags = new HashMap<Integer, HashMap<Integer, Object>>();

	/** Bar chart from expression **/
	public static final int TYPE_BARCHART_EXPRESSION = 0;

	/** Bar chart from raw data and given width **/
	public static final int TYPE_BARCHART_RAWDATA = 1;

	/** Bar chart from (values,frequencies) **/
	public static final int TYPE_BARCHART_FREQUENCY_TABLE = 2;

	/** Bar chart from (values,frequencies) with given width **/
	public static final int TYPE_BARCHART_FREQUENCY_TABLE_WIDTH = 3;

	/** Stick graph **/
	public static final int TYPE_STICKGRAPH = 10;

	/** Step graph **/
	public static final int TYPE_STEPGRAPH = 20;

	/** Graph of a discrete probability distribution **/
	public static final int TYPE_BARCHART_BINOMIAL = 40;
	public static final int TYPE_BARCHART_PASCAL = 41;
	public static final int TYPE_BARCHART_POISSON = 42;
	public static final int TYPE_BARCHART_HYPERGEOMETRIC = 43;
	public static final int TYPE_BARCHART_BERNOULLI = 44;
	public static final int TYPE_BARCHART_ZIPF = 45;

	// largest possible number of rectangles
	private static final int MAX_RECTANGLES = 10000;

	// output
	private GeoNumeric sum;

	// input
	private GeoNumberValue a, b, p1, p2, p3;
	private GeoList list1, list2;

	// local fields
	private GeoElement ageo, bgeo, widthGeo, isCumulative, isHorizontal,
			p1geo, p2geo, p3geo, hasJoin, pointType;
	private int type;
	private int N; // # of intervals
	private double[] yval; // y value (= min) in interval 0 <= i < N
	private double[] leftBorder; // leftBorder (x val) of interval 0 <= i < N
	private String[] value; // value string for each bar
	private double barWidth;
	private double freqMax;
	private double dataSize;

	private String toolTipText;

	// flag to determine if result sum measures area or length
	private boolean isAreaSum = true;

	/******************************************************
	 * BarChart[<interval start>,<interval stop>, <list of heights>]
	 * 
	 * @param cons
	 * @param label
	 * @param a
	 * @param b
	 * @param list1
	 */
	public AlgoBarChart(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b, GeoList list1) {
		super(cons);

		type = TYPE_BARCHART_EXPRESSION;

		this.a = a;
		this.b = b;
		this.list1 = list1;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();

		// output
		sum = new GeoNumeric(cons) {
			@Override
			public String getTooltipText(final boolean colored,
					final boolean alwaysOn) {
				return toolTipText;
			}
		};

		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
		sum.setLabel(label);
	}

	/******************************************************
	 * BarChart[<a>,<b>, <list of raw data>, <bar width>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param width
	 */
	public AlgoBarChart(Construction cons, String label, GeoList list1,
			GeoNumeric width) {
		this(cons, list1, null, width, null, null, null, TYPE_BARCHART_RAWDATA);
		sum.setLabel(label);

	}

	public AlgoBarChart(Construction cons, String label, GeoList list1,
			GeoNumeric width, GeoNumeric scale) {
		this(cons, list1, null, width, null, null, null, scale,
				TYPE_BARCHART_RAWDATA);
		sum.setLabel(label);

	}

	/******************************************************
	 * BarChart[<a>,<b>, <list of raw data>, <bar width>] (no label)
	 * 
	 * @param cons
	 * @param list1
	 * @param width
	 */
	public AlgoBarChart(Construction cons, GeoList list1, GeoNumeric width) {
		this(cons, list1, null, width, null, null, null, TYPE_BARCHART_RAWDATA);
	}

	/******************************************************
	 * BarChart[<a>,<b>, <list of values>, <list of frequencies>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoBarChart(Construction cons, String label, GeoList list1,
			GeoList list2) {

		this(cons, list1, list2, null, null, null, null,
				TYPE_BARCHART_FREQUENCY_TABLE);
		sum.setLabel(label);
	}

	/******************************************************
	 * BarChart[<a>,<b>, <list of values>, <list of frequencies>] (no label)
	 * 
	 * @param cons
	 * @param list1
	 * @param list2
	 */
	public AlgoBarChart(Construction cons, GeoList list1, GeoList list2) {

		this(cons, list1, list2, null, null, null, null,
				TYPE_BARCHART_FREQUENCY_TABLE);
	}

	/******************************************************
	 * BarChart[<a>,<b>, <list of values>, <list of frequencies>, <bar width>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param width
	 */
	public AlgoBarChart(Construction cons, String label, GeoList list1,
			GeoList list2, GeoNumberValue width) {

		this(cons, list1, list2, width, null, null, null,
				TYPE_BARCHART_FREQUENCY_TABLE_WIDTH);
		sum.setLabel(label);
	}

	/******************************************************
	 * BarChart[<a>,<b>, <list of values>, <list of frequencies>, <bar width>]
	 * (no label)
	 * 
	 * @param cons
	 * @param list1
	 * @param list2
	 * @param width
	 */
	public AlgoBarChart(Construction cons, GeoList list1, GeoList list2,
			GeoNumberValue width) {

		this(cons, list1, list2, width, null, null, null,
				TYPE_BARCHART_FREQUENCY_TABLE_WIDTH);
	}

	/******************************************************
	 * General constructor with label
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param width
	 * @param isHorizontal
	 * @param join
	 * @param pointType
	 * @param type
	 * 
	 */
	public AlgoBarChart(Construction cons, String label, GeoList list1,
			GeoList list2, GeoNumberValue width, GeoBoolean isHorizontal,
			GeoBoolean join, GeoNumeric pointType, int type) {

		this(cons, list1, list2, width, isHorizontal, join, pointType, type);
		sum.setLabel(label);

	}

	private GeoNumeric scale;

	/******************************************************
	 * General constructor
	 * 
	 * @param cons
	 * @param list1
	 * @param list2
	 * @param width
	 * @param isHorizontal
	 * @param join
	 * @param showStepJump
	 * @param showPoints
	 * @param pointType
	 * @param type
	 */
	public AlgoBarChart(Construction cons, GeoList list1, GeoList list2,
			GeoNumberValue width, GeoBoolean isHorizontal, GeoBoolean join,
			GeoNumeric pointType, int type) {

		this(cons, list1, list2, width, isHorizontal, join, pointType, null,
				type);

	}

	/******************************************************
	 * General constructor
	 * 
	 * @param cons
	 * @param list1
	 * @param list2
	 * @param width
	 * @param isHorizontal
	 * @param join
	 * @param showStepJump
	 * @param showPoints
	 * @param pointType
	 * @param scale
	 * @param type
	 */
	public AlgoBarChart(Construction cons, GeoList list1, GeoList list2,
			GeoNumberValue width, GeoBoolean isHorizontal, GeoBoolean join,
			GeoNumeric pointType, GeoNumeric scale, int type) {
		super(cons);

		this.type = type;

		this.list1 = list1;
		this.list2 = list2;
		if (width != null) {
			widthGeo = width.toGeoElement();
		}
		this.isHorizontal = isHorizontal;
		this.hasJoin = join;
		this.pointType = pointType;

		this.scale = scale;

		sum = new GeoNumeric(cons) {
			@Override
			public String getTooltipText(final boolean colored,
					final boolean alwaysOn) {
				return toolTipText;
			}
		};

		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
	}

	/******************************************************
	 * Discrete distribution bar chart
	 * 
	 * @param cons
	 * @param label
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param isCumulative
	 * @param type
	 */
	public AlgoBarChart(Construction cons, String label, GeoNumberValue p1,
			GeoNumberValue p2, GeoNumberValue p3, GeoBoolean isCumulative,
			int type) {

		super(cons);

		this.type = type;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		p1geo = p1.toGeoElement();
		if (p2 != null)
			p2geo = p2.toGeoElement();
		if (p3 != null)
			p3geo = p3.toGeoElement();
		this.isCumulative = isCumulative;

		sum = new GeoNumeric(cons) {
			@Override
			public String getTooltipText(final boolean colored,
					final boolean alwaysOn) {
				return toolTipText;
			}
		};

		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
		sum.setLabel(label);
		if (yval == null) {
			yval = new double[0];
			leftBorder = new double[0];
		}
	}

	/**
	 * @param cons
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param isCumulative
	 * @param type
	 * @param a
	 * @param b
	 * @param vals
	 * @param borders
	 * @param N
	 */
	protected AlgoBarChart(GeoNumberValue p1, GeoNumberValue p2,
			GeoNumberValue p3,
			GeoBoolean isCumulative, int type, GeoNumberValue a,
			GeoNumberValue b,
			double[] vals, double[] borders, int N) {

		super(p1.getConstruction(), false);

		this.type = type;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		p1geo = p1.toGeoElement();
		if (p2 != null)
			p2geo = p2.toGeoElement();
		if (p3 != null)
			p3geo = p3.toGeoElement();
		this.isCumulative = isCumulative;
		this.a = a;
		this.b = b;
		this.yval = vals;

		this.leftBorder = borders;
		this.N = N;
	}

	// ==================================================
	// Copy constructors
	// ==================================================

	private AlgoBarChart(Construction cons, GeoNumberValue a, GeoNumberValue b,
			double[] vals, double[] borders, int N) {
		super(cons, false);

		type = TYPE_BARCHART_EXPRESSION;

		this.a = a;
		this.b = b;
		this.yval = vals;
		this.leftBorder = borders;
		this.N = N;

	}

	private AlgoBarChart(Construction cons, GeoNumeric width, double[] vals,
			double[] borders, int N) {
		super(cons, false);
		type = TYPE_BARCHART_RAWDATA;

		this.widthGeo = width;
		this.yval = vals;
		this.leftBorder = borders;
		this.N = N;

	}

	private AlgoBarChart(Construction cons, double[] vals, double[] borders,
			int N) {
		super(cons, false);
		type = TYPE_BARCHART_FREQUENCY_TABLE;

		this.yval = vals;
		this.leftBorder = borders;
		this.N = N;
	}

	private AlgoBarChart(Construction cons, GeoNumberValue width, double[] vals,
			double[] borders, int N) {
		super(cons, false);
		type = TYPE_BARCHART_FREQUENCY_TABLE_WIDTH;

		widthGeo = width.toGeoElement();

		this.yval = vals;
		this.leftBorder = borders;
		this.N = N;

	}

	// ======================================================
	// InputOutput
	// ======================================================

	// for AlgoElement
	@Override
	@SuppressFBWarnings({ "SF_SWITCH_FALLTHROUGH",
			"missing break is deliberate" })
	protected void setInputOutput() {

		ArrayList<GeoElement> list = new ArrayList<GeoElement>();

		switch (type) {

		case TYPE_BARCHART_EXPRESSION:

			input = new GeoElement[3];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = list1;
			break;

		case TYPE_BARCHART_RAWDATA:
			createHelperAlgos(list1, scale);

			// fall through
		case TYPE_BARCHART_FREQUENCY_TABLE:
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:

			list.add(list1);
			if (list2 != null) {
				list.add(list2);
			}
			if (widthGeo != null) {
				list.add(widthGeo);
			}

			if (scale != null) {
				list.add(scale);
			}

			input = new GeoElement[list.size()];
			input = list.toArray(input);
			break;

		case TYPE_STICKGRAPH:

			list.add(list1);
			if (list2 != null) {
				list.add(list2);
			}

			if (isHorizontal != null) {
				list.add(isHorizontal);
			}

			input = new GeoElement[list.size()];
			input = list.toArray(input);
			break;

		case TYPE_STEPGRAPH:

			list.add(list1);
			if (list2 != null) {
				list.add(list2);
			}

			if (hasJoin != null) {
				list.add(hasJoin);
			}
			if (pointType != null) {
				list.add(pointType);
			}

			input = new GeoElement[list.size()];
			input = list.toArray(input);
			break;

		case TYPE_BARCHART_BERNOULLI:
		case TYPE_BARCHART_BINOMIAL:
		case TYPE_BARCHART_PASCAL:
		case TYPE_BARCHART_HYPERGEOMETRIC:
		case TYPE_BARCHART_POISSON:
		case TYPE_BARCHART_ZIPF:
			ArrayList<GeoElement> inputList = new ArrayList<GeoElement>();
			inputList.add(p1geo);
			if (p2geo != null)
				inputList.add(p2geo);
			if (p3geo != null)
				inputList.add(p3geo);
			if (isCumulative != null)
				inputList.add(isCumulative);

			input = new GeoElement[inputList.size()];
			input = inputList.toArray(input);
			break;
		}
		setOutputLength(1);
		setOutput(0, sum);
		setDependencies(); // done by AlgoElement

	}

	// ======================================================
	// Getters/Setters
	// ======================================================

	@Override
	public Commands getClassName() {
		return Commands.BarChart;
	}

	/**
	 * @return the resulting sum
	 */
	public GeoNumeric getSum() {
		return sum;
	}

	/**
	 * @return the isCumulative
	 */
	public GeoElement getIsCumulative() {
		return isCumulative;
	}

	/**
	 * @return maximum frequency of a bar chart
	 */
	public double getFreqMax() {

		freqMax = 0.0;
		for (int k = 0; k < yval.length; ++k) {
			freqMax = Math.max(yval[k], freqMax);
		}
		return freqMax;
	}

	/**
	 * @return y values (heights) of a bar chart
	 */
	public double[] getYValue() {
		return yval;
	}

	/**
	 * @return left class borders of a bar chart
	 */
	public double[] getLeftBorder() {
		return leftBorder;
	}

	/**
	 * @return values of a bar chart formatted as string (for frequency tables)
	 */
	public String[] getValue() {
		return value;
	}

	/**
	 * @return type of the bar chart
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return lower bound for sums
	 */
	public GeoNumberValue getA() {
		return a == null ? new GeoNumeric(cons, Double.NaN) : a;
	}

	/**
	 * @return upper bound for sums
	 */
	public GeoNumberValue getB() {
		return b == null ? new GeoNumeric(cons, Double.NaN) : b;
	}

	/**
	 * @return list of function values
	 */
	public double[] getValues() {
		return yval;
	}

	/**
	 * number of intervals
	 * 
	 * @return number of intervals
	 */
	public int getIntervals() {
		return N;
	}

	/**
	 * @return bar width
	 */
	public double getWidth() {
		return barWidth;
	}

	/**
	 * @return discrete graph parameter p1
	 */
	public NumberValue getP1() {
		return p1;
	}

	/**
	 * @return discrete graph parameter p2
	 */
	public GeoNumberValue getP2() {
		return p2;
	}

	/**
	 * @return discrete graph parameter p3
	 */
	public GeoNumberValue getP3() {
		return p3;
	}

	/**
	 * @return the type of graph to draw
	 */
	public DrawType getDrawType() {

		// case 1: step graphs
		if (type == TYPE_STEPGRAPH) {
			if ((hasJoin != null && ((GeoBoolean) hasJoin).getBoolean())) {
				return DrawType.STEP_GRAPH_CONTINUOUS;
			}
			return DrawType.STEP_GRAPH_JUMP;
		}

		// case 2: cumulative discrete probability
		else if (isCumulative != null
				&& ((GeoBoolean) isCumulative).getBoolean()) {
			return DrawType.STEP_GRAPH_CONTINUOUS;

			// case 3: all other types use either horizontal or vertical bars
		} else if (isHorizontal != null
				&& ((GeoBoolean) isHorizontal).getBoolean()) {
			return DrawType.HORIZONTAL_BAR;

		} else {
			return DrawType.VERTICAL_BAR;
		}
	}

	/**
	 * @return true if points are drawn with the graph
	 */
	public boolean hasPoints() {

		return (type == TYPE_STICKGRAPH || type == TYPE_STEPGRAPH);
	}

	/**
	 * @return point style
	 */
	public int getPointType() {

		if (type == TYPE_STICKGRAPH) {
			return DrawBarGraph.POINT_LEFT;
		}
		if (pointType == null)
			return DrawBarGraph.POINT_NONE;

		int p = (int) ((GeoNumeric) pointType).getDouble();
		if (p < -2 || p > 2) {
			p = DrawBarGraph.POINT_NONE;
		}
		return p;

	}

	// ======================================================
	// Compute
	// ======================================================

	@Override
	public void compute() {

		isAreaSum = true;

		switch (type) {

		case TYPE_BARCHART_FREQUENCY_TABLE:
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			computeWithFrequency();
			break;

		case TYPE_STICKGRAPH:
		case TYPE_STEPGRAPH:

			isAreaSum = false;

			if (list1 == null || !list1.isDefined()) {
				sum.setUndefined();
				return;
			}

			if (list1.getGeoElementForPropertiesDialog().isGeoPoint()) {
				computeFromPointList(list1);
			} else {
				if (list2 == null) {
					sum.setUndefined();
					return;
				}
				barWidth = 0.0;
				computeFromValueFrequencyLists(list1, list2);
			}
			break;

		case TYPE_BARCHART_EXPRESSION:
			computeWithExp();
			break;

		case TYPE_BARCHART_RAWDATA:
			computeWithRawData();
			break;

		case TYPE_BARCHART_BINOMIAL:
		case TYPE_BARCHART_POISSON:
		case TYPE_BARCHART_HYPERGEOMETRIC:
		case TYPE_BARCHART_PASCAL:
		case TYPE_BARCHART_ZIPF:

			if (!prepareDistributionLists()) {
				sum.setUndefined();
				return;
			}
			barWidth = -1;
			computeWithFrequency();
			break;

		}
	}

	public void computeWithExp() {

		GeoElement geo; // temporary var

		if (!(ageo.isDefined() && bgeo.isDefined() && list1.isDefined())) {
			sum.setUndefined();
			return;
		}

		N = list1.size();

		double ad = a.getDouble();
		double bd = b.getDouble();

		double ints = list1.size();
		if (ints < 1) {
			sum.setUndefined();
			return;
		} else if (ints > MAX_RECTANGLES) {
			N = MAX_RECTANGLES;
		} else {
			N = (int) Math.round(ints);
		}

		barWidth = (bd - ad) / N;

		if (yval == null || yval.length < N) {
			yval = new double[N];
			leftBorder = new double[N];
		}
		value = new String[N];

		double ySum = 0;

		for (int i = 0; i < N; i++) {
			leftBorder[i] = ad + i * barWidth;

			geo = list1.get(i);
			if (geo.isGeoNumeric())
				yval[i] = ((GeoNumeric) geo).getDouble();
			else
				yval[i] = 0;

			value[i] = kernel.format(ad + i * barWidth / 2,
					StringTemplate.defaultTemplate);

			ySum += yval[i];
		}

		// calc area of rectangles
		sum.setValue(ySum * barWidth);
		dataSize = ySum;

	}

	public void computeWithRawData() {

		if (widthGeo == null || !widthGeo.isDefined()) {
			sum.setUndefined();
			return;
		}
		barWidth = ((GeoNumeric) widthGeo).getDouble();
		if (barWidth < 0) {
			sum.setUndefined();
			return;
		}

		computeFromValueFrequencyLists(algoFreq.getValue(),
				algoFreq.getResult());

	}

	public void computeWithFrequency() {

		if (list1 == null || !list1.isDefined()) {
			sum.setUndefined();
			return;
		}
		if (!list2.isDefined() || list1.size() == 0
				|| list1.size() != list2.size()) {
			sum.setUndefined();
			return;
		}
		if (list1.size() == 0 || list1.size() != list2.size()) {
			sum.setUndefined();
			return;
		}

		if (type == TYPE_BARCHART_FREQUENCY_TABLE_WIDTH) {

			if (widthGeo == null || !widthGeo.isDefined()) {
				sum.setUndefined();
				return;
			}
			barWidth = ((GeoNumeric) widthGeo).getDouble();
			if (barWidth < 0) {
				sum.setUndefined();
				return;
			}

		} else {
			barWidth = -1;
		}

		computeFromValueFrequencyLists(list1, list2);

	}

	private void computeFromValueFrequencyLists(GeoList list1, GeoList list2) {

		if (barWidth < 0) {
			if (list1.size() > 1) {
				double x1, x2;
				if (list1.get(1).isGeoNumeric()) {
					x1 = list1.get(0).evaluateDouble();
					x2 = list1.get(1).evaluateDouble();
				} else {
					// use integers 1,2,3 ... for non-numeric data
					x1 = 1;
					x2 = 2;
				}

				if (!Double.isNaN(x1) && !Double.isNaN(x2)) {
					barWidth = x2 - x1;
				} else {
					sum.setUndefined();
					return;
				}
			} else {
				barWidth = 0.5;
			}
		}

		N = list1.size();
		if (yval == null || yval.length < N) {
			yval = new double[N];
			leftBorder = new double[N];
		}

		value = new String[N];
		for (int i = 0; i < N; i++) {
			value[i] = list1.get(i).toValueString(
					StringTemplate.defaultTemplate);
		}

		double ySum = 0;
		double x = 0;
		if (list2.size() < N) {
			sum.setUndefined();
			return;
		}

		for (int i = 0; i < N; i++) {
			if (list1.get(i).isGeoNumeric()) {
				x = list1.get(i).evaluateDouble();
			} else {
				// use integers 1,2,3 ... to position non-numeric data
				x = i + 1;
			}

			if (!Double.isNaN(x)) {
				leftBorder[i] = x - barWidth / 2;
			} else {
				sum.setUndefined();
				return;
			}

			// frequencies
			double y = list2.get(i).evaluateDouble();
			if (!Double.isNaN(y)) {
				yval[i] = y;
				ySum += y;
			} else {
				sum.setUndefined();
				return;
			}
		}

		// set the sum
		if (isAreaSum) {
			// sum = total area
			sum.setValue(Math.abs(ySum) * barWidth);
		} else {
			// sum = total length
			sum.setValue(Math.abs(ySum));
		}
		dataSize = ySum;

	}

	/**
	 * Computes stick or step graph from a list of points
	 * 
	 * @param list1
	 */
	private void computeFromPointList(GeoList list1) {

		N = list1.size();
		if (yval == null || yval.length < N) {
			yval = new double[N];
			leftBorder = new double[N];
		}

		value = new String[N];

		double ySum = 0;

		for (int i = 0; i < N; i++) {

			GeoElement geo = list1.get(i);
			Coords coords = ((GeoPointND) geo).getCoordsInD3();
			double x = coords.getX();
			if (!Double.isNaN(x)) {
				leftBorder[i] = x - barWidth / 2;
			} else {
				sum.setUndefined();
				return;
			}

			value[i] = kernel.format(x, StringTemplate.defaultTemplate);

			double y = coords.getY();
			if (!Double.isNaN(y)) {
				yval[i] = y;
				ySum += y;
			} else {
				sum.setUndefined();
				return;
			}
		}

		// sum = total length
		sum.setValue(ySum);
	}

	// ======================================================
	// Probability Distributions
	// ======================================================

	/**
	 * Prepares list1 and list2 for use with probability distribution bar charts
	 */
	private boolean prepareDistributionLists() {
		IntegerDistribution dist = null;
		int first = 0, last = 0;
		try {
			// get the distribution and the first, last list values for given
			// distribution type
			switch (type) {
			case TYPE_BARCHART_BINOMIAL:
				if (!(p1geo.isDefined() && p2geo.isDefined()))
					return false;
				int n = (int) Math.round(p1.getDouble());
				double p = p2.getDouble();
				dist = new BinomialDistributionImpl(n, p);
				first = 0;
				last = n;
				break;

			case TYPE_BARCHART_PASCAL:
				if (!(p1geo.isDefined() && p2geo.isDefined()))
					return false;
				n = (int) Math.round(p1.getDouble());
				p = p2.getDouble();
				dist = new PascalDistributionImpl(n, p);

				first = 0;
				last = (int) Math.max(1, (kernel).getXmax() + 1);
				break;
			case TYPE_BARCHART_ZIPF:
				if (!(p1geo.isDefined() && p2geo.isDefined()))
					return false;
				n = (int) Math.round(p1.getDouble());
				p = p2.getDouble();
				dist = new ZipfDistributionImpl(n, p);

				first = 0;
				last = n;
				break;
			case TYPE_BARCHART_POISSON:
				if (!p1geo.isDefined())
					return false;
				double lambda = p1.getDouble();
				dist = new PoissonDistributionImpl(lambda);
				first = 0;
				last = (int) Math.max(1, kernel.getXmax() + 1);
				break;

			case TYPE_BARCHART_HYPERGEOMETRIC:
				if (!(p1geo.isDefined() && p2geo.isDefined() && p3geo
						.isDefined()))
					return false;
				int pop = (int) p1.getDouble();
				int successes = (int) p2.getDouble();
				int sample = (int) p3.getDouble();
				dist = new HypergeometricDistributionImpl(pop, successes,
						sample);
				first = Math.max(0, successes + sample - pop);
				last = Math.min(successes, sample);
				break;
			}

			// load class list and probability list
			loadDistributionLists(first, last, dist);
		}

		catch (Exception e) {
			Log.debug(e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Utility method, creates and loads list1 and list2 with classes and
	 * probabilities for the probability distribution bar charts
	 */
	private void loadDistributionLists(int first, int last,
			IntegerDistribution dist) throws Exception {
		boolean oldSuppress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		if(list1 == null){
			list1= new GeoList(cons);
		}
		else{
			list1.clear();
		}
		if(list2 == null){
			list2= new GeoList(cons);
		}else{
			list2.clear();
		}
		double prob;
		double cumProb = 0;

		for (int i = first; i <= last; i++) {
			list1.addNumber(i, this);
			prob = dist.probability(i);
			cumProb += prob;
			if (isCumulative != null
					&& ((GeoBoolean) isCumulative).getBoolean())
				list2.addNumber(cumProb, this);
			else
				list2.addNumber(prob, this);
		}
		cons.setSuppressLabelCreation(oldSuppress);
	}

	// ======================================================
	// Copy
	// ======================================================

	public DrawInformationAlgo copy() {
		int N = this.getIntervals();
		switch (this.getType()) {
		case TYPE_BARCHART_EXPRESSION:
			return new AlgoBarChart(cons,
					(GeoNumberValue) getA().deepCopy(kernel),
					(GeoNumberValue) getB()
							.deepCopy(kernel), Cloner.clone(getValues()),
					Cloner.clone(getLeftBorder()), N);
		case TYPE_BARCHART_FREQUENCY_TABLE:
			return new AlgoBarChart(kernel.getConstruction(),
					Cloner.clone(getValues()), Cloner.clone(getLeftBorder()), N);
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			return new AlgoBarChart(cons,
					(GeoNumberValue) getA().deepCopy(kernel),
					Cloner.clone(getValues()), Cloner.clone(getLeftBorder()), N);
		default: // TYPE_BARCHART_RAWDATA
			return new AlgoBarChart(cons,
					(GeoNumberValue) widthGeo.deepCopy(kernel),
					Cloner.clone(getValues()), Cloner.clone(getLeftBorder()), N);
		}
	}

	@Override
	public void remove() {
		super.remove();
		if (protectedInput) {
			return;
		}

		removeHelperAlgos();
	}

	public void setBarColor(GColor color, int numBar) {
		if (color == null) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(0);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(0, color);
		} else {
			HashMap<Integer, Object> hm = new HashMap<Integer, Object>();
			hm.put(0, color);
			tags.put(numBar, hm);
		}
	}

	public GColor getBarColor(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null) {
			return (GColor) hm.get(0);
		}
		return null;
	}

	public void setBarAlpha(float alpha, int numBar) {
		if (alpha == -1) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(1);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(1, alpha);
		} else {
			HashMap<Integer, Object> hm = new HashMap<Integer, Object>();
			hm.put(1, alpha);
			tags.put(numBar, hm);
		}
	}

	/**
	 * 
	 * @param numBar
	 *            bar number
	 * @return -1 if not set, otherwise alpha (between 0 and 1)
	 */
	public float getBarAlpha(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null && hm.get(1) != null) {
			return ((Float) hm.get(1)).floatValue();
		}
		return -1;
	}

	public void setBarFillType(FillType fillType, int numBar) {
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(2, fillType);
		} else {
			HashMap<Integer, Object> hm = new HashMap<Integer, Object>();
			hm.put(2, fillType);
			tags.put(numBar, hm);
		}
	}

	public FillType getBarFillType(int numBar) {
		return getBarFillType(numBar, FillType.STANDARD);
	}
	public FillType getBarFillType(int numBar, FillType fallback) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null) {
			if (hm.get(2) == null) {
				return fallback;
			}
			return (FillType) hm.get(2);
		}
		return fallback;
	}

	public void setBarSymbol(String symbol, int numBar) {
		if (symbol == null) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(3);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(3, symbol);
		} else {
			HashMap<Integer, Object> hm = new HashMap<Integer, Object>();
			hm.put(3, symbol);
			tags.put(numBar, hm);
		}
	}

	public String getBarSymbol(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null) {
			return (String) hm.get(3);
		}
		return null;
	}

	public void setBarImage(String image, int numBar) {
		if (image == null) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(4);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(4, image);
		} else {
			HashMap<Integer, Object> hm = new HashMap<Integer, Object>();
			hm.put(4, image);
			tags.put(numBar, hm);
		}
	}

	public String getBarImage(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null) {
			return (String) hm.get(4);
		}
		return null;
	}

	public void setBarHatchDistance(int distance, int numBar) {
		if (distance == -1) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(5);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(5, distance);
		} else {
			HashMap<Integer, Object> hm = new HashMap<Integer, Object>();
			hm.put(5, distance);
			tags.put(numBar, hm);
		}
	}

	public int getBarHatchDistance(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null && hm.get(5) != null) {
			return ((Integer) hm.get(5)).intValue();
		}
		return -1;
	}

	public void setBarHatchAngle(int angle, int numBar) {
		if (angle == -1) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(6);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(6, angle);
		} else {
			HashMap<Integer, Object> hm = new HashMap<Integer, Object>();
			hm.put(6, angle);
			tags.put(numBar, hm);
		}
	}

	public int getBarHatchAngle(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null && hm.get(6) != null) {
			return ((Integer) hm.get(6)).intValue();
		}
		return -1;
	}

	public void barXml(StringBuilder sb) {
		sb.append("\t<tags>\n");
		for (int i = 1; i <= N; i++) {
			if (getBarColor(i) != null) {
				sb.append("\t\t<tag key=\"barColor\"" + " barNumber=\"" + i
						+ "\" value=\"" + GColor.getColorString(getBarColor(i))
						+ "\" />\n");
			}

			float barAlpha = getBarAlpha(i);
			if (barAlpha != -1) {
				sb.append("\t\t<tag key=\"barAlpha\"" + " barNumber=\"" + i
						+ "\" value=\"" + barAlpha + "\" />\n");
			}
			if (getBarHatchDistance(i) != -1) {
				sb.append("\t\t<tag key=\"barHatchDistance\"" + " barNumber=\""
						+ i + "\" value=\"" + getBarHatchDistance(i)
						+ "\" />\n");
			}
			if (getBarHatchAngle(i) != -1) {
				sb.append("\t\t<tag key=\"barHatchAngle\"" + " barNumber=\""
						+ i + "\" value=\"" + getBarHatchAngle(i) + "\" />\n");
			}
			if (getBarFillType(i) != FillType.STANDARD) {
				sb.append("\t\t<tag key=\"barFillType\"" + " barNumber=\"" + i
						+ "\" value=\"" + getBarFillType(i).ordinal()
						+ "\" />\n");
			}
			if (getBarImage(i) != null) {
				sb.append("\t\t<tag key=\"barImage\"" + " barNumber=\"" + i
						+ "\" value=\"" + getBarImage(i) + "\" />\n");
			}
			if (getBarSymbol(i) != null) {
				sb.append("\t\t<tag key=\"barSymbol\"" + " barNumber=\"" + i
						+ "\" value=\"" + getBarSymbol(i) + "\" />\n");
			}
		}
		sb.append("\t</tags>\n");
	}

	public void setToolTipText(int index) {
		int freq = (int) yval[index];
		double percent = 100 * freq / dataSize;
		StringBuilder sb = new StringBuilder();
		sb.append(getLoc().getMenu("Value") + " = " + value[index]);
		sb.append("<br>");
		sb.append(getLoc().getMenu("Count") + " = "
				+ kernel.format(freq, StringTemplate.defaultTemplate));
		sb.append("<br>");
		sb.append(kernel.format(percent, StringTemplate.defaultTemplate) + "%");

		toolTipText = sb.toString();
	}
}
