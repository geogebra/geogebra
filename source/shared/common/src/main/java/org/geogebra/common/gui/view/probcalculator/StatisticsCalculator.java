/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.awt.annotations.HasNativeSubclass;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.TextObject;
import org.geogebra.common.util.debug.Log;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * StatisticCalculator common superclass
 *
 * @author gabor
 */
@HasNativeSubclass
public abstract class StatisticsCalculator {
	/** statistics data and settings */
	protected final StatisticsCollection sc;
	/** processor computes results from data */
	protected StatisticsCalculatorProcessor statProcessor;
	/** HTML output utility */
	protected StatisticsCalculatorHTML statHTML;
	/** kernel */
	protected Kernel kernel;
	/** SD field */
	protected TextObject fldSigma;
	/** null hypothesis */
	protected TextObject fldNullHyp;
	/** confidence level */
	protected TextObject fldConfLevel;
	protected TextObject[] fldSampleStat1;
	protected TextObject[] fldSampleStat2;

	// =========================================
	// Misc
	// =========================================

	protected StringBuilder bodyText;

	protected String strMean;
	protected String strSD;
	protected String strSigma;
	protected String strSuccesses;
	protected String strN;

	protected double[] s1;
	protected double[] s2;
	protected Localization loc;
	protected App app;

	// =========================================
	// Getters/Setters
	// =========================================

	/**
	 * @param app
	 *            application
	 */
	@SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
			justification = "false positive, used in web and desktop")
	public StatisticsCalculator(App app) {
		this.loc = app.getLocalization();
		this.app = app;
		kernel = app.getKernel();
		sc = app.getSettings().getProbCalcSettings().getCollection();
		statProcessor = new StatisticsCalculatorProcessor(app, this, sc);
		statHTML = new StatisticsCalculatorHTML(app, this, sc);
	}

	public Procedure getSelectedProcedure() {
		return sc.getSelectedProcedure();
	}

	/**
	 * Formats a number string using local format settings.
	 * 
	 * @param x
	 *            number
	 * @return formatted number
	 */
	public String format(double x) {
		StringTemplate highPrecision;

		if (kernel.useSignificantFigures) {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					kernel.getPrintFigures(), false);
		} else {
			// override the default decimal place if < 4
			int d = kernel.getPrintDecimals() < 4 ? 4
					: kernel.getPrintDecimals();
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, d,
					false);
		}
		// get the formatted string
		String result = kernel.format(x, highPrecision);

		return result;
	}

	public StatisticsCalculatorProcessor getStatProcessor() {
		return statProcessor;
	}

	public StatisticsCollection getStatisticsCollection() {
		return sc;
	}

	/**
	 * @param userInitiated
	 *            whether it was triggered by enter
	 */
	public final void updateResult(boolean userInitiated) {
		updateStatisticCollection(userInitiated);
		recompute(userInitiated);
	}

	/**
	 * Recompute the results
	 * 
	 * @param userInitiated
	 *            whether it was triggered by enter
	 */
	public void recompute(boolean userInitiated) {
		statProcessor.doCalculate();

		bodyText = new StringBuilder();
		statHTML.getStatString(bodyText);
		updateResultText(bodyText.toString());

		// prevent auto scrolling
		resetCaret();
	}

	/**
	 * Update collection from GUI
	 */
	final protected void updateStatisticCollection(boolean userInitiated) {
		ErrorHandler errorHandler = userInitiated ? app.getDefaultErrorHandler()
				: ErrorHelper.silent();
		try {
			sc.level = parseStringData(fldConfLevel.getText(), errorHandler);
			sc.sd = parseStringData(fldSigma.getText(), errorHandler);
			sc.nullHyp = parseStringData(fldNullHyp.getText(), errorHandler);

			sc.setTail(getSelectedTail());

			for (int i = 0; i < s1.length; i++) {
				s1[i] = parseStringData(fldSampleStat1[i].getText(),
						errorHandler);
			}
			for (int i = 0; i < s2.length; i++) {
				s2[i] = parseStringData(fldSampleStat2[i].getText(),
						errorHandler);
			}

			updateCollectionProcedure();
			if (userInitiated) {
				setSampleFieldText();
			}

		} catch (NumberFormatException e) {
			Log.debug(e);
		}

	}

	final protected void setSampleFieldText() {
		for (int i = 0; i < 3; i++) {
			removeActionListener(fldSampleStat1[i]);
			removeActionListener(fldSampleStat2[i]);
			fldSampleStat1[i].setText("");
			fldSampleStat2[i].setText("");
		}

		switch (sc.getSelectedProcedure()) {
		default:
			// do nothing
			break;
		case ZMEAN_TEST:
		case ZMEAN_CI:
		case TMEAN_TEST:
		case TMEAN_CI:
			setFieldText(fldSampleStat1[0], format(sc.mean));
			setFieldText(fldSampleStat1[1], format(sc.sd));
			setFieldText(fldSampleStat1[2], format(sc.n));
			break;
		case ZMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_TEST:
		case TMEAN2_CI:
			setFieldText(fldSampleStat1[0], format(sc.mean));
			setFieldText(fldSampleStat1[1], format(sc.sd));
			setFieldText(fldSampleStat1[2], format(sc.n));
			setFieldText(fldSampleStat2[0], format(sc.mean2));
			setFieldText(fldSampleStat2[1], format(sc.sd2));
			setFieldText(fldSampleStat2[2], format(sc.n2));
			break;
		case ZPROP_TEST:
		case ZPROP_CI:
			setFieldText(fldSampleStat1[0], format(sc.count));
			setFieldText(fldSampleStat1[1], format(sc.n));
			break;
		case ZPROP2_TEST:
		case ZPROP2_CI:
			setFieldText(fldSampleStat1[0], format(sc.count));
			setFieldText(fldSampleStat1[1], format(sc.n));
			setFieldText(fldSampleStat2[0], format(sc.count2));
			setFieldText(fldSampleStat2[1], format(sc.n2));
			break;
		}

		for (int i = 0; i < 3; i++) {
			addActionListener(fldSampleStat1[i]);
			addActionListener(fldSampleStat2[i]);
		}

		fldConfLevel.setText(format(sc.level));
		setFieldText(fldNullHyp, format(sc.nullHyp));
		updateTailCheckboxes(sc.getTail());
	}

	private void setFieldText(TextObject field, String text) {
		if (!text.equals("?")) {
			field.setText(text);
		}
	}

	protected abstract void updateTailCheckboxes(String tail);

	final protected boolean forceZeroHypothesis() {
		return sc.getSelectedProcedure() == Procedure.ZPROP2_TEST
				|| sc.getSelectedProcedure() == Procedure.ZPROP2_CI
				|| sc.getSelectedProcedure() == Procedure.ZMEAN2_TEST
				|| sc.getSelectedProcedure() == Procedure.TMEAN2_TEST;
	}

	/**
	 * @param textObject
	 *            input field
	 */
	protected void addActionListener(TextObject textObject) {
		// not needed in web
	}

	/**
	 * @param textObject
	 *            input field
	 */
	protected void removeActionListener(TextObject textObject) {
		// not needed in web
	}

	/**
	 * @return tail value
	 */
	abstract protected String getSelectedTail();

	/**
	 * Prevent auto scrolling
	 */
	protected abstract void resetCaret();

	/**
	 * @param string
	 *            result text
	 */
	protected abstract void updateResultText(String string);

	protected void setLabelStrings() {
		strMean = loc.getMenu("Mean");
		strSD = loc.getMenu("SampleStandardDeviation.short");
		strSigma = loc.getMenu("StandardDeviation.short");
		strSuccesses = loc.getMenu("Successes");
		strN = loc.getMenu("N");
	}

	public App getApp() {
		return kernel.getApplication();
	}

	protected void updateCollectionProcedure() {
		switch (sc.getSelectedProcedure()) {

		default:
			// do nothing
			break;
		case ZMEAN_TEST:
		case ZMEAN_CI:
		case TMEAN_TEST:
		case TMEAN_CI:
			sc.mean = s1[0];
			sc.sd = s1[1];
			sc.n = s1[2];
			break;

		case ZMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_TEST:
		case TMEAN2_CI:
			sc.mean = s1[0];
			sc.sd = s1[1];
			sc.n = s1[2];
			sc.mean2 = s2[0];
			sc.sd2 = s2[1];
			sc.n2 = s2[2];

			// force the null hypothesis to zero
			// TODO: allow non-zero values
			sc.nullHyp = 0;
			break;

		case ZPROP_TEST:
		case ZPROP_CI:
			sc.count = s1[0];
			sc.n = s1[1];
			break;

		case ZPROP2_TEST:
		case ZPROP2_CI:
			sc.count = s1[0];
			sc.n = s1[1];
			sc.count2 = s2[0];
			sc.n2 = s2[1];

			// force the null hypothesis to zero
			// TODO: allow non-zero values
			sc.nullHyp = 0;
			break;
		}

		sc.validate();
	}

	/**
	 * @param sb
	 *            XML builder
	 * @param active
	 *            whether the tab is active
	 */
	public void getXML(XMLStringBuilder sb, boolean active) {
		if (sc != null) {
			sc.setActive(active);
			sc.getXML(sb);
		}
	}

	/**
	 * Update after ProbabilityCalculatorSettings have changed
	 */
	public void settingsChanged() {
		// only in web (?)
	}

	/**
	 * @param input
	 *            user input
	 * @param handler
	 *            error handler
	 * @return double value (NaN if invalid)
	 */
	protected double parseStringData(String input, ErrorHandler handler) {
		if (StringUtil.emptyTrim(input)) {
			return Double.NaN;
		}
		try {
			String inputText = input.trim();

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = kernel.getAlgebraProcessor()
					.evaluateToNumeric(inputText, handler);
			return nv == null ? Double.NaN : nv.getDouble();

		} catch (NumberFormatException e) {
			Log.debug(e);
		}
		return Double.NaN;
	}
}
