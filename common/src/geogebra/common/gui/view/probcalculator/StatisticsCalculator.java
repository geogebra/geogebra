package geogebra.common.gui.view.probcalculator;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.main.App;

import java.util.HashMap;

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

}
