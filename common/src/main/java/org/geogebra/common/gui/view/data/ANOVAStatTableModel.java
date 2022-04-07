package org.geogebra.common.gui.view.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.exception.MathRuntimeException;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class ANOVAStatTableModel extends StatTableModel {

	/**
	 * @param geoList
	 *            tabular data
	 * @return category data
	 */
	public static ArrayList<double[]> getCategoryData(GeoList geoList) {

		// create an array list of data arrays
		ArrayList<double[]> categoryData = new ArrayList<>();

		// load the data arrays from the input GeoList
		GeoList list;
		for (int index = 0; index < geoList.size(); index++) {

			list = (GeoList) geoList.get(index);
			double[] valueArray = new double[list.size()];

			for (int i = 0; i < list.size(); i++) {
				GeoElement geo = list.get(i);
				if (geo instanceof NumberValue) {
					NumberValue num = (NumberValue) geo;
					valueArray[i] = num.getDouble();
				}
			}
			categoryData.add(valueArray);
			// System.out.println(Arrays.toString(valueArray));
		}

		return categoryData;
	}

	/**
	 * Calculates ANOVA stats. (Modified form of method found in Apache Commons
	 * OneWayAnovaImpl)
	 * 
	 * @param categoryData
	 *            <code>Collection</code> of <code>double[]</code> arrays each
	 *            containing data for one category
	 * @return computed AnovaStats
	 * @throws IllegalArgumentException
	 *             if categoryData does not meet preconditions specified in the
	 *             interface definition
	 * @throws ArithmeticException
	 *             if an error occurs computing the Anova stats
	 */
	public static AnovaStats anovaStats(Collection<double[]> categoryData)
			throws IllegalArgumentException, ArithmeticException {

		// check if we have enough categories
		if (categoryData.size() < 2) {
			throw MathRuntimeException.createIllegalArgumentException(
					// LocalizedFormats.TWO_OR_MORE_CATEGORIES_REQUIRED,
					"two or more categories required, got {0}",
					categoryData.size());
		}

		// check if each category has enough data and all is double[]
		for (double[] array : categoryData) {
			if (array.length <= 1) {
				throw MathRuntimeException.createIllegalArgumentException(
						// LocalizedFormats.TWO_OR_MORE_VALUES_IN_CATEGORY_REQUIRED,
						"two or more values required in each category, one has {0}",
						array.length);
			}
		}

		int dfwg = 0;
		double sswg = 0;
		Sum totsum = new Sum();
		SumOfSquares totsumsq = new SumOfSquares();
		int totnum = 0;

		for (double[] data : categoryData) {

			Sum sum = new Sum();
			SumOfSquares sumsq = new SumOfSquares();
			int num = 0;

			for (int i = 0; i < data.length; i++) {
				double val = data[i];

				// within category
				num++;
				sum.increment(val);
				sumsq.increment(val);

				// for all categories
				totnum++;
				totsum.increment(val);
				totsumsq.increment(val);
			}
			dfwg += num - 1;
			double ss = sumsq.getResult()
					- sum.getResult() * sum.getResult() / num;
			sswg += ss;
		}
		double sst = totsumsq.getResult()
				- totsum.getResult() * totsum.getResult() / totnum;
		double ssbg = sst - sswg;
		int dfbg = categoryData.size() - 1;
		double msbg = ssbg / dfbg;
		double mswg = sswg / dfwg;
		double F = msbg / mswg;

		FDistribution fdist = new FDistribution(dfbg, dfwg);
		double P = 1.0 - fdist.cumulativeProbability(F);

		return new AnovaStats(dfbg, dfwg, F, P, ssbg, sswg, sst, msbg, mswg);
	}

	/**
	 * Calculates ANOVA stats. (Modified form of method found in Apache Commons
	 * OneWayAnovaImpl)
	 * 
	 * @param categoryData
	 *            <code>Collection</code> of <code>double[]</code> arrays each
	 *            containing data for one category
	 * @return computed AnovaStats
	 */
	public static AnovaStats anovaStatsSilent(
			Collection<double[]> categoryData) {
		try {
			return anovaStats(categoryData);
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			Log.debug(e);
		}
		return null;
	}

	/**
	 * Convenience class to pass dfbg,dfwg,F values around within AnovaImpl.
	 * (Modified form of class found in OneWayAnovaImpl)
	 */
	public static class AnovaStats {

		/** Degrees of freedom in numerator (between groups). */
		private int dfbg;

		/** Degrees of freedom in denominator (within groups). */
		private int dfwg;

		/** F test statistic. */
		private double F;

		/** sum of squares */
		private double ssbg;
		private double sswg;
		private double sst;

		/** mean squares */
		private double msbg;
		private double mswg;

		/** P value */
		private double P;

		/**
		 * Constructor
		 * 
		 * @param dfbg
		 *            degrees of freedom in numerator (between groups)
		 * @param dfwg
		 *            degrees of freedom in denominator (within groups)
		 * @param F
		 *            statistic
		 */
		public AnovaStats(int dfbg, int dfwg, double F, double P, double ssbg,
				double sswg, double sst, double msbg, double mswg) {
			this.setDfbg(dfbg);
			this.setDfwg(dfwg);
			this.setF(F);
			this.setP(P);
			this.setSsbg(ssbg);
			this.setSswg(sswg);
			this.setSst(sst);
			this.setMsbg(msbg);
			this.setMswg(mswg);

		}

		public int getDfbg() {
			return dfbg;
		}

		public void setDfbg(int dfbg) {
			this.dfbg = dfbg;
		}

		public int getDfwg() {
			return dfwg;
		}

		public void setDfwg(int dfwg) {
			this.dfwg = dfwg;
		}

		public double getSsbg() {
			return ssbg;
		}

		public void setSsbg(double ssbg) {
			this.ssbg = ssbg;
		}

		public double getSswg() {
			return sswg;
		}

		public void setSswg(double sswg) {
			this.sswg = sswg;
		}

		public double getSst() {
			return sst;
		}

		public void setSst(double sst) {
			this.sst = sst;
		}

		public double getMsbg() {
			return msbg;
		}

		public void setMsbg(double msbg) {
			this.msbg = msbg;
		}

		public double getMswg() {
			return mswg;
		}

		public void setMswg(double mswg) {
			this.mswg = mswg;
		}

		public double getF() {
			return F;
		}

		public void setF(double f) {
			F = f;
		}

		public double getP() {
			return P;
		}

		public void setP(double p) {
			P = p;
		}
	}

	/**
	 * Get anovastats ignoring errors
	 * 
	 * @param dataList
	 *            tabular data
	 * @return ANOVA stats
	 */
	public static AnovaStats getStatsSilent(GeoList dataList) {
		return anovaStatsSilent(getCategoryData(dataList));
	}

	/**
	 * @param app
	 *            application
	 * @param listener
	 *            change listener
	 */
	public ANOVAStatTableModel(App app, StatTableListener listener) {
		super(app, listener);
	}

	@Override
	public String[] getRowNames() {
		Localization loc = getApp().getLocalization();
		String[] names = { loc.getMenu("BetweenGroups"),
				loc.getMenu("WithinGroups"), loc.getMenu("Total"), };
		return names;
	}

	@Override
	public String[] getColumnNames() {
		Localization loc = getApp().getLocalization();
		String[] names = { loc.getMenu("DegreesOfFreedom.short"),
				loc.getMenu("SumSquares.short"),
				loc.getMenu("MeanSquare.short"), loc.getMenu("FStatistic"),
				loc.getMenu("PValue"), };

		return names;
	}

	@Override
	public int getRowCount() {
		return getRowNames().length;
	}

	@Override
	public int getColumnCount() {
		return getColumnNames().length;
	}
}
