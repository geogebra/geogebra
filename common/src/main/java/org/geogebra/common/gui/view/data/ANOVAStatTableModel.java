package org.geogebra.common.gui.view.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

public class ANOVAStatTableModel extends StatTableModel {
	
	public static ArrayList<double[]> getCategoryData(GeoList geoList) {

		// create an array list of data arrays
		ArrayList<double[]> categoryData = new ArrayList<double[]>();

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
	 * @throws MathException
	 *             if an error occurs computing the Anova stats
	 */
	public static AnovaStats anovaStats(Collection<double[]> categoryData)
			throws IllegalArgumentException, MathException {

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
				throw MathRuntimeException
						.createIllegalArgumentException(
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
			double ss = sumsq.getResult() - sum.getResult() * sum.getResult()
					/ num;
			sswg += ss;
		}
		double sst = totsumsq.getResult() - totsum.getResult()
				* totsum.getResult() / totnum;
		double ssbg = sst - sswg;
		int dfbg = categoryData.size() - 1;
		double msbg = ssbg / dfbg;
		double mswg = sswg / dfwg;
		double F = msbg / mswg;

		FDistribution fdist = new FDistributionImpl(dfbg, dfwg);
		double P = 1.0 - fdist.cumulativeProbability(F);

		return new AnovaStats(dfbg, dfwg, F, P, ssbg, sswg, sst, msbg, mswg);
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
		private double ssbg, sswg, sst;

		/** mean squares */
		private double msbg, mswg;

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

	public static AnovaStats getStats(GeoList dataList) throws IllegalArgumentException, MathException {
		return anovaStats(getCategoryData(dataList));
	}
	public ANOVAStatTableModel(App app, StatTableListener listener) {
		super(app, listener);
	}
	
	@Override
	public String[] getRowNames() {
		String[] names = { getApp().getMenu("BetweenGroups"),
				getApp().getMenu("WithinGroups"), getApp().getMenu("Total"), };
		return names;
	}

	@Override
	public String[] getColumnNames() {

		String[] names = { getApp().getMenu("DegreesOfFreedom.short"),
				getApp().getMenu("SumSquares.short"),
				getApp().getMenu("MeanSquare.short"),
				getApp().getMenu("FStatistic"), getApp().getMenu("PValue"), };

		return names;
	}

	public int getRowCount() {
		return getRowNames().length;
	}

	public int getColumnCount() {
		return getColumnNames().length;
	}
}
