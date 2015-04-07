package org.geogebra.common.gui.view.probcalculator;

import java.util.HashMap;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.main.App;

/**
 * @author gabor
 * StatisticCalculator common superclass
 */
public abstract class StatisticsCalculator {
	
	protected App app;
	protected Construction cons;
	protected StatisticsCollection sc;
	protected StatisticsCalculatorProcessor statProcessor;
	protected StatisticsCalculatorHTML statHTML;
	protected Kernel kernel;
	
	// =========================================
		// Procedures
		// =========================================

		/***/
		public enum Procedure {
			ZMEAN_TEST, ZMEAN2_TEST, TMEAN_TEST, TMEAN2_TEST, ZPROP_TEST, ZPROP2_TEST, ZMEAN_CI, ZMEAN2_CI, TMEAN_CI, TMEAN2_CI, ZPROP_CI, ZPROP2_CI, GOF_TEST, CHISQ_TEST
		}

		protected Procedure selectedProcedure;

		protected HashMap<String, Procedure> mapNameToProcedure;
		protected HashMap<Procedure, String> mapProcedureToName;

		// =========================================
		// Misc
		// =========================================

		public static final String tail_left = "<";
		public static final String tail_right = ">";
		public static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;

		protected StringBuilder bodyText;

		protected String strMean;
		protected String strSD;
		protected String strSigma;
		protected String strSuccesses;
		protected String strN;
		protected String strPooled;

		protected double[] s1;
		protected double[] s2;
		
		// =========================================
		// Getters/Setters
		// =========================================

		public StatisticsCalculator(App app) {
			this.app = app;
			cons = app.getKernel().getConstruction();
			kernel = cons.getKernel();
			sc = new StatisticsCollection();
			statProcessor = new StatisticsCalculatorProcessor(app, this, sc);
			statHTML = new StatisticsCalculatorHTML(app, this, sc);

			selectedProcedure = Procedure.ZMEAN_TEST;
		}

		public Procedure getSelectedProcedure() {
			return selectedProcedure;
		}
	
	/**
	 * Formats a number string using local format settings.
	 * 
	 * @param x
	 * @return
	 */
	public String format(double x) {
		StringTemplate highPrecision;

		if (kernel.useSignificantFigures) {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					kernel.getPrintFigures(), false);
		} else {
			// override the default decimal place if < 4
			int d = kernel.getPrintDecimals() < 4 ? 4 : cons.getKernel()
					.getPrintDecimals();
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA,
					d, false);
		}
		// get the formatted string
		String result = kernel.format(x, highPrecision);

		return result;
	}
	
	public HashMap<Procedure, String> getMapProcedureToName() {
		return mapProcedureToName;
	}

	public StatisticsCalculatorProcessor getStatProcessor() {
		return statProcessor;
	}

	public StatisticsCollection getStatististicsCollection() {
		return sc;
	}
	
	public abstract void updateResult();

	protected void combolabelsPreprocess() {
		if (mapNameToProcedure == null) {
			mapNameToProcedure = new HashMap<String, Procedure>();
		}
		if (mapProcedureToName == null) {
			mapProcedureToName = new HashMap<Procedure, String>();
		}
	
		mapNameToProcedure.clear();
		mapProcedureToName.clear();
	
		mapNameToProcedure.put(app.getMenu("ZMeanTest"), Procedure.ZMEAN_TEST);
		mapNameToProcedure.put(app.getMenu("ZMeanTest"), Procedure.ZMEAN_TEST);
		mapNameToProcedure.put(app.getMenu("TMeanTest"), Procedure.TMEAN_TEST);
		mapNameToProcedure
				.put(app.getMenu("ZMeanInterval"), Procedure.ZMEAN_CI);
		mapNameToProcedure
				.put(app.getMenu("TMeanInterval"), Procedure.TMEAN_CI);
		mapNameToProcedure.put(app.getMenu("ZTestDifferenceOfMeans"),
				Procedure.ZMEAN2_TEST);
		mapNameToProcedure.put(app.getMenu("TTestDifferenceOfMeans"),
				Procedure.TMEAN2_TEST);
		mapNameToProcedure.put(app.getMenu("ZEstimateDifferenceOfMeans"),
				Procedure.ZMEAN2_CI);
		mapNameToProcedure.put(app.getMenu("TEstimateDifferenceOfMeans"),
				Procedure.TMEAN2_CI);
		mapNameToProcedure.put(app.getMenu("ZProportionTest"),
				Procedure.ZPROP_TEST);
		mapNameToProcedure.put(app.getMenu("ZProportionInterval"),
				Procedure.ZPROP_CI);
		mapNameToProcedure.put(app.getMenu("ZTestDifferenceOfProportions"),
				Procedure.ZPROP2_TEST);
		mapNameToProcedure.put(app.getMenu("ZEstimateDifferenceOfProportions"),
				Procedure.ZPROP2_CI);
		mapNameToProcedure.put(app.getMenu("GoodnessOfFitTest"),
				Procedure.GOF_TEST);
		mapNameToProcedure.put(app.getMenu("ChiSquaredTest"),
				Procedure.CHISQ_TEST);
	
		for (String s : mapNameToProcedure.keySet()) {
			this.mapProcedureToName.put(mapNameToProcedure.get(s), s);
		}
	}

	protected void setLabelStrings() {
	
		strMean = app.getMenu("Mean");
		strSD = app.getMenu("SampleStandardDeviation.short");
		strSigma = app.getMenu("StandardDeviation.short");
		strSuccesses = app.getMenu("Successes");
		strN = app.getMenu("N");
		strPooled = app.getMenu("Pooled");
	}

}
