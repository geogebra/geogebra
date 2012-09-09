/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.draw.DrawBarGraph;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.advanced.AlgoUnique;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.statistics.AlgoFrequency;
import geogebra.common.main.App;
import geogebra.common.util.Cloner;

import java.util.ArrayList;

import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.distribution.HypergeometricDistributionImpl;
import org.apache.commons.math.distribution.IntegerDistribution;
import org.apache.commons.math.distribution.PascalDistributionImpl;
import org.apache.commons.math.distribution.PoissonDistributionImpl;
import org.apache.commons.math.distribution.ZipfDistributionImpl;

/**
 * Bar chart algorithm.
 * 
 * @author G. Sturr
 * 
 */
public class AlgoBarChart extends AlgoElement implements DrawInformationAlgo {

	/** Bar chart from expression **/
	public static final int TYPE_BARCHART_EXP = 10;

	/** Bar chart from raw data and given width **/
	public static final int TYPE_BARCHART_RAWDATA = 11;

	/** Bar chart from (values,frequencies) **/
	public static final int TYPE_BARCHART_FREQUENCY_TABLE = 12;

	/** Bar chart from (values,frequencies) with given width **/
	public static final int TYPE_BARCHART_FREQUENCY_TABLE_WIDTH = 13;

	/** Bar chart of a discrete probability distribution **/
	public static final int TYPE_BARCHART_BINOMIAL = 40;
	public static final int TYPE_BARCHART_PASCAL = 41;
	public static final int TYPE_BARCHART_POISSON = 42;
	public static final int TYPE_BARCHART_HYPERGEOMETRIC = 43;
	public static final int TYPE_BARCHART_BERNOULLI = 44;
	public static final int TYPE_BARCHART_ZIPF = 45;
	private int type;

	// largest possible number of rectangles
	private static final int MAX_RECTANGLES = 10000;

	private NumberValue a, b, n, p1, p2, p3; // input
	private GeoList list1, list2; // input
	private GeoElement ageo, bgeo, ngeo, widthGeo, isCumulative, p1geo,
			p2geo, p3geo;

	private int N; // # of intervals
	private double[] yval; // y value (= min) in interval 0 <= i < N
	private double[] leftBorder; // leftBorder (x val) of interval 0 <= i < N
	private double barWidth;

	private double freqMax;
	
	private GeoNumeric sum; // output sum

	/******************************************************
	 * BarChart[<interval start>,<interval stop>, <list of heights>]
	 * 
	 * @param cons
	 * @param label
	 * @param a
	 * @param b
	 * @param list1
	 */
	public AlgoBarChart(Construction cons, String label, NumberValue a,
			NumberValue b, GeoList list1) {
		super(cons);

		type = TYPE_BARCHART_EXP;

		this.a = a;
		this.b = b;
		this.list1 = list1;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setLabel(label);
		sum.setDrawable(true); 
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
		this(cons, list1, width);
		type = TYPE_BARCHART_RAWDATA;
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
		super(cons);

		type = TYPE_BARCHART_RAWDATA;

		this.list1 = list1;
		widthGeo = width.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);

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

		this(cons, list1, list2);
		type = TYPE_BARCHART_FREQUENCY_TABLE;
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
		super(cons);

		type = TYPE_BARCHART_FREQUENCY_TABLE;

		this.list1 = list1;
		this.list2 = list2;

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);

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
			GeoList list2, NumberValue width) {
		super(cons);

		type = TYPE_BARCHART_FREQUENCY_TABLE_WIDTH;

		this.list1 = list1;
		this.list2 = list2;
		widthGeo = width.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);

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
			NumberValue width) {
		super(cons);

		type = TYPE_BARCHART_FREQUENCY_TABLE_WIDTH;

		this.list1 = list1;
		this.list2 = list2;
		widthGeo = width.toGeoElement();

		sum = new GeoNumeric(cons); // output
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
	public AlgoBarChart(Construction cons, String label, NumberValue p1,
			NumberValue p2, NumberValue p3, GeoBoolean isCumulative, int type) {

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
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
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
	protected AlgoBarChart(NumberValue p1, NumberValue p2, NumberValue p3,
			GeoBoolean isCumulative, int type, NumberValue a, NumberValue b,
			double[] vals, double[] borders, int N) {

		super(isCumulative.getConstruction(), false);

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

	private AlgoBarChart(Construction cons, NumberValue a, NumberValue b,
			double[] vals, double[] borders, int N) {
		super(cons, false);

		type = TYPE_BARCHART_EXP;

		this.a = a;
		this.b = b;
		this.yval = vals;
		this.leftBorder = borders;
		this.N = N;

	}

	private AlgoBarChart(Construction cons, GeoNumeric a, double[] vals,
			double[] borders, int N) {
		super(cons, false);
		type = TYPE_BARCHART_RAWDATA;

		this.n = a;
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

	private AlgoBarChart(Construction cons, NumberValue width, double[] vals,
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
	protected void setInputOutput() {

		switch (type) {

		case TYPE_BARCHART_EXP:
			input = new GeoElement[3];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = list1;
			break;
		case TYPE_BARCHART_FREQUENCY_TABLE:
			input = new GeoElement[2];
			input[0] = list1;
			input[1] = list2;
			break;
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			input = new GeoElement[3];
			input[0] = list1;
			input[1] = list2;
			input[2] = widthGeo;
			break;
		case TYPE_BARCHART_RAWDATA:
			input = new GeoElement[2];
			input[0] = list1;
			input[1] = widthGeo;
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
	public Algos getClassName() {
		return Algos.AlgoBarChart;
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
	 * @return type of the bar chart
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return lower bound for sums
	 */
	public NumberValue getA() {
		return a == null ? new MyDouble(kernel, Double.NaN) : a;
	}

	/**
	 * @return upper bound for sums
	 */
	public NumberValue getB() {
		return b == null ? new MyDouble(kernel, Double.NaN) : b;
	}

	/**
	 * @return list of function values
	 */
	public double[] getValues() {
		return yval;
	}

	/**
	 * @return n
	 */
	public GeoNumeric getN() {
		return (GeoNumeric) ngeo;
	}

	/**
	 * number of intervals
	 * 
	 * @return number of intervals
	 */
	public int getIntervals() {
		return N;
	}

	public double getWidth() {
		return barWidth;
	}

	/**
	 * @return the p1
	 */
	public NumberValue getP1() {
		return p1;
	}

	/**
	 * @return the p2
	 */
	public NumberValue getP2() {
		return p2;
	}

	/**
	 * @return the p3
	 */
	public NumberValue getP3() {
		return p3;
	}
	
	/**
	 * @return the type of graph to draw
	 */
	public int getDrawType() {
		if(isCumulative != null && ((GeoBoolean)isCumulative).getBoolean()){
			return DrawBarGraph.TYPE_STEP_GRAPH; 
		}
		return DrawBarGraph.TYPE_VERTICAL_BAR;	
	}

	/**
	 * @return the type of graph to draw
	 */
	public boolean hasPoints() {
		if(this.widthGeo != null && ((GeoNumeric)widthGeo).getDouble() == 0){
			return true; 
		}
		return false;	
	}
	

	// ======================================================
	// Compute
	// ======================================================

	@Override
	public void compute() {

		switch (type) {

		case TYPE_BARCHART_FREQUENCY_TABLE:
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			computeWithFrequency();
			break;

		case TYPE_BARCHART_EXP:
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

		double ySum = 0;

		for (int i = 0; i < N; i++) {
			leftBorder[i] = ad + i * barWidth;

			geo = list1.get(i);
			if (geo.isGeoNumeric())
				yval[i] = ((GeoNumeric) geo).getDouble();
			else
				yval[i] = 0;

			ySum += yval[i];
		}

		// calc area of rectangles
		sum.setValue(ySum * barWidth * N);

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

		AlgoUnique al1 = new AlgoUnique(cons, list1);
		AlgoFrequency al2 = new AlgoFrequency(cons, null, null, list1);

		cons.removeFromConstructionList(al1);
		cons.removeFromConstructionList(al2);

		computeFromValueFrequencyLists(al1.getResult(), al2.getResult(),
				barWidth);

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

		computeFromValueFrequencyLists(list1, list2, barWidth);

	}

	private void computeFromValueFrequencyLists(GeoList list1, GeoList list2,
			double width) {

		if (width < 0) {
			if (list1.size() > 1) {
				double x1 = list1.get(0).evaluateNum().getDouble();
				double x2 = list1.get(1).evaluateNum().getDouble();
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

		int ySum = 0;

		for (int i = 0; i < N; i++) {
			double x = list1.get(i).evaluateNum().getDouble();
			if (!Double.isNaN(x)) {
				leftBorder[i] = x - barWidth / 2;
			} else {
				sum.setUndefined();
				return;
			}

			double y = list2.get(i).evaluateNum().getDouble();
			if (!Double.isNaN(y)) {
				yval[i] = y;
				ySum += y;
			} else {
				sum.setUndefined();
				return;
			}
		}

		// set the sum to the total area
		sum.setValue(ySum * barWidth * yval.length);
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
			App.debug(e.getMessage());
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
		if (list1 != null)
			list1.remove();
		list1 = new GeoList(cons);

		if (list2 != null)
			list2.remove();
		list2 = new GeoList(cons);

		double prob;
		double cumProb = 0;

		for (int i = first; i <= last; i++) {
			list1.add(new GeoNumeric(cons, i));
			prob = dist.probability(i);
			cumProb += prob;
			if (isCumulative != null
					&& ((GeoBoolean) isCumulative).getBoolean())
				list2.add(new GeoNumeric(cons, cumProb));
			else
				list2.add(new GeoNumeric(cons, prob));
		}
		cons.setSuppressLabelCreation(oldSuppress);
	}

	// ======================================================
	// Copy
	// ======================================================

	public DrawInformationAlgo copy() {
		int N = this.getIntervals();
		switch (this.getType()) {
		case TYPE_BARCHART_EXP:
			return new AlgoBarChart(cons,
					(NumberValue) getA().deepCopy(kernel), (NumberValue) getB()
							.deepCopy(kernel), Cloner.clone(getValues()),
					Cloner.clone(getLeftBorder()), N);
		case TYPE_BARCHART_FREQUENCY_TABLE:
			return new AlgoBarChart(kernel.getConstruction(),
					Cloner.clone(getValues()), Cloner.clone(getLeftBorder()), N);
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			return new AlgoBarChart(cons,
					(NumberValue) getA().deepCopy(kernel),
					Cloner.clone(getValues()), Cloner.clone(getLeftBorder()), N);
		default: // TYPE_BARCHART_RAWDATA
			return new AlgoBarChart(cons, (GeoNumeric) getN().copy(),
					Cloner.clone(getValues()), Cloner.clone(getLeftBorder()), N);
		}
	}

}
