package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.main.AppD;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;

public class ANOVATable extends BasicStatTable {
	private static final long serialVersionUID = 1L;

	public ANOVATable(AppD app, DataAnalysisViewD statDialog) {
		super(app, statDialog, -1);
		this.setMinimumSize(this.getPreferredSize());
	}

	@Override
	public String[] getRowNames() {
		String[] names = { app.getMenu("BetweenGroups"),
				app.getMenu("WithinGroups"), app.getMenu("Total"), };
		return names;
	}

	@Override
	public String[] getColumnNames() {

		String[] names = { app.getMenu("DegreesOfFreedom.short"),
				app.getMenu("SumSquares.short"),
				app.getMenu("MeanSquare.short"), app.getMenu("FStatistic"),
				app.getMenu("PValue"), };

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

	@Override
	public void updatePanel() {

		GeoList dataList = statDialog.getController()
				.getDataSelected();
		DefaultTableModel model = statTable.getModel();

		try {
			AnovaStats stats = anovaStats(getCategoryData(dataList));

			// first column, degrees of freedom
			model.setValueAt(statDialog.format(stats.dfbg), 0, 0);
			model.setValueAt(statDialog.format(stats.dfwg), 1, 0);
			model.setValueAt(statDialog.format(stats.dfbg + stats.dfwg), 2, 0);

			// second column, sum of squares
			model.setValueAt(statDialog.format(stats.ssbg), 0, 1);
			model.setValueAt(statDialog.format(stats.sswg), 1, 1);
			model.setValueAt(statDialog.format(stats.sst), 2, 1);

			// third column, mean sum of squares
			model.setValueAt(statDialog.format(stats.msbg), 0, 2);
			model.setValueAt(statDialog.format(stats.mswg), 1, 2);

			// fourth column, F test statistics
			model.setValueAt(statDialog.format(stats.F), 0, 3);

			// fifth column, P value
			model.setValueAt(statDialog.format(stats.P), 0, 4);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repaint();
	}

	private static ArrayList<double[]> getCategoryData(GeoList geoList) {

		// create an array list of data arrays
		ArrayList<double[]> categoryData = new ArrayList<double[]>();

		// load the data arrays from the input GeoList
		GeoList list;
		for (int index = 0; index < geoList.size(); index++) {

			list = (GeoList) geoList.get(index);
			double[] valueArray = new double[list.size()];

			for (int i = 0; i < list.size(); i++) {
				GeoElement geo = list.get(i);
				if (geo.isNumberValue()) {
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
	private static AnovaStats anovaStats(Collection<double[]> categoryData)
			throws IllegalArgumentException, MathException {

		// check if we have enough categories
		if (categoryData.size() < 2) {
			throw MathRuntimeException.createIllegalArgumentException(
					LocalizedFormats.TWO_OR_MORE_CATEGORIES_REQUIRED,
					categoryData.size());
		}

		// check if each category has enough data and all is double[]
		for (double[] array : categoryData) {
			if (array.length <= 1) {
				throw MathRuntimeException
						.createIllegalArgumentException(
								LocalizedFormats.TWO_OR_MORE_VALUES_IN_CATEGORY_REQUIRED,
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
	private static class AnovaStats {

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
		private AnovaStats(int dfbg, int dfwg, double F, double P, double ssbg,
				double sswg, double sst, double msbg, double mswg) {
			this.dfbg = dfbg;
			this.dfwg = dfwg;
			this.F = F;
			this.P = P;
			this.ssbg = ssbg;
			this.sswg = sswg;
			this.sst = sst;
			this.msbg = msbg;
			this.mswg = mswg;

		}
	}

}
