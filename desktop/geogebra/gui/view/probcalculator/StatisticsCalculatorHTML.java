package geogebra.gui.view.probcalculator;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.util.Unicode;
import geogebra.main.AppD;

/**
 * @author G. Sturr
 * 
 */
public class StatisticsCalculatorHTML {

	private AppD app;
	private Construction cons;
	private Kernel kernel;
	private StatisticsCalculator statCalc;
	private StatisticsCollection sc;

	private String strSample1, strSample2, strMean, strSD, strSigma,
			strSuccesses, strN, strSE, strDF, strUpper, strLower, strInterval,
			strP, strChiSq, strZ, strT, strPooled;

	private final static String newline = "<br>";
	private final static String eq = " = ";

	/*********************************************
	 * Constructs StatisticsCalculatorHTML
	 * 
	 * @param app
	 * @param statCalc
	 * @param sc
	 */
	public StatisticsCalculatorHTML(AppD app, StatisticsCalculator statCalc,
			StatisticsCollection sc) {

		this.app = app;
		cons = app.getKernel().getConstruction();
		kernel = cons.getKernel();
		this.statCalc = statCalc;
		this.sc = sc;

		this.setLabelStrings();
	}

	
	/**
	 * Formats a number string using local format settings.
	 * 
	 * @param x
	 * @return
	 */
	public String format(double x) {
		return statCalc.format(x);
	}

	private void setLabelStrings() {

		strSample1 = app.getMenu("Sample1");
		strSample2 = app.getMenu("Sample2");

		strMean = app.getMenu("Mean");
		strSD = app.getMenu("SampleStandardDeviation.short");
		strSigma = app.getMenu("StandardDeviation.short");
		strSuccesses = app.getMenu("Successes");
		strN = app.getMenu("N");
		strSE = app.getMenu("StandardError.short");
		strDF = app.getMenu("DegreesOfFreedom.short");
		strP = app.getMenu("PValue");
		strZ = app.getMenu("ZStatistic");
		strT = app.getMenu("TStatistic");
		
		strUpper = app.getMenu("UpperLimit");
		strLower = app.getMenu("LowerLimit");
		strInterval = app.getMenu("Interval");
		strPooled = app.getMenu("Pooled");
		
		strChiSq = "\u03a7²";
		
	}

	public StringBuilder getStatString() {

		StringBuilder sb = new StringBuilder("");
		sb.append(statCalc.getMapProcedureToName().get(
				statCalc.getSelectedProcedure()));
		sb.append(newline);
		sb.append(newline);

		switch (statCalc.getSelectedProcedure()) {

		case ZMEAN_TEST:

			String[][] zTestTable = { { strMean, format(sc.mean) },
					{ strSigma, format(sc.sd) }, { strSE, format(sc.se) },
					{ strZ, format(sc.testStat) }, { strP, format(sc.P) } };

			sb.append(htmlTable(zTestTable, true));

			break;

		case TMEAN_TEST:

			String[][] tTestTable = { { strMean, format(sc.mean) },
					{ strSD, format(sc.sd) }, { strSE, format(sc.se) },
					{ strT, format(sc.testStat) }, { strP, format(sc.P) } };

			sb.append(htmlTable(tTestTable, true));

			break;

		case ZMEAN_CI:

			String[][] zCITable = {
					{ strMean, format(sc.mean) },
					{ strSigma, format(sc.sd) },
					{ strSE, format(sc.se) },
					{ strLower, format(sc.lower) },
					{ strUpper, format(sc.upper) },
					{
							strInterval,
							format(sc.mean) + "&nbsp;" + Unicode.PLUSMINUS
									+ "&nbsp;" + format(sc.me) } };

			sb.append(htmlTable(zCITable, true));

			break;

		case TMEAN_CI:

			String[][] tCITable = { { strMean, format(sc.mean) },
					{ strSD, format(sc.sd) }, { strSE, format(sc.se) },
					{ strLower, format(sc.lower) },
					{ strUpper, format(sc.upper) },
					{ strInterval, getInterval() } };

			sb.append(htmlTable(tCITable, true));

			break;

		case ZMEAN2_TEST:

			String[][] zTest2SampleTable = {
					{ "&nbsp;", strSample1, strSample2 },
					{ strMean, format(sc.mean), format(sc.mean2) },
					{ strSigma, format(sc.sd), format(sc.sd2) },
					{ strN, format(sc.n), format(sc.n2) },
					{ strSE, format(sc.se) }, { strZ, format(sc.testStat) },
					{ strP, format(sc.P) } };

			sb.append(htmlTable(zTest2SampleTable, true));

			break;

		case ZMEAN2_CI:

			String[][] zCI2SampleTable = {
					{ "&nbsp;", strSample1, strSample2 },
					{ strMean, format(sc.mean), format(sc.mean2) },
					{ strSigma, format(sc.sd), format(sc.sd2) },
					{ strN, format(sc.n), format(sc.n2) },
					{ strSE, format(sc.se) }, { strLower, format(sc.lower) },
					{ strUpper, format(sc.upper) },
					{ strInterval, getInterval() } };

			sb.append(htmlTable(zCI2SampleTable, true));

			break;

		case TMEAN2_TEST:

			String[][] tTest2SampleTable = {
					{ "&nbsp;", strSample1, strSample2 },
					{ strMean, format(sc.mean), format(sc.mean2) },
					{ strSigma, format(sc.sd), format(sc.sd2) },
					{ strN, format(sc.n), format(sc.n2) },
					{ strSE, format(sc.se) }, { strT, format(sc.testStat) },
					{ strP, format(sc.P) } };

			sb.append(htmlTable(tTest2SampleTable, true));

			break;

		case TMEAN2_CI:

			String[][] tCI2SampleTable = {
					{ "&nbsp;", strSample1, strSample2 },
					{ strMean, format(sc.mean), format(sc.mean2) },
					{ strSigma, format(sc.sd), format(sc.sd2) },
					{ strN, format(sc.n), format(sc.n2) },
					{ strSE, format(sc.se) }, { strLower, format(sc.lower) },
					{ strUpper, format(sc.upper) },
					{ strInterval, getInterval() }, { strPooled, isPooled() } };

			sb.append(htmlTable(tCI2SampleTable, true));

			break;

		case ZPROP_TEST:

			String[][] zPropTestTable = { { strSuccesses, format(sc.count) },
					{ strN, format(sc.n) }, { strZ, format(sc.testStat) },
					{ strP, format(sc.P) } };

			sb.append(htmlTable(zPropTestTable, true));

			break;

		case ZPROP_CI:

			String[][] zPropEstTable = { { strSuccesses, format(sc.count) },
					{ strN, format(sc.n) }, { strSE, format(sc.se) },
					{ strLower, format(sc.lower) },
					{ strUpper, format(sc.upper) },
					{ strInterval, getInterval() } };

			sb.append(htmlTable(zPropEstTable, true));

			break;

		case ZPROP2_TEST:
			String[][] zProp2TestSampleTable = {
					{ "&nbsp;", strSample1, strSample2 },
					{ strSuccesses, format(sc.count), format(sc.count2) },
					{ strN, format(sc.n), format(sc.n2) },
					{ strSE, format(sc.se) }, { strZ, format(sc.testStat) },
					{ strP, format(sc.P) } };

			sb.append(htmlTable(zProp2TestSampleTable, true));

			break;

		case ZPROP2_CI:

			String[][] zProp2CISampleTable = {
					{ "&nbsp;", strSample1, strSample2 },
					{ strSuccesses, format(sc.count), format(sc.count2) },
					{ strN, format(sc.n), format(sc.n2) },
					{ strSE, format(sc.se) }, { strLower, format(sc.lower) },
					{ strUpper, format(sc.upper) },
					{ strInterval, getInterval() } };

			sb.append(htmlTable(zProp2CISampleTable, true));

			break;

		case CHISQ_TEST:
		case GOF_TEST:
			
			String[][] chiSqTestTable = { { strDF, format(sc.df) },
					{ strChiSq, format(sc.testStat) },
					{ strP, format(sc.P) } };

			sb.append(htmlTable(chiSqTestTable, true));

		}

		// System.out.println(sb.toString());
		return sb;

	}

	private String isPooled() {
		return sc.pooled ? app.getPlain("True") : app.getPlain("False");
	}

	private String getInterval() {
		return format(sc.mean) + "&nbsp;" + Unicode.PLUSMINUS + "&nbsp;"
				+ format(sc.me);
	}

	private static StringBuilder htmlTable(String[][] s, boolean isRowBased) {

		StringBuilder sb = new StringBuilder();

		// sb.append("<table border=1 cellspacing=0 cellpadding=3> ");
		sb.append("<table> ");
		if (isRowBased) {
			for (int r = 0; r < s.length; r++) {
				sb.append("<tr>");
				for (int c = 0; c < s[r].length; c++) {
					sb.append("</td>" + s[r][c] + "</td>");
				}
				sb.append("</tr>");
			}
		} else {
			for (int r = 0; r < s[0].length; r++) {
				sb.append("<tr>");
				for (int c = 0; c < s.length; c++) {
					sb.append("</td>" + s[c][r] + "</td>");
				}
				sb.append("</tr>");
			}
		}
		sb.append("</table> ");
		return sb;

	}

}
